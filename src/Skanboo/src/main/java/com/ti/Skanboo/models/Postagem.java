package com.ti.Skanboo.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = Postagem.NOME_TABELA)
@Entity
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Postagem {

    public static final String NOME_TABELA = "postagem";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "titulo", length = 30, nullable = false)
    @NotBlank
    private String titulo;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Usuario usuario;

    @Column(name = "descricao", length = 255, nullable = false)
    @Size(min = 1, max = 255)
    private String descricao;

    // @Column(name = "hora_postagem", length = 8, nullable = false)
    // @NotNull
    // private LocalDateTime hora;

    // @Column(name = "data_postagem", length = 8, nullable = false)
    // @NotNull
    // private LocalDateTime data;

    // @Enumerated(EnumType.STRING)
    // @Column(name = "categoria_postagem", length = 20, nullable = false)
    // @NotNull
    // @NotEmpty
    // private CategoriaPostagem categoria;

    // @Column(name = "foto", length = 255)
    // @Lob //*Campo de objeto grande (Large Object)
    // private byte[] foto;

}