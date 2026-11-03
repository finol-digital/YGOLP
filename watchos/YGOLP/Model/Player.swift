import Foundation

enum Player: Int, CaseIterable, Identifiable, Hashable, Sendable {
    case one = 1
    case two = 2

    var id: Int { rawValue }
}
