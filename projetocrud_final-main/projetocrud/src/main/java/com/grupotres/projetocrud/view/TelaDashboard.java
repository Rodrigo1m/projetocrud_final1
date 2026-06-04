package com.grupotres.projetocrud.view;

import com.grupotres.projetocrud.controller.ObraController;
import com.grupotres.projetocrud.controller.UsuarioController;
import com.grupotres.projetocrud.model.Obra;
import com.grupotres.projetocrud.model.ObraTipoAtendimento;
import com.grupotres.projetocrud.model.StatusAtendimento;
import com.grupotres.projetocrud.model.User;
import com.grupotres.projetocrud.util.PerfilUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TelaDashboard {

    private final UsuarioController usuarioController;
    private final ObraController obraController;
    private final User usuarioLogado;

    public TelaDashboard(UsuarioController usuarioController, ObraController obraController, User usuarioLogado) {
        this.usuarioController = usuarioController;
        this.obraController = obraController;
        this.usuarioLogado = usuarioLogado;
    }

    public ScrollPane buildView() {
        VBox painel = new VBox(14);
        painel.setPadding(new Insets(16));
        painel.setStyle("-fx-background-color: #f5f5f0;");

        HBox topo = new HBox(12);
        topo.setAlignment(Pos.CENTER_LEFT);

        Label titulo = new Label("Dashboard de Obras");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1a1a18;");

        Button btnAtualizar = new Button("Atualizar");
        btnAtualizar.setStyle("-fx-background-color: #2c2c2a; -fx-text-fill: #f1efe8; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-pref-height: 32;");
        btnAtualizar.setOnAction(e -> carregarDados(painel));

        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS);
        topo.getChildren().addAll(titulo, espacador, btnAtualizar);

        Label descricao = new Label("Visão geral das obras, atendimentos e equipes.");
        descricao.setStyle("-fx-font-size: 13px; -fx-text-fill: #666660;");

        painel.getChildren().addAll(topo, descricao);
        carregarDados(painel);

        ScrollPane sp = new ScrollPane(painel);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #f5f5f0; -fx-background-color: #f5f5f0; -fx-border-color: transparent;");
        return sp;
    }

    private void carregarDados(VBox painel) {
        List<Node> restantes = painel.getChildren().stream().skip(2).collect(Collectors.toList());
        painel.getChildren().removeAll(restantes);

        List<Obra> obras = obrasVisiveis();
        int totalObras = obras.size();
        int obrasComEquipe = (int) obras.stream().filter(o -> !o.getEquipe().isEmpty()).count();
        int obrasSemEquipe = totalObras - obrasComEquipe;
        int totalAtendimentos = obras.stream().mapToInt(o -> o.getTiposAtendimento().size()).sum();

        Map<StatusAtendimento, Long> statusContagem = obras.stream()
            .flatMap(o -> o.getTiposAtendimento().stream())
            .collect(Collectors.groupingBy(ObraTipoAtendimento::getStatus, Collectors.counting()));

        Map<String, Long> obrasPorCliente = obras.stream()
            .collect(Collectors.groupingBy(o -> o.getCliente() == null ? "Sem cliente" : o.getCliente().getNome(), Collectors.counting()));

        VBox cards = new VBox(12);
        cards.getChildren().addAll(
            criarCard("Total de obras", String.valueOf(totalObras), "Obras visíveis para seu perfil"),
            criarCard("Total de atendimentos", String.valueOf(totalAtendimentos), "Atendimentos cadastrados nas obras"),
            criarCard("Obras com equipe", String.valueOf(obrasComEquipe), "Obras que já contam com membros alocados"),
            criarCard("Obras sem equipe", String.valueOf(obrasSemEquipe), "Obras sem membros de equipe atribuídos")
        );

        VBox statusPainel = new VBox(10);
        statusPainel.setPadding(new Insets(10));
        statusPainel.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        statusPainel.setStyle("-fx-border-color: #e0e0d8; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label statusTitulo = new Label("Atendimentos por status");
        statusTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a1a18;");
        statusPainel.getChildren().add(statusTitulo);

        for (StatusAtendimento status : StatusAtendimento.values()) {
            Label linha = new Label(status.getLabel() + ": " + statusContagem.getOrDefault(status, 0L));
            linha.setStyle("-fx-font-size: 12px; -fx-text-fill: #444440;");
            statusPainel.getChildren().add(linha);
        }

        VBox clientesPainel = new VBox(10);
        clientesPainel.setPadding(new Insets(10));
        clientesPainel.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        clientesPainel.setStyle("-fx-border-color: #e0e0d8; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label clientesTitulo = new Label("Obras por cliente");
        clientesTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a1a18;");
        clientesPainel.getChildren().add(clientesTitulo);

        obrasPorCliente.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()).thenComparing(Map.Entry.comparingByKey()))
            .limit(5)
            .forEach(entry -> {
                Label linha = new Label(entry.getKey() + ": " + entry.getValue());
                linha.setStyle("-fx-font-size: 12px; -fx-text-fill: #444440;");
                clientesPainel.getChildren().add(linha);
            });

        if (obrasPorCliente.isEmpty()) {
            clientesPainel.getChildren().add(new Label("Nenhuma obra cadastrada."));
        }

        painel.getChildren().addAll(cards, statusPainel, clientesPainel);
    }

    private List<Obra> obrasVisiveis() {
        if (usuarioLogado.isAdministrador() || usuarioLogado.isResponsavelTecnico()) {
            return obraController.listarObras();
        } else if (usuarioLogado.isCliente()) {
            return obraController.listarObrasPorCliente(usuarioLogado);
        } else if (usuarioLogado.isEquipe()) {
            return obraController.listarObrasPorMembro(usuarioLogado);
        } else if (usuarioLogado.isTerceirizada()) {
            return obraController.listarAtendimentosPorTerceirizada(usuarioLogado)
                .stream().map(ObraTipoAtendimento::getObra).distinct().collect(Collectors.toList());
        }
        return List.of();
    }

    private VBox criarCard(String titulo, String valor, String descricao) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12));
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        card.setStyle("-fx-border-color: #e0e0d8; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #666660;");
        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1a1a18;");
        Label lblDescricao = new Label(descricao);
        lblDescricao.setStyle("-fx-font-size: 12px; -fx-text-fill: #888880;");

        card.getChildren().addAll(lblTitulo, lblValor, lblDescricao);
        return card;
    }
}
