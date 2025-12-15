# **📝 Project Introduction – E-Notes Management System**

The E-Notes Management System is a full-featured digital notes platform designed to help users create, organize, store, and manage their notes and tasks efficiently. Built using Spring Boot, it provides a secure, scalable, and modular backend for real-world usage.

The system allows users to maintain text notes, documents, and categorized content, along with a powerful TODO tracking module that sends daily email reports of a user's progress. It also includes advanced features like pagination, filtering, soft delete, recycle bin, and auto-deletion of expired notes.

Security is enforced using Spring Security + JWT with role-based authentication, ensuring that every user’s data is protected, isolated, and accessible only to the authorized account.

A future enhancement includes integrating an AI-powered Chatbot that can analyze notes based on categories and assist the user by answering queries, summarizing content, and providing personalized suggestions—making note management smarter and more interactive.

## **⭐ Key Features**

### **🗂️ Notes Management**

* Create, update, and delete notes.

* Organize notes by categories.

* Attach documents/files to notes.

* Pagination & filtering for scalable browsing.

* Soft delete & recycle bin with restore support.

* Automatic permanent deletion after expiry time.

### **📎 File Management**

* Upload files/documents for each note.

* Store files locally with filename saved in DB.

* Secure download & view functionality.

* User-specific file organization.

### **🗂️ Category Management**

* Create and manage categories.

* Link notes under specific categories.

* Soft delete & restore support.

### **✔️ TODO Management**

* Full CRUD operations for tasks.

* Status tracking: CREATED, IN_PROCESS, COMPLETED.

* Soft delete & recovery.

* Daily email notifications summarizing user’s tasks for the day.

### **🔐 User & Security Module**

* User registration & login.

* Spring Security with JWT authentication.

* Role-based access control.

* Secure APIs for every module.

* Fully isolated user data.

### **🤖 (Upcoming) AI Chatbot Integration**

* Category-wise note understanding.

* Personalized responses and suggestions.

* Assist users in searching, summarizing, and managing notes.

* Enhances overall user experience.

## **🎯 Project Goal**

The goal of this project is to create a smart, secure, and user-friendly notes management system that helps individuals and teams organize their information, track tasks, and maintain digital documents effortlessly—powered by modern backend architecture and intelligent automation.

## **🚀 Tech Stack**

### **Backend**

1. [ ] Java 17
3. [ ] Spring Boot 
5. [ ] Spring Web  
7. [ ] Spring Data JPA 
9. [ ] Spring Auditing   
11. [ ] Spring Security (JWT Authentication)    
13. [ ] Spring Ai 
15. [ ] Lombok
17. [ ] Swagger (API Documentation)
19. [ ] Postman

### **Database**

1. [ ] MySQL

### **Build Tool**

1. [ ] Maven
