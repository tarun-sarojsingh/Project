package com.example.library.controller;

import com.example.library.dto.BookDTO;
import com.example.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Book Management", description = "APIs for managing books")
@SecurityRequirement(name = "basicAuth")
public class BookController {
    
    private final BookService bookService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Get all books", description = "Retrieve a list of all books")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        log.info("GET /api/v1/books - Fetching all books");
        List<BookDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Get book by ID", description = "Retrieve a specific book by its ID")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        log.info("GET /api/v1/books/{} - Fetching book", id);
        BookDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new book", description = "Create a new book")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        log.info("POST /api/v1/books - Creating new book");
        BookDTO createdBook = bookService.createBook(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update book", description = "Update an existing book")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO) {
        log.info("PUT /api/v1/books/{} - Updating book", id);
        BookDTO updatedBook = bookService.updateBook(id, bookDTO);
        return ResponseEntity.ok(updatedBook);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete book", description = "Delete a book by ID")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.info("DELETE /api/v1/books/{} - Deleting book", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bulk update books", description = "Update multiple books at once")
    public ResponseEntity<List<BookDTO>> bulkUpdateBooks(@Valid @RequestBody List<BookDTO> bookDTOs) {
        log.info("PUT /api/v1/books/bulk - Bulk updating {} books", bookDTOs.size());
        List<BookDTO> updatedBooks = bookService.bulkUpdateBooks(bookDTOs);
        return ResponseEntity.ok(updatedBooks);
    }
    
    @GetMapping("/author/{authorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Get books by author", description = "Retrieve all books by a specific author")
    public ResponseEntity<List<BookDTO>> getBooksByAuthor(@PathVariable Long authorId) {
        log.info("GET /api/v1/books/author/{} - Fetching books by author", authorId);
        List<BookDTO> books = bookService.getBooksByAuthor(authorId);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Search books", description = "Search books by title")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String title) {
        log.info("GET /api/v1/books/search?title={}", title);
        List<BookDTO> books = bookService.searchBooksByTitle(title);
        return ResponseEntity.ok(books);
    }
}