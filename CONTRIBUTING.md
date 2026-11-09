# Guia de Contribuição - TrackmyBoot

Obrigado por considerar contribuir com o TrackmyBoot! 🎉

## 📋 Índice
- [Código de Conduta](#código-de-conduta)
- [Como Contribuir](#como-contribuir)
- [Configuração do Ambiente](#configuração-do-ambiente)
- [Padrões de Código](#padrões-de-código)
- [Commits & PRs](#commits--prs)
- [Reportando Bugs](#reportando-bugs)
- [Sugerindo Funcionalidades](#sugerindo-funcionalidades)

---

## 🤝 Código de Conduta

Este projeto segue o [Contributor Covenant](https://www.contributor-covenant.org/). Ao participar, você concorda em manter um ambiente respeitoso e inclusivo para todos.

---

## 🛠️ Como Contribuir

### 1. Fork & Clone
```bash
# Fork no GitHub, depois clone seu fork
git clone https://github.com/SEU_USUARIO/TrackmyBoot.git
cd TrackmyBoot

# Adicione upstream para manter sincronizado
git remote add upstream https://github.com/ORIGINAL_OWNER/TrackmyBoot.git
```

### 2. Crie uma Branch
```bash
# Atualize main
git checkout main
git pull upstream main

# Crie branch descritiva
git checkout -b feature/nome-da-funcionalidade
# ou
git checkout -b fix/descricao-do-bug
# ou
git checkout -b docs/atualizacao-readme
```

### 3. Desenvolva
- Faça commits pequenos e atômicos
- Escreva testes para novas funcionalidades
- Mantenha o código formatado (`./gradlew ktlintFormat` se configurado)

### 4. Teste Localmente
```bash
# Build e testes
./gradlew assembleDebug test

# Lint
./gradlew lint
```

### 5. Commit & Push
```bash
# Stage mudanças
git add .

# Commit seguindo Conventional Commits
git commit -m "feat: adiciona filtro por faixa de preço na busca"

# Push para seu fork
git push origin feature/nome-da-funcionalidade
```

### 6. Abra Pull Request
- Título claro e descritivo
- Descreva **o que** e **por que** (não apenas como)
- Referencie issues relacionadas: `Closes #123`
- Adicione screenshots se houver mudanças visuais

---

## 💻 Configuração do Ambiente

### Pré-requisitos
- **Android Studio** Hedgehog+ (2023.1.1+)
- **JDK 17** (Temurin/OpenJDK recomendado)
- **Android SDK 34**
- **Git** 2.30+

### Configuração Recomendada do Android Studio
1. `Settings > Editor > Code Style > Kotlin` → Import scheme do projeto
2. `Settings > Tools > Kotlin` → Enable "Use in-process compiler"
3. `Settings > Build > Compiler` → Enable "Build project automatically"

### Variáveis de Ambiente (opcional)
```bash
# ~/.bashrc ou ~/.zshrc
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

---

## 📝 Padrões de Código

### Kotlin Style Guide
- Seguimos [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use `ktlint` com configuração padrão do Android
- 4 espaços para indentação (sem tabs)
- Linhas máximas de 120 caracteres

### Jetpack Compose
- Funções `@Composable` em **PascalCase**
- Parâmetros `Modifier` sempre primeiro após parâmetros obrigatórios
- `Preview` anotadas com `@Preview(name = "Descritivo", showBackground = true)`
- Estados imutáveis preferidos (`val` sobre `var`)

### Arquitetura
- **ViewModels**: sem lógica de negócio direta (ficam em `ui/ViewModels.kt`)
- **Repositories**: Single source of truth, expõem `Flow`
- **Use Cases**: Opcional, para lógica complexa reutilizável
- **Data Layer**: Room + DataStore + OkHttp (conectores de loja) + WorkManager

### Naming Conventions
| Tipo | Convenção | Exemplo |
|------|-----------|---------|
| Classes/Interfaces | PascalCase | `ProductRepository` |
| Funções/Variáveis | camelCase | `getProducts()` |
| Constantes | UPPER_SNAKE_CASE | `MAX_PRICE` |
| Pacotes | lowercase | `com.trackmyboot.app.data` |
| Recursos XML | snake_case | `ic_product_card.xml` |
| Composables | PascalCase | `ProductCard()` |

---

## 📦 Commits & PRs

### Conventional Commits
```
<tipo>(<escopo>): <descrição curta>

[corpo opcional]

[rodapé opcional]
```

### Tipos Principais
| Tipo | Descrição |
|------|-----------|
| `feat` | Nova funcionalidade |
| `fix` | Correção de bug |
| `docs` | Documentação |
| `style` | Formatação (sem lógica) |
| `refactor` | Refatoração |
| `perf` | Performance |
| `test` | Testes |
| `chore` | Build, deps, tooling |
| `ci` | CI/CD |
| `revert` | Reverte commit anterior |

### Exemplos
```bash
# Funcionalidade
git commit -m "feat(search): adiciona filtro por faixa de preço"

# Bug fix
git commit -m "fix(wishlist): corrige cálculo de economia no target price"

# Documentação
git commit -m "docs: atualiza README com instruções de build"

# Refatoração
git commit -m "refactor(data): extrai PriceHistoryRepository"

# Com breaking change
git commit -m "feat(api)!: muda estrutura de resposta da API

BREAKING CHANGE: response agora inclui campo 'metadata'"
```

### Checklist do PR
- [ ] Título segue Conventional Commits
- [ ] Descrição explica **o quê** e **por quê**
- [ ] Testes passam (`./gradlew test`)
- [ ] Lint limpo (`./gradlew lint`)
- [ ] Build compila (`./gradlew assembleDebug`)
- [ ] Screenshots para mudanças visuais
- [ ] Documentação atualizada se necessário
- [ ] Sem warnings novos

---

## 🐛 Reportando Bugs

### Antes de Reportar
1. Verifique se já existe issue similar
2. Teste na versão mais recente
3. Reproduza consistentemente

### Template de Bug Report
```markdown
**Descrição**
Breve descrição do bug

**Passos para Reproduzir**
1. Vá para '...'
2. Clique em '...'
3. Role até '...'
4. Veja o erro

**Comportamento Esperado**
O que deveria acontecer

**Screenshots/Vídeo**
Se aplicável

**Ambiente**
- Device: [ex: Pixel 7, API 34]
- Android: [ex: 14]
- App Version: [ex: 1.0.0]
- Build Type: [Debug/Release]

**Logs** (se houver)
```
cole logs relevantes aqui
```

**Contexto Adicional**
Qualquer outra informação relevante
```

---

## 💡 Sugerindo Funcionalidades

### Template de Feature Request
```markdown
**Problema/Contexto**
Descreva o problema que a funcionalidade resolve

**Solução Proposta**
Descreva a solução desejada

**Alternativas Consideradas**
Outras soluções que você pensou

**Mockups/Referências**
Sketches, links para apps similares, etc.

**Impacto**
- Usuários afetados: [Todos / Segmento específico]
- Prioridade: [Baixa / Média / Alta / Crítica]
- Esforço estimado: [Pequeno / Médio / Grande]
```

---

## 🏷️ Labels do GitHub

| Label | Cor | Uso |
|-------|-----|-----|
| `bug` | 🔴 #d73a4a | Bug confirmado |
| `enhancement` | 🔵 #a2eeef | Nova funcionalidade |
| `documentation` | 🟣 #5319e7 | Docs |
| `good first issue` | 🟢 #7057ff | Para iniciantes |
| `help wanted` | 🟠 #fef2c0 | Precisa de ajuda |
| `question` | ⚪ #d876e3 | Dúvida |
| `wontfix` | ⚫ #ffffff | Não será feito |
| `duplicate` | ⚫ #cfd3d7 | Duplicado |
| `priority: high` | 🔴 #b60205 | Urgente |
| `priority: low` | 🟢 #0e8a16 | Baixa prioridade |

---

## 📞 Dúvidas?

- Abra uma [Discussion](https://github.com/SEU_USUARIO/TrackmyBoot/discussions)
- Marque como `question`
- A comunidade e maintainers vão responder

---

**Obrigado por contribuir!** 💚

*Este guia é baseado em boas práticas da comunidade open source e pode evoluir.*