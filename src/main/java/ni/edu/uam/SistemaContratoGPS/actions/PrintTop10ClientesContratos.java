package ni.edu.uam.SistemaContratoGPS.actions;

import net.sf.jasperreports.engine.*;
import org.openxava.actions.JasperReportBaseAction;
import ni.edu.uam.SistemaContratoGPS.reports.Top10ClientesContratosDS;

import java.util.*;

public class PrintTop10ClientesContratos extends JasperReportBaseAction {

    private JRDataSource dataSource;

    @Override
    protected JRDataSource getDataSource() throws Exception {
        return dataSource;
    }

    @Override
    protected Map<String, Object> getParameters() throws Exception {
        return new HashMap<>();
    }

    @Override
    public void execute() throws Exception {

        List<Map<String, ?>> datos = new Top10ClientesContratosDS().getData();
        this.dataSource = new net.sf.jasperreports.engine.data.JRMapCollectionDataSource(datos);

        super.execute(); // ejecuta Jasper
    }

    @Override
    protected String getJRXML() throws Exception {
        return "Top10ClientesContratos.jrxml";
    }
}