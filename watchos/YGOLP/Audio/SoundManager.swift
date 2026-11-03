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
final class SoundManager: SoundPlaying {
    var isMuted = false {
        didSet {
            // Muting cuts off a clip in progress; its completion still runs.
            if isMuted, let activeVoice {
                activeVoice.player.stop()
                voiceDidFinish(activeVoice.generation)
            }
        }
    }

    private struct ActiveVoice {
        let player: AVAudioPlayer
        let delegate: VoiceDelegate
        let generation: Int
        let completion: (@MainActor () -> Void)?
    }

    private var players: [Sound: AVAudioPlayer] = [:]
    private var activeVoice: ActiveVoice?
    private var voiceGeneration = 0

    init() {
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
        activeVoice?.player.stop()

        // Players are cached, so a generation tells a replay of the same clip
        // apart from a finish callback still queued for the previous play.
        voiceGeneration += 1
        let generation = voiceGeneration
        let delegate = VoiceDelegate { [weak self] in
            Task { @MainActor in self?.voiceDidFinish(generation) }
        }
        player.delegate = delegate
        activeVoice = ActiveVoice(player: player, delegate: delegate, generation: generation, completion: completion)

        player.currentTime = 0
        if !player.play() {
            voiceDidFinish(generation)
        }
    }

    @discardableResult
    private func player(for sound: Sound) -> AVAudioPlayer? {
        if let player = players[sound] { return player }
        guard let url = Bundle.main.url(forResource: sound.rawValue, withExtension: "mp3"),
              let player = try? AVAudioPlayer(contentsOf: url) else { return nil }
        players[sound] = player
        return player
    }

    private func voiceDidFinish(_ generation: Int) {
        guard let voice = activeVoice, voice.generation == generation else { return }
        activeVoice = nil
        voice.completion?()
    }
}

private final class VoiceDelegate: NSObject, AVAudioPlayerDelegate {
    private let onFinish: @Sendable () -> Void

    init(onFinish: @escaping @Sendable () -> Void) {
        self.onFinish = onFinish
    }

    func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool) {
        onFinish()
    }

    func audioPlayerDecodeErrorDidOccur(_ player: AVAudioPlayer, error: (any Error)?) {
        onFinish()
    }
}
