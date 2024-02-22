package com.inner.consulting.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class EmpleadorDiffblueTest {

    @Test
    void testNewEmpleador() {
        // Arrange
        String uuid = "ad77d8sa7d87ads7a";

        // Act
        Empleador actualEmpleador = new Empleador(uuid, "https://example.org/example", "alice.liddell@example.org",
                "Fecha Solicitud", "Tipo Inscripcion");

        // Assert
        assertEquals("Fecha Solicitud", actualEmpleador.getFechaSolicitud());
        assertEquals("Tipo Inscripcion", actualEmpleador.getTipoInscripcion());
        assertEquals("alice.liddell@example.org", actualEmpleador.getMetadatosDocumento());
        assertEquals("https://example.org/example", actualEmpleador.getPdfUrl());
        assertNull(actualEmpleador.getAgenciaSolicitudInscripcion());
        assertNull(actualEmpleador.getCasilla());
        assertNull(actualEmpleador.getCelular());
        assertNull(actualEmpleador.getCorreoElectronico());
        assertNull(actualEmpleador.getDireccionEstablecimiento());
        assertNull(actualEmpleador.getFax());
        assertNull(actualEmpleador.getFechaInicioLabores());
        assertNull(actualEmpleador.getIdDocumento());
        assertNull(actualEmpleador.getLocalizacionGeografica());
        assertNull(actualEmpleador.getNombreComercial());
        assertNull(actualEmpleador.getNumeroDocumento());
        assertNull(actualEmpleador.getPaginaWeb());
        assertNull(actualEmpleador.getRazonSocial());
        assertNull(actualEmpleador.getRuc());
        assertNull(actualEmpleador.getTelefonoAlterno());
        assertNull(actualEmpleador.getTelefonoPrincipal());
        assertNull(actualEmpleador.getTipoDocumento());
        assertNull(actualEmpleador.getTipoEmpresa());
        assertEquals(0, actualEmpleador.getApartadoEstablecimiento());
        assertEquals(0, actualEmpleador.getDigitoVerificacion());
        assertEquals(0, actualEmpleador.getNumeroAvisoOperacion());
        String id = actualEmpleador.getId();
        //assertEquals(2, id.variant());
       // assertEquals(4, id.version());
       // assertSame(uuid, id);
    }
}
