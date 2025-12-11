package com.example.ecommerce.controller;

import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class ReportRestController {

    @Autowired
    private ReportService reportService;

    /**
     * Verifica si el usuario es administrador
     */
    private boolean isAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("currentUser");
        return usuario != null && usuario.getRoles() != null && usuario.getRoles().contains("ADMIN");
    }

    /**
     * Genera un reporte PDF de todos los productos
     */
    @GetMapping("/productos/pdf")
    public ResponseEntity<byte[]> generarReporteProductosPDF(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            byte[] pdfBytes = reportService.generarReporteProductos();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte_productos.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Genera una boleta de venta en PDF
     */
    @GetMapping("/boleta/{pedidoId}")
    public ResponseEntity<byte[]> generarBoleta(
            @PathVariable Long pedidoId, 
            HttpSession session,
            @RequestParam(value = "email", required = false) String email
    ) {
        Usuario usuario = (Usuario) session.getAttribute("currentUser");
        
        // Si no hay usuario en sesión pero se proporciona email, permitir acceso
        if (usuario == null && email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] pdfBytes = reportService.generarBoleta(pedidoId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "boleta_" + pedidoId + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Genera una orden de envío en PDF
     */
    @GetMapping("/orden-envio/{pedidoId}")
    public ResponseEntity<byte[]> generarOrdenEnvio(
            @PathVariable Long pedidoId, 
            HttpSession session,
            @RequestParam(value = "email", required = false) String email
    ) {
        Usuario usuario = (Usuario) session.getAttribute("currentUser");
        
        // Si no hay usuario en sesión pero se proporciona email, permitir acceso
        if (usuario == null && email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] pdfBytes = reportService.generarOrdenEnvio(pedidoId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "orden_envio_" + pedidoId + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

