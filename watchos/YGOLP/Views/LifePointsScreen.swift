import SwiftUI

struct CalculatorRequest: Identifiable, Hashable {
    let player: Player
    let mode: CalculatorMode

    var id: String { "\(player.rawValue)-\(mode.rawValue)" }
}

/// Swipe left and right between the two players' life points.
struct LifePointsScreen: View {
    let duel: DuelViewModel

    @State private var selectedPlayer: Player = .one
    @State private var calculatorRequest: CalculatorRequest?
    @State private var showingDuelMenu = false

    var body: some View {
        NavigationStack {
            TabView(selection: $selectedPlayer) {
                ForEach(Player.allCases) { player in
                    LifePointsPage(
                        player: player,
                        lifePoints: duel.displayedLifePoints(for: player),
                        onSelectMode: { calculatorRequest = CalculatorRequest(player: player, mode: $0) },
                        onRestart: { duel.restart() }
                    )
                    .tag(player)
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .never))
            .ignoresSafeArea()
            .overlay(alignment: .bottom) {
                PlayerIndicator(selected: selectedPlayer)
                    .padding(.bottom, 4)
                    .allowsHitTesting(false)
            }
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button(action: duel.toggleMute) {
                        Image(systemName: duel.isMuted ? "speaker.slash.fill" : "speaker.wave.2.fill")
                    }
                    .accessibilityLabel(duel.isMuted ? "Unmute" : "Mute")
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button { showingDuelMenu = true } label: {
                        Image(systemName: "arrow.counterclockwise")
                    }
                    .accessibilityLabel("Duel menu")
                    .accessibilityIdentifier("duelMenu")
                }
            }
        }
        .fullScreenCover(item: $calculatorRequest) { request in
            CalculatorScreen(
                player: request.player,
                lifePoints: duel.lifePoints(for: request.player),
                initialMode: request.mode,
                onDiscard: { calculatorRequest = nil },
                onSubmit: { result in
                    duel.changeLifePoints(to: result, for: request.player)
                    calculatorRequest = nil
                }
            )
        }
        .confirmationDialog("Duel", isPresented: $showingDuelMenu, titleVisibility: .hidden) {
            Button("Restart Duel", role: .destructive) { duel.restart() }
            if !duel.isMuted {
                Button("It's Time to Duel!") { duel.playItsTimeToDuel() }
            }
            Button("Cancel", role: .cancel) {}
        }
    }
}

#Preview {
    let defaults = UserDefaults(suiteName: "preview")!
    defaults.set(LifePoints.starting, forKey: DuelViewModel.StorageKey.lifePointsP1)
    defaults.set(LifePoints.starting, forKey: DuelViewModel.StorageKey.lifePointsP2)
    return LifePointsScreen(duel: DuelViewModel(defaults: defaults))
}
