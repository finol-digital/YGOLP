### Requirement: Pager-level mute button overlay
The `LifePointsHorizontalPager` composable SHALL render the mute button as a fixed overlay at top-center, outside the `HorizontalPager` content. The mute button SHALL NOT animate or translate during page swipes.

#### Scenario: Mute button stays fixed during swipe
- **WHEN** the user swipes between player pages
- **THEN** the mute button remains stationary at top-center throughout the swipe gesture

#### Scenario: Mute button renders once
- **WHEN** the pager displays either player page
- **THEN** exactly one mute button instance is rendered (not one per page)

### Requirement: Pager-level player indicator overlay
The `LifePointsHorizontalPager` composable SHALL render the `PlayerIndicator` as an overlay at bottom-center, outside the `HorizontalPager` content. The indicator's active dot SHALL reflect `pagerState.currentPage`.

#### Scenario: Player indicator reflects current page
- **WHEN** the user is on page 0
- **THEN** the player indicator highlights Player ONE's dot
- **WHEN** the user is on page 1
- **THEN** the player indicator highlights Player TWO's dot

#### Scenario: Player indicator position is fixed
- **WHEN** the user swipes between player pages
- **THEN** the player indicator remains at bottom-center position

### Requirement: LifePointsScreen contains only player content
`LifePointsScreen` SHALL NOT render the mute button or player indicator. Its composable signature SHALL NOT include `isMuted` or `onToggleMute` parameters.

#### Scenario: LifePointsScreen without pager chrome
- **WHEN** `LifePointsScreen` is rendered
- **THEN** it displays only the background, touch zones, and life points text
