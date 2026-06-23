package com.example.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.library.dto.BookDTO;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private AuthorRepository authorRepository;
    
    private Author testAuthor;
    private Book testBook;
    
    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        
        testAuthor = Author.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        testAuthor = authorRepository.save(testAuthor);
        
        testBook = Book.builder()
                .title("Test Book")
                .isbn("978-3-16-148410-0")
                .publicationYear(2023)
                .genre("Fiction")
                .price(new BigDecimal("29.99"))
                .availableCopies(10)
                .description("Test description")
                .author(testAuthor)
                .build();
        testBook = bookRepository.save(testBook);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllBooks_ShouldReturnBooks() throws Exception {
        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Test Book")))
                .andExpect(jsonPath("$[0].isbn", is("978-3-16-148410-0")));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getBookById_WhenExists_ShouldReturnBook() throws Exception {
        mockMvc.perform(get("/api/v1/books/{id}", testBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test Book")))
                .andExpect(jsonPath("$.isbn", is("978-3-16-148410-0")))
                .andExpect(jsonPath("$.authorName", containsString("John Doe")));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createBook_WithValidData_ShouldCreateBook() throws Exception {
        BookDTO newBook = BookDTO.builder()
                .title("New Book")
                .isbn("978-3-16-148410-1")
                .publicationYear(2024)
                .genre("Science Fiction")
                .price(new BigDecimal("39.99"))
                .availableCopies(5)
                .description("New book description")
                .authorId(testAuthor.getId())
                .build();
        
        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Book")))
                .andExpect(jsonPath("$.isbn", is("978-3-16-148410-1")));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createBook_WithDuplicateIsbn_ShouldReturn409() throws Exception {
        BookDTO duplicateBook = BookDTO.builder()
                .title("Duplicate Book")
                .isbn("978-3-16-148410-0") // Duplicate ISBN
                .publicationYear(2024)
                .genre("Fiction")
                .price(new BigDecimal("29.99"))
                .availableCopies(5)
                .authorId(testAuthor.getId())
                .build();
        
        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateBook)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("ISBN already exists")));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBook_WithValidData_ShouldUpdateBook() throws Exception {
        BookDTO updatedBook = BookDTO.builder()
                .title("Updated Title")
                .isbn("978-3-16-148410-0")
                .publicationYear(2023)
                .genre("Fiction")
                .price(new BigDecimal("34.99"))
                .availableCopies(15)
                .description("Updated description")
                .authorId(testAuthor.getId())
                .build();
        
        mockMvc.perform(put("/api/v1/books/{id}", testBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.price", is(34.99)));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_WhenExists_ShouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/v1/books/{id}", testBook.getId()))
                .andExpect(status().isNoContent());
        
        // Verify deletion
        mockMvc.perform(get("/api/v1/books/{id}", testBook.getId()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void createBook_AsLibrarian_ShouldReturn403() throws Exception {
        BookDTO newBook = BookDTO.builder()
                .title("New Book")
                .isbn("978-3-16-148410-1")
                .publicationYear(2024)
                .genre("Fiction")
                .price(new BigDecimal("29.99"))
                .availableCopies(5)
                .authorId(testAuthor.getId())
                .build();
        
        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isForbidden());
    }
}
