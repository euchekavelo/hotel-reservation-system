package ru.mephi.bookingservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.mephi.bookingservice.model.enums.Role;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookingList = new ArrayList<>();
}
