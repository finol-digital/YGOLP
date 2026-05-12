## ADDED Requirements

### Requirement: CI runs lint before build
The GitHub Actions Android workflow SHALL execute `./gradlew lint` as a named step before the build step.

#### Scenario: Lint step precedes build
- **WHEN** the Android CI workflow is triggered
- **THEN** a step named "Lint" SHALL run `./gradlew lint` before the "Build with Gradle" step

#### Scenario: Lint failure blocks the build
- **WHEN** `./gradlew lint` exits with a non-zero code
- **THEN** the workflow SHALL fail and the build step SHALL NOT execute

### Requirement: CI runs unit tests before build
The GitHub Actions Android workflow SHALL execute `./gradlew test` as a named step before the build step.

#### Scenario: Test step precedes build
- **WHEN** the Android CI workflow is triggered
- **THEN** a step named "Run unit tests" SHALL run `./gradlew test` before the "Build with Gradle" step

#### Scenario: Test failure blocks the build
- **WHEN** `./gradlew test` exits with a non-zero code
- **THEN** the workflow SHALL fail and the build step SHALL NOT execute

