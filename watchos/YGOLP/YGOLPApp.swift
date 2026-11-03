import SwiftUI

@main
struct YGOLPApp: App {
    @State private var duel = Self.makeDuel()

    var body: some Scene {
        WindowGroup {
            LifePointsScreen(duel: duel)
        }
    }

    private static func makeDuel() -> DuelViewModel {
        #if DEBUG
        // UI tests launch with a fresh, muted duel at the starting life points.
        if ProcessInfo.processInfo.arguments.contains("-uiTesting") {
            let suite = "ui-testing"
            let defaults = UserDefaults(suiteName: suite)!
            defaults.removePersistentDomain(forName: suite)
            defaults.set(true, forKey: DuelViewModel.StorageKey.isMuted)
            for player in Player.allCases {
                defaults.set(LifePoints.starting, forKey: DuelViewModel.StorageKey.lifePoints(player))
            }
            return DuelViewModel(defaults: defaults, animationDuration: .zero)
        }
        #endif
        return DuelViewModel()
    }
}
