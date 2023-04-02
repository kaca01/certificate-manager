package com.example.certificateback.domains;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @Column
    private String title;

//    @Column
    private String content;
}
