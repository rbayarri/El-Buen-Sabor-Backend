package com.lacodigoneta.elbuensabor.entities;

import com.lacodigoneta.elbuensabor.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true)
    private String username;

    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private String name;

    private String lastName;

    @OneToMany(mappedBy = "user")
    private Set<Address> addresses;

    @OneToMany(mappedBy = "user")
    private Set<PhoneNumber> phoneNumbers;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @ManyToOne
    private Image image;

    private boolean emailConfirmed;

    private boolean active;

    private boolean firstTimeAccess;

}
