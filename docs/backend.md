# 🏗️ Desafio Fullstack Integrado

## Visão Geral
Este repositório contém a solução proposta para o desafio fullstack em camadas, com foco nas partes de banco de dados, EJB e backend. O frontend Angular permanece pendente conforme solicitado.

## Estrutura
- `db/`: scripts de schema e seed executados automaticamente pelo backend em H2.
- `ejb-module/`: módulo Jakarta EE com o serviço `BeneficioEjbService` corrigido e exposto via interface remota.
- `backend-module/`: API Spring Boot com CRUD de Benefícios, consumo do EJB e testes automatizados.
- `docs/`: instruções originais do desafio.
- `.github/workflows/`: pipeline de CI Maven.

## Pré-requisitos
- JDK 17
- Maven 3.9+
- (Opcional) Servidor de aplicação Jakarta EE 10 compatível com EJB remoto (WildFly, Payara etc.).

## Executando o Backend

### Instalar o EJB no repositório local e depois subir o backend

Comandos:
- Compilar o projeto:
  - mvn clean package
- Instalar o EJB: 
    - mvn -f ejb-module/pom.xml -DskipTests install
- Subir o backend:
  - mvn -f backend-module spring-boot:run

O backend sobe em http://localhost:8080 e expõe a documentação em http://localhost:8080/swagger-ui.html.

Por padrão é utilizado um banco H2 em memória carregado com os dados de db/schema.sql e db/seed.sql.

## Integração com EJB
O backend delega a operação de transferência para o módulo EJB por meio de uma porta (`BeneficioTransferPort`). Dois modos estão disponíveis:

1. **Local (default)** – usa `LocalBeneficioTransferService`, reutilizando a lógica com locking otimista e rollback. Ideal para desenvolvimento rápido e execução de testes.
2. **Remoto** – habilite a chamada ao EJB implantado em um servidor Jakarta EE definindo as propriedades abaixo:

```yaml
ejb:
  beneficio:
    enabled: true
    jndi-name: java:global/bip/BeneficioEjbService
    jndi:
      java.naming.factory.initial: org.wildfly.naming.client.WildFlyInitialContextFactory
      java.naming.provider.url: http-remoting://localhost:8080
      java.naming.security.principal: usuario
      java.naming.security.credentials: senha
```

A interface remota compartilhada `BeneficioTransferRemote` garante contrato único entre backend e EJB.

### Implantando o EJB no servidor
1. Gere o artefato com mvn -pl ejb-module clean package.
2. Faça o deploy do ejb-module/target/ejb-module-0.0.1-SNAPSHOT.jar no servidor escolhido.
3. Ajuste o jndi-name conforme o nome global disponibilizado pelo servidor.

## Endpoints Principais
- GET /api/v1/beneficios – lista benefícios.
- GET /api/v1/beneficios/`id` – detalhe.
- POST /api/v1/beneficios – cria.
- PUT /api/v1/beneficios/`id` – atualiza.
- DELETE /api/v1/beneficios/`id` – remove.
- POST /api/v1/beneficios/transfer – transfere valores (body: { "fromId": 1, "toId": 2, "amount": 100.00 }).

As exceções de validação retornam status 400/422 e mensagens padronizadas em JSON.

## Testes
Os testes cobrem regras de negócio e o fluxo REST: 
- Comando para execução dos testes:
  - mvn test

* BeneficioServiceTest: cenários de transferência e saldo.
* BeneficioControllerTest: CRUD e endpoints de transferência via MockMvc.

