package ni.edu.uam.SistemaContratoGPS.modelo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.Hidden;

import javax.persistence.*;

@Entity
@Table(name="planes")
@Getter
@Setter

public class Plan {
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String clienteId;

    @Column(name="nombre", length = 30, nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_plan")
    private Periodicidad tipoPlan;

    @Column(name="precio_base", nullable = false)
    private Double precioBase;

    @Column(name="descripcion", length = 200)
    private String descripcion;

    @Column(name = "activo", nullable = false)
    @Required
    @DefaultValueCalculator(value = TrueCalculator.class)
    private Boolean activo;
}