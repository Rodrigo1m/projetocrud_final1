# Projeto CRUD - Sistema de Gerenciamento de Obras e Usuários

Este projeto é uma aplicação desktop/web híbrida em **Java 21** usando **Spring Boot 4**, **JavaFX 21** e **Spring Data JPA**.
A aplicação gerencia usuários, obras, equipes e atendimentos em um sistema funcional com autenticação e dashboard.

---

## Onde está o projeto

O código principal está em:

- `projetocrud/pom.xml`
- `projetocrud/src/main/java/com/grupotres/projetocrud/`
- `projetocrud/src/main/resources/application.properties`

A pasta `projetocrud` é o diretório correto do projeto Maven.

---

## Visão geral do funcionamento

### 1. Inicialização

A classe principal é:

- `ProjetocrudApplication.java`

Ela inicializa o contexto Spring Boot e, em seguida, lança a aplicação JavaFX `TelaInicial`.

### 2. Camadas do sistema

O projeto usa uma arquitetura em camadas:

- `controller/` - expõe operações de negócio para a interface
- `service/` - contém regras de negócio e orquestração
- `repository/` - mapeia entidades JPA para o banco de dados H2
- `model/` - define as entidades do domínio
- `view/` - contém telas JavaFX para o fluxo do usuário
- `util/` - componentes de apoio, como seleção de perfil

### 3. Fluxo de telas

O fluxo da aplicação passa por:

1. `TelaInicial` - tela de login e acesso principal
2. `TelaUsuario` - painel principal após login
3. `TelaDashboard` - visão geral de obras e atendimentos
4. `TelaObras` - gerenciamento completo de obras e equipes

---

## Funcionalidades implementadas

### Autenticação e controle de usuários

- Login seguro usando email e senha
- Criptografia de senha com **BCrypt**
- Perfis de usuário com regras no app
- Controle de permissões na interface

Perfis suportados:

- `ADMIN MORHAR`
- `CLIENTE`
- `TRABALHADOR INTERNO MORHAR`
- `EMPRESA TERCERIZADA`

### Gerenciamento de usuários

A aplicação permite:

- cadastrar novos usuários
- listar usuários existentes
- editar usuário existente
- excluir usuário

### Gerenciamento de obras

A aplicação controla:

- criação de obras
- edição e exclusão de obras
- atribuição de clientes às obras
- gerenciamento de equipes associadas à obra
- registro de tipos de atendimento por obra
- controle de status de atendimento
- gestão de terceirizadas associadas a atendimentos

### Dashboard de obras

A `TelaDashboard` apresenta métricas como:

- total de obras visíveis ao usuário
- total de atendimentos registrados
- obras com equipe
- obras sem equipe
- contagem de atendimentos por status
- distribuição de obras por cliente

O conteúdo visível depende do perfil do usuário:

- administradores e responsáveis técnicos veem todas as obras
- clientes veem suas obras
- membros de equipe veem obras onde participam
- terceirizadas veem obras por atendimento

---

## Principais classes e responsabilidades

### `ProjetocrudApplication.java`

- inicializa Spring Boot
- injeta o contexto em `TelaInicial`
- inicia o launcher JavaFX

### `UsuarioController` / `UsuarioService`

Responsáveis pelas operações de usuário:

- login
- cadastro
- listagem
- edição
- exclusão

### `ObraController` / `ObraService`

Responsáveis pelas operações de obra:

- criar obra
- listar obras completas
- filtrar obras por cliente ou membro
- editar obra
- excluir obra
- atribuir/remover equipe
- adicionar tipo de atendimento
- alterar status de atendimento
- associar/remover terceirizadas
- criar e gerenciar tipos de atendimento

### `TelaInicial`, `TelaUsuario`, `TelaObras`, `TelaDashboard`

- `TelaInicial` controla autenticação e a entrada do usuário
- `TelaUsuario` monta o painel principal com abas
- `TelaObras` exibe o CRUD de obras e suas equipes
- `TelaDashboard` mostra a visão geral e métricas do sistema

---

## Banco de dados

A aplicação usa banco H2 em memória configurado em `src/main/resources/application.properties`:

```properties
spring.application.name=projetocrud
server.port=0

spring.datasource.url=jdbc:h2:mem:morhardb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

- `server.port=0` usa porta dinâmica no startup
- a console H2 fica disponível em `/h2-console`
- o esquema é mantido pelo Hibernate com `update`

---

## Como executar

Abra um terminal em `projetocrud` e use:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.
\mvnw.cmd spring-boot:run
```

Ou para compilar apenas:

```bash
./mvnw clean package
```

Para rodar testes:

```bash
./mvnw test
```

---

## Estrutura do projeto

```
projetocrud/
├── mvnw
├── mvnw.cmd
├── pom.xml
├── src/main/java/com/grupotres/projetocrud/
│   ├── ProjetocrudApplication.java
│   ├── controller/
│   │   ├── ObraController.java
│   │   └── UsuarioController.java
│   ├── model/
│   │   ├── Obra.java
│   │   ├── ObraTipoAtendimento.java
│   │   ├── StatusAtendimento.java
│   │   ├── StatusHistorico.java
│   │   ├── TipoAtendimento.java
│   │   └── User.java
│   ├── repository/
│   │   ├── ObraRepository.java
│   │   ├── ObraTipoAtendimentoRepository.java
│   │   ├── TipoAtendimentoRepository.java
│   │   └── UserRepository.java
│   ├── service/
│   │   ├── ObraService.java
│   │   └── UsuarioService.java
│   ├── util/
│   │   └── PerfilUtil.java
│   └── view/
│       ├── TelaInicial.java
│       ├── TelaUsuario.java
│       ├── TelaObras.java
│       └── TelaDashboard.java
└── src/main/resources/
    └── application.properties
```

---

## Observações

- A UI é feita em JavaFX, mas o backend usa Spring Boot para injeção de dependência e persistência.
- O aplicativo roda como uma aplicação desktop com servidor embarcado.
- A porta é escolhida automaticamente, então verifique o console para saber o endereço HTTP.

---

## Dicas rápidas

- Ao iniciar, faça login com um usuário cadastrado.
- Use a aba `Dashboard` para ver o panorama das obras.
- Use a aba `Obras` para cadastrar e gerenciar equipes e atendimentos.
- Use a aba `Usuários` para administrar perfis e permissões.
