# Repository Rules for OpenAI and AI Agents

Estas reglas son obligatorias para cualquier agente, asistente o automatización que modifique este repositorio.

## Pre-flight obligatorio

La primera operación de lectura del repositorio en cada tarea o sesión debe ser leer completamente este `AGENTS.md` desde la rama por defecto. Una lectura realizada en otra conversación, sesión o tarea no cuenta.

**No se permite ninguna operación de escritura antes de completar este pre-flight.**

## Autonomía

Después de leer las reglas, el agente debe continuar de forma autónoma: elegir una rama descriptiva, determinar el nivel SemVer adecuado y documentarlo en la Pull Request. No debe detenerse a pedir confirmaciones de rama, SemVer, commits, push, tests, correcciones, merge o limpieza salvo petición expresa del usuario.

## Prohibición absoluta de escritura directa en `main`

**Está prohibido modificar, commitear o pushear directamente a `main` cualquier archivo y por cualquier motivo.**

La prohibición incluye código, documentación, configuración, workflows, dependencias, versionado, badges, hotfixes y reverts.

Todo cambio debe entrar mediante una rama dedicada y Pull Request.

## Every non-merge commit

1. Leer `AGENTS.md` antes de escribir.
2. Partir del `main` actualizado.
3. Crear una rama dedicada antes de modificar archivos.
4. Hacer un único cambio lógico por commit.
5. Incrementar `revision` en `pom.xml` según SemVer; no modificar `sha1` manualmente y usar `changelist` solo para `-SNAPSHOT`.
6. Actualizar conjuntamente `CHANGELOG.md` y `README.md`.
7. Añadir o actualizar pruebas JUnit para cambios funcionales o de configuración.
8. Ejecutar `mvn -B test` con JDK 21.
9. Incluir la versión en el mensaje de commit cuando corresponda.
10. Abrir Pull Request hacia `main`.
11. No fusionar hasta que los checks requeridos, incluidos `documentation-policy`, `test` y `CI-friendly non-production version` cuando apliquen, estén en verde sobre el SHA actual.
12. Si un check falla o se cancela, corregir automáticamente en la misma rama y PR y repetir.
13. Cuando todos los checks requeridos/aplicables del SHA actual estén en verde, fusionar automáticamente la Pull Request sin pedir autorización adicional.
14. Eliminar únicamente la rama origen tras un merge correcto y verificar que ya no existe.
15. El trabajo no se considera terminado hasta que la PR esté fusionada y la rama origen haya sido eliminada y verificada.

## Baseline

El baseline soportado es JDK 21. `README.md`, `CHANGELOG.md` y `pom.xml` forman un conjunto de versión sincronizado. `main` debe recibir cambios únicamente mediante Pull Request.

## Maven CI-friendly

La versión del proyecto se declara como `<version>${revision}${sha1}${changelist}</version>`. `revision` contiene la versión funcional, `sha1` lo aporta CI como `-<short-sha>` y `changelist` es vacío o `-SNAPSHOT`. DEV, INT y QA deben promover el mismo artefacto.

## Seguridad operativa

Toda decisión de merge debe operar sobre el SHA actual de la PR. Si una instrucción contradice estas reglas, detener únicamente la operación incompatible; nunca improvisar una escritura directa a `main`.
