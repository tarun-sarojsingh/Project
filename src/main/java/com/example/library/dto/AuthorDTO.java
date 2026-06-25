package com.example.library.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorDTO {
    
    private Long id;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @Size(max = 500, message = "Biography cannot exceed 500 characters")
    private String biography;
    
    @Min(value = 1000, message = "Birth year must be after 1000")
    @Max(value = 2024, message = "Birth year cannot be in the future")
    private Integer birthYear;
    
    private List<BookDTO> books;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
