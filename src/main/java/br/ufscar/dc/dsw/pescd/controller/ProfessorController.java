package br.ufscar.dc.dsw.pescd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    // User Story Surpresa — tela inicial do professor com as duas opções
    @GetMapping("/ofertas")
    public String index() {
        return "professor/index";
    }
}