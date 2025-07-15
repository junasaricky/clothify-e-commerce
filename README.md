# Clothify E-Commerce

Clothify is a full-stack e-commerce web application built with Angular 17 on the frontend and Spring Boot on the backend. It allows users to browse clothing products, manage carts, place orders, and perform account operations. Admins can manage inventory and view customer orders. The application includes PayMongo-powered GCash and Card payment flows (test mode), with backend-integrated order processing and session handling.

## Features

###  User-Side Features
- User registration, login, and logout
- Profile update and password change
- Product listing with:
  - Discounts
  - Real-time search
- Add to Cart and Buy Now functionality
- Checkout with GCash or Card (via PayMongo - test mode)
- Address selection during checkout
- Order history with:
  - Status badges (PENDING, PROCESSING, PACKED, OUT_FOR_DELIVERY, DELIVERED)
  - Address and payment details

###  Admin-Side Features
- Login with admin credentials
- Product management (CRUD)
- View all orders placed by customers
- View user list and account details
- View monthy orders metrics in dashboard format

###  Technical Highlights
- Angular 17 with modular structure and responsive layout
- Spring Boot REST API with JPA/Hibernate
- PostgreSQL (via Neon) for persistent data storage
- Payment integration using PayMongo API (test mode only)
- Checkout session creation with real-time redirect logic
- Session management using sessionStorage and Spring Security JWT auth
- Email notifications via SMTP
- Fully responsive design for desktop and mobile views
- Deployed on Render (frontend and backend)

## Technologies Used

- **Frontend**: Angular 17, SCSS, TypeScript
- **Backend**: Java (Spring Boot), Spring Security
- **Database**: PostgreSQL (Neon)
- **Payment**: PayMongo (GCash & Card - test mode)
- **Email**: SMTP (Spring Mail)
- **Session**: Browser `sessionStorage` and JWT for auth
- **Hosting**: Render (Production-ready deployment)
- **Build Tools**: Maven (backend), Angular CLI (frontend)

## Project Setup

### Backend
1. Clone the repository and navigate to the backend directory.
2. Configure your `application.properties`:
   - Set PostgreSQL Neon credentials (`DB_USERNAME`, `DB_PASSWORD`)
   - Set SMTP credentials (`MAIL_USERNAME`, `MAIL_PASSWORD`)
   - Set PayMongo secret key (`paymongo.secret.key`)
3. Run the application using your IDE or `mvn spring-boot:run`.

### Frontend
1. Navigate to the Angular project directory.
2. Install dependencies:
   ```bash
   npm install

##  Screenshots

### Admin Interface
![Dashboard](./screenshots/Admin%20Interface/admin-dashboard.png)
![Products](./screenshots/Admin%20Interface/product-listing.png)
![Orders](./screenshots/Admin%20Interface/orders.png)
![Settings](./screenshots/Admin%20Interface/admin-settings.png)
![Users](./screenshots/Admin%20Interface/users-listing-account.png)

### User Interface
![Shop](./screenshots/User%20Interface/user-shop.png)
![Cart](./screenshots/User%20Interface/user-cart.png)
![Checkout](./screenshots/User%20Interface/user-checkout-preview.png)
![Payment-Gcash](./screenshots/User%20Interface/user-gcash-payment.png)
![Payment-Card](./screenshots/User%20Interface/user-card-payment.png)
![Thank You](./screenshots/User%20Interface/user-thank_you-message.png)
![Orders](./screenshots/User%20Interface/user-orders.png)
![Settings](./screenshots/User%20Interface/user-settings.png)
![Account](./screenshots/User%20Interface/user-account.png)
