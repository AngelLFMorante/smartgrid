package com.smartgrid.controller;

import com.smartgrid.logic.SmartGridDecisionEngine;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador web que gestiona la visualización del dashboard principal
 * y la gestión manual de dispositivos.
 */
@Controller
public class DashboardController {

    private final SmartGridDecisionEngine ia;

    /**
     * Constructor que recibe el motor de decisiones para interactuar
     * con el estado energético actual y los dispositivos activos.
     *
     * @param ia instancia del motor de decisiones
     */
    public DashboardController(SmartGridDecisionEngine ia) {
        this.ia = ia;
    }

    /**
     * Muestra el dashboard principal con el estado actual de los dispositivos.
     * Si se excede el límite energético y todos los dispositivos son críticos,
     * se activa una alerta visible en la interfaz.
     *
     * @param model modelo de datos para Thymeleaf
     * @return nombre del template Thymeleaf (dashboard.html)
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("dispositivos", ia.getDispositivosActivos());
        model.addAttribute("alertaCriticos", ia.isAlertaCriticos());
        model.addAttribute("limitePermitido", ia.getLimiteConsumo());
        model.addAttribute("totalActual", ia.getConsumoTotal());
        return "dashboard";
    }

    /**
     * Muestra la página de gestión manual, donde se listan los dispositivos activos
     * con opción de desconexión si no son críticos.
     *
     * @param model modelo de datos para Thymeleaf
     * @return nombre del template Thymeleaf (gestion.html)
     */
    @GetMapping("/gestion")
    public String gestionManual(Model model) {
        model.addAttribute("dispositivos", ia.getDispositivosActivos());
        model.addAttribute("modoManual", true);
        model.addAttribute("totalActual", ia.getConsumoTotal());
        model.addAttribute("limitePermitido", ia.getLimiteConsumo());
        return "gestion";
    }

    /**
     * Permite desconectar un dispositivo manualmente desde la interfaz.
     *
     * @param nombre nombre del dispositivo a desconectar
     * @return redirección a la página de gestión
     */
    @PostMapping("/desconectar")
    public String desconectar(@RequestParam String nombre) {
        ia.desconectarDispositivo(nombre);
        return "redirect:/gestion";
    }

    /**
     * Permite ajustar la potencia de un dispositivo crítico.
     * Si el consumo total sigue siendo mayor que el límite, no permitirá la salida.
     *
     * @param nombre     nombre del dispositivo
     * @param nuevaPotencia nueva potencia que se desea asignar
     * @return redirección a la página de gestión
     */
    @PostMapping("/ajustar-potencia")
    public String ajustarPotencia(@RequestParam String nombre, @RequestParam double nuevaPotencia) {
        boolean ajustado = ia.ajustarPotenciaDispositivo(nombre, nuevaPotencia);
        if (ajustado) {
            return "redirect:/gestion";
        } else {
            return "redirect:/gestion?error=No se pudo ajustar la potencia";
        }
    }
}
