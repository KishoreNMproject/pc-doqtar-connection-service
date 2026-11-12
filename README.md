# PC DOQTAR Connection Service ☁️🩺
### *Secure Cloud Telemetry, Intelligence & Historical Health Synchronization Backend*

[![Service](https://img.shields.io/badge/service-PC%20DOQTAR%20Cloud-009688.svg)](https://pc-doqtar-connection-service.vercel.app)
[![Framework](https://img.shields.io/badge/framework-Spring%20Boot%203.x-6DB33F.svg)](https://spring.io/projects/spring-boot)
[![Security](https://img.shields.io/badge/auth-Firebase%20Token%20Verified-FFCA28.svg)](https://firebase.google.com)
[![Database](https://img.shields.io/badge/database-MySQL%20JPA-00758F.svg)](https://www.mysql.com)
[![Status](https://img.shields.io/badge/status-active-brightgreen.svg)]()

---

## 🌟 What is the PC DOQTAR Connection Service?

The **PC DOQTAR Connection Service** is the cloud intelligence and telemetry synchronization backbone for the [PC DOQTAR](https://github.com/KishoreNMproject/pc-doqtar) desktop application.

While the PC DOQTAR desktop app provides real-time, on-device diagnostics, the **Connection Service** elevates the user experience by giving customers a **secure, persistent health history**. It enables users to observe their computer’s health trajectory over time, automatically detects hardware degradation trends, and provides reliable cloud backup for all diagnostic milestones.

---

## ✨ Key Customer & User Benefits

### 📈 1. Long-Term Health Timeline & Historical Tracking
- **Beyond the Moment:** A single moment of lag doesn't tell the whole story. The Connection Service stores snapshots of your system's vital statistics across reboots and operating sessions.
- **Comparative Trends:** Each diagnostic report is automatically compared with your previous baseline to determine whether your PC's health is **Improving**, **Stable**, or **Degrading**.
- **Early Warning Detection:** Uncover slow memory leaks, creeping background CPU drain, or steadily diminishing disk capacity before they lead to unexpected system crashes.

### 🔄 2. Seamless Offline-to-Cloud Synchronization
- **Reliable Batch Sync:** If your computer is disconnected from the internet, the desktop app stores diagnostics locally in an offline queue. When your connection is restored, the Connection Service ingests and processes batch updates without losing a single data point.
- **One-Click Manual Sync:** Customers can trigger an immediate cloud sync anytime directly from the desktop application's title bar.

### 🔐 3. Enterprise-Grade Security & User Privacy
- **Strict User Isolation:** Every diagnostic snapshot is permanently tied to your authenticated Firebase User ID (UID). No other user or unauthorized third party can view your computer’s performance data.
- **End-to-End Encryption:** All network communications are conducted over encrypted HTTPS/TLS 1.3 connections.
- **Zero Sensitive Data Access:** The service strictly accepts hardware telemetry (percentages, byte counts, and hardware names). It never receives or stores personal documents, passwords, browsing logs, or file contents.

### ⚡ 4. High Availability & Cloud Scalability
- Engineered with **Spring Boot 3** and cloud-hosted **MySQL persistence** to deliver sub-second response times and 99.9% uptime.
- Stateless, container-ready architecture designed to scale seamlessly whether servicing individual home users or large corporate fleets.

---

## 🔄 How It Works: The Customer Journey

```
┌──────────────────────────┐             ┌───────────────────────────┐
│   PC DOQTAR Desktop      │             │  PC DOQTAR Cloud Service  │
│  (Customer's Computer)   │             │     (Connection Service)  │
└────────────┬─────────────┘             └─────────────┬─────────────┘
             │                                         │
             │  1. Collects hardware diagnostics       │
             │     (CPU %, RAM, Disk, Network)         │
             │                                         │
             │  2. Authenticates with Firebase         │
             │     (receives secure Bearer ID token)   │
             │                                         │
             │  3. POST /api/diagnostics/upload ──────>│
             │     (Encrypted payload + Token)         │  4. Validates Firebase Token
             │                                         │  5. Verifies data integrity
             │                                         │  6. Computes historical delta
             │                                         │  7. Stores record in MySQL
             │                                         │
             │<─ 8. HTTP 201 Created ──────────────────┤
             │     (Returns trend assessment & alert)  │
             ▼                                         ▼
   [ Instant User Feedback: "Sync Successful • System Stable" ]
```

---

## 🛡️ Customer Privacy & Data Governance

Your trust is paramount. Here is exactly what the PC DOQTAR Connection Service processes:

| Data Collected & Saved | Data NEVER Collected |
| :--- | :--- |
| ✅ Overall System Health rating (Healthy / Warning / Critical) | ❌ Personal files, photos, or documents |
| ✅ Overall CPU Load percentage and core count | ❌ Passwords, encryption keys, or credentials |
| ✅ Total, Used, and Free RAM bytes | ❌ Website browsing history, cookies, or searches |
| ✅ Disk capacity, free storage, and drive letters | ❌ Application content, keystrokes, or screen data |
| ✅ Network adapter names and transmission counters | ❌ Usernames, local machine accounts, or IP logs |

All records are bound to your account and can be exported or purged upon user request.

---

## 🔌 API Overview (Client & Integrator Scope)

All endpoints require a valid Firebase ID token passed in the `Authorization` HTTP header:
```http
Authorization: Bearer <FIREBASE_ID_TOKEN>
```

| Method | Endpoint | Description | Customer Result |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/diagnostics/upload` | Uploads a single diagnostic snapshot from the desktop app. | Evaluates metrics, persists record, and returns immediate comparative trend. |
| `POST` | `/api/diagnostics/upload-batch` | Uploads a batch of queued offline diagnostic snapshots. | Syncs all offline history in a single request once reconnected. |
| `GET` | `/api/diagnostics/latest` | Retrieves the customer's most recent diagnostic snapshot. | Displays your PC's latest cloud-recorded health status. |
| `GET` | `/api/diagnostics/history?limit=20` | Retrieves the customer's last *N* diagnostic records. | Powers timeline visualization and long-term health charts. |
| `GET` | `/actuator/health` | Public service health status check. | Verifies cloud service operational availability. |

### Sample Response (`/api/diagnostics/upload`)

```json
{
  "recordId": 1042,
  "status": "SUCCESS",
  "systemHealth": "HEALTHY",
  "trendAssessment": "STABLE",
  "cpuDelta": "+1.4%",
  "memoryDelta": "-0.8%",
  "message": "Diagnostic snapshot recorded successfully. System is operating within healthy parameters.",
  "timestamp": "2026-11-12T07:35:00Z"
}
```

---

## 🏢 Enterprise & Self-Hosting Guide

Organizations managing internal PC fleets or users desiring a private cloud can easily self-host the connection service:

### Prerequisites
* Java 21 JDK or higher
* MySQL 8.0+ instance
* Firebase Project (for Firebase Admin authentication)

### Environment Configuration

Configure the following environment variables:

```bash
# Database Settings
export DB_URL="jdbc:mysql://your-mysql-host:3306/pc_doqtar?useSSL=true&serverTimezone=UTC"
export DB_USERNAME="your_database_username"
export DB_PASSWORD="your_database_password"

# Firebase Admin Configuration
export FIREBASE_CONFIG_PATH="/path/to/firebase-service-account.json"

# Server Port (Default: 8080)
export PORT=8080
```

### Running the Service

```bash
# Clone the repository
git clone https://github.com/KishoreNMproject/pc-doqtar-connection-service.git
cd pc-doqtar-connection-service

# Build and run
mvn clean spring-boot:run
```

Once running, point your desktop client to your custom server URL by setting `PC_DOQTAR_SERVER_URL=https://your-custom-domain.com`.

---

## 🤝 Ecosystem & Support

* 💻 **Desktop Application:** [PC DOQTAR Client](https://github.com/KishoreNMproject/pc-doqtar)
* 🐛 **Issue Tracker:** [Report a Service Issue](https://github.com/KishoreNMproject/pc-doqtar-connection-service/issues)
* 💬 **Community Discussions:** [Join the Conversation](https://github.com/KishoreNMproject/pc-doqtar/discussions)

---

<p align="center">
  <b>PC DOQTAR Connection Service</b> — <i>Reliable, secure cloud intelligence for every personal computer.</i>
</p>
