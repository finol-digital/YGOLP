import SwiftUI

enum AppColors {
    static let lifePointsYellow = Color(hex: 0xFBFF0C)
    static let p2GradientStart = Color(hex: 0x0000FF)
    static let p2GradientEnd = Color.black
    static let indicator = Color.white

    // Calculator keys (Wear OS Material defaults).
    static let operatorKey = Color(hex: 0x444444)
    static let digitKey = Color(hex: 0x303133)
    static let halveKey = Color(hex: 0xFDE293)
    static let discardKey = Color(hex: 0xEE675C)
    static let submitKey = Color(hex: 0xAECBFA)
    static let keyDarkText = Color(hex: 0x303133)
    static let backspace = Color(hex: 0xEE675C)

    static let addMode = Color(hex: 0x00FF00)
    static let subtractMode = Color(hex: 0xFF0000)
    static let setMode = Color(hex: 0xFFFF00)
}

extension Color {
    init(hex: UInt32) {
        self.init(
            red: Double((hex >> 16) & 0xFF) / 255,
            green: Double((hex >> 8) & 0xFF) / 255,
            blue: Double(hex & 0xFF) / 255
        )
    }
}

extension CalculatorMode {
    var symbol: String {
        switch self {
        case .add: "+"
        case .subtract: "-"
        case .set: "=>"
        }
    }

    var color: Color {
        switch self {
        case .add: AppColors.addMode
        case .subtract: AppColors.subtractMode
        case .set: AppColors.setMode
        }
    }

    var accessibilityLabel: LocalizedStringKey {
        switch self {
        case .add: "Add"
        case .subtract: "Subtract"
        case .set: "Set"
        }
    }
}

extension Font {
    /// The Yu-Gi-Oh! style life point font bundled from the Android app.
    static func lifePoints(size: CGFloat) -> Font { .custom("NationalyzeALP", fixedSize: size) }
}
