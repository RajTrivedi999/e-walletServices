package com.example;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByUserName(String username);

    List<User> findAllByAge(int age);
    boolean existsByUserName(String username);
}
