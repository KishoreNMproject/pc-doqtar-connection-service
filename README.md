# PC DOQTAR Connection Service ☁️🛡️
### *Cloud Diagnostics Synchronization, Fleet Health History & Trend Analytics Platform*

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Security](https://img.shields.io/badge/Auth-Firebase%20Admin%20SDK-yellow.svg)](https://firebase.google.com/)
[![Database](https://img.shields.io/badge/Database-Cloud%20MySQL%20%7C%20JPA-blue.svg)](https://www.mysql.com/)
[![Deployment](https://img.shields.io/badge/Deployment-Cloud%20%7C%20Docker%20%7C%20Vercel-purple.svg)]()
[![License](https://img.shields.io/badge/License-Proprietary-lightgrey.svg)](#)

---

## 🌟 What is PC DOQTAR Connection Service?

The **PC DOQTAR Connection Service** is the secure cloud backend and analytics engine powering the **PC DOQTAR** desktop ecosystem. 

While the [PC DOQTAR Desktop Application](https://github.com/KishoreNMproject/pc-doqtar) performs real-time local hardware monitoring and instant health assessments on your PC, the **Connection Service** serves as your centralized cloud health record. It securely archives your diagnostic snapshots over time, computes historical performance deltas, detects emerging hardware degradation trends, and enables seamless health monitoring across multiple machines from a single user account.

Whether you are an individual user tracking the long-term health of your primary workstation, a family managing multiple home PCs, or an organization monitoring computer fleet stability, the Connection Service provides the secure cloud intelligence you need.

---

## ✨ Key Customer Benefits & Features

### 📈 1. Long-Term Health Records & Trend Analysis
- **Track Performance Over Time:** Monitor how your PC's CPU workload, memory pressure, and storage capacity evolve across weeks and months.
- **Automated Degradation Detection:** Every diagnostic upload is automatically compared against your previous snapshot to identify gradual performance degradation before it causes system instability.
- **Hardware Delta Insights:** Receive immediate feedback on whether system changes (e.g., driver updates, software installations, or background tasks) improved or degraded hardware performance.

### 🔄 2. Multi-Device Unified Account
- **One Account, All Your Devices:** Sign in from your desktop, laptop, or home office computer using your secure Firebase account.
- **Consolidated Health History:** Easily access the latest health records and diagnostic timelines for each of your enrolled devices.

### ⚡ 3. Resilient Offline-First Synchronization & Batch Ingestion
- **Never Miss a Health Check:** If you are working offline, traveling, or experiencing poor connectivity, the desktop application queues diagnostic snapshots locally.
- **Seamless Batch Processing:** As soon as an internet connection is re-established, the Connection Service ingests queued offline records via `/api/diagnostics/upload-batch` in a single transaction, preserving exact historical timestamps.

### 🛡️ 4. Enterprise-Grade Security & Account Isolation
- **Firebase Token Verification:** Every API call is verified via Google Firebase Admin SDK cryptographic token inspection.
- **Zero Cross-Account Access:** Your diagnostic data is strictly tied to your authenticated Firebase User ID (`UID`), ensuring absolute privacy and data isolation.
- **Secure Transport:** All communications between client devices and the cloud service are encrypted using modern HTTPS / TLS standards.

### 🚀 5. High-Performance, Scalable Cloud Architecture
- Built on modern **Spring Boot 3** and **Java 21** with **Spring Data JPA / Hibernate**, designed for low-latency request handling and elastic cloud scaling on platforms such as Docker, Vercel, AWS, or Google Cloud Platform.

---

## 🔒 Customer Privacy & Data Ethics Guarantee

We recognize that computer health telemetry must be handled with utmost care. Our privacy commitment to every user and customer:

| Information We Store | Information We NEVER Access or Store |
| :--- | :--- |
| ✅ CPU load percentages & clock metrics | ❌ Personal documents, photos, or downloads |
| ✅ Memory utilization & available RAM | ❌ Web browsing activity, cookies, or search history |
| ✅ Disk capacity, free space, and drive health metrics | ❌ Passwords, keystrokes, or account credentials |
| ✅ Network throughput, latency, and adapter types | ❌ Screen captures or window contents |
| ✅ Anonymized diagnostic timestamps & trend deltas | ❌ Any private application or user data files |

Your telemetry data is used exclusively to calculate performance metrics and historical trends for your personal account.

---

## 🔄 How the Ecosystem Works

```
┌─────────────────────────┐                HTTPS / TLS
│  PC DOQTAR Desktop App  │ ─────────────────────────────────────────────┐
│  • OSHI Hardware Engine │         Authorization: Bearer <ID_TOKEN>     │
│  • Local Diagnostics    │                                              ▼
│  • Offline Sync Queue   │                               ┌───────────────────────────────┐
└─────────────────────────┘                               │ PC DOQTAR Connection Service  │
                                                          │ • Firebase Token Filter       │
                                                          │ • Diagnostic Service Engine   │
                                                          │ • Delta & Trend Calculator    │
                                                          └───────────────┬───────────────┘
                                                                          │
                                                                          ▼
                                                          ┌───────────────────────────────┐
                                                          │   Secure Cloud Database       │
                                                          │   • Diagnostic Snapshots      │
                                                          │   • Historical Metrics        │
                                                          │   • Storage Partition Records │
                                                          └───────────────────────────────┘
```

1. **Local Collection:** The PC DOQTAR desktop client collects hardware metrics (CPU, RAM, Disks, Network).
2. **Authenticated Transmission:** The client transmits a signed diagnostic snapshot over HTTPS with the user's Firebase Bearer token.
3. **Validation & Persistence:** The Connection Service verifies identity, persists the snapshot to the database, and queries the user's most recent prior record.
4. **Trend Response:** The service calculates performance changes and responds with saved record IDs, historical trend assessments, and health score feedback.

---

## 🌐 API Overview (For Integrators & Administrators)

All diagnostic endpoints require a valid Firebase ID Token in the request header:
```http
Authorization: Bearer <FIREBASE_ID_TOKEN>
```

### Endpoints Summary

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/diagnostics/upload` | Ingests a single diagnostic snapshot, evaluates health deltas against the previous snapshot, and returns a trend assessment. |
| `POST` | `/api/diagnostics/upload-batch` | Ingests an array of cached offline diagnostic snapshots in a single synchronized transaction. |
| `GET` | `/api/diagnostics/latest` | Retrieves the authenticated user's most recent diagnostic report along with historical comparison metrics. |
| `GET` | `/api/diagnostics/history?limit=20` | Returns up to `limit` (default: 20, max: 100) historical records ordered chronologically (newest first). |
| `GET` | `/auth/me` | Validates authentication and returns the current user's Firebase UID and verified email. |
| `GET` | `/sessions` | Health check endpoint verifying database connectivity and active session state. |
| `GET` | `/getsession` | Generates a unique tracking session UUID for client handover coordination. |
| `GET` | `/` | Service root confirmation ("PC DOQTAR Server is running."). |

---

## ⚙️ Configuration & Deployment Guide

For system administrators, DevOps engineers, or users running their own private cloud instance:

### Environment Variables

| Variable | Description | Example / Default |
| :--- | :--- | :--- |
| `DB_URL` | JDBC connection URL for MySQL database | `jdbc:mysql://hostname:3306/pc_doqtar?sslMode=REQUIRED` |
| `DB_USERNAME` | Database username with read/write privileges | `doqtar_admin` |
| `DB_PASSWORD` | Database password | `••••••••••••` |
| `PORT` | Listening HTTP port for Spring Boot | `8080` |
| `FIREBASE_CONFIG` / Service Account | Firebase Admin credentials for token verification | Service account JSON or default credentials |

### Quick Start (Local or Containerized)

```bash
# 1. Clone the repository
git clone https://github.com/KishoreNMproject/pc-doqtar-connection-service.git
cd pc-doqtar-connection-service

# 2. Build the application with Maven
mvn clean package -DskipTests

# 3. Run the service
java -jar target/pc-doqtar-connection-service-1.0.0.jar
```

Or deploy using the included `Dockerfile.vercel` container specification for serverless hosting.

---

## ❓ Frequently Asked Questions (FAQ)

<details>
<summary><b>Can someone else view my PC's diagnostic reports?</b></summary>
No. Access to diagnostic reports strictly requires valid Firebase authentication matching the user account that uploaded the records. No public or cross-account access is permitted.
</details>

<details>
<summary><b>What happens if the cloud service is temporarily unreachable?</b></summary>
The PC DOQTAR desktop application continues functioning completely uninterrupted. It records diagnostic reports to a local queue and automatically synchronizes them once the connection is restored.
</details>

<details>
<summary><b>Can I self-host this service on my own private network or cloud?</b></summary>
Yes. The service is standard Spring Boot 3 and can be deployed on any server or private cloud with Java 21 and a MySQL database.
</details>

---

## 🤝 Ecosystem Links & Support

- 🖥️ **Desktop Client:** [PC DOQTAR Desktop Application](https://github.com/KishoreNMproject/pc-doqtar)
- 🐛 **Issue Tracker:** [Connection Service Issues](https://github.com/KishoreNMproject/pc-doqtar-connection-service/issues)
- 💬 **Discussions & Feedback:** [GitHub Discussions](https://github.com/KishoreNMproject/pc-doqtar/discussions)

---

<p align="center">
  <b>PC DOQTAR Connection Service</b> — <i>Reliable, Secure Cloud Intelligence for Your PC Health.</i>
</p>
