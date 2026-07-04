package br.ufscar.dc.dsw.pescd.dto;

import java.util.List;

public class ResultadoCsvDTO {
    private int sucessos;
    private List<String> falhas;

    public ResultadoCsvDTO(int sucessos, List<String> falhas) {
        this.sucessos = sucessos;
        this.falhas = falhas;
    }

    public int getSucessos() { return sucessos; }
    public List<String> getFalhas() { return falhas; }
}
