package com.example.ecommerce.service;

import com.example.ecommerce.model.DetallePedido;
import com.example.ecommerce.model.EstadoPedido;
import com.example.ecommerce.model.Pedido;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

// Esta anotación levanta el contexto completo de Spring para la prueba
@SpringBootTest
class InvoiceServiceTest {

    // Inyectamos el servicio que queremos probar
    @Autowired
    private InvoiceService invoiceService;

    @Test
    void testGenerateInvoicePdf() throws Exception {
        // --- 1. Preparación de Datos de Prueba (Mock Data) ---
        // Creamos un usuario de prueba
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@example.com");
        usuario.setNombre("Cliente de Prueba");

        // Creamos productos de prueba
        Product p1 = new Product();
        p1.setId(1L);
        p1.setNombre("Laptop Pro Test");
        p1.setPrecio(new BigDecimal("1200.00"));

        Product p2 = new Product();
        p2.setId(2L);
        p2.setNombre("Mouse Inalámbrico Test");
        p2.setPrecio(new BigDecimal("25.50"));

        // Creamos el pedido principal
        Pedido pedido = new Pedido();
        pedido.setId(999L); // Un ID de prueba
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadoPedido.ENTREGADO);

        // Creamos el primer detalle
        DetallePedido detalle1 = new DetallePedido();
        detalle1.setProducto(p1);
        detalle1.setCantidad(1);
        detalle1.setPrecioUnitario(p1.getPrecio());
        detalle1.setSubtotal(p1.getPrecio().multiply(new BigDecimal(1)));
        pedido.addDetalle(detalle1);

        // Creamos el segundo detalle
        DetallePedido detalle2 = new DetallePedido();
        detalle2.setProducto(p2);
        detalle2.setCantidad(2);
        detalle2.setPrecioUnitario(p2.getPrecio());
        detalle2.setSubtotal(p2.getPrecio().multiply(new BigDecimal(2)));
        pedido.addDetalle(detalle2);

        // Calculamos el total del pedido
        pedido.setTotal(detalle1.getSubtotal().add(detalle2.getSubtotal()));

        // --- 2. Ejecución del Método a Probar ---
        System.out.println(">>> Ejecutando generateInvoicePdf con JasperReports...");
        byte[] pdfBytes = invoiceService.generateInvoicePdf(pedido);
        System.out.println(">>> Boleta PDF generada en memoria con JasperReports.");

        // --- 3. Verificaciones (Assertions) ---
        // Verificamos que el resultado no sea nulo
        assertNotNull(pdfBytes, "El array de bytes del PDF no debería ser nulo.");
        // Verificamos que el PDF tiene contenido
        assertTrue(pdfBytes.length > 0, "El PDF generado debería tener contenido.");

        // --- 4. (Opcional pero MUY útil) Guardar el PDF para inspección manual ---
        // Esto guardará el archivo en la carpeta 'target' de tu proyecto
        Path outputPath = Paths.get("target/test-boleta.pdf");
        Files.createDirectories(outputPath.getParent()); // Crea la carpeta 'target' si no existe
        try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
            fos.write(pdfBytes);
        }
        System.out.println(">>> Test finalizado. Boleta PDF de prueba guardada en: " + outputPath.toAbsolutePath());
        assertTrue(Files.exists(outputPath), "El archivo PDF no se guardó correctamente en el disco.");
    }
}