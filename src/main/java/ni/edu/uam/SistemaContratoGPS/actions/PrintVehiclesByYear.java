package ni.edu.uam.SistemaContratoGPS.actions;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.openxava.actions.JasperReportBaseAction;
import ni.edu.uam.SistemaContratoGPS.reports.*;

import java.util.*;

public class PrintVehiclesByYear extends JasperReportBaseAction {

    @Override
    protected JRDataSource getDataSource() throws Exception {
        return new JREmptyDataSource();
    }

    @Override
    protected Map<String, Object> getParameters() throws Exception {

        Map<String, Object> params = new HashMap<>();

        // 1?? obtener datos reales del datasource
        List<Map<String, Object>> datosOriginales =
                new VehiculosPorAnioDS().getData();

        // 2?? agrupar dinámicamente en intervalos
        Collection<Map<String, Object>> datosAgrupados =
                agruparPorIntervalos(datosOriginales, 5);

        // 3?? enviar al reporte
        params.put("RANGOS", datosAgrupados);

        return params;
    }

    @Override
    protected String getJRXML() throws Exception {
        return "VehiclesByYear.jrxml";
    }

    public static Collection<Map<String, Object>> agruparPorIntervalos(
            List<Map<String, Object>> lista, int k) {

        int min = lista.stream().mapToInt(m -> (int)m.get("anio")).min().getAsInt();
        int max = lista.stream().mapToInt(m -> (int)m.get("anio")).max().getAsInt();

        int rango = max - min;
        int ancho = (int) Math.ceil((double) rango / k);

        List<int[]> intervalos = new ArrayList<>();
        int inicio = min;

        for (int i = 0; i < k; i++) {
            int fin = inicio + ancho;
            intervalos.add(new int[]{inicio, fin});
            inicio = fin + 1;
        }

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (int[] intervalo : intervalos) {
            int ini = intervalo[0];
            int fin = intervalo[1];

            long total = lista.stream()
                    .filter(m -> {
                        int anio = (int) m.get("anio");
                        return anio >= ini && anio <= fin;
                    })
                    .mapToLong(m -> ((Number)m.get("cantidad")).longValue())
                    .sum();

            Map<String, Object> fila = new HashMap<>();
            fila.put("rango", ini + " - " + fin);
            fila.put("total", total);

            resultado.add(fila);
        }

        return resultado;
    }
}