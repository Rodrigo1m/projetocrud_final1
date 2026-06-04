package com.grupotres.projetocrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.grupotres.projetocrud.view.TelaInicial;

@SpringBootApplication
public class ProjetocrudApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ProjetocrudApplication.class, args);
        TelaInicial.springContext = context;
        javafx.application.Application.launch(TelaInicial.class, args);
    }
}
