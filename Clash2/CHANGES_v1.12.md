# v1.12 changes

**Removed**
- Admin app: Deposits screen/tile and deposit stats card, Store products, Store orders, related staff permissions.
- Player app: manual UPI deposit (UTR form), Store, My Order. ZapUPI online payment stays.
- Alerts for "new player" and "deposit request" (admin now gets: withdrawal request, new support ticket, ticket reply).

**New**
- Ban reason: Players -> BAN PLAYER asks for a reason (also on Edit user). Banned player sees a popup with the reason, CUSTOMER SUPPORT and LOG OUT.
- Admin popup: App control -> POPUP FOR PLAYERS -> SEND POPUP NOW. Shows once to each player, optional Customer Support button.
- App control: CURRENTLY LIVE summary; every field is pre-filled with the live value (first time: this build's version), edit and SAVE UPDATE.
- Tutorial video link: Settings -> "App tutorial video link". Player app shows a "coming soon" note when empty.

**Deploy order**
1. Publish `database.rules.json` (new `banReason` rule; `deposits`, `products`, `orders` closed to app writes).
2. Redeploy `backend/` on Railway (admin-event files).
3. Build + install both apps (versionCode 13 / 1.12). Then App control -> set Latest 13 / 1.12.
