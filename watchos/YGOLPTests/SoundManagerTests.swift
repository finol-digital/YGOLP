import Testing
@testable import YGOLP

@MainActor
struct SoundManagerTests {
    @Test func mutingFinishesTheActiveVoiceClip() {
        let manager = SoundManager()
        var finished = 0

        manager.play(.duelStart) { finished += 1 }
        manager.isMuted = true
        #expect(finished == 1)

        manager.isMuted = false
        manager.isMuted = true
        #expect(finished == 1)
    }

    @Test func mutedVoiceClipFinishesImmediately() {
        let manager = SoundManager()
        manager.isMuted = true
        var finished = false

        manager.play(.itsTimeToDuel) { finished = true }
        #expect(finished)
    }
}
