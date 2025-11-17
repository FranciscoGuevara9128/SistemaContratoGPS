package ni.edu.uam.SistemaContratoGPS.modelo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.Hidden;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name="vehiculo")
@Getter
@Setter

public class Vehiculo {
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String vehiculoId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cliente_id")
    private Cliente cliente;

    @Column(name="placa", length = 60, nullable = false, unique = true)
    private String placa;

    @Column(name="vin", length = 30, nullable = false, unique = true)
    private String vin;

    @Column(name="marca", length = 30, nullable = false)
    private String marca;

    @Column(name="modelo", length = 30, nullable = false)
    private String modelo;

    @Column(name="anio", nullable = false)
    @Min(message="El año no puede ser menor a 1900", value=1900)
    private Integer anio;

    @Column(name="color", length = 30, nullable = false)
    private String color;

    @Column(name="activo", nullable = false, columnDefinition = "boolean default true")
    private Boolean activo;
}