package com.inner.consulting.services;

import com.inner.consulting.entities.Empleador;
import com.inner.consulting.repositories.EmpleadorRepository;
import com.inner.consulting.utils.PdfUtils;

import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class EmpleadorServiceTest {

    @InjectMocks
    private EmpleadorService empleadorService;

    @Mock
    private EmpleadorRepository empleadorRepository;

    @Mock
    private MinioClient minioClient;

    @Mock
    private PdfUtils pdfUtils;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testSaveEmpleador() throws Exception {
        // Preparar datos de prueba
        Empleador empleador = new Empleador();
        empleador.setNombreComercial("Test");
        MultipartFile pdfFile = new MockMultipartFile("file", "hello.pdf", "application/pdf", "PDF content".getBytes());

        // Crear un InputStream de prueba
        InputStream testInputStream = new ByteArrayInputStream("PDF content".getBytes());

        // Configurar el comportamiento del mock
        when(empleadorRepository.save(any(Empleador.class))).thenReturn(empleador);
        doNothing().when(minioClient).makeBucket(any(MakeBucketArgs.class));
        when(pdfUtils.processPDFDocument(any(InputStream.class))).thenReturn("mocked pdf content");
        when(pdfFile.getInputStream()).thenReturn(testInputStream); // Añade esta línea

        // Llamar al método que se está probando
        Empleador result = empleadorService.saveEmpleador(empleador, pdfFile);

        // Verificar el resultado
        assertEquals(empleador.getNombreComercial(), result.getNombreComercial());
    }


}

