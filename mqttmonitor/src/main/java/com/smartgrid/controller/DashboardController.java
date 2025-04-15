package com.smartgrid.controller;

import com.smartgrid.logic.SmartGridDecisionEngine;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador web que maneja las peticiones a la vista principal.
 */
@Controller
public class DashboardController {

    private final SmartGridDecisionEngine ia;

    /**
     * Inyección del motor de decisiones.
     *
     * @param ia instancia de SmartGridDecisionEngine
     */
    public DashboardController(SmartGridDecisionEngine ia) {
        this.ia = ia;
    }

    /**
     * Renderiza el dashboard principal con la lista de dispositivos activos.
     *
     * @param model modelo de datos Thymeleaf
     * @return nombre de la plantilla Thymeleaf (dashboard.html)
     */
    @GetMapping("/")
    public String index(Model model) {
        // Pasamos los dispositivos activos al frontend
        model.addAttribute("dispositivos", ia.getDispositivosActivos());
        return "dashboard";
    }
}
