#!/bin/bash

# AWS Deployment Script for WhatsApp Event Service
# This script deploys the service to AWS EC2 with cost optimization

set -e

# Configuration
INSTANCE_TYPE="t3.medium"
KEY_NAME="whatsapp-service-key"
SECURITY_GROUP="whatsapp-service-sg"
AMI_ID="ami-0c02fb55956c7d316"  # Amazon Linux 2 AMI
REGION="us-east-1"
VOLUME_SIZE=20  # GB

echo "🚀 Starting AWS deployment for WhatsApp Event Service..."

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "❌ AWS CLI is not installed. Please install it first."
    exit 1
fi

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install it first."
    exit 1
fi

# Create security group if it doesn't exist
echo "🔒 Creating security group..."
if ! aws ec2 describe-security-groups --group-names $SECURITY_GROUP --region $REGION &> /dev/null; then
    aws ec2 create-security-group \
        --group-name $SECURITY_GROUP \
        --description "Security group for WhatsApp Event Service" \
        --region $REGION
    
    # Allow HTTP, HTTPS, and SSH
    aws ec2 authorize-security-group-ingress \
        --group-name $SECURITY_GROUP \
        --protocol tcp \
        --port 22 \
        --cidr 0.0.0.0/0 \
        --region $REGION
    
    aws ec2 authorize-security-group-ingress \
        --group-name $SECURITY_GROUP \
        --protocol tcp \
        --port 80 \
        --cidr 0.0.0.0/0 \
        --region $REGION
    
    aws ec2 authorize-security-group-ingress \
        --group-name $SECURITY_GROUP \
        --protocol tcp \
        --port 443 \
        --cidr 0.0.0.0/0 \
        --region $REGION
    
    aws ec2 authorize-security-group-ingress \
        --group-name $SECURITY_GROUP \
        --protocol tcp \
        --port 8080 \
        --cidr 0.0.0.0/0 \
        --region $REGION
    
    echo "✅ Security group created"
else
    echo "✅ Security group already exists"
fi

# Create key pair if it doesn't exist
echo "🔑 Creating key pair..."
if ! aws ec2 describe-key-pairs --key-names $KEY_NAME --region $REGION &> /dev/null; then
    aws ec2 create-key-pair \
        --key-name $KEY_NAME \
        --region $REGION \
        --query 'KeyMaterial' \
        --output text > ${KEY_NAME}.pem
    
    chmod 400 ${KEY_NAME}.pem
    echo "✅ Key pair created: ${KEY_NAME}.pem"
else
    echo "✅ Key pair already exists"
fi

# Create user data script
cat > user-data.sh << 'EOF'
#!/bin/bash
yum update -y
yum install -y docker git

# Start Docker
systemctl start docker
systemctl enable docker
usermod -a -G docker ec2-user

# Install Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Create application directory
mkdir -p /home/ec2-user/whatsapp-service
cd /home/ec2-user/whatsapp-service

# Create environment file
cat > .env << 'ENVEOF'
WHATSAPP_ACCESS_TOKEN=your_whatsapp_access_token_here
WHATSAPP_PHONE_NUMBER_ID=your_phone_number_id_here
ENVEOF

# Create docker-compose.yml
cat > docker-compose.yml << 'COMPOSEEOF'
version: '3.8'

services:
  whatsapp-service:
    image: whatsapp-event-service:latest
    ports:
      - "8080:8080"
    environment:
      - WHATSAPP_ACCESS_TOKEN=${WHATSAPP_ACCESS_TOKEN}
      - WHATSAPP_PHONE_NUMBER_ID=${WHATSAPP_PHONE_NUMBER_ID}
      - GPT4ALL_API_URL=http://gpt4all:8000
    volumes:
      - ./data:/app/data
    depends_on:
      - gpt4all
    restart: unless-stopped

  gpt4all:
    image: gpt4all/gpt4all:latest
    ports:
      - "8000:8000"
    environment:
      - MODEL_NAME=gpt4all-j
      - MAX_TOKENS=200
      - TEMPERATURE=0.3
    volumes:
      - gpt4all_models:/root/.cache/gpt4all
    restart: unless-stopped

volumes:
  gpt4all_models:
    driver: local
COMPOSEEOF

# Set ownership
chown -R ec2-user:ec2-user /home/ec2-user/whatsapp-service

echo "✅ User data script completed"
EOF

# Launch EC2 instance
echo "🖥️  Launching EC2 instance..."
INSTANCE_ID=$(aws ec2 run-instances \
    --image-id $AMI_ID \
    --count 1 \
    --instance-type $INSTANCE_TYPE \
    --key-name $KEY_NAME \
    --security-groups $SECURITY_GROUP \
    --user-data file://user-data.sh \
    --block-device-mappings "[{\"DeviceName\":\"/dev/xvda\",\"Ebs\":{\"VolumeSize\":$VOLUME_SIZE,\"DeleteOnTermination\":true}}]" \
    --region $REGION \
    --query 'Instances[0].InstanceId' \
    --output text)

echo "✅ Instance launched: $INSTANCE_ID"

# Wait for instance to be running
echo "⏳ Waiting for instance to be running..."
aws ec2 wait instance-running --instance-ids $INSTANCE_ID --region $REGION

# Get public IP
PUBLIC_IP=$(aws ec2 describe-instances \
    --instance-ids $INSTANCE_ID \
    --region $REGION \
    --query 'Reservations[0].Instances[0].PublicIpAddress' \
    --output text)

echo "✅ Instance is running at: $PUBLIC_IP"

# Wait for instance to be ready
echo "⏳ Waiting for instance to be ready..."
sleep 60

# Build and push Docker image (if you have a registry)
echo "🐳 Building Docker image..."
docker build -t whatsapp-event-service:latest .

# Copy files to instance
echo "📁 Copying files to instance..."
scp -i ${KEY_NAME}.pem -o StrictHostKeyChecking=no docker-compose.yml ec2-user@$PUBLIC_IP:/home/ec2-user/whatsapp-service/
scp -i ${KEY_NAME}.pem -o StrictHostKeyChecking=no .env ec2-user@$PUBLIC_IP:/home/ec2-user/whatsapp-service/

# Deploy application
echo "🚀 Deploying application..."
ssh -i ${KEY_NAME}.pem -o StrictHostKeyChecking=no ec2-user@$PUBLIC_IP << 'EOF'
cd /home/ec2-user/whatsapp-service

# Load Docker image (you would need to save and transfer the image)
# docker load < whatsapp-event-service.tar

# Start services
docker-compose up -d

# Wait for services to be ready
sleep 30

# Check service status
docker-compose ps
curl -f http://localhost:8080/health || echo "Service not ready yet"
EOF

echo ""
echo "🎉 Deployment completed!"
echo ""
echo "📋 Deployment Summary:"
echo "   Instance ID: $INSTANCE_ID"
echo "   Public IP: $PUBLIC_IP"
echo "   Instance Type: $INSTANCE_TYPE"
echo "   Webhook URL: http://$PUBLIC_IP:8080/webhook"
echo "   Health Check: http://$PUBLIC_IP:8080/health"
echo ""
echo "🔧 Next Steps:"
echo "   1. Update your WhatsApp Business API webhook URL to: http://$PUBLIC_IP:8080/webhook"
echo "   2. Set the verify token to: mywhatsappverify"
echo "   3. Update the .env file on the server with your WhatsApp credentials"
echo "   4. SSH to the instance: ssh -i ${KEY_NAME}.pem ec2-user@$PUBLIC_IP"
echo ""
echo "💰 Estimated Monthly Cost: ~$30-50 (t3.medium instance)"
echo ""
echo "⚠️  Remember to:"
echo "   - Update WhatsApp credentials in .env file"
echo "   - Set up SSL certificate for production"
echo "   - Configure domain name and DNS"
echo "   - Set up monitoring and backups"
