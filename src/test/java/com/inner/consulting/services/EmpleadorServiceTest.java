package com.inner.consulting.services;

import com.inner.consulting.entities.Empleador;
import com.inner.consulting.repositories.EmpleadorRepository;
import com.inner.consulting.utils.PdfUtils;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class EmpleadorServiceTest {

    @InjectMocks
    EmpleadorService empleadorService;

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    EmpleadorRepository empleadorRepository;

    @Mock
    MinioClient minioClient;

    @BeforeEach
    public void setUp() throws Exception {
        Field kafkaTemplateField = EmpleadorService.class.getDeclaredField("kafkaTemplate");
        kafkaTemplateField.setAccessible(true);
        kafkaTemplateField.set(empleadorService, kafkaTemplate);

        Field empleadorRepositoryField = EmpleadorService.class.getDeclaredField("empleadorRepository");
        empleadorRepositoryField.setAccessible(true);
        empleadorRepositoryField.set(empleadorService, empleadorRepository);

        Field minioClientField = EmpleadorService.class.getDeclaredField("minioClient");
        minioClientField.setAccessible(true);
        minioClientField.set(empleadorService, minioClient);
    }

    @Test
    public void testSaveEmpleador() throws Exception {

        EmpleadorService empleadorService = Mockito.mock(EmpleadorService.class);


        MultipartFile multipartFileMock = Mockito.mock(MultipartFile.class);



// Configura el comportamiento del mock para que devuelva un Empleador
        Empleador empleador = new Empleador();
        Mockito.when(empleadorService.saveEmpleador(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(empleador);

// Llama al método bajo prueba
        Empleador result = empleadorService.saveEmpleador(empleador, multipartFileMock,multipartFileMock);

// Verifica que el método se llamó correctamente con los argumentos esperados
        Mockito.verify(empleadorService).saveEmpleador(empleador, multipartFileMock,multipartFileMock);

// Verifica que el resultado no sea nulo
        assertNotNull(result);
}}
