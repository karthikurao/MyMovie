package com.moviebooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "screens")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int screenId;

    @NotNull(message = "Theatre ID is required")
    @Column(nullable = false)
    private int theatreId;

    @NotBlank(message = "Screen name is required")
    @Column(nullable = false)
    private String screenName;

    @NotNull(message = "Rows count is required")
    @Column(nullable = false)
    private int rows;

    @NotNull(message = "Columns count is required")
    @Column(nullable = false)
    private int columns;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatreId", insertable = false, updatable = false)
    @JsonIgnoreProperties({"listOfScreens", "hibernateLazyInitializer", "handler"})
    private Theatre theatre;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"screen", "movie", "hibernateLazyInitializer", "handler"})
    private List<Show> showList;

    // Constructors
    public Screen() {}

    public Screen(int theatreId, String screenName, int rows, int columns) {
        this.theatreId = theatreId;
        this.screenName = screenName;
        this.rows = rows;
        this.columns = columns;
    }

    // Getters and Setters
    public int getScreenId() {
        return screenId;
    }

    public void setScreenId(int screenId) {
        this.screenId = screenId;
    }

    public int getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(int theatreId) {
        this.theatreId = theatreId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public Theatre getTheatre() {
        return theatre;
    }

    public void setTheatre(Theatre theatre) {
        this.theatre = theatre;
    }

    public List<Show> getShowList() {
        return showList;
    }

    public void setShowList(List<Show> showList) {
        this.showList = showList;
    }
}
