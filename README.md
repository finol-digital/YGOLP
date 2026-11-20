# YGOLP

**Yu-Gi-Oh! LifePoints (LP) Tracker/Calculator for Wear OS and Apple Watch**

YGOLP is a simple and intuitive Life Points tracker and calculator designed specifically for Yu-Gi-Oh! players using Wear OS smartwatches and Apple Watch. It allows duelists to quickly manage and calculate Life Points during games, making dueling more convenient and accessible on the go.

## Features

- Quick and easy Life Points adjustment for both players
- Clean, minimal interface optimized for small screens
- Designed for Wear OS smartwatches and Apple Watch

## Installation

You can download and install YGOLP directly from the Google Play Store:

[![Get it on Google Play](https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=com.finoldigital.ygolp)

Or search for "YGOLP" or "Yu-Gi-Oh! LifePoints" on your Wear OS device's Play Store app.

## Usage

Keep track of your game with a simple glance at your watch.
Losing or gaining life causes the classic animation and sound effects.

Swipe left and right to navigate between 2 different players.

Tap on the top 3rd of the screen to enter the gain life calculator.
Tap in the center of the screen to enter the set life total calculator.
Tap on the bottom 3rd of the screen to enter the reduce life total calculator.

In the calculator, you can toggle between the 3 modes by pressing the operator button.
The C button is backspace, and the X button cancels.
The submit button will apply the calculation with the number you have input.

There's also a bonus audio clip if your watch has physical multifunction button(s)!

### Apple Watch

The Apple Watch app works the same way, with a few watchOS-specific touches:

- The mute toggle sits in the top-left corner.
- The ⟲ button in the top-right corner opens the duel menu, where you can restart the duel or play the bonus "It's time to duel!" clip (Apple Watch buttons can't be used by apps).
- In the calculator, the system ✕ in the top-left corner discards, just like the X key.

## Apple Watch Development

The watchOS app lives in [`watchos/`](watchos) and is a native SwiftUI, watch-only app (watchOS 10+). It reuses the sounds, font, and background image from the Android app's `res/` folder, so there is a single copy of each asset.

```sh
open watchos/YGOLP.xcodeproj
```

Pick the `YGOLP` scheme and an Apple Watch simulator, then run. To build for a device or submit to the App Store, set your development team under *Signing & Capabilities*.

Run the unit and UI tests from the command line with:

```sh
xcodebuild test -project watchos/YGOLP.xcodeproj -scheme YGOLP \
  -destination 'platform=watchOS Simulator,name=Apple Watch Series 11 (46mm)'
```

## Contributing

Contributions, bug reports, and feature requests are welcome! To contribute:

1. Fork this repository
2. Create a new branch for your feature or fix
3. Commit your changes
4. Open a pull request describing your changes

Please follow the existing code style and include tests where appropriate.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.