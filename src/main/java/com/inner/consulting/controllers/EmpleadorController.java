package com.inner.consulting.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.inner.consulting.entities.Empleador;
import com.inner.consulting.services.EmpleadorService;

@Controller
public class EmpleadorController {
    @Autowired
    private EmpleadorService empleadorService;
    @PostMapping("/agregar-empleador")
    public String saveEmpleador(@ModelAttribute Empleador empleador,
                                @RequestParam("pdfFile") MultipartFile pdfFile,
                                @RequestParam("documentoIdentidad") MultipartFile documentoIdentidad,
                                Model model) {
        try {
            Empleador empleadorGuardado = empleadorService.saveEmpleador(empleador, pdfFile,documentoIdentidad);
            model.addAttribute("empleador", empleadorGuardado);
            return "success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}
