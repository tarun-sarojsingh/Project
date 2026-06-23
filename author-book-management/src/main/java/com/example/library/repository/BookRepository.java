package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    Optional<Book> findByIsbn(String isbn);
    
    boolean existsByIsbn(String isbn);
    
    List<Book> findByAuthorId(Long authorId);
    
    List<Book> findByGenreIgnoreCase(String genre);
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT b FROM Book b JOIN FETCH b.author WHERE b.id = ?1")
    Optional<Book> findByIdWithAuthor(Long id);
    
    @Query("SELECT DISTINCT b.genre FROM Book b ORDER BY b.genre")
    List<String> findAllDistinctGenres();
}
