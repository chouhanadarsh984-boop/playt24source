# M4 ESPORTS Admin alerts — v1.11

Is update mein Admin app ko push notifications milengi:

- Naya Player account bana.
- Player ne manual UPI deposit request bheji.
- Player ne withdrawal request bheji (ye request hai; payout complete hone ka confirmation nahi).
- Naya support ticket bana.
- Player ne support ticket par reply bheja.

Notification tap karne par sambandhit Admin screen khulegi. Lock-screen text mein ticket ka message ya UPI payout ID nahi bheji jaati. Admin ko sab alerts milte hain; staff ko sirf us permission ke alerts milte hain jo unke kaam se match karte hain (`withdrawals_review`/`withdrawals_export`, `deposits_review`/`deposits_export`, `support_manage`, ya Player-list permissions).

## 1. Firebase Realtime Database rules publish karein

APK ya Railway deploy se pehle project ki poori rules file update karein:

1. Firebase Console → **Realtime Database → Rules** kholen.
2. Maujooda rules ka backup lein.
3. Project ZIP/source mein diya poora [`database.rules.json`](database.rules.json) publish karein—sirf naya snippet nahi.
4. Is file mein `adminNotificationTokens/` rule hai. Ye signed-in Admin/staff ko sirf apne device ka token likhne/dekhne deta hai.

## 2. Firebase Cloud Messaging enable karein

1. Firebase Console → **Project settings → Cloud Messaging** mein **FCM HTTP v1 API** enabled hona chahiye.
2. Google Cloud Console → **IAM & Admin → IAM** mein us service account ko dhoondhein jo Railway ke `FIREBASE_CONFIG` credential ka `client_email` hai. Email/key ko chat ya GitHub mein share na karein.
3. Us account ko **Firebase Cloud Messaging API Admin** role dein (`cloudmessaging.messages.create` ke liye) aur uski existing Realtime Database access role ko bana rehne dein. Sirf FCM ke liye broad **Project Editor** dene ki zaroorat nahi.

## 3. Backend Railway par update karein

Updated files `backend/admin-events.js`, `backend/admin-event-core.js`, `backend/server.js` aur `backend/lib.js` hain. Backend key/code mein hardcode nahi hoti. Railway mein wahi existing Firebase credentials aur backend environment variables rakhein.

GitHub Web upload mein ZIP ko ek file ki tarah mat daalein:

1. Backend ZIP ko phone/computer ke file manager se **extract** karein.
2. Agar repo sirf backend hai, extracted `backend/` ke andar ke files repo ke root mein merge karein; duplicate `backend/backend` folder na banayein.
3. Agar repo poora Android project hai, naye source ke `backend/` files project ke `backend/` mein merge karein, `database.rules.json` bhi repo mein commit karein, aur Railway ka **Root Directory** `/backend` rakhein.
4. GitHub changes commit/push karein. Railway deploy complete hone ke baad `https://<backend>/health` par `{"ok":true}` verify karein.

## 4. APK install aur notification permission

1. Player **v1.11** aur Admin **v1.11** install karein. Existing package IDs wahi hain, isliye v1.8/v1.10 app ke upar update install honi chahiye.
2. Admin app kholkar Admin/staff account se login karein. Android permission aaye to **Allow notifications** dabayein. Login par device FCM token khud register hota hai.
3. Staff ko Team & Keys → Staff permissions mein sambandhit permission dein. Push sirf panel mein logged-in Admin/staff devices ko bheji jaati hai.

## 5. Test checklist

Test accounts se bina real payment kiye:

- Ek test Player account signup karke **New player registered** alert dekhein.
- Test account se manual deposit request, withdrawal request, support ticket aur support reply bhejkar alag alerts verify karein.
- Alert par tap karke Deposits, Withdrawals ya Support tickets page khulna check karein.
- Admin notification permission deny/disable ho to Android Settings mein **M4 ESPORTS Admin → Notifications** enable karein.
- Staff alert missing ho to staff permission, active Admin-panel session, Firebase `adminNotificationTokens/<uid>/` record, Railway deploy, FCM API enablement/IAM role check karein.

Player requests Firebase mein pehle save hoti hain; notification service temporary unavailable ho to deposit/withdraw/ticket submission ko fail nahi karti. Alert na aaye to request Admin queue mein phir bhi rahegi. User ka UPI ID, password, API key ya Firebase service-account JSON logs/chat mein paste na karein.

## Official Google references

- [Firebase Admin SDK: send messages](https://firebase.google.com/docs/cloud-messaging/send/admin-sdk)
- [Firebase Cloud Messaging IAM roles and permissions](https://docs.cloud.google.com/iam/docs/roles-permissions/firebasecloudmessaging)
