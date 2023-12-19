package com.project.library.books;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/books")
    public ResponseEntity<Map<Long, Book>> getAllBooks(){
        return ResponseEntity.ok(bookService.getAllBooks());
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/books")
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book){
        return ResponseEntity.ok(bookService.createBooks(book));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/books/authors/{author_id}")
    public ResponseEntity<Book> createBookWithAuthor(@Valid @PathVariable Long author_id,
                                                             @RequestBody Book bookDetails){
        return ResponseEntity.ok(bookService.createBookWithAuthor(author_id,bookDetails));
    }
    @GetMapping("/books/authors/{author_id}")
    public ResponseEntity<List<Book>> getAllBooksByAuthorId(@PathVariable Long author_id){
        return ResponseEntity.ok(bookService.getAllBooksByAuthorId(author_id));
    }
    @GetMapping("/books/author_name/{name}")
    public ResponseEntity<List<Book>> getAllBooksByAuthorName(@PathVariable String name){
        return ResponseEntity.ok(bookService.getAllBooksByAuthorName(name));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/books/title/{title}")
    public ResponseEntity<List<Book>> getBookByTitleContaining(@PathVariable String title) {
        return ResponseEntity.ok(bookService.getBookByTitleContaining(title));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/books/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails){
        return ResponseEntity.ok(bookService.updateBook(id,bookDetails));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteBook(@PathVariable Long id){
        return ResponseEntity.ok(bookService.deleteBook(id));
    }
}
