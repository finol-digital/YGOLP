import SwiftUI
import UIKit

/// One player's page. Tap the top third to gain, the middle to set, and the
/// bottom third to lose life points. At 0 the whole page restarts the duel.
struct LifePointsPage: View {
    let player: Player
    let lifePoints: Int
    var onSelectMode: (CalculatorMode) -> Void = { _ in }
    var onRestart: () -> Void = {}

    private var isLost: Bool { lifePoints <= LifePoints.minimum }

    var body: some View {
        ZStack {
            PlayerBackground(player: player)

            if isLost {
                tapZone("Restart duel", id: "restart", action: onRestart)
            } else {
                VStack(spacing: 0) {
                    tapZone("Gain life points", id: "gain") { onSelectMode(.add) }
                    tapZone("Set life points", id: "set") { onSelectMode(.set) }
                    tapZone("Lose life points", id: "lose") { onSelectMode(.subtract) }
                }
            }

            Text(verbatim: String(max(lifePoints, LifePoints.minimum)))
                .font(.lifePoints(size: 32))
                .foregroundStyle(AppColors.lifePointsYellow)
                .lineLimit(1)
                .minimumScaleFactor(0.5)
                .padding(.horizontal, 8)
                .allowsHitTesting(false)
                .accessibilityLabel("Player \(player.rawValue): \(String(lifePoints)) life points")
                .accessibilityIdentifier("lifePoints-\(player.rawValue)")
        }
        .ignoresSafeArea()
    }

    /// An invisible full-width button covering one third of the page.
    private func tapZone(_ label: LocalizedStringKey, id: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Rectangle()
                .fill(.clear)
                .contentShape(Rectangle())
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        .buttonStyle(.plain)
        .accessibilityLabel(label)
        .accessibilityIdentifier("\(id)-\(player.rawValue)")
    }
}

private struct PlayerBackground: View {
    let player: Player

    var body: some View {
        switch player {
        case .one:
            if let image = UIImage(named: "lifepoints_background.jpg") {
                Image(uiImage: image)
                    .resizable()
                    .accessibilityHidden(true)
            } else {
                Color.black
            }
        case .two:
            LinearGradient(
                colors: [AppColors.p2GradientStart, AppColors.p2GradientEnd],
                startPoint: .top,
                endPoint: .bottom
            )
        }
    }
}

#Preview("Player 1") {
    LifePointsPage(player: .one, lifePoints: LifePoints.starting)
}

#Preview("Player 2") {
    LifePointsPage(player: .two, lifePoints: LifePoints.starting)
}

#Preview("Lost") {
    LifePointsPage(player: .one, lifePoints: 0)
}
