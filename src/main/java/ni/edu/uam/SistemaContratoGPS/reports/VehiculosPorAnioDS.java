package ni.edu.uam.SistemaContratoGPS.reports;


import javax.persistence.Query;
import org.openxava.jpa.XPersistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehiculosPorAnioDS {

    public List<Map<String, Object>> getData() {

        Query query = XPersistence.getManager().createQuery(
                "select v.anio, count(v) " +
                        "from Vehiculo v " +
                        "group by v.anio " +
                        "order by v.anio asc"
        );

        List<Object[]> result = query.getResultList();
        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : result) {
            Map<String, Object> m = new HashMap<>();
            m.put("anio", row[0]);
            m.put("cantidad", row[1]);
            data.add(m);
        }

        return data;
    }
}