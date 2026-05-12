## 1. Add test dependencies

- [x] 1.1 Add `junit` and `kotlin-test` version entries to `gradle/libs.versions.toml` and declare library aliases
- [x] 1.2 Add `testImplementation` dependencies for `junit` and `kotlin-test` in `app/build.gradle.kts`

## 2. Create unit tests

- [x] 2.1 Create `app/src/test/java/com/finoldigital/ygolp/presentation/enums/PlayerTest.kt` covering `fromInt()` for known values (1→ONE, 2→TWO) and unknown value (99→ONE)
- [x] 2.2 Create `app/src/test/java/com/finoldigital/ygolp/presentation/enums/CalculatorModeTest.kt` covering `fromInt()` for known values (0→SET, 1→SUBTRACT, 2→ADD) and unknown value (99→SET)
- [x] 2.3 Create `app/src/test/java/com/finoldigital/ygolp/presentation/constants/ConstantsTest.kt` verifying MIN_LIFE_POINTS=0, MAX_LIFE_POINTS=99999, STARTING_LIFE_POINTS=8000

## 3. Replace ExampleInstrumentedTest with real instrumented tests

- [x] 3.1 Delete `app/src/androidTest/java/com/finoldigital/ygolp/ExampleInstrumentedTest.kt`
- [x] 3.2 Create `app/src/androidTest/java/com/finoldigital/ygolp/presentation/screens/LifePointsScreenTest.kt` with Compose UI tests: displays life points, shows mute/unmute icon based on state
- [x] 3.3 Create `app/src/androidTest/java/com/finoldigital/ygolp/presentation/screens/CalculatorScreenTest.kt` with Compose UI tests: displays life points, digit entry updates operand, mode cycling changes operator, submit triggers callback with correct result

## 4. Update CI workflow

- [x] 4.1 Add a "Lint" step running `./gradlew lint` in `.github/workflows/android.yml` after the permission grant and before the build step
- [x] 4.2 Add a "Run unit tests" step running `./gradlew test` in `.github/workflows/android.yml` after the lint step and before the build step

