package com.inner.consulting.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("empleador3")
public class Empleador {

    @Id
    @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    @CassandraType(type = CassandraType.Name.TEXT)
    private String id;

//    @Column("nombre")
//    @CassandraType(type = CassandraType.Name.TEXT)
//    private String nombre;
//
//    @Column("apellido")
//    @CassandraType(type = CassandraType.Name.TEXT)
//    private String apellido;

    @Column("pdf_url")
    private String pdfUrl;

    @Column("metadatos_documento")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String metadatosDocumento;

    // Nuevos atributos
    @Column("fecha_solicitud")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String fechaSolicitud;

    @Column("tipo_inscripcion")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String tipoInscripcion;





    @Column("tipo_empresa")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String tipoEmpresa;

    @Column("ruc")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String ruc;

    @Column("tipo_documento")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String tipoDocumento;

    @Column("numero_documento")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String numeroDocumento;

    @Column("id_documento")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String idDocumento;

    @Column("digito_verificacion")
    private int digitoVerificacion;

    @Column("casilla")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String casilla;

    @Column("razon_social")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String razonSocial;

    @Column("nombre_comercial")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String nombreComercial;

    @Column("fecha_inicio_labores")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String fechaInicioLabores;

    @Column("localizacion_geografica")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String localizacionGeografica;

    @Column("direccion_establecimiento")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String direccionEstablecimiento;

    @Column("apartado_establecimiento")
    private int apartadoEstablecimiento;

    @Column("telefono_principal")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String telefonoPrincipal;

    @Column("telefono_alterno")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String telefonoAlterno;

    @Column("celular")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String celular;

    @Column("fax")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String fax;

    @Column("correo_electronico")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String correoElectronico;

    @Column("pagina_web")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String paginaWeb;

    @Column("agencia_solicitud_inscripcion")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String agenciaSolicitudInscripcion;

    @Column("numero_aviso_operacion")
    private int numeroAvisoOperacion;

    public Empleador(String uuid, String pdfUrl, String metadatosDocumento,
                     String fechaSolicitud, String tipoInscripcion) {
        this.id = uuid;
        this.pdfUrl = pdfUrl;
        this.metadatosDocumento = metadatosDocumento;
        this.fechaSolicitud = fechaSolicitud;
        this.tipoInscripcion = tipoInscripcion;
    }

}
