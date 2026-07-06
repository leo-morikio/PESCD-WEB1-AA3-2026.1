package br.ufscar.dc.dsw.pescd.dto;

/** Resposta do PR.03 (encerrar oferta): dados da oferta + estatísticas dos alunos concluídos. */
public class EncerramentoOfertaResponseDTO {

    private OfertaResponseDTO oferta;
    private EstatisticasOfertaDTO estatisticas;

    public EncerramentoOfertaResponseDTO(OfertaResponseDTO oferta, EstatisticasOfertaDTO estatisticas) {
        this.oferta = oferta;
        this.estatisticas = estatisticas;
    }

    public OfertaResponseDTO getOferta() { return oferta; }
    public EstatisticasOfertaDTO getEstatisticas() { return estatisticas; }
}
