# csv-watcher

Versión actual: **1.0.3**.

Microservicio Spring Boot/JDK 21 que observa un directorio compartido y publica un evento Avro en `file.ready` por cada CSV nuevo o modificado. Clasifica `SEASONS`, `RESULTS`, `FIXTURES`, `POINT_BY_POINT`, `MATCH_SUMMARY` y `STATS_PLAYER`; `csv-file-event-router` dirige después cada evento al consumidor correspondiente.

El identificador se calcula con la ruta relativa y el SHA-256 del contenido. Por ello, el mismo fichero genera la misma clave y los consumidores downstream pueden deduplicar mensajes con semántica al menos una vez.

## Configuración

- `WATCH_DIRECTORY` (por defecto `/basketball-data`)
- `WATCH_STABILITY_CHECKS` (por defecto `3`)
- `WATCH_STABILITY_INTERVAL` (por defecto `1s`)
- `KAFKA_BOOTSTRAP_SERVERS` (por defecto `kafka:9092`)
- `KAFKA_SCHEMA_REGISTRY_URL` (por defecto `http://schema-registry:8081`)
- `KAFKA_FILE_READY_TOPIC` (por defecto `file.ready`)

## Construcción y pruebas

```bash
mvn -B test
mvn -B clean package
docker build -t csv-watcher:1.0.3 .
```

## Contrato de desarrollo

Todo cambio se realiza en rama dedicada, sincroniza `pom.xml`, `README.md` y `CHANGELOG.md`, incluye pruebas JUnit y entra en `main` mediante Pull Request con los checks `documentation-policy` y `test` en verde. Consulta `AGENTS.md`.
