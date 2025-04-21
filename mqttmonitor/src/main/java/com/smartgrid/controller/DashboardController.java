package com.smartgrid.controller;

import com.smartgrid.logic.SmartGridDecisionEngine;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador web principal que gestiona el acceso al dashboard de dispositivos.
 */
@Controller
public class DashboardController {

    private final SmartGridDecisionEngine ia;

    /**
     * Constructor que recibe el motor de decisiones para poder acceder a los dispositivos activos.
     *
     * @param ia instancia del motor de decisiones
     */
    public DashboardController(SmartGridDecisionEngine ia) {
        this.ia = ia;
    }

    /**
     * Mapea la raíz "/" para mostrar el dashboard.
     * Se cargan los dispositivos activos y se pasan al modelo de la vista.
     *
     * @param model objeto del modelo para Thymeleaf
     * @return nombre del template Thymeleaf (dashboard.html)
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("dispositivos", ia.getDispositivosActivos());
        return "dashboard";
    }
}
