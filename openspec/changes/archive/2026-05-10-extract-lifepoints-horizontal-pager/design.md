## Context

`LifePointsScreen` currently renders the mute button and `PlayerIndicator` inline within each page of a `HorizontalPager` in `WearApp`. The pager creates two instances of `LifePointsScreen` (one per player), so both overlay elements are duplicated and slide with page content during swipes. The mute button is a global toggle (same state on both pages) and the player indicator reflects which page is active — both are pager-level concerns, not page-level.

## Goals / Non-Goals

**Goals:**
- Extract pager setup, mute button, and player indicator into a `LifePointsHorizontalPager` composable
- Mute button renders once as a fixed overlay (no animation during swipe)
- Player indicator renders once as a fixed-position overlay, with its active dot driven by `pagerState.currentPage`
- Simplify `LifePointsScreen` to only contain player-specific content (background, touch zones, life points text)
- Simplify `WearApp` by delegating pager logic to the new composable

**Non-Goals:**
- Changing mute button or player indicator visual design
- Changing touch zone behavior or calculator navigation
- Refactoring `MainViewModel` or state management

## Decisions

**1. New composable location: `presentation/screens/LifePointsHorizontalPager.kt`**

Rationale: It orchestrates the `LifePointsScreen` pages and lives at the same abstraction level. It's a screen-level composition, not a reusable component.

**2. Overlay via `Box` z-ordering (pager first, overlays after)**

Rationale: Compose `Box` draws children in declaration order. Placing `HorizontalPager` first, then `PlayerIndicator` and mute button, ensures overlays render on top of sliding content without needing `zIndex`. This is the standard Compose pattern for fixed overlays on pagers.

**3. Remove `isMuted` and `onToggleMute` from `LifePointsScreen` signature**

Rationale: These parameters only served the mute button. Once the mute button moves to the pager level, `LifePointsScreen` no longer needs them. Cleaner API.

## Risks / Trade-offs

- [Preview breakage] `LifePointsScreenPreview` currently shows the full screen including mute button — after extraction, previews will lack it. → Acceptable; add a preview for `LifePointsHorizontalPager` if needed.
- [Touch interception] Overlaid mute button at top-center could intercept touches meant for the ADD touch zone. → This already exists today and is mitigated by the mute button's 48dp tap target being smaller than the zone.
