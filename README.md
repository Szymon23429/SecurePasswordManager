# PasswordManager

A secure, modern, and user-friendly Android application designed to help individuals manage their digital credentials with confidence.

##  About the App

**PasswordManager** is a robust tool built to simplify your digital life without compromising on security. In an era where password reuse is a major vulnerability, this app provides a centralized, encrypted vault for all your login information.

### Key Features

*   **Secure Vault:** All credentials (service name, username, and password) are stored locally using **AES-256 GCM encryption**.
*   **Security Audit Dashboard:** A "Pro" feature that analyzes your vault to identify weak passwords and potentially dangerous password reuse across different services.
*   **Biometric Authentication:** Quick and secure access using your fingerprint or face, powered by the **Android Keystore** and `EncryptedSharedPreferences`.
*   **Smart Search & Filtering:** Instantly find your credentials with a real-time search bar and category-based organization (Work, Social, Banking, etc.).
*   **Integrated Password Generator:** Create strong, complex passwords on the fly with customizable length and character sets.
*   **Visual Strength Meter:** Get real-time feedback on your password's security level as you type.
*   **Modern Material Design:** A clean, fluid interface featuring smooth animations and a focus on ease of use.

##  Security Architecture

*   **Zero-Knowledge Storage:** Your master PIN is never stored in plain text. It is used to derive encryption keys that never leave your device.
*   **Local-Only Data:** Your data stays on your phone. No cloud syncing means you have total control over your information.
*   **Auto-Clear Clipboard:** For added safety, copied passwords are automatically cleared from your clipboard after a short duration.

## 👥Who is this for?

**PasswordManager** is designed for anyone who takes their digital security seriously but wants a simple solution:

1.  **Students and Professionals:** Who manage dozens of accounts across different platforms and need a reliable way to keep them unique and secure.
2.  **Security-Conscious Individuals:** Who want to move away from easy-to-guess passwords and password reuse.
3.  **Everyday Users:** Who want a fast, biometric-enabled way to access their login details without having to remember every single one.

---
*Built as a high-performance Android project focusing on Security, UX, and modern Software Architecture.*