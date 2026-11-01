# Criador de Ficha de RPG

Aplicação web em Java para **criar, visualizar, editar e exportar fichas de personagem de RPG de mesa** (regras no estilo D&D 5e), com cálculo automático de HP e Classe de Armadura, ações de combate com desfazer e sistema de descansos com restauração de estado.

> Projeto acadêmico de **Modelagem de Projetos / Padrões de Projeto**: além de funcionar como aplicação, ele é uma vitrine de padrões GoF aplicados a um domínio real (veja a seção [Arquitetura](#4-arquitetura)).

---

## Sumário

1. [Visão geral](#1-visão-geral)
2. [Requisitos](#2-requisitos)
3. [Tecnologias](#3-tecnologias)
4. [Arquitetura](#4-arquitetura)
5. [Pré-requisitos](#5-pré-requisitos)
6. [Instalação, configuração e execução](#6-instalação-configuração-e-execução)
7. [Persistência e banco de dados](#7-persistência-e-banco-de-dados)
8. [Variáveis de ambiente e configuração](#8-variáveis-de-ambiente-e-configuração)
9. [Documentação das rotas e da API](#9-documentação-das-rotas-e-da-api)
10. [Testes](#10-testes)
11. [Deploy](#11-deploy)
12. [Documentação complementar](#12-documentação-complementar)
13. [Limitações conhecidas e próximos passos](#13-limitações-conhecidas-e-próximos-passos)

---

## 1. Visão geral

### 1.1 Objetivo

Oferecer uma ferramenta simples para que jogadores e mestres criem e gerenciem fichas de personagem sem papel e sem planilhas, com as regras básicas já calculadas.

### 1.2 Problema que pretende resolver

Fichas de RPG em papel ou planilha exigem **cálculos manuais repetitivos** (modificadores de atributo, HP máximo, Classe de Armadura, usos de habilidades) e são **propensas a erro** durante a sessão: esquecer de descontar o HP temporário, errar quantos usos de uma habilidade restam, não conseguir voltar atrás após um engano. O sistema automatiza essas regras e mantém um histórico que permite desfazer ações.

### 1.3 Escopo

**Dentro do escopo**

- Criação guiada de personagem (assistente em etapas com revisão) a partir de espécie, antecedente, classe, nível e atributos.
- Ficha interativa com edição automática (autosave).
- Ações de combate: dano, cura, HP temporário e desfazer.
- Descanso curto e longo, uso de habilidades com recarga e interrupção de descanso.
- Exportação da ficha em TXT, JSON e CSV.
- Conteúdo de jogo (classes, espécies, antecedentes) definido em arquivos JSON.

**Fora do escopo (hoje)**

- Autenticação, contas de usuário e multiusuário.
- Banco de dados relacional (as fichas são arquivos JSON — veja a [seção 7](#7-persistência-e-banco-de-dados)).
- Magias, inventário completo e rolagem de dados.
- Regras de outros sistemas além do modelo D&D 5e usado no projeto.

---

## 2. Requisitos

### 2.1 Requisitos funcionais

| ID | Requisito |
|----|-----------|
| RF01 | O sistema deve permitir criar um personagem por um assistente em etapas (informações básicas, espécie e antecedente, classe, features, atributos) seguido de uma tela de revisão. |
| RF02 | O sistema deve validar os dados na criação: nome obrigatório (até 50 caracteres), nível de 1 a 20, atributos de 1 a 20 e existência de classe, espécie e antecedente. |
| RF03 | O sistema deve calcular automaticamente HP máximo e Classe de Armadura, recalculando-os quando atributos, nível ou armadura mudam. |
| RF04 | O sistema deve listar as fichas salvas na página inicial e exibir cada ficha em uma tela própria. |
| RF05 | O sistema deve permitir editar campos da ficha (nome, nível, HP, HP temporário, atributos, armadura, notas) com salvamento automático. |
| RF06 | O sistema deve aceitar HP atual absoluto (`40`) ou relativo (`+10`, `-10`) na tela da ficha. |
| RF07 | O sistema deve aplicar dano descontando primeiro do HP temporário e depois do HP atual. |
| RF08 | O sistema deve curar o personagem conforme seu estado de vida (Normal, Sangrando ou Inconsciente). |
| RF09 | O sistema deve permitir desfazer a última ação de combate (dano, cura, HP temporário). |
| RF10 | O sistema deve permitir descanso curto (gastando dados de vida) e descanso longo (HP total, HP temporário zerado, metade dos dados de vida e todas as features recarregadas). |
| RF11 | O sistema deve controlar os usos de features limitadas por descanso (ex.: Rage, Second Wind, Action Surge) e recarregá-las no tipo de descanso correto. |
| RF12 | O sistema deve permitir **interromper** o último descanso, restaurando a ficha ao estado anterior a ele (padrão Memento). |
| RF13 | O sistema deve exportar a ficha em TXT, JSON e CSV. |
| RF14 | O sistema deve carregar classes, espécies e antecedentes a partir de arquivos JSON, sem alteração de código. |

### 2.2 Requisitos não funcionais

| ID | Requisito | Como é atendido |
|----|-----------|-----------------|
| RNF01 | **Manutenibilidade / extensibilidade** | Padrões de projeto por responsabilidade; novos formatos de exportação, regras de armadura ou validações entram sem alterar código existente. |
| RNF02 | **Integridade dos dados** | Gravação atômica (arquivo `.tmp` e depois `move`), evitando JSON corrompido em caso de falha. |
| RNF03 | **Resiliência na leitura** | Um JSON inválido em `saves/` não impede o carregamento das demais fichas. |
| RNF04 | **Desempenho de interface** | Autosave com *debounce* de 350 ms e gravação assíncrona (pool de threads dedicado). |
| RNF05 | **Compatibilidade retroativa** | Fichas salvas antes de novos campos continuam carregando (campos assumem valores padrão; propriedades desconhecidas são ignoradas). |
| RNF06 | **Portabilidade** | Roda em qualquer SO com JDK 21; sem dependência de banco ou serviços externos. |
| RNF07 | **Usabilidade** | Interface responsiva (painel de combate se reposiciona em telas estreitas) e feedback de estado ("Salvando...", "Salvo ✓"). |
| RNF08 | **Testabilidade** | Regras de negócio concentradas em classes sem dependência de HTTP (modelo, comandos, `RestService`, `FeatureUses`). |

---

## 3. Tecnologias

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.3.2 (Web, Validation, DevTools) |
| Visão | Thymeleaf (renderização no servidor) + HTML/CSS/JavaScript puro (`fetch`) |
| Serialização | Jackson (com módulo `jsr310` para datas) |
| Produtividade | Lombok |
| Build | Maven |
| Testes | JUnit 5 + Spring Boot Test |
| Persistência | Arquivos JSON em disco |
| Diagramas | Mermaid (fontes nos `.md`) e PNG em `Diagramas/` |

---

## 4. Arquitetura

### 4.1 Visão em camadas

```
┌──────────────────────────────────────────────────────────────┐
│  Navegador: templates Thymeleaf + character-sheet.js (fetch) │
└───────────────┬──────────────────────────────┬───────────────┘
                │ páginas (HTML)               │ JSON (REST)
┌───────────────▼──────────────┐  ┌────────────▼───────────────┐
│ Controllers MVC              │  │ Controllers REST           │
│ wizard / view / export       │  │ CharacterSheetRestController│
│                              │  │ CharacterRestController    │
└───────────────┬──────────────┘  └────────────┬───────────────┘
                │        Facade / Services     │
        ┌───────▼──────────────────────────────▼───────┐
        │ CharacterSheetFacade · CharacterService      │
        │ RestService · CharacterCommandInvoker        │
        └───────┬──────────────────────────────┬───────┘
                │ Modelo de domínio            │ Persistência
        ┌───────▼───────────┐        ┌─────────▼────────────────┐
        │ CharacterSheet    │        │ CharacterPersistenceService│
        │ + State, Strategy,│        │ saves/characters/*.json  │
        │ Chain, Memento    │        └──────────────────────────┘
        └───────────────────┘
        Conteúdo de jogo: src/main/resources/data/*.json (Registries)
```

**Ponto importante:** a ficha é **recarregada do disco a cada requisição**. Por isso, estruturas que precisam sobreviver entre requisições (histórico de *undo* do Command e snapshots do Memento) ficam em componentes singleton em memória, fora da instância da ficha.

### 4.2 Padrões de projeto aplicados

| Padrão | Onde | Para quê |
|--------|------|----------|
| **Builder** | `builder/CharacterBuilder` | Monta a ficha (features por nível, HP inicial). |
| **Prototype** | `prototype/`, `registry/*` | Clona classes, espécies e antecedentes carregados dos JSONs. |
| **Facade** | `facade/CharacterSheetFacade` | Esconde validação e criação do controller do assistente. |
| **Chain of Responsibility** | `chain/validation`, `chain/damage` | Validação da criação (nome → nível → registros → atributos) e dano (HP temporário → HP atual). |
| **Observer** | `observer/` | `AutoSaveObserver` grava e `DerivedStatsObserver` recalcula HP/CA a cada mudança. |
| **Strategy** | `strategy/` | Cálculo de estatísticas derivadas e de CA por tipo de armadura. |
| **State** | `state/` | Estado de vida (Normal, Sangrando, Inconsciente) define a cura. |
| **Command** | `command/` | Dano, cura e HP temporário como objetos, com histórico e *undo*. |
| **Decorator** | `decorator/` | Efeitos temporários (itens, HP temporário) sobre a ficha. |
| **Adapter** | `adapter/` | Exportadores TXT, JSON e CSV atrás de uma interface única. |
| **Memento** | `memento/`, `CharacterSheet`, `rest/` | Snapshot antes de cada descanso para poder interrompê-lo. |

### 4.3 Memento — descanso com restauração

| Papel GoF | Classe |
|-----------|--------|
| Memento (face pública) | `memento/RestMemento` — apenas `label()` e `createdAt()` |
| Memento (conteúdo) | `CharacterSheet.SheetMemento` — *record* **privado** |
| Originator | `CharacterSheet` — `createMemento()` e `restore()` |
| Caretaker | `memento/RestHistory` — pilha de até 5 snapshots por personagem |
| Client | `rest/RestService` |

Fluxo: ao iniciar um descanso, `RestService` pede um memento à ficha, entrega ao `RestHistory` e aplica as regras. Em **Interromper descanso**, o snapshot mais recente é removido da pilha e a ficha se restaura a partir dele, com uma única notificação aos observers.

O snapshot guarda HP atual, HP temporário, dados de vida gastos e usos de features. Detalhes, diagrama e decisões de design em [`Diagramas/memento.md`](Diagramas/memento.md).

### 4.4 Organização dos pacotes

```
src/main/java/com/glauser/rpg/CriadorFichaRPG/
├── CriadorFichaRpgApplication.java
├── config/          AsyncConfig (pool de autosave), JacksonConfig
├── controller/      HomeController, CharacterController, CharacterExporterController,
│   │                CharacterSheetRestController, CharacterRestController
│   ├── wizard/      CharacterWizardController (assistente de criação)
│   └── view/        CharacterViewController, CharacterEffectsController
├── facade/          CharacterSheetFacade
├── service/         CharacterService, CharacterPersistenceService
├── rest/            RestService, RestType, FeatureUses        (descansos)
├── memento/         RestMemento, RestHistory                  (Memento)
├── command/         CharacterCommand + Take/Heal/SetTemporaryHp + Invoker
├── chain/           validation/ (criação)  damage/ (dano)
├── observer/        CharacterObserver, AutoSave, DerivedStats
├── strategy/        DerivedStatsStrategy, estratégias de armadura, ExportStrategy
├── state/           LifeState, Normal, Bleeding, Unconscious
├── decorator/       CharacterSheetDecorator, ItemBoost, TemporaryHp, EffectsState
├── adapter/         CharacterExporter, Txt/Json/CSV adapters
├── builder/         CharacterBuilder
├── prototype/       Prototype
├── registry/        Class/Species/Background/Skill/CharacterRegistry
├── model/           character/ (CharacterSheet, Attributes, Features)
│                    content/   (CharacterClass, Species, Backgrounds, Usage...)
└── dto/             DTOs de criação, patch e resposta

src/main/resources/
├── data/            classes/ (7)  species/ (10)  backgrounds/ (16)  — JSON
├── templates/       home, character/view, character/wizard/step-1..4, 6, review, fragments/
└── static/          css/wizard.css, js/character-sheet.js

Diagramas/           diagramas de classe e documentação dos padrões
database/            script SQL (não utilizado pela aplicação — ver seção 7)
saves/characters/    fichas salvas em JSON (gerado em execução)
```

---

## 5. Pré-requisitos

| Ferramenta | Versão | Observação |
|------------|--------|------------|
| JDK | **21** | `java -version` deve indicar 21. |
| Maven | 3.9+ | O repositório contém `mvnw`/`mvnw.cmd`, mas **não** inclui a pasta `.mvn/` (o `.jar` do wrapper é ignorado pelo Git). Use um Maven instalado ou gere o wrapper com `mvn wrapper:wrapper`. |
| Git | qualquer | Para clonar o repositório. |
| Plugin Lombok na IDE | — | Necessário para a IDE reconhecer getters/setters gerados (IntelliJ: *Settings → Plugins → Lombok*; habilite o *annotation processing*). |

Não é necessário instalar banco de dados.

---

## 6. Instalação, configuração e execução

### 6.1 Clonar e compilar

```bash
git clone https://github.com/LuisGlauser/CriadorFichaRPG.git
cd CriadorFichaRPG
mvn clean package
```

O build gera `target/CriadorFichaRPG-0.0.1-SNAPSHOT.jar`.

### 6.2 Executar em desenvolvimento

```bash
mvn spring-boot:run
```

O DevTools reinicia a aplicação ao recompilar e o cache do Thymeleaf está desligado, então alterações nos templates aparecem ao recarregar a página.

### 6.3 Executar o JAR

```bash
java -jar target/CriadorFichaRPG-0.0.1-SNAPSHOT.jar
```

### 6.4 Acessar

Abra <http://localhost:8080>. Na página inicial, crie um personagem pelo assistente ou abra uma ficha existente.

> **Diretório de trabalho:** as fichas são gravadas em `saves/characters/`, **relativo ao diretório em que o comando é executado**. Execute sempre a partir da raiz do projeto para manter o mesmo conjunto de fichas.

---

## 7. Persistência e banco de dados

A aplicação **não usa banco de dados**. Cada ficha é um arquivo `saves/characters/{id}.json`, gravado de forma atômica (escreve em `{id}.tmp` e depois renomeia). Arquivos `.tmp` remanescentes indicam uma gravação interrompida e podem ser apagados.

Estrutura resumida de uma ficha:

```json
{
  "id": "uuid",
  "name": "Thorin",
  "level": 5,
  "attributes": { "strength": 16, "dexterity": 12, "constitution": 14,
                  "intelligence": 8, "wisdom": 10, "charisma": 10 },
  "characterClass": { "...": "..." },
  "species": { "...": "..." },
  "background": { "...": "..." },
  "maxHp": 50, "currentHp": 38, "temporaryHp": 0, "armorClass": 14,
  "armorType": "UNARMORED", "armorBase": 10, "shieldBonus": 0,
  "features": [ { "name": "Rage", "...": "..." } ],
  "hitDiceSpent": 1,
  "featureUsesSpent": { "Rage": 2 },
  "notes": "",
  "updatedAt": "2026-10-26T15:30:00"
}
```

O arquivo `database/criador_ficha_rpg.sql` apenas cria um schema MySQL vazio (`criador_ficha_rpg`, `utf8mb4`). Ele é um resquício de planejamento: **nenhuma dependência JDBC/JPA está configurada** e a aplicação não o lê. Se a persistência migrar para um banco, a troca fica restrita a `CharacterPersistenceService`.

### Conteúdo de jogo

Classes, espécies e antecedentes ficam em `src/main/resources/data/` e são carregados na inicialização pelos *registries*. Para adicionar uma classe, crie um novo arquivo em `data/classes/`:

```json
{
  "name": "Barbarian",
  "hitDie": 12,
  "features": {
    "Rage": {
      "level": 1,
      "usage": {
        "usesByLevel": { "1-2": 2, "3-5": 3 },
        "recharge": ["longRest"]
      }
    }
  }
}
```

Em `usage`, `uses` aceita um número, `proficiency_bonus` ou `charisma_modifier`; `usesByLevel` aceita faixas (`"3-5"`); `recharge` aceita `shortRest`/`short_rest` e `longRest`/`long_rest`.

---

## 8. Variáveis de ambiente e configuração

A aplicação **não exige variáveis de ambiente**. A configuração está em `src/main/resources/application.properties`:

| Propriedade | Valor atual | Descrição |
|-------------|-------------|-----------|
| `spring.application.name` | `CriadorFichaRPG` | Nome da aplicação. |
| `spring.thymeleaf.cache` | `false` | Desliga o cache de templates (desenvolvimento). Ative (`true`) em produção. |

Qualquer propriedade padrão do Spring Boot pode ser sobrescrita por variável de ambiente ou argumento, por exemplo:

```bash
SERVER_PORT=9090 java -jar target/CriadorFichaRPG-0.0.1-SNAPSHOT.jar
java -jar target/CriadorFichaRPG-0.0.1-SNAPSHOT.jar --spring.thymeleaf.cache=true
```

O diretório de saves (`saves/characters`) e o pool de autosave (1 a 2 threads, em `AsyncConfig`) são fixos no código.

---

## 9. Documentação das rotas e da API

As ações da ficha usam JSON. Todas retornam `404` quando a ficha não existe.

### 9.1 Ficha — `/api/characters`

| Método | Rota | Descrição | Resposta |
|--------|------|-----------|----------|
| `PATCH` / `PUT` | `/api/characters/{id}` | Atualiza campos enviados (os ausentes são ignorados). | `CharacterSheetResponseDTO` |
| `POST` | `/api/characters/{id}/save` | Força o salvamento. | `200` |
| `POST` | `/api/characters/{id}/heal` | Cura conforme o estado de vida (State), como Command. | `CharacterSheetResponseDTO` |
| `POST` | `/api/characters/{id}/damage?amount=N` | Aplica dano (HP temporário primeiro). | `CharacterSheetResponseDTO` |
| `POST` | `/api/characters/{id}/undo` | Desfaz a última ação de combate. | `CharacterSheetResponseDTO` |

Campos aceitos no `PATCH`: `name`, `level`, `currentHp`, `temporaryHp`, `notes`, `armorType` (`UNARMORED`, `LIGHT`, `MEDIUM`, `HEAVY`), `armorBase`, `shieldBonus`, `strength`, `dexterity`, `constitution`, `intelligence`, `wisdom`, `charisma`.

```bash
curl -X PATCH http://localhost:8080/api/characters/{id} \
     -H "Content-Type: application/json" \
     -d '{"level": 6, "strength": 18}'

curl -X POST "http://localhost:8080/api/characters/{id}/damage?amount=12"
```

### 9.2 Descansos e features — `/api/characters/{id}`

| Método | Rota | Descrição | Erros |
|--------|------|-----------|-------|
| `GET` | `/rest` | Estado de descanso: dados de vida, usos das features, se há descanso a interromper. | — |
| `POST` | `/rest/short?hitDice=N` | Descanso curto gastando `N` dados de vida (limitado ao que resta). | — |
| `POST` | `/rest/long` | Descanso longo. | — |
| `POST` | `/rest/interrupt` | Restaura o estado anterior ao último descanso (Memento). | `409` se não houver descanso |
| `POST` | `/features/use?name=X` | Gasta um uso da feature `X`. | `400` feature inexistente ou sem usos por descanso; `409` sem usos restantes |

Resposta (`RestStatusDTO`):

```json
{
  "sheet": { "id": "...", "currentHp": 28, "maxHp": 50, "lifeState": "Normal", "...": "..." },
  "hitDie": 12,
  "hitDiceRemaining": 3,
  "hitDiceTotal": 5,
  "canInterrupt": true,
  "lastRestLabel": "Antes do descanso curto",
  "message": "Descanso curto: gastou 2 dado(s) de vida e recuperou 18 HP.",
  "features": [
    { "name": "Rage", "spent": 1, "max": 3, "remaining": 2, "recharge": "Descanso longo" }
  ]
}
```

Regras dos descansos:

| | Curto | Longo |
|---|---|---|
| HP | cada dado gasto cura `dado/2 + 1 + mod. CON` (média, sem rolagem) | máximo |
| HP temporário | mantém | zera |
| Dados de vida | gasta `N` | recupera metade do nível (mín. 1) |
| Features | recarrega as de descanso curto | recarrega as de descanso curto e longo |

Cada descanso também limpa o histórico de *undo* de combate daquele personagem.

### 9.3 Páginas e exportação

| Método | Rota | Descrição |
|--------|------|-----------|
| `GET` | `/` | Página inicial, com a lista de fichas salvas. |
| `GET` | `/character/create/step-1` … `step-4`, `step-6`, `review` | Etapas do assistente (estado mantido em sessão). |
| `POST` | `/character/create/finish` | Valida e cria a ficha. Em caso de erro volta à revisão com a mensagem. |
| `GET` | `/character/view` | Redireciona para a ficha mais recente (ou para `/` se não houver). |
| `GET` | `/character/view/{id}` | Ficha interativa. |
| `POST` | `/character/save/{id}` | Salva a ficha pela tela. |
| `GET` | `/character/export/{id}/{format}` | Baixa a ficha; `format` = `txt`, `json` ou `csv`. Formato inválido retorna `400`. |

---

## 10. Testes

```bash
mvn test
```

Hoje o projeto tem apenas o teste de contexto do Spring (`CriadorFichaRpgApplicationTests.contextLoads`), que garante que todos os beans sobem.

Roteiro de verificação manual do Memento e dos descansos:

1. Crie um Bárbaro nível 5 e abra a ficha.
2. Em **Descanso**, clique em **Usar** em *Rage* duas vezes: os usos mostram `●○○`.
3. Aplique dano e faça um **Descanso curto** gastando 2 dados de vida: o HP sobe e *Rage* continua gasto.
4. Faça um **Descanso longo**: HP no máximo e *Rage* recarregado (`●●●`).
5. Clique em **Interromper descanso**: HP, dados de vida e usos voltam ao estado anterior ao descanso longo.

Próximo passo recomendado: testes unitários JUnit para `FeatureUses`, `RestHistory` e `RestService` (não dependem de HTTP).

---

## 11. Deploy

O repositório **não inclui** Dockerfile nem pipeline de CI/CD. O caminho mais simples é o JAR executável:

```bash
mvn clean package -DskipTests
java -jar target/CriadorFichaRPG-0.0.1-SNAPSHOT.jar --spring.thymeleaf.cache=true
```

Pontos de atenção em produção:

- **Persistência em disco:** monte `saves/` em um volume persistente e faça backup; sem isso, as fichas se perdem ao recriar o servidor/contêiner.
- **Instância única:** o histórico de *undo* e os snapshots de descanso ficam em memória do processo e a gravação é por arquivo; não escale horizontalmente sem antes trocar esses pontos.
- **Sem autenticação:** não exponha a aplicação diretamente à internet.

Exemplo de `Dockerfile` (sugestão, não faz parte do repositório):

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/CriadorFichaRPG-0.0.1-SNAPSHOT.jar app.jar
VOLUME /app/saves
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.thymeleaf.cache=true"]
```

```bash
docker build -t criador-ficha-rpg .
docker run -p 8080:8080 -v "$(pwd)/saves:/app/saves" criador-ficha-rpg
```

---

## 12. Documentação complementar

| Arquivo | Conteúdo |
|---------|----------|
| [`Diagramas/memento.md`](Diagramas/memento.md) | Padrão Memento: estrutura, fluxo, decisões e limitações. |
| [`Diagramas/command.md`](Diagramas/command.md) | Padrão Command: ações de combate e *undo*. |
| [`Diagramas/DIAGRAM.md`](Diagramas/DIAGRAM.md) | Diagrama de classes geral (Mermaid). |
| `Diagramas/*.png` | Diagramas de classes, Observer + Strategy, State, Command e Memento. |

---

## 13. Limitações conhecidas e próximos passos

- Os snapshots do Memento e o histórico do Command ficam em memória e se perdem ao reiniciar o servidor.
- **Interromper descanso** restaura HP, HP temporário, dados de vida e usos para o estado anterior ao descanso, sobrescrevendo o que mudou depois dele.
- Banco de dados relacional ainda não adotado (o script em `database/` não é usado).
- Cobertura de testes mínima.
- Evoluções possíveis: checkpoints nomeados persistidos em disco (Memento), testes JUnit, Dockerfile e CI, autenticação e múltiplos usuários.

---

## Autor

**Luis Glauser** — [github.com/LuisGlauser/CriadorFichaRPG](https://github.com/LuisGlauser/CriadorFichaRPG)
