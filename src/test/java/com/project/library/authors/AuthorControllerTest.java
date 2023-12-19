package com.project.library.authors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

//import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private AuthorController authorController;

    @Test
    void getAllAuthors() {
        List<Author> authors = mock(List.class);
        when(authorService.getAllAuthors()).thenReturn(authors);

        ResponseEntity<List<Author>> result = authorController.getAllAuthors();

        assertSame(authors, result.getBody());
        verify(authorService).getAllAuthors();
    }

    @Test
    void getAuthorById() {
        Long authorId = 1L;
        Author author = new Author();
        when(authorService.getAuthorById(authorId)).thenReturn(author);

        ResponseEntity<Author> result = authorController.getAuthorById(authorId);

        assertSame(author, result.getBody());
        verify(authorService).getAuthorById(authorId);
    }

    @Test
    void getAuthorByNameContaining() {
        String name = "John";
        List<Author> authors = mock(List.class);
        when(authorService.getAuthorByNameContaining(name)).thenReturn(authors);

        ResponseEntity<List<Author>> result = authorController.getAuthorByNameContaining(name);

        assertSame(authors, result.getBody());
        verify(authorService).getAuthorByNameContaining(name);
    }

    @Test
    void createAuthor() {
        Author author = new Author();
        when(authorService.createAuthor(author)).thenReturn(author);

        ResponseEntity<Author> result = authorController.createAuthor(author);

        assertSame(author, result.getBody());
        verify(authorService).createAuthor(author);
    }

    @Test
    void updateAuthor() {
        Long authorId = 1L;
        Author authorDetails = new Author();
        when(authorService.updateAuthor(authorId, authorDetails)).thenReturn(authorDetails);

        ResponseEntity<Author> result = authorController.updateAuthor(authorId, authorDetails);

        assertSame(authorDetails, result.getBody());
        verify(authorService).updateAuthor(authorId, authorDetails);
    }

    @Test
    void deleteAuthor() {
        Long authorId = 1L;
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", true);
        when(authorService.deleteAuthor(authorId)).thenReturn(response);

        ResponseEntity<Map<String, Boolean>> result = authorController.deleteAuthor(authorId);

        assertSame(response, result.getBody());
        verify(authorService).deleteAuthor(authorId);
    }
}
