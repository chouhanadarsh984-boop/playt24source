# Clash 2.0 - two Android apps + Firebase

* `app/`   = Player app  (package `com.clash.app`)
* `admin/` = Admin + Staff app (package `com.clash.admin`)

## 1. Firebase console (project paynal-18aa1)
1. Authentication -> Sign-in method -> enable Email/Password.
2. Realtime Database -> Rules -> paste `database.rules.json` -> Publish.
3. Project settings -> Add app -> Android, once for `com.clash.app` and once for `com.clash.admin`.
   Download google-services.json (it contains both apps) and copy the SAME file into `app/` and `admin/`.

## 2. Open in Android Studio
Open the `Clash2` folder, sync Gradle (JDK 17), run module `app` or `admin`.

## 3. Admin / Staff access (12-digit keys)
There are two different keys:
* **Signup key** - needed to create an account in the admin app. Default key is written in `database.rules.json`
  (search `adminSignups`). It is NOT inside the APK. After the first admin exists, generate a new one from Team & Keys
  (this turns the default off).
* **Login key** - a personal 12-digit key made automatically when an admin adds someone as staff/admin.
  Without it the person cannot enter the panel (Firebase rules enforce this, not just the app).

Steps:
1. Publish `database.rules.json`.
2. Admin app -> Create account -> use the default signup key. The FIRST account ever becomes admin automatically
   and gets its own login key on screen. Save it. Do this immediately, before anyone else knows the default key.
3. Admin app -> Team & Keys -> GENERATE NEW SIGNUP KEY (disables the default).
4. Staff/other admin: they create an account with the signup key, you open Team & Keys -> ADD AS STAFF / ADD AS ADMIN,
   a login key is generated, share it with them privately. They log in with email/username + password + that key.
5. NEW KEY logs the person out until they use the new key. REMOVE takes access away.
Use an email that is not already used by a player account for the first admin.
If you already made an admin by hand in the console earlier: set `system/bootstrapped` = true and add
`panelInvites/<uid>` = {role:"admin", key:"<12 digits>"} in the console, then log in with that key.

## 4. First setup in the admin app
* Settings: UPI ID, logo URL, **loading page image URL**, WhatsApp channel link, home card images, support details (address, email, Instagram, Telegram, time),
  tutorial YouTube link, share text, referral bonus, about/terms.
* Games: add tiles (section freefire / private).
* Banners: sections Important, Welcome strip, Earn tab, Onboarding slides.
  Banner link can be a web URL or an in-app link: `app:refer`, `app:addmoney`, `app:wallet`.
* Matches: create, set room ID/password (only joined players see it), then PLAYERS / RESULT -> DECLARE.

## 5. Notes
* Wallet writes run on the client. Rules limit it, but for real money move join / withdraw / result logic to Cloud Functions.
* Push notifications: the toggle subscribes the device to topic `all`. Send from Firebase console -> Messaging -> topic `all`.
* Google Sign-In button is UI only. My Order is a placeholder.

## 6. Photos (Telegram storage via Railway backend)
Images are no longer sent to ImgBB/Imgur. Admin app -> backend (`backend/`) -> Telegram private channel; Firebase stores `tgimg:<imageId>`.
Follow `backend/README.md` (bot, channel, Railway, variables), then set `BASE_URL` in
`app/.../BackendConfig.kt` and `admin/.../BackendConfig.kt` and rebuild both apps. Checklist: `backend/TESTING.md`.

## 7. Match ID, Store, Notifications, Google sign-in
* **Match ID**: every new match gets an 8-digit Match ID (editable). Players see it on the match card and details and can search by it.
  Admin app: search by Match ID in Matches; Players / Result shows everyone who joined that match.
* **Store / My Order**: Admin app -> Store products (add items with photo and price), Store orders (mark delivered, or cancel + refund).
  Players buy with wallet coins in Account -> Store; orders show in My Order.
* **Edit everything**: Games, Banners, Matches, Announcements (with photo), Store products, Settings (about, terms, support, tutorial, logo, loading image) and player name/balance are all editable in the admin app.
* **Notifications (push, with photo)**: Admin app -> Send Notification -> choose All players / Players of one match / One player,
  write title + message, add a photo, SEND. It can also post the same message in Announcements.
  One-time setup for push:
  1. Google Cloud console -> IAM -> Service accounts -> create one with role **Firebase Cloud Messaging API Admin** only.
  2. Keys -> Add key -> JSON, save the file on the admin phone.
  3. In Send Notification tap SELECT SERVICE ACCOUNT FILE and pick that file. It stays only on that phone.
  Players get the push even when the app is closed (they allow notifications once). Do not use an owner-level key.
* **Google sign-in** (player app): Firebase console -> Authentication -> Sign-in method -> enable Google. Project settings -> add your
  debug/release SHA-1 to the Android app, then re-download google-services.json into `app/`.

## 8. Themes, user management, faster images
* **Themes**: Admin app -> palette icon (top right of Home) -> 6 themes (Royal Purple, Midnight Black, Ocean Blue, Forest Green, Crimson, Clean Light). Saved on that phone.
* **Create user**: Admin app -> Create user. Enter name, username, Gmail, password (or GENERATE), pick Player / Staff / Admin. The account is made at once and
  you get a share box with the login details (staff/admin also get their 12-digit login key). You stay logged in.
* **Edit user**: Players -> tap player -> EDIT USER. Change name, username, phone (no extra key needed). Email and password need an **Auth key**:
  create a second service account with the role **Firebase Authentication Admin** only, download its JSON, pick it on the Edit user screen.
  Without that key use SEND PASSWORD RESET EMAIL (the player sets a new password from the mail).
* **Fast images**: loading image is saved on the phone after the first start and shows instantly next time; all banners, game tiles, logo and cards are
  downloaded in the background while the loading page shows, and kept in a 250 MB cache.

## 9. App control, edit-everything, dashboard, logs
* **App control** (admin app): Maintenance ON blocks every player with your message at once. App update: set Latest version code (popup with Later/Update),
  Minimum version code (older versions are blocked until they update), the update link and "what is new". To ship an update: raise `versionCode` in
  `app/build.gradle.kts`, build the APK/AAB, upload it, then set Latest (and Minimum) code here. Optionally tell players with Send Notification.
* **Edit user**: name, username, phone, email, password, plus deposited / winning / earnings / payouts / referral earnings / wins / kills / matches played,
  date of birth, gender, KYC and banned. Balance edits also create a line in the player's transactions. VIEW TRANSACTIONS shows the full history.
* **Dashboard**: players, banned, KYC, coins held by players, deposits and withdrawals (total and today), net, pending requests, matches, store orders.
* **Activity log**: who did what (approvals, payouts, results, balance edits, bans, team changes, app control).
* **More**: CLONE a match, Pending/Approved/All filters and CSV EXPORT for deposits, withdrawals and players.
