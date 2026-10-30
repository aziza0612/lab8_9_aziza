# PermissionsLab (Lab 8-9): Android Ruxsatnomalari Topshiriqlari Hibsi

Ushbu loyihada Android tizimida ruxsatnomalar (Permissions) bilan ishlashning barcha asosiy bosqichlari va holatlari Compose Material 3 arxitekturasi asosida to'liq amalga oshirilgan.

---

## Topshiriqlar bo'yicha hisobot:

### 1-topshiriq: Kotlin modeli — "Denied Twice" rad etish holatlari
- `PermissionModel` klassi yaratildi (`granted`, `rationale`, `dialogShows` xossalari bilan).
- `request(userAllows: Boolean)` funksiyasi orqali ketma-ket so'rovlar simulyatsiya qilindi:
  - **1-qadam (Rad etish):** `granted=false`, `rationale=true`, `dialogShows=true`. Rationale faqat 1-marta rad etilganda `true` bo'ladi.
  - **2-qadam (Rad etish):** `granted=false`, `rationale=false`, `dialogShows=false`. Tizim dialogi endi ko'rsatilmadi ("Permanently Denied").
  - **3-qadam (So'rov):** Dialog ko'rsatilmadi, konsolga `"dialog yo'q, darhol rad"` matni chiqarildi.
  - **4-qadam (Ruxsat):** Modelning holatlar o'tishi va unit-testlar (`PermissionModelTest`) orqali tekshirildi.

---

### 2-topshiriq: Loyiha va normal ruxsatnomalar
- `AndroidManifest.xml`ga `INTERNET` va `ACCESS_NETWORK_STATE` joylashtirildi.
- `ConnectivityManager` orqali real-vaqt rejimida tarmoq holati ("🟢 Online" / "🔴 Offline") ko'rsatildi.
- **Sharh (Nima uchun runtime dialog kerak emas?):**
  > *Normal ruxsatnomalar (INTERNET, ACCESS_NETWORK_STATE) ilova o'rnatilayotganda Android tizimi tomonidan avtomatik beriladi. Ular foydalanuvchining shaxsiy ma'lumotlariga yoki xavfsizlikka xavf tug'dirmagani uchun hech qanday runtime permission dialog talab qilinmaydi.*

---

### 3-topshiriq: Kamera ekrani (Uch holat)
- Manifestga `CAMERA` ruxsatnomasi qo'shildi.
- `CameraScreen` composable'ida ruxsatnomaning 3 ta holati boshqarildi:
  1. **Granted (Ruxsat berilgan):** Kamera tayyor va faol.
  2. **Rationale (Tushuntirish):** 1-marta rad etilganda tushuntirish va "Ruxsat so'rash" tugmasi ko'rsatiladi.
  3. **Denied Twice (Ikki marta rad etilgan):** Tugma o'rniga "Sozlamalarni ochish" (`Settings.ACTION_APPLICATION_DETAILS_SETTINGS`) tugmasi chiqariladi.
- `ON_RESUME` hodisasi orqali Sozlamalardan qaytganda ekran holati avtomatik yangilanadi.

---

### 4-topshiriq: Special ruxsatnoma — Overlay (`SYSTEM_ALERT_WINDOW`)
- Manifestga `SYSTEM_ALERT_WINDOW` e'lon qilindi.
- `OverlayScreen`da `Switch` joylashtirildi va u bosilganda `Settings.ACTION_MANAGE_OVERLAY_PERMISSION` ochiladi.
- `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)` orqali `Settings.canDrawOverlays(context)` har safar ekran aktivlashganda qayta o'qiladi va `Switch` holati avtomatik sinxronlanadi.

---

### 5-topshiriq: Bir martalik joylashuv ("Only this time")
- `ACCESS_FINE_LOCATION` va `ACCESS_COARSE_LOCATION` ruxsatnomalari `RequestMultiplePermissions` yordamida so'raladi.
- Natija `Map<String, Boolean>` sifatida o'qilib: `"aniq"` (FINE), `"taxminiy"` (COARSE) va `"yo'q"` holatlari aniqlanadi.
- **Bir martalik joylashuv hisoboti (README paragrafi):**
  > *"Faqat bu safar" (Only this time) ruxsatnomasi tanlanganda, Android OS ruxsatni faqat joriy ilova sessiyasi uchun beradi. Ilova fonga o'tkazilib, ma'lum vaqtdan keyin qaytilganda yoki sessiya tugaganda, bir martalik ruxsatnoma avtomatik ravishda bekor qilinadi. `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)` har safar ilovaga qaytilganda `checkSelfPermission` funksiyasini qayta chaqirganligi sababli, foydalanuvchi interfeysi (UI) darhol ruxsatning "yo'q" (bekor qilingan) holatiga o'tganini va qayta ruxsat so'rash tugmasi chiqqanini to'g'ri aks ettiradi.*

---

### 6-topshiriq: Bonus — Bildirishnoma ruxsatnomasi (`POST_NOTIFICATIONS`)
- API 33+ (Android 13+) uchun `POST_NOTIFICATIONS` runtime ruxsatnomasi so'raladi.
- Ruxsat berilganda `NotificationCompat.Builder` va `NotificationChannel` orqali test bildirishnomasi yuboriladi.
- **API 32 va API 34 taqqoslash hisoboti:**
  > **API 32 (Android 12L va undan past):** `POST_NOTIFICATIONS` ruxsatnomasi normal permission hisoblanadi va ilova o me'yorida o'rnatilishi bilan avtomatik beriladi. Runtime so'rov dialogi ko'rsatilmaydi, so'rov kodi o'tkazib yuboriladi va test bildirishnomasi darhol yuboriladi.
  > 
  > **API 34 (Android 14 va API 33+):** `POST_NOTIFICATIONS` o'ta muhim dangerous runtime permission turiga kiradi. Ilova bildirishnoma yuborishi uchun ekranda rasmiy tizim dialogi chiqadi. Foydalanuvchi "Allow" tugmasini bosmaguncha ilova bildirishnoma yubora olmaydi.

---

## Ishga tushirish va tekshirish:
1. Loyihani Android Studio'da oching va build qiling (`./gradlew assembleDebug`).
2. Unit-testlarni ishga tushirish uchun: `./gradlew test`.
3. Emulyatorda yoki real qurilmada ilovani ishga tushiring va har bir topshiriq sahifasiga Scrollable Tab-bar orqali o'tib tekshiring.
