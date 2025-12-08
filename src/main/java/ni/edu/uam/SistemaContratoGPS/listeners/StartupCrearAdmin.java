package ni.edu.uam.SistemaContratoGPS.listeners;

import ni.edu.uam.SistemaContratoGPS.modelo.Usuario;
import ni.edu.uam.SistemaContratoGPS.modelo.TipoUsuario;
import org.openxava.jpa.XPersistence;

import javax.persistence.NoResultException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class StartupCrearAdmin implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            XPersistence.getManager()
                    .createQuery("from Usuario u where u.nombreUsuario = 'admin'")
                    .getSingleResult();

        }
        catch (NoResultException ex) {

            Usuario admin = new Usuario();
            admin.setNombre("Administrador del Sistema");
            admin.setDocumento("0000");
            admin.setTelefono("0000");
            admin.setEmail("admin@sistema.com");
            admin.setDireccion("N/A");

            admin.setNombreUsuario("admin");
            admin.setContrasenia("admin");

            admin.setRol(TipoUsuario.GERENTE);

            XPersistence.getManager().persist(admin);
            XPersistence.commit();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
