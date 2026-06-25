package com.example.library.util;

import com.example.library.dto.AuthorDTO;
import com.example.library.dto.BookDTO;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvUtilTest {

    @Test
    void parseAuthorsCsv_parsesCorrectly() throws Exception {
        String csv = "First Name,Last Name,Email,Biography,Birth Year\n" +
                "Jane,Austen,jane.austen@example.com,English novelist,1775\n" +
                "George,Orwell,george.orwell@example.com,,1903\n";

        ByteArrayInputStream in = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        List<AuthorDTO> authors = CsvUtil.parseAuthorsCsv(in);

        assertEquals(2, authors.size());
        AuthorDTO a1 = authors.get(0);
        assertEquals("Jane", a1.getFirstName());
        assertEquals("Austen", a1.getLastName());
        assertEquals("jane.austen@example.com", a1.getEmail());
        assertEquals("English novelist", a1.getBiography());
        assertEquals(1775, a1.getBirthYear());

        AuthorDTO a2 = authors.get(1);
        assertEquals("George", a2.getFirstName());
        // CsvUtil preserves empty fields as empty string
        assertEquals("", a2.getBiography());
        assertEquals(1903, a2.getBirthYear());
    }

    @Test
    void parseBooksCsv_parsesCorrectly() throws Exception {
        String csv = "Title,ISBN,Publication Year,Genre,Price,Available Copies,Description,Author ID\n" +
                "Test Book,978-3-16-148410-0,2023,Fiction,19.99,10,Sample book,1\n";

        ByteArrayInputStream in = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        List<BookDTO> books = CsvUtil.parseBooksCsv(in);

        assertEquals(1, books.size());
        BookDTO b = books.get(0);
        assertEquals("Test Book", b.getTitle());
        assertEquals("978-3-16-148410-0", b.getIsbn());
        assertEquals(2023, b.getPublicationYear());
        assertEquals("Fiction", b.getGenre());
        assertEquals(new BigDecimal("19.99"), b.getPrice());
        assertEquals(10, b.getAvailableCopies());
        assertEquals(1L, b.getAuthorId());
    }

    @Test
    void generateAuthorsCsv_and_generateBooksCsv_outputContainsHeader() throws Exception {
        Author author = Author.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Austen")
                .email("jane.austen@example.com")
                .biography("English novelist")
                .birthYear(1775)
                .build();

        byte[] authorsCsv = CsvUtil.generateAuthorsCsv(List.of(author));
        String authorsOut = new String(authorsCsv, StandardCharsets.UTF_8);
        assertTrue(authorsOut.contains("First Name") && authorsOut.contains("Last Name"));
        assertTrue(authorsOut.contains("Jane"));

        Book book = Book.builder()
                .id(1L)
                .title("Test Book")
                .isbn("978-3-16-148410-0")
                .publicationYear(2023)
                .genre("Fiction")
                .price(new BigDecimal("19.99"))
                .availableCopies(10)
                .description("Sample book")
                .author(author)
                .build();

        byte[] booksCsv = CsvUtil.generateBooksCsv(List.of(book));
        String booksOut = new String(booksCsv, StandardCharsets.UTF_8);
        assertTrue(booksOut.contains("Title") && booksOut.contains("ISBN") && booksOut.contains("Author ID"));
        assertTrue(booksOut.contains("Test Book"));
    }
}
