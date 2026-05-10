## ADDED Requirements

### Requirement: Calculator operand input is limited to 5 digits
The `append()` function in the calculator SHALL reject input that would make the operand text exceed 5 characters. The operand display SHALL never show more than 5 digits.

#### Scenario: User enters 5 digits then tries a 6th
- **WHEN** the operand text is "99999" and the user taps "0"
- **THEN** the operand text SHALL remain "99999"

#### Scenario: Appending "000" when 4 digits exist
- **WHEN** the operand text is "9999" and the user taps "000"
- **THEN** the operand text SHALL become "99990" (truncated to 5 chars) or the append SHALL be rejected entirely

#### Scenario: Appending "00" when at limit
- **WHEN** the operand text is "99999" and the user taps "00"
- **THEN** the operand text SHALL remain "99999"

