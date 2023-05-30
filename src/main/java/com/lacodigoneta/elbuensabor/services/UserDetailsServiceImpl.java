package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.User;
import com.lacodigoneta.elbuensabor.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userByUsername = repository.findUserByUsernameAndActiveTrue(username);
        if (userByUsername == null) {
            throw new UsernameNotFoundException(username);
        }
        return toUserDetails(userByUsername);
    }

    private UserDetails toUserDetails(User userByUsername) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(userByUsername.getUsername())
                .password(userByUsername.getPassword())
                .authorities(userByUsername.getRole().getName())
                .disabled(false)
                .credentialsExpired(false)
                .accountLocked(false)
                .accountExpired(false)
                .build();
    }
}
