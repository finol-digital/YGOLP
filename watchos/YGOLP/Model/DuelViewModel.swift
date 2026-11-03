import Foundation
import Observation

/// Duel state for both players: persisted life points, the animated values on
/// screen, and the mute setting. Mirrors the Wear OS `MainViewModel`.
@MainActor
@Observable
final class DuelViewModel {
    enum StorageKey {
        static let lifePointsP1 = "life_points_p1"
        static let lifePointsP2 = "life_points_p2"
        static let isMuted = "is_muted"

        static func lifePoints(_ player: Player) -> String {
            player == .one ? lifePointsP1 : lifePointsP2
        }
    }

    private(set) var lifePoints: [Player: Int] = [.one: 0, .two: 0]
    private(set) var displayedLifePoints: [Player: Int] = [.one: 0, .two: 0]
    private(set) var isMuted: Bool
    /// A clip leading into a fresh duel is playing; the count-up starts when it ends.
    private(set) var isStartingDuel = false

    @ObservationIgnored private let defaults: UserDefaults
    @ObservationIgnored private let sound: any SoundPlaying
    @ObservationIgnored private let animationDuration: Duration
    @ObservationIgnored private var animations: [Player: Task<Void, Never>] = [:]

    init(
        defaults: UserDefaults = .standard,
        sound: (any SoundPlaying)? = nil,
        animationDuration: Duration = .seconds(2)
    ) {
        self.defaults = defaults
        self.sound = sound ?? SoundManager()
        self.animationDuration = animationDuration
        isMuted = defaults.bool(forKey: StorageKey.isMuted)
        self.sound.isMuted = isMuted

        if defaults.object(forKey: StorageKey.lifePointsP1) != nil {
            for player in Player.allCases {
                let value = defaults.integer(forKey: StorageKey.lifePoints(player))
                lifePoints[player] = value
                displayedLifePoints[player] = value
            }
        } else {
            // First launch.
            start()
        }
    }

    func lifePoints(for player: Player) -> Int { lifePoints[player] ?? 0 }

    func displayedLifePoints(for player: Player) -> Int { displayedLifePoints[player] ?? 0 }

    /// "It's time to duel!" followed by a fresh duel.
    func start() {
        guard !isMuted else {
            restart()
            return
        }
        isStartingDuel = true
        sound.play(.itsTimeToDuel) { [weak self] in self?.restart() }
    }

    /// Resets both players to zero, then counts up to the starting life points.
    func restart() {
        for player in Player.allCases {
            animations[player]?.cancel()
            lifePoints[player] = 0
            displayedLifePoints[player] = 0
            // Saved up front so quitting during the clip still lands on a fresh duel.
            defaults.set(LifePoints.starting, forKey: StorageKey.lifePoints(player))
        }

        if isMuted {
            isStartingDuel = false
            for player in Player.allCases {
                changeLifePoints(to: LifePoints.starting, for: player, animated: false, force: true)
            }
        } else {
            isStartingDuel = true
            sound.play(.duelStart) { [weak self] in
                self?.isStartingDuel = false
                for player in Player.allCases {
                    self?.changeLifePoints(to: LifePoints.starting, for: player, force: true)
                }
            }
        }
    }

    func playItsTimeToDuel() {
        // Another voice clip would cancel the one that starts the duel.
        guard !isMuted, !isStartingDuel else { return }
        sound.play(.itsTimeToDuel)
    }

    func toggleMute() {
        isMuted.toggle()
        sound.isMuted = isMuted
        defaults.set(isMuted, forKey: StorageKey.isMuted)
    }

    func changeLifePoints(to value: Int, for player: Player, animated: Bool = true, force: Bool = false) {
        let target = value.clamped(to: LifePoints.range)
        guard force || target != lifePoints(for: player) else { return }
        setLifePoints(target, for: player)

        guard animated else {
            animations[player]?.cancel()
            displayedLifePoints[player] = target
            return
        }

        if !isMuted {
            sound.play(.lifePointsChange)
        }
        animateDisplayedLifePoints(of: player, to: target)
    }

    /// Waits for running count animations to finish. Used by tests.
    func finishAnimations() async {
        for task in animations.values {
            await task.value
        }
    }

    private func setLifePoints(_ value: Int, for player: Player) {
        lifePoints[player] = value
        defaults.set(value, forKey: StorageKey.lifePoints(player))
    }

    /// The classic linear "count" from the displayed value to the new total.
    private func animateDisplayedLifePoints(of player: Player, to target: Int) {
        animations[player]?.cancel()
        let start = displayedLifePoints(for: player)
        let duration = animationDuration
        animations[player] = Task { [weak self] in
            let clock = ContinuousClock()
            let startInstant = clock.now
            while true {
                let elapsed = startInstant.duration(to: clock.now)
                guard elapsed < duration else { break }
                let progress = elapsed / duration
                self?.displayedLifePoints[player] = start + Int(Double(target - start) * progress)
                do {
                    try await Task.sleep(for: .milliseconds(16))
                } catch {
                    return // Superseded by a newer change.
                }
            }
            self?.displayedLifePoints[player] = target
        }
    }
}
