package com.project.library.books;

import com.project.library.authors.Author;
import com.project.library.authors.AuthorRepository;
import com.project.library.exception.BadRequestException;
import com.project.library.exception.ResourceNotFoundException;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest(classes = TestConfig.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private Logger log; // Mock Logger object

//    @Mock
//    private RedisTemplate<String, Book> redisTemplate;

    @Mock
    private HashOperations<String, Long, Book> hashOperations;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        bookService = new BookService(bookRepository, authorRepository, hashOperations);
    }


    @Test
    void getAllBooks() {
        Map<Long, Book> books = Collections.emptyMap();
        when(hashOperations.entries("Book")).thenReturn(books);
        Map<Long, Book> result = bookService.getAllBooks();
        assertSame(books, result);
        verify(hashOperations).entries("Book");
    }

    @Test
    void testGetAllBooksByAuthorId_ExistingAuthor() {
        // Arrange
        Long authorId = 1L;
        Author author = new Author();
        author.setId(authorId);
        when(authorRepository.existsById(authorId)).thenReturn(true);

        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setAuthor(author);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setAuthor(author);

        List<Book> expectedBooks = Arrays.asList(book1, book2);
        when(bookRepository.findByAuthorId(authorId)).thenReturn(expectedBooks);

        // Act
        List<Book> result = bookService.getAllBooksByAuthorId(authorId);

        // Assert
        assertEquals(expectedBooks, result);
        verify(authorRepository, times(1)).existsById(authorId);
        verify(bookRepository, times(1)).findByAuthorId(authorId);
    }

    @Test
    void testGetAllBooksByAuthorId_NonExistingAuthor() {
        // Arrange
        Long authorId = 1L;
        when(authorRepository.existsById(authorId)).thenReturn(false);

        // Act and Assert
        assertThrows(BadRequestException.class, () -> bookService.getAllBooksByAuthorId(authorId));

        // Verify that bookRepository.findByAuthorId() is not called when author does not exist
        verify(bookRepository, never()).findByAuthorId(authorId);
    }

    @Test
    void testGetAllBooksByAuthorName_ExistingAuthors() {
        // Arrange
        String authorName = "John";
        Author author1 = new Author();
        author1.setId(1L);
        author1.setName("John Doe");

        Author author2 = new Author();
        author2.setId(2L);
        author2.setName("John Smith");

        List<Author> authors = Arrays.asList(author1, author2);

        when(authorRepository.findByNameContaining(authorName)).thenReturn(authors);

        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setAuthor(author1);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setAuthor(author2);

        List<Book> expectedBooks = Arrays.asList(book1, book2);

        when(bookRepository.findByAuthorId(1L)).thenReturn(Arrays.asList(book1));
        when(bookRepository.findByAuthorId(2L)).thenReturn(Arrays.asList(book2));

        // Act
        List<Book> result = bookService.getAllBooksByAuthorName(authorName);

        // Assert
        assertEquals(expectedBooks, result);
        verify(authorRepository, times(1)).findByNameContaining(authorName);
        verify(bookRepository, times(1)).findByAuthorId(1L);
        verify(bookRepository, times(1)).findByAuthorId(2L);
    }

    @Test
    void testGetAllBooksByAuthorName_NonExistingAuthors() {
        // Arrange
        String authorName = "Nonexistent";
        when(authorRepository.findByNameContaining(authorName)).thenReturn(Arrays.asList());

        // Act and Assert
        assertThrows(BadRequestException.class, () -> bookService.getAllBooksByAuthorName(authorName));

        // Verify that bookRepository.findByAuthorId() is not called when authors do not exist
        verify(bookRepository, never()).findByAuthorId(anyLong());
    }

    @Test
    void testGetBookByTitleContaining_ExistingBooks() {
        // Arrange
        String title = "Java";
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Java Programming");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Advanced Java Concepts");

        List<Book> expectedBooks = Arrays.asList(book1, book2);

        when(bookRepository.findByTitleContaining(title)).thenReturn(expectedBooks);

        // Act
        List<Book> result = bookService.getBookByTitleContaining(title);

        // Assert
        assertEquals(expectedBooks, result);
        verify(bookRepository, times(1)).findByTitleContaining(title);
    }

    @Test
    void createBooks_shouldSaveBookAndAddToHashOperations() {
        Book savedBook = new Book(/* initialize saved Book object */);
        savedBook.setId(1L);
        savedBook.setTitle("test");
        savedBook.setDescription("test");
        savedBook.setQuantity(1);

        Book inputBook = new Book(/* initialize input Book object */);
        inputBook.setTitle("test");
        inputBook.setDescription("test");
        inputBook.setQuantity(1);

        when(bookRepository.save(inputBook)).thenReturn(savedBook);
        when(hashOperations.putIfAbsent(any(), any(), any())).thenReturn(true);
        // Act
        Book result = bookService.createBooks(inputBook);
        // Assertj
        assertEquals(savedBook, result);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(hashOperations, times(1)).putIfAbsent(anyString(), anyLong(), any(Book.class));
    }

    @Test
    void createBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Title1");
        book.setDescription("Description1");
        book.setQuantity(0);
        book.setAuthor(new Author());

        when(bookRepository.save(book)).thenReturn(book);
        when(hashOperations.putIfAbsent(any(), any(), any())).thenReturn(true);
        Book result = bookService.createBooks(book);
        assertNotNull(result);
        assertEquals("Title1", result.getTitle());
        assertEquals("Description1", result.getDescription());
    }

    @Test
    void testGetBookByTitleContaining_NonExistingBooks() {
        // Arrange
        String title = "Nonexistent";
        when(bookRepository.findByTitleContaining(title)).thenReturn(Arrays.asList());

        // Act
        List<Book> result = bookService.getBookByTitleContaining(title);

        // Assert
        assertTrue(result.isEmpty());
        verify(bookRepository, times(1)).findByTitleContaining(title);
    }

    @Test
    void createBookWithAuthor_shouldCreateBookAndAddToHashOperations() {
        // Arrange
        Long authorId = 1L;
        Author author = new Author();
        author.setId(authorId);
        author.setName("Test Author");

        Book bookDetails = new Book();
        bookDetails.setTitle("Test Book");
        bookDetails.setDescription("Test Description");
        bookDetails.setQuantity(1);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("Test Book");
        savedBook.setDescription("Test Description");
        savedBook.setQuantity(1);
        savedBook.setAuthor(author);

        // Mocking
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(bookRepository.save(bookDetails)).thenReturn(savedBook);
        when(hashOperations.putIfAbsent(any(), any(), any())).thenReturn(true);
        // Act
        Book result = bookService.createBookWithAuthor(authorId, bookDetails);
        // Assert
        assertEquals(savedBook, result);
        // Verify interactions
        verify(authorRepository, times(1)).findById(authorId);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(hashOperations, times(1)).putIfAbsent(any(), any(), any());
    }

    @Test
    void getBookById_shouldReturnBookAndAddToHashOperations() {
        // Arrange
        Long bookId = 1L;

        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("Test Book");
        existingBook.setDescription("Test Description");
        existingBook.setQuantity(1);

        // Mocking
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(hashOperations.putIfAbsent(any(), any(), any())).thenReturn(true);

        // Act
        Book result = bookService.getBookById(bookId);

        // Assert
        assertEquals(existingBook, result);

        // Verify interactions
        verify(bookRepository, times(1)).findById(bookId);
        verify(hashOperations, times(1)).putIfAbsent(any(), any(), any());
    }

    @Test
    void getBookById_shouldThrowExceptionForNonexistentBook() {
        // Arrange
        Long nonExistentBookId = 2L;

        // Mocking
        when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

        // Act and Assert
        // Using assertThrows to verify that the method throws BadRequestException
        assertThrows(BadRequestException.class, () -> bookService.getBookById(nonExistentBookId));

        // Verify interactions
        verify(bookRepository, times(1)).findById(nonExistentBookId);
        verify(hashOperations, never()).putIfAbsent(anyString(), anyLong(), any(Book.class));
    }

    @Test
    void updateBook_shouldUpdateBookAndAddToHashOperations() {
        Long bookId = 1L;
        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("Old Title");
        existingBook.setDescription("Old Description");
        existingBook.setQuantity(1);

        Book updatedBookDetails = new Book();
        updatedBookDetails.setTitle("New Title");
        updatedBookDetails.setDescription("New Description");
        updatedBookDetails.setQuantity(2);

        Book updatedBook = new Book();
        updatedBook.setId(bookId);
        updatedBook.setTitle("New Title");
        updatedBook.setDescription("New Description");
        updatedBook.setQuantity(2);

        when(bookRepository.findById(eq(bookId))).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        Book result = bookService.updateBook(bookId, updatedBookDetails);

        assertEquals(updatedBook, result);

        verify(bookRepository, times(1)).findById(eq(bookId));
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(hashOperations, times(1)).put(anyString(), anyLong(), any(Book.class));
    }

    @Test
    void updateBook_shouldThrowExceptionForNonexistentBook() {
        Long nonExistentBookId = 999L;

        when(bookRepository.findById(eq(nonExistentBookId))).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> bookService.updateBook(nonExistentBookId, new Book()));

        verify(bookRepository, times(1)).findById(eq(nonExistentBookId));
        verify(bookRepository, never()).save(any(Book.class));
        verify(hashOperations, never()).put(anyString(), anyLong(), any(Book.class));
    }

    @Test
    void deleteBook_shouldDeleteBookAndRemoveFromHashOperations() {
        Long bookId = 1L;

        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("Test Book");
        existingBook.setDescription("Test Description");
        existingBook.setQuantity(1);

        when(bookRepository.findById(eq(bookId))).thenReturn(Optional.of(existingBook));

        Map<String, Boolean> result = bookService.deleteBook(bookId);

        assertTrue(result.get("deleted"));
        verify(bookRepository, times(1)).findById(eq(bookId));
        verify(bookRepository, times(1)).delete(eq(existingBook));
        verify(hashOperations, times(1)).delete(anyString(), eq(bookId));
    }

    @Test
    void deleteBook_shouldThrowExceptionForNonexistentBook() {
        Long nonExistentBookId = 999L;

        when(bookRepository.findById(eq(nonExistentBookId))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(nonExistentBookId));

        verify(bookRepository, times(1)).findById(eq(nonExistentBookId));
        verify(bookRepository, never()).delete(any(Book.class));
        verify(hashOperations, never()).delete(anyString(), anyLong());
    }

}

