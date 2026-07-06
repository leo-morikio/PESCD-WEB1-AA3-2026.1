package br.ufscar.dc.dsw.pescd.dto;

/** Estatísticas exibidas ao professor responsável ao encerrar uma oferta (PR.03 RN-2). */
public class EstatisticasOfertaDTO {

    private long totalConcluidos;
    private long porEstagio;
    private long porDocumentacao;
    private double frequenciaMedia;
    private long quantidadeNotaA;
    private long quantidadeNotaB;
    private long quantidadeNotaC;
    private long quantidadeNotaD;
    private long quantidadeNotaE;

    public long getTotalConcluidos() { return totalConcluidos; }
    public void setTotalConcluidos(long totalConcluidos) { this.totalConcluidos = totalConcluidos; }

    public long getPorEstagio() { return porEstagio; }
    public void setPorEstagio(long porEstagio) { this.porEstagio = porEstagio; }

    public long getPorDocumentacao() { return porDocumentacao; }
    public void setPorDocumentacao(long porDocumentacao) { this.porDocumentacao = porDocumentacao; }

    public double getFrequenciaMedia() { return frequenciaMedia; }
    public void setFrequenciaMedia(double frequenciaMedia) { this.frequenciaMedia = frequenciaMedia; }

    public long getQuantidadeNotaA() { return quantidadeNotaA; }
    public void setQuantidadeNotaA(long quantidadeNotaA) { this.quantidadeNotaA = quantidadeNotaA; }

    public long getQuantidadeNotaB() { return quantidadeNotaB; }
    public void setQuantidadeNotaB(long quantidadeNotaB) { this.quantidadeNotaB = quantidadeNotaB; }

    public long getQuantidadeNotaC() { return quantidadeNotaC; }
    public void setQuantidadeNotaC(long quantidadeNotaC) { this.quantidadeNotaC = quantidadeNotaC; }

    public long getQuantidadeNotaD() { return quantidadeNotaD; }
    public void setQuantidadeNotaD(long quantidadeNotaD) { this.quantidadeNotaD = quantidadeNotaD; }

    public long getQuantidadeNotaE() { return quantidadeNotaE; }
    public void setQuantidadeNotaE(long quantidadeNotaE) { this.quantidadeNotaE = quantidadeNotaE; }
}
