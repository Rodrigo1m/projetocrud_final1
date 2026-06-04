package com.grupotres.projetocrud.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.grupotres.projetocrud.controller.UsuarioController;
import com.grupotres.projetocrud.controller.ObraController;
import com.grupotres.projetocrud.model.User;
import com.grupotres.projetocrud.util.PerfilUtil;

import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Optional;

public class TelaUsuario {

    private final UsuarioController controller;
    private final ObraController obraController;
    private final User usuarioLogado;
    private Stage stage;

    private String corBadge(String perfil) {
        if (perfil == null) return "#e8e8e0|#444440";
        return switch (perfil) {
            case PerfilUtil.ADMINISTRADOR       -> "#eeedfe|#3c3489";
            case PerfilUtil.CLIENTE             -> "#e1f5ee|#0f6e56";
            case PerfilUtil.RESPONSAVEL_TECNICO -> "#e6f1fb|#185fa5";
            case PerfilUtil.TERCEIRIZADA        -> "#faeeda|#854f0b";
            case PerfilUtil.EQUIPE              -> "#f0f8e8|#3a6e11";
            // legado
            case "ADMIN MORHAR"                                   -> "#eeedfe|#3c3489";
            case "CLIENTE"                                        -> "#e1f5ee|#0f6e56";
            case "TRABALHADOR INTERNO MORHAR"                     -> "#e6f1fb|#185fa5";
            case "EMPRESA TERCERIZADA (PRESTADOR DE SERVIÇO)"     -> "#faeeda|#854f0b";
            default -> "#e8e8e0|#444440";
        };
    }

    public TelaUsuario(UsuarioController controller, ObraController obraController, User usuarioLogado) {
        this.controller      = controller;
        this.obraController  = obraController;
        this.usuarioLogado   = usuarioLogado;
    }

    public void abrir() {
        stage = new Stage();

        VBox root = new VBox();
        root.setStyle("-fx-background-color: #f5f5f0;");
        root.setPrefWidth(480);

        // ---- HEADER ----
        HBox header = buildHeader();

        // ---- TAB BAR ----
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color: #f5f5f0;");

        Tab tabDashboard = new Tab("Dashboard");
        tabDashboard.setContent(new TelaDashboard(controller, obraController, usuarioLogado).buildView());

        // Tab Obras (todos exceto Terceirizada pura)
        Tab tabObras = new Tab("Obras");
        tabObras.setContent(new TelaObras(controller, obraController, usuarioLogado).buildView());

        // Tab Usuários (apenas Administrador)
        if (usuarioLogado.isAdministrador()) {
            Tab tabUsuarios = new Tab("Usuários");
            tabUsuarios.setContent(buildPainelUsuarios());
            tabs.getTabs().add(tabUsuarios);

            Tab tabTipos = new Tab("Tipos de Atendimento");
            tabTipos.setContent(buildPainelTipos());
            tabs.getTabs().add(tabTipos);
        }

        tabs.getTabs().addAll(tabDashboard, tabObras);
        tabs.getSelectionModel().select(0);

        root.getChildren().addAll(header, tabs);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Sistema Morhar");
        stage.setResizable(true);
        stage.setMinWidth(480);
        stage.setMinHeight(500);
        stage.show();
    }

    // ---- HEADER ----
    private HBox buildHeader() {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 20, 14, 20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e0e0d8; -fx-border-width: 0 0 1 0;");

        String iniciais = iniciais(usuarioLogado.getNome());
        Label avatar = new Label(iniciais);
        avatar.setMinSize(38, 38); avatar.setMaxSize(38, 38);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle("-fx-background-color: #eeedfe; -fx-text-fill: #3c3489; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 19;");

        VBox info = new VBox(2);
        Label nomeLabel = new Label(usuarioLogado.getNome());
        nomeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a1a18;");
        String[] cores = corBadge(usuarioLogado.getPerfil()).split("\\|");
        Label perfilLabel = new Label(usuarioLogado.getPerfil());
        perfilLabel.setStyle("-fx-background-color: "+cores[0]+"; -fx-text-fill: "+cores[1]+"; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 999; -fx-padding: 2 8;");
        info.getChildren().addAll(nomeLabel, perfilLabel);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-border-color: #d0d0c8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #666660; -fx-font-size: 12px; -fx-cursor: hand; -fx-pref-height: 28;");
        btnLogout.setOnAction(e -> {
            stage.close();
            new TelaInicial().start(new Stage());
        });

        header.getChildren().addAll(avatar, info, spacer, btnLogout);
        return header;
    }

    // ---- PAINEL USUÁRIOS ----
    private ScrollPane buildPainelUsuarios() {
        VBox painel = new VBox(8);
        painel.setPadding(new Insets(16));

        HBox barra = new HBox(8);
        Button btnListar = new Button("Listar Usuários");
        btnListar.setStyle("-fx-background-color: #2c2c2a; -fx-text-fill: #f1efe8; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-pref-height: 34;");
        barra.getChildren().add(btnListar);

        Label mensagem = new Label();
        mensagem.setVisible(false); mensagem.setManaged(false);
        mensagem.setWrapText(true);

        VBox lista = new VBox(8);

        btnListar.setOnAction(e -> {
            mensagem.setVisible(false); mensagem.setManaged(false);
            lista.getChildren().clear();
            List<User> usuarios = controller.listarUsuarios();
            if (usuarios.isEmpty()) {
                lista.getChildren().add(new Label("Nenhum usuário."));
                return;
            }
            for (User u : usuarios) {
                lista.getChildren().add(criarCartaoUsuario(u, mensagem, lista, btnListar));
            }
        });

        painel.getChildren().addAll(barra, mensagem, lista);
        ScrollPane sp = new ScrollPane(painel);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #f5f5f0; -fx-background-color: #f5f5f0; -fx-border-color: transparent;");
        return sp;
    }

    private VBox criarCartaoUsuario(User u, Label mensagemPrincipal, VBox lista, Button btnListar) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0d8; -fx-border-radius: 8; -fx-background-radius: 8;");

        HBox top = new HBox(8); top.setAlignment(Pos.CENTER_LEFT);
        Label nomeCard = new Label(u.getNome());
        nomeCard.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a1a18;");
        String[] cb = corBadge(u.getPerfil()).split("\\|");
        Label badge = new Label(u.getPerfil());
        badge.setStyle("-fx-background-color: "+cb[0]+"; -fx-text-fill: "+cb[1]+"; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 999; -fx-padding: 2 7;");
        top.getChildren().addAll(nomeCard, badge);

        Label meta = new Label(u.getEmail() + "  ·  " + u.getStatus() + "  ·  " + u.getDataCadastro());
        meta.setStyle("-fx-font-size: 11px; -fx-text-fill: #888880;");
        card.getChildren().addAll(top, meta);

        HBox acoes = new HBox(6); acoes.setPadding(new Insets(4, 0, 0, 0));
        Button btnEditar  = new Button("Editar");
        btnEditar.setStyle("-fx-background-color: #f0f0e8; -fx-border-color: #d0d0c8; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 11px; -fx-cursor: hand; -fx-pref-height: 26;");
        Button btnExcluir = new Button("Excluir");
        btnExcluir.setStyle("-fx-background-color: #fcebeb; -fx-text-fill: #a32d2d; -fx-border-color: #f09595; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 11px; -fx-cursor: hand; -fx-pref-height: 26;");

        btnEditar.setOnAction(e -> abrirModalEditarUsuario(u, mensagemPrincipal, lista, btnListar));
        btnExcluir.setOnAction(e -> {
            Alert c = new Alert(Alert.AlertType.CONFIRMATION);
            c.setTitle("Confirmar exclusão");
            c.setHeaderText("Excluir " + u.getNome() + "?");
            c.setContentText("Essa ação não pode ser desfeita.");
            c.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
                controller.excluirUsuario(u.getId());
                mostrarMensagem(mensagemPrincipal, "Usuário excluído!", true);
                btnListar.fire();
            });
        });
        acoes.getChildren().addAll(btnEditar, btnExcluir);
        card.getChildren().add(acoes);
        return card;
    }

    private void abrirModalEditarUsuario(User u, Label mensagemPrincipal, VBox lista, Button btnListar) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Editar Usuário");
        dialog.setResizable(false);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");
        layout.setPrefWidth(320);

        String si = "-fx-background-color: #f0f0e8; -fx-border-color: #d0d0c8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-pref-height: 34; -fx-font-size: 13px;";

        Label titulo = new Label("Editar Usuário");
        titulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        TextField campoNome  = field(u.getNome(), si);
        TextField campoEmail = field(u.getEmail(), si);
        PasswordField campoSenha = new PasswordField(); campoSenha.setPromptText("Nova senha"); campoSenha.setStyle(si);

        ComboBox<String> comboPerfil = new ComboBox<>();
        comboPerfil.getItems().addAll(PerfilUtil.TODOS_PERFIS);
        comboPerfil.setValue(u.getPerfil());
        comboPerfil.setMaxWidth(Double.MAX_VALUE);
        comboPerfil.setStyle(si);

        Label msg = new Label(); msg.setVisible(false); msg.setManaged(false); msg.setWrapText(true);

        Button btnSalvar = new Button("Salvar");
        btnSalvar.setMaxWidth(Double.MAX_VALUE);
        btnSalvar.setStyle("-fx-background-color: #2c2c2a; -fx-text-fill: #f1efe8; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-pref-height: 34;");
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setMaxWidth(Double.MAX_VALUE);
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-border-color: #d0d0c8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #666660; -fx-font-size: 13px; -fx-cursor: hand; -fx-pref-height: 34;");
        HBox.setHgrow(btnSalvar, Priority.ALWAYS); HBox.setHgrow(btnCancelar, Priority.ALWAYS);

        btnSalvar.setOnAction(e -> {
            String nn = campoNome.getText().trim();
            String ne = campoEmail.getText().trim();
            String ns = campoSenha.getText();
            String np = comboPerfil.getValue();
            if (nn.isEmpty() || ne.isEmpty() || ns.isEmpty()) { mostrarMensagem(msg, "Preencha todos os campos.", false); dialog.sizeToScene(); return; }
            if (np == null) { mostrarMensagem(msg, "Selecione um perfil.", false); dialog.sizeToScene(); return; }
            u.setNome(nn); u.setEmail(ne); u.definirPerfil(np);
            controller.editarUsuario(u, ns);
            mostrarMensagem(mensagemPrincipal, "Usuário atualizado!", true);
            btnListar.fire(); dialog.close();
        });
        btnCancelar.setOnAction(e -> dialog.close());

        layout.getChildren().addAll(titulo,
            lbl("Nome"), campoNome, lbl("Email"), campoEmail,
            lbl("Senha"), campoSenha, lbl("Perfil"), comboPerfil,
            msg, new HBox(8, btnSalvar, btnCancelar));

        dialog.setScene(new Scene(layout));
        dialog.show();
    }

    // ---- PAINEL TIPOS DE ATENDIMENTO ----
    private ScrollPane buildPainelTipos() {
        VBox painel = new VBox(8);
        painel.setPadding(new Insets(16));

        HBox barra = new HBox(8);
        Button btnListar = new Button("Listar Tipos");
        btnListar.setStyle("-fx-background-color: #2c2c2a; -fx-text-fill: #f1efe8; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-pref-height: 34;");
        Button btnNovo = new Button("+ Novo Tipo");
        btnNovo.setStyle("-fx-background-color: #185fa5; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-pref-height: 34;");
        barra.getChildren().addAll(btnListar, btnNovo);

        Label mensagem = new Label(); mensagem.setVisible(false); mensagem.setManaged(false); mensagem.setWrapText(true);
        VBox lista = new VBox(8);

        Runnable recarregar = () -> {
            lista.getChildren().clear();
            obraController.listarTipos().forEach(t -> {
                HBox card = new HBox(10);
                card.setPadding(new Insets(10));
                card.setAlignment(Pos.CENTER_LEFT);
                card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0d8; -fx-border-radius: 8; -fx-background-radius: 8;");
                VBox txt = new VBox(2);
                Label tn = new Label(t.getNome());
                tn.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
                Label td = new Label(t.getDescricao() == null ? "" : t.getDescricao());
                td.setStyle("-fx-font-size: 11px; -fx-text-fill: #888880;");
                txt.getChildren().addAll(tn, td);
                Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
                Button be = new Button("Editar");
                be.setStyle("-fx-background-color: #f0f0e8; -fx-border-color: #d0d0c8; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 11px; -fx-cursor: hand;");
                Button bx = new Button("Excluir");
                bx.setStyle("-fx-background-color: #fcebeb; -fx-text-fill: #a32d2d; -fx-border-color: #f09595; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 11px; -fx-cursor: hand;");
                be.setOnAction(ev -> {
                    TextInputDialog d1 = new TextInputDialog(t.getNome());
                    d1.setTitle("Editar Tipo"); d1.setHeaderText("Nome do tipo"); d1.setContentText("Nome:");
                    d1.showAndWait().ifPresent(nn -> {
                        TextInputDialog d2 = new TextInputDialog(t.getDescricao());
                        d2.setTitle("Editar Tipo"); d2.setHeaderText("Descrição"); d2.setContentText("Descrição:");
                        d2.showAndWait().ifPresent(nd -> {
                            obraController.editarTipo(t.getId(), nn, nd);
                            mostrarMensagem(mensagem, "Tipo atualizado!", true);
                            lista.getChildren().clear(); btnListar.fire();
                        });
                    });
                });
                bx.setOnAction(ev -> {
                    Alert c = new Alert(Alert.AlertType.CONFIRMATION);
                    c.setTitle("Excluir"); c.setHeaderText("Excluir tipo " + t.getNome() + "?");
                    c.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
                        obraController.excluirTipo(t.getId());
                        mostrarMensagem(mensagem, "Tipo excluído!", true);
                        lista.getChildren().clear(); btnListar.fire();
                    });
                });
                card.getChildren().addAll(txt, sp2, be, bx);
                lista.getChildren().add(card);
            });
        };

        btnListar.setOnAction(e -> { mensagem.setVisible(false); mensagem.setManaged(false); recarregar.run(); });
        btnNovo.setOnAction(e -> {
            TextInputDialog d1 = new TextInputDialog();
            d1.setTitle("Novo Tipo"); d1.setHeaderText("Criar tipo de atendimento"); d1.setContentText("Nome:");
            d1.showAndWait().ifPresent(nn -> {
                if (nn.isBlank()) return;
                TextInputDialog d2 = new TextInputDialog();
                d2.setTitle("Novo Tipo"); d2.setHeaderText("Descrição (opcional)"); d2.setContentText("Descrição:");
                String nd = d2.showAndWait().orElse("");
                obraController.criarTipo(nn, nd);
                mostrarMensagem(mensagem, "Tipo criado!", true);
                recarregar.run();
            });
        });

        painel.getChildren().addAll(barra, mensagem, lista);
        ScrollPane sp = new ScrollPane(painel);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #f5f5f0; -fx-background-color: #f5f5f0; -fx-border-color: transparent;");
        return sp;
    }

    // ---- HELPERS ----
    private Label lbl(String txt) {
        Label l = new Label(txt);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: #666660;");
        return l;
    }

    private TextField field(String val, String style) {
        TextField tf = new TextField(val);
        tf.setStyle(style);
        return tf;
    }

    private String iniciais(String nome) {
        if (nome == null || nome.isBlank()) return "?";
        String[] p = nome.trim().split("\\s+");
        if (p.length == 1) return p[0].substring(0, Math.min(2, p[0].length())).toUpperCase();
        return ("" + p[0].charAt(0) + p[p.length - 1].charAt(0)).toUpperCase();
    }

    private void mostrarMensagem(Label label, String texto, boolean sucesso) {
        label.setText(texto);
        label.setVisible(true); label.setManaged(true);
        label.setStyle("-fx-font-size: 12px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-border-radius: 6;"
            + (sucesso ? "-fx-background-color: #eaf3de; -fx-text-fill: #3b6d11; -fx-border-color: #c0dd97;"
                       : "-fx-background-color: #fcebeb; -fx-text-fill: #a32d2d; -fx-border-color: #f09595;"));
    }
}
