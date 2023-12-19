package com.project.library.authors;

import com.project.library.exception.ResourceNotFoundException;
import com.project.library.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Log4j2
@RequiredArgsConstructor
//@CacheConfig(cacheNames = "authorCache")
public class AuthorService {
    private final AuthorRepository authorRepository;

    public List<Author> getAllAuthors(){
        log.info("Requested All Authors names");
        return authorRepository.findAll();
    }
    public Author getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("No Author was found with ID: "+id));
        log.info("Requested Author with ID="+id);
        //rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_DEPARTMENT_DIRECT,RabbitMQConfig.ROUTING_KEY_DEPARTMENT_FIND, author);
        return author;
    }
    //@Cacheable(key = "#name")
    public List<Author> getAuthorByNameContaining(String name){
        List<Author> author = authorRepository.findByNameContaining(name);
        log.info("Author containing " + name + "\n" + author);
        return author;
    }
    public Author createAuthor(Author author){
        Author newAuthor = authorRepository.save(author);
        log.info("New Author Added:\n{}", newAuthor);
        return newAuthor;
    }
    public Author updateAuthor(Long id, Author authorDetails){
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("No Author was found with ID: "+id));
        author.setName(authorDetails.getName());
        author.setAbout(authorDetails.getAbout());
        Author updatedAuthor = authorRepository.save(author);
        log.info("Updated Author with id={}\n{}",id, updatedAuthor);
        return updatedAuthor;
    }
    //@CacheEvict()
    public Map<String, Boolean> deleteAuthor(Long id){
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Author was found to delete with ID: " + id));
        authorRepository.delete(author);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        log.info("Deleted Author with id={}\nDeleted details were: {}",id, author);
        return response;
    }
}
