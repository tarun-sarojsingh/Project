package com.example.library;

import com.example.library.dto.AuthorDTO;
import com.example.library.entity.Author;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.library.service.AuthorService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {
    
    @Mock
    private AuthorRepository authorRepository;
    
    @InjectMocks
    private AuthorService authorService;
    
    private Author testAuthor;
    private AuthorDTO testAuthorDTO;
    
    @BeforeEach
    void setUp() {
        testAuthor = Author.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .biography("Test biography")
                .birthYear(1970)
                .build();
        
        testAuthorDTO = AuthorDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .biography("Test biography")
                .birthYear(1970)
                .build();
    }
    
    @Test
    void getAllAuthors_ShouldReturnAllAuthors() {
        // Arrange
        List<Author> authors = Arrays.asList(testAuthor);
        when(authorRepository.findAll()).thenReturn(authors);
        
        // Act
        List<AuthorDTO> result = authorService.getAllAuthors();
        
        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        verify(authorRepository, times(1)).findAll();
    }
    
    @Test
    void getAuthorById_WhenAuthorExists_ShouldReturnAuthor() {
        // Arrange
        when(authorRepository.findByIdWithBooks(1L)).thenReturn(Optional.of(testAuthor));
        
        // Act
        AuthorDTO result = authorService.getAuthorById(1L);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        verify(authorRepository, times(1)).findByIdWithBooks(1L);
    }
    
    @Test
    void getAuthorById_WhenAuthorDoesNotExist_ShouldThrowException() {
        // Arrange
        when(authorRepository.findByIdWithBooks(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> authorService.getAuthorById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author not found with id: 999");
        
        verify(authorRepository, times(1)).findByIdWithBooks(999L);
    }
    
    @Test
    void createAuthor_WhenValid_ShouldCreateAuthor() {
        // Arrange
        when(authorRepository.existsByEmail(testAuthorDTO.getEmail())).thenReturn(false);
        when(authorRepository.existsByFirstNameAndLastName(
                testAuthorDTO.getFirstName(), testAuthorDTO.getLastName())).thenReturn(false);
        when(authorRepository.save(any(Author.class))).thenReturn(testAuthor);
        
        // Act
        AuthorDTO result = authorService.createAuthor(testAuthorDTO);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
        verify(authorRepository, times(1)).save(any(Author.class));
    }
    
    @Test
    void createAuthor_WhenEmailExists_ShouldThrowException() {
        // Arrange
        when(authorRepository.existsByEmail(testAuthorDTO.getEmail())).thenReturn(true);
        
        // Act & Assert
        assertThatThrownBy(() -> authorService.createAuthor(testAuthorDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already exists");
        
        verify(authorRepository, never()).save(any(Author.class));
    }
    
    @Test
    void updateAuthor_WhenValid_ShouldUpdateAuthor() {
        // Arrange
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(testAuthor);
        
        testAuthorDTO.setBiography("Updated biography");
        
        // Act
        AuthorDTO result = authorService.updateAuthor(1L, testAuthorDTO);
        
        // Assert
        assertThat(result).isNotNull();
        verify(authorRepository, times(1)).save(any(Author.class));
    }
    
    @Test
    void deleteAuthor_WhenAuthorExists_ShouldDeleteAuthor() {
        // Arrange
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        doNothing().when(authorRepository).delete(testAuthor);
        
        // Act
        authorService.deleteAuthor(1L);
        
        // Assert
        verify(authorRepository, times(1)).delete(testAuthor);
    }
    
    @Test
    void searchAuthorsByLastName_ShouldReturnMatchingAuthors() {
        // Arrange
        List<Author> authors = Arrays.asList(testAuthor);
        when(authorRepository.findByLastNameContainingIgnoreCase("Doe")).thenReturn(authors);
        
        // Act
        List<AuthorDTO> result = authorService.searchAuthorsByLastName("Doe");
        
        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Doe");
        verify(authorRepository, times(1)).findByLastNameContainingIgnoreCase("Doe");
    }
}
