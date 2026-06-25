package com.example.library.controller;

import com.example.library.dto.AuthorDTO;
import com.example.library.service.AuthorService;
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
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Author Management", description = "APIs for managing authors")
@SecurityRequirement(name = "basicAuth")
public class AuthorController {
    
    private final AuthorService authorService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Get all authors", description = "Retrieve a list of all authors")
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        log.info("GET /api/v1/authors - Fetching all authors");
        List<AuthorDTO> authors = authorService.getAllAuthors();
        return ResponseEntity.ok(authors);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Get author by ID", description = "Retrieve a specific author by their ID")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        log.info("GET /api/v1/authors/{} - Fetching author", id);
        AuthorDTO author = authorService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new author", description = "Create a new author")
    public ResponseEntity<AuthorDTO> createAuthor(@Valid @RequestBody AuthorDTO authorDTO) {
        log.info("POST /api/v1/authors - Creating new author");
        AuthorDTO createdAuthor = authorService.createAuthor(authorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAuthor);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update author", description = "Update an existing author")
    public ResponseEntity<AuthorDTO> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorDTO authorDTO) {
        log.info("PUT /api/v1/authors/{} - Updating author", id);
        AuthorDTO updatedAuthor = authorService.updateAuthor(id, authorDTO);
        return ResponseEntity.ok(updatedAuthor);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete author", description = "Delete an author by ID")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        log.info("DELETE /api/v1/authors/{} - Deleting author", id);
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Search authors", description = "Search authors by last name")
    public ResponseEntity<List<AuthorDTO>> searchAuthors(@RequestParam String lastName) {
        log.info("GET /api/v1/authors/search?lastName={}", lastName);
        List<AuthorDTO> authors = authorService.searchAuthorsByLastName(lastName);
        return ResponseEntity.ok(authors);
    }
}