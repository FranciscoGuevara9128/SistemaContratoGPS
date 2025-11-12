package ni.edu.uam.SistemaContratoGPS.modelo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.Hidden;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name="cliente")
@Getter
@Setter

public class Cliente {

    @Id
    @Hidden
    @GeneratedValue(generator = "Cliente_id")
    @GenericGenerator(name = "Cliente_id", strategy = "uuid2")
    private String Clienteid;

    @Column(name="nombreCliente", length = 60, nullable = false)
    private String nombreCliente;

    @Column(name="documento", length = 60, nullable = false)
    @Size(min=8, max=60)
    private String documento;

    @Column(name="tipoCliente")
    private String tipoCliente;

    @Column(name="telefono")
    private int telefono;

    @Column(name="email", length = 60, nullable = false)
    private String email;

    @Column(name="direccion", length = 100, nullable = false)
    private String direccion;

    @Column (name="activo")
    private boolean activo;

    @Column (name="fechaRegistro")
    private LocalDateTime fechaRegistro;
}
