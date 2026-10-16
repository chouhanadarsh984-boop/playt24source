# Testing checklist (end-to-end)

Backend
- [ ] `npm test` → 5 pass
- [ ] `/health` ok, `/health?deep=1` → channelAdmin true, canPost true

Upload flow
- [ ] Admin app → Login (email + password + 12-digit key)
- [ ] Matches → New/Edit → SELECT IMAGE → "Uploading..." → "✓ Uploaded"
- [ ] Telegram private channel में नई file आई (caption `#matches <imageId>`)
- [ ] Firebase `images/<imageId>` में telegramMessageId, telegramFileId, type, tournamentId, uploadedBy, createdAt
- [ ] Firebase `matches/<id>/image` = `tgimg:<imageId>`
- [ ] Existing match edit में image बदलते ही Firebase अपने आप update (SAVE से पहले)
- [ ] Player app में match banner दिखा (बिना refresh), दोबारा खोलने पर तुरंत (cache)
- [ ] Airplane mode में फिर भी cached images दिखीं

Errors (Upload failed + RETRY दिखना चाहिए)
- [ ] Internet बंद → "No internet connection."
- [ ] Backend बंद/गलत URL → "Cannot reach the backend..." / "Backend is not available..."
- [ ] Bot को channel से हटाओ → "The bot is not an admin of the Telegram channel..."
- [ ] 8 MB से बड़ी file → "Image is too large"
- [ ] गलत file (PDF/text rename करके) → "not a valid image"
- [ ] Logout करके/पुराना token → "Authentication failed"
- [ ] Staff/Admin के अलावा किसी player account से upload → 403 "not allowed"

Cleanup
- [ ] Image REMOVE/CHANGE (existing item) → पुरानी image Telegram से delete
- [ ] `grep -ri imgbb app admin` → कुछ नहीं
