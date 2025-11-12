package ni.edu.uam.SistemaContratoGPS.modelo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
public abstract class Persona {
    @Column(name="nombre", length = 60)
    protected String nombre;

    @Column(name="documento", length = 40)
    protected String documento;

    @Column(name="telefono", length = 20)
    protected String telefono;

    @Column(name="email", length = 60)
    protected String email;
}
