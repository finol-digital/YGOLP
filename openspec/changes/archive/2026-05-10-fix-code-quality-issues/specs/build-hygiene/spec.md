## ADDED Requirements

### Requirement: compose-ui-tooling is a debug-only dependency
The build SHALL declare `compose-ui-tooling` as `debugImplementation` instead of `implementation`. Release APKs SHALL NOT contain UI tooling classes.

#### Scenario: Release APK excludes tooling
- **WHEN** the release build variant is assembled
- **THEN** the APK SHALL NOT contain any classes from `androidx.wear.compose:compose-ui-tooling`

### Requirement: No unused imports in production code
All Kotlin source files SHALL contain only imports that are actively referenced. The build SHALL pass with no unused-import warnings.

#### Scenario: Clean import check
- **WHEN** a lint or IDE inspection is run on all Kotlin source files
- **THEN** zero unused imports SHALL be reported

