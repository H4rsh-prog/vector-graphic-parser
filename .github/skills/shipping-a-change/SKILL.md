---
name: shipping-a-change
description: "Ship a small Java/Spring Boot change with the repository's validation and review rules."
---

# Shipping a change

Use this workflow for small, well-scoped updates in this repository.

## Before coding

- Read [AGENTS.md](../../../AGENTS.md) and the relevant package under `src/main/java`.
- Review the nearest tests in `src/test/java` before changing behavior.
- Keep the change limited to the actual bug or feature.

## Implementation checklist

- Prefer small, explicit Java classes and methods.
- Avoid adding new runtime dependencies unless the task requires them.
- Keep Spring Boot configuration changes minimal and intentional.
- Add or update tests for changed behavior.

## Validation

Run the repo's standard verification before finishing:

```bash
./mvnw test
```

## Merge gate

- The change must pass the Maven test suite.
- Any runtime, configuration, or dependency change must receive human review before merge.
- If behavior changes, update the relevant documentation or repo guidance.
