# WhatsApp Event Service - Spring Boot API

A complete Spring Boot REST API for handling WhatsApp webhooks and managing local event discovery through conversational AI.

## 🚀 Features

- **REST API**: Complete REST endpoints for webhook handling and event management
- **WhatsApp Integration**: Full WhatsApp Cloud API webhook support
- **MySQL Database**: JPA/Hibernate integration with MySQL
- **AI Processing**: Natural language understanding for event queries
- **Interactive Messages**: Support for WhatsApp buttons and lists
- **User Management**: Complete user preference and activity tracking
- **Event Management**: Full CRUD operations for events
- **Health Monitoring**: Spring Boot Actuator endpoints

## 📋 API Endpoints

### Webhook Endpoints
- `GET /webhook` - Webhook verification
- `POST /webhook` - Receive WhatsApp messages
- `GET /webhook/health` - Webhook service health
- `GET /webhook/config` - Webhook configuration

### Event Management
- `GET /api/events/search` - Search events with filters
- `GET /api/events/category/{category}` - Get events by category
- `GET /api/events/upcoming` - Get upcoming events
- `GET /api/events/{id}` - Get event by ID
- `POST /api/events` - Create new event
- `PUT /api/events/{id}` - Update event
- `DELETE /api/events/{id}` - Delete event
- `GET /api/events/stats` - Get event statistics
- `GET /api/events/categories` - Get available categories

### User Management
- `GET /api/users/phone/{phoneNumber}` - Get user by phone
- `POST /api/users/get-or-create` - Get or create user
- `PUT /api/users/{id}` - Update user
- `PUT /api/users/{id}/opt-in` - Set opt-in status
- `GET /api/users/{id}/preferences` - Get user preferences
- `POST /api/users/{id}/preferences` - Save user preference
- `GET /api/users/{id}/activity` - Get user activity
- `POST /api/users/{id}/activity` - Log user activity
- `GET /api/users/opted-in` - Get opted-in users
- `GET /api/users/stats` - Get user statistics

### Health & Monitoring
- `GET /actuator/health` - Application health
- `GET /actuator/info` - Application info
- `GET /actuator/metrics` - Application metrics

## 🛠️ Quick Start

### Prerequisites
- Java 17+
- Gradle 8.5+ (or use the included wrapper)
- MySQL 8.0+
- WhatsApp Business API access

### Local Development

1. **Set up MySQL**:
   ```bash
   mysql -u root -p < mysql-init.sql
   ```

2. **Set environment variables**:
   ```bash
   export WHATSAPP_ACCESS_TOKEN="your_access_token"
   export WHATSAPP_PHONE_NUMBER_ID="your_phone_number_id"
   export DB_URL="jdbc:mysql://localhost:3306/whatsapp_service"
   export DB_USER="whatsapp_user"
   export DB_PASSWORD="whatsapp_password"
   ```

3. **Build and run**:
   ```bash
   ./gradlew clean build
   java -jar build/libs/whatsapp-event-service-1.0.0.jar
   ```

4. **Test the API**:
   ```bash
   # Health check
   curl http://localhost:8080/actuator/health
   
   # Webhook verification
   curl "http://localhost:8080/webhook?hub.mode=subscribe&hub.verify_token=mywhatsappverify&hub.challenge=test"
   
   # Search events
   curl "http://localhost:8080/api/events/search?category=music&city=Boston"
   ```

### Docker Deployment

1. **Set up environment**:
   ```bash
   cp env.example .env
   # Edit .env with your credentials
   ```

2. **Build and run**:
   ```bash
   docker-compose up -d
   ```

3. **Check status**:
   ```bash
   docker-compose ps
   docker-compose logs -f whatsapp-service
   ```

## 📱 WhatsApp Setup

### 1. Configure Webhook
- **URL**: `https://your-domain.com/webhook`
- **Verify Token**: `mywhatsappverify`
- **Webhook Fields**: `messages`, `message_statuses`

### 2. Test Webhook
```bash
# Verification
curl "https://your-domain.com/webhook?hub.mode=subscribe&hub.verify_token=mywhatsappverify&hub.challenge=test"

# Send test message
curl -X POST https://your-domain.com/webhook \
  -H "Content-Type: application/json" \
  -d '{
    "object": "whatsapp_business_account",
    "entry": [{
      "id": "ENTRY_ID",
      "changes": [{
        "value": {
          "messaging_product": "whatsapp",
          "metadata": {
            "display_phone_number": "PHONE_NUMBER",
            "phone_number_id": "PHONE_NUMBER_ID"
          },
          "contacts": [{
            "profile": {"name": "Test User"},
            "wa_id": "1234567890"
          }],
          "messages": [{
            "from": "1234567890",
            "id": "MESSAGE_ID",
            "timestamp": "1234567890",
            "text": {"body": "What music events are this weekend?"},
            "type": "text"
          }]
        },
        "field": "messages"
      }]
    }]
  }'
```

## 🔧 Configuration

### Application Properties
```yaml
# WhatsApp Configuration
whatsapp:
  access-token: ${WHATSAPP_ACCESS_TOKEN}
  phone-number-id: ${WHATSAPP_PHONE_NUMBER_ID}
  verify-token: ${VERIFY_TOKEN:mywhatsappverify}
  api-url: ${WHATSAPP_API_URL:https://graph.facebook.com/v21.0}

# Database Configuration
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/whatsapp_service}
    username: ${DB_USER:whatsapp_user}
    password: ${DB_PASSWORD:whatsapp_password}
```

### Environment Variables
```bash
# Required
WHATSAPP_ACCESS_TOKEN=your_access_token
WHATSAPP_PHONE_NUMBER_ID=your_phone_number_id

# Database
DB_URL=jdbc:mysql://localhost:3306/whatsapp_service
DB_USER=whatsapp_user
DB_PASSWORD=whatsapp_password

# Optional
VERIFY_TOKEN=mywhatsappverify
GPT4ALL_API_URL=http://localhost:8000
```

## 📊 Database Schema

The application uses JPA entities with the following main tables:

- **users**: User information and opt-in status
- **events**: Event details and metadata
- **event_tags**: Event tags for flexible categorization
- **user_preferences**: User preferences (when implemented)
- **conversation_history**: Chat history (when implemented)
- **user_activity**: User interaction tracking (when implemented)

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Integration Tests
```bash
./gradlew test --tests "*IntegrationTest"
```

### API Testing
```bash
# Test webhook verification
curl "http://localhost:8080/webhook?hub.mode=subscribe&hub.verify_token=mywhatsappverify&hub.challenge=test"

# Test event search
curl "http://localhost:8080/api/events/search?category=music&limit=5"

# Test user creation
curl -X POST "http://localhost:8080/api/users/get-or-create?phoneNumber=1234567890&whatsappId=1234567890"
```

## 📈 Monitoring

### Health Checks
- **Application Health**: `GET /actuator/health`
- **Database Health**: Included in application health
- **WhatsApp API Health**: Check webhook config endpoint

### Metrics
- **Application Metrics**: `GET /actuator/metrics`
- **Custom Metrics**: User activity, event searches, message processing

### Logging
- **Application Logs**: Structured logging with SLF4J
- **Request Logging**: HTTP request/response logging
- **Error Logging**: Detailed error tracking

## 🚀 Deployment

### AWS Deployment
```bash
# Build for production
./gradlew clean build

# Deploy to AWS
./aws-deploy.sh
```

### Docker Production
```bash
# Build production image
docker build -t whatsapp-event-service:latest .

# Run with production config
docker run -d \
  -p 8080:8080 \
  -e WHATSAPP_ACCESS_TOKEN=your_token \
  -e WHATSAPP_PHONE_NUMBER_ID=your_id \
  -e DB_URL=jdbc:mysql://your-db:3306/whatsapp_service \
  whatsapp-event-service:latest
```

## 🔒 Security

### Best Practices
- Environment variable configuration
- Input validation and sanitization
- SQL injection prevention (JPA)
- Rate limiting (implement with Spring Security)
- HTTPS enforcement

### WhatsApp Security
- Webhook verification
- Message signature validation
- User opt-in/opt-out handling
- Data privacy compliance

## 📚 API Documentation

### Swagger/OpenAPI
Access API documentation at: `http://localhost:8080/swagger-ui.html`

### Postman Collection
Import the provided Postman collection for easy API testing.

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check MySQL service is running
   - Verify database credentials
   - Check network connectivity

2. **WhatsApp Webhook Verification Failed**
   - Verify token matches configuration
   - Check webhook URL is accessible
   - Ensure HTTPS is enabled

3. **Message Sending Failed**
   - Verify WhatsApp access token
   - Check phone number ID
   - Ensure user has opted in

### Debug Mode
```bash
# Enable debug logging
export LOGGING_LEVEL_COM_WHATSAPP_EVENTSERVICE=DEBUG
java -jar build/libs/whatsapp-event-service-1.0.0.jar
```

## 📞 Support

For issues and questions:
1. Check the logs: `docker-compose logs -f whatsapp-service`
2. Verify configuration: `GET /webhook/config`
3. Test health: `GET /actuator/health`
4. Check database: `GET /api/events/stats`

---

**Built with Spring Boot 2.7.18 and Java 16**
