# Docker Setup Instructions

This guide explains how to run the backend and database using Docker containers for the Kotlin frontend.

## Prerequisites

- Docker Desktop installed
- Docker Compose installed
- Java 17+ (for building the backend JAR)

## Building and Running

### 1. Build the Backend Application

First, build the Spring Boot backend:

```bash
cd sbstudms
mvn clean package
```

This creates the JAR file in `target/sbemplms-1.0-SNAPSHOT.jar`.

### 2. Start Docker Services

Run the following command from the `sbstudms` directory:

```bash
docker-compose up --build
```

This will:
- Build the Docker image for the backend
- Start MySQL database container
- Start the backend Spring Boot application
- Connect both containers in a shared network

### 3. Access Services

- **Backend API**: http://localhost:8080
- **MySQL Database**: localhost:3306

### 4. Database Health Check

The backend waits for the database to be healthy before starting. You can check the status with:

```bash
docker-compose ps
```

### 5. Stopping Services

To stop all services:

```bash
docker-compose down
```

To stop and remove volumes (cleans up database data):

```bash
docker-compose down -v
```

## Kotlin Frontend Configuration

When configuring your Kotlin frontend to connect to the Dockerized backend:

- Use `http://localhost:8080` as the base URL for API calls
- The backend runs on port 8080 externally
- Internally, the backend connects to MySQL on the Docker network

## Useful Commands

### View Logs
```bash
docker-compose logs -f backend
docker-compose logs -f mysqldb
```

### Restart Services
```bash
docker-compose restart
```

### Rebuild and Start
```bash
docker-compose up --build --force-recreate
```

## Database Access

You can connect to the MySQL database using:
- Host: localhost
- Port: 3306
- Database: empldb
- Username: mysqluser
- Password: password

## Troubleshooting

1. **Port conflicts**: If ports 3306 or 8080 are already in use, modify them in docker-compose.yml
2. **Backend not starting**: Check that the JAR file exists in `target/` directory
3. **Database connection issues**: Ensure MySQL container is healthy before backend starts