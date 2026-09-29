# AGENTS.md

## Build, test, and run

- Use the Maven wrapper for all Java commands: `./mvnw test`, `./mvnw package`, and `./mvnw spring-boot:run`.
- Java 17 is required by the project configuration in `pom.xml`.
- This project is a Spring Boot application; run it locally with the Boot launcher or by invoking the packaged JAR after `./mvnw package`.

## Project conventions

- Keep the code under `src/main/java/com/vgp` and mirror test classes under `src/test/java`.
- Prefer small, explicit classes and methods over broad refactors or hidden abstraction layers.
- Do not introduce new runtime libraries unless the task specifically requires them.
- Use JUnit 5 for automated tests and place them beside the production code in the same package structure.
- Favor straightforward Spring Boot wiring and do not add unnecessary configuration or startup complexity.

## Maintenance matrix

| Area | Files to review | When changed |
| --- | --- | --- |
| Application bootstrap | `src/main/java/com/vgp/VectorGraphicParserApplication.java` | Startup, config, or package wiring |
| Build and dependency policy | `pom.xml`, `mvnw` | Java version, dependencies, or project runtime behavior |
| Tests | `src/test/java/**` | Any logic or regression fix |
| Runtime configuration | `src/main/resources/application.properties` | Environment defaults, ports, or app behavior |
| AI/project instructions | `.github/**`, `AGENTS.md`, `README.md` | Any repo workflow or contributor guidance change |

## Done means

- `./mvnw test` exits successfully.
- The change stays within the relevant package boundary and does not broaden scope without a clear reason.
- Any behavior change is reflected in tests or documentation when appropriate.
- No new warnings or build errors are introduced.

## Never merges without a human

Definition: any runtime, configuration, dependency, or security-sensitive change needs human review before merge.

- Do not merge a change that modifies the Spring Boot startup path, application configuration, or dependency set without human approval.
- Do not merge a new test-only shortcut or a broad refactor that hides a behavior change.
- Do not merge a change that alters public behavior without updating the relevant documentation or tests.

## Common pitfalls

- Do not change package names or directory structure without updating imports and tests.
- Do not add unneeded dependencies when the problem can be solved with the existing Java and Spring Boot stack.
- Do not broaden a fix into unrelated cleanup or large rewrites.

## Repository notes

- The project is intentionally small and focused; prefer surgical edits.
- If you are unsure whether a change is in scope, read the relevant package and existing tests before making a patch.
