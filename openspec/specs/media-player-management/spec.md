## ADDED Requirements

### Requirement: SoundManager encapsulates all MediaPlayer lifecycle
The system SHALL provide a `SoundManager` class that owns all `MediaPlayer` instances (duel start, life points change, "it's time to duel") and provides `play(soundId)` and `releaseAll()` methods. No other class SHALL directly create or hold `MediaPlayer` references.

#### Scenario: Playing a sound when no MediaPlayer exists yet
- **WHEN** `SoundManager.play(soundId)` is called for a sound that has not been played before
- **THEN** the SoundManager SHALL create a new `MediaPlayer` for that sound, play it, and set a completion listener that releases the MediaPlayer

#### Scenario: Playing a sound while it is already playing
- **WHEN** `SoundManager.play(soundId)` is called for a sound that is currently playing
- **THEN** the SoundManager SHALL stop and release the existing MediaPlayer, create a new one, and play it to prevent overlapping playback of the same sound

#### Scenario: Releasing all sounds on activity stop
- **WHEN** the Activity's `onStop` lifecycle event fires
- **THEN** the SoundManager SHALL release all active MediaPlayer instances and set their references to null

#### Scenario: Playing a sound when muted
- **WHEN** `SoundManager.play(soundId)` is called and the mute flag is true
- **THEN** the SoundManager SHALL NOT create or play any MediaPlayer

### Requirement: MediaPlayer resources are never leaked
The system SHALL ensure that every `MediaPlayer.create()` call has a corresponding `MediaPlayer.release()` call on all code paths, including error paths and rapid successive calls.

#### Scenario: Rapid restart calls
- **WHEN** the user triggers `restart()` twice in quick succession
- **THEN** the first MediaPlayer SHALL be released before the second is created, and no leaked players exist

