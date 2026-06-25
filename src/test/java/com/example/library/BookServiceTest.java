package com.example.library;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.library.service.BookService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    
    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private AuthorRepository authorRepository;
    
    @InjectMocks
    private BookService bookService;
    
    private Book testBook;
    private BookDTO testBookDTO;
    private Author testAuthor;
    
    @BeforeEach
    void setUp() {
        testAuthor = Author.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        
        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .isbn("978-3-16-148410-0")
                .publicationYear(2023)
                .genre("Fiction")
                .price(new BigDecimal("29.99"))
                .availableCopies(10)
                .description("Test description")
                .author(testAuthor)
                .build();
        
        testBookDTO = BookDTO.builder()
                .title("Test Book")
                .isbn("978-3-16-148410-0")
                .publicationYear(2023)
                .genre("Fiction")
                .price(new BigDecimal("29.99"))
                .availableCopies(10)
                .description("Test description")
                .authorId(1L)
                .build();
    }
    
    @Test
    void getAllBooks_ShouldReturnAllBooks() {
        // Arrange
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(books);
        
        // Act
        List<BookDTO> result = bookService.getAllBooks();
        
        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Book");
        verify(bookRepository, times(1)).findAll();
    }
    
    @Test
    void getBookById_WhenBookExists_ShouldReturnBook() {
        // Arrange
        when(bookRepository.findByIdWithAuthor(1L)).thenReturn(Optional.of(testBook));
        
        // Act
        BookDTO result = bookService.getBookById(1L);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Book");
        assertThat(result.getIsbn()).isEqualTo("978-3-16-148410-0");
        verify(bookRepository, times(1)).findByIdWithAuthor(1L);
    }
    
    @Test
    void getBookById_WhenBookDoesNotExist_ShouldThrowException() {
        // Arrange
        when(bookRepository.findByIdWithAuthor(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> bookService.getBookById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id: 999");
        
        verify(bookRepository, times(1)).findByIdWithAuthor(999L);
    }
    
    @Test
    void createBook_WhenValid_ShouldCreateBook() {
        // Arrange
        when(bookRepository.existsByIsbn(testBookDTO.getIsbn())).thenReturn(false);
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        
        // Act
        BookDTO result = bookService.createBook(testBookDTO);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Book");
        verify(bookRepository, times(1)).save(any(Book.class));
    }
    
    @Test
    void createBook_WhenIsbnExists_ShouldThrowException() {
        // Arrange
        when(bookRepository.existsByIsbn(testBookDTO.getIsbn())).thenReturn(true);
        
        // Act & Assert
        assertThatThrownBy(() -> bookService.createBook(testBookDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("ISBN already exists");
        
        verify(bookRepository, never()).save(any(Book.class));
    }
    
    @Test
    void createBook_WhenAuthorDoesNotExist_ShouldThrowException() {
        // Arrange
        when(bookRepository.existsByIsbn(testBookDTO.getIsbn())).thenReturn(false);
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> bookService.createBook(testBookDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author not found");
        
        verify(bookRepository, never()).save(any(Book.class));
    }
    
    @Test
    void updateBook_WhenValid_ShouldUpdateBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        
        testBookDTO.setTitle("Updated Title");
        
        // Act
        BookDTO result = bookService.updateBook(1L, testBookDTO);
        
        // Assert
        assertThat(result).isNotNull();
        verify(bookRepository, times(1)).save(any(Book.class));
    }
    
    @Test
    void deleteBook_WhenBookExists_ShouldDeleteBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        doNothing().when(bookRepository).delete(testBook);
        
        // Act
        bookService.deleteBook(1L);
        
        // Assert
        verify(bookRepository, times(1)).delete(testBook);
    }
    
    @Test
    void getBooksByAuthor_ShouldReturnAuthorBooks() {
        // Arrange
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findByAuthorId(1L)).thenReturn(books);
        
        // Act
        List<BookDTO> result = bookService.getBooksByAuthor(1L);
        
        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Book");
        verify(bookRepository, times(1)).findByAuthorId(1L);
    }
    
    @Test
    void searchBooksByTitle_ShouldReturnMatchingBooks() {
        // Arrange
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(books);
        
        // Act
        List<BookDTO> result = bookService.searchBooksByTitle("Test");
        
        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).contains("Test");
        verify(bookRepository, times(1)).findByTitleContainingIgnoreCase("Test");
    }
}
