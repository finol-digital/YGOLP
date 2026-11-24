import Foundation

enum CalculatorMode: Int, CaseIterable, Identifiable, Hashable, Sendable {
    case set = 0
    case subtract = 1
    case add = 2

    var id: Int { rawValue }

    /// The mode the operator button cycles to next: set → subtract → add → set.
    var next: CalculatorMode {
        let all = Self.allCases
        return all[(all.firstIndex(of: self)! + 1) % all.count]
    }
}
