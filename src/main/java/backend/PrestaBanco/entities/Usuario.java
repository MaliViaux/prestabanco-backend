package backend.PrestaBanco.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter

public class Usuario {
    @Id
    @Column(name = "rut")
    private String rut;

    @Column(name = "contraseña")
    private String contraseña;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "edad")
    private Integer edad;
}
