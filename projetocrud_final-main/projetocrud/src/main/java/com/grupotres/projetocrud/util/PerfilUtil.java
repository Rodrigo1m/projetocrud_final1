package com.grupotres.projetocrud.util;

import java.util.List;

public class PerfilUtil {

    public static final String ADMINISTRADOR       = "Administrador";
    public static final String RESPONSAVEL_TECNICO = "Responsável Técnico";
    public static final String CLIENTE             = "Cliente";
    public static final String TERCEIRIZADA        = "Terceirizada";
    public static final String EQUIPE              = "Equipe";

    public static final List<String> TODOS_PERFIS = List.of(
        ADMINISTRADOR, RESPONSAVEL_TECNICO, CLIENTE, TERCEIRIZADA, EQUIPE
    );

    public static String descricao(String perfil) {
        return switch (perfil) {
            case ADMINISTRADOR       -> "Acesso total: cria, edita e exclui obras, tipos de atendimento, usuários e papéis.";
            case RESPONSAVEL_TECNICO -> "Gerencia obras, equipe interna, terceirizadas e status de atendimento.";
            case CLIENTE             -> "Visualiza apenas as obras em que figura como cliente.";
            case TERCEIRIZADA        -> "Vê apenas os tipos de atendimento aos quais foi designada.";
            case EQUIPE              -> "Membro interno alocado a uma obra específica.";
            default                  -> "";
        };
    }
}
