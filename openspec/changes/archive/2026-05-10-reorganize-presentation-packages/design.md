## Context

The presentation layer currently has a flat `screens/` package containing route definitions, navigation destinations, and sub-screen components. With only 2 navigation destinations (life points and calculator), a lightweight restructure is appropriate — enough to clarify naming without over-engineering.

## Goals / Non-Goals

**Goals:**
- Clear distinction between navigation destinations (`*Screen`) and components within them (`*Page`)
- Route definitions separated from UI composables
- Naming conventions that scale naturally if a third destination is ever added

**Non-Goals:**
- Feature-based package organization (unnecessary at 2 destinations)
- Extracting the nav graph from `MainActivity.kt`
- Any behavioral changes

## Decisions

**Flat `screens/` package, no feature subdirectories**
The app has 2 destinations. Feature folders add nesting with no payoff. A flat package with clear naming is sufficient.

**`Routes` instead of `Screens` for the sealed class**
The sealed class defines navigation routes, not screens. Renaming to `Routes` avoids confusion with `*Screen` composables and moves it to a `navigation/` package where it belongs. Alternative: keep it as `Screens` in `screens/` — rejected because it conflates route definitions with UI.

**`*Page` suffix for pager content**
`LifePointsPage` communicates that it's a page within a pager, not a standalone destination. Alternative: `*Content` suffix — rejected because "Page" maps directly to the `HorizontalPager` concept.

## Risks / Trade-offs

[Rename churn] → Small scope (4 files, ~10 import updates). Pure refactor with no behavioral change. Risk is minimal.
