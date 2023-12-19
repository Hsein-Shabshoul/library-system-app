package com.project.library.authors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

class AuthorServiceTest {
    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        when(authorRepository.findAll()).thenReturn(authors);

        List<Author> result = authorService.getAllAuthors();

        assertSame(authors, result);
        verify(authorRepository).findAll();
    }

    @Test
    void getAuthorById() {
        Long authorId = 1L;
        Author author = new Author();
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        Author result = authorService.getAuthorById(authorId);
        assertSame(author, result);
        verify(authorRepository).findById(authorId);
    }

    @Test
    void getAuthorByNameContaining() {
        String name = "John";
        List<Author> authors = new ArrayList<>();
        when(authorRepository.findByNameContaining(name)).thenReturn(authors);

        List<Author> result = authorService.getAuthorByNameContaining(name);

        assertSame(authors, result);
        verify(authorRepository).findByNameContaining(name);
    }

    @Test
    void createAuthor() {
        Author author = new Author();
        when(authorRepository.save(author)).thenReturn(author);

        Author result = authorService.createAuthor(author);

        assertSame(author, result);
        verify(authorRepository).save(author);
    }

    @Test
    void updateAuthor() {
        Long authorId = 1L;
        Author existingAuthor = new Author();
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));

        Author updatedAuthor = new Author();
        when(authorRepository.save(existingAuthor)).thenReturn(updatedAuthor);

        Author result = authorService.updateAuthor(authorId, updatedAuthor);

        assertSame(updatedAuthor, result);
        verify(authorRepository).findById(authorId);
        verify(authorRepository).save(existingAuthor);
    }

    @Test
    void deleteAuthor() {

        Long authorId = 1L;
        Author existingAuthor = new Author();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        authorService.deleteAuthor(authorId);

        verify(authorRepository).findById(authorId);
        verify(authorRepository).delete(existingAuthor);
    }

    @Test
    void testAllArgsConstructor() {
        Long id = 1L;
        String name = "John Doe";
        String about = "Author about info";

        Author author = new Author(id, name, about);
        assertEquals(id, author.getId());
        assertEquals(name, author.getName());
        assertEquals(about, author.getAbout());
    }
}