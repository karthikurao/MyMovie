# MyMovie – Movie Ticket Booking System

## Overview
MyMovie is a full-stack movie ticket booking project built with Spring Boot and React. The current implementation focuses on dependable booking flows, practical administrative CRUD operations, and a polished landing experience for end users. Everything documented below reflects features that already exist in the repository.

## What's Implemented

### Backend (Spring Boot)
- **Portable SQLite database** that persists data across restarts, seeded with users, movies, theatres, screens, shows, and seats via `DataInitializer`.
- REST controllers for managing movies, theatres, shows, bookings, users, and customers.
- Server-side seat validation that rejects duplicate or already-reserved seats before a booking is persisted.
- Aggregation endpoint that summarizes bookings grouped by movie, exposing counts and revenue metrics.
- Stateless security configuration (CSRF disabled, CORS enabled) tuned for the SPA frontend; no JWT or session logic is wired yet.
- Unit tests covering service and controller layers (`src/test/java/com/moviebooking`).
- **Image URL support** for movies - admins can easily add/edit movie poster images via URL instead of file uploads.

### Frontend (React)
- React 18 single-page app bootstrapped with Create React App and React Router.
- Hero carousel with image preloading, thumbnail previews, and gradient overlays on the `Home` component.
- Featured movie cards displaying images from database with consistent poster sizing and hover states.
- Admin dashboard with image URL field for easy movie poster management.
- Navigation, login/register, dashboard, booking, movie, theatre, and show components scaffolded for the booking flow.
- Fixed navigation buttons in Theatres page using proper React Router navigation.
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
├─ mymovie.db (SQLite database - created automatically)
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

The API starts on `http://localhost:8080`. 

**Note:** The SQLite database file (`mymovie.db`) will be automatically created in the project root directory on first run. This file persists all your data across application restarts, making it portable - you can copy the entire project folder to another machine and your data will be preserved!

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

## Database Management

### SQLite Benefits
- **Portable**: The entire database is a single file (`mymovie.db`) that you can backup, share, or move easily
- **No setup required**: Unlike MySQL or PostgreSQL, SQLite requires no separate database server
- **Perfect for development**: Lightweight and fast for local development and testing
- **Easy backup**: Just copy the `mymovie.db` file to create a backup

### Viewing/Editing Database
You can use any SQLite browser tool to view and edit the database:
- **DB Browser for SQLite** (https://sqlitebrowser.org/) - Free, cross-platform GUI
- **DBeaver** (https://dbeaver.io/) - Universal database tool
- **SQLite command-line** - Pre-installed on most systems

### Resetting Database
To reset the database to initial seeded data:
1. Stop the application
2. Delete the `mymovie.db` file
3. Restart the application - the database will be recreated with fresh seed data

## Seeded Demo Data & Login Credentials

### Admin Login
- **Email**: `admin@mymovie.com`
- **Password**: `admin123`
- **Role**: ADMIN

### Customer Logins
- **John Doe**: `john@mymovie.com` / `password123`
- **Jane Smith**: `jane@mymovie.com` / `password123`  
- **Demo User**: `demo@mymovie.com` / `demo123`

## Recent Updates

### Database Migration (October 2025)
- ✅ Migrated from H2 in-memory database to SQLite file-based database
- ✅ All data now persists across application restarts
- ✅ Database file is portable and can be easily backed up or shared

### Movie Image Management
- ✅ Added `imageUrl` field to Movie entity for easy poster management
- ✅ Admin dashboard now includes image URL input field
- ✅ Movies display custom poster images from URLs
- ✅ Fallback to genre-based placeholder images if URL not provided

### UI Improvements
- ✅ Fixed "View Shows" button in Theatres page to use proper React Router navigation
- ✅ Enhanced admin panel with edit functionality for movie images
