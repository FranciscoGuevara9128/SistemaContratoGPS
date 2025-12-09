package ni.edu.uam.SistemaContratoGPS.modelo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
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
@Tab(properties="cliente.tipoCliente, cliente.nombre, vehiculo.placa, dispositivoGPS.imei, fechaInicio, fechaFin, estado")
@View(
        members=
                "cliente;" +
                        "datosPrincipales { " +
                        "   vehiculo, dispositivoGPS; " +
                        "   plan, estado; " +
                        "} " +

                        "fechas { " +
                        "   fechaInicio; " +
                        "   fechaFin; " +
                        "} " +

                        "detalles { " +
                        "   condiciones; " +
                        "   observaciones; " +
                        "} " +

                        "auditoria { " +
                        "   creadoPor; " +
                        "   fechaCreacion; " +
                        "   eventos; " +
                        "} "
)




public class Contrato {

    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String contratoId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cliente_id", nullable = false)
    @DescriptionsList(descriptionProperties = "nombre")
    @Required
    private Cliente cliente;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="vehiculo_id", nullable = false, unique = true)
    @DescriptionsList(
            descriptionProperties = "placa",
            condition = "vehiculoId NOT IN (" +
                    "select c.vehiculo.vehiculoId from Contrato c " +
                    "where c.activo = true" +
                    ")"
    )
    @Required
    private Vehiculo vehiculo;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="dispositivo_gps_id", nullable = false, unique = true)
    @DescriptionsList(
            descriptionProperties = "imei",
            condition = "dispositivoGPSId NOT IN (" +
                    "select c.dispositivoGPS.dispositivoGPSId from Contrato c " +
                    "where c.activo = true" +
                    ")"
    )
    @Required
    private DispositivoGPS dispositivoGPS;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="plan_id", nullable = false)
    @DescriptionsList(descriptionProperties = "nombre")
    @Required
    private Plan plan;

    @Column(name="fecha_inicio", nullable = false)
    @Required
    private LocalDate fechaInicio;

    @Column(name="fecha_fin", nullable = false)
    @ReadOnly
    private LocalDate fechaFin;

    @AssertTrue(message = "La fecha fin debe ser posterior o igual a la fecha inicio")
    @Hidden
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
    @ReadOnly
    private Usuario creadoPor;

    @Column(name="fechaCreacion", nullable = false)
    @Hidden
    private LocalDateTime fechaCreacion;

    @Column(name = "activo", nullable = false)
    @Hidden
    @DefaultValueCalculator(value = TrueCalculator.class)
    private Boolean activo;

    // ===== RELACI?N CON EVENTOS =====
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

        // Asignar usuario actual si no est? asignado
        if (this.creadoPor == null) {
            String username = org.openxava.util.Users.getCurrent();

            this.creadoPor = XPersistence.getManager()
                    .createQuery("from Usuario u where u.nombreUsuario = :user", Usuario.class)
                    .setParameter("user", username)
                    .getSingleResult();
        }

        calcularFechaFin();

        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.activo == null) {
            this.activo = true;
        }

        // Cambiar estado del GPS
        if (this.dispositivoGPS != null) {
            this.dispositivoGPS.setEstadoGPS(EstadoGPS.ASIGNADO);
            XPersistence.getManager().merge(this.dispositivoGPS);
        }

        if (this.estado == null) {
            this.estado = EstadoContrato.ACTIVO;
        }

        EventoContrato evento = new EventoContrato();
        evento.setContrato(this);
        evento.setTipoEvento(TipoEventoContrato.CREACION);
        evento.setFechaHora(LocalDateTime.now());
        evento.setDescripcion("Contrato creado");
        evento.setUsuario(this.creadoPor); // <-- ya lo tenemos asignado

        this.eventos.add(evento);
    }

    @PreUpdate
    protected void onUpdate() {

        calcularFechaFin();

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

        /*String username = org.openxava.util.Users.getCurrent();
        Usuario usuarioActual = XPersistence.getManager()
                .createQuery("from Usuario u where u.nombreUsuario = :user", Usuario.class)
                .setParameter("user", username)
                .getSingleResult();*/

        EventoContrato evento = new EventoContrato();
        evento.setContrato(this);
        evento.setTipoEvento(TipoEventoContrato.ACTUALIZACION);
        evento.setFechaHora(LocalDateTime.now());
        evento.setDescripcion(detalles.toString());
        evento.setUsuario(this.creadoPor); // <-- usamos el mismo usuario que cre? el contrato

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

        // Aqu? puedes personalizar c?mo se muestran entidades
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

    private void calcularFechaFin() {
        if (fechaInicio == null || plan == null) return;

        switch (plan.getTipoPlan()) {
            case ANUAL:
                fechaFin = fechaInicio.plusYears(1);
                break;
            case SEMESTRAL:
                fechaFin = fechaInicio.plusMonths(6);
                break;
            case MENSUAL:
                fechaFin = fechaInicio.plusMonths(1);
                break;
        }
    }

}