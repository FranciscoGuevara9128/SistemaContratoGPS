package ni.edu.uam.SistemaContratoGPS.modelo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.Required;
import org.openxava.calculators.TrueCalculator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table( name="cliente",
        uniqueConstraints = @UniqueConstraint(columnNames = "documento")
)
@Getter
@Setter

public class Cliente extends Persona{

    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String clienteId;

    @Enumerated(EnumType.STRING)
    @Column(name="tipoCliente")
    private TipoCliente tipoCliente;

    @Column(name = "activo", nullable = false)
    @Required
    @DefaultValueCalculator(value = TrueCalculator.class)
    private Boolean activo;

    @Column (name="fechaRegistro")
    @Hidden
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
        if (this.activo == null) {
            this.activo = true;
        }
    }
}
