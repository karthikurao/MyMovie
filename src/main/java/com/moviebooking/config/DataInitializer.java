package com.moviebooking.config;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.moviebooking.entity.Customer;
import com.moviebooking.entity.Movie;
import com.moviebooking.entity.Screen;
import com.moviebooking.entity.Seat;
import com.moviebooking.entity.Show;
import com.moviebooking.entity.Theatre;
import com.moviebooking.entity.User;
import com.moviebooking.repository.ICustomerRepository;
import com.moviebooking.repository.IMovieRepository;
import com.moviebooking.repository.IScreenRepository;
import com.moviebooking.repository.ISeatRepository;
import com.moviebooking.repository.IShowRepository;
import com.moviebooking.repository.ITheatreRepository;
import com.moviebooking.repository.IUserRepository;

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
            Movie movie1 = new Movie("Avengers: Endgame", "Action", "3h 1m", "English", "Epic superhero movie where the Avengers face their final battle");
            Movie movie2 = new Movie("Inception", "Sci-Fi", "2h 28m", "English", "Mind-bending thriller about dreams within dreams");
            Movie movie3 = new Movie("The Dark Knight", "Action", "2h 32m", "English", "Batman faces his greatest challenge against the Joker");
            Movie movie4 = new Movie("Interstellar", "Sci-Fi", "2h 49m", "English", "A father's journey through space to save humanity");
            Movie movie5 = new Movie("The Lion King", "Animation", "1h 58m", "English", "The classic tale of Simba's journey to become king");
            Movie movie6 = new Movie("Spider-Man: No Way Home", "Action", "2h 28m", "English", "Spider-Man faces villains from across the multiverse");
            Movie movie7 = new Movie("Dune", "Sci-Fi", "2h 35m", "English", "Paul Atreides leads a rebellion on the desert planet Arrakis");
            Movie movie8 = new Movie("Top Gun: Maverick", "Action", "2h 11m", "English", "Maverick returns to train a new generation of pilots");
            Movie movie9 = new Movie("Black Panther", "Action", "2h 14m", "English", "T'Challa becomes the king of Wakanda and Black Panther");
            Movie movie10 = new Movie("Avatar", "Sci-Fi", "2h 42m", "English", "A paraplegic marine explores the alien world of Pandora");
            Movie movie11 = new Movie("Joker", "Drama", "2h 2m", "English", "The origin story of Batman's most iconic villain");
            Movie movie12 = new Movie("Fast & Furious 9", "Action", "2h 23m", "English", "Dom and his crew face their most dangerous mission yet");
            Movie movie13 = new Movie("Wonder Woman", "Action", "2h 21m", "English", "Diana Prince discovers her full powers and true destiny");
            Movie movie14 = new Movie("Aquaman", "Action", "2h 23m", "English", "Arthur Curry learns to become the King of Atlantis");
            Movie movie15 = new Movie("Doctor Strange", "Action", "1h 55m", "English", "A surgeon becomes a powerful sorcerer after a car accident");
            Movie movie16 = new Movie("Guardians of the Galaxy", "Action", "2h 1m", "English", "A group of unlikely heroes team up to save the galaxy");
            Movie movie17 = new Movie("Thor: Ragnarok", "Action", "2h 10m", "English", "Thor must escape imprisonment to save Asgard from destruction");
            Movie movie18 = new Movie("Captain Marvel", "Action", "2h 3m", "English", "Carol Danvers becomes one of the universe's most powerful heroes");
            Movie movie19 = new Movie("The Matrix", "Sci-Fi", "2h 16m", "English", "A computer hacker discovers the shocking truth about reality");
            Movie movie20 = new Movie("John Wick", "Action", "1h 41m", "English", "An ex-hitman comes out of retirement to track down the gangsters");
            Movie movie21 = new Movie("Parasite", "Thriller", "2h 12m", "Korean", "A poor family infiltrates the lives of a wealthy household");
            Movie movie22 = new Movie("1917", "War", "1h 59m", "English", "Two British soldiers race against time to deliver a crucial message");
            Movie movie23 = new Movie("Once Upon a Time in Hollywood", "Drama", "2h 41m", "English", "A fading actor and his stunt double navigate 1969 Hollywood");
            Movie movie24 = new Movie("Ford v Ferrari", "Drama", "2h 32m", "English", "The story of Ford's attempt to beat Ferrari at Le Mans");

            movieRepository.saveAll(Arrays.asList(
                    movie1, movie2, movie3, movie4, movie5, movie6, movie7, movie8,
                    movie9, movie10, movie11, movie12, movie13, movie14, movie15, movie16,
                    movie17, movie18, movie19, movie20, movie21, movie22, movie23, movie24
            ));
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
