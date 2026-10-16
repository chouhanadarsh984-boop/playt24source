# Clash2 staff permissions and theme controls

## What changed

- Admins can open **Team & Keys → Permissions** for each staff member.
- New staff accounts start with **all permissions off**. Only an admin can add/remove staff, regenerate staff keys, or assign permissions.
- The admin home only shows modules allowed by the current staff grants. Routes, sensitive repository actions, Firebase rules, and image-backend endpoints also enforce access; hiding a button is not the only security check.
- Delete/export grants can open their corresponding lists without granting create/edit/review actions. Player actions such as balance changes, moderation, theme changes, and transaction history are separate grants; staff also need `players_view` to find a player in the panel.
- Staff permission changes are loaded at login. Ask the staff member to log out and back in after a grant changes so the interface refreshes.
- **Players → player → Change player theme** sets a per-player override. “Use global default” clears the override. **Settings → Default player theme** controls the fallback theme.
- Change the shared spinning logo in **Admin → Settings → Center loading spinner logo**. The new supplied spinner artwork is bundled in both v1.2 apps. A saved remote logo takes priority: upload the new artwork in this field to make it the shared remote logo, or remove the saved remote image to use the bundled artwork. A staff account needs both `settings_manage` and `media_upload`; admins already have access.
- The player language picker offers English, Hindi, Bengali, Urdu, Nepali, Tamil, and Arabic. The selected language is saved on that device; Arabic and Urdu use right-to-left layout. Navigation, account-menu, language, and support labels are translated.
- **Admin → Settings** now includes support WhatsApp/phone contacts and developer name, role, details, logo, email, Telegram, and contact link. The Customer Support page shows brand-colored contact buttons and a developer card below them. The developer link accepts a website, WhatsApp/Telegram URL, email address, or phone number.
- Use **Theme & colors** to select **Custom colors** as the global default and edit the app background, surfaces, fields, primary/buttons, match cards, customer-support accents, and success/warning/danger colors. Individual users can also be assigned **Custom colors**. Presets remain available.
- Use **Leaderboard** to set weekly, monthly, or full-time scores for existing ranked players. **Auto updates** default to on and add a player's awarded prize to all three periods when match results are declared. Turning this off only stops those ranking increments; results, winnings, and player statistics continue normally. Manual changes can be affected by later automatic awards. Weekly/monthly resets still require `ranking_reset`.
- Player match room ID/password is fetched only after the signed-in player has a joined slot. Firebase rules also protect `/rooms/{matchId}`; a non-joined player cannot read those credentials.

## Permission catalogue

Permissions are independent; use only the minimum grants needed.

| Area | Grant keys |
|---|---|
| Overview | `dashboard_view`, `admin_theme`, `activity_view` |
| Tournaments | `matches_manage`, `matches_delete`, `results_manage` |
| Content | `games_manage`, `games_delete`, `banners_manage`, `banners_delete`, `announcements_manage`, `announcements_delete`, `notifications_send`, `media_upload`, `media_delete` |
| Players | `players_view`, `players_export`, `balance_adjust`, `players_reward`, `players_moderate`, `players_theme`, `transactions_view` |
| Finance | `deposits_review`, `deposits_export`, `withdrawals_review`, `withdrawals_export` |
| Store | `products_manage`, `products_delete`, `orders_manage` |
| App controls | `settings_manage`, `app_control`, `leaderboard_manage`, `ranking_reset` |

## Required rollout steps

The project files and APKs do **not** deploy changes to Firebase or Railway automatically.

1. **Back up the live Realtime Database rules**, then review the included `database.rules.json` against the production schema. Publish/merge the intended rules in Firebase Console → Realtime Database → Rules. This revision adds the scoped `/leaderboardConfig` rule and `leaderboard_manage` access to `/leaderboard`; do not replace unrelated production rules without reviewing them.
2. Deploy the updated `backend/` service to Railway. `backend/lib.js` and `backend/server.js` now check separate `media_upload` and `media_delete` grants. Until the backend update is deployed, only the Android UI gates those image controls.
3. Install both current debug APKs and smoke-test player login, staff login, a minimum-grant staff account, image upload, and a joined/non-joined match's room details.
4. As the admin, grant staff access in **Team & Keys → Permissions**. Re-login on the staff device after changes.

The included Firebase rules default staff capabilities to explicit permissions, preserve an admin override, and separate match editing from result declaration. Test rules in a staging Firebase project before production publication.
