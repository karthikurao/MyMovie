package com.moviebooking.service;

import com.moviebooking.entity.Theatre;
import com.moviebooking.repository.ITheatreRepository;
import com.moviebooking.service.impl.TheatreServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Theatre Service Tests")
public class TheatreServiceTest {

    @Mock
    private ITheatreRepository theatreRepository;

    @InjectMocks
    private TheatreServiceImpl theatreService;

    private Theatre testTheatre;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testTheatre = new Theatre("PVR Cinemas", "Mumbai", "John Manager", "9876543210");
        testTheatre.setTheatreId(1);
    }

    @Test
    @DisplayName("Should add theatre successfully")
    void testAddTheatre_Success() {
        // Given
        when(theatreRepository.existsByTheatreName(testTheatre.getTheatreName())).thenReturn(false);
        when(theatreRepository.save(testTheatre)).thenReturn(testTheatre);

        // When
        Theatre result = theatreService.addTheatre(testTheatre);

        // Then
        assertNotNull(result);
        assertEquals(testTheatre.getTheatreName(), result.getTheatreName());
        verify(theatreRepository).save(testTheatre);
    }

    @Test
    @DisplayName("Should throw exception when theatre name already exists")
    void testAddTheatre_NameExists() {
        // Given
        when(theatreRepository.existsByTheatreName(testTheatre.getTheatreName())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> theatreService.addTheatre(testTheatre));

        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("Should view theatres by city successfully")
    void testViewTheatresByCity_Success() {
        // Given
        String city = "Mumbai";
        List<Theatre> theatres = Arrays.asList(testTheatre);
        when(theatreRepository.findByTheatreCity(city)).thenReturn(theatres);

        // When
        List<Theatre> result = theatreService.viewTheatresByCity(city);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTheatre.getTheatreCity(), result.get(0).getTheatreCity());
    }
}
