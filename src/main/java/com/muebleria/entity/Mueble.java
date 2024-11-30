package com.muebleria.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Mueble {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column(nullable = false)
    private double precio;
<<<<<<< HEAD

=======
<<<<<<< HEAD
>>>>>>> ccc9a1fe8fb111e26b307c6259eaaa1b86c5a488
    
    // Nuevo campo para la imagen (URL o ruta local)
    @Column
    private String imagen;
<<<<<<< HEAD

=======
=======
>>>>>>> bcf8fd40bb68b8960587a3fce46128abd0ac81cb
>>>>>>> ccc9a1fe8fb111e26b307c6259eaaa1b86c5a488
}
