# WFH Billing API

A Spring Boot REST API for the Wright Funeral Home billing system. Exposes the billing data managed by [wfh-billing-desktop](https://github.com/RyPalm04/wfh-billing) over HTTP.

## Tech Stack

- Java 21 / Spring Boot 3.5
- Spring Web, Spring Data JDBC
- Flyway (PostgreSQL migrations)
- Supabase (hosted PostgreSQL)
- Spock/Groovy for testing

## Getting Started

### Prerequisites

- Java 21+
- A PostgreSQL database (or a [Supabase](https://supabase.com) project)

### Configuration

Copy the example config and fill in your database credentials:

```bash
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
```

Edit `application-local.yml` with your connection details:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://<host>:<port>/<database>?sslmode=require
    username: <username>
    password: <password>
```

### Run

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

The API will start on `http://localhost:18080`. Flyway will apply any pending migrations on startup.

### Test

```bash
./gradlew test
```

Tests run against an in-memory H2 database — no credentials required.