# QR MealTrack

QR MealTrack is an Android app that helps you **track your expenses and analyze your meals**.  
It scans **Moldovan QR codes from fiscal receipts**, automatically extracts receipt text, saves your purchases, and shows statistics for a **selected period**:  

- how much you **spent on meals**,  
- how much you’ve **“eaten” in kilograms**,  
- and **overall spending category statistics displayed as charts**.  

> **Disclaimer:**  
> This app is designed for personal expense tracking and works **with Moldovan fiscal receipts**. It may not work with receipt formats from other countries.
> It is an independent project and is **not affiliated with or endorsed by FISC, ANAF, or any government authority**.

---

## ✨ Features

- 📷 **Scan QR codes from Moldovan fiscal receipts**  
- 🌐 **Load receipt pages through a built-in WebView**  
- 📝 **Extract page content via JavaScript (`document.body.innerText`)**  
- 🔍 **Parse raw text into structured receipt data** (store, total amount, meal items with weight and price)  
- 💾 **Save purchases locally using Room database**  
- 📊 **View overall spending statistics by category**  
- 🍽 **Detailed analysis of the MEAL category**:  
  - total **spent on meals** for a given period (day, week, month)  
  - total **weight of meals consumed (kg)** for the same period  
  - which meals appear most frequently  
- 🔄 **Time filters (day, week, month) with planned calendar view for selecting a custom date range**  
- 🛠 **Modern sci-fi styled UI built with Jetpack Compose**  
- 🚀 **Basic CI with GitHub Actions** (automatic build pipeline)  
- 🛡 **Firebase Crashlytics** *(planned)* for crash monitoring  

---

## 🛠 Tech Stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose + Material 3  
- **Architecture:**  
  - **Clean Architecture**  
  - **MVI (UiState, UiAction, UiEvent)** on interactive screens  
  - **MVVM (StateFlow, UseCase)** on analytics/statistics screens  
- **Database:** Room  
- **Data loading:** WebView + JavaScript (`document.body.innerText`)  
- **Asynchronous:** Coroutines + Flow  
- **DI:** Hilt  
- **CI:** GitHub Actions  

---

## 🎯 Why I built this project

- To **easily track personal expenses**  
- To focus on **meal tracking and food spending analysis**  
- To see **how much I spent and how much I consumed in kilograms**  
- To find out **which meals I buy most often**  
- To practice **both MVI and MVVM architectures in one project**  
- To explore **reactive UI updates with StateFlow**  
- To set up **basic CI with GitHub Actions** and integrate Crashlytics  

---

## 📸 Screenshots

https://github.com/user-attachments/assets/db38fd7c-73ba-408d-a6fc-15f91bcd2b2f 

https://github.com/user-attachments/assets/404fef62-63ac-409d-871c-b641546ded7a 
https://github.com/user-attachments/assets/00302a9e-f1b0-4d2d-8425-7f42d2602bbc  
https://github.com/user-attachments/assets/781ace21-754c-420d-8c9b-588227f36677  
https://github.com/user-attachments/assets/f25b7a5a-203e-46d0-843e-b7f42fcf5985

---

## 📜 License

This project is licensed under the **Apache License 2.0**.  
You are free to use and modify the code as long as you **retain attribution to the author**.

---

## 👩‍💻 Author

Developed by **Alexandra Milis** as a pet project for learning modern Android development practices and personal expense & meal tracking.
