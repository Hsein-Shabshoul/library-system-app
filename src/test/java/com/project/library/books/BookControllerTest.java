package com.project.library.books;
import com.project.library.authors.Author;

import com.project.library.books.Book;
import com.project.library.books.BookController;
import com.project.library.books.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @Test
    void getAllBooks() {
        when(bookService.getAllBooks()).thenReturn(Collections.emptyMap());

        ResponseEntity<Map<Long, Book>> responseEntity = bookController.getAllBooks();

        assertEquals(Collections.emptyMap(), responseEntity.getBody());
    }

    @Test
    void createBook() {
        Book book = new Book();
        book.setId(0L);
        book.setTitle("test title");
        book.setDescription("description");
        book.setQuantity(10);
        book.setAuthor(new Author());

        when(bookService.createBooks(any(Book.class))).thenReturn(book);

        ResponseEntity<Book> responseEntity = bookController.createBook(book);

        assertEquals(book, responseEntity.getBody());
    }

    @Test
    void createBookWithAuthor() {
        Book book = new Book();
        book.setTitle("test");
        book.setDescription("test");
        book.setQuantity(10);
        book.setAuthor(new Author());

        when(bookService.createBookWithAuthor(anyLong(), any(Book.class))).thenReturn(book);

        ResponseEntity<Book> responseEntity = bookController.createBookWithAuthor(1L, book);

        assertEquals(book, responseEntity.getBody());
    }

    @Test
    void getAllBooksByAuthorId() {
        /* initialize books */
        List<Book> books = List.of();
        when(bookService.getAllBooksByAuthorId(anyLong())).thenReturn(books);

        ResponseEntity<List<Book>> responseEntity = bookController.getAllBooksByAuthorId(1L);

        assertEquals(books, responseEntity.getBody());
    }

    @Test
    void getAllBooksByAuthorName() {
        List<Book> books = List.of();
        when(bookService.getAllBooksByAuthorName(anyString())).thenReturn(books);

        ResponseEntity<List<Book>> responseEntity = bookController.getAllBooksByAuthorName("John");

        assertEquals(books, responseEntity.getBody());
    }

    @Test
    void getBookById() {
        Book book = new Book();
        when(bookService.getBookById(anyLong())).thenReturn(book);

        ResponseEntity<Book> responseEntity = bookController.getBookById(1L);

        assertEquals(book, responseEntity.getBody());
    }

    @Test
    void getBookByTitleContaining() {
        List<Book> books = List.of();
        when(bookService.getBookByTitleContaining(anyString())).thenReturn(books);

        ResponseEntity<List<Book>> responseEntity = bookController.getBookByTitleContaining("test");

        assertEquals(books, responseEntity.getBody());
    }

    @Test
    void updateBook() {
        Book book = new Book();
        when(bookService.updateBook(anyLong(), any(Book.class))).thenReturn(book);

        ResponseEntity<Book> responseEntity = bookController.updateBook(1L, book);

        assertEquals(book, responseEntity.getBody());
    }

    @Test
    void deleteBook() {
        Map<String, Boolean> response = Collections.singletonMap("deleted", true);
        when(bookService.deleteBook(anyLong())).thenReturn(response);

        ResponseEntity<Map<String, Boolean>> responseEntity = bookController.deleteBook(1L);

        assertEquals(response, responseEntity.getBody());
    }
}
