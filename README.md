# Meeting Planner

A JavaFX application for scheduling and managing meetings.  
The project uses a database that runs via Docker Compose.

## Requirements
- Docker & Docker Compose
- Java 21+
- Maven

## Setup

1. **Start the database**
   ```bash
   docker compose up -d

2. **Build and run the application**
    ```bash
    mvn clean package
    mvn javafx:run