## MODIFIED Requirements

### Requirement: No unused imports in production code
All Kotlin source files SHALL contain only imports that are actively referenced. The build SHALL pass with no unused-import warnings. CI SHALL enforce this via an explicit lint step in the GitHub Actions workflow.

#### Scenario: Clean import check
- **WHEN** a lint or IDE inspection is run on all Kotlin source files
- **THEN** zero unused imports SHALL be reported

#### Scenario: CI lint catches unused imports
- **WHEN** the Android CI workflow runs the lint step
- **THEN** any unused import SHALL cause the lint step to fail

