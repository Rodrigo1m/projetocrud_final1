# 🎯 Projeto CRUD - Sistema de Gerenciamento de Usuários

Uma aplicação **Spring Boot** com interface gráfica **JavaFX** para gerenciamento completo de usuários com autenticação segura, controle de perfis e operações CRUD.

## 📖 Sobre o Projeto

Este é um sistema completo de **CRUD** (Create, Read, Update, Delete) desenvolvido em **Java** com **Spring Boot** para gerenciar usuários de forma segura e eficiente. 

O projeto combina:
- 🖥️ **Backend poderoso** com Spring Boot 4 e Java 21
- 🔐 **Segurança avançada** com criptografia BCrypt
- 🎨 **Interface visual amigável** com JavaFX
- 💾 **Banco de dados relacional** com JPA/Hibernate
- 👥 **Controle de acessos** por perfil de usuário

Ideal para estudar arquitetura em camadas, segurança de aplicações Java e integração Spring Boot com JavaFX.

## ⚡ Quick Start - Como Executar em 3 Passos

### 1️⃣ Clonar o repositório
```bash
git clone <seu-repositorio>
cd projetocrud
```

### 2️⃣ Executar a aplicação
```bash
./mvnw spring-boot:run
```
> No Windows: `.\mvnw.cmd spring-boot:run`

### 3️⃣ Acessar a aplicação
- 🖥️ **Aplicação**: http://localhost:PORT (a porta é exibida no console)
- 💾 **H2 Console**: http://localhost:PORT/h2-console

**Pronto!** A aplicação está rodando com interface gráfica JavaFX aberta.

---

## ✨ Funcionalidades Principais

### 🔐 Autenticação e Segurança
- **Login seguro** com validação de email e senha
- **Hash de senha** com BCrypt (nunca armazena senhas em texto puro)
- **Perfis de usuário** com diferentes níveis de acesso:
  - ADMIN MORHAR
  - CLIENTE
  - TRABALHADOR INTERNO MORHAR
  - EMPRESA TERCERIZADA (PRESTADOR DE SERVIÇO)

### 👥 Gerenciamento de Usuários
- ✅ **Criar** - Cadastrar novos usuários com validação de dados
- ✅ **Ler** - Listar todos os usuários cadastrados
- ✅ **Atualizar** - Editar dados do usuário (nome, email, perfil, etc)
- ✅ **Deletar** - Remover usuários do sistema
- ✅ **Autenticar** - Sistema de login com verificação de credenciais

### 📋 Atributos do Usuário
- **ID** - Identificador único (auto-incrementado)
- **Nome** - Nome do usuário
- **Email** - Email único para login
- **Senha** - Criptografada com BCrypt
- **Perfil** - Nível de acesso/permissão
- **Status** - Ativo ou Inativo
- **Data de Cadastro** - Registrada automaticamente
- **Permissões** - Controle se pode editar e se está na equipe

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **Java** | 21 | Linguagem principal |
| **Spring Boot** | 4.0.5 | Framework web e injeção de dependência |
| **Spring Data JPA** | Latest | Abstração do banco de dados |
| **Hibernate** | 7.2.7 | Mapeamento objeto-relacional |
| **H2 Database** | 2.4.240 | Banco de dados em memória/arquivo |
| **JavaFX** | 21 | Interface gráfica desktop |
| **BCrypt** | Spring Security | Criptografia de senhas |
| **Maven** | 3.9+ | Gerenciador de dependências |

## 📂 Estrutura do Projeto

```
src/main/java/com/grupotres/projetocrud/
├── ProjetocrudApplication.java      # Classe principal da aplicação
├── controller/
│   └── UsuarioController.java       # Controller com operações CRUD
├── model/
│   └── User.java                    # Entidade de usuário
├── service/
│   └── UsuarioService.java          # Lógica de negócios
├── repository/
│   └── UserRepository.java          # Acesso ao banco de dados
├── util/
│   └── PerfilUtil.java              # Utilitário para seleção de perfil
└── view/
    ├── TelaInicial.java             # Tela de boas-vindas/menu
    └── TelaUsuario.java             # Tela de gerenciamento de usuários

resources/
└── application.properties            # Configurações da aplicação
```

## 🚀 Pré-requisitos

- **Java 21** ou superior
- **Maven 3.9+** (incluso no projeto com wrapper)
- Nenhuma configuração adicional necessária!

### Compilar o Projeto (Opcional)
```bash
./mvnw clean package
```

## 📡 API REST / Endpoints Disponíveis

### Controller: UsuarioController

| Operação | Método | Descrição |
|----------|---------|-----------|
| **Login** | `realizarLogin(email, senha)` | Autentica usuário |
| **Cadastro** | `cadastrarUsuario(nome, email, senha, perfil)` | Cria novo usuário |
| **Listar** | `listarUsuarios()` | Retorna todos os usuários |
| **Editar** | `editarUsuario(user, novaSenha)` | Atualiza dados do usuário |
| **Deletar** | `excluirUsuario(id)` | Remove usuário pelo ID |

## 🔒 Segurança

### Proteção de Senhas
- Todas as senhas são criptografadas com **BCrypt**
- Verificação de senha usa comparação segura (timing-safe)
- Senhas nunca são armazenadas em texto puro

### Validações
- Email obrigatório e validado
- Senha obrigatória e com requisitos mínimos
- Nome obrigatório
- Perfil selecionado entre opções válidas

## 💾 Banco de Dados

### Banco H2
- Banco de dados em memória (desenvolvimento rápido)
- Console web disponível em `/h2-console`
- URL padrão: `jdbc:h2:mem:projetocrud`
- Usuário: `sa` (sem senha)

### Tabela users
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(100) NOT NULL,
    status VARCHAR(50) DEFAULT 'Ativo',
    data_cadastro DATE,
    equipe_morhar BOOLEAN DEFAULT FALSE,
    pode_editar BOOLEAN DEFAULT FALSE
);
```

## 🎨 Interface Gráfica

### Telas
- **TelaInicial** - Menu de boas-vindas e opções principais
- **TelaUsuario** - Gerenciamento completo de usuários com CRUD visual

O projeto usa **JavaFX** para proporcionar uma experiência desktop moderna.

## 🧪 Testes

Testes unitários inclusos:
```bash
./mvnw test
```

## 📝 Configurações (application.properties)

```properties
# Server
server.port=0  # Porta dinâmica

# Database H2
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:projetocrud
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa

# JPA
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

## 👨‍💻 Arquitetura

### Padrão em Camadas
```
┌─────────────────────────┐
│      JavaFX (UI)        │
├─────────────────────────┤
│    UsuarioController    │
├─────────────────────────┤
│     UsuarioService      │
├─────────────────────────┤
│    UserRepository       │
├─────────────────────────┤
│   H2 Database (JPA)     │
└─────────────────────────┘
```

- **View**: Interface gráfica JavaFX
- **Controller**: Recebe requisições
- **Service**: Lógica de negócios e validações
- **Repository**: Acesso aos dados
- **Database**: Persistência

## 🎯 Casos de Uso

1. **Novo usuário** → Cadastro → Armazenamento no BD
2. **Login** → Validação → Acesso ao sistema
3. **Editar usuário** → Atualizar dados → Confirmar alterações
4. **Listar usuários** → Exibir tabela de usuários
5. **Deletar usuário** → Remover do BD

## 📊 Perfis de Usuário

### ADMIN MORHAR
- Acesso total ao sistema
- Pode editar todos os usuários
- Gerencia permissões

### CLIENTE
- Acesso limitado
- Pode visualizar dados básicos
- Não pode editar outros usuários

### TRABALHADOR INTERNO MORHAR
- Acesso moderado
- Pode editar conforme permissão
- Membro da equipe interna

### EMPRESA TERCERIZADA (PRESTADOR DE SERVIÇO)
- Acesso específico
- Integração com sistema de prestação de serviços

## 🐛 Tratamento de Erros

O sistema valida:
- ✗ Nome vazio ou nulo
- ✗ Email vazio, nulo ou já cadastrado
- ✗ Senha vazia ou fraca
- ✗ Perfil inválido
- ✗ Credenciais incorretas no login

## 📈 Melhorias Futuras

- [ ] Recuperação de senha
- [ ] Autenticação por token (JWT)
- [ ] Audit log de operações
- [ ] Exportar usuários (CSV/PDF)
- [ ] Dashboard com estatísticas
- [ ] Integração com LDAP/Active Directory
- [ ] Autenticação de dois fatores (2FA)

## 📄 Licença

Projeto educacional - Grupo Três

## 👨‍🎓 Desenvolvido por

**Grupo Três** - Projeto de Gerenciamento de Usuários

**Rodrigo Leal**

**Thais soares**

**Maria Eduarda**

**Antonio Carlos**

---

**Status**: Em desenvolvimento ✨  
**Versão**: 0.0.1-SNAPSHOT  
**Java**: 21  
**Spring Boot**: 4.0.5
