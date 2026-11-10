import Foundation
import Testing
@testable import YGOLP

@MainActor
final class FakeSound: SoundPlaying {
    var isMuted = false
    /// Leaves voice clips "playing" until `finishVoice()`. Like the real player,
    /// a later voice clip drops the pending completion.
    var holdsVoiceClips = false
    private(set) var played: [Sound] = []
    private var pendingCompletion: (@MainActor () -> Void)?

    func play(_ sound: Sound, completion: (@MainActor () -> Void)?) {
        if !isMuted { played.append(sound) }
        if holdsVoiceClips && !isMuted && sound.isVoice {
            pendingCompletion = completion
        } else {
            completion?()
        }
    }

    func finishVoice() {
        let completion = pendingCompletion
        pendingCompletion = nil
        completion?()
    }
}

@MainActor
struct DuelViewModelTests {
    let defaults = UserDefaults(suiteName: "YGOLPTests-\(UUID().uuidString)")!
    let sound = FakeSound()

    private func makeDuel() -> DuelViewModel {
        DuelViewModel(defaults: defaults, sound: sound, animationDuration: .zero)
    }

    private func save(p1: Int, p2: Int, muted: Bool = false) {
        defaults.set(p1, forKey: DuelViewModel.StorageKey.lifePointsP1)
        defaults.set(p2, forKey: DuelViewModel.StorageKey.lifePointsP2)
        defaults.set(muted, forKey: DuelViewModel.StorageKey.isMuted)
    }

    @Test func firstLaunchStartsDuel() async {
        let duel = makeDuel()
        await duel.finishAnimations()

        #expect(sound.played == [.itsTimeToDuel, .duelStart, .lifePointsChange, .lifePointsChange])
        for player in Player.allCases {
            #expect(duel.lifePoints(for: player) == LifePoints.starting)
            #expect(duel.displayedLifePoints(for: player) == LifePoints.starting)
        }
        #expect(defaults.integer(forKey: DuelViewModel.StorageKey.lifePointsP1) == LifePoints.starting)
        #expect(defaults.integer(forKey: DuelViewModel.StorageKey.lifePointsP2) == LifePoints.starting)
    }

    @Test func restoresSavedDuelSilently() {
        save(p1: 1234, p2: 5678, muted: true)
        let duel = makeDuel()

        #expect(sound.played.isEmpty)
        #expect(sound.isMuted)
        #expect(duel.isMuted)
        #expect(duel.lifePoints(for: .one) == 1234)
        #expect(duel.displayedLifePoints(for: .two) == 5678)
    }

    @Test func changeLifePointsClampsPersistsAndAnimates() async {
        save(p1: 8000, p2: 8000)
        let duel = makeDuel()

        duel.changeLifePoints(to: 120_000, for: .two)
        #expect(duel.lifePoints(for: .two) == LifePoints.maximum)
        #expect(defaults.integer(forKey: DuelViewModel.StorageKey.lifePointsP2) == LifePoints.maximum)
        await duel.finishAnimations()
        #expect(duel.displayedLifePoints(for: .two) == LifePoints.maximum)
        #expect(sound.played == [.lifePointsChange])

        duel.changeLifePoints(to: -50, for: .one)
        await duel.finishAnimations()
        #expect(duel.displayedLifePoints(for: .one) == LifePoints.minimum)
    }

    @Test func unchangedLifePointsAreIgnored() {
        save(p1: 8000, p2: 8000)
        let duel = makeDuel()

        duel.changeLifePoints(to: 8000, for: .one)
        #expect(sound.played.isEmpty)
    }

    @Test func mutedRestartSkipsSoundsAndAnimation() {
        save(p1: 0, p2: 300, muted: true)
        let duel = makeDuel()

        duel.restart()
        #expect(sound.played.isEmpty)
        for player in Player.allCases {
            #expect(duel.displayedLifePoints(for: player) == LifePoints.starting)
        }
    }

    @Test func bonusClipCannotInterruptDuelStart() async {
        save(p1: 300, p2: 300)
        sound.holdsVoiceClips = true
        let duel = makeDuel()

        duel.restart()
        #expect(duel.isStartingDuel)
        duel.playItsTimeToDuel()
        #expect(sound.played == [.duelStart])

        sound.finishVoice()
        await duel.finishAnimations()
        #expect(!duel.isStartingDuel)
        #expect(duel.displayedLifePoints(for: .one) == LifePoints.starting)
        #expect(duel.displayedLifePoints(for: .two) == LifePoints.starting)
    }

    @Test func restartSavesStartingLifePointsBeforeClipEnds() {
        save(p1: 300, p2: 500)
        sound.holdsVoiceClips = true
        let duel = makeDuel()

        duel.restart()
        #expect(duel.lifePoints(for: .one) == 0)
        #expect(defaults.integer(forKey: DuelViewModel.StorageKey.lifePointsP1) == LifePoints.starting)
        #expect(defaults.integer(forKey: DuelViewModel.StorageKey.lifePointsP2) == LifePoints.starting)
    }

    @Test func toggleMutePersists() {
        save(p1: 8000, p2: 8000)
        let duel = makeDuel()

        duel.toggleMute()
        #expect(duel.isMuted)
        #expect(sound.isMuted)
        #expect(defaults.bool(forKey: DuelViewModel.StorageKey.isMuted))

        duel.playItsTimeToDuel()
        #expect(sound.played.isEmpty)

        duel.toggleMute()
        duel.playItsTimeToDuel()
        #expect(sound.played == [.itsTimeToDuel])
    }
}
