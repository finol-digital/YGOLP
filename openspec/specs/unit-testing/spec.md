### Requirement: Player enum round-trips through fromInt
The `Player.fromInt()` method SHALL return the correct `Player` entry for each defined value and SHALL default to `Player.ONE` for undefined values.

#### Scenario: Known player values
- **WHEN** `Player.fromInt(1)` is called
- **THEN** the result SHALL be `Player.ONE`

#### Scenario: Known player two value
- **WHEN** `Player.fromInt(2)` is called
- **THEN** the result SHALL be `Player.TWO`

#### Scenario: Unknown player value defaults to ONE
- **WHEN** `Player.fromInt(99)` is called
- **THEN** the result SHALL be `Player.ONE`

### Requirement: CalculatorMode enum round-trips through fromInt
The `CalculatorMode.fromInt()` method SHALL return the correct `CalculatorMode` entry for each defined value and SHALL default to `CalculatorMode.SET` for undefined values.

#### Scenario: SET mode value
- **WHEN** `CalculatorMode.fromInt(0)` is called
- **THEN** the result SHALL be `CalculatorMode.SET`

#### Scenario: SUBTRACT mode value
- **WHEN** `CalculatorMode.fromInt(1)` is called
- **THEN** the result SHALL be `CalculatorMode.SUBTRACT`

#### Scenario: ADD mode value
- **WHEN** `CalculatorMode.fromInt(2)` is called
- **THEN** the result SHALL be `CalculatorMode.ADD`

#### Scenario: Unknown mode value defaults to SET
- **WHEN** `CalculatorMode.fromInt(99)` is called
- **THEN** the result SHALL be `CalculatorMode.SET`

### Requirement: Life-point constants are correct
The constants `MIN_LIFE_POINTS`, `MAX_LIFE_POINTS`, and `STARTING_LIFE_POINTS` SHALL have the expected values.

#### Scenario: Constants have expected values
- **WHEN** the constants are read
- **THEN** `MIN_LIFE_POINTS` SHALL equal `0`, `MAX_LIFE_POINTS` SHALL equal `99999`, and `STARTING_LIFE_POINTS` SHALL equal `8000`

### Requirement: Test dependencies are available
The project SHALL include JUnit and kotlin-test as `testImplementation` dependencies so that local JVM tests can compile and run.

#### Scenario: Unit tests compile and execute
- **WHEN** `./gradlew test` is run
- **THEN** all unit tests SHALL compile and pass with exit code 0

