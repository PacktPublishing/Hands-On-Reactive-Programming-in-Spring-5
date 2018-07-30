# Chapter 10: And Finally, Release it!

This sample depicts a reactive application based on Spring Boot 2 and WebFlux with all required infrastructure for operational monitoring.

Metrics are pulled by Prometheus, Grafana has a simple dashboard with app metrics, Zipkin gathers traces.

## Application Structure

- Application itself:
  - Reactive Web App (with Spring Boot Admin)
  - Database: MongoDB (is not used)
- Monitoring infrastructure:
  - Prometheus
  - Grafana
  - Zipkin
  
## Start or Stop Infrastructural Services

To start services run the following command:

```bash
docker-compose -f docker/docker-compose.yml up -d
```

To stop services run the following command:

```bash
docker-compose -f docker/docker-compose.yml down
```

## Start Spring Boot Application

To start the application, run in your favorite IDE class 
`org.rpis5.chapters.chapter_10.Chapter10CloudReadyApplication`.
  
## Accessing Application Components
  
- Reactive Web Application: <localhost:8080>
- Spring Boot Admin 2.0: <localhost:8090/admin>
- Prometheus: <localhost:9090>
- Grafana: <localhost:3000> (user: `admin`, password: `admin`)
- Zipkin: <localhost:9411>

