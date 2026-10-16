# ZapUPI Payment Gateway Setup — M4 ESPORTS

This integration adds ZapUPI wallet top-ups to the Player app while keeping the ZapUPI API key exclusively on the Node.js backend. Do **not** add the key to Kotlin, `google-services.json`, Firebase Realtime Database, Git, the APK, or this document.

## Payment flow and safety

1. The signed-in Player app sends the requested amount to the existing HTTPS backend with its Firebase ID token.
2. The backend validates the account, amount, and configured minimum, creates a local payment/deposit record, and calls ZapUPI `create-order` with the server-side key.
3. The Player opens ZapUPI's hosted checkout page. The API key is never returned to the phone.
4. The backend receives ZapUPI's webhook and **does not trust the webhook's claimed status or amount**. It calls ZapUPI `order-status` from the server, checks the order and exact INR amount, and only then credits the wallet.
5. The Player can request a status refresh from the app. The same server-side verification runs; webhook repeats and status polling cannot credit one payment twice.
6. A confirmed payment appears as an approved ZapUPI deposit in Admin and as a successful wallet transaction. Existing manual UPI deposits remain available as a fallback.

ZapUPI's docs do not describe a webhook signature. Therefore, the webhook is treated only as a notification to query the authenticated server-to-server order-status endpoint. Never credit funds directly from webhook JSON, a redirect, or the phone.

ZapUPI's create-order example uses a numeric `order_id`. The backend now sends a unique numeric provider ID and keeps the separate `M4ZAP-…` ID only for the private Firebase record and ownership checks; the mapping is retained for status polling and webhooks.

## Player-entered deposit amount (v1.11)

In Admin → **App settings**, set **ZapUPI deposits** to Enabled and optionally set **Default ZapUPI order remark** for the gateway. The Player deposit screen now shows only a single blank amount box and a Pay button; fixed package cards, preset options, and Player-facing descriptions have been removed. The player can enter any valid amount within the Admin-configured minimum and Railway server-side maximum. Old `zapupiPackages` and `zapupiAmountOptions` values left in Firebase are ignored by the new backend.

ZapUPI generates a **unique hosted payment link for each player/order** when the Player taps Pay. Do not paste a reusable/shared link: a unique order is needed to verify the exact payer amount and credit the correct wallet. The Player opens the returned ZapUPI link and asks the backend to verify payment when returning to the app; the webhook can also reconcile it. Wallet credit happens only after a matching server-side success check.

## Railway environment variables

Set these in the **backend service's Railway Variables**. Do not commit real values or paste the API key into Android/admin settings.

| Variable | Value |
|---|---|
| `ZAPUPI_API_KEY` | ZapUPI dashboard API key (server-only secret) |
| `ZAPUPI_WEBHOOK_URL` | `https://turnament-1-storage-production.up.railway.app/api/payments/zapupi/webhook` (use the actual public HTTPS backend domain) |
| `ZAPUPI_MAX_AMOUNT_INR` | Optional per-order ceiling in rupees; defaults to `100000` |
| `ZAPUPI_ALLOW_TEST_PAYMENTS` | Leave unset/`false` in production. Only set to `true` on a separate staging backend when testing with ZapUPI's test environment. |

The existing backend still needs its normal Railway settings (`BOT_TOKEN`, `CHANNEL_ID`, `FIREBASE_CONFIG`, and optional `FIREBASE_DATABASE_URL`). Do not replace or expose those secrets.

## Diagnose “The payment provider could not process this request”

Keep `ZAPUPI_API_KEY` only in Railway Variables. The backend now trims accidental surrounding whitespace/quotes and writes a **sanitized diagnostic** to Railway deploy logs when ZapUPI rejects a request; it never logs the request body or API key. After deploying this backend update, create a payment link without completing payment, then open Railway → service → **Deploy** → logs and look for `[ZapUPI] provider rejected request`. The log includes only the endpoint, HTTP status, provider status/code and a bounded/sanitized provider message. Use that message to correct the key, merchant activation or request issue in ZapUPI/Railway; do not paste secrets into chat. If the log says request failed or non-JSON, it also includes only the endpoint and status/type needed to diagnose connectivity or an upstream response.

### ZapUPI dashboard

- Configure the merchant's global webhook only if desired; each order also sends `ZAPUPI_WEBHOOK_URL` to ZapUPI as its per-order webhook URL.
- Keep the backend URL HTTPS and publicly reachable.
- The backend uses `POST https://pay.zapupi.com/api/create-order` and `POST https://pay.zapupi.com/api/order-status`, following the [ZapUPI API documentation](https://zapupi.com/docs).

## Deploy and install order

1. Deploy the updated `backend/` code to the Railway service currently used by the app.
2. Add the environment variables above and wait for the backend health check to return `{"ok":true}`.
3. Build/install Player v1.11 (and Admin v1.11 if distributing the aligned build). The APKs contain only the public backend base URL, never the ZapUPI key.
4. In staging, test a pending order, a confirmed success, a failed order, and repeated webhook/status calls. Confirm that only verified non-test payments credit once. Never use a production payment to test reconciliation.
5. In production, verify successful deposits in Admin's Deposits screen and Player's Transactions/Wallet.

## Endpoints added

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/payments/zapupi/create-order` | Authenticated Player creates a payment order; request body `{ "amount": 100 }` |
| `POST` | `/api/payments/zapupi/status` | Authenticated owner checks `{ "orderId": "M4ZAP-…" }`; backend queries ZapUPI status |
| `POST` | `/api/payments/zapupi/webhook` | ZapUPI callback; the callback only triggers server-side status verification |

Amounts are accepted to paise precision, the backend enforces the configured Admin minimum and a server-side maximum, and test-environment payments are not credited by default.

## Credential hygiene

Because the API key was shared in the task conversation, rotate it in ZapUPI before enabling production payments, then put the replacement only in Railway's secret variable `ZAPUPI_API_KEY`. Do not put it in chat again or in any source file. If payment verification encounters an amount mismatch or provider outage, no automatic wallet credit is made; check the provider dashboard and backend logs, then reconcile manually.
