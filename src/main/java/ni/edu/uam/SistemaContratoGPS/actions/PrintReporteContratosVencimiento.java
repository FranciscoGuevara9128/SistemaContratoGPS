package ni.edu.uam.SistemaContratoGPS.actions;

import org.openxava.actions.JasperReportBaseAction;
import org.openxava.jpa.XPersistence;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;

import ni.edu.uam.SistemaContratoGPS.modelo.Contrato;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class PrintReporteContratosVencimiento extends JasperReportBaseAction {

    @Override
    protected JRDataSource getDataSource() throws Exception {
        return new JREmptyDataSource();
    }

    @Override
    protected String getJRXML() throws Exception {
        return "contrato_vencimientos.jrxml";
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected Map getParameters() throws Exception {

        Map params = new HashMap();

        // --- Fechas base ---
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMesSiguiente = hoy.plusMonths(1).withDayOfMonth(1);
        LocalDate finMesSiguiente = inicioMesSiguiente.withDayOfMonth(inicioMesSiguiente.lengthOfMonth());

        // --- Contratos que vencen el próximo mes ---
        List<Contrato> proximos = XPersistence.getManager()
                .createQuery(
                        "SELECT c FROM Contrato c WHERE c.fechaFin BETWEEN :ini AND :fin",
                        Contrato.class)
                .setParameter("ini", inicioMesSiguiente)
                .setParameter("fin", finMesSiguiente)
                .getResultList();

        // --- Contratos vencidos ---
        List<Contrato> vencidos = XPersistence.getManager()
                .createQuery(
                        "SELECT c FROM Contrato c WHERE c.fechaFin < :hoy",
                        Contrato.class)
                .setParameter("hoy", hoy)
                .getResultList();

        // --- Texto para contratos que vencen el próximo mes ---
        String textoProximos = proximos.isEmpty()
                ? "No hay contratos que venzan el próximo mes."
                : proximos.stream()
                .map(c ->
                        "Cliente: " + c.getCliente().getNombre() +
                                " | Vehículo: " + c.getVehiculo().getPlaca() +
                                " | IMEI: " + c.getDispositivoGPS().getImei() +
                                " | Fecha fin: " + c.getFechaFin()
                )
                .collect(Collectors.joining("\n"));

        // --- Texto para contratos vencidos ---
        String textoVencidos = vencidos.isEmpty()
                ? "No hay contratos vencidos."
                : vencidos.stream()
                .map(c ->
                        "Cliente: " + c.getCliente().getNombre() +
                                " | Vehículo: " + c.getVehiculo().getPlaca() +
                                " | IMEI: " + c.getDispositivoGPS().getImei() +
                                " | Fecha fin: " + c.getFechaFin()
                )
                .collect(Collectors.joining("\n"));

        params.put("PROXIMOS_TEXTO", textoProximos);
        params.put("VENCIDOS_TEXTO", textoVencidos);

        return params;
    }
}