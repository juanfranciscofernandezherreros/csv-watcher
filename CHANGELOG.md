# Changelog

## 1.0.6 - 2026-09-25

- [patch] Refuerza AGENTS.md con lectura obligatoria por tarea, autonomía y prohibición absoluta de escrituras directas en main.

## 1.0.5 - 2026-09-25

- [patch] Exige confirmar nombre de rama y nivel SemVer antes de cualquier cambio.
- [patch] Adopta Maven CI-friendly con `revision`, `sha1` y `changelist`.

## 1.0.3 - 2026-09-24

- Publica `csv-watcher` como repositorio independiente.
- Alinea JDK 21, SemVer, README, AGENTS.md, CI, protección de `main` y limpieza de ramas con el resto de microservicios CSV.
- Mantiene el contrato Avro de `file.ready`, la clave determinista y el productor Kafka idempotente.
- Validado con `mvn -B test`.

## 1.0.2

- Añade clasificación de los tipos CSV y publicación Avro estable en Kafka.
