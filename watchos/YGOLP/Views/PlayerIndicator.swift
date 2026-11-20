import SwiftUI

struct PlayerIndicator: View {
    let selected: Player

    var body: some View {
        HStack(spacing: 4) {
            ForEach(Player.allCases) { player in
                let shape = RoundedRectangle(cornerRadius: 4)
                Group {
                    if player == selected {
                        shape.fill(AppColors.indicator)
                    } else {
                        shape.stroke(AppColors.indicator, lineWidth: 1)
                    }
                }
                .frame(width: 12, height: 6)
            }
        }
        .accessibilityElement()
        .accessibilityLabel("Player \(selected.rawValue)")
    }
}

#Preview {
    VStack(spacing: 12) {
        PlayerIndicator(selected: .one)
        PlayerIndicator(selected: .two)
    }
}
