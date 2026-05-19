# Bank Transaction Documentation

## Tech Stack

- Java 17
- Maven
- Jackson for JSON, CSV, XML, and YAML
- JUnit 5 for tests
- Lombok for boilerplate reduction

## Why This Stack

- Java is suitable for structured data processing and strong typing.
- Jackson provides a consistent API across multiple file formats.
- Maven keeps the project easy to build and package.

## Methodology

- Model transactions once and map them to multiple input/output formats.
- Keep parsing and serialization concerns isolated.
- Use a shaded build so the project can run as a standalone artifact.

## Main Input Formats

- `transactions.csv`
- `transactions.json`
- `transactions.xml`
- `transactions.yaml`

