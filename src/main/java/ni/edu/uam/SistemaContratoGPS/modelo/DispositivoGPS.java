package ni.edu.uam.SistemaContratoGPS.modelo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.Hidden;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="dispositivosGPS")
@Getter
@Setter

public class DispositivoGPS {

    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String dispositivoGPSId;

    @Column(name="imei", length = 30, nullable = false, unique = true)
    private String imei;

    @Enumerated(EnumType.STRING)
    @Column(name="estadoGPS")
    private EstadoGPS estadoGPS;

    @Column(name="fabricante", length = 30, nullable = false)
    private String fabricante;

    @Column(name="modelo", length = 30, nullable = false)
    private String modelo;

    @Column(name="fechaAlta", nullable = false)
    private LocalDateTime fechaAlta;
}
