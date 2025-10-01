package com.moviebooking.config;

import com.moviebooking.entity.*;
import com.moviebooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private IMovieRepository movieRepository;

    @Autowired
    private ITheatreRepository theatreRepository;

    @Autowired
    private IScreenRepository screenRepository;

    @Autowired
    private ISeatRepository seatRepository;

    @Autowired
    private IShowRepository showRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        // Initialize Users
        if (userRepository.count() == 0) {
            User admin = new User("admin123", "ADMIN");
            User customer = new User("customer123", "CUSTOMER");
            userRepository.saveAll(Arrays.asList(admin, customer));
        }

        // Initialize Customers
        if (customerRepository.count() == 0) {
            Customer customer1 = new Customer("John Doe", "123 Main St, New York", "1234567890", "john@mymovie.com", "password123");
            Customer customer2 = new Customer("Jane Smith", "456 Oak Ave, Los Angeles", "0987654321", "jane@mymovie.com", "password123");
            Customer customer3 = new Customer("Demo User", "789 Demo Street, Demo City", "5555555555", "demo@mymovie.com", "demo123");
            customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3));
        }

        // Initialize Movies
        if (movieRepository.count() == 0) {
            Movie movie1 = new Movie("Avengers: Endgame", "Action", "3h 1m", "English", "Epic superhero movie");
            Movie movie2 = new Movie("Inception", "Sci-Fi", "2h 28m", "English", "Mind-bending thriller");
            Movie movie3 = new Movie("The Dark Knight", "Action", "2h 32m", "English", "Batman superhero movie");
            movieRepository.saveAll(Arrays.asList(movie1, movie2, movie3));
        }

        // Initialize Theatres
        if (theatreRepository.count() == 0) {
            Theatre theatre1 = new Theatre("PVR Cinemas", "Mumbai", "Raj Sharma", "9876543210");
            Theatre theatre2 = new Theatre("INOX", "Delhi", "Priya Patel", "8765432109");
            theatreRepository.saveAll(Arrays.asList(theatre1, theatre2));
        }

        // Initialize Screens
        if (screenRepository.count() == 0) {
            Screen screen1 = new Screen(1, "Screen 1", 10, 15);
            Screen screen2 = new Screen(1, "Screen 2", 8, 12);
            Screen screen3 = new Screen(2, "Screen 1", 12, 18);
            screenRepository.saveAll(Arrays.asList(screen1, screen2, screen3));
        }

        // Initialize Shows
        if (showRepository.count() == 0) {
            Show show1 = new Show(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(3), "Morning Show", 1, 1);
            show1.setMovieId(1); // Set movieId explicitly

            Show show2 = new Show(LocalDateTime.now().plusDays(1).plusHours(4), LocalDateTime.now().plusDays(1).plusHours(7), "Afternoon Show", 2, 1);
            show2.setMovieId(2); // Set movieId explicitly

            Show show3 = new Show(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(3), "Evening Show", 1, 2);
            show3.setMovieId(3); // Set movieId explicitly

            showRepository.saveAll(Arrays.asList(show1, show2, show3));
        }

        // Initialize Seats
        if (seatRepository.count() == 0) {
            Seat seat1 = new Seat("A1", "Premium", 300.0);
            Seat seat2 = new Seat("A2", "Premium", 300.0);
            Seat seat3 = new Seat("B1", "Regular", 200.0);
            Seat seat4 = new Seat("B2", "Regular", 200.0);
            seatRepository.saveAll(Arrays.asList(seat1, seat2, seat3, seat4));
        }
    }
}
