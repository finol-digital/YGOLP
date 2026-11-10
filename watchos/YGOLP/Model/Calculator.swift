import Foundation

/// Pure calculator state: an operand typed on the keypad combined with the
/// player's current life points according to the selected mode.
struct Calculator: Equatable, Sendable {
    static let maxOperandLength = 5

    let lifePoints: Int
    var mode: CalculatorMode
    private(set) var operandText = "0"

    init(lifePoints: Int, mode: CalculatorMode = .set) {
        self.lifePoints = lifePoints
        self.mode = mode
    }

    var operand: Int { Int(operandText) ?? 0 }

    /// The life point total that submitting would apply.
    var result: Int {
        let value = switch mode {
        case .add: lifePoints + operand
        case .subtract: lifePoints - operand
        case .set: operand
        }
        return value.clamped(to: LifePoints.range)
    }

    var halved: Int { lifePoints / 2 }

    mutating func append(_ digits: String) {
        let text = String(((operandText == "0" ? "" : operandText) + digits).prefix(Self.maxOperandLength))
        operandText = (text.isEmpty || Int(text) == 0) ? "0" : text
    }

    mutating func backspace() {
        operandText = operandText.count > 1 ? String(operandText.dropLast()) : "0"
    }

    mutating func cycleMode() {
        mode = mode.next
    }
}
