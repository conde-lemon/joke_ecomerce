package com.example.ecommerce.service;

import com.example.ecommerce.model.DetallePedido;
import com.example.ecommerce.model.Pedido;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.PedidoRepository;
import com.example.ecommerce.repository.ProductRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Genera un reporte PDF de todos los productos en inventario
     */
    public byte[] generarReporteProductos() throws Exception {
        List<Product> productos = productRepository.findAll();

        // Preparar datos para el reporte
        List<Map<String, Object>> datosProductos = new ArrayList<>();
        BigDecimal valorTotalInventario = BigDecimal.ZERO;

        for (Product producto : productos) {
            Map<String, Object> dato = new HashMap<>();
            dato.put("id", producto.getId());
            dato.put("nombre", producto.getNombre());
            dato.put("precio", producto.getPrecio());
            dato.put("stock", producto.getStock());
            dato.put("activo", producto.isActivo());

            BigDecimal valorTotal = producto.getPrecio().multiply(BigDecimal.valueOf(producto.getStock()));
            dato.put("valorTotal", valorTotal);
            valorTotalInventario = valorTotalInventario.add(valorTotal);

            datosProductos.add(dato);
        }

        // Parámetros del reporte
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("fechaReporte", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(java.time.LocalDateTime.now()));
        parametros.put("totalProductos", productos.size());
        parametros.put("valorInventario", valorTotalInventario);

        // Cargar plantilla y generar PDF
        InputStream reportStream = new ClassPathResource("reports/reporte_productos.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(datosProductos);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    /**
     * Genera una boleta de venta en PDF
     */
    public byte[] generarBoleta(Long pedidoId) throws Exception {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Preparar datos de los items
        List<Map<String, Object>> items = new ArrayList<>();
        for (DetallePedido detalle : pedido.getDetalles()) {
            Map<String, Object> item = new HashMap<>();
            item.put("codigo", detalle.getProducto().getId());
            item.put("producto", detalle.getProducto().getNombre());
            item.put("cantidad", detalle.getCantidad());
            item.put("precioUnitario", detalle.getPrecioUnitario());
            item.put("subtotalItem", detalle.getSubtotal());
            items.add(item);
        }

        // Calcular totales (IGV 18%)
        BigDecimal subtotal = pedido.getTotal().divide(new BigDecimal("1.18"), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal igv = pedido.getTotal().subtract(subtotal);

        // Parámetros
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("numeroBoleta", pedido.getId());
        parametros.put("fechaBoleta", pedido.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        parametros.put("estadoBoleta", pedido.getEstado().toString());
        parametros.put("nombreCliente", pedido.getUsuario().getNombre());
        parametros.put("emailCliente", pedido.getUsuario().getCorreo());
        parametros.put("subtotal", subtotal);
        parametros.put("igv", igv);
        parametros.put("total", pedido.getTotal());

        // Generar PDF
        InputStream reportStream = new ClassPathResource("reports/boleta.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(items);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    /**
     * Genera una orden de envío en PDF
     */
    public byte[] generarOrdenEnvio(Long pedidoId) throws Exception {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Preparar datos de productos
        List<Map<String, Object>> productos = new ArrayList<>();
        int totalProductos = 0;

        for (DetallePedido detalle : pedido.getDetalles()) {
            Map<String, Object> producto = new HashMap<>();
            producto.put("producto", detalle.getProducto().getNombre());
            producto.put("cantidad", detalle.getCantidad());
            productos.add(producto);
            totalProductos += detalle.getCantidad();
        }

        // Parámetros
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("numeroOrden", pedido.getId());
        parametros.put("fechaOrden", pedido.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        parametros.put("estadoOrden", pedido.getEstado().toString());
        parametros.put("nombreCliente", pedido.getUsuario().getNombre());
        parametros.put("emailCliente", pedido.getUsuario().getCorreo());
        parametros.put("totalProductos", totalProductos);
        parametros.put("totalPedido", pedido.getTotal());

        // Generar PDF
        InputStream reportStream = new ClassPathResource("reports/orden_envio.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(productos);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}

