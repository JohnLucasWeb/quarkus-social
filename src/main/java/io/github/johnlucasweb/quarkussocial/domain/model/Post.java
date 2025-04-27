package io.github.johnlucasweb.quarkussocial.domain.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //id do post
    private Long id;

    @Column(name = "post_text")
    //texto do post
    private String text;

    @Column(name = "dateTime")
    //data do post
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    //user do post
    private User user;

}

