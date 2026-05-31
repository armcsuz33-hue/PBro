# Avangard Stock - Sklad Boshqaruv Tizimi

Android ilovasi - Avangard brendi uchun sklad kirdi-chiqdi boshqaruv tizimi.

## Texnologiyalar

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Room Database** — lokal ma'lumotlar bazasi
- **MVVM Architecture** — ViewModel + Repository pattern
- **Navigation Compose** — ekranlar navigatsiyasi
- **Coroutines + Flow** — asinxron ma'lumot oqimi

## Funksiyalar

### 📊 Dashboard (Bosh sahifa)
- Umumiy statistika (mahsulotlar soni, ombordagi miqdor)
- Kirdi/Sotildi/Qaytdi ko'rsatkichlari
- Umumiy oborot va foyda
- Kam qolgan mahsulotlar ogohlantirishi
- So'nggi tranzaksiyalar

### 📦 Mahsulotlar
- Barcha mahsulotlar ro'yxati
- Qidirish funksiyasi
- Narxlar va foyda ko'rsatish
- Stock holati (yashil/sariq/qizil)

### 🔄 Tranzaksiyalar
- Barcha kirdi-chiqdi amallari
- Filter: Barchasi / Kirdi / Sotildi / Qaytdi
- Batafsil ma'lumot

### ➕ Yangi amal qo'shish
- Mahsulot tanlash (dropdown)
- Turi: Kirdi / Sotildi / Qaytdi
- Soni va narxi
- Avtomatik stock yangilanishi

### ⚙️ Admin Panel
- Yangi mahsulot qo'shish
- Mahsulotlarni o'chirish
- To'liq CRUD boshqaruv

## Avangard Mahsulotlar (oldindan qo'shilgan)

1. Avangard BCD 442 I
2. Avangard 800
3. Avangard BCD 410 S
4. Avangard LSC 395
5. Avangard BCD 276 S
6. Avangard BCD 276 BP
7. Avangard SD/SC 1800

## Loyiha Strukturasi

```
app/src/main/java/com/avangard/stock/
├── AvangardApp.kt
├── MainActivity.kt
├── data/
│   ├── model/
│   │   ├── Product.kt
│   │   ├── StockTransaction.kt
│   │   └── DashboardStats.kt
│   ├── dao/
│   │   ├── ProductDao.kt
│   │   └── StockTransactionDao.kt
│   ├── database/
│   │   ├── AppDatabase.kt
│   │   └── Converters.kt
│   └── repository/
│       └── ProductRepository.kt
└── ui/
    ├── theme/
    │   ├── Color.kt
    │   └── Theme.kt
    ├── navigation/
    │   └── AvangardNavHost.kt
    ├── viewmodel/
    │   ├── DashboardViewModel.kt
    │   ├── ProductViewModel.kt
    │   └── TransactionViewModel.kt
    └── screens/
        ├── DashboardScreen.kt
        ├── ProductsScreen.kt
        ├── TransactionsScreen.kt
        ├── AddTransactionScreen.kt
        └── AdminScreen.kt
```

## Ishga tushirish

1. Android Studio'da loyihani oching
2. `AvangardStock` papkasini import qiling
3. Build va Run qiling (min SDK 26)

## Rasmlar

Logotip va mahsulot rasmlarini `app/src/main/res/drawable/` papkasiga qo'shing.
