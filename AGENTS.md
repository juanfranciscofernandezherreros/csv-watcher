# Repository Rules for OpenAI and AI Agents

Estas reglas son obligatorias para cambios automatizados en este repositorio.

## Every non-merge commit

1. Trabajar en una rama dedicada; no enviar cambios directamente a `main`.
2. Hacer un único cambio lógico por commit.
3. Incrementar la versión en `pom.xml`.
4. Actualizar conjuntamente `CHANGELOG.md` y `README.md`.
5. Añadir o actualizar pruebas JUnit para cambios funcionales o de configuración.
6. Ejecutar `mvn -B test` con JDK 21.
7. Incluir la versión en el mensaje de commit.
8. Abrir Pull Request hacia `main` y no fusionar hasta que `documentation-policy` y `test` estén en verde.
9. Eliminar automáticamente la rama origen tras un merge correcto.

## Baseline

El baseline soportado es JDK 21. `README.md`, `CHANGELOG.md` y `pom.xml` forman un conjunto de versión sincronizado. `main` debe estar protegida y aceptar cambios únicamente mediante Pull Request, salvo el commit inicial necesario para crear el repositorio vacío.
