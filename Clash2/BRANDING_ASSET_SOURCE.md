# M4 ESPORTS logo asset

- **Source image:** https://i.ibb.co/HLGpjzQL/IMG-20261006-WA0011.webp (provided by the app owner)
- **Bundled resource:** `app/src/main/res/drawable-nodpi/m4_esports_logo.webp` and the matching Admin resource.
- The launcher assets use the supplied artwork in legacy density sizes and an adaptive icon foreground sized to the safe area; the original artwork is retained in the APK.
- Android notification headers use the monochrome `ic_notification_logo.png` app mark. Admin pushes are data-only, so the Player app renders them locally in foreground/background with the full-color logo as the large app icon. Admin-sent notifications do **not** attach a full-width image unless Admin selects an optional photo; when selected, that photo appears as the expanded notification image.
