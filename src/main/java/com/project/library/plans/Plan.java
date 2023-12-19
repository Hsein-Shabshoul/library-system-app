package com.project.library.plans;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "plans")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Can not be empty")
    @Column(name = "type")
    private String type;

    @NotNull(message = "Can not be empty")
    @Column(name = "duration")
    private Integer duration;

    @NotNull(message = "Can not be empty")
    @Column(name = "quantity")
    private Integer quantity;

    @NotNull(message = "Can not be empty")
    @Column(name = "price")
    private Integer price;

}
