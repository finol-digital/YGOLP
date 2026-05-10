## MODIFIED Requirements

### Requirement: SoundManager encapsulates all MediaPlayer lifecycle
The system SHALL provide a `SoundManager` class that uses `MediaPlayer` for sounds requiring completion callbacks (`duel_start`, `its_time_to_duel`) and `SoundPool` for fire-and-forget sounds (`lifepoints_change`). The `play(soundResId, onCompletion)` method SHALL invoke `onCompletion` only after the sound has actually finished playing when using `MediaPlayer`. No other class SHALL directly create or hold `MediaPlayer` references.

#### Scenario: Playing a sound with completion callback
- **WHEN** `SoundManager.play(R.raw.duel_start, onCompletion)` is called
- **THEN** the SoundManager SHALL play the sound via `MediaPlayer` and invoke `onCompletion` only after `MediaPlayer.OnCompletionListener` fires

#### Scenario: Playing a sound while it is already playing
- **WHEN** `SoundManager.play(soundId)` is called for a sound that is currently playing
- **THEN** the SoundManager SHALL stop and release the existing MediaPlayer, create a new one, and play it to prevent overlapping playback of the same sound

#### Scenario: Playing a fire-and-forget sound
- **WHEN** `SoundManager.play(R.raw.lifepoints_change)` is called with no `onCompletion`
- **THEN** the SoundManager SHALL play the sound via `SoundPool` and return immediately

#### Scenario: Playing a sound when muted
- **WHEN** `SoundManager.play(soundId)` is called and the mute flag is true
- **THEN** the SoundManager SHALL NOT create or play any MediaPlayer and SHALL invoke `onCompletion` immediately if provided

### Requirement: MediaPlayer resources are never leaked
The system SHALL ensure that every `MediaPlayer.create()` call has a corresponding `MediaPlayer.release()` call on all code paths, including error paths and rapid successive calls.

#### Scenario: Rapid restart calls
- **WHEN** the user triggers `restart()` twice in quick succession
- **THEN** the first MediaPlayer SHALL be released before the second is created, and no leaked players exist

## ADDED Requirements

### Requirement: SoundManager guards against use after release
The `SoundManager` SHALL track whether `releaseAll()` has been called. If `play()` is called after `releaseAll()`, it SHALL either silently no-op or re-initialize, and SHALL NOT crash or throw a NullPointerException.

#### Scenario: play() called after releaseAll()
- **WHEN** `releaseAll()` has been called and then `play(R.raw.lifepoints_change)` is invoked
- **THEN** the call SHALL not throw an exception and SHALL not play any sound

