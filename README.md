# 🚚 SmartCourier Delivery Management System

A full-stack microservices-based web application designed to manage courier and parcel delivery efficiently. The system allows users to create delivery requests, track shipments in real time, and enables admins to monitor and manage operations.

---

## 📌 Project Overview

SmartCourier is built using a scalable **Spring Boot Microservices Architecture** with an **API Gateway**. It supports domestic, express, and international deliveries with real-time tracking and secure authentication.

---

## 🎯 Objectives

- Digitize parcel booking and delivery lifecycle
- Improve tracking accuracy and transparency
- Reduce manual coordination with automation
- Secure APIs using JWT Authentication
- Demonstrate enterprise-level microservices design

---

## 👥 User Roles

### 👤 Customer
- Register/Login
- Create delivery requests
- Schedule pickup
- Upload documents
- Track deliveries

### 🛠️ Admin
- Monitor parcel movement
- Handle delivery issues
- Manage users & hubs
- Generate reports

---

## 🔐 Security

- JWT-based Authentication
- Role-based Authorization (`ADMIN`, `CUSTOMER`)
- Angular Route Guards
- API Gateway security routing

---


### Microservices:

- Auth Service (Authentication & JWT)
- Delivery Service (Order management)
- Tracking Service (Tracking & documents)
- Admin Service (Monitoring & reports)

---

## 🖥️ Frontend (Angular)

Modules:
- Core Module
- Shared Module
- Auth Module
- Customer Module
- Delivery Module
- Admin Module
- Reports Module

Routing:
- `/auth/login`, `/auth/signup`
- `/customer/dashboard`
- `/admin/dashboard`

---

## 🔄 Delivery Lifecycle
Draft → Booked → Picked Up → In Transit → Out for Delivery → Delivered

Exception States:
- Delayed
- Failed
- Returned

---

## 🔗 API Endpoints (Gateway)

- `/gateway/auth/*`
- `/gateway/deliveries/*`
- `/gateway/tracking/*`
- `/gateway/admin/*`

---

## 🗄️ Database Design

Entities:
- User
- Delivery
- Package
- Address
- TrackingEvent
- Document
- DeliveryProof
- Report

Each microservice uses its own database schema.

---

## ⚙️ Tech Stack

**Frontend:**
- Angular
- TypeScript
- HTML/CSS

**Backend:**
- Java 17+
- Spring Boot
- Spring Cloud Gateway
- JPA / Hibernate

**Database:**
- MySQL / PostgreSQL

**Tools:**
- Eclipse / IntelliJ
- Postman
- Swagger

---

## 🚀 How to Run

1. Clone the repository  
2. Open backend services in Eclipse/IntelliJ  
3. Run each microservice  
4. Start API Gateway  
5. Run Angular frontend  
6. Access application at:  
   `http://localhost:4200`

---

## 🔁 Microservices Communication

Microservices communicate using REST APIs via the API Gateway. Feign Client can be used to simplify inter-service communication.

---

## 🧪 Testing

- Unit Testing using JUnit & Mockito
- Controller testing
- Code coverage ≥ 80%

---

## ⚠️ Challenges Faced

- Managing communication between microservices
- Handling real-time tracking updates
- Debugging runtime and system issues

---






