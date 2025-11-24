package ni.edu.uam.SistemaContratoGPS.modelo;

import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.File;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.Required;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "imagen_vehiculo")
public class ImagenVehiculo {
    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @File
    @Column(length = 200)
    private String archivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    @Required
    private Vehiculo vehiculo;
}