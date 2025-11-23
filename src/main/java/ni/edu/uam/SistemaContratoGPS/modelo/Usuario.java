package ni.edu.uam.SistemaContratoGPS.modelo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.Hidden;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name="usuario")
@Getter
@Setter
public class Usuario extends Persona{
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String usuarioId;

    @Column(name="nombreUsuario", length = 60, nullable = false)
    private String nombreUsuario;

    @Column(name="contrasenia", length = 20, nullable = false)
    @Size(min=8, max=20)
    private String contrasenia;

    @Enumerated(EnumType.STRING)
    @Column(name="rol")
    private TipoUsuario rol;

    @Column(name="activo", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean activo = true;

    @Column(name="fechaRegistro")
    @Hidden
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
    }
}
