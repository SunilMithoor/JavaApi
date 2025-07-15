package com.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.util.Date;

import static com.app.util.MessageConstants.DATE_OF_BIRTH_INVALID;


@Entity
@Table(name = "customers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Customers{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "customer_id", unique = true,nullable = false, length = 50)
    @JsonProperty("customer_id")
    private String customerId;

    @Column(name = "first_name", length = 100)
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "last_name", length = 100)
    @JsonProperty("last_name")
    private String lastName;

    @Column(name = "company", length = 150)
    @JsonProperty("company")
    private String company;

    @Column(name = "city", length = 100)
    @JsonProperty("city")
    private String city;

    @Column(name = "country", length = 100)
    @JsonProperty("country")
    private String country;

    @Column(name = "phone1", length = 25)
    @JsonProperty("phone1")
    private String phone1;

    @Column(name = "phone2", length = 25)
    @JsonProperty("phone2")
    private String phone2;

    @Column(name = "email", length = 150)
    @JsonProperty("email")
    private String email;

    @Column(name = "subscription_date")
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("subscription_date")
    private Date subscriptionDate;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("created_at")
    private Date createdAt = new Date();

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("updated_at")
    private Date updatedAt = new Date();

}