package ni.edu.uam.SistemaContratoGPS.actions;

import net.sf.jasperreports.engine.*;
import org.openxava.actions.JasperReportBaseAction;
import org.openxava.jpa.XPersistence;

import ni.edu.uam.SistemaContratoGPS.modelo.Contrato;

import java.util.*;

public class ImprimirContratoAction extends JasperReportBaseAction {

    @Override
    protected JRDataSource getDataSource() throws Exception {
        // No usamos datasource; el JRXML usa parámetros
        return new JREmptyDataSource();
    }

    @Override
    protected Map<String, Object> getParameters() throws Exception {

        Map<String, Object> params = new HashMap<>();

        // Obtener el ID del contrato desde la vista
        String contratoId = (String) getView().getValue("contratoId");

        if (contratoId == null) {
            throw new IllegalArgumentException("Debe seleccionar un contrato");
        }

        // Cargar contrato desde BD
        Contrato contrato = XPersistence.getManager().find(Contrato.class, contratoId);

        // Agregar parámetros para Jasper
        params.put("contratoId", contratoId);
        params.put("cliente", contrato.getCliente().getNombre());
        params.put("cedula", contrato.getCliente().getDocumento());

        params.put("vehiculo", contrato.getVehiculo().getPlaca());
        params.put("vehiculoPlaca", contrato.getVehiculo().getPlaca());
        params.put("vin", contrato.getVehiculo().getVin());
        params.put("marca", contrato.getVehiculo().getMarca());
        params.put("modelo", contrato.getVehiculo().getModelo());

        params.put("imei", contrato.getDispositivoGPS().getImei());

        params.put("fechaInicio",
                contrato.getFechaInicio() != null
                        ? java.sql.Date.valueOf(contrato.getFechaInicio())
                        : null
        );

        params.put("fechaFin",
                contrato.getFechaFin() != null
                        ? java.sql.Date.valueOf(contrato.getFechaFin())
                        : null
        );

        params.put("plan", contrato.getPlan().getNombre());
        params.put("precioBase", String.valueOf(contrato.getPlan().getPrecioBase()));

        // Cuerpo del contrato
        String cuerpo =
                "PRIMERA - OBJETO DEL CONTRATO:\n"
                        + "El presente contrato tiene por objeto la prestación del servicio de localización, monitoreo y "
                        + "gestión vehicular mediante el dispositivo GPS instalado en el vehículo descrito en este documento. "
                        + "SISEK GPS se compromete a brindar acceso a la plataforma en línea, generación de reportes y "
                        + "funciones de seguimiento en tiempo real, según las características del plan contratado.\n\n"

                        + "SEGUNDA - DECLARACIONES DEL CLIENTE:\n"
                        + "El CLIENTE declara que los datos personales y la información del vehículo suministrados son verídicos. "
                        + "Asimismo, reconoce que el dispositivo GPS es propiedad de SISEK GPS y que no podrá ser removido, "
                        + "desconectado o manipulado sin autorización.\n\n"

                        + "TERCERA - ALCANCE DEL SERVICIO:\n"
                        + "El servicio incluye acceso a la plataforma de monitoreo, visualización del recorrido, alertas básicas "
                        + "(apagado, encendido, velocidad), ubicación en tiempo real y soporte técnico. No incluye reparación "
                        + "de daños causados por mala manipulación, accidentes, fluctuaciones eléctricas o intervención no autorizada.\n\n"

                        + "CUARTA - RESPONSABILIDADES:\n"
                        + "El CLIENTE es responsable del uso adecuado del dispositivo GPS. Cualquier daño ocasionado por "
                        + "vandalismo, golpes, cortocircuitos, manipulación del sistema eléctrico del vehículo o intervención de terceros "
                        + "será asumido por el CLIENTE.\n\n"

                        + "QUINTA - PAGO Y FACTURACIÓN:\n"
                        + "El CLIENTE acepta pagar la tarifa correspondiente al plan seleccionado, cuyo precio base es definido "
                        + "por SISEK GPS. Los pagos deben realizarse puntualmente según el ciclo de facturación establecido.\n\n"

                        + "SEXTA - CONFIDENCIALIDAD Y USO DE DATOS:\n"
                        + "El CLIENTE autoriza el uso, procesamiento y almacenamiento de los datos generados por el vehículo, "
                        + "exclusivamente para la prestación del servicio contratado.\n\n"

                        + "SÉPTIMA - VIGENCIA:\n"
                        + "El contrato tiene vigencia desde la fecha de inicio y finalizará automáticamente en la fecha indicada, "
                        + "salvo renovación o suspensión por incumplimiento.\n\n"

                        + "OCTAVA - TERMINACIÓN:\n"
                        + "SISEK GPS podrá suspender el servicio en caso de falta de pago, manipulación del GPS o uso indebido. "
                        + "La terminación del contrato no libera al CLIENTE de obligaciones pendientes.\n\n"

                        + "NOVENA - ACEPTACIÓN:\n"
                        + "El CLIENTE declara haber leído, comprendido y aceptado todas las cláusulas del presente contrato, "
                        + "firmando en señal de conformidad.\n\n";

        params.put("cuerpoContrato", cuerpo);

        return params;
    }

    @Override
    protected String getJRXML() throws Exception {
        return "ContratoGPS.jrxml";
    }
}
