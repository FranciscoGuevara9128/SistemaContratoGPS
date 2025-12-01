package ni.edu.uam.SistemaContratoGPS.actions;

import net.sf.jasperreports.engine.*;
import org.openxava.actions.JasperReportBaseAction;
import ni.edu.uam.SistemaContratoGPS.reports.ContratosPorPlanDS;

import java.util.*;

public class PrintContratosPorPlan extends JasperReportBaseAction {

    @Override
    protected JRDataSource getDataSource() throws Exception {
        // El datasource principal NO se usa (el gráfico usa el parámetro)
        return new JREmptyDataSource();
    }

    @Override
    protected Map<String, Object> getParameters() throws Exception {

        Map<String, Object> params = new HashMap<>();

        // OBTENER DATOS REALES
        List<Map<String, Object>> datos = new ContratosPorPlanDS().getData();

        // ENVIAR AL REPORTE
        params.put("PLANES", datos);

        return params;
    }

    @Override
    protected String getJRXML() throws Exception {
        return "ContratosPorPlan.jrxml";
    }
}