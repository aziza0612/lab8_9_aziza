# Android Permissions (Ruxsatnomalar) va Jetpack Compose: Kompleks Qo'llanma

---

## 1-savol: Ruxsatnomalar bilan ishlash jarayoni (Workflow) va mantiqiy yondashuv

### 1. Ma'ruzadagi 4 ta qaror (Workflow)
Android-da foydalanuvchidan ruxsat so'rashdan oldin dasturchi quyidagi ketma-ketlikda 4 ta qarorni qabul qilishi kerak:

1. **Ruxsatnoma chindan ham kerakmi yoki muqobil yechim bormi?** (*Is a permission needed, or is there an alternative?*)
2. **So'ralayotgan ruxsatnoma qaysi toifaga kiradi?** (*Install-time, Runtime yoki Special?*)
3. **Ruxsatnoma ma'lum xususiyat uchun zarurmi (optional) yoki ilovaning asosiy ish faoliyati (core function) uchunmi?**
4. **Foydalanuvchiga tushuntirish (Rationale UI) va rad etish (deny) holatlarini qanday boshqaramiz?**

### 2. Ruxsatnomasiz bajariladigan holatga misol
Foydalanuvchi profiliga rasm yuklashi yoki hujjatni skaner qilishi kerak bo'lsa, ilova ichida `CAMERA` ruxsatini so me'yorlashtirib so'ramasdan, tizimning **`ActivityResultContracts.TakePicture()`** yoki **`PickVisualMedia`** (Photo Picker) kabi tayyor tizim Intenti (System Intent) orqali kamera yoki galereyani ochish mumkin.

### 3. Nega bu eng yaxshi variant?
* **Xavfsizlik va Maxfiylik:** Ilovamiz foydalanuvchining kamerasiga yoki butun xotirasiga doimiy va to'liq kirish huquqini olmaydi. Faqat foydalanuvchi tanlagan/suratga olgan fayl ilovaga qaytariladi.
* **UX va Foydalanuvchi Ishonchi:** Foydalanuvchiga ortiqcha qo'rqinchli dialoglar ko'rsatilmadi, ruxsat so'rovlari va rad etish muammolari bilan ishlash zaruriyati yo'qoladi.

---

## 2-savol: Ruxsatnomalar toifalari va kod hajmiga ta'siri

Android’da ruxsatnomalar 3 ta asosiy toifaga bo'linadi:

1. **Install-time Permissions (Normal va Signature):**
   * **Qanday beriladi:** Ilova yuklab olinib o'rnatilayotganda tizim (Google Play) tomonidan avtomatishtirilgan holda avtomatik beriladi. Foydalanuvchidan ish vaqtida hech qanday dialog so'ralmaydi.
2. **Runtime Permissions (Dangerous):**
   * **Qanday beriladi:** Ilova ishlayotgan vaqtda (runtime) dialog oynasi orqali foydalanuvchi o'zi qo'lda tasdiqlaydi (masalan, Camera, Location, Contacts).
3. **Special Permissions:**
   * **Qanday beriladi:** Oddiy dialog bilan berilmaydi. Foydalanuvchini maxsus Android Tizim Sozlamalari (System Settings) ekraniga yo'naltirib, u yerdagi birorta switch/toggle'ni yoqishi orqali beriladi (masalan, System Alert Window, Write Settings).

### Nega toifa kod hajmini belgilaydi?
* **Install-time** uchun **0 qator kod** (faqat `AndroidManifest.xml` ichida `<uses-permission>` yoziladi).
* **Runtime** uchun o'rta hajmdagi kod (`AndroidManifest` + holatni tekshirish `checkSelfPermission` + Rationale UI ko'rsatish + dialog launch qilish + rad etilgan holatni boshqarish).
* **Special** uchun eng katta va murakkab kod (`AndroidManifest` + `Settings.canDrawOverlays` tekshiruvi + Intent bilan Sozlamalarga yo'naltirish + `ON_RESUME` lifecycle tinglovchisi orqali qaytganda holatni qayta yangilash).

---

## 3-savol: Normal ruxsatnomalar va Runtime tekshiruvlar

### 1. Nega runtime tekshiruv va dialog kerak emas?
`INTERNET`, `ACCESS_NETWORK_STATE`, `VIBRATE`, `BLUETOOTH` kabi ruxsatnomalar **Normal Permissions** toifasiga kiradi. Ular foydalanuvchining maxfiy ma'lumotlariga yoki boshqa ilovalarga xavf tug'dirmaydi. Shuning uchun Google Play va Android OS ilova o'rnatilishi bilan ularga avtomatik ravishda tasdiq beradi. Runtime dialog chiqarish mantiqsiz va ortiqcha.

### 2. Manifestdagi qator unutilsa nima bo'ladi?
Agar Manifest'da `<uses-permission android:name="android.permission.INTERNET" />` unutilsa, tarmoq so'rovi yuborilganda tizim **`java.lang.SecurityException: Permission denied (missing INTERNET permission)`** xatoligini (Crash) otadi.

### 3. Normal ruxsatnomalarga 3 ta misol:
1. `android.permission.INTERNET`
2. `android.permission.ACCESS_NETWORK_STATE`
3. `android.permission.VIBRATE`

---

## 4-savol: Signature ruxsatnomalari va `protectionLevel`

* **Signature ruxsatnomasi kimga beriladi:** Faqat va faqat ruxsatnomani e'lon qilgan ilova bilan **bir xil raqamli kalit (Signing Certificate / Key store)** bilan imzolangan ilovalargagina beriladi.
* **protectionLevel:** Manifest’da e'lon qilishda `android:protectionLevel="signature"` deb yoziladi.

### Hamyon va Begona ilova uchun natija:
* **Hamyon ilovasi:** Agar "Bank" va "Hamyon" ilovalari bir xil developer kaliti bilan imzolangan bo'lsa, Hamyon ilovasi o'rnatilishi bilanoq `READ_ACCOUNTS` ruxsatnomasi unga **avtomatik ravishda beriladi**. Foydalanuvchiga **hech qanday dialog ko'rsatilmaydi**.
* **Uchinchi tomon (Begona) ilovasi:** Boshqa kalit bilan imzolangan bo'lsa, tizim ruxsatnomani berishni **rad etadi** (Permission Denied). Uchinchi tomon ilovasiga ham **hech qanday dialog ko'rsatilmaydi** va u bank xizmatiga ulanolmaydi.

---

## 5-savol: Install-time ruxsatnomalarning berilish tartibi

Install-time ruxsatnomalar foydalanuvchiga ilova Google Play Store'dan o'rnatilayotgan paytda **Store listing sahifasida ("App permissions" bo'limida)** ma'lumot sifatida ko'rsatiladi. Tizim ularni ilova **o'rnatilishi bilanoq (install time)** avtomatik ravishda tasdiqlaydi va faol qiladi.

### Bu toifaga kiruvchi ikki turdagi ruxsatnomalar:
1. **Normal permissions** (masalan, `INTERNET`, `VIBRATE`)
2. **Signature permissions** (masalan, kompaniyaning o'z ilovalari o'rtasidagi maxsus ruxsatlar)

---

## 6-savol: Android versiyalaridagi farq va Runtime model

### Farq va sabab:
Runtime permissions tizimi **Android 6.0 (API level 23 - Marshmallow)** versiyasida joriy qilingan.
* **Android 5.1 (API 22) va undan past:** Barcha dangerous ruxsatnomalar (shu jumladan `CAMERA`) **Install-time** shaklida ishlaydi. Foydalanuvchi ilovani Play Store'dan yuklab olayotganida "Ushbu ilova kamerasiga kiradi, rozi bo'lasizmi?" degan ro'yxatni ko'radi va yuklab olish tugmasini bossa, ruxsat noma'lum muddatga beriladi. Shuning uchun API 22 da runtime dialog chiqmaydi.
* **Android 14 (API 34):** API 23 dan yuqori bo'lgani uchun va runtime ruxsatnoma bo'lgani uchun, ilova kamerani ishlatishdan oldin dialog chiqarishi shart.

### Ma'ruzadagi `granted(kind, sdk)` modeli:
Ma'ruzadagi mantiqiy model quyidagicha ishlaydi:
$$\text{granted}(\text{Dangerous}, \text{sdk}) = \begin{cases} \text{Avtomatik beriladi (Install-time)}, & \text{agar } \text{sdk} < 23 \\ \text{Foydalanuvchi dialogidan so'raladi (Runtime)}, & \text{agar } \text{sdk} \ge 23 \end{cases}$$

---

## 7-savol: Runtime ruxsatnoma holatining ketma-ketligi

### Holatlar ketma-ketligi va `shouldShowRationale` mantiqiy holati:

1. **Hali so'ralmagan (Initial State):**
   * `isGranted = false`, `shouldShowRationale = false`.
   * **Kod harakati:** Foydalanuvchiga ruxsat so'rovchi tugma ko'rsatiladi, bosilganda `launchPermissionRequest()` chaqiriladi.
2. **Bir marta rad etilgan (Denied Once):**
   * `isGranted = false`, `shouldShowRationale = true`.
   * **Kod harakati:** Nega bu ruxsatnoma ilovaga zarurligini tushuntiruvchi **Rationale UI** (dialog yoki card) ko'rsatiladi ("Kameradan profil rasmini olish uchun foydalanamiz"). Foydalanuvchi tushunib, "Tushundim" desa, qayta `launchPermissionRequest()` chaqiriladi.
3. **Ikki marta rad etilgan / "Don't ask again" (Permanently Denied):**
   * `isGranted = false`, `shouldShowRationale = false` (lekin ilgarilari so'ralgan).
   * **Kod harakati:** Tizim dialog chiqarishni to'xtatadi. Foydalanuvchiga Sozlamalar menyusiga o'tib ruxsatni qo'lda yoqish haqida xabar va **"Sozlamalarga o'tish" (Open Settings)** tugmasi ko'rsatiladi.

### Foydalanuvchini yo'naltirish:
Ikki marta rad etilgandan so'ng foydalanuvchini `Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)` orqali ilovaning Sozlamalar sahifasiga yo'naltirish kerak.

---

## 8-savol: Accompanist Permissions kutubxonasi va tarmoqlar

### Uchta tarmoq mantiqi va ekrandagi tasvirlar:

1. **`permissionState.status.isGranted` (True):**
   * **Qachon ishlaydi:** Foydalanuvchi ruxsat bergan bo'lsa.
   * **Ekranda:** Kamerasiz/Ruxsatsiz ishlaydigan asosiy Kontent (masalan, Kamera Vizori / Camera Preview) ko'rsatiladi.
2. **`permissionState.status.shouldShowRationale` (True):**
   * **Qachon ishlaydi:** Foydalanuvchi ruxsatni kamida bir marta rad etgan bo'lsa.
   * **Ekranda:** Rationale matni va Ruxsat so'rash tugmasi ("Kamera ilovamiz uchun juda zarur, sababi...") ko'rsatiladi.
3. **Qolgan holat (`Else` / Hali so'ralmagan yoki Permanently Denied):**
   * **Qachon ishlaydi:** Ruxsat hali umuman so'ralmagan bo'lsa yoki 2 marta rad etilib blocked bo'lsa.
   * **Ekranda:** Ruxsat berish tugmasi ("Kameraga ruxsat berish") yoki Sozlamalarga o'tish xabari chiqariladi.

### Nega `rememberPermissionState` recomposition'dan omon qoladi?
U `remember` va Activity Result API mexanizmlaridan foydalanib, Android Lifecycle hamda recomposition vaqtida obyekt holatini va uning tinglovchilarini saqlab qoladi.

### `ExperimentalPermissionsApi` nimani anglatadi?
Ushbu API hali to'liq barqaror (stable) emasligini, kelajakdagi kutubxona versiyalarida o'zgarishi yoki boshqa struktura bilan almashtirilishi mumkinligini anglatuvchi opt-in annotatsiyasidir.

---

## 9-savol: Kutubxonasiz (Native Jetpack Compose) ruxsat so'rovlari

Qo'shimcha kutubxonasiz Jetpack Compose'da native yechim quyidagicha yoziladi:

```kotlin
val context = LocalContext.current
val activity = context as? Activity

// 1. Activity Result Launcher yaratiladi
val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        // Ruxsat berildi
    } else {
        // Rad etildi
    }
}

// 2. Joriy holatni o'qish
val hasPermission = ContextCompat.checkSelfPermission(
    context,
    Manifest.permission.CAMERA
) == PackageManager.PERMISSION_GRANTED

// 3. Rationale holatini aniqlash
val showRationale = activity?.let {
    ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
} ?: false
```

### Accompanist bilan solishtirish:
Native yondashuvda `checkSelfPermission` va `shouldShowRequestPermissionRationale` holatlarini har bir recomposition va lifecycle holatida o'zingiz qo'lda boshqarishingiz shart. Accompanist kutubxonasi esa bu 3 ta holatni bitta reactive `PermissionState` obyektiga birlashtirib beradi va Compose kodini sezilarli darsda qisqartiradi.

---

## 10-savol: `RequestMultiplePermissions` va Joylashuv ruxsatnomalari

### Natija shakli va o'qilishi:
`RequestMultiplePermissions` kontrakti natijani `Map<String, Boolean>` ko'rinishida qaytaradi. Kalit — ruxsatnoma nomi, qiymat — `true` (berildi) yoki `false` (rad etildi).

```kotlin
val launcher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
) { permissions ->
    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
}
```

### Nega bitta interfeys ko'radi?
Android 12 (API 31) dan boshlab foydalanuvchilarning shaxsiy xavfsizligini ta'minlash uchun `ACCESS_FINE_LOCATION` va `ACCESS_COARSE_LOCATION` bitta tizim dialogida birga ko'rsatiladi. Foydalanuvchiga **"Precise"** (Aniq) yoki **"Approximate"** (Taxminiy) tanlov huquqi beriladi.

### Foydalanuvchi ikkalasini ham rad etsa:
Ilova joylashuvga bog'liq bo'lmagan cheklangan funksionallik rejimiga o'tishi kerak. Joylashuvsiz ishlay olmaydigan bo'lsa, Rationale ko me'yor ko'rsatilib, foydalanuvchiga taxminiy bo'lsa ham joylashuv berish afzalliklari tushuntirilishi shart.

---

## 11-savol: Special Permissions va Jadval

### Special ruxsatnomalarning farqi:
Special ruxsatnomalar foydalanuvchi va tizim uchun juda yuqori xavf tug'dirishi mumkin bo'lgan operatsiyalardir. Shuning uchun tizim ularni standart runtime dialogi orqali so'rashga ruxsat bermaydi. Foydalanuvchi atayin Android Tizim Sozlamalarining maxsus menyusiga o'tib ruxsat berishi kerak.

### Jadval:

| Ruxsatnoma | Nimaga ruxsat beradi | Tekshirish chaqiruvi |
| :--- | :--- | :--- |
| **SYSTEM_ALERT_WINDOW** | Boshqa ilovalar va ekranlar ustidan oynalar chiqarish (Overlay) | `Settings.canDrawOverlays(context)` |
| **WRITE_SETTINGS** | Tizim sozlamalarini (masalan, ekran yorqinligi, qo'ng'iroq ovozi) o'zgartirish | `Settings.System.canWrite(context)` |
| **MANAGE_EXTERNAL_STORAGE** | Qurilmadagi barcha fayllarni to'liq o'qish va tahrirlash (All files access) | `Environment.isExternalStorageManager()` |
| **REQUEST_INSTALL_PACKAGES** | APK fayllarni ilova ichidan o'rnatishni boshlash | `context.packageManager.canRequestPackageInstalls()` |
| **ACCESS_BACKGROUND_LOCATION** | Ilova yopiq yoki fonda bo'lganda ham joylashuvni olish | `ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)` (Alohida bosqichda so'raladi) |

---

## 12-savol: Special Permission va `ON_RESUME` yechimi

### "Tekshir $\rightarrow$ Sozlamalarni och $\rightarrow$ Qaytganda yana tekshir" andozasi va Callback yo'qligi sababi:
Foydalanuvchi Tizim Sozlamalari oynasiga `startActivity(Intent(...))` orqali yo'naltiriladi. Tizim Sozlamalari ilovasi boshqa tashqi protsess bo'lgani uchun, u yerdagi toggle o'zgarganda ilovamizga hech qanday avtomatik callback qaytmaydi. Foydalanuvchi **"Orqaga"** tugmasini bosib ilovaga qaytganida, Activity `ON_RESUME` holatiga o'tadi.

### Tuzatilgan `OverlayScreen.kt` kodi:

```kotlin
@Composable
fun OverlayScreen() {
    val context = LocalContext.current
    var canDraw by remember {
        mutableStateOf(Settings.canDrawOverlays(context))
    }

    // Foydalanuvchi Sozlamalardan qaytganda (ON_RESUME) holatni qayta tekshiramiz
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        canDraw = Settings.canDrawOverlays(context)
    }

    Switch(
        checked = canDraw,
        onCheckedChange = {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            context.startActivity(intent)
        }
    )
}
```

---

## 13-savol: Fon joylashuvi (Background Location) va Ikki bosqichli tartib

### Ikki bosqichli tartib:
Android 10 (API 29) va undan yuqori versiyalarda **Foreground** va **Background** location bir vaqtning o'zida birga so'ralishiga Android OS ruxsat bermaydi.

1. **1-bosqich:** Avval faqat Foreground location (`ACCESS_FINE_LOCATION` va/yoki `ACCESS_COARSE_LOCATION`) so'raladi.
2. **2-bosqich:** Faqat va faqat foydalanuvchi 1-bosqichdagi ruxsatni berganidan keyingina, alohida mantiq va alohida Rationale ko'rsatilib, `ACCESS_BACKGROUND_LOCATION` ruxsati so'raladi.

### Nega `SDK_INT >= Build.VERSION_CODES.Q` sharti kerak?
Android 9 (API 28 - Pie) va undan past versiyalarda `ACCESS_BACKGROUND_LOCATION` degan alohida ruxsatnoma mavjud bo'lmagan. Oldingi versiyalarda `ACCESS_FINE_LOCATION` berilsa, fon rejimi ham avtomatik berilgan. Shuning uchun bu shart faqat Android 10 (Q) va undan yuqori qurilmalarga tegishli.

---

## 14-savol: Permission Groups (Ruxsatnomalar guruhlari)

### Permission Group nima va dialoglar sonining kamayishi:
Android tizimida bir-biriga yaqin bo'lgan dangerous ruxsatnomalar mantiqiy **Guruhlar (Permission Groups)**ga birlashtirilgan. Agar foydalanuvchi biror guruhga kiruvchi bitta ruxsatnomani tasdiqlasa, keyinchalik xuddi shu guruhdagi boshqa ruxsatnoma so'ralganda, tizim foydalanuvchiga dialog ko'rsatmasdan ruxsatni avtomatik tasdiqlaydi.

So'ralgan 5 ta ruxsatnoma:
1. `ACCESS_FINE_LOCATION` va `ACCESS_COARSE_LOCATION` $\rightarrow$ **LOCATION** guruhi (1 ta dialog)
2. `READ_CONTACTS` va `WRITE_CONTACTS` $\rightarrow$ **CONTACTS** guruhi (1 ta dialog)
3. `CAMERA` $\rightarrow$ **CAMERA** guruhi (1 ta dialog)  
Natijada 5 ta so'rov o'rniga jami **3 ta dialog** chiqadi.

### Guruhlarga 3 ta misol:
1. `LOCATION` (`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`)
2. `CONTACTS` (`READ_CONTACTS`, `WRITE_CONTACTS`, `GET_ACCOUNTS`)
3. `CALENDAR` (`READ_CALENDAR`, `WRITE_CALENDAR`)

### Nega guruh tarkibiga kodingizda tayanmaslik kerak?
Android platformasining kelgusi versiyalarida Google qaysi ruxsatnoma qaysi guruhga tegishli ekanligini yoki ularning ishlash mexanizmini o'zgartirishi mumkin. Kodingizda har bir ruxsatnomani alohida va aniq so'rash lozim.

---

## 15-savol: Manifest va `android:maxSdkVersion`

### Manifestga `<uses-permission>` qo'shish unutilsa:
Ruxsatnoma kodingizda runtime orqali so'ralganda, Android OS so'rovni foydalanuvchiga ko'rsatmaydi va **darhol rad etadi (Denied)**. Logcat'da xatolik xabari chiqadi.

### E'lon qilish jarayonidagi farqlar:
* **Install-time & Runtime:** Ikkalasi ham Manifest'ga `<uses-permission android:name="..." />` shaklida kiritilishi shart.
* **Special permissions:** Manifest'ga kiritilishidan tashqari, maxsus Sozlamalar Intent'lari bilan bog'liq harakatlarni talab qiladi.

### `android:maxSdkVersion` atributi va WRITE_EXTERNAL_STORAGE misoli:
Android versiyalari rivojlanishi bilan ba'zi ruxsatnomalar eskiradi. Masalan, Android 10 (API 29) da Scoped Storage joriy qilindi. Android 4.4 (API 19) va undan pastda ilovaning xususiy papkasiga fayl yozish uchun `WRITE_EXTERNAL_STORAGE` kerak edi, API 19 dan yuqorida esa kerak emas.
```xml
<uses-permission 
    android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="18" />
```
Bu tizimga: "Ushbu ruxsatnomani faqat API 18 va undan past qurilmalarda so'ra, yangi versiyalarda so'rama" degan buyruqni beradi.

---

## 16-savol: Hardware features va `uses-feature` filtri

### `android:required` (true vs false) ta'siri:
* **`required="true"` (Default):** Google Play Store ushbu ilovani kamerasi (hardware) bo'lmagan qurilmalarga (masalan, Android TV, ba'zi planshet va smart-soatlarga) ko'rsatmaydi va **yuklab olishni taqiqlaydi**.
* **`required="false"`:** Ilova kamerasiz qurilmalarga ham yuklab olinadi. Lekin dasturchi kod ichida qurilmada kamera bor-yo'qligini tekshirishi shart.

### Kodda tekshirish:
```kotlin
val hasCamera = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
```

### Ma'ruzadagi `visibleOn` modelining 4 ta natijasi:

| Tizim holati (Hardware) | `android:required` | Play Store'da ko'rinishi | Koddagi tekshiruv (`hasSystemFeature`) |
| :--- | :--- | :--- | :--- |
| Qurilmada Kamera Bor | `true` | **Ko'rinadi** | `true` |
| Qurilmada Kamera Yo'q | `true` | **Yashiriladi (Yutiladi)** | N/A (Yuklab bo'lmaydi) |
| Qurilmada Kamera Bor | `false` | **Ko me'yor ko'rinadi** | `true` |
| Qurilmada Kamera Yo'q | `false` | **Ko me'yor ko'rinadi** | `false` (Kamera funksiyalari o'chirilishi kerak) |

---

## 17-savol: Bir martalik ruxsatnoma (One-time permission)

### Bir martalik ruxsatnoma qanday ishlaydi va bekor qilinadi?
Android 11 (API 30) da "Only this time" (Faqat bu safar) opsiyasi qo'shilgan.
1. Foydalanuvchi ilovadan chiqsa yoki ilova fonga (background) o'tsa va u yerda biroz vaqt tursa, tizim berilgan ruxsatni **avtomatik ravishda Bekor qiladi (Revoke qiladi)**.
2. Ilova jarayoni (process) o'chirilganda (kill bo'lganda) ham ruxsat bekor qilinadi.

### Nega `granted` qiymatini keshlab bo'lmaydi?
O'zgaruvchini `remember { ... }` ichida saqlash yoki `SharedPreferences`/`DataStore` ichida keshlab qo'yish xatolik beradi, chunki foydalanuvchi ilovadan chiqib-qaytganida ruxsat bekor qilingan bo'lishi mumkin.

### Tuzatilgan `CameraScreen.kt` kodi (`ON_RESUME` yordamida):

```kotlin
@Composable
fun CameraScreen() {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Ilovaga har safar qaytilganda (Resume) ruxsat holati qayta tekshiriladi
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    if (hasCameraPermission) {
        // Kamera UI
    } else {
        // Ruxsat so'rash UI
    }
}
```

---

## 18-savol: Auto-reset va Unused Permissions (Auto-revoke)

### (1) Auto-reset (Android 11+):
* **Nima qiladi:** Agar foydalanuvchi ilovani bir necha oy davomida ishlatmasa, Android OS maxfiylikni ta'minlash uchun ilovaga ilgari berilgan barcha dangerous ruxsatnomalarni **avtomatik ravishda bekor qiladi (reset qiladi)**.
* **Foydalanuvchi uni o'chirishi:** Foydalanuvchi Sozlamalar bo'limiga o'tib, "Unused app settings" opsiyasini o'chirishi mumkin. Ilova foydalanuvchini `Intent(Intent.ACTION_AUTO_REVOKE_PERMISSIONS)` orqali ushbu Sozlamalar sahifasiga yo'naltirishi mumkin.

### (2) `revokeSelfPermissionOnKill` (Android 13 / API 33):
* **Funksiyasi:** Android 13 da qo'shilgan `ContextCompat.revokeSelfPermissionOnKill(context, permission)` funksiyasi orqali dasturchi ilovaga ortiq kerak bo'lmagan ruxsatnomalarni dasturiy ravishda topshirishi (vos kechishi) mumkin.
* **Foydasi:** Ilovaning keraksiz ma'lumotlarga kirish xavfini kamaytiradi, foydalanuvchi ishonchini oshiradi va maxfiylik standartlariga to'liq mos keladi.

---

## 19-savol: Android versiyalari bo'yicha evolyutsiya jadvali va `POST_NOTIFICATIONS`

### API Versiyalari bo'yicha asosiy o'zgarishlar:
* **API 23 (Android 6.0):** Runtime Permissions tizimi joriy qilindi (xavfli ruxsatnomalar ish jarayonida so'raladigan bo'ldi).
* **API 30 (Android 11):** Bir martalik ruxsatnomalar ("Only this time") va oylab ishlatilmagan ilovalar uchun Auto-reset mexanizmi qo'shildi.
* **API 33 (Android 13):** Bildirishnomalar uchun `POST_NOTIFICATIONS` runtime ruxsatnomasi kiritildi va ruxsatnomalarni ko'ngilli ravishda topshirish (`revokeSelfPermissionOnKill`) imkoniyati paydo bo me'yor bo'ldi.

### `POST_NOTIFICATIONS` va API 33 dan past versiyalar:
Android 13 (API 33) ga qadar bildirishnoma yuborish uchun hech qanday runtime ruxsatnoma talab qilinmas edi. API 33 dan boshlab bildirishnomalar yuborish **Runtime Permission**ga aylandi.

### Nega API 33 dan pastda so'ramaslik kerak?
API 33 dan past versiyalarda `POST_NOTIFICATIONS` ruxsatnomasi mavjud emas. Agar siz uni API 32 yoki undan past Android versiyali qurilmalarda so'rashga urinsangiz, tizim so'rovni e'tiborsiz qoldiradi yoki xatolik kelib chiqadi. Shuning uchun bildirishnoma ruxsatini so'rash kodi quyidagicha himoyalanadi:

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    // POST_NOTIFICATIONS ruxsatini so'rash kodi
}
```