# MyMovie – Movie Ticket Booking Platform

## Overview
MyMovie is a full-stack ticketing platform built with Spring Boot and React. The codebase already ships with an end-to-end booking flow, seed data, JWT-based authentication, refresh-token rotation, and an admin workspace that can manage every core catalogue entity. Everything described below reflects functionality that exists in this repository today.

## Project Highlights
- Spring Boot 3 backend with SQLite persistence, seeded through `DataInitializer` so the app is usable immediately.
- React 18 single-page application styled with React Bootstrap and custom gradients for a polished landing page.
- Admin dashboard that provides full CRUD for movies, theatres, screens, and shows with inline validation and dependent selects.
- Robust booking workflow that validates seat availability server-side before confirming a ticket.
- Modern authentication stack: JWT access tokens, rotating refresh tokens, global Axios interceptors, and stateless Spring Security guards.
- Stripe-powered card payments that create PaymentIntents on the server and confirm them in the React checkout modal before persisting a booking.

## Backend Capabilities (Spring Boot)
- Portable SQLite database (`mymovie.db`) stored alongside the source; seeded with admins, customers, movies, theatres, screens, shows, seats, and sample bookings.
- REST controllers for movies, theatres, screens, shows, bookings, users, and customers.
- Seat validation in `BookingServiceImpl` rejects duplicate or already-reserved seats and enforces positive totals before persisting a booking.
- Aggregation endpoint (`GET /api/bookings/summary/movies`) that returns movie-level booking counts and revenue totals.
- JWT suite located under `config/` (`JwtTokenProvider`, `JwtAuthenticationFilter`, `SecurityConfig`, etc.) securing all protected endpoints in a stateless fashion.
- Refresh tokens persisted in SQLite (`RefreshToken` entity + repository/service layer) providing rotation and revocation support backing the `/api/users/refresh` endpoint.
- Dedicated `PaymentController` + `PaymentServiceImpl` wrapper around Stripe's Java SDK that issues PaymentIntents using the configured secret key and guards against missing configuration.
- Automated test coverage includes controller/service unit tests and an integration test (`UserControllerIntegrationTest`) that exercises sign-in plus refresh-token rotation.

## Frontend Capabilities (React SPA)
- Landing page (`Home.js`) with hero carousel, animated CTAs, featured movies section, and themed surface styles from `App.css`.
- Route scaffolding for movies, theatres, shows, booking, dashboards, login, and registration backed by React Router.
- Admin dashboard (`frontend/src/components/AdminDashboard.js`) offering modal-driven CRUD for movies, theatres, screens, shows, and booking oversight cards.
- Axios configuration module (`frontend/src/api/axiosConfig.js`) that applies base URLs, injects bearer tokens, refreshes tokens transparently, and broadcasts session-expiry events.
- Reusable datetime helpers (`frontend/src/utils/datetime.js`) ensuring UI forms send ISO-compatible timestamps to Spring.
- Navigation fixes and contrast improvements across the SPA so buttons and links remain accessible.
- `BookTicket` now opens a Stripe-powered payment modal (Card Element) that confirms a PaymentIntent before the booking API is called, with graceful error handling and payment reference surfacing in the success dialog.

## Authentication Flow
- `POST /api/users/signin` and `POST /api/users/register` return JWT access tokens, refresh tokens, expiry metadata, and user role details.
- Axios interceptor listens for `401` responses, queues pending requests, rotates refresh tokens through `/api/users/refresh`, and clears local storage when the session is no longer valid.
- Spring Security runs in stateless mode, using `JwtAuthenticationFilter` + `JwtAuthenticationEntryPoint` to authorize requests and return consistent JSON error payloads.

## Admin Workspace
- Movies: add/edit/delete titles, genres, languages, durations, descriptions, and poster image URLs.
- Theatres: maintain theatre metadata and validate manager contact numbers (7–15 digits).
- Screens: manage per-theatre seating grids (rows/columns must be numeric) and cascade updates in the UI when a theatre is removed.
- Shows: enforce dependent selections (theatre → screen), prevent mismatched theatre/screen combinations, and validate that end times follow start times.
- Bookings: view recent transactions, revenue totals, and cancel bookings directly from the dashboard.

## Seeded Credentials
- Admin: `admin@mymovie.com` / `admin123`
- Customers: `john@mymovie.com`, `jane@mymovie.com`, `demo@mymovie.com` with their respective passwords in `DataInitializer`.

## Repository Layout
```
MyMovie/
├─ pom.xml
├─ run-app.bat
├─ mymovie.db (created on first run; portable SQLite store)
├─ src/
│  ├─ main/java/com/moviebooking/
│  │  ├─ MovieBookingApplication.java
│  │  ├─ config/        # Security, JWT, data init
│  │  ├─ controller/    # REST endpoints
│  │  ├─ dto/           # Request/response records
│  │  ├─ entity/        # JPA entities (incl. RefreshToken)
│  │  ├─ exception/
│  │  ├─ repository/
│  │  └─ service/
│  └─ test/java/com/moviebooking/
│     └─ controller/UserControllerIntegrationTest.java
└─ frontend/
   ├─ package.json
   ├─ run-app scripts (npm)
   └─ src/
      ├─ index.js / App.js / App.css
      ├─ api/axiosConfig.js
      ├─ utils/datetime.js
      └─ components/
         └─ AdminDashboard.js, Home.js, Movies.js, Shows.js, etc.
```

## Running the Project
### Prerequisites
- Java 17 or later
- Maven 3.6+
- Node.js 16+

### Backend
```powershell
cd C:\Users\P12C4F0\IdeaProjects\MyMovie
mvn spring-boot:run
```
The API serves on `http://localhost:8080`.

### Frontend
```powershell
cd C:\Users\P12C4F0\IdeaProjects\MyMovie\frontend
npm install
npm start
```
The React dev server runs on `http://localhost:3000` and proxies API calls to the backend.

### Stripe Setup
1. Create a copy of `frontend/.env.example` named `.env` and paste your Stripe publishable key:
   ```env
   REACT_APP_STRIPE_PUBLISHABLE_KEY=pk_test_...
   ```
   Restart the React dev server after editing the `.env` file.
2. Expose your Stripe secret key to the backend by either setting the `STRIPE_SECRET_KEY` environment variable or updating `src/main/resources/application.properties` (`stripe.secret-key=sk_test_...`).
3. Optional: update `stripe.currency` in `application.properties` if you plan to charge in a currency other than INR.

### Stripe Test Card Details
Use these test card numbers for payment testing:

**Successful Payments:**
- **Visa**: `4242424242424242`
- **Visa (debit)**: `4000056655665556`
- **Mastercard**: `5555555555554444`
- **American Express**: `378282246310005`

**Declined Payments:**
- **Generic decline**: `4000000000000002`
- **Insufficient funds**: `4000000000009995`
- **Lost card**: `4000000000009987`
- **Stolen card**: `4000000000009979`

**3D Secure Authentication:**
- **Authentication required**: `4000002500003155`
- **Authentication fails**: `4000008400001629`

**Additional Test Details:**
- **Expiry Date**: Use any future date (e.g., `12/34`)
- **CVC**: Use any 3-digit number (4 digits for Amex)
- **Name**: Use any name
- **ZIP/Postal Code**: Use any valid format

For more test scenarios, visit: https://stripe.com/docs/testing#cards

## Testing
```powershell
cd C:\Users\P12C4F0\IdeaProjects\MyMovie
mvn test
```
`UserControllerIntegrationTest` spins up the application with an in-memory SQLite database and asserts that access tokens and refresh tokens rotate correctly.

## API Reference

### Authentication Endpoints
| Endpoint | Method | Description | Request Body |
| --- | --- | --- | --- |
| `/api/users/register` | POST | Register new user/customer | `{email, password, role}` |
| `/api/users/signin` | POST | Authenticate and get tokens | `{email, password}` |
| `/api/users/refresh` | POST | Refresh access token | `{refreshToken}` |
| `/api/users/signout` | POST | Revoke refresh token | `{email, userId, role}` |

### Movie Management
| Endpoint | Method | Description | Request Body |
| --- | --- | --- | --- |
| `/api/movies` | GET | Get all movies | - |
| `/api/movies/{id}` | GET | Get movie by ID | - |
| `/api/movies` | POST | Create new movie | `{movieName, movieGenre, movieHours, language, description, imageUrl}` |
| `/api/movies/{id}` | PUT | Update movie | Movie object |
| `/api/movies/{id}` | DELETE | Delete movie | - |

### Theatre & Screen Management
| Endpoint | Method | Description | Request Body |
| --- | --- | --- | --- |
| `/api/theatres` | GET | Get all theatres | - |
| `/api/theatres/{id}` | GET | Get theatre by ID | - |
| `/api/theatres` | POST | Create theatre | `{theatreName, theatreCity, managerName, managerContact}` |
| `/api/theatres/{id}` | PUT | Update theatre | Theatre object |
| `/api/theatres/{id}` | DELETE | Delete theatre | - |
| `/api/screens` | GET | Get all screens | - |
| `/api/screens/theatre/{theatreId}` | GET | Get screens by theatre | - |
| `/api/screens` | POST | Create screen | `{theatreId, screenName, rows, columns}` |

### Show Management
| Endpoint | Method | Description | Request Body |
| --- | --- | --- | --- |
| `/api/shows` | GET | Get all shows | - |
| `/api/shows/{id}` | GET | Get show by ID | - |
| `/api/shows` | POST | Create show | `{showStartTime, showEndTime, showName, screenId, theatreId, movieId}` |
| `/api/shows/{id}` | PUT | Update show | Show object |
| `/api/shows/{id}` | DELETE | Delete show | - |
| `/api/shows/theatre/{theatreId}` | GET | Get shows by theatre | - |

### Booking & Payment
| Endpoint | Method | Description | Request Body |
| --- | --- | --- | --- |
| `/api/bookings` | GET | Get all bookings (Admin) | - |
| `/api/bookings` | POST | Create new booking | `{showId, customerId, seatNumbers[], totalCost, paymentIntentId}` |
| `/api/bookings/customer/{customerId}` | GET | Get customer's tickets | - |
| `/api/bookings/summary/movies` | GET | Get booking summary by movie | - |
| `/api/payments/create-intent` | POST | Create Stripe PaymentIntent | `{amount, currency, receiptEmail, description}` |

### Customer Management
| Endpoint | Method | Description | Request Body |
| --- | --- | --- | --- |
| `/api/customers` | GET | Get all customers | - |
| `/api/customers/{id}` | GET | Get customer by ID | - |
| `/api/customers` | POST | Create customer | `{customerName, address, mobileNumber, email, password}` |

## Database Management Tips
- The SQLite database is generated automatically and persists data across restarts; delete `mymovie.db` to reset to seed data.
- Use tools like DB Browser for SQLite or DBeaver to inspect or modify the data file.
- Back up your environment by copying the database file—no separate export scripts required.

## Recent Milestones
- Migrated persistence from H2 to SQLite for portability and realistic testing data.
- Added movie poster URL management across backend entity, REST API, and admin UI.
- Improved SPA accessibility/contrast and fixed router navigation issues in theatres listing.
- Delivered admin CRUD for theatres, screens, and shows with dependency-aware validation.
- Implemented JWT authentication, refresh-token rotation, Axios request queueing, and centralized session expiry handling.
