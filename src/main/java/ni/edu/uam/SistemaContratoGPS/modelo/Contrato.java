package ni.edu.uam.SistemaContratoGPS.modelo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.Hidden;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="contrato")
@Getter
@Setter
@NoArgsConstructor
public class Contrato {
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String contratoId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="dispositivo_gps_id", nullable = false)
    private DispositivoGPS dispositivoGPS;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="plan_id", nullable = false)
    private Plan plan;

    @Column(name="fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name="fecha_fin", nullable = false)
    private LocalDate fechaFin;
    @AssertTrue(message = "La fecha fin debe ser posterior o igual a la fecha inicio")
    public boolean isFechasValidas() {
        return fechaFin == null || fechaInicio == null || !fechaFin.isBefore(fechaInicio);
    }

    @Enumerated(EnumType.STRING)
    @Column(name="estado", nullable = false)
    private EstadoContrato estado;

    @Column(name="condiciones", length = 500)
    private String condiciones;

    @Column(name="observaciones", length = 500)
    private String observaciones;

    @ManyToOne
    @JoinColumn(name="usuario_id", nullable = false)
    private Usuario creadoPor;

    @Column(name="fecha_creacion", nullable = false)
    @Hidden
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }

    @Column(name="activo", nullable = false, columnDefinition = "boolean default true")
    private Boolean activo;
}
