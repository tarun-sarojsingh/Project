package com.example.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.library.dto.AuthorDTO;
import com.example.library.entity.Author;
import com.example.library.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthorControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private AuthorRepository authorRepository;
    
    private Author testAuthor;
    
    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();
        
        testAuthor = Author.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .biography("Test biography")
                .birthYear(1970)
                .build();
        
        testAuthor = authorRepository.save(testAuthor);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllAuthors_AsAdmin_ShouldReturnAuthors() throws Exception {
        mockMvc.perform(get("/api/v1/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Doe")));
    }
    
    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void getAllAuthors_AsLibrarian_ShouldReturnAuthors() throws Exception {
        mockMvc.perform(get("/api/v1/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }
    
    @Test
    void getAllAuthors_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/authors"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAuthorById_WhenExists_ShouldReturnAuthor() throws Exception {
        mockMvc.perform(get("/api/v1/authors/{id}", testAuthor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAuthorById_WhenNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/authors/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Author not found")));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createAuthor_WithValidData_ShouldCreateAuthor() throws Exception {
        AuthorDTO newAuthor = AuthorDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .biography("Author biography")
                .birthYear(1980)
                .build();
        
        mockMvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAuthor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("Jane")))
                .andExpect(jsonPath("$.lastName", is("Smith")))
                .andExpect(jsonPath("$.email", is("jane.smith@example.com")));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createAuthor_WithDuplicateEmail_ShouldReturn409() throws Exception {
        AuthorDTO duplicateAuthor = AuthorDTO.builder()
                .firstName("Another")
                .lastName("Author")
                .email("john.doe@example.com") // Duplicate email
                .build();
        
        mockMvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateAuthor)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createAuthor_WithInvalidData_ShouldReturn400() throws Exception {
        AuthorDTO invalidAuthor = AuthorDTO.builder()
                .firstName("") // Empty first name
                .lastName("Smith")
                .email("invalid-email") // Invalid email format
                .build();
        
        mockMvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthor)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")));
    }
    
    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void createAuthor_AsLibrarian_ShouldReturn403() throws Exception {
        AuthorDTO newAuthor = AuthorDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .build();
        
        mockMvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAuthor)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAuthor_WithValidData_ShouldUpdateAuthor() throws Exception {
        AuthorDTO updatedAuthor = AuthorDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe.updated@example.com")
                .biography("Updated biography")
                .birthYear(1970)
                .build();
        
        mockMvc.perform(put("/api/v1/authors/{id}", testAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAuthor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("john.doe.updated@example.com")))
                .andExpect(jsonPath("$.biography", is("Updated biography")));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAuthor_WhenExists_ShouldDeleteAuthor() throws Exception {
        mockMvc.perform(delete("/api/v1/authors/{id}", testAuthor.getId()))
                .andExpect(status().isNoContent());
        
        // Verify deletion
        mockMvc.perform(get("/api/v1/authors/{id}", testAuthor.getId()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void deleteAuthor_AsLibrarian_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/v1/authors/{id}", testAuthor.getId()))
                .andExpect(status().isForbidden());
    }
}
