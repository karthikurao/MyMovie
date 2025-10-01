package com.moviebooking.service;

import com.moviebooking.entity.Movie;
import com.moviebooking.repository.IMovieRepository;
import com.moviebooking.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Movie Service Tests")
public class MovieServiceTest {

    @Mock
    private IMovieRepository movieRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie testMovie;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testMovie = new Movie("Avengers", "Action", "3h", "English", "Marvel superhero movie");
        testMovie.setMovieId(1);
    }

    @Test
    @DisplayName("Should add movie successfully")
    void testAddMovie_Success() {
        // Given
        when(movieRepository.save(testMovie)).thenReturn(testMovie);

        // When
        Movie result = movieService.addMovie(testMovie);

        // Then
        assertNotNull(result);
        assertEquals(testMovie.getMovieName(), result.getMovieName());
        assertEquals(testMovie.getMovieGenre(), result.getMovieGenre());
        verify(movieRepository).save(testMovie);
    }

    @Test
    @DisplayName("Should update movie successfully")
    void testUpdateMovie_Success() {
        // Given
        when(movieRepository.findById(testMovie.getMovieId())).thenReturn(Optional.of(testMovie));
        when(movieRepository.save(testMovie)).thenReturn(testMovie);

        // When
        Movie result = movieService.updateMovie(testMovie);

        // Then
        assertNotNull(result);
        assertEquals(testMovie.getMovieId(), result.getMovieId());
        verify(movieRepository).save(testMovie);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent movie")
    void testUpdateMovie_NotFound() {
        // Given
        when(movieRepository.findById(testMovie.getMovieId())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> movieService.updateMovie(testMovie));

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should remove movie successfully")
    void testRemoveMovie_Success() {
        // Given
        when(movieRepository.findById(1)).thenReturn(Optional.of(testMovie));
        doNothing().when(movieRepository).deleteById(1);

        // When
        Movie result = movieService.removeMovie(1);

        // Then
        assertNotNull(result);
        assertEquals(testMovie.getMovieId(), result.getMovieId());
        verify(movieRepository).deleteById(1);
    }

    @Test
    @DisplayName("Should view movie successfully")
    void testViewMovie_Success() {
        // Given
        when(movieRepository.findById(1)).thenReturn(Optional.of(testMovie));

        // When
        Movie result = movieService.viewMovie(1);

        // Then
        assertNotNull(result);
        assertEquals(testMovie.getMovieId(), result.getMovieId());
    }

    @Test
    @DisplayName("Should view all movies successfully")
    void testViewMovieList_Success() {
        // Given
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findAll()).thenReturn(movies);

        // When
        List<Movie> result = movieService.viewMovieList();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMovie.getMovieName(), result.get(0).getMovieName());
    }
}
