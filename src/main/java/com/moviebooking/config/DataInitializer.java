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
            Movie movie1 = new Movie("Avengers: Endgame", "Action", "3h 1m", "English", "Epic superhero movie where the Avengers face their final battle", "https://images.unsplash.com/photo-1635805737707-575885ab0820?w=500");
            Movie movie2 = new Movie("Inception", "Sci-Fi", "2h 28m", "English", "Mind-bending thriller about dreams within dreams", "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=500");
            Movie movie3 = new Movie("The Dark Knight", "Action", "2h 32m", "English", "Batman faces his greatest challenge against the Joker", "https://images.unsplash.com/photo-1509347528160-9a9e33742cdb?w=500");
            Movie movie4 = new Movie("Interstellar", "Sci-Fi", "2h 49m", "English", "A father's journey through space to save humanity", "https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=500");
            Movie movie5 = new Movie("The Lion King", "Animation", "1h 58m", "English", "The classic tale of Simba's journey to become king", "https://images.unsplash.com/photo-1556103255-4443dbae8e5a?w=500");
            Movie movie6 = new Movie("Spider-Man: No Way Home", "Action", "2h 28m", "English", "Spider-Man faces villains from across the multiverse", "https://images.unsplash.com/photo-1635805737707-575885ab0820?w=500");
            Movie movie7 = new Movie("Dune", "Sci-Fi", "2h 35m", "English", "Paul Atreides leads a rebellion on the desert planet Arrakis", "https://images.unsplash.com/photo-1534809027769-b00d750a6bac?w=500");
            Movie movie8 = new Movie("Top Gun: Maverick", "Action", "2h 11m", "English", "Maverick returns to train a new generation of pilots", "https://images.unsplash.com/photo-1583939003579-730e3918a45a?w=500");
            Movie movie9 = new Movie("Black Panther", "Action", "2h 14m", "English", "T'Challa becomes the king of Wakanda and Black Panther", "https://images.unsplash.com/photo-1608889825103-eb5ed706fc64?w=500");
            Movie movie10 = new Movie("Avatar", "Sci-Fi", "2h 42m", "English", "A paraplegic marine explores the alien world of Pandora", "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=500");
            Movie movie11 = new Movie("Joker", "Drama", "2h 2m", "English", "The origin story of Batman's most iconic villain", "https://images.unsplash.com/photo-1590979960110-4de42eb9172d?w=500");
            Movie movie12 = new Movie("Fast & Furious 9", "Action", "2h 23m", "English", "Dom and his crew face their most dangerous mission yet", "https://images.unsplash.com/photo-1542282088-fe8426682b8f?w=500");
            Movie movie13 = new Movie("Wonder Woman", "Action", "2h 21m", "English", "Diana Prince discovers her full powers and true destiny", "https://images.unsplash.com/photo-1560169897-fc0cdbdfa4d5?w=500");
            Movie movie14 = new Movie("Aquaman", "Action", "2h 23m", "English", "Arthur Curry learns to become the King of Atlantis", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500");
            Movie movie15 = new Movie("Doctor Strange", "Action", "1h 55m", "English", "A surgeon becomes a powerful sorcerer after a car accident", "https://images.unsplash.com/photo-1616530940355-351fabd9524b?w=500");
            Movie movie16 = new Movie("Guardians of the Galaxy", "Action", "2h 1m", "English", "A group of unlikely heroes team up to save the galaxy", "https://images.unsplash.com/photo-1608889476561-6242cfdbf622?w=500");
            Movie movie17 = new Movie("Thor: Ragnarok", "Action", "2h 10m", "English", "Thor must escape imprisonment to save Asgard from destruction", "https://images.unsplash.com/photo-1626814026160-2237a95fc5a0?w=500");
            Movie movie18 = new Movie("Captain Marvel", "Action", "2h 3m", "English", "Carol Danvers becomes one of the universe's most powerful heroes", "https://images.unsplash.com/photo-1518676590629-3dcbd9c5a5c9?w=500");
            Movie movie19 = new Movie("The Matrix", "Sci-Fi", "2h 16m", "English", "A computer hacker discovers the shocking truth about reality", "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=500");
            Movie movie20 = new Movie("John Wick", "Action", "1h 41m", "English", "An ex-hitman comes out of retirement to track down the gangsters", "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=500");
            Movie movie21 = new Movie("Parasite", "Thriller", "2h 12m", "Korean", "A poor family infiltrates the lives of a wealthy household", "https://images.unsplash.com/photo-1489599856772-16c0924af999?w=500");
            Movie movie22 = new Movie("1917", "War", "1h 59m", "English", "Two British soldiers race against time to deliver a crucial message", "https://images.unsplash.com/photo-1574267432644-f610a4ab6a9c?w=500");
            Movie movie23 = new Movie("Once Upon a Time in Hollywood", "Drama", "2h 41m", "English", "A fading actor and his stunt double navigate 1969 Hollywood", "https://images.unsplash.com/photo-1485846234645-a62644f84728?w=500");
            Movie movie24 = new Movie("Ford v Ferrari", "Drama", "2h 32m", "English", "The story of Ford's attempt to beat Ferrari at Le Mans", "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=500");

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
