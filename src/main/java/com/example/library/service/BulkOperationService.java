package com.example.library.service;

import com.example.library.dto.AuthorDTO;
import com.example.library.dto.BookDTO;
import com.example.library.dto.BulkResponseDTO;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import com.example.library.util.CsvUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulkOperationService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final BookService bookService;
    
    @Transactional
    public BulkResponseDTO importAuthorsFromCsv(MultipartFile file) {
        log.info("Starting bulk import of authors from CSV file: {}", file.getOriginalFilename());
        
        BulkResponseDTO response = BulkResponseDTO.builder()
                .totalRecords(0)
                .successCount(0)
                .failureCount(0)
                .build();
        
        try {
            List<AuthorDTO> authors = CsvUtil.parseAuthorsCsv(file.getInputStream());
            response.setTotalRecords(authors.size());
            
            for (int i = 0; i < authors.size(); i++) {
                try {
                    authorService.createAuthor(authors.get(i));
                    response.setSuccessCount(response.getSuccessCount() + 1);
                } catch (Exception e) {
                    response.setFailureCount(response.getFailureCount() + 1);
                    response.getErrors().add("Row " + (i + 2) + ": " + e.getMessage());
                    log.error("Error importing author at row {}: {}", i + 2, e.getMessage());
                }
            }
            
            response.setMessage("Import completed: " + response.getSuccessCount() + " successful, " + 
                              response.getFailureCount() + " failed");
            
        } catch (IOException e) {
            log.error("Error reading CSV file", e);
            response.getErrors().add("Error reading CSV file: " + e.getMessage());
            response.setMessage("Import failed");
        }
        
        log.info("Bulk import completed. Success: {}, Failed: {}", 
                response.getSuccessCount(), response.getFailureCount());
        
        return response;
    }
    
    @Transactional
    public BulkResponseDTO importBooksFromCsv(MultipartFile file) {
        log.info("Starting bulk import of books from CSV file: {}", file.getOriginalFilename());
        
        BulkResponseDTO response = BulkResponseDTO.builder()
                .totalRecords(0)
                .successCount(0)
                .failureCount(0)
                .build();
        
        try {
            List<BookDTO> books = CsvUtil.parseBooksCsv(file.getInputStream());
            response.setTotalRecords(books.size());
            
            for (int i = 0; i < books.size(); i++) {
                try {
                    bookService.createBook(books.get(i));
                    response.setSuccessCount(response.getSuccessCount() + 1);
                } catch (Exception e) {
                    response.setFailureCount(response.getFailureCount() + 1);
                    response.getErrors().add("Row " + (i + 2) + ": " + e.getMessage());
                    log.error("Error importing book at row {}: {}", i + 2, e.getMessage());
                }
            }
            
            response.setMessage("Import completed: " + response.getSuccessCount() + " successful, " + 
                              response.getFailureCount() + " failed");
            
        } catch (IOException e) {
            log.error("Error reading CSV file", e);
            response.getErrors().add("Error reading CSV file: " + e.getMessage());
            response.setMessage("Import failed");
        }
        
        log.info("Bulk import completed. Success: {}, Failed: {}", 
                response.getSuccessCount(), response.getFailureCount());
        
        return response;
    }
    
    public byte[] exportAuthorsToCsv() {
        log.info("Exporting all authors to CSV");
        List<Author> authors = authorRepository.findAll();
        return CsvUtil.generateAuthorsCsv(authors);
    }
    
    public byte[] exportBooksToCsv() {
        log.info("Exporting all books to CSV");
        List<Book> books = bookRepository.findAll();
        return CsvUtil.generateBooksCsv(books);
    }
}
