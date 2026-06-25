package com.example.library.util;

import com.example.library.dto.AuthorDTO;
import com.example.library.dto.BookDTO;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvUtil {
    
    public static List<AuthorDTO> parseAuthorsCsv(InputStream inputStream) throws IOException {
        List<AuthorDTO> authors = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            List<String[]> rows = reader.readAll();
            
            // Skip header
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                
                AuthorDTO author = AuthorDTO.builder()
                        .firstName(row[0])
                        .lastName(row[1])
                        .email(row[2])
                        .biography(row.length > 3 ? row[3] : null)
                        .birthYear(row.length > 4 && !row[4].isEmpty() ? 
                                  Integer.parseInt(row[4]) : null)
                        .build();
                
                authors.add(author);
            }
        } catch (CsvException e) {
            log.error("Error parsing CSV", e);
            throw new IOException("Error parsing CSV", e);
        }
        
        return authors;
    }
    
    public static List<BookDTO> parseBooksCsv(InputStream inputStream) throws IOException {
        List<BookDTO> books = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            List<String[]> rows = reader.readAll();
            
            // Skip header
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                
                BookDTO book = BookDTO.builder()
                        .title(row[0])
                        .isbn(row[1])
                        .publicationYear(Integer.parseInt(row[2]))
                        .genre(row[3])
                        .price(new BigDecimal(row[4]))
                        .availableCopies(Integer.parseInt(row[5]))
                        .description(row.length > 6 ? row[6] : null)
                        .authorId(Long.parseLong(row[7]))
                        .build();
                
                books.add(book);
            }
        } catch (CsvException e) {
            log.error("Error parsing CSV", e);
            throw new IOException("Error parsing CSV", e);
        }
        
        return books;
    }
    
    public static byte[] generateAuthorsCsv(List<Author> authors) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream))) {
            // Write header
            String[] header = {"First Name", "Last Name", "Email", "Biography", "Birth Year"};
            writer.writeNext(header);
            
            // Write data
            for (Author author : authors) {
                String[] row = {
                    author.getFirstName(),
                    author.getLastName(),
                    author.getEmail(),
                    author.getBiography() != null ? author.getBiography() : "",
                    author.getBirthYear() != null ? author.getBirthYear().toString() : ""
                };
                writer.writeNext(row);
            }
        } catch (IOException e) {
            log.error("Error generating CSV", e);
        }
        
        return outputStream.toByteArray();
    }
    
    public static byte[] generateBooksCsv(List<Book> books) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream))) {
            // Write header
            String[] header = {"Title", "ISBN", "Publication Year", "Genre", 
                             "Price", "Available Copies", "Description", "Author ID"};
            writer.writeNext(header);
            
            // Write data
            for (Book book : books) {
                String[] row = {
                    book.getTitle(),
                    book.getIsbn(),
                    book.getPublicationYear().toString(),
                    book.getGenre(),
                    book.getPrice() != null ? book.getPrice().toString() : "",
                    book.getAvailableCopies() != null ? book.getAvailableCopies().toString() : "0",
                    book.getDescription() != null ? book.getDescription() : "",
                    book.getAuthor().getId().toString()
                };
                writer.writeNext(row);
            }
        } catch (IOException e) {
            log.error("Error generating CSV", e);
        }
        
        return outputStream.toByteArray();
    }
}
