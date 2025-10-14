package com.moviebooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "theatres")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Theatre {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int theatreId;

    @NotBlank(message = "Theatre name is required")
    @Column(nullable = false)
    private String theatreName;

    @NotBlank(message = "Theatre city is required")
    @Column(nullable = false)
    private String theatreCity;

    @NotBlank(message = "Manager name is required")
    @Column(nullable = false)
    private String managerName;

    @NotBlank(message = "Manager contact is required")
    @Column(nullable = false)
    private String managerContact;

    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"theatre", "shows", "hibernateLazyInitializer", "handler"})
    private List<Screen> listOfScreens;

    // Constructors
    public Theatre() {}

    public Theatre(String theatreName, String theatreCity, String managerName, String managerContact) {
        this.theatreName = theatreName;
        this.theatreCity = theatreCity;
        this.managerName = managerName;
        this.managerContact = managerContact;
    }

    // Getters and Setters
    public int getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(int theatreId) {
        this.theatreId = theatreId;
    }

    public String getTheatreName() {
        return theatreName;
    }

    public void setTheatreName(String theatreName) {
        this.theatreName = theatreName;
    }

    public String getTheatreCity() {
        return theatreCity;
    }

    public void setTheatreCity(String theatreCity) {
        this.theatreCity = theatreCity;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerContact() {
        return managerContact;
    }

    public void setManagerContact(String managerContact) {
        this.managerContact = managerContact;
    }

    public List<Screen> getListOfScreens() {
        return listOfScreens;
    }

    public void setListOfScreens(List<Screen> listOfScreens) {
        this.listOfScreens = listOfScreens;
    }
}
