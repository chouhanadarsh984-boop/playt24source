# M4 ESPORTS v1.11 — FAQ, Support Tickets and Admin Alerts

## Player app

- **Customer Support** now includes an FAQ, **Create support ticket**, and **My tickets**.
- Tickets collect a subject, category, and text description. Players can follow replies and status changes in the ticket conversation.
- A player can read only tickets stored under their own Firebase UID. Replies append to the conversation; a player reply reopens a resolved ticket. Closed tickets cannot receive player replies.
- Room ID/password guidance in the FAQ reflects the existing joined-player-only room access.
- The FAQ is currently built into the app in English and Hindi. Other interface languages use the app's translated ticket labels, while FAQ answers fall back to English.
- Admin push alerts are sent for a new player account, a pending manual deposit request, a withdrawal request, a new support ticket, and a new player reply on a ticket. Ticket message contents and payout UPI IDs are not included in lock-screen alert text.

## Admin app

1. Sign in to **M4 ESPORTS Admin v1.11** and allow Android notifications when prompted.
2. Open **Support tickets** to search and filter the inbox, view the conversation, respond, and set status to Open, In progress, Resolved, or Closed.
3. To delegate this work, go to **Team & Keys → Staff permissions** and enable **Manage customer support tickets** (`support_manage`) for the staff account. It is off by default for staff.
4. New tickets and player replies appear as push alerts for Admins and staff with `support_manage`; withdrawal/deposit alerts go only to staff with the corresponding finance permission. Ticket replies and status changes are written to the existing Admin activity log.

## Required Firebase Realtime Database rules update

The APK does not deploy Firebase security rules. Before using tickets with real accounts, publish the included [`database.rules.json`](database.rules.json) to the **same Realtime Database** configured in both apps:

1. Open Firebase Console → **Realtime Database → Rules** for the configured project.
2. Back up/review the currently active rules.
3. Replace them with the complete included `database.rules.json` and publish.
4. Sign in to the player app and Admin app, then test with two player accounts: each player should see only their own tickets, while an admin or a staff account with `support_manage` can see and reply to all tickets. The added `adminNotificationTokens/` rule lets only the signed-in Admin/staff account write its own device token.

The rules also retain the existing role checks and require the Admin app's active `panelLogin` for privileged writes. Do not publish a partial snippet over the full rule set. If the Firebase project is not the intended production project, do not publish these rules there.

## Branding

Both launcher icons use the supplied M4 ESPORTS logo. Player-directed Admin announcements remain data-only and are rendered by the Player app with the M4 logo and an optional selected photo. Admin event alerts are separate data-only FCM messages rendered by the Admin app with its monochrome status icon. The Player deposit screen now has one user-entered amount box, without preset package cards. Launcher labels remain **M4 ESPORTS** and **M4 ESPORTS Admin**; application IDs remain unchanged so v1.11 updates existing installations. See [`BRANDING_ASSET_SOURCE.md`](BRANDING_ASSET_SOURCE.md) for resource details and [`ADMIN_ALERTS_SETUP.md`](ADMIN_ALERTS_SETUP.md) for the FCM setup.
