package com.project.library.authors;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class AuthorController {


    private final AuthorService authorService;
    @GetMapping("/authors")
    public ResponseEntity<List<Author>> getAllAuthors(){
        return ResponseEntity.ok(authorService.getAllAuthors());
    }
    @GetMapping("/authors/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        Author author = authorService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }
    @GetMapping("/authors/name/{name}")
    public ResponseEntity<List<Author>> getAuthorByNameContaining(@PathVariable String name){
        return ResponseEntity.ok(authorService.getAuthorByNameContaining(name));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/authors")
    public ResponseEntity<Author> createAuthor(@Valid @RequestBody Author author){
        return ResponseEntity.ok(authorService.createAuthor(author));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/authors/{id}")
    public ResponseEntity<Author> updateAuthor(@Valid @PathVariable Long id, @RequestBody Author authorDetails){
        return ResponseEntity.ok(authorService.updateAuthor(id, authorDetails));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/authors/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteAuthor(@PathVariable Long id){
        return ResponseEntity.ok(authorService.deleteAuthor(id));
    }
}
