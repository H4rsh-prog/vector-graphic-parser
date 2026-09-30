# Vector Graphic Parser

An in-progress Java application for building a raster-to-vector graphics processing pipeline. The current code provides the initial image-processing steps; SVG generation and an HTTP API are not implemented yet.

## Current capabilities

- Load raster images supported by Java's `ImageIO`.
- Convert image pixels into a grayscale map with values normalized to `[0, 1]`.
- Create a binary mask by marking pixels whose grayscale value is greater than a supplied threshold.
- Inspect arrays while developing and testing the processing steps.

## Requirements

- Java 17+
- Maven wrapper (recommended) or Maven 3.9+

## Run the application

```bash
./mvnw spring-boot:run
```

This starts the Spring Boot application. The image-processing pipeline is currently available as Java utilities; no web endpoints are defined yet.

## Tests

The image-processing tests require a local raster image. Set its path in a root-level `.env` file (this file is ignored by Git):

```dotenv
test-image-path=C:/path/to/test-image.png
```

Then run:

```bash
./mvnw test
```

## Build

```bash
./mvnw package
```

## Repository conventions

For build rules, coding expectations, and merge boundaries, see [AGENTS.md](AGENTS.md).
