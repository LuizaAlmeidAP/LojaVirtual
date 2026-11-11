# Loja Virtual — Livraria

Aplicação web de livraria virtual para consulta e venda de livros e outros produtos, com perfis de **cliente** e **administrador**. A interface é construída com Vaadin e a aplicação usa Spring Boot para a camada de serviços, persistência e segurança.

> **Status:** projeto de estudo em desenvolvimento.

## Sobre o projeto

Este projeto foi criado para aprofundar os conceitos de Java aprendidos na faculdade e praticar uma arquitetura em camadas, indo além dos projetos feitos nas disciplinas. Foi desenvolvido com a orientação de uma professora particular de Java.

## Capturas de tela

<!-- Adicione aqui 2 ou 3 imagens: catálogo público, login e área administrativa -->
<!-- Exemplo: ![Catálogo público](docs/catalogo.png) -->

## Funcionalidades

**Implementado**
- Catálogo público e catálogo para clientes autenticados
- Cadastro e gestão de produtos, livros e autores (área administrativa)
- Filtro de produtos
- Autenticação e controle de acesso por perfil (cliente e administrador), com Spring Security
- Carrinho de compras e consulta de pedidos

**Em desenvolvimento**
- Cadastro de utilizadores (clientes e administradores)
- Atualização dos testes automatizados da camada de serviços
- Configurações do utilizador

## Tecnologias

- Java 21
- Spring Boot 3.5
- Vaadin 24
- Spring Data JPA e Spring Security
- H2 Database
- Maven

## Pré-requisitos

- JDK 21
- Maven 3.6.3 ou superior

Confira as instalações:

```bash
java -version
mvn -version
```

## Executar em desenvolvimento

Na raiz do projeto, inicie a aplicação:

```bash
mvn spring-boot:run
```

Abra [http://localhost:8080](http://localhost:8080) no navegador. A rota inicial apresenta o catálogo público; as áreas de cliente e administração exigem autenticação.

## Acesso de demonstração

Usuários de teste, apenas para ambiente de desenvolvimento:

| Perfil        | Usuário / e-mail | Senha      |
| ------------- | ---------------- | ---------- |
| Administrador | [preencher]      | [preencher] |
| Cliente       | [preencher]      | [preencher] |

## Testes

```bash
mvn test
```

Os testes cobrem a camada de serviços (`src/test/java`) e estão sendo atualizados após a implementação do frontend, por isso alguns podem falhar neste momento.

## Gerar pacote de produção

O perfil `production` prepara e compila os recursos frontend do Vaadin:

```bash
mvn clean package -Pproduction
```

Depois, execute o JAR gerado:

```bash
java -jar target/lojavirtual-0.0.1-SNAPSHOT.jar
```

## Banco de dados

Por padrão, a aplicação usa H2 em modo arquivo. O banco é criado em `data/lojavirtual.mv.db` e mantido entre execuções (a pasta `data/` não é versionada). O console do H2 fica disponível em [http://localhost:8080/h2-console](http://localhost:8080/h2-console); a URL JDBC é `jdbc:h2:file:./data/lojavirtual`, com usuário `sa` e senha vazia. Essa configuração é apenas para desenvolvimento.

As configurações de conexão e persistência podem ser ajustadas em `src/main/resources/application.properties`.

## Estrutura do código

O código Java está em `src/main/java/pt/loualmeida/lojavirtual`:

| Pacote       | Conteúdo                                                                 |
| ------------ | ------------------------------------------------------------------------ |
| `model`      | Entidades: `Usuario`, `Cliente`, `Produto`, `Livro`, `Autor`, `Carrinho`, `ItemVenda`, `Venda` |
| `repository` | Repositórios Spring Data JPA e `ProdutoSpec` (filtros dinâmicos)         |
| `service`    | Regras de negócio e `SegurancaService`                                   |
| `view`       | Telas Vaadin, separadas em `admin` e `cliente`, além de login e home     |
| `config`     | Configuração de segurança (`SecurityConfig`)                             |
| `enums`      | `Categoria`, `FaixaEtaria`, `Genero`, `Perfil`                           |
| `exception`  | Exceções de negócio (dados inválidos, estoque indisponível, etc.)        |
| `utils`      | Utilitários, como `FormatadorUtils`                                      |

Os testes estão em `src/test/java`.