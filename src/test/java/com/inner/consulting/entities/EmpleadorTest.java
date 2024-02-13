package com.inner.consulting.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.UUID;

class EmpleadorTest {

    @Test
    void testEmpleadorConstructor() {
        // Arrange
        String pdfUrl = "example.com/test.pdf";
        String metadatosDocumento = "Some metadata";
        String fechaSolicitud = "2024-01-30";
        String tipoInscripcion = "Some tipoInscripcion";
        // Otros atributos que desees probar...

        // Act
        Empleador empleador = new Empleador(UUID.randomUUID(), pdfUrl, metadatosDocumento,
                fechaSolicitud, tipoInscripcion);

        // Assert
        assertNotNull(empleador);
        assertEquals(pdfUrl, empleador.getPdfUrl());
        assertEquals(metadatosDocumento, empleador.getMetadatosDocumento());
        assertEquals(fechaSolicitud, empleador.getFechaSolicitud());
        assertEquals(tipoInscripcion, empleador.getTipoInscripcion());
    }

    @Test
    void testEmpleadorSetterGetter() {
        // Arrange
        Empleador empleador = new Empleador();
        String metadatosDocumento = "{\"Fecha Solicitud\":\"2/03/2020\",\"Ti  poI nscripcion\":\"Empleador Actividad Comercial\",\"Ti  podeempr  e  sa\":\"Empresa de derecho privado natural\",\"RUC\":\"12  345678  9A\",\"Tipo de documento\":\"Cedula\",\"Numero de documento\":\"123456\",\"ID de documento\":\"987654321\",\"Digito verificacion\":\"6\",\"Casilla si pertenece a un grupo empresarial\":\"no aplica\",\"Razon social\":\"sociedad anonima\",\"Nombre comercial del establecimiento\":\"empresa textiles sa\",\"Fecha Inicio de labores\":\"1/01/2024\",\"Localizacion geografica\":\"medellin\",\"Direccion del establecimiento comercial\":\"cra 345 #51A\",\"Apartado del establecimiento comercial\":\"12345\",\"Telefono del establecimiento comercial\":\"3244557\",\"Telefono alterno del establecimiento\":\"34242434\",\"Celular\":\"3103453434\",\"Fax\":\"123456\",\"Correo electronico\":\"empresa@correo.com\",\"Agencia Solicitud inscripcion\":\"no aplica\",\"Numero aviso operacion\":\"78473\"}";
        String fechaSolicitud = "2024-01-30";
        String tipoInscripcion = "Some tipoInscripcion";
        // Otros valores de atributos para probar...

        // Act
        empleador.setMetadatosDocumento(metadatosDocumento);
        empleador.setFechaSolicitud(fechaSolicitud);
        empleador.setTipoInscripcion(tipoInscripcion);
        // Establecer otros atributos...

        // Assert
        assertEquals(metadatosDocumento, empleador.getMetadatosDocumento());
        assertEquals(fechaSolicitud, empleador.getFechaSolicitud());
        assertEquals(tipoInscripcion, empleador.getTipoInscripcion());
        // Agrega más aserciones para los otros atributos según sea necesario
    }
}
