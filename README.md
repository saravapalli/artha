# WhatsApp Event Service

A conversational WhatsApp service that helps users discover local events using AI-powered natural language understanding.

## 🎯 Features

- **Natural Language Processing**: Understands conversational queries like "What music events are happening this weekend?"
- **AI Integration**: Uses GPT4All for enhanced query understanding with fallback to rule-based processing
- **Interactive Messages**: Supports WhatsApp buttons and quick replies
- **User Preferences**: Learns and stores user preferences for personalized recommendations
- **Event Management**: Comprehensive event database with categories, locations, and filtering
- **Cost Optimized**: Designed to run on AWS for under $100/month

## 🏗️ Architecture

```
WhatsApp Business API → Webhook → Java Backend → AI Processing → Event Database → Response
```

### Components

1. **WhatsApp Webhook Handler**: Receives and processes incoming messages
2. **AI Query Processor**: Parses natural language queries (GPT4All + fallback)
3. **Event Service**: Manages event data and search functionality
4. **User Service**: Handles user preferences and conversation history
5. **Message Sender**: Sends responses back via WhatsApp API

## 🚀 Quick Start

### Prerequisites

- Java 17+
- MySQL 8.0+ (or Docker and Docker Compose)
- WhatsApp Business API access
- AWS account (for deployment)

### Local Development

1. **Clone and setup**:
   ```bash
   git clone <repository>
   cd MyDreamProject
   ```

2. **Set up MySQL**:
   ```bash
   # Option 1: Use the setup script (recommended)
   ./setup-mysql.sh
   
   # Option 2: Manual setup
   mysql -u root -p < mysql-init.sql
   ```

3. **Set environment variables**:
   ```bash
   export WHATSAPP_ACCESS_TOKEN="your_whatsapp_access_token"
   export WHATSAPP_PHONE_NUMBER_ID="your_phone_number_id"
   export DB_URL="jdbc:mysql://localhost:3306/whatsapp_service"
   export DB_USER="whatsapp_user"
   export DB_PASSWORD="whatsapp_password"
   ```

4. **Build and start the service**:
   ```bash
   # Build with Gradle
   ./gradlew clean build
   
   # Run the service
   java -jar build/libs/whatsapp-event-service-1.0.0.jar
   ```

5. **Test the service**:
   - Visit: http://localhost:8080
   - Health check: http://localhost:8080/health
   - Webhook URL: http://localhost:8080/webhook

### Docker Deployment

1. **Set up environment variables**:
   ```bash
   cp env.example .env
   # Edit .env with your WhatsApp credentials
   ```

2. **Build and run**:
   ```bash
   docker-compose up -d
   ```

3. **Check status**:
   ```bash
   docker-compose ps
   docker-compose logs -f
   ```

4. **Access services**:
   - WhatsApp Service: http://localhost:8080
   - MySQL: localhost:3306
   - GPT4All: http://localhost:8000

## 📱 WhatsApp Setup

### 1. WhatsApp Business API Configuration

1. Create a WhatsApp Business Account
2. Get your access token and phone number ID
3. Set up webhook URL: `https://your-domain.com/webhook`
4. Set verify token: `mywhatsappverify`

### 2. Webhook Configuration

- **URL**: `https://your-domain.com/webhook`
- **Verify Token**: `mywhatsappverify`
- **Webhook Fields**: `messages`

### 3. Message Templates

Create templates for outbound notifications:
- Welcome message
- Event notifications
- Preference updates

## 🤖 AI Integration

### GPT4All Setup

The service supports GPT4All for enhanced natural language understanding:

```bash
# Run GPT4All service
docker run -p 8000:8000 gpt4all/gpt4all:latest
```

### Fallback Processing

If GPT4All is unavailable, the service uses rule-based processing with:
- Category detection (music, sports, family, art, etc.)
- Date parsing (today, weekend, this week, etc.)
- Location extraction (city names, venues)
- Price range detection
- Age restriction parsing

## 🗄️ Database Schema

### Core Tables

- **users**: User information and opt-in status
- **events**: Event details and metadata
- **user_preferences**: User preferences and interests
- **conversation_history**: Chat history and context
- **user_activity**: Interaction tracking and analytics

### Event Categories

- Music (jazz, rock, classical, pop)
- Sports (basketball, football, running, etc.)
- Family (children's events, family-friendly)
- Art (exhibitions, galleries, museums)
- Food (festivals, tastings, restaurants)
- Education (workshops, classes, seminars)
- Entertainment (shows, comedy, theater)
- Outdoor (hiking, nature, parks)

## 💰 Cost Optimization

### AWS Deployment (~$50-100/month)

- **EC2 Instance**: t3.medium (~$30/month)
- **Storage**: 20GB EBS (~$2/month)
- **Data Transfer**: Minimal (~$5/month)
- **GPT4All**: Self-hosted (free)

### Cost-Saving Tips

1. Use spot instances for development
2. Implement request caching
3. Optimize AI model size
4. Use SQLite for small scale
5. Implement request rate limiting

## 🔧 Configuration

### Environment Variables

```bash
# WhatsApp Configuration
WHATSAPP_ACCESS_TOKEN=your_access_token
WHATSAPP_PHONE_NUMBER_ID=your_phone_number_id

# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/whatsapp_service
DB_USER=whatsapp_user
DB_PASSWORD=whatsapp_password

# AI Configuration
GPT4ALL_API_URL=http://localhost:8000
```

### Application Settings

- **Port**: 8080
- **Verify Token**: mywhatsappverify
- **Max Events per Response**: 3
- **Request Timeout**: 30 seconds

## 📊 Usage Examples

### User Queries

```
"What music events are happening this weekend?"
"Show me family-friendly activities in Boston"
"Any free events today?"
"Art exhibitions this month"
"Sports games near me"
```

### System Responses

The service responds with:
- Event details (title, date, location, description)
- Interactive buttons (Interested, Not Interested, More Info)
- Personalized recommendations based on preferences
- Help and guidance messages

## 🚀 Deployment

### AWS Deployment

Use the provided deployment script:

```bash
chmod +x aws-deploy.sh
./aws-deploy.sh
```

### Manual Deployment

1. Launch EC2 instance (t3.medium)
2. Install Docker and Docker Compose
3. Copy application files
4. Configure environment variables
5. Start services with docker-compose

### Production Considerations

- Set up SSL certificate
- Configure domain name
- Implement monitoring and logging
- Set up automated backups
- Configure auto-scaling
- Implement rate limiting

## 🔍 Monitoring

### Health Checks

- Service health: `/health`
- GPT4All status: Built-in availability check
- Database connectivity: Automatic validation

### Logging

- Request/response logging
- Error tracking
- User interaction analytics
- Performance metrics

## 🛠️ Development

### Adding New Features

1. **New Event Categories**: Update `AIQueryProcessor.CATEGORY_SYNONYMS`
2. **Enhanced AI**: Modify `GPT4AllIntegration` prompts
3. **New Message Types**: Extend `WhatsAppWebhookHandler`
4. **Database Changes**: Update schema in `DatabaseSetup`

### Testing

```bash
# Run tests
./gradlew test

# Test database setup (if needed)
./gradlew run --args="DatabaseSetup"

# Test event seeding (if needed)
./gradlew run --args="EventDataSeeder"

# Test webhook handling
curl -X POST http://localhost:8080/webhook \
  -H "Content-Type: application/json" \
  -d '{"entry":[{"changes":[{"value":{"messages":[{"from":"1234567890","text":{"body":"What music events are this weekend?"}}]}}]}]}'

# Test database connection
mysql -u whatsapp_user -pwhatsapp_password whatsapp_service -e "SELECT COUNT(*) as event_count FROM events;"
```

## 📈 Scaling

### Horizontal Scaling

- Load balancer for multiple instances
- Database replication
- Message queue for high volume
- CDN for static content

### Vertical Scaling

- Larger EC2 instances
- More memory for AI processing
- SSD storage for better performance
- Dedicated database instance

## 🔒 Security

### Best Practices

- HTTPS for all communications
- Input validation and sanitization
- Rate limiting and DDoS protection
- Secure credential management
- Regular security updates

### WhatsApp Compliance

- User opt-in/opt-out handling
- Message template approval
- Anti-spam measures
- Data privacy compliance

## 📞 Support

### Troubleshooting

1. **Service not responding**: Check health endpoint
2. **WhatsApp messages not received**: Verify webhook configuration
3. **AI not working**: Check GPT4All service status
4. **Database errors**: Verify SQLite file permissions

### Common Issues

- **Webhook verification fails**: Check verify token
- **Messages not sent**: Verify WhatsApp credentials
- **AI responses slow**: Check GPT4All service
- **Database locked**: Check file permissions

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📚 Resources

- [WhatsApp Business API Documentation](https://developers.facebook.com/docs/whatsapp)
- [GPT4All Documentation](https://gpt4all.io/)
- [AWS EC2 Pricing](https://aws.amazon.com/ec2/pricing/)
- [Docker Documentation](https://docs.docker.com/)

---

**Built with ❤️ for local event discovery**
