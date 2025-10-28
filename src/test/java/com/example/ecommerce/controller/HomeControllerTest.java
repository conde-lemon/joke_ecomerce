package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
// --- AÑADIR ESTAS IMPORTACIONES ---
import com.example.ecommerce.repository.PedidoRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UsuarioRepository;
// --- FIN DE IMPORTACIONES ---
import com.example.ecommerce.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogService catalogService;

    // --- CORRECCIÓN: AÑADIR MOCKS PARA LOS REPOSITORIOS ---
    // Estos mocks son necesarios para que los CommandLineRunner de la clase
    // principal (EcommerceApplication) puedan iniciarse sin errores.
    @MockBean
    private UsuarioRepository usuarioRepository;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private PedidoRepository pedidoRepository;
    // --- FIN DE LA CORRECCIÓN ---

    @Test
    void homePageShouldContainProducts() throws Exception {
        // 1. PREPARACIÓN
        Product testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setNombre("Laptop de Prueba");
        testProduct.setPrecio(new BigDecimal("999.99"));
        testProduct.setImageUrl("http://example.com/image.jpg");

        // 2. CONFIGURACIÓN DEL MOCK
        when(catalogService.findAll()).thenReturn(List.of(testProduct));

        // 3. EJECUCIÓN Y VERIFICACIÓN
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("products"))
                .andExpect(content().string(containsString("Laptop de Prueba")))
                .andExpect(content().string(containsString("999.99")));
    }
}