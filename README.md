# 📦 Inventory Management App

An Android Inventory Management application developed using **Kotlin**, **XML**, **Room Database**, and the **MVVM Architecture**. The app allows users to manage products, monitor inventory, calculate total inventory value, and receive low-stock alerts through a clean and user-friendly interface.

---

## Features

- Dashboard displaying inventory summary
- Add new products
- Edit existing products
- Delete products
- Search products by name or category
- Automatic inventory value calculation
- Low stock monitoring
- Offline data storage using Room Database
- Clean and responsive Material Design UI

---

## Technology Stack

- Kotlin
- XML
- Android Studio
- Room Database
- MVVM Architecture
- Repository Pattern
- LiveData
- Navigation Component
- Material Design Components

---

## Project Structure

```
app
├── data
│   ├── Product.kt
│   ├── ProductDao.kt
│   ├── InventoryDatabase.kt
├── repository
│   └── InventoryRepository.kt
├── ui
│   ├── Dashboard
│   ├── Products
│   └── Add/Edit Product
├── viewmodel
│   └── InventoryViewModel.kt
└── MainActivity.kt
```

---

## Main Modules

### Dashboard
- Displays total products
- Shows total inventory value
- Displays low-stock product count
- Shows inventory status

### Product Management
- Add Product
- Edit Product
- Delete Product
- View Product Details

### Search
- Search products by product name
- Search products by category

### Inventory Tracking
- Track product quantity
- Low stock alerts
- Automatic inventory value calculation

---

## Installation

1. Clone the repository

```bash
git clone https://github.com/YOUR_GITHUB_USERNAME/InventoryManagement.git
```

2. Open the project in Android Studio.

3. Sync Gradle.

4. Run the application on an Android Emulator or Android device.

---

## Future Enhancements

- Dark Mode
- Barcode Scanner
- Product Images
- Export Inventory to PDF
- Export Inventory to Excel/CSV
- Charts and Analytics
- Cloud Backup
- User Authentication

---

## Author

**Kunal Kishore**

B.Tech Computer Science Engineering

---

## License

This project is created for educational and learning purposes.
