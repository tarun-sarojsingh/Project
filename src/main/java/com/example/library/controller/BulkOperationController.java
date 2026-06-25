package com.example.library.controller;


import com.example.library.dto.BulkResponseDTO;
import com.example.library.service.BulkOperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/bulk")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bulk Operations", description = "APIs for bulk import/export operations")
@SecurityRequirement(name = "basicAuth")
public class BulkOperationController {
    
    private final BulkOperationService bulkOperationService;
    
    @PostMapping("/authors/import")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Import authors from CSV", description = "Bulk import authors from a CSV file")
    public ResponseEntity<BulkResponseDTO> importAuthors(@RequestParam("file") MultipartFile file) {
        log.info("POST /api/v1/bulk/authors/import - Importing authors from CSV");
        BulkResponseDTO response = bulkOperationService.importAuthorsFromCsv(file);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/books/import")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Import books from CSV", description = "Bulk import books from a CSV file")
    public ResponseEntity<BulkResponseDTO> importBooks(@RequestParam("file") MultipartFile file) {
        log.info("POST /api/v1/bulk/books/import - Importing books from CSV");
        BulkResponseDTO response = bulkOperationService.importBooksFromCsv(file);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/authors/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Export authors to CSV", description = "Export all authors to a CSV file")
    public ResponseEntity<byte[]> exportAuthors() {
        log.info("GET /api/v1/bulk/authors/export - Exporting authors to CSV");
        byte[] csvData = bulkOperationService.exportAuthorsToCsv();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "authors.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }
    
    @GetMapping("/books/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Export books to CSV", description = "Export all books to a CSV file")
    public ResponseEntity<byte[]> exportBooks() {
        log.info("GET /api/v1/bulk/books/export - Exporting books to CSV");
        byte[] csvData = bulkOperationService.exportBooksToCsv();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "books.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }
}
