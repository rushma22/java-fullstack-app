# TechTopia 💻

TechTopia is a **full-stack Java e-commerce web application** for selling laptops online.  
Built with **Servlets, JSP, Hibernate ORM, and MySQL** inside NetBeans IDE.  
Includes user authentication, shopping cart, checkout, order history, and integrated **PayHere payment gateway**.

---

## 🚀 Features

- **User**
  - Sign up, sign in, sign out, email verification
  - Browse products, search and filter
  - Add to cart, checkout with payment gateway
  - View purchase history and account details

- **Admin**
  - Admin login
  - Add new products
  - Manage own product listings

- **General**
  - Product catalog with detailed view
  - Order placement and status tracking
  - Clean MVC structure (controllers, models, entities)
  - Hibernate ORM for database persistence

---

## 🛠️ Tech Stack

- **Backend:** Java (Servlets, JSP), Hibernate ORM  
- **Database:** MySQL  
- **Frontend:** HTML, CSS, JavaScript (Vanilla)  
- **Payment Gateway:** PayHere (sandbox/live supported)  
- **IDE:** NetBeans  

---

## 📂 Project Structure

Web Pages/
assets/ (CSS + JS)
product-images/
*.html
WEB-INF/

Source Packages/
controller/ (servlets)
hibernate/ (entities, HibernateUtil)
model/ (filters, utilities, PayHere, mail)
Util.java
hibernate.cfg.xml
