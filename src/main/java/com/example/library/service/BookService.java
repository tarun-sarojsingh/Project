package com.example.library.service;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
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
public class BookService {
    
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    
    public List<BookDTO> getAllBooks() {
        log.info("Fetching all books");
        List<Book> books = bookRepository.findAll();
        log.debug("Found {} books", books.size());
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public BookDTO getBookById(Long id) {
        log.info("Fetching book with id: {}", id);
        Book book = bookRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> {
                    log.error("Book not found with id: {}", id);
                    return new ResourceNotFoundException("Book not found with id: " + id);
                });
        return convertToDTO(book);
    }
    
    @Transactional
    public BookDTO createBook(BookDTO bookDTO) {
        log.info("Creating new book: {}", bookDTO.getTitle());
        
        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            log.error("ISBN already exists: {}", bookDTO.getIsbn());
            throw new DuplicateResourceException("Book with ISBN already exists: " + bookDTO.getIsbn());
        }
        
        Author author = authorRepository.findById(bookDTO.getAuthorId())
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", bookDTO.getAuthorId());
                    return new ResourceNotFoundException("Author not found with id: " + bookDTO.getAuthorId());
                });
        
        Book book = convertToEntity(bookDTO);
        book.setAuthor(author);
        Book savedBook = bookRepository.save(book);
        
        log.info("Book created successfully with id: {}", savedBook.getId());
        return convertToDTO(savedBook);
    }
    
    @Transactional
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        log.info("Updating book with id: {}", id);
        
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book not found with id: {}", id);
                    return new ResourceNotFoundException("Book not found with id: " + id);
                });
        
        // Check ISBN uniqueness
        if (!existingBook.getIsbn().equals(bookDTO.getIsbn()) && 
            bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            log.error("ISBN already exists: {}", bookDTO.getIsbn());
            throw new DuplicateResourceException("ISBN already exists: " + bookDTO.getIsbn());
        }
        
        // Update author if changed
        if (!existingBook.getAuthor().getId().equals(bookDTO.getAuthorId())) {
            Author newAuthor = authorRepository.findById(bookDTO.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + bookDTO.getAuthorId()));
            existingBook.setAuthor(newAuthor);
        }
        
        updateBookFields(existingBook, bookDTO);
        Book updatedBook = bookRepository.save(existingBook);
        
        log.info("Book updated successfully with id: {}", id);
        return convertToDTO(updatedBook);
    }
    
    @Transactional
    public void deleteBook(Long id) {
        log.info("Deleting book with id: {}", id);
        
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book not found with id: {}", id);
                    return new ResourceNotFoundException("Book not found with id: " + id);
                });
        
        bookRepository.delete(book);
        log.info("Book deleted successfully with id: {}", id);
    }
    
    @Transactional
    public List<BookDTO> bulkUpdateBooks(List<BookDTO> bookDTOs) {
        log.info("Bulk updating {} books", bookDTOs.size());
        
        return bookDTOs.stream()
                .map(dto -> {
                    try {
                        return updateBook(dto.getId(), dto);
                    } catch (Exception e) {
                        log.error("Error updating book with id: {}", dto.getId(), e);
                        throw e;
                    }
                })
                .collect(Collectors.toList());
    }
    
    public List<BookDTO> getBooksByAuthor(Long authorId) {
        log.info("Fetching books for author id: {}", authorId);
        return bookRepository.findByAuthorId(authorId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<BookDTO> searchBooksByTitle(String title) {
        log.info("Searching books by title: {}", title);
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private void updateBookFields(Book book, BookDTO dto) {
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setPublicationYear(dto.getPublicationYear());
        book.setGenre(dto.getGenre());
        book.setPrice(dto.getPrice());
        book.setAvailableCopies(dto.getAvailableCopies());
        book.setDescription(dto.getDescription());
    }
    
    private BookDTO convertToDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .genre(book.getGenre())
                .price(book.getPrice())
                .availableCopies(book.getAvailableCopies())
                .description(book.getDescription())
                .authorId(book.getAuthor().getId())
                .authorName(book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
    
    private Book convertToEntity(BookDTO dto) {
        return Book.builder()
                .title(dto.getTitle())
                .isbn(dto.getIsbn())
                .publicationYear(dto.getPublicationYear())
                .genre(dto.getGenre())
                .price(dto.getPrice())
                .availableCopies(dto.getAvailableCopies())
                .description(dto.getDescription())
                .build();
    }
}
