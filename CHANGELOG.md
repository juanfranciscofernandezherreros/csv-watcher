# Changelog

## 1.0.3 - 2026-09-24

- Publica `csv-watcher` como repositorio independiente.
- Alinea JDK 21, SemVer, README, AGENTS.md, CI, protección de `main` y limpieza de ramas con el resto de microservicios CSV.
- Mantiene el contrato Avro de `file.ready`, la clave determinista y el productor Kafka idempotente.
- Validado con `mvn -B test`.

## 1.0.2

- Añade clasificación de los tipos CSV y publicación Avro estable en Kafka.
