# 🛒 Retail Ordering System

A full-stack retail ordering application built for a hackathon.
Users can browse products, manage carts, and place orders, while admins can manage inventory and orders.

---

## 🚀 Features

* User registration & login
* Browse products (by category & brand)
* Add to cart & place orders
* Admin can manage products, stock, and order status
* Secure APIs using JWT authentication

---

## 🛠️ Tech Stack

* **Frontend:** React.js
* **Backend:** Spring Boot (Java)
* **Database:** MySQL
* **Tools:** Postman, Maven, npm

---

## ▶️ How to Run

### 1. Database

* Create database: `retail_ordering`
* Run `database/schema.sql`

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

Runs on: `http://localhost:8080`

### 3. Frontend

```bash
cd frontend
npm install
npm start
```

Runs on: `http://localhost:3000`

---

## 📂 Structure

* `backend/` – API & business logic
* `frontend/` – UI
* `database/` – SQL scripts

---

## ⚡ Note

Frontend is partially implemented. All features are fully working via backend APIs.

---

Built for Hackathon 🚀
