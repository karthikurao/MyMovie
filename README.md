# MyMovie – Movie Ticket Booking System

## Overview
MyMovie is a full-stack movie ticket booking project built with Spring Boot and React. The current implementation focuses on dependable booking flows, practical administrative CRUD operations, and a polished landing experience for end users. Everything documented below reflects features that already exist in the repository.

## What’s Implemented

### Backend (Spring Boot)
- In-memory H2 database seeded with users, movies, theatres, screens, shows, and seats via `DataInitializer`.
- REST controllers for managing movies, theatres, shows, bookings, users, and customers.
- Server-side seat validation that rejects duplicate or already-reserved seats before a booking is persisted.
- Aggregation endpoint that summarizes bookings grouped by movie, exposing counts and revenue metrics.
- Stateless security configuration (CSRF disabled, CORS enabled) tuned for the SPA frontend; no JWT or session logic is wired yet.
- Unit tests covering service and controller layers (`src/test/java/com/moviebooking`).

### Frontend (React)
- React 18 single-page app bootstrapped with Create React App and React Router.
- Hero carousel with image preloading, thumbnail previews, and gradient overlays on the `Home` component.
- Featured movie cards with consistent poster sizing and hover states.
- Navigation, login/register, dashboard, booking, movie, theatre, and show components scaffolded for the booking flow.
- Global styling in `App.css` aligned with the hero and featured sections.

## Key Backend Endpoints

| Purpose | Method & Path |
| --- | --- |
| Register a user | `POST /api/users/register` |
| Sign in a user (basic placeholder) | `POST /api/users/signin` |
| CRUD movies | `GET/POST/PUT/DELETE /api/movies` |
| CRUD theatres | `GET/POST/PUT/DELETE /api/theatres` |
| Retrieve shows (by theatre/date) | `GET /api/shows`, `/api/shows/theatre/{theatreId}`, `/api/shows/date/{date}` |
| Create a booking with seat validation | `POST /api/bookings` |
| Booking summary grouped by movie | `GET /api/bookings/summary/movies` |

All endpoints respond with JSON and rely on the seeded data for demo usage.

## Repository Layout

```
MyMovie/
├─ pom.xml
├─ src/
│  ├─ main/java/com/moviebooking/
│  │  ├─ MovieBookingApplication.java
│  │  ├─ config/
│  │  ├─ controller/
│  │  ├─ dto/
│  │  ├─ entity/
│  │  ├─ repository/
│  │  └─ service/
│  └─ test/java/com/moviebooking/
├─ frontend/
│  ├─ package.json
│  └─ src/
│     ├─ App.js / App.css
│     ├─ index.js
│     └─ components/
└─ run-app.bat
```

## Running the Project

### Prerequisites
- Java 17+
- Node.js 16+
- Maven 3.6+

### Backend
```powershell
cd C:\Users\P12C4F0\IdeaProjects\MyMovie
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. The H2 console is available at `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:testdb`, user `sa`, password blank.

### Frontend
```powershell
cd C:\Users\P12C4F0\IdeaProjects\MyMovie\frontend
npm install
npm start
```

The React dev server runs on `http://localhost:3000`.

## Testing
```powershell
cd C:\Users\P12C4F0\IdeaProjects\MyMovie
mvn test
```

## Seeded Demo Data
- Users: `admin123` (ADMIN), `customer123` (CUSTOMER)
- Customers: John Doe, Jane Smith, Demo User
- Movies: Avengers: Endgame, Inception, The Dark Knight
- Theatres: PVR Cinemas (Mumbai), INOX (Delhi)
- Shows: Three future-dated shows mapped to the seeded movies
- Seats: Premium (A1, A2) and Regular (B1, B2) with prices

This README is intentionally limited to shipped functionality so it stays accurate as the project evolves.
