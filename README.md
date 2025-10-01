# MyMovie - Movie Ticket Booking Application

## Overview
A comprehensive online movie ticket booking system built with Spring Boot (backend) and React (frontend), following Test-Driven Development (TDD) approach.

## Features

### Customer Features
- User registration and authentication
- Browse movies, theatres, and shows
- Book tickets with seat selection
- Immediate seat availability validation during checkout
- View booking history and status
- Real-time seat availability

### Admin Features
- Movie management (CRUD operations)
- Theatre management
- Show scheduling
- Booking analytics and reports
- User management

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.1.5
- **Database**: H2 (development), MySQL (production)
- **ORM**: JPA with Hibernate
- **Security**: Spring Security with JWT
- **Testing**: JUnit 5, Mockito
- **Build Tool**: Maven

### Frontend
- **Framework**: React 18.2.0
- **Routing**: React Router DOM
- **UI Framework**: React Bootstrap
- **HTTP Client**: Axios
- **Package Manager**: npm

## Project Structure

```
MyMovie/
├── src/main/java/com/moviebooking/
│   ├── MovieBookingApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── DataInitializer.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── Customer.java
│   │   ├── Admin.java
│   │   ├── Movie.java
│   │   ├── Theatre.java
│   │   ├── Screen.java
│   │   ├── Show.java
│   │   ├── Seat.java
│   │   ├── Ticket.java
│   │   └── TicketBooking.java
│   ├── repository/
│   │   ├── IUserRepository.java
│   │   ├── ICustomerRepository.java
│   │   ├── IMovieRepository.java
│   │   ├── ITheatreRepository.java
│   │   ├── IShowRepository.java
│   │   ├── ISeatRepository.java
│   │   └── IBookingRepository.java
│   ├── service/
│   │   ├── interfaces/
│   │   └── impl/
│   └── controller/
├── src/test/java/com/moviebooking/
│   ├── service/
│   └── controller/
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── Navigation.js
│   │   │   ├── Home.js
│   │   │   ├── Movies.js
│   │   │   ├── Theatres.js
│   │   │   ├── Shows.js
│   │   │   ├── BookTicket.js
│   │   │   ├── Login.js
│   │   │   ├── Register.js
│   │   │   ├── CustomerDashboard.js
│   │   │   └── AdminDashboard.js
│   │   ├── App.js
│   │   ├── index.js
│   │   ├── App.css
│   │   └── index.css
│   ├── public/
│   └── package.json
└── pom.xml
```

## API Endpoints

### User Management
- `POST /api/users/register` - User registration
- `POST /api/users/signin` - User login
- `POST /api/users/signout` - User logout

### Customer Management
- `GET /api/customers/{id}` - Get customer details
- `POST /api/customers` - Create customer
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer

### Movie Management
- `GET /api/movies` - Get all movies
- `GET /api/movies/{id}` - Get movie by ID
- `POST /api/movies` - Add new movie
- `PUT /api/movies/{id}` - Update movie
- `DELETE /api/movies/{id}` - Delete movie

### Theatre Management
- `GET /api/theatres` - Get all theatres
- `GET /api/theatres/{id}` - Get theatre by ID
- `GET /api/theatres/city/{city}` - Get theatres by city
- `POST /api/theatres` - Add new theatre
- `PUT /api/theatres/{id}` - Update theatre
- `DELETE /api/theatres/{id}` - Delete theatre

### Show Management
- `GET /api/shows` - Get all shows
- `GET /api/shows/{id}` - Get show by ID
- `GET /api/shows/theatre/{theatreId}` - Get shows by theatre
- `GET /api/shows/date/{date}` - Get shows by date
- `POST /api/shows` - Add new show
- `PUT /api/shows/{id}` - Update show
- `DELETE /api/shows/{id}` - Delete show

### Booking Management
- `GET /api/bookings` - Get all bookings
- `GET /api/bookings/movie/{movieId}` - Get bookings by movie
- `GET /api/bookings/show/{showId}` - Get bookings by show
- `GET /api/bookings/date/{date}` - Get bookings by date
- `GET /api/bookings/summary/movies` - Get aggregate booking metrics grouped by movie
- `POST /api/bookings` - Create booking
- `PUT /api/bookings/{id}` - Update booking
- `DELETE /api/bookings/{id}` - Cancel booking

## Database Schema

The application uses the following main entities:

1. **User** - Authentication and role management
2. **Customer** - Customer profile information
3. **Admin** - Administrator details
4. **Movie** - Movie information and metadata
5. **Theatre** - Theatre details and location
6. **Screen** - Theatre screens with seating configuration
7. **Show** - Movie showings with timing and theatre
8. **Seat** - Individual seat information and pricing
9. **Ticket** - Ticket details with seat allocation
10. **TicketBooking** - Booking transaction details

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- Maven 3.6 or higher

### Backend Setup
1. Clone the repository
2. Navigate to the project root
3. Run `mvn clean install`
4. Run `mvn spring-boot:run`
5. Backend will start on `http://localhost:8080`

### Frontend Setup
1. Navigate to the `frontend` directory
2. Run `npm install`
3. Run `npm start`
4. Frontend will start on `http://localhost:3000`

### Database Access
- H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Testing

The application follows TDD approach with comprehensive test coverage:

### Unit Tests
- Service layer tests with Mockito
- Repository layer tests
- Controller layer tests with MockMvc

### Running Tests
```bash
mvn test
```

## Key Features Implemented

1. **Modular Architecture** - Clean separation of concerns
2. **Security** - JWT-based authentication and authorization
3. **Data Validation** - Input validation with Bean Validation
4. **Error Handling** - Centralized exception handling
5. **Responsive UI** - Mobile-friendly React interface
6. **Real-time Updates** - Dynamic seat selection and booking
7. **Role-based Access** - Different interfaces for customers and admins
8. **Data Persistence** - JPA entities with proper relationships
9. **Seat Conflict Prevention** - Server-side validation to prevent double booking
10. **Movie Analytics** - Booking summary endpoint grouped by movie

## Default Test Data

The application initializes with sample data:
- Admin user (ID: 1, password: admin123)
- Customer user (ID: 2, password: customer123)
- Sample movies (Avengers, Inception, The Dark Knight)
- Sample theatres (PVR Cinemas Mumbai, INOX Delhi)
- Sample customers and seats

## Future Enhancements

- Payment gateway integration
- Email notifications
- Advanced seat selection with pricing tiers
- Movie recommendations
- Reviews and ratings
- Mobile application
- Integration with external movie databases

This comprehensive Movie Ticket Booking Application demonstrates modern web development practices with a full-stack implementation following industry standards and best practices.
