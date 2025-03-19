package com.app.v1.user.model;

import jakarta.persistence.*;


@Entity
@Table(name = "users") 
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name") 
    private String firstName;

    @Column(name = "last_name") 
    private String lastName;

    @Column(name = "email") 
    private String email;

    @Column(name = "dob") 
    private String dob;

    public User() {}

    public User(String firstName, String lastName, String email, String dob) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dob = dob;
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getDob() { return dob; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setDob(String dob) { this.dob = dob; }
}
