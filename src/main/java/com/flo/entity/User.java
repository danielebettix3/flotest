package com.flo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user", schema = "public")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid", nullable = false)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "cognome", nullable = false)
    private String cognome;

    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "indirizzo", nullable = false)
    private String indirizzo;

}