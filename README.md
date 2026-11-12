# 🌐 PC DOQTAR Connection Service

> **Cloud Telemetry, Diagnostics History & Fleet Health Synchronization Backbone for PC DOQTAR**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Database](https://img.shields.io/badge/Database-MySQL%20%2F%20Hibernate%20JPA-blue.svg)](https://www.mysql.com/)
[![Security](https://img.shields.io/badge/Auth-Firebase%20Bearer%20Tokens-yellow.svg)](https://firebase.google.com/)
[![License](https://img.shields.io/badge/License-Proprietary-lightgrey.svg)](#)

---

## 🌟 Overview

The **PC DOQTAR Connection Service** is the secure cloud backend and telemetry synchronization service powering the [PC DOQTAR](https://github.com/KishoreNMproject/pc-doqtar) desktop application ecosystem. 

While the desktop client captures immediate hardware vitals and performs real-time checks, the **Connection Service serves as the centralized brain**: securely archiving health snapshots, computing historical delta trends, assessing hardware degradation patterns over time, and synchronizing device telemetry across user sessions.

Whether you are an individual customer keeping a long-term performance history of your personal workstation, or an IT administrator managing the ongoing health of a fleet of PCs, **PC DOQTAR Connection Service ensures your hardware data is safe, organized, and actionable**.

---

## 🎯 What Value Does This Deliver to Users & Customers?

```
┌────────────────────────┐         Encrypted HTTPS (Bearer Token)        ┌──────────────────────────────────┐
│   💻 PC DOQTAR Client  │ ────────────────────────────────────────────► │ ☁️ PC DOQTAR Connection Service  │
│  (Real-Time Monitoring)│ ◄──────────────────────────────────────────── │   (Spring Boot Cloud Backend)    │
└────────────────────────┘          Trend Assessment & History           └─────────────────┬────────────────┘
                                                                                           │
                                                                       ┌───────────────────┴────────────────┐
                                                                       ▼                                    ▼
                                                                🗄️ MySQL Telemetry DB              🛡️ Firebase Auth
                                                                (Encrypted User Records)          (Identity Verification)
```

### 1. 📈 Longitudinal Trend & Degradation Analysis
A single diagnostic scan only shows how your computer is performing *right now*. The Connection Service compares every uploaded snapshot against your historical baseline to answer crucial questions:
- *Is CPU temperature progressively climbing under identical loads, suggesting dried thermal paste or dust buildup?*
- *Is available RAM shrinking continuously across reboots due to background bloatware?*
- *What is the rate of storage consumption across your solid-state drives?*

### 2. 🗄️ Centralized Cloud Health Vault
- All diagnostic records are securely stored in your personal cloud vault.
- Never lose your hardware records even if you format your drive, upgrade Windows, or switch to a new machine.

### 3. 🔄 Resilient Offline Sync & Batch Uploads
- Diagnostic tests run in environments with spotty or no internet connection are safely cached by the desktop client.
- Once connectivity is restored, the Connection Service accepts **batch snapshot synchronization**, seamlessly backfilling your historical timeline without data loss.

### 4. 🛡️ Enterprise-Grade Security & User Privacy
- **Strict User Isolation**: Every diagnostic record is bound to your authenticated Firebase UID. Only you can view or query your system's data.
- **Stateless Bearer Authentication**: Every API request is verified with cryptographically signed Firebase ID tokens.
- **Pure Telemetry Focus**: The service stores hardware performance indicators (temperatures, clock speeds, disk utilization, bandwidth). It **never** receives, processes, or stores personal documents or file contents.

---

## 📡 REST API & Capabilities

All diagnostics endpoints require an `Authorization: Bearer <Firebase_ID_Token>` HTTP header to ensure authenticated access.

### Diagnostic Telemetry Endpoints

| Method | Endpoint | Description | Customer / User Benefit |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/diagnostics/upload` | Ingests a single diagnostic snapshot, evaluates thresholds, and computes deltas. | Saves a current scan and immediately returns comparative trend analysis. |
| `POST` | `/api/diagnostics/upload-batch` | Ingests an array of cached diagnostic snapshots in a single network round-trip. | Fast, efficient synchronization after offline work. |
| `GET` | `/api/diagnostics/latest` | Retrieves your most recent diagnostic record along with comparative delta metrics. | Instant view of your machine's latest known health status. |
| `GET` | `/api/diagnostics/history?limit=20` | Returns your past diagnostic records (customizable limit between 1 and 100). | Powers historical graphs and long-term health reporting. |

### System & Health Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/` | Health check endpoint confirming the service is online. |
| `GET` | `/sessions` | Validates database connectivity and active persistence session health. |
| `GET` | `/getsession` | Generates a secure session identifier for client handshakes. |

---

## 📊 Sample Diagnostic Data Flow

When a client uploads a snapshot:
```json
{
  "cpuUsagePercent": 24.5,
  "cpuTemperatureCelsius": 52.0,
  "memoryUsedBytes": 7890123456,
  "memoryTotalBytes": 17179869184,
  "diskUsedBytes": 256000000000,
  "diskTotalBytes": 1024000000000,
  "overallHealth": "HEALTHY",
  "warningCount": 0
}
```

The Connection Service responds with saved record details and calculated historical trends:
```json
{
  "recordId": 1042,
  "recordedAt": "2026-11-12T13:00:00Z",
  "status": "SAVED",
  "trendAssessment": "STABLE",
  "historicalDelta": {
    "cpuTempDeltaCelsius": -1.5,
    "memoryUsageDeltaMb": +120.0,
    "diskUsageDeltaGb": +2.1
  }
}
```

---

## ⚙️ Configuration & Deployment

### Prerequisites
- **Java**: Version 21 or higher
- **Database**: MySQL 8.0+ (or compatible MariaDB/Cloud SQL instance)
- **Firebase Project**: Firebase project credentials for token authentication

### Environment Variables
Configure the following standard environment variables:

| Variable | Description | Example |
| :--- | :--- | :--- |
| `DB_URL` | JDBC connection string to the MySQL database | `jdbc:mysql://localhost:3306/pcdoqtar?useSSL=true` |
| `DB_USERNAME` | Database username | `pcdoqtar_user` |
| `DB_PASSWORD` | Database user password | `••••••••••••` |
| `PORT` | Listening server port (defaults to 8080) | `8080` |

### Building and Running Locally
```bash
# Clone the repository
git clone https://github.com/KishoreNMproject/pc-doqtar-connection-service.git
cd pc-doqtar-connection-service

# Build the Spring Boot executable JAR
mvn clean package -DskipTests

# Run the service
java -jar target/pc-doqtar-1.0.0.jar
```

---

## 🤝 Companion Desktop Client

This service is designed to pair directly with the desktop client:
👉 **[PC DOQTAR Desktop Application](https://github.com/KishoreNMproject/pc-doqtar)**

By default, the desktop client connects to the hosted cloud instance at:
`https://pc-doqtar-connection-service.vercel.app`

---

## 💬 Frequently Asked Questions (FAQ)

<details>
<summary><b>Can other users see my PC's hardware diagnostics?</b></summary>
No. Data isolation is enforced at the database and query layer. Every record is stored with and filtered by the cryptographic UID assigned to your account by Firebase Authentication.
</details>

<details>
<summary><b>What happens to my data if the connection service is temporarily unreachable?</b></summary>
The PC DOQTAR desktop client caches your diagnostic snapshots locally in a secure offline queue. As soon as the service is reachable again, the client automatically flushes the queue via the batch upload API.
</details>

<details>
<summary><b>Can this service be deployed on private cloud or on-premise infrastructure?</b></summary>
Yes. Because the service is built as a standard Spring Boot application packaged with Docker and Maven, organizations can deploy it within their private cloud (AWS, GCP, Azure, or on-premise Kubernetes/VMs).
</details>

---

## 📬 Support & Inquiries

- Report technical issues or suggest enhancements via [GitHub Issues](https://github.com/KishoreNMproject/pc-doqtar-connection-service/issues).
- For inquiries regarding enterprise fleet monitoring, contact the project maintainers.

---

<p align="center">
  <b>PC DOQTAR Connection Service — Reliable, Secure Cloud Telemetry for Modern Systems.</b>
</p>
