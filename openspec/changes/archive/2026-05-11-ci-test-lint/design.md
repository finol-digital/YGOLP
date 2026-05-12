## Context

The YGOLP project is a Wear OS Yu-Gi-Oh! life-point tracker built with Jetpack Compose. It currently has no local unit tests and only one instrumented test placeholder. The GitHub Actions CI workflow (`android.yml`) runs `./gradlew build` without explicit lint or test steps. The build task implicitly includes some checks, but failures are not surfaced as separate, visible quality gates.

Key source files with testable pure logic:
- `Player` enum with `fromInt()` companion method
- `CalculatorMode` enum with `fromInt()` companion method
- `Constants.kt` defining `MIN_LIFE_POINTS`, `MAX_LIFE_POINTS`, `STARTING_LIFE_POINTS`

## Goals / Non-Goals

**Goals:**
- Add local JVM unit tests for all pure-logic classes (enums, constants)
- Replace the placeholder `ExampleInstrumentedTest` with Compose UI instrumented tests for `LifePointsScreen` and `CalculatorScreen`
- Add `junit` and `kotlin-test` as `testImplementation` dependencies
- Run `./gradlew lint` and `./gradlew test` as explicit steps before `./gradlew build` in CI
- Make lint and test failures block the build, providing clear feedback on PRs

**Non-Goals:**
- Testing `MainViewModel` (requires Android context / mocking — future work)
- Achieving high code coverage thresholds
- Adding code coverage reporting tools
- Running instrumented tests in CI (requires an emulator — future work)

## Decisions

1. **Local JVM tests over instrumented tests** — Pure-logic classes (`Player`, `CalculatorMode`, constants) have no Android dependencies and can run on the JVM. This is faster and simpler than Robolectric or instrumented tests.

2. **JUnit 4 + kotlin-test** — JUnit 4 is already in the project's dependency catalog (via `ui-test-junit4`). Adding `junit:junit` and `kotlin-test` for local tests keeps things consistent and minimal.

3. **Compose UI tests for screens** — `LifePointsScreen` and `CalculatorScreen` are Composable functions that accept state as parameters (no ViewModel dependency), making them straightforward to test with `createComposeRule()`. The existing `ui-test-junit4` and `ui-test-manifest` dependencies already cover the instrumented test infrastructure. Tests will replace the placeholder `ExampleInstrumentedTest`.

4. **Separate CI steps for lint and test** — Running `./gradlew lint` and `./gradlew test` as individual named steps (before `build`) makes failures immediately visible in the GitHub Actions UI. The `build` task already runs these internally, but explicit steps provide better diagnostics.

5. **Keep the existing `build` step** — The `build` step remains to produce the APK artifact. Lint and test steps run before it to fail fast.

## Risks / Trade-offs

- **[Increased CI time]** → Lint and test add ~1-2 minutes. Acceptable for the quality gate benefits. Gradle caching mitigates most overhead since `build` won't re-run already-completed tasks.
- **[Lint may surface existing warnings]** → The project already has `lint.xml` for suppression. If new warnings appear, they can be addressed or suppressed as needed.

