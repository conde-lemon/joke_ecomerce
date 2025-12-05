package com.example.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de conexión a MySQL real (NO usa H2 en memoria).
 * Este test usa la configuración de application.properties (MySQL).
 * Los datos persisten en la base de datos y puedes verlos en MySQL Workbench.
 */
@SpringBootTest
public class MySQLConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testMySQLConnection() throws Exception {
        // Verifica que el DataSource esté configurado
        assertNotNull(dataSource, "DataSource debe estar configurado");

        // Intenta obtener una conexión y verificar que esté abierta y sea válida
        try (Connection conn = dataSource.getConnection()) {
            assertNotNull(conn, "La conexión no debe ser null");
            assertFalse(conn.isClosed(), "La conexión debe estar abierta");

            // Verifica que la conexión sea válida
            boolean valid = conn.isValid(2);
            assertTrue(valid, "La conexión debe ser válida");

            // Obtiene información sobre la base de datos
            DatabaseMetaData metaData = conn.getMetaData();
            String dbName = metaData.getDatabaseProductName();
            String dbVersion = metaData.getDatabaseProductVersion();
            String url = metaData.getURL();

            System.out.println("===========================================");
            System.out.println("✅ CONEXIÓN A MYSQL EXITOSA");
            System.out.println("===========================================");
            System.out.println("Base de datos: " + dbName);
            System.out.println("Versión: " + dbVersion);
            System.out.println("URL: " + url);
            System.out.println("===========================================");

            // Verifica que sea MySQL
            assertTrue(dbName.toLowerCase().contains("mysql"),
                    "Debe estar conectado a MySQL, pero se detectó: " + dbName);
        }
    }
}

