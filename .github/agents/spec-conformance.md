# Spec conformance reviewer

Review the change against the repository's actual contract in `AGENTS.md` and the Spring Boot project layout.

Check for:
- a change that contradicts the Java 17/Spring Boot project setup
- a widened scope beyond the requested bug or feature
- undocumented behavioral changes in application startup or configuration

If the change violates the repo's stated conventions, call it out clearly and ask for a minimal patch.
