package com.mscfinalyearproject.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Interface provided by Spring Data JPA, It comes with pre defined methods that we can use to interact with the database
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
