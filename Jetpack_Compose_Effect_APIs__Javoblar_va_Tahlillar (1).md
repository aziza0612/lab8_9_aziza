# Jetpack Compose Side-Effects va Effect APIs: Kompleks Tahlil va Javoblar

---

## 1-savol: Side-effect va Composable Tanasining Xavfi

### 1. Side-effect nima?
**Side-effect** (yon-ta'sir) — bu composable funksiya doirasidan tashqarida joylashgan ilova holatini (app state) o'zgartirish yoki tashqi tizimlar bilan muloqot qilishdir. Masalan, tahlillar (analytics) logini yuborish, ma'lumotlar bazasiga yozish, tarmoq so'rovini amalga oshirish yoki ViewModel funksiyasini chaqirish side-effect hisoblanadi.

### 2. Nega Composable tanasi side-effect uchun xavfli?
Composable funksiyaning asosiy vazifasi — holatni (state) UI'ga aylantirib berish (describe UI). Composable tanasi freymlar almashinuvida juda ko'p marta va oldindan aytib bo'lmaydigan chastotada qayta ijro etilishi (**recomposition**) mumkin. 

Composable tanasi side-effect uchun quyidagi sabablarga ko'ra xavfli:
* **Ko'p marta qayta chaqirilishi:** Animatsiya yoki scroll vaqtida recomposition soniyasiga 60 yoki 120 marta sodir bo'lishi mumkin. Agar side-effect to'g'ridan-to'g'ri tanada chaqirilsa, tarmoq so'rovlari yoki loglar ham shuncha marta takrorlanadi.
* **Bekor qilinishi va tashlab yuborilishi (Discarded composition):** Compose kadrlarni optimal chizish uchun composition jarayonini istalgan joyda to'xtatishi va bekor qilishi mumkin. Lekin composable tanasidagi to'g'ridan-to mezoniy yon-ta'sirlar bekor qilinmaydi va bajarilib bo'lgan bo'ladi.

### 3. Ma'ruzadagi “Effect” ta'rifi (Key Term)
> **Effect** — bu composable funksiyaning UI chizish jarayoniga (render execution) xalaqit bermaydigan, Compose lifecycle'i bilan xavfsiz integratsiya qilingan va uning nazorati ostida bajariladigan side-effect'dir.

### 4. Nega ma'lumot yuklash ViewModel'ga tegishli? (Unidirectional Data Flow)
Unidirectional Data Flow (UDF) tamoyiliga ko'ra:
1. **State top-down (yuqoridan pastga)** harakatlanadi: ViewModel UI'ga o'zgarmas holatni (State) beradi.
2. **Events bottom-up (pastdan yuqoriga)** harakatlanadi: UI foydalanuvchi harakatlarini ViewModel'ga uzatadi.

Ma'lumot yuklash jarayoni biznes-mantiq va ma'lumotlar qatlamiga kiradi. UI faqat holatni aks ettirishi kerak. Agar ma'lumot yuklash Composable ichida boshlansa, UI va biznes-mantiq chatishib ketadi, ma'lumot ekran qayta chizilganda qaytadan yuklanaveradi va Test qilish unumdorligi yo'qoladi.

---

## 2-savol: Effect APIs Moslik Jadvali

| Ehtiyoj / Holat | Mos keladigan API |
| :--- | :--- |
| Composable Composition'da turganda suspend funksiyani chaqirish | `LaunchedEffect` |
| `onClick` kabi callback ichidan coroutine ishga tushirish | `rememberCoroutineScope` |
| Effect ichida eng so'nggi qiymatni o'qish, lekin effect'ni qayta ishga tushirmaslik | `rememberUpdatedState` |
| Listener qo'shish va keyin uni olib tashlash (cleanup) | `DisposableEffect` |
| Compose state'ni Compose boshqarmaydigan kodga uzatish | `SideEffect` |
| Flow, LiveData yoki RxJava'ni Compose State'ga aylantirish | `collectAsStateWithLifecycle` / `produceState` |
| Kirish qiymatidan kamroq recompose qilish | `derivedStateOf` |
| Compose State'ni Flow'ga aylantirish | `snapshotFlow` |

---

## 3-savol: PulseEffect Animatsiyasi Tahlili

### 1. Effect qachon ishga tushadi?
`LaunchedEffect(pulseRateMs)` composable Composition'ga birinchi marta kirganda (Attach) va `pulseRateMs` kaliti birinchi marta initsializatsiya bo'lganda ishga tushadi.

### 2. `pulseRateMs` 3000 dan 1000 ga o'zgarganda nima bo'ladi?
`LaunchedEffect` kaliti (`pulseRateMs`) o'zgargani uchun mavjud korutina **bekor qilinadi (cancel)**. Shundan so'ng, yangi `pulseRateMs = 1000L` qiymati bilan korutina **qaytadan ishga tushiriladi (restart)**.

### 3. Composition'ni tark etganda va `isActive` roli
Composable Composition'ni tark etganda `LaunchedEffect` ning `CoroutineScope`i va uning ichidagi Job bekor qilinadi. 
`while(isActive)` siklidagi `isActive` xususiyati Kotlin Coroutines a'zosi bo'lib, joriy korutinaning faollik holatini tekshiradi. Korutina bekor qilingach (`isActive == false`), sikl o'z ishini tugatadi.

### 4. `MiniComposition` modeli tushuntirishi
`MiniComposition` modelida `effect(100) cancelled` iborasi shuni ko'rsatadiki: kalit o'zgarganda yoki composable yo'qolganda eskicha render/kutish sikli tugatilishi (cancel) va xotirani band qilmasligi shart. Bu resource leak'larning oldini oladi.

---

## 4-savol: `LaunchedEffect(true)` va Kalitlar

### Nega `LaunchedEffect(true)` "while(true) kabi shubhali"?
`LaunchedEffect(true)` yoki `LaunchedEffect(Unit)` ishlatilganda, effect faqat bitta marta — composable composition'ga kirganda ishlaydi va keyin kalit o'zgarmagani uchun hech qachon restart bo'lmaydi. 

Bu joyda shubha shundaki: agar effect ichida ishlatilayotgan parametrlar (`userId` kabi) o'zgarishi mumkin bo'lsa, `true` kaliti ishlatilganda effect ushbu o'zgarishlarni e'tiborsiz qoldiradi ва "stale state" (eskirgan holat) xatosi yuzaga keladi.

### Qachon doimiy kalit (constant key) o'rinli?
Faqat va faqat effect **butun ekran / composable umri davomida aniq 1 marta** bajarilishi kerak bo'lganda va u hech qanday o'zgaruvchi tashqi parametrlarga bog'liq bo'lmaganda o'rinli. 
* *Misol:* **Splash screen** — ekran ochilganda 2 soniya kutib, Keyingi ekranga o'tish operatsiyasi.

### Tuzatilgan kod:
```kotlin
// Hello.kt - Tuzatilgan variant
LaunchedEffect(userId) { // userId kalit sifatida berildi
    api.sendHello(userId)
}
```

---

## 5-savol: Coroutine Scope va Callbacks

### 1. Nega `LaunchedEffect` callback ichidan chaqirib bo'lmaydi?
`LaunchedEffect` — bu `@Composable` funksiya. Compose qoidalariga ko'ra, Composable funksiyalar faqat va faqat boshqa Composable funksiyalar tanasidan chaqirilishi mumkin. `onClick` kabi callback'lar esa oddiy lambda funksiyalardir.

### 2. `rememberCoroutineScope` va uning umri
`rememberCoroutineScope` Compose lifecycle'iga bog'langan `CoroutineScope` qaytaradi.
Uning umri u chaqirilgan Composable'ning Composition davomiyligi bilan cheklangan. Composable Composition'ni tark etganda, ushbu scope va undan ishga tushirilgan barcha korutinalar avtomatik bekor qilinadi (cancel bo'ladi).

### 3. `showSnackbar` suspend funksiyaning ahamiyati va To'g'ri kod
`showSnackbar` suspend funksiya bo'lgani uchun u xabarni ekranga chiqaradi va snackbar yo'qolguncha (yoki boshqa snackbar uni almashtirguncha) korutinani to'xtatib (suspend qilib) turadi.

```kotlin
// To'g'ri variant
val scope = rememberCoroutineScope()

Button(onClick = {
    scope.launch {
        snackbarHostState.showSnackbar("Hi")
    }
}) {
    Text("Click me")
}
```

### 4. API Tanlash Qoidasi:
* **"Composable paydo bo'lgani uchun boshlanadigan ish"**: `LaunchedEffect` (Event-driven by Lifecycle)
* **"Foydalanuvchi biror narsa qilgani uchun boshlanadigan ish"**: `rememberCoroutineScope` (Event-driven by User)

---

## 6-savol: Stale Lambdas va `rememberUpdatedState`

### 1. Nega effect eski lambdani (captured) chaqirib yuborishi mumkin?
`LaunchedEffect(true)` faqat bir marta ishga tushgani uchun u initsializatsiya paytidagi `onTimeout` xotira manzilini (reference) o'zida saqlab (`capture` qilib) qoladi. Agar ota composable recompose bo'lib, yangi `onTimeout` lambdasini uzatsa ham, effect ichidagi eski lambda bajarilaveradi.

### 2. `rememberUpdatedState` buni qanday hal qiladi?
`rememberUpdatedState` ichida `remember { mutableStateOf(newValue) }` saqlaydi va har gal recomposition bo'lganda uning `.value` qiymatini yangilab boradi. State ob'ektining o'zi o'zgarmasdan qolgani uchun, `LaunchedEffect` restart bo'lmasdan turib ham har doim eng so'nggi lambdaga murojaat qiladi.

### 3. Nega `onTimeout` ni kalit qilish noto'g'ri?
Agar `LaunchedEffect(onTimeout)` qilib yazilsa, ota-ona recompose bo'lib har gal yangi lambda nusxasini berganida `LaunchedEffect` bekor qilinadi va 2 soniyalik taymer (`delay`) **qaytadan 0 dan boshlanadi**. Natijada taymer hech qachon tugamasligi mumkin.

### 4. Model natijasi isboti
* **Captured (`onTimeout v1`)**: 2 soniyadan keyin v1 chaqiriladi, recomposition'da kelgan v2 va v3 inobatga olinmaydi.
* **UpdatedState (`onTimeout v3`)**: 2 soniya davomida recomposition orqali lambda v3 ga o'zgargan bo'lsa, xuddi shu eng so'nggi v3 izchillik bilan chaqiriladi.

---

## 7-savol: Lifecycle Observers va `DisposableEffect`

### 1. Nega tozalash (cleanup) kerak va nima uchun bu memory leak beradi?
`LifecycleOwner` (masalan, Activity yoki Fragment) Compose ekranidan ko'proq yashashi mumkin. Observer olib tashlanmasa, `LifecycleOwner` o'z ro'yxatida composable obyektiga bo'lgan havolani ushlab turadi. Composable ekrandan yo'qolsa ham xotiradan tozalanmaydi — bu **Memory Leak** ga olib keladi.

### 2. `onDispose` qoidasi
`DisposableEffect` blokining **eng oxirgi satrida** `onDispose` chaqiruvi bo'lishi shart. Aks holda kod kompilyatsiya bo'lmaydi (kompilyator xatolik beradi).

### 3. Bo'sh `onDispose` nega yomon amaliyot?
Bo'sh `onDispose { }` yozish — cleanup kerak emasligini anglatadi. Agar hech qanday tozalash mantiqiy shart bo'lmasa, `DisposableEffect` emas, `LaunchedEffect` yoki `SideEffect` ishlatilishi kerak.

### 4. Tuzatilgan kod:
```kotlin
// LifecycleLogger.kt - Tuzatilgan variant
DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event -> 
        // Event processing
    }
    lifecycleOwner.lifecycle.addObserver(observer)

    onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
    }
}
```

---

## 8-savol: `MiniDisposable` Model Simulation

### 1. Konsol chiqishi (To'liq 4 qator):
```text
A.addObserver(observer)
A.removeObserver(observer)
B.addObserver(observer)
B.removeObserver(observer)
```

### 2. Sabab va ketma-ketlik tahlili:
* **Ikkinchi `screen("A")`**: Kalit (`owner = "A"`) o'zgarmagani uchun effect qayta ishga tushmaydi va hech narsa konsolga chiqmaydi.
* **`screen("B")` kelganda**: Kalit "A" dan "B" ga o mezoniy o'zgardi. **Birinchi bo'lib eski effect tozalanadi** (`A.removeObserver`), shundan so'ng yangi effect qo'shiladi (`B.addObserver`).
* **`comp.leave()`**: Composition tugagani uchun oxirgi active effect tozalanadi (`B.removeObserver`).

---

## 9-savol: `SideEffect` va Uncommitted Composition

### 1. `SideEffect` qachon ishga tushadi?
`SideEffect` har bir **muvaffaqiyatli recomposition (commit)** amalga oshib, Compose o'zgarishlarni real ekran darajasiga uzatib bo'lganidan so'ng darhol ishga tushadi.

### 2. Nega to'g'ridan-to'g'ri chaqirish noto'g'ri?
Compose render qilish jarayonida recomposition'ni **bekor qilishi (discard)** mumkin. Agar analytics to'g'ridan-to'g'ri composable tanasida chaqirilsa, ekran chizilmay bekor bo'lgan taqdirda ham analytics so'rovi ketib bo mezoniy bo'ladi va ma'lumotlar buziladi.

### 3. Attempt modeli tahlili:
* **Direct call (commits = false)**: Analytics **chaqiriladi** (xato! Chunki frame barbod bo'ldi).
* **SideEffect (commits = false)**: Analytics **chaqirilmaydi** (to'g'ri, chunki commit bo'lmadi).
* **SideEffect (commits = true)**: Analytics **chaqiriladi** (to'g'ri, chunki commit muvaffaqiyatli o'tdi).

### 4. Kalitlari bormi?
Yo'q, `SideEffect` hech qanday kalit (`key`) qabul qilmaydi. U har bir omadli recomposition'dan keyin ishlaydi.

---

## 10-savol: `produceState` va Nomlash Qoidalari

### 1. `produceState` nima qiladi va uning lifecycle'i
`produceState` Compose-ga tegishli bo'lmagan manbalardan (Flow, LiveData, Callback, Tarmoq) ma'lumotni kuzatib, uni Compose `State`ga aylantiradi.
* **Start:** Composable Composition'ga kirganda producer coroutine ishga tushadi.
* **Cancellation:** Composable Composition'ni tark etganda bekor qilinadi.

### 2. Conflation (State tutishi)
`produceState` qaytaradigan `State` ob'ekti `.value` o'zgarganda xabardor qiladi. Agar bir xil qiymat qayta berilsa (`equals == true`), Compose recomposition chaqirmaydi (qiymat bir xilligi uchun to'planadi/conflate bo'ladi).

### 3. Nomlash qoidasi
Kotlin Compose konvensiyasiga ko'ra:
* UI komponent qaytaruvchi (void/Unit) Composable'lar **Katta harf** bilan boshlanadi (`fun UserProfile()`).
* Qiymat qaytaruvchi (`State<T>` va h.k.) Composable funksiyalar **kichik harf** bilan boshlanadi (`fun loadNetworkImage(...)`).

---

## 11-savol: Custom `produceState` Implementatsiyasi

### Custom `myProduceState` kodi:
```kotlin
@Composable
fun <T> myProduceState(
    initialValue: T,
    key: Any?,
    producer: suspend MutableState<T>.() -> Unit
): State<T> {
    val result = remember { mutableStateOf(initialValue) }
    
    LaunchedEffect(key) {
        result.producer()
    }
    
    return result
}
```

### Xatti-harakat:
`produceState` ichida `remember` va `LaunchedEffect` dan foydalanadi. Shu sababli, `key` o'zgarganda `LaunchedEffect` qayta ishga tushadi va ichidagi `producer` coroutine bekor qilinib, yangi kalit bilan qaytadan bajariladi.

---

## 12-savol: High-frequency State va `derivedStateOf`

### 1. Nega to'g'ridan-to'g'ri o'qish va `derivedStateOf`
`listState.firstVisibleItemIndex` scroll vaqtida har bir piksel bo'yicha juda ko'p marta o'zgaradi.
To'g'ridan-to'g'ri o'qilganda, har bir indeks o'zgarishida composable recomposition bo'ladi.
`derivedStateOf` esa o'zgarishlar chastotasini kamaytiradi: kirish holati ko'p marta o'zgarsa ham, uning mantiqiy natijasi (`index > 0` - true/false) o'zgargandagina recomposition beradi.

### 2. Nega `remember` ichiga o'raladi?
`derivedStateOf` yaratgan ob'ekt recomposition davomida qayta-qayta yangidan yaratilmasligi va o'zining ichki kesh va observer holatini saqlab qolishi uchun u `remember` ichida ushlab turiladi.

### 3. Indekslar oqimi tahlili `[0, 0, 1, 2, 3, 4, 5, 3, 1, 0]`:
* **To'g'ridan-to'g'ri o'qilganda**: Qiymat har gal o'zgarganda recomposition bo'ladi (0 -> 0 tushib qolishi mumkin, qolgan 9 ta o'zgarishda recompose bo'ladi) = **9 ta recomposition**.
* **`derivedStateOf` bilan (`index > 0`)**:
  - `0` -> `false` (Boshlang'ich holat)
  - `0` -> `false` (O'zgarish yo'q)
  - `1` -> `true` (**1-recomposition: false -> true**)
  - `2, 3, 4, 5, 3, 1` -> hammasida `true` (O'zgarish yo'q)
  - `0` -> `false` (**2-recomposition: true -> false**)
  Total: Boshlang'ich va holat almashinuvi bilan hisoblaganda jami **2-3 ta recomposition**.

---

## 13-savol: `derivedStateOf` Noto'g'ri Ishlatilishi

### 1. Nega bu yerda `derivedStateOf` ortiqcha?
`firstName` va `lastName` o'zi Compose State hisoblanadi. Ularning o'zgarish chastotasi `$firstName $lastName` natijasining o'zgarish chastotasi bilan **bir xil**. Bu yerda hech qanday "buferlash" yoki chastotani kamaytirish ro'y bermaydi. `derivedStateOf` ishlatish faqat ortiqcha ob'ekt yaratib, xotira va protsessorni ortiqcha band qiladi.

### 2. To'g'ri variant:
```kotlin
// FullName.kt - To'g'ri variant
var firstName by remember { mutableStateOf("") }
var lastName by remember { mutableStateOf("") }

// Oddiy Kotlin string kontaktenatsiyasi
val fullName = "$firstName $lastName"
```

### 3. `derivedStateOf` qo'llash mezoni (Caution):
> **Qoida / Caution:** `derivedStateOf` faqat **kirish holatlarining (inputs) o'zgarish chastotasi chiqish holatining (output) o'zgarish chastotasidan sezilarli darajada ko'p bo'lgandagina** ishlatilishi kerak. Agar kirish va chiqish bir xil chastotada o'zgarsa, `derivedStateOf` ishlatmang!

---

## 14-savol: Audio Waveform va `derivedStateOf`

### 1. Qachon ishlatiladi?
Agar o'ynalayotgan audio pozitsiyasi (`currentPositionMs` - har millisekundda o'zgaradi) asosida 100 ta ustundan iborat waveform'ning faqat o'tgan ustunlar sonini (`highlightedBarCount` - har 500ms da 1 ta o'zgaradi) hisoblayotgan bo'lsak, `derivedStateOf` ishlatiladi.

### 2. Qaysi 3 holatda ISHLATILMAYDI va sabablari:
1. **Amplitudalar oldindan hisoblangan bo'lsa:**
   * *Sabab:* Ma'lumotlar statik ro'yxat (List) ko'rinishida va ular holat o'zgarishiga bog'liq emas.
2. **Waveform statik bo'lsa:**
   * *Sabab:* Hech qanday yuqori chastatali holat kuzatilmaydi.
3. **Waveform `LaunchedEffect` yoki `animate*` bilan qo'lda yangilansa:**
   * *Sabab:* Animatsiyalar Compose'ning o'zining ichki optimizatsiyalangan mexanizmlari orqali UI state'ni yangilaydi. `derivedStateOf` qo'shish ortiqcha hisob-kitob qatlamini paydo qiladi.

---

## 15-savol: `snapshotFlow` va Stream Operatorlari

### 1. `snapshotFlow` nima va nega "Cold Flow"?
`snapshotFlow` Compose `State` ob'ektlarini Kotlin Coroutines `Flow`iga aylantirib beradi. U **Cold Flow** hisoblanadi, chunki u ichidagi koddik o'zgarishlarni terminal operator (masalan, `collect`) chaqirilmagunicha kuzatmaydi va hisoblamaydi.

### 2. Operatorlar vazifasi:
* `map { index -> index > 0 }`: Indeksni boolean (`true/false`) qiymatga o'giradi.
* `distinctUntilChanged()`: Ketma-ket kelgan bir xil qiymatlarni o'tkazib yuboradi (faqat holat `false`dan `true`ga yoki aksincha o'zgarganda keyinga o'tkazadi).
* `filter { it == true }`: Faqat `true` bo'lgan holatni (ro'yxat birinchi elementdan o'tganda) filterlaydi.

### 3. Nega `collect` `LaunchedEffect` ichida?
`collect` suspend funksiyasi bo'lgani uchun u faqat CoroutineScope ichida bajarilishi kerak.

### 4. Hisoblash (Oqim: `0, 0, 1, 2, 5, 3, 0, 0, 1, 4`):
1. `0` -> map: `false` -> distinct: `false` -> filter: elenadi.
2. `0` -> map: `false` -> distinct: takroriy `false`, tashlanadi.
3. `1` -> map: `true`  -> distinct: yangi (`true`) -> filter: o'tadi -> **Analytics yuborildi (1-marta)**.
4. `2` -> map: `true`  -> distinct: takroriy `true`, tashlanadi.
5. `5` -> map: `true`  -> distinct: takroriy `true`, tashlanadi.
6. `3` -> map: `true`  -> distinct: takroriy `true`, tashlanadi.
7. `0` -> map: `false` -> distinct: yangi (`false`) -> filter: elenadi (`false` bo'lgani uchun).
8. `0` -> map: `false` -> distinct: takroriy `false`, tashlanadi.
9. `1` -> map: `true`  -> distinct: yangi (`true`) -> filter: o'tadi -> **Analytics yuborildi (2-marta)**.
10. `4` -> map: `true` -> distinct: takroriy `true`, tashlanadi.

* **Natija:** Analytics jami **2 marta** yuboriladi.

---

## 16-savol: Effect Kalitlari Modellari

Natijalar jadvali (`tick` 1 dan 4 gacha o'zgaradi, `owner` 3-tickda "A" dan "B" ga o'zgaradi):

| Kalitlar | Restart soni | Sababi va Baholash |
| :--- | :---: | :--- |
| `keys = []` | **1 marta** | **Xato (Eskirgan qiymat):** Owner 3-tickda "B"ga o'zgarsa ham effect restart bo'lmaydi va eskirgan "A" bilan ishlaydi. |
| `keys = [owner]` | **2 marta** | **To'g'ri:** Effect faqat `owner` "A" dan "B" ga o'zgarganda restart bo'ladi. Zaruriy va optimal. |
| `keys = [owner, tick]`| **4 marta** | **Samarasiz:** `tick` har gal o'zgarganda effect keraksiz ravishda bekor qilinib, qaytadan ishga tushaveradi. |

---

## 17-savol: `HomeScreen` Kalitlar Optimizatsiyasi

### 1. Qaysi o'zgaruvchilar kalit bo'lishi kerak / kerak emas?
* **Kalit bo'lishi kerak:** `lifecycleOwner`.
* **Kalit bo'lmasligi kerak:** `currentOnStart`, `currentOnStop` (chunki ular `rememberUpdatedState` bilan o'ralgan).

### 2. Nega `currentOnStart` / `currentOnStop` kalit emas?
Chunki ularning maqsadi — effect restart bo'lmasdan turib, har doim so'nggi callback funksiyasini chaqirishdir. Ularni kalitga qo'shish `rememberUpdatedState` ning ma'nosini yo'qotadi.

### 3. Nega `lifecycleOwner` kalit qilinmasa xato bo'ladi?
Agar `lifecycleOwner` o'zgarsa (masalan, yangi oynaga o'tilsa), eski observer tozalanmaydi va yangi owner'ga qo mezoniy qo'shilmaydi. Effect yangi lifecycle lifecycle hodisalarini o mezoniy tuta olmaydi.

### 4. "Key Point" Qoidasi:
> **Key Point:** Effect ichida ishlatiladigan barcha o'zgaruvchi ob'ektlar `keys` ro'yxatiga kiritilishi shart, Agarda ushbu o'zgaruvchi `rememberUpdatedState` orqali o'ralmagan bo'lsa.

---

## 18-savol: `ChatScreen` Code Review (4 xato va tuzatish)

### Topilgan xatolar:
1. **(1) `viewModel.markOpened(userId)` composable tanasida joylashgan:** Recomposition paytida ko'p marta qayta ishlaydi. `LaunchedEffect(userId)` ichiga o'tkazilishi kerak.
2. **(2) `LaunchedEffect(true)` ishlatilgan va `onClose` ushlanib qolgan:** Agar `onClose` lambda o'zgarsa stale state beradi, shuningdek ekran 5s kutish davrida re-render bo'lsa xato keladi.
3. **(3) `DisposableEffect` ichida `onDispose` yo'q:** Observer qo'shilgan lekin olib tashlanmagan. Memory leak beradi.
4. **(4) `Button` `onClick` callback ichida `LaunchedEffect` chaqirilgan:** Composable bo'lmagan joyda Composable chaqirish mumkin emas. CoroutineScope ishlatish kerak.

### Tuzatilgan to'liq kod:
```kotlin
// ChatScreen.kt - Tuzatilgan variant
@Composable
fun ChatScreen(
    userId: String, 
    onClose: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val owner = LocalLifecycleOwner.current
    val currentOnClose by rememberUpdatedState(onClose)

    // 1-xato yechimi
    LaunchedEffect(userId) {
        viewModel.markOpened(userId)
    }

    // 2-xato yechimi
    LaunchedEffect(Unit) {
        delay(5_000)
        currentOnClose()
    }

    // 3-xato yechimi
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, _ -> }
        owner.lifecycle.addObserver(observer)
        onDispose {
            owner.lifecycle.removeObserver(observer)
        }
    }

    Button(onClick = {
        // 4-xato yechimi
        scope.launch {
            viewModel.send()
        }
    }) {
        Text("Send")
    }
}
```

---

## 19-savol: Sakkizta Effect API Umumiy Jadvali va Amaliy Misollar

| API | Qachon ishlaydi | Tozalash (Cleanup) | Restart sababi | Real Amaliy Misol |
| :--- | :--- | :--- | :--- | :--- |
| **`LaunchedEffect`** | Composition'ga kirganda | Coroutine cancel qilinadi | Key (`keys`) o'zgarganda | Ekran ochilganda bir marta ma'lumot yuklash yoki taymer ishga tushirish |
| **`rememberCoroutineScope`** | Callback ichida `.launch` bo'lganda | Composition tugaganda Scope cancel bo'ladi | N/A (Restart bo'lmaydi) | Tugma bosilganda Snackbard ko'rsatish yoki BottomSheet'ni yopish |
| **`rememberUpdatedState`** | Recomposition bo'lganda | Yo'q | N/A (O'zi qiymatni update qiladi) | Uzoq davom etuvchi effect ichida har doim yangi `onTimeout` lambdasini o'qish |
| **`DisposableEffect`** | Composition'ga kirganda | `onDispose` bloki bajariladi | Key (`keys`) o'zgarganda | `SensorManager` yoki `LocationListener` qo'shish va olib tashlash |
| **`SideEffect`** | Har bir Omaddli Commit'dan keyin | Yo'q | Har bir recomposition'da qayta ishlaydi | Compose State'ni tashqi uchinchi tomon SDK'siga (Firebase/Crashlytics) uzatish |
| **`produceState`** | Composition'ga kirganda | Coroutine cancel qilinadi | Key (`keys`) o'zgarganda | Websocket yoki Firebase Realtime DB oqimini Compose State'ga aylantirish |
| **`derivedStateOf`** | O'qilgan state o'zgarganda | Kesh tozalanadi | Ichki state'lar qiymati o'zgarganda | LazyColumn scroll'ida "Top Page" tugmasi ko'rinishini (`index > 0`) hisoblash |
| **`snapshotFlow`** | Flow `collect` bo'lganda | Flow collector to'xtaganda | Ichida o'qilayotgan Compose State o'zgarganda | Search Input State'ini Flow'ga o'tkazib `debounce(300ms)` operatorini qo'llash |