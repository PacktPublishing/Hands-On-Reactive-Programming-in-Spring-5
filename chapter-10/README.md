# Chapter 10: And Finally, Release it!

This sample depicts a reactive application based on Spring Boot 2 and WebFlux with all required infrastructure for operational monitoring.

## Application Structure

- Application itself:
  - Reactive Web App (With Spring Boot Admin)
  - Database: MongoDB
- Monitoring infrastructure:
  - ElasticSearch
  - Prometheus
  - Grafana
  - Zipkin
- Possible additions:
  - FluentD/Logstash
  - Kibana
  
## Accessing Application Components
  
- Reactive Web Application: <localhost:8080>
- Spring Boot Admin 2.0: <localhost:8090/admin>
- Prometheus: <localhost:9090>
- Grafana: <localhost:3000> (user: `admin`, password: `admin`)
