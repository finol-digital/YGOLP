import Foundation

/// Life point limits shared by the duel state and the calculator.
enum LifePoints {
    static let minimum = 0
    static let maximum = 99_999
    static let starting = 8_000
    static let range = minimum...maximum
}

extension Comparable {
    func clamped(to range: ClosedRange<Self>) -> Self {
        min(max(self, range.lowerBound), range.upperBound)
    }
}
