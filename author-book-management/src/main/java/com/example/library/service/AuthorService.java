package com.example.library.service;

import com.example.library.dto.AuthorDTO;
import com.example.library.dto.BookDTO;
import com.example.library.entity.Author;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthorService {
    
    private final AuthorRepository authorRepository;
    
    public List<AuthorDTO> getAllAuthors() {
        log.info("Fetching all authors");
        List<Author> authors = authorRepository.findAll();
        log.debug("Found {} authors", authors.size());
        return authors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public AuthorDTO getAuthorById(Long id) {
        log.info("Fetching author with id: {}", id);
        Author author = authorRepository.findByIdWithBooks(id)
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", id);
                    return new ResourceNotFoundException("Author not found with id: " + id);
                });
        return convertToDTOWithBooks(author);
    }
    
    @Transactional
    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        log.info("Creating new author: {} {}", authorDTO.getFirstName(), authorDTO.getLastName());
        
        validateAuthorUniqueness(authorDTO);
        
        Author author = convertToEntity(authorDTO);
        Author savedAuthor = authorRepository.save(author);
        
        log.info("Author created successfully with id: {}", savedAuthor.getId());
        return convertToDTO(savedAuthor);
    }
    
    @Transactional
    public AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO) {
        log.info("Updating author with id: {}", id);
        
        Author existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", id);
                    return new ResourceNotFoundException("Author not found with id: " + id);
                });
        
        // Check for duplicates excluding current author
        if (!existingAuthor.getEmail().equals(authorDTO.getEmail()) && 
            authorRepository.existsByEmail(authorDTO.getEmail())) {
            log.error("Email already exists: {}", authorDTO.getEmail());
            throw new DuplicateResourceException("Email already exists: " + authorDTO.getEmail());
        }
        
        updateAuthorFields(existingAuthor, authorDTO);
        Author updatedAuthor = authorRepository.save(existingAuthor);
        
        log.info("Author updated successfully with id: {}", id);
        return convertToDTO(updatedAuthor);
    }
    
    @Transactional
    public void deleteAuthor(Long id) {
        log.info("Deleting author with id: {}", id);
        
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", id);
                    return new ResourceNotFoundException("Author not found with id: " + id);
                });
        
        authorRepository.delete(author);
        log.info("Author deleted successfully with id: {}", id);
    }
    
    public List<AuthorDTO> searchAuthorsByLastName(String lastName) {
        log.info("Searching authors by last name: {}", lastName);
        return authorRepository.findByLastNameContainingIgnoreCase(lastName)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private void validateAuthorUniqueness(AuthorDTO authorDTO) {
        if (authorRepository.existsByEmail(authorDTO.getEmail())) {
            log.error("Email already exists: {}", authorDTO.getEmail());
            throw new DuplicateResourceException("Email already exists: " + authorDTO.getEmail());
        }
        
        if (authorRepository.existsByFirstNameAndLastName(
                authorDTO.getFirstName(), authorDTO.getLastName())) {
            log.error("Author with same name already exists: {} {}", 
                    authorDTO.getFirstName(), authorDTO.getLastName());
            throw new DuplicateResourceException(
                    "Author already exists with name: " + authorDTO.getFirstName() + " " + authorDTO.getLastName());
        }
    }
    
    private void updateAuthorFields(Author author, AuthorDTO dto) {
        author.setFirstName(dto.getFirstName());
        author.setLastName(dto.getLastName());
        author.setEmail(dto.getEmail());
        author.setBiography(dto.getBiography());
        author.setBirthYear(dto.getBirthYear());
    }
    
    private AuthorDTO convertToDTO(Author author) {
        return AuthorDTO.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .email(author.getEmail())
                .biography(author.getBiography())
                .birthYear(author.getBirthYear())
                .createdAt(author.getCreatedAt())
                .updatedAt(author.getUpdatedAt())
                .build();
    }
    
    private AuthorDTO convertToDTOWithBooks(Author author) {
        AuthorDTO dto = convertToDTO(author);
        List<BookDTO> bookDTOs = author.getBooks().stream()
                .map(book -> BookDTO.builder()
                        .id(book.getId())
                        .title(book.getTitle())
                        .isbn(book.getIsbn())
                        .genre(book.getGenre())
                        .publicationYear(book.getPublicationYear())
                        .build())
                .collect(Collectors.toList());
        dto.setBooks(bookDTOs);
        return dto;
    }
    
    private Author convertToEntity(AuthorDTO dto) {
        return Author.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .biography(dto.getBiography())
                .birthYear(dto.getBirthYear())
                .build();
    }
}