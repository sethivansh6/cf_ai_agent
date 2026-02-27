# AI Agent with Persistent Memory

A stateful AI chatbot application that maintains conversation history per user using Firebase Firestore. The agent remembers previous interactions and provides contextually aware responses based on past conversations.

## Overview

This project provides two interfaces for interacting with an AI assistant:

1. **CLI Application** (`Agent.java`) - For local command-line conversations
2. **REST API Server** (`AgentServer.java`) - For remote API-based interactions (deployed on Railway)

Both share the same core functionality: maintaining persistent conversation memory per user and leveraging an LLM for intelligent responses.

## Tech Stack

### Backend Framework
- **Java 17** - Primary programming language
- **Spark** - Lightweight REST API framework
- **Maven** - Build and dependency management

### AI/LLM
- **Hugging Face API** - LLM endpoint for chat completions
  - Model: `openai/gpt-oss-120b:fastest`
  - Endpoint: `https://router.huggingface.co/v1/chat/completions`

### Database
- **Google Firestore** - NoSQL cloud database for storing conversation history
  - Collection: `memory`
  - Document ID: User email (for per-user storage)
  - Data: List of message objects with role and content

### Dependencies
- `jackson-databind` (2.17.0) - JSON serialization/deserialization
- `spark-core` (2.9.4) - Web framework
- `google-cloud-firestore` (3.20.0) - Firebase Firestore client
- `java-dotenv` (3.0.0) - Environment variable management
- `slf4j` (2.0.12) - Logging

### Deployment
- **Railway** - Cloud platform for hosting
  - Live URL: `https://aiagent-production-49e3.up.railway.app`

## Project Structure

```
src/main/java/com/vansh/
├── Agent.java           # CLI interface for local chat
├── AgentServer.java     # REST API server
├── LLMClient.java       # Hugging Face API integration
├── MemoryStore.java     # Firestore operations
├── FirestoreConfig.java # Firebase initialization
└── Config.java          # Configuration management

src/main/resources/
└── memory.json          # Local memory storage (for local testing)
```

## Getting Started

### Prerequisites

1. **Java 17+** installed
2. **Maven** 3.6+ installed
3. **Hugging Face API Key** - Get from https://huggingface.co/settings/tokens
4. **Google Firebase Project** with:
   - Firestore database enabled
   - Service account JSON key

### Configuration

Create a `.env` file in the project root:

```bash
# Hugging Face API
HF_API_KEY=your_hugging_face_api_key_here

# Firebase
FIREBASE_PROJECT_ID=your_firebase_project_id
FIREBASE_KEY_JSON={"type":"service_account","project_id":"..."}
```

**Alternative:** Set environment variables directly instead of using `.env`

### Building the Project

```bash
# Install dependencies and compile
mvn clean compile

# Package with all dependencies
mvn clean package
```

This creates a fat JAR with the Shade plugin at `target/ai-agent-1.0-SNAPSHOT.jar`

## Usage

### 1. Local CLI Usage (Agent.java)

Run the CLI application for interactive conversations:

```bash
# Build first
mvn clean compile

# Run the CLI agent
mvn exec:java -Dexec.mainClass="com.vansh.Agent"
```

**Interaction Flow:**
```
Java AI Agent (type 'exit' to quit)

Enter your email ID: vansh@example.com
You: Hello
Agent: Hi there! How can I help you today?
You: My name is Vansh
Agent: Nice to meet you, Vansh! How can I assist you today?
You: What's my name?
Agent: Your name is Vansh, as you mentioned earlier!
You: exit
```

**How it works:**
1. Enter your email (used as unique identifier for memory)
2. Type your message
3. Agent responds based on conversation history
4. All messages are saved to Firestore
5. Type `exit` to quit

### 2. REST API Usage (AgentServer.java)

#### Local Deployment

```bash
# Build the project
mvn clean package

# Run the server (default port: 8080)
java -jar target/ai-agent-1.0-SNAPSHOT.jar

# Or specify custom port
PORT=3000 java -jar target/ai-agent-1.0-SNAPSHOT.jar
```

Server starts and listens on `http://localhost:8080/chat`

#### Production Deployment

Live API: `https://aiagent-production-49e3.up.railway.app/chat`

### API Endpoint

**Endpoint:** `POST /chat`

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "user@example.com",
  "message": "Your message here"
}
```

**Response:**
```json
{
  "reply": "Agent's response here"
}
```

#### Example cURL Commands

**Example 1: Initial greeting**
```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"email":"vansh@example.com","message":"Hello"}'
```

**Response:**
```json
{"reply":"Hi there! How can I help you today?"}
```

**Example 2: Introduce yourself**
```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"email":"vansh@example.com","message":"My name is Vansh"}'
```

**Response:**
```json
{"reply":"Nice to meet you, Vansh! How can I assist you today?"}
```

**Example 3: Test memory - agent recalls your name**
```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"email":"vansh@example.com","message":"What is my name?"}'
```

**Response:**
```json
{"reply":"Your name is Vansh, as you told me earlier!"}
```

**Example 4: Production API**
```bash
curl -X POST https://aiagent-production-49e3.up.railway.app/chat \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","message":"Hello"}'
```

#### Using jq for formatted output

```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"email":"vansh@example.com","message":"Hello"}' | jq .
```

#### Using different users

The system maintains separate conversation history per email:

```bash
# User 1 conversation
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","message":"Call me Alice"}'

# User 2 conversation
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"email":"bob@example.com","message":"Call me Bob"}'

# User 1 again - remembers as Alice
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","message":"Who am I?"}'
# Response: "You are Alice, as you told me earlier!"
```

## How It Works

### Conversation Flow

1. **Load Memory**: Load all previous messages for the user from Firestore
2. **Prepare Context**: Build message list with:
   - System prompt ("You are a helpful assistant")
   - User's conversation history
   - Current user message
3. **Get LLM Response**: Send to Hugging Face API
4. **Save Memory**: Store user message + AI response back to Firestore
5. **Return**: Send response to client

### Memory Storage

Firestore stores user conversations in this structure:

```
Collection: "memory"
Document ID: "user@example.com"
Data: {
  "messages": [
    {"role": "user", "content": "Hello"},
    {"role": "assistant", "content": "Hi there!"},
    {"role": "user", "content": "My name is Vansh"},
    {"role": "assistant", "content": "Nice to meet you, Vansh!"}
  ]
}
```

## Debugging

### Enable Debug Logs

Both CLI and API include debug logging. Check console output for:
- `[DEBUG]` - Information about request/response flow
- `[ERROR]` - Error messages and stack traces

Example output:
```
[DEBUG] AgentServer: POST /chat called
[DEBUG] AgentServer: request body email=vansh@example.com message=Hello
[DEBUG] MemoryStore.load: email=vansh@example.com
[DEBUG] AgentServer: LLM replied
[DEBUG] AgentServer: saving memory
[DEBUG] AgentServer: save complete
```

### Common Issues

**502 Error (Application failed to respond):**
- Check Firebase credentials are set correctly
- Verify Hugging Face API key is valid
- Check server logs for detailed errors

**FIREBASE_KEY_JSON not set:**
- Ensure environment variables are configured
- Or place `firebase_key_memory_store.json` in project root

**Connection timeout:**
- Verify Hugging Face API is accessible
- Check internet connection
- Verify API key has usage quota

## Deployment on Railway

The application is configured for Railway deployment:

1. **Procfile** specifies the start command
2. **pom.xml** builds a fat JAR with all dependencies
3. Environment variables set in Railway dashboard:
   - `HF_API_KEY`
   - `FIREBASE_PROJECT_ID`
   - `FIREBASE_KEY_JSON`
   - `PORT` (defaults to 8080)

## Development

### Adding Features

To extend functionality:

1. **Custom LLM models**: Modify `LLMClient.java` - change model name in payload
2. **Custom system prompt**: Update message construction in `Agent.java` or `AgentServer.java`
3. **User metadata**: Extend Firestore documents in `MemoryStore.java`
4. **Additional endpoints**: Add more routes in `AgentServer.java`

### Running Tests

```bash
mvn test
```

## License

MIT License - Feel free to use, modify, and distribute

## Contact

Author: Vansh
Repository: https://github.com/sethivansh6/cf_ai_agent
Live API: https://aiagent-production-49e3.up.railway.app
