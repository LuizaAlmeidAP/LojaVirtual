# Loja Virtual

Aplicação de livraria virtual para consulta e venda de livros e outros produtos. A interface é construída com Vaadin, e a aplicação usa Spring Boot para a camada de serviços, persistência e segurança.

> **Status:** projeto em desenvolvimento.

Este projeto foi criado com a ajuda da Professora Alessandra Patriarca. Seu objetivo é estudar conceitos de Java e experimentar o desenvolvimento de um projeto mais profissional em comparação com os projetos realizados na faculdade.

## Funcionalidades

- Catálogo público e catálogo para clientes autenticados.
- Cadastro e gerenciamento de clientes, produtos, livros e autores.
- Carrinho de compras e consulta de pedidos.
- Área administrativa para acompanhar vendas e gerenciar o catálogo e clientes.
- Autenticação e controle de acesso por perfil (cliente e administrador).

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

## Testes

Execute os testes automatizados com:

```bash
mvn test
```

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

Por padrão, a aplicação usa H2 em modo arquivo. O banco é criado em `data/lojavirtual.mv.db` e mantido entre execuções. O console do H2 fica disponível em [http://localhost:8080/h2-console](http://localhost:8080/h2-console); a URL JDBC configurada é `jdbc:h2:file:./data/lojavirtual`, com usuário `sa` e senha vazia.

As configurações de conexão e persistência podem ser ajustadas em `src/main/resources/application.properties`.

## Estrutura do código

O código Java está em `src/main/java/pt/loualmeida/lojavirtual` e está organizado nas áreas `model`, `repository`, `service`, `view`, `config`, `enums` e `exception`. Os testes de serviços estão em `src/test/java`.
