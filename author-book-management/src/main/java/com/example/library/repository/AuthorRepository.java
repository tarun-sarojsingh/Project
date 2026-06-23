package com.example.library.repository;

import com.example.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    Optional<Author> findByEmail(String email);
    
    @Query("SELECT a FROM Author a WHERE LOWER(a.firstName) = LOWER(?1) AND LOWER(a.lastName) = LOWER(?2)")
    Optional<Author> findByFirstNameAndLastNameIgnoreCase(String firstName, String lastName);
    
    boolean existsByEmail(String email);
    
    boolean existsByFirstNameAndLastName(String firstName, String lastName);
    
    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.books WHERE a.id = ?1")
    Optional<Author> findByIdWithBooks(Long id);
    
    List<Author> findByLastNameContainingIgnoreCase(String lastName);
}
