package com.grupotres.projetocrud.model;

public enum StatusAtendimento {
    ABERTO("Aberto"),
    ANALISE("Análise"),
    AGENDADO("Agendado"),
    ATENDIMENTO("Atendimento"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String label;

    StatusAtendimento(String label) { this.label = label; }

    public String getLabel() { return label; }

    @Override public String toString() { return label; }

    public static StatusAtendimento fromLabel(String label) {
        for (StatusAtendimento s : values()) {
            if (s.label.equalsIgnoreCase(label)) return s;
        }
        return ABERTO;
    }
}
