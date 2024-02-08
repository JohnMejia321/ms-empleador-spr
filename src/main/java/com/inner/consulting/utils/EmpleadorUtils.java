package com.inner.consulting.utils;

import com.inner.consulting.entities.Empleador;

public class EmpleadorUtils {

    public static void setearAtributosEmpleador(Empleador empleador) {
        empleador.setFechaSolicitud(empleador.getFechaSolicitud());
        empleador.setTipoInscripcion(empleador.getTipoInscripcion());
        empleador.setTipoEmpresa(empleador.getTipoEmpresa());
        empleador.setRuc(empleador.getRuc());
        empleador.setTipoDocumento(empleador.getTipoDocumento());
        empleador.setNumeroDocumento(empleador.getNumeroDocumento());
        empleador.setIdDocumento(empleador.getIdDocumento());
        empleador.setDigitoVerificacion(empleador.getDigitoVerificacion());
        empleador.setCasilla(empleador.getCasilla());
        empleador.setRazonSocial(empleador.getRazonSocial());
        empleador.setNombreComercial(empleador.getNombreComercial());
        empleador.setFechaInicioLabores(empleador.getFechaInicioLabores());
        empleador.setLocalizacionGeografica(empleador.getLocalizacionGeografica());
        empleador.setDireccionEstablecimiento(empleador.getDireccionEstablecimiento());
        empleador.setApartadoEstablecimiento(empleador.getApartadoEstablecimiento());
        empleador.setTelefonoPrincipal(empleador.getTelefonoPrincipal());
        empleador.setTelefonoAlterno(empleador.getTelefonoAlterno());
        empleador.setCelular(empleador.getCelular());
        empleador.setFax(empleador.getFax());
        empleador.setCorreoElectronico(empleador.getCorreoElectronico());
        empleador.setPaginaWeb(empleador.getPaginaWeb());
        empleador.setAgenciaSolicitudInscripcion(empleador.getAgenciaSolicitudInscripcion());
        empleador.setNumeroAvisoOperacion(empleador.getNumeroAvisoOperacion());
    }
}

