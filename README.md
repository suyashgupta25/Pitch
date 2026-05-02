# Pitch

**Scroll-based startup pitch video discovery — a TikTok-style feed for founders and investors.**

A vertical, autoplay video feed of founder-uploaded pitches with swipe-to-like, swipe-to-pass, tap-to-save gestures, a saved shelf, and a profile carousel. Built as a take-home for AXIOO's founding mobile engineer role.

---

## Demo

![Axioo demo](demo/demo.gif)

## Architecture

**Clean Architecture, kept thin.** Three modules total — no per-feature splits at this scope. `:domain` is pure Kotlin (configured with `org.jetbrains.kotlin.jvm`, no Android library plugin), so the dependency graph enforces architectural direction at the build level. ViewModels call repositories directly when the call is a passthrough; `GetFeedPitchesUseCase` exists because it does real work — joining the feed with the user's local pass set and applying a trending sort that combines engagement and recency. That sort is product policy, not data plumbing.

State is sealed `UiState` per screen, intents are sealed `Intent` types where the screen has more than two write paths, and ViewModels expose `StateFlow<UiState>` collected with `collectAsStateWithLifecycle`. Coroutine dispatchers come from a single `DispatcherProvider` interface so tests don't have to fight `Dispatchers.IO`.

**Video playback** runs through a `PitchPlayerPool` that owns three ExoPlayer instances mapped to the previous, current, and next page in the pager. The pool reuses slots when the page window shifts so we never allocate a player on a per-page basis, which is the single biggest cause of feed-video stutter. On `Lifecycle.Event.ON_STOP` every player is released — hardware decoders are too expensive to keep when the app is backgrounded. The pool is rebuilt on the next entry; on a warm cache the rebuild is invisible.

## Tech stack

- **Language / build:** Kotlin 2.0.21, AGP 8.7.3, Kotlin DSL with version catalog
- **UI:** Jetpack Compose with Material 3, Compose plugin (Kotlin 2.0+)
- **Navigation:** Navigation Compose with type-safe `@Serializable` route classes
- **DI:** Hilt (KSP)
- **Async:** Kotlin Coroutines + Flow, `StateFlow` + sealed UI state
- **Video:** AndroidX Media3 / ExoPlayer (`PitchPlayerPool`)
- **Persistence:** DataStore Preferences (liked / saved id sets, user type)
- **Networking seam:** Retrofit + OkHttp + kotlinx.serialization, debug-only logging interceptor
- **Image / thumbnail loading:** Coil
- **Code quality:** detekt, ktlint, both wired into `./gradlew check`
- **Testing:** JUnit 4, Turbine, MockK, Truth, kotlinx-coroutines-test, Compose UI test

## What's intentionally not done

- **No backend.** Pitches are loaded from `app/src/main/assets/pitches.json`. The Retrofit seam is wired in `:data` (OkHttp client + converter + base URL `BuildConfig` field) but no service interface is declared — there's nothing to call. Swapping the mock source for a real backend is a one-module edit.
- **Video sources are public stock clips.** The JSON points at Google's `commondatastorage` sample MP4s (Big Buck Bunny, Sintel, etc.). They're landscape and play letterboxed inside the portrait card; in production these would be portrait pitch clips from a real CMS.
- **No auth.**
- **No analytics, crash reporting, or telemetry.** Out of scope for the prototype.
- **No Room.** Persistence is a `Set<String>` of liked ids, a `Set<String>` of saved ids, and a single `String` user type. Room would be ceremony.
- **Compose UI test is a smoke test only.** It verifies the card composes; gesture-level UI testing (swipe-to-like) is left as a follow-up because reliable swipe testing requires a real device or robust touch-injection setup.

## Trade-offs

- **DataStore over Room.** The persisted shape is two id sets and a string. Room would have meant a database, a DAO, an entity, mappers, and a migration plan to persist three primitives. With richer state — local engagement edits beyond a count delta, an offline draft of a pitch — I'd switch to Room.
- **One use case, not many.** `GetFeedPitchesUseCase` exists because it composes data and applies trending policy. Bookmarks and Profile call repositories directly. The brief explicitly warned against use cases for trivial passthroughs; this is the line.
- **Three modules, not per-feature.** Clean architecture across three modules gives architectural enforcement without the configuration-time cost of a per-feature module graph that doesn't pay back at this scope. With more than three screens or any kind of feature-flag gating I'd revisit.
- **Mixed dark/light surfaces.** This is a deliberate visual choice (videos on ink, browsing on cream) that makes the navigation graph slightly more complex (the surface mode is computed from the current route). The simpler choice would be one mode for the whole app — I think the editorial split is worth the small extra code.
- **Player pool of size 3.** Could be made configurable per-device based on hardware decoder capacity. For this prototype the constant is the right call; on lower-end devices we'd want telemetry-driven adjustment.

---

_Built with Compose, Hilt, Media3, and a four-color palette._
