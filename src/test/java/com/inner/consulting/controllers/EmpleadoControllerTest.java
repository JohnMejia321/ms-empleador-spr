package com.inner.consulting.controllers;


import com.inner.consulting.controllers.EmpleadorController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.mockito.Mockito.*;

import com.inner.consulting.entities.Empleador;
import com.inner.consulting.services.EmpleadorService;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class EmpleadoControllerTest {

    @InjectMocks
    EmpleadorController empleadorController;

    @Mock
    EmpleadorService empleadorService;

    @Test
    public void testSaveEmpleador() throws Exception {
        Empleador empleador = new Empleador();
        MockMultipartFile pdfFile = new MockMultipartFile("pdfFile", "hello.pdf", "application/pdf", "PDF content".getBytes());
        MockMultipartFile pdfFileDocumento = new MockMultipartFile("pdfFile", "hello.pdf", "application/pdf", "PDF content".getBytes());

        Model model = mock(Model.class);

        when(empleadorService.saveEmpleador(any(Empleador.class), any(MultipartFile.class),any(MultipartFile.class))).thenReturn(empleador);

        String viewName = empleadorController.saveEmpleador(empleador, pdfFile,pdfFileDocumento , model);

        verify(empleadorService, times(1)).saveEmpleador(any(Empleador.class), any(MultipartFile.class),any(MultipartFile.class));
        verify(model, times(1)).addAttribute("empleador", empleador);
        assert(viewName.equals("success"));
    }
}

