package com.project.library.books;
import com.project.library.authors.Author;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
@Data
@Entity
@RequiredArgsConstructor
@Table(name = "books")
public class Book implements java.io.Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Title can not be null")
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "quantity")
    private int quantity;
    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    public Book(String title, String description, int quantity) {
        super();
        this.title = title;
        this.description =description;
        this.quantity = quantity;
    }
}
