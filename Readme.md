# 📱 Firebase Marketing Management App (Android - Kotlin)

This is a modern Android application built using **Kotlin**, **Firebase**, and **Jetpack components**, designed to manage a referral-based marketing and product interaction system. It supports **multi-role access** for Admins, Users, and Developers, each with distinct feature sets and access privileges.

---

## 🚀 Features Overview

### 👨‍💼 Admin Panel
- Manage and verify user accounts
- Review and moderate marketing status submissions (screenshots)
- Track and update user wallet withdrawals (manual status updates; no in-app payment processing)
- Generate and manage redeemable coupons
- View total user statistics and activity logs

### 🙋‍♂️ User Features
- Purchase products and receive redeemable coupons
- Participate in daily marketing activities by uploading status screenshots
- Earn rewards through:
  - Referral system
  - Status uploads
  - Product interactions
- View wallet balance and request withdrawals
- Monitor referral network and income history

### 🛠 Developer Panel
- Manage and test app features during development
- Housekeeping operations like scheduled cleanup of older media (e.g., screenshot removal)

---

## 🧠 Built With

- **Kotlin (Android)**
- **Firebase**
  - Authentication
  - Cloud Firestore
  - Cloud Storage
- **Android Jetpack Components**
  - ViewModel, LiveData, Navigation, Lifecycle
- **MVVM Architecture**
- **Material Design Components**

---

## 🔒 Security & Source Code Disclaimer

This repository **does not contain any sensitive data or API credentials**.

- `google-services.json` is excluded via `.gitignore`
- Admin credentials and access control are managed securely via Firebase
- Firestore collections and paths are modularized and generic where possible

> ⚠️ **Disclaimer:** This codebase is intended for educational and demonstration purposes.  
> It is **not** a production-ready financial application.  
> All transactions (e.g., payments) are handled externally — the app only reflects status updates.  
> Reuse of this codebase for commercial or unauthorized replication is discouraged.

---
