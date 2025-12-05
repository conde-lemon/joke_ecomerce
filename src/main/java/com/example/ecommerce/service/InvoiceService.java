package com.example.ecommerce.service;

import com.example.ecommerce.model.DetallePedido;
import com.example.ecommerce.model.Pedido;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para generar boletas en formato PDF usando JasperReports.
 */
@Service
public class InvoiceService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Genera un PDF de boleta para un pedido dado usando JasperReports.
     *
     * @param pedido El pedido para el cual generar la boleta
     * @return Array de bytes con el contenido del PDF
     */
    public byte[] generateInvoicePdf(Pedido pedido) {
        try {
            // Cargar el archivo .jrxml de la plantilla
            InputStream jasperStream = new ClassPathResource("reports/boleta.jrxml").getInputStream();

            // Compilar el reporte
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);

            // Preparar los parámetros
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("numeroBoleta", pedido.getId());
            parameters.put("fechaBoleta", pedido.getFechaPedido() != null ?
                    pedido.getFechaPedido().format(DATE_FORMAT) : "N/A");
            parameters.put("estadoBoleta", pedido.getEstado().toString());

            if (pedido.getUsuario() != null) {
                parameters.put("nombreCliente", pedido.getUsuario().getNombre());
                parameters.put("emailCliente", pedido.getUsuario().getCorreo());
            } else {
                parameters.put("nombreCliente", "Cliente Anónimo");
                parameters.put("emailCliente", "N/A");
            }

            // Calcular totales
            BigDecimal subtotalGeneral = BigDecimal.ZERO;
            List<BoletaItem> items = new ArrayList<>();

            for (DetallePedido detalle : pedido.getDetalles()) {
                BoletaItem item = new BoletaItem();
                item.setCodigo(detalle.getProducto().getId());
                item.setProducto(detalle.getProducto().getNombre());
                item.setCantidad(detalle.getCantidad());
                item.setPrecioUnitario(detalle.getPrecioUnitario());
                item.setSubtotalItem(detalle.getSubtotal());
                items.add(item);

                subtotalGeneral = subtotalGeneral.add(detalle.getSubtotal());
            }

            BigDecimal igv = subtotalGeneral.multiply(new BigDecimal("0.18"));
            BigDecimal total = subtotalGeneral.add(igv);

            parameters.put("subtotal", subtotalGeneral);
            parameters.put("igv", igv);
            parameters.put("total", total);

            // Crear el data source con los items
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(items);

            // Llenar el reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Exportar a PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar la boleta con JasperReports: " + e.getMessage(), e);
        }
    }

    /**
     * Clase interna para representar un item de la boleta.
     */
    public static class BoletaItem {
        private Long codigo;
        private String producto;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotalItem;

        // Getters y Setters
        public Long getCodigo() {
            return codigo;
        }

        public void setCodigo(Long codigo) {
            this.codigo = codigo;
        }

        public String getProducto() {
            return producto;
        }

        public void setProducto(String producto) {
            this.producto = producto;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public BigDecimal getSubtotalItem() {
            return subtotalItem;
        }

        public void setSubtotalItem(BigDecimal subtotalItem) {
            this.subtotalItem = subtotalItem;
        }
    }
}

