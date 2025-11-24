package ni.edu.uam.SistemaContratoGPS.modelo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.TrueCalculator;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Collection;

@Entity
@Table(name="vehiculo",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "placa"),
                @UniqueConstraint(columnNames = "vin")
        }
)
@Getter
@Setter

public class Vehiculo {
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String vehiculoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "placa", length = 12, nullable = false, unique = true)
    private String placa;

    @Column(name = "vin", length = 30, nullable = false, unique = true)
    private String vin;

    @Column(name = "marca", length = 30, nullable = false)
    private String marca;

    @Column(name = "modelo", length = 30, nullable = false)
    private String modelo;

    @Column(name = "año", nullable = false)
    @Min(message = "El año no puede ser menor a 1900", value = 1900)
    private Integer anio;

    @Column(name = "color", length = 30, nullable = false)
    private String color;

    @OneToMany
    @ListProperties("archivo")
    private Collection<ImagenVehiculo> imagenes;

    @Column(name = "activo", nullable = false)
    @Required
    @DefaultValueCalculator(value = TrueCalculator.class)
    private Boolean activo;
}