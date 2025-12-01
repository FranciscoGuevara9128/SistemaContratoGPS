package ni.edu.uam.SistemaContratoGPS.reports;

import org.openxava.jpa.XPersistence;

import java.util.*;

public class Top10ClientesContratosDS {

    @SuppressWarnings("unchecked")
    public List<Map<String, ?>> getData() {

        List<Object[]> rows = XPersistence.getManager()
                .createQuery(
                        "SELECT c.cliente.nombre, COUNT(c) " +
                                "FROM Contrato c " +
                                "WHERE c.estado = ni.edu.uam.SistemaContratoGPS.modelo.EstadoContrato.ACTIVO " +
                                "GROUP BY c.cliente.nombre " +
                                "ORDER BY COUNT(c) DESC"
                )
                .setMaxResults(10)
                .getResultList();

        List<Map<String, ?>> datos = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> map = new HashMap<>();
            map.put("cliente", (String) row[0]);
            map.put("total", ((Number) row[1]).longValue());
            datos.add(map);
        }

        return datos;
    }
}