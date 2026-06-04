package com.grupotres.projetocrud.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.grupotres.projetocrud.controller.ObraController;
import com.grupotres.projetocrud.controller.UsuarioController;
import com.grupotres.projetocrud.model.*;
import com.grupotres.projetocrud.util.PerfilUtil;

import java.util.List;

public class TelaObras {

    private final UsuarioController userCtrl;
    private final ObraController    obraCtrl;
    private final User              usuarioLogado;

    public TelaObras(UsuarioController userCtrl, ObraController obraCtrl, User usuarioLogado) {
        this.userCtrl      = userCtrl;
        this.obraCtrl      = obraCtrl;
        this.usuarioLogado = usuarioLogado;
    }

    public ScrollPane buildView() {
        VBox painel = new VBox(8);
        painel.setPadding(new Insets(16));

        HBox barra = new HBox(8);
        Button btnListar = new Button("Atualizar Lista");
        btnListar.setStyle(estiloBtnPrimario());

        Button btnNova = new Button("+ Nova Obra");
        btnNova.setStyle("-fx-background-color: #185fa5; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-pref-height: 34;");

        barra.getChildren().add(btnListar);
        if (usuarioLogado.isAdministrador() || usuarioLogado.isResponsavelTecnico()) {
            barra.getChildren().add(btnNova);
        }

        Label mensagem = new Label(); mensagem.setVisible(false); mensagem.setManaged(false); mensagem.setWrapText(true);
        VBox lista = new VBox(10);

        Runnable[] recarregarHolder = new Runnable[1];
        Runnable recarregar = () -> {
            lista.getChildren().clear();
            List<Obra> obras = obrasVisiveis();
            if (obras.isEmpty()) {
                Label v = new Label("Nenhuma obra encontrada.");
                v.setStyle("-fx-font-size: 13px; -fx-text-fill: #888880;");
                lista.getChildren().add(v);
                return;
            }
            for (Obra o : obras) lista.getChildren().add(criarCartaoObra(o, mensagem, () -> {
                mensagem.setVisible(false);
                mensagem.setManaged(false);
                recarregarHolder[0].run();
            }));
        };
        recarregarHolder[0] = recarregar;

        btnListar.setOnAction(e -> { mensagem.setVisible(false); mensagem.setManaged(false); recarregar.run(); });

        btnNova.setOnAction(e -> {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Nova Obra");
            dialog.setResizable(false);

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(20));
            layout.setStyle("-fx-background-color: white;");
            layout.setPrefWidth(340);

            Label titulo = new Label("Nova Obra");
            titulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

            String si = estiloInput();
            TextField campoNome  = new TextField(); campoNome.setPromptText("Nome da obra"); campoNome.setStyle(si);
            TextField campoEnd   = new TextField(); campoEnd.setPromptText("Endereço"); campoEnd.setStyle(si);

            // Selecionar cliente
            ComboBox<User> comboCliente = new ComboBox<>();
            List<User> clientes = userCtrl.listarPorPerfil(PerfilUtil.CLIENTE);
            comboCliente.getItems().addAll(clientes);
            comboCliente.setPromptText("Selecione o cliente");
            comboCliente.setMaxWidth(Double.MAX_VALUE);
            comboCliente.setStyle(si);
            comboCliente.setCellFactory(lv -> new ListCell<>() {
                protected void updateItem(User u, boolean empty) { super.updateItem(u, empty); setText(empty || u == null ? null : u.getNome() + " — " + u.getEmail()); }
            });
            comboCliente.setButtonCell(comboCliente.getCellFactory().call(null));

            Label msg = new Label(); msg.setVisible(false); msg.setManaged(false); msg.setWrapText(true);

            Button btnSalvar = new Button("Criar Obra");
            btnSalvar.setMaxWidth(Double.MAX_VALUE);
            btnSalvar.setStyle(estiloBtnPrimario());

            btnSalvar.setOnAction(ev -> {
                String nn = campoNome.getText().trim();
                String ne = campoEnd.getText().trim();
                User nc   = comboCliente.getValue();
                if (nn.isEmpty()) { mostrarMensagem(msg, "Informe o nome da obra!", false); dialog.sizeToScene(); return; }
                try {
                    obraCtrl.criarObra(nn, ne, nc);
                    dialog.close();
                    mostrarMensagem(mensagem, "Obra criada com sucesso!", true);
                    recarregar.run();
                } catch (Exception ex) { mostrarMensagem(msg, ex.getMessage(), false); dialog.sizeToScene(); }
            });

            layout.getChildren().addAll(titulo, lbl("Nome"), campoNome, lbl("Endereço"), campoEnd,
                lbl("Cliente"), comboCliente, msg, btnSalvar);
            dialog.setScene(new Scene(layout));
            dialog.show();
        });

        painel.getChildren().addAll(barra, mensagem, lista);

        // Carrega ao abrir
        recarregar.run();

        ScrollPane sp = new ScrollPane(painel);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #f5f5f0; -fx-background-color: #f5f5f0; -fx-border-color: transparent;");
        return sp;
    }

    private List<Obra> obrasVisiveis() {
        if (usuarioLogado.isAdministrador() || usuarioLogado.isResponsavelTecnico()) {
            return obraCtrl.listarObras();
        } else if (usuarioLogado.isCliente()) {
            return obraCtrl.listarObrasPorCliente(usuarioLogado);
        } else if (usuarioLogado.isEquipe()) {
            return obraCtrl.listarObrasPorMembro(usuarioLogado);
        } else if (usuarioLogado.isTerceirizada()) {
            // Terceirizada: mostra obras cujos atendimentos ela está designada
            return obraCtrl.listarAtendimentosPorTerceirizada(usuarioLogado)
                .stream().map(ObraTipoAtendimento::getObra).distinct().toList();
        }
        return List.of();
    }

    private VBox criarCartaoObra(Obra o, Label mensagemPai, Runnable recarregar) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0d8; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Cabeçalho da obra
        HBox top = new HBox(8); top.setAlignment(Pos.CENTER_LEFT);
        Label nomeObra = new Label(o.getNome());
        nomeObra.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a1a18;");
        Label endLabel = new Label("📍 " + (o.getEndereco() == null || o.getEndereco().isBlank() ? "Sem endereço" : o.getEndereco()));
        endLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888880;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        top.getChildren().addAll(nomeObra, sp);

        String clienteNome = o.getCliente() != null ? "👤 " + o.getCliente().getNome() : "Sem cliente";
        Label clienteLabel = new Label(clienteNome);
        clienteLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666660;");

        card.getChildren().addAll(top, endLabel, clienteLabel);

        // Atendimentos da obra (para todos exceto Cliente vê só status)
        List<ObraTipoAtendimento> atas = obraCtrl.listarAtendimentosPorObra(o);

        if (!atas.isEmpty()) {
            Label lblAtas = new Label("Atendimentos:");
            lblAtas.setStyle("-fx-font-size: 11px; -fx-text-fill: #444440; -fx-font-weight: bold; -fx-padding: 4 0 0 0;");
            card.getChildren().add(lblAtas);

            for (ObraTipoAtendimento ata : atas) {
                // Terceirizada só vê se estiver designada
                if (usuarioLogado.isTerceirizada() && !ata.getTerceirizadas().contains(usuarioLogado)) continue;

                HBox linha = new HBox(8); linha.setAlignment(Pos.CENTER_LEFT);

                Label tipoLbl = new Label(ata.getTipoAtendimento().getNome());
                tipoLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #333330;");

                Label statusLbl = new Label(ata.getStatus().getLabel());
                statusLbl.setStyle("-fx-background-color: " + corStatus(ata.getStatus()) + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 999; -fx-padding: 2 8; -fx-text-fill: white;");

                linha.getChildren().addAll(tipoLbl, statusLbl);

                // Botão histórico (todos podem ver)
                Button btnHist = new Button("Histórico");
                btnHist.setStyle("-fx-background-color: transparent; -fx-border-color: #c0c0b8; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 10px; -fx-cursor: hand;");
                btnHist.setOnAction(ev -> abrirHistorico(ata));
                linha.getChildren().add(btnHist);

                // Mudar status (Admin e Resp. Técnico)
                if (usuarioLogado.isAdministrador() || usuarioLogado.isResponsavelTecnico()) {
                    ComboBox<StatusAtendimento> comboStatus = new ComboBox<>();
                    comboStatus.getItems().addAll(StatusAtendimento.values());
                    comboStatus.setValue(ata.getStatus());
                    comboStatus.setStyle("-fx-font-size: 11px;");
                    comboStatus.setOnAction(ev -> {
                        obraCtrl.alterarStatus(ata.getId(), comboStatus.getValue());
                        statusLbl.setText(comboStatus.getValue().getLabel());
                        statusLbl.setStyle("-fx-background-color: " + corStatus(comboStatus.getValue()) + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 999; -fx-padding: 2 8; -fx-text-fill: white;");
                    });
                    linha.getChildren().add(comboStatus);
                }

                card.getChildren().add(linha);
            }
        }

        // Botões de gestão (Admin e Resp. Técnico)
        if (usuarioLogado.isAdministrador() || usuarioLogado.isResponsavelTecnico()) {
            Separator sep = new Separator(); sep.setStyle("-fx-background-color: #ececec;");
            card.getChildren().add(sep);

            HBox acoes = new HBox(6);
            Button btnAddAta = new Button("+ Atendimento");
            btnAddAta.setStyle("-fx-background-color: #185fa5; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");

            Button btnEquipe = new Button("Equipe");
            btnEquipe.setStyle("-fx-background-color: #f0f0e8; -fx-border-color: #d0d0c8; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 11px; -fx-cursor: hand;");

            acoes.getChildren().addAll(btnAddAta, btnEquipe);

            if (usuarioLogado.isAdministrador()) {
                Button btnEditar = new Button("Editar");
                btnEditar.setStyle("-fx-background-color: #f0f0e8; -fx-border-color: #d0d0c8; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 11px; -fx-cursor: hand;");
                Button btnExcluir = new Button("Excluir");
                btnExcluir.setStyle("-fx-background-color: #fcebeb; -fx-text-fill: #a32d2d; -fx-border-color: #f09595; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 11px; -fx-cursor: hand;");

                btnEditar.setOnAction(e -> abrirModalEditarObra(o, mensagemPai, recarregar));
                btnExcluir.setOnAction(e -> {
                    Alert c = new Alert(Alert.AlertType.CONFIRMATION);
                    c.setTitle("Excluir Obra"); c.setHeaderText("Excluir obra \"" + o.getNome() + "\"?");
                    c.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
                        obraCtrl.excluirObra(o.getId());
                        mostrarMensagem(mensagemPai, "Obra excluída!", true);
                        recarregar.run();
                    });
                });
                acoes.getChildren().addAll(btnEditar, btnExcluir);
            }

            btnAddAta.setOnAction(e -> abrirModalAddAtendimento(o, mensagemPai, recarregar));
            btnEquipe.setOnAction(e -> abrirModalEquipe(o));

            card.getChildren().add(acoes);
        }

        return card;
    }

    private void abrirHistorico(ObraTipoAtendimento ata) {
        Stage d = new Stage();
        d.setTitle("Histórico — " + ata.getTipoAtendimento().getNome());
        d.setResizable(false);

        VBox layout = new VBox(8);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");
        layout.setPrefWidth(320);

        Label titulo = new Label("Histórico de Status");
        titulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        layout.getChildren().add(titulo);

        List<StatusHistorico> hist = ata.getHistorico();
        if (hist.isEmpty()) {
            layout.getChildren().add(new Label("Sem histórico."));
        } else {
            for (StatusHistorico h : hist) {
                HBox linha = new HBox(12); linha.setAlignment(Pos.CENTER_LEFT);
                Label sl = new Label(h.getStatus().getLabel());
                sl.setStyle("-fx-background-color: " + corStatus(h.getStatus()) + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 999; -fx-padding: 2 8; -fx-text-fill: white;");
                Label dl = new Label(h.getDataInicio());
                dl.setStyle("-fx-font-size: 11px; -fx-text-fill: #666660;");
                linha.getChildren().addAll(sl, dl);
                layout.getChildren().add(linha);
            }
        }

        Button btnFechar = new Button("Fechar");
        btnFechar.setMaxWidth(Double.MAX_VALUE);
        btnFechar.setStyle(estiloBtnPrimario());
        btnFechar.setOnAction(e -> d.close());
        layout.getChildren().add(btnFechar);

        d.setScene(new Scene(layout));
        d.show();
    }

    private void abrirModalAddAtendimento(Obra o, Label mensagemPai, Runnable recarregar) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Adicionar Atendimento");
        dialog.setResizable(false);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");
        layout.setPrefWidth(320);

        Label titulo = new Label("Adicionar Tipo de Atendimento");
        titulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ComboBox<TipoAtendimento> comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll(obraCtrl.listarTipos());
        comboTipo.setPromptText("Selecione o tipo");
        comboTipo.setMaxWidth(Double.MAX_VALUE);
        comboTipo.setStyle(estiloInput());

        Label msg = new Label(); msg.setVisible(false); msg.setManaged(false); msg.setWrapText(true);

        Button btnAdd = new Button("Adicionar");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setStyle(estiloBtnPrimario());
        btnAdd.setOnAction(e -> {
            TipoAtendimento t = comboTipo.getValue();
            if (t == null) { mostrarMensagem(msg, "Selecione um tipo!", false); dialog.sizeToScene(); return; }
            try {
                obraCtrl.adicionarTipoAtendimento(o.getId(), t.getId());
                dialog.close();
                mostrarMensagem(mensagemPai, "Atendimento adicionado!", true);
                recarregar.run();
            } catch (Exception ex) { mostrarMensagem(msg, ex.getMessage(), false); dialog.sizeToScene(); }
        });

        layout.getChildren().addAll(titulo, lbl("Tipo de Atendimento"), comboTipo, msg, btnAdd);
        dialog.setScene(new Scene(layout));
        dialog.show();
    }

    private void abrirModalEquipe(Obra o) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Equipe — " + o.getNome());
        dialog.setResizable(false);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");
        layout.setPrefWidth(340);

        Label titulo = new Label("Gerenciar Equipe");
        titulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        VBox listaEquipe = new VBox(6);
        Runnable[] reloadHolder = new Runnable[1];
        Runnable reload = () -> {
            listaEquipe.getChildren().clear();
            obraCtrl.buscarPorId(o.getId()).ifPresent(ob -> {
                ob.getEquipe().forEach(m -> {
                    HBox row = new HBox(8); row.setAlignment(Pos.CENTER_LEFT);
                    Label ml = new Label(m.getNome() + " (" + m.getPerfil() + ")");
                    ml.setStyle("-fx-font-size: 12px;");
                    Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
                    Button br = new Button("Remover");
                    br.setStyle("-fx-background-color: #fcebeb; -fx-text-fill: #a32d2d; -fx-border-color: #f09595; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 10px; -fx-cursor: hand;");
                    br.setOnAction(e -> { obraCtrl.removerMembro(ob.getId(), m); reloadHolder[0].run(); });
                    row.getChildren().addAll(ml, sp, br);
                    listaEquipe.getChildren().add(row);
                });
                if (ob.getEquipe().isEmpty()) listaEquipe.getChildren().add(new Label("Sem membros."));
            });
        };
        reloadHolder[0] = reload;
        reload.run();

        // Adicionar membro
        ComboBox<User> comboMembro = new ComboBox<>();
        List<User> internos = new java.util.ArrayList<>(userCtrl.listarPorPerfil(PerfilUtil.EQUIPE));
        internos.addAll(userCtrl.listarPorPerfil(PerfilUtil.RESPONSAVEL_TECNICO));
        comboMembro.getItems().addAll(internos);
        comboMembro.setPromptText("Adicionar membro");
        comboMembro.setMaxWidth(Double.MAX_VALUE);
        comboMembro.setStyle(estiloInput());
        comboMembro.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(User u, boolean empty) { super.updateItem(u, empty); setText(empty || u == null ? null : u.getNome()); }
        });
        comboMembro.setButtonCell(comboMembro.getCellFactory().call(null));

        Button btnAdicionar = new Button("+ Adicionar");
        btnAdicionar.setStyle(estiloBtnPrimario());
        btnAdicionar.setOnAction(e -> {
            User m = comboMembro.getValue();
            if (m != null) { obraCtrl.adicionarMembro(o.getId(), m); comboMembro.setValue(null); reload.run(); }
        });

        Button btnFechar = new Button("Fechar");
        btnFechar.setMaxWidth(Double.MAX_VALUE);
        btnFechar.setStyle("-fx-background-color: transparent; -fx-border-color: #d0d0c8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #666660; -fx-font-size: 13px; -fx-cursor: hand; -fx-pref-height: 32;");
        btnFechar.setOnAction(e -> dialog.close());

        layout.getChildren().addAll(titulo, new Label("Membros atuais:"), listaEquipe,
            new Separator(), lbl("Adicionar membro da equipe:"), comboMembro, btnAdicionar, btnFechar);
        dialog.setScene(new Scene(layout));
        dialog.show();
    }

    private void abrirModalEditarObra(Obra o, Label mensagemPai, Runnable recarregar) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar Obra");
        dialog.setResizable(false);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");
        layout.setPrefWidth(340);

        String si = estiloInput();
        Label titulo = new Label("Editar Obra");
        titulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        TextField campoNome = new TextField(o.getNome()); campoNome.setStyle(si);
        TextField campoEnd  = new TextField(o.getEndereco() == null ? "" : o.getEndereco()); campoEnd.setStyle(si);

        ComboBox<User> comboCliente = new ComboBox<>();
        comboCliente.getItems().addAll(userCtrl.listarPorPerfil(PerfilUtil.CLIENTE));
        comboCliente.setValue(o.getCliente());
        comboCliente.setMaxWidth(Double.MAX_VALUE);
        comboCliente.setStyle(si);
        comboCliente.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(User u, boolean empty) { super.updateItem(u, empty); setText(empty || u == null ? null : u.getNome()); }
        });
        comboCliente.setButtonCell(comboCliente.getCellFactory().call(null));

        Label msg = new Label(); msg.setVisible(false); msg.setManaged(false); msg.setWrapText(true);

        Button btnSalvar = new Button("Salvar");
        btnSalvar.setMaxWidth(Double.MAX_VALUE);
        btnSalvar.setStyle(estiloBtnPrimario());
        btnSalvar.setOnAction(e -> {
            String nn = campoNome.getText().trim();
            if (nn.isEmpty()) { mostrarMensagem(msg, "Nome é obrigatório!", false); dialog.sizeToScene(); return; }
            try {
                obraCtrl.editarObra(o.getId(), nn, campoEnd.getText().trim(), comboCliente.getValue());
                dialog.close();
                mostrarMensagem(mensagemPai, "Obra atualizada!", true);
                recarregar.run();
            } catch (Exception ex) { mostrarMensagem(msg, ex.getMessage(), false); dialog.sizeToScene(); }
        });

        layout.getChildren().addAll(titulo, lbl("Nome"), campoNome, lbl("Endereço"), campoEnd,
            lbl("Cliente"), comboCliente, msg, btnSalvar);
        dialog.setScene(new Scene(layout));
        dialog.show();
    }

    // ---- HELPERS ----
    private String corStatus(StatusAtendimento s) {
        return switch (s) {
            case ABERTO      -> "#6c757d";
            case ANALISE     -> "#fd7e14";
            case AGENDADO    -> "#0d6efd";
            case ATENDIMENTO -> "#0f6e56";
            case CONCLUIDO   -> "#198754";
            case CANCELADO   -> "#dc3545";
        };
    }

    private Label lbl(String txt) {
        Label l = new Label(txt);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: #666660;");
        return l;
    }

    private String estiloInput() {
        return "-fx-background-color: #f0f0e8; -fx-border-color: #d0d0c8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-pref-height: 34; -fx-font-size: 13px;";
    }

    private String estiloBtnPrimario() {
        return "-fx-background-color: #2c2c2a; -fx-text-fill: #f1efe8; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-pref-height: 34;";
    }

    private void mostrarMensagem(Label label, String texto, boolean sucesso) {
        label.setText(texto);
        label.setVisible(true); label.setManaged(true);
        label.setStyle("-fx-font-size: 12px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-border-radius: 6;"
            + (sucesso ? "-fx-background-color: #eaf3de; -fx-text-fill: #3b6d11; -fx-border-color: #c0dd97;"
                       : "-fx-background-color: #fcebeb; -fx-text-fill: #a32d2d; -fx-border-color: #f09595;"));
    }
}
