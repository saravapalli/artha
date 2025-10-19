#!/bin/bash

# MySQL Setup Script for WhatsApp Event Service
# This script helps you set up MySQL for the WhatsApp Event Service

set -e

echo "🗄️  Setting up MySQL for WhatsApp Event Service..."

# Check if MySQL is installed
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL is not installed. Please install MySQL first."
    echo "   On Ubuntu/Debian: sudo apt-get install mysql-server"
    echo "   On macOS: brew install mysql"
    echo "   On CentOS/RHEL: sudo yum install mysql-server"
    exit 1
fi

# Check if MySQL service is running
if ! systemctl is-active --quiet mysql 2>/dev/null && ! systemctl is-active --quiet mysqld 2>/dev/null; then
    echo "⚠️  MySQL service is not running. Starting MySQL..."
    sudo systemctl start mysql 2>/dev/null || sudo systemctl start mysqld 2>/dev/null || {
        echo "❌ Failed to start MySQL service. Please start it manually."
        exit 1
    }
fi

# Create database and user
echo "📊 Creating database and user..."

mysql -u root -p << 'EOF'
CREATE DATABASE IF NOT EXISTS whatsapp_service 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'whatsapp_user'@'localhost' IDENTIFIED BY 'whatsapp_password';
CREATE USER IF NOT EXISTS 'whatsapp_user'@'%' IDENTIFIED BY 'whatsapp_password';

GRANT ALL PRIVILEGES ON whatsapp_service.* TO 'whatsapp_user'@'localhost';
GRANT ALL PRIVILEGES ON whatsapp_service.* TO 'whatsapp_user'@'%';

FLUSH PRIVILEGES;

SELECT 'WhatsApp Event Service database created successfully!' as status;
EOF

if [ $? -eq 0 ]; then
    echo "✅ Database and user created successfully!"
else
    echo "❌ Failed to create database and user."
    exit 1
fi

# Test connection
echo "🔍 Testing database connection..."
mysql -u whatsapp_user -pwhatsapp_password -e "USE whatsapp_service; SELECT 'Connection successful!' as status;" 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✅ Database connection test successful!"
else
    echo "❌ Database connection test failed."
    exit 1
fi

# Download MySQL JDBC driver if not present
if [ ! -f "mysql-connector-java-8.0.33.jar" ]; then
    echo "📥 Downloading MySQL JDBC driver..."
    wget -O mysql-connector-java-8.0.33.jar \
        https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar
    
    if [ $? -eq 0 ]; then
        echo "✅ MySQL JDBC driver downloaded successfully!"
    else
        echo "❌ Failed to download MySQL JDBC driver."
        exit 1
    fi
else
    echo "✅ MySQL JDBC driver already present."
fi

# Compile Java classes with MySQL driver
echo "🔨 Compiling Java classes..."
javac -cp mysql-connector-java-8.0.33.jar -d classes src/*.java

if [ $? -eq 0 ]; then
    echo "✅ Java compilation successful!"
else
    echo "❌ Java compilation failed."
    exit 1
fi

# Initialize database schema
echo "🏗️  Initializing database schema..."
java -cp classes:mysql-connector-java-8.0.33.jar DatabaseSetup

if [ $? -eq 0 ]; then
    echo "✅ Database schema initialized successfully!"
else
    echo "❌ Failed to initialize database schema."
    exit 1
fi

# Seed sample data
echo "🌱 Seeding sample events..."
java -cp classes:mysql-connector-java-8.0.33.jar EventDataSeeder

if [ $? -eq 0 ]; then
    echo "✅ Sample events seeded successfully!"
else
    echo "❌ Failed to seed sample events."
    exit 1
fi

echo ""
echo "🎉 MySQL setup completed successfully!"
echo ""
echo "📋 Configuration Summary:"
echo "   Database: whatsapp_service"
echo "   User: whatsapp_user"
echo "   Password: whatsapp_password"
echo "   Host: localhost"
echo "   Port: 3306"
echo ""
echo "🚀 Next Steps:"
echo "   1. Set your WhatsApp credentials in environment variables:"
echo "      export WHATSAPP_ACCESS_TOKEN='your_token'"
echo "      export WHATSAPP_PHONE_NUMBER_ID='your_phone_id'"
echo ""
echo "   2. Start the service:"
echo "      java -cp classes:mysql-connector-java-8.0.33.jar WhatsAppEventService"
echo ""
echo "   3. Or use Docker Compose:"
echo "      docker-compose up -d"
echo ""
echo "🔧 Database Management:"
echo "   Connect: mysql -u whatsapp_user -pwhatsapp_password whatsapp_service"
echo "   Backup: mysqldump -u whatsapp_user -pwhatsapp_password whatsapp_service > backup.sql"
echo "   Restore: mysql -u whatsapp_user -pwhatsapp_password whatsapp_service < backup.sql"
