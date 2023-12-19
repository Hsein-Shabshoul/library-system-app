package com.project.library.books;


import com.project.library.authors.Author;
import com.project.library.authors.AuthorRepository;
import com.project.library.exception.BadRequestException;
import com.project.library.exception.ResourceNotFoundException;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
//@AllArgsConstructor
//all args needed for the HashOperation to have a constructor to work in test mocking
//need test make it final and remove all args
//using autowired instead solves both service run and unit tests
public class BookService {
    @Autowired
    private  BookRepository bookRepository;
    @Autowired
    private  AuthorRepository authorRepository;

    @Value("${redis.reference}")
    String hashReference= "Book";
    @Autowired
    @Resource(name="redisTemplate")  // 'redisTemplate' is defined as a Bean in CacheConfiguration.java
    private HashOperations<String, Long, Book> hashOperations;

    public Map<Long, Book> getAllBooks(){
        log.info("Requested All Books.");
        //return bookRepository.findAll();
        return hashOperations.entries(hashReference);
    }
    public Book createBooks(Book book){
        Book newBook = bookRepository.save(book);
        hashOperations.putIfAbsent(hashReference, newBook.getId(), book);
        log.info("New Book Added:\n{}", newBook);
        return newBook;
    }
    public Book createBookWithAuthor(Long author_id, Book bookDetails){
        Book book = authorRepository.findById(author_id).map(author -> {
            bookDetails.setAuthor(author);
            return bookRepository.save(bookDetails);
        }).orElseThrow(() -> new BadRequestException("No Author was found with ID: " + author_id));
        hashOperations.putIfAbsent(hashReference, bookDetails.getId(), bookDetails);
        return book;
    }
    public List<Book> getAllBooksByAuthorId(Long author_id){
        if(!authorRepository.existsById(author_id)){
            throw new BadRequestException("No Author was found with ID="+author_id);
        }
        List<Book> books = bookRepository.findByAuthorId(author_id);
        return books;
    }

    public List<Book> getAllBooksByAuthorName(String name){
        List<Author> author = authorRepository.findByNameContaining(name);
        if(author.isEmpty()){
            throw new BadRequestException("No Author was found with name = "+name);
        }
        List<Book> books = new java.util.ArrayList<>();
        for (Author value : author) {
            books.addAll(bookRepository.findByAuthorId(value.getId()));
        }
        return books;
    }
    public Book getBookById(Long id) {
        Book book= bookRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("No Book was found with ID: " + id));
        log.info("Requested Book with id=" + id + ".\n" + book);
        hashOperations.putIfAbsent(hashReference, book.getId(), book);
        return book;
    }
    public List<Book> getBookByTitleContaining(String title) {
        List<Book> book = bookRepository.findByTitleContaining(title);
        log.info("Job Titles containing " + title + "\n" + book);
        return book;
    }
    public Book updateBook(Long id, Book bookDetails){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("No Book was found to edit with ID: " + id));
        book.setTitle(bookDetails.getTitle());
        book.setDescription(bookDetails.getDescription());
        book.setQuantity(bookDetails.getQuantity());
        Book updatedBook = bookRepository.save(book);
        hashOperations.put(hashReference, updatedBook.getId(), updatedBook);
        log.info("Updated Book with id={}\n{}", id, updatedBook);
        return updatedBook;
    }
    public Map<String, Boolean> deleteBook(Long id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Book was found to delete with ID: " + id));
        bookRepository.delete(book);
        hashOperations.delete(hashReference, id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        log.info("Deleted Book with id={}\nDeleted details were: {}",id, book);
        return response;
    }
}