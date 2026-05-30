# Feedorch1: Low-Latency Hybrid Feed Orchestrator & Telemetry Ingestion Engine

Feedorch1 is a scalable backend engine engineered to solve the "Celebrity Problem" (Thundering Herd) in modern social media timelines. Built using **Spring Boot**, **MySQL**, and **Redis**, the system utilizes a polyglot storage architecture to balance high-speed memory caching with resilient relational querying. Additionally, it integrates **RabbitMQ** to ingest and process user behavioral analytics asynchronously on a separate thread pool.

---

## 🚀 Key Architectural Capabilities

### 1. The Hybrid Push/Pull Timeline Router
To prevent memory exhaustion ($O(N)$ write amplification) when high-volume creators publish content, the core architecture applies a hybrid distribution pattern based on user status:
* **The Push Layer (Regular Users):** When a standard user posts, an asynchronous fan-out engine pushes the content metadata directly into their active followers' pre-computed Redis timelines ($O(1)$ read delivery).
* **The Pull Layer (Celebrity Figures):** When a celebrity updates their status, the engine stores the post *once* in a highly indexed MySQL block, completely avoiding write storms. During the client feed-generation pass, the engine dynamically queries MySQL via B-Tree indices and merges the records seamlessly in local application memory.

### 2. Decoupled Asynchronous Telemetry Pipeline
Real-time tracking of high-intent user interaction indicators (e.g., clicks, swipes, profile visits) runs via an event-driven framework to maintain absolute UI fluidity:
* Controllers hand over inbound tracking payloads to a **RabbitMQ** exchange in under **2ms**, instantly responding with a `202 Accepted` acknowledgement.
* A background `InteractionConsumer` listener handles multi-tier analytical matrix math, real-time bounding guardrails (clamping scores between $-10$ and $+20$), and updates the cache layout out of sight.

### 3. In-Memory Affinity Overlays (Personalization Sorting)
When a user calls the feed endpoint, the system applies a dynamic sorting engine across the unified timeline arrays:
* **Tier 1 (Affinity Weighting):** The engine cross-references the live Redis Hash matrix for the viewer's implicit preference ledger. Creators with higher positive engagement weights are instantly bubbled to the top.
* **Tier 2 (Chronological Fallback):** If multiple accounts register equivalent affinity weights, the engine drops back onto high-resolution millisecond timestamps to keep layout ordering cohesive.

---

## 🛠️ System Infrastructure

* **Backend Framework:** Spring Boot v3.x (Java 17+)
* **Relational Database:** MySQL 8.0 (B-Tree indexed relationships)
* **High-Performance Cache:** Redis (Lists for fan-out indices, Hashes for user-affinity matrices)
* **Message Broker:** RabbitMQ 3.x (Event-driven asynchronous ingestion queues)
* **Containerization:** Docker / Alpine Linux environment

---

## 📡 API Interface Blueprint (Endpoints)

All routes are mounted directly under the root domain workspace (`api/`).

### 1. Ingest Telemetry Signal
* **Method:** `POST`
* **URL:** `http://localhost:8080/api/interactions`
* **Payload:**

```json
{
    "userId": 1,
    "storyId": 101,
    "authorId": 10,
    "interactionType": "PROFILE_CLICK",
    "durationSeconds": 0.0
}
```
### 2. Generate Personalized Feed
* **Method:** `GET`
* **URL:** `http:localhost:8080/api/feed/{userId}`
* **Response Payload Structure:**
  
```json
[
  {
    "id": 2,
        "authorId": 10,
        "type": "POST",
        "category": "SPORTS",
        "mediaUrl": null,
        "createdAt": "2026-05-27T22:59:44.395622",
        "contentValue": "Excited for the upcoming matches!"
  }
]
```
### 3. Create platform profile
* **Method:** `POST`
* **URL: `http:localhost:8080/api/users`
* **Response Payload:**
  
```json
{
  "username": "nadal",
  "isCelebrity": true
}
```

### 4. Create Follow Link
* **Method:** `POST`
* **URL:** `http://localhost:8080/api/follows`
* **Response Payload:**

```json
{
  "followerId": 1,
  "followingId": 11
}
```

### 5. Publish Content (Hybrid Router Entry)
* **Method:** `POST`
* **URL:** `http://localhost:8080/api/content`
* **Response Payload:**
```json
{
  "authorId": 3,
  "category": "TECH",
  "type": "POST",
  "contentValue": "Push/Pull hybrid compilation testing."
}
```











