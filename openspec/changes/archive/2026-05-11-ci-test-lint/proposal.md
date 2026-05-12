## Why

The project has no unit tests and the GitHub Actions CI workflow only builds the app without running lint or tests first. Adding unit tests and running both lint and tests before the build catches regressions earlier, prevents broken code from being merged, and establishes a quality baseline for future development.

## What Changes

- Add JUnit and Kotlin test dependencies for local (non-instrumented) unit tests
- Create unit tests for pure-logic classes: `Player`, `CalculatorMode`, and `Constants`
- Replace the placeholder `ExampleInstrumentedTest` with real Compose UI instrumented tests for `LifePointsScreen` and `CalculatorScreen`
- Update the GitHub Actions `android.yml` workflow to run `lint` and `test` tasks before `build`

## Capabilities

### New Capabilities
- `unit-testing`: Local JVM unit tests covering enums, constants, and other pure-logic code
- `instrumented-testing`: Compose UI tests replacing the placeholder ExampleInstrumentedTest, covering LifePointsScreen display, mute toggle, and CalculatorScreen digit entry, mode cycling, and submit
- `ci-quality-gates`: Lint and unit test steps in the CI workflow that must pass before building

### Modified Capabilities
- `build-hygiene`: Adding CI enforcement of lint checks and test execution as a build prerequisite

## Impact

- **Code**: New test source files under `app/src/test/` and `app/src/androidTest/`; test dependencies added to `app/build.gradle.kts` and `gradle/libs.versions.toml`
- **CI**: `.github/workflows/android.yml` gains lint and test steps before the build step
- **Dependencies**: `junit`, `kotlin-test` added as `testImplementation`; existing `ExampleInstrumentedTest.kt` replaced with real UI tests

