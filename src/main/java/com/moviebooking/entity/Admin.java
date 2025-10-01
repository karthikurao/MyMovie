package com.moviebooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "admins")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int adminId;

    @NotBlank(message = "Admin name is required")
    @Column(nullable = false)
    private String adminName;

    @NotBlank(message = "Admin contact is required")
    @Column(nullable = false)
    private String adminContact;

    // Constructors
    public Admin() {}

    public Admin(String adminName, String adminContact) {
        this.adminName = adminName;
        this.adminContact = adminContact;
    }

    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminContact() {
        return adminContact;
    }

    public void setAdminContact(String adminContact) {
        this.adminContact = adminContact;
    }
}
