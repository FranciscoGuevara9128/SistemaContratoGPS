package ni.edu.uam.SistemaContratoGPS.modelo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.Required;
import org.openxava.calculators.TrueCalculator;
import org.openxava.jpa.XPersistence;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

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
    @JoinColumn(name="vehiculo_id", nullable = false, unique = true)
    private Vehiculo vehiculo;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="dispositivo_gps_id", nullable = false, unique = true)
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

    @Column(name = "activo", nullable = false)
    @Hidden
    @DefaultValueCalculator(value = TrueCalculator.class)
    private Boolean activo;

    // ===== RELACIÓN CON EVENTOS =====
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<EventoContrato> eventos = new ArrayList<>();

    // ===== CAMPOS TRANSIENT PARA SNAPSHOT (solo internos, NO en la vista) =====
    @Transient @Hidden
    private LocalDate fechaInicioOriginal;

    @Transient @Hidden
    private LocalDate fechaFinOriginal;

    @Transient @Hidden
    private String condicionesOriginal;

    @Transient @Hidden
    private String observacionesOriginal;

    @Transient @Hidden
    private EstadoContrato estadoOriginal;

    @Transient @Hidden
    private Plan planOriginal;

    @Transient @Hidden
    private Vehiculo vehiculoOriginal;

    @Transient @Hidden
    private DispositivoGPS dispositivoGPSOriginal;


    // ===== CALLBACKS JPA =====

    @PostLoad
    private void onLoad() {
        // Guardamos snapshot del estado al cargar desde BD
        this.fechaInicioOriginal   = this.fechaInicio;
        this.fechaFinOriginal      = this.fechaFin;
        this.condicionesOriginal   = this.condiciones;
        this.observacionesOriginal = this.observaciones;
        this.estadoOriginal        = this.estado;
        this.planOriginal          = this.plan;
        this.vehiculoOriginal      = this.vehiculo;
        this.dispositivoGPSOriginal = this.dispositivoGPS;
    }

    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.activo == null) {
            this.activo = true;
        }

        // Evento de creación (se guarda por cascade)
        EventoContrato evento = new EventoContrato();
        evento.setContrato(this);
        evento.setTipoEvento(TipoEventoContrato.CREACION);
        evento.setFechaHora(LocalDateTime.now());
        evento.setDescripcion("Contrato creado");
        evento.setUsuario(this.creadoPor);

        this.eventos.add(evento);
    }

    @PreUpdate
    protected void onUpdate() {
        StringBuilder detalles = new StringBuilder();

        agregarCambio(detalles, "fechaInicio",
                fechaInicioOriginal, fechaInicio);

        agregarCambio(detalles, "fechaFin",
                fechaFinOriginal, fechaFin);

        agregarCambio(detalles, "condiciones",
                condicionesOriginal, condiciones);

        agregarCambio(detalles, "observaciones",
                observacionesOriginal, observaciones);

        agregarCambio(detalles, "estado",
                estadoOriginal, estado);

        agregarCambio(detalles, "plan",
                planOriginal, plan);

        agregarCambio(detalles, "vehiculo",
                vehiculoOriginal, vehiculo);

        agregarCambio(detalles, "dispositivoGPS",
                dispositivoGPSOriginal, dispositivoGPS);

        if (detalles.length() == 0) {
            // No hubo cambios reales
            return;
        }

        EventoContrato evento = new EventoContrato();
        evento.setContrato(this);
        evento.setTipoEvento(TipoEventoContrato.ACTUALIZACION);
        evento.setFechaHora(LocalDateTime.now());
        evento.setDescripcion(detalles.toString());
        evento.setUsuario(this.creadoPor);

        // Aquí SÍ usamos XPersistence, pero solo en update
        XPersistence.getManager().persist(evento);
    }

    // ===== HELPERS =====

    private void agregarCambio(StringBuilder sb, String campo, Object viejo, Object nuevo) {
        if (iguales(viejo, nuevo)) return;

        if (sb.length() > 0) sb.append("\n");

        sb.append(campo)
                .append(": ")
                .append(valor(viejo))
                .append(" -> ")
                .append(valor(nuevo));
    }

    private boolean iguales(Object a, Object b) {
        if (a == null) return b == null;
        return a.equals(b);
    }

    private String valor(Object o) {
        if (o == null) return "(null)";

        // Aquí puedes personalizar cómo se muestran entidades
        if (o instanceof Plan) {
            return ((Plan) o).getNombre(); // ejemplo
        }
        if (o instanceof Vehiculo) {
            return ((Vehiculo) o).getPlaca(); // ejemplo
        }
        if (o instanceof DispositivoGPS) {
            return ((DispositivoGPS) o).getImei(); // ejemplo
        }

        return o.toString();
    }
}