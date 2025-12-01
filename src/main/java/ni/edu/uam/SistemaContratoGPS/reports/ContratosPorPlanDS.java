package ni.edu.uam.SistemaContratoGPS.reports;

import org.openxava.jpa.XPersistence;

import java.util.*;

public class ContratosPorPlanDS {

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getData() {

        List<Object[]> rows = XPersistence.getManager()
                .createQuery(
                        "SELECT c.plan.nombre, COUNT(c) " +
                                "FROM Contrato c " +
                                "GROUP BY c.plan.nombre " +
                                "ORDER BY c.plan.nombre"
                )
                .getResultList();

        List<Map<String, Object>> datos = new ArrayList<>();

        for (Object[] row : rows) {

            Map<String, Object> map = new HashMap<>();
            map.put("plan", row[0]);
            map.put("total", ((Number) row[1]).longValue());

            datos.add(map);
        }

        return datos;
    }
}