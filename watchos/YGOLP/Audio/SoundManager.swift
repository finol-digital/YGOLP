import AVFoundation

enum Sound: String, CaseIterable, Sendable {
    case itsTimeToDuel = "its_time_to_duel"
    case duelStart = "duel_start"
    case lifePointsChange = "lifepoints_change"

    /// Voice clips are exclusive and report completion; effects are fire-and-forget.
    var isVoice: Bool { self != .lifePointsChange }
}

@MainActor
protocol SoundPlaying: AnyObject {
    var isMuted: Bool { get set }
    /// Plays `sound`. `completion` runs when a voice clip finishes, immediately for
    /// effects or when muted, and never if the clip is interrupted by another voice clip.
    func play(_ sound: Sound, completion: (@MainActor () -> Void)?)
}

extension SoundPlaying {
    func play(_ sound: Sound) { play(sound, completion: nil) }
}

@MainActor
final class SoundManager: NSObject, SoundPlaying {
    var isMuted = false

    private var players: [Sound: AVAudioPlayer] = [:]
    private var activeVoice: AVAudioPlayer?
    private var activeVoiceCompletion: (@MainActor () -> Void)?

    override init() {
        super.init()
        try? AVAudioSession.sharedInstance().setCategory(.ambient)
        player(for: .lifePointsChange)?.prepareToPlay()
    }

    func play(_ sound: Sound, completion: (@MainActor () -> Void)?) {
        guard !isMuted, let player = player(for: sound) else {
            completion?()
            return
        }

        guard sound.isVoice else {
            player.currentTime = 0
            player.play()
            completion?()
            return
        }

        // Stop any voice clip in progress; its completion is dropped.
        activeVoice?.stop()
        activeVoice = player
        activeVoiceCompletion = completion
        player.currentTime = 0
        if !player.play() {
            voiceDidFinish(ObjectIdentifier(player))
        }
    }

    @discardableResult
    private func player(for sound: Sound) -> AVAudioPlayer? {
        if let player = players[sound] { return player }
        guard let url = Bundle.main.url(forResource: sound.rawValue, withExtension: "mp3"),
              let player = try? AVAudioPlayer(contentsOf: url) else { return nil }
        player.delegate = self
        players[sound] = player
        return player
    }

    private func voiceDidFinish(_ id: ObjectIdentifier) {
        guard let activeVoice, ObjectIdentifier(activeVoice) == id else { return }
        let completion = activeVoiceCompletion
        self.activeVoice = nil
        activeVoiceCompletion = nil
        completion?()
    }
}

extension SoundManager: AVAudioPlayerDelegate {
    nonisolated func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool) {
        let id = ObjectIdentifier(player)
        Task { @MainActor in self.voiceDidFinish(id) }
    }

    nonisolated func audioPlayerDecodeErrorDidOccur(_ player: AVAudioPlayer, error: (any Error)?) {
        let id = ObjectIdentifier(player)
        Task { @MainActor in self.voiceDidFinish(id) }
    }
}
