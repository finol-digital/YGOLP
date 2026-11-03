import SwiftUI

/// Keypad for gaining, setting, or losing life points. The operator button
/// cycles between modes; ½ halves immediately; X discards; = submits.
struct CalculatorScreen: View {
    let player: Player
    var onDiscard: () -> Void = {}
    var onSubmit: (Int) -> Void = { _ in }

    @State private var calculator: Calculator

    private let spacing: CGFloat = 3
    private let horizontalPadding: CGFloat = 4
    private let indicatorHeight: CGFloat = 12

    init(
        player: Player = .one,
        lifePoints: Int = LifePoints.starting,
        initialMode: CalculatorMode = .set,
        onDiscard: @escaping () -> Void = {},
        onSubmit: @escaping (Int) -> Void = { _ in }
    ) {
        self.player = player
        self.onDiscard = onDiscard
        self.onSubmit = onSubmit
        _calculator = State(initialValue: Calculator(lifePoints: lifePoints, mode: initialMode))
    }

    var body: some View {
        NavigationStack {
            GeometryReader { proxy in
                // The top safe area holds the system close button, title and clock;
                // the keypad extends to the bottom edge, leaving room for the indicator.
                let keyWidth = (proxy.size.width - 2 * horizontalPadding - 3 * spacing) / 4
                let keyHeight = (proxy.size.height - indicatorHeight - 4 * spacing) / 5

                VStack(spacing: spacing) {
                    operatorRow(keyWidth: keyWidth)
                        .frame(height: keyHeight)
                    keyRow(keyWidth: keyWidth, keyHeight: keyHeight) {
                        digitKey("7"); digitKey("8"); digitKey("9")
                        Button("½") { onSubmit(calculator.halved) }
                            .buttonStyle(KeyButtonStyle(background: AppColors.halveKey, foreground: AppColors.keyDarkText))
                            .accessibilityLabel("Halve")
                    }
                    keyRow(keyWidth: keyWidth, keyHeight: keyHeight) {
                        digitKey("4"); digitKey("5"); digitKey("6")
                        Button("X", action: onDiscard)
                            .buttonStyle(KeyButtonStyle(background: AppColors.discardKey, foreground: AppColors.keyDarkText))
                            .accessibilityLabel("Discard")
                    }
                    keyRow(keyWidth: keyWidth, keyHeight: keyHeight) {
                        digitKey("1"); digitKey("2"); digitKey("3")
                        Button("=") { onSubmit(calculator.result) }
                            .buttonStyle(KeyButtonStyle(background: AppColors.submitKey, foreground: AppColors.keyDarkText))
                            .accessibilityLabel("Submit")
                    }
                    keyRow(keyWidth: keyWidth, keyHeight: keyHeight) {
                        digitKey("0")
                        digitKey("00").accessibilityLabel("Double zero")
                        digitKey("000").accessibilityLabel("Triple zero")
                    }
                }
                .padding(.horizontal, horizontalPadding)
                .padding(.bottom, indicatorHeight)
                .overlay(alignment: .bottom) {
                    PlayerIndicator(selected: player)
                        .frame(height: indicatorHeight)
                        .allowsHitTesting(false)
                }
            }
            .ignoresSafeArea(edges: [.horizontal, .bottom])
            .background(.black)
            // Current life points share the top row with the clock.
            .navigationTitle(Text(verbatim: String(calculator.lifePoints)))
            .navigationBarTitleDisplayMode(.inline)
        }
    }

    private func operatorRow(keyWidth: CGFloat) -> some View {
        HStack(spacing: spacing) {
            Button(calculator.mode.symbol) { calculator.cycleMode() }
                .buttonStyle(KeyButtonStyle(background: AppColors.operatorKey, foreground: calculator.mode.color))
                .frame(width: keyWidth)
                .accessibilityLabel(calculator.mode.accessibilityLabel)
                .accessibilityHint("Changes the calculation mode")

            Text(verbatim: calculator.operandText)
                .font(.system(size: 22, weight: .semibold, design: .rounded))
                .foregroundStyle(calculator.mode.color)
                .lineLimit(1)
                .minimumScaleFactor(0.6)
                .frame(maxWidth: .infinity)
                .contentTransition(.numericText())
                .accessibilityIdentifier("operand")

            Button { calculator.backspace() } label: {
                Image(systemName: "delete.left.fill")
            }
            .buttonStyle(KeyButtonStyle(background: AppColors.operatorKey, foreground: AppColors.backspace))
            .frame(width: keyWidth)
            .accessibilityLabel("Backspace")
        }
    }

    private func keyRow<Content: View>(
        keyWidth: CGFloat,
        keyHeight: CGFloat,
        @ViewBuilder content: () -> Content
    ) -> some View {
        HStack(spacing: spacing) {
            content()
                .frame(width: keyWidth, height: keyHeight)
        }
        .frame(maxWidth: .infinity)
    }

    private func digitKey(_ digits: String) -> some View {
        Button(digits) { calculator.append(digits) }
            .buttonStyle(KeyButtonStyle(background: AppColors.digitKey, foreground: .white))
    }
}

struct KeyButtonStyle: ButtonStyle {
    let background: Color
    let foreground: Color

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.system(size: 18, weight: .semibold, design: .rounded))
            .foregroundStyle(foreground)
            .lineLimit(1)
            .minimumScaleFactor(0.6)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(background, in: Capsule())
            .contentShape(Capsule())
            .opacity(configuration.isPressed ? 0.6 : 1)
            .scaleEffect(configuration.isPressed ? 0.94 : 1)
            .animation(.easeOut(duration: 0.1), value: configuration.isPressed)
    }
}

#Preview {
    CalculatorScreen()
}
