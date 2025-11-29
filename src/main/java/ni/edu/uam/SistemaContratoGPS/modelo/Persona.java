package ni.edu.uam.SistemaContratoGPS.modelo;

import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.Required;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@MappedSuperclass
public abstract class Persona {
    @Column(name="nombre", length = 60)
    @Required
    protected String nombre;

    @Column(name="documento", length = 40)
    @Required
    protected String documento;

    @Column(name="telefono", length = 20)
    @Required
    protected String telefono;

    @Column(name="email", length = 60)
    @Required
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser una dirección de email válida")
    protected String email;

    @Column(name="direccion", length = 100, nullable = false)
    private String direccion;
}
