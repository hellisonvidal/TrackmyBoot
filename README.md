# ⚽ TrackmyBoot - Monitor de Preços de Artigos de Futebol

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2024%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.6-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material3](https://img.shields.io/badge/Material%203-1.3-757575?logo=materialdesign&logoColor=white)](https://m3.material.io/)
[![Hilt](https://img.shields.io/badge/Hilt-2.48-FF6D00?logo=dagger&logoColor=white)](https://dagger.dev/hilt/)
[![Room](https://img.shields.io/badge/Room-2.6-4285F4?logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![WorkManager](https://img.shields.io/badge/WorkManager-2.9-4285F4?logo=android&logoColor=white)](https://developer.android.com/topic/libraries/architecture/workmanager)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> **Aplicativo Android nativo** para monitorar preços e ofertas de artigos de futebol: camisas, bolas, chuteiras, meias, luvas, bolsas, caneleiras e acessórios.

---

## 📱 Screenshots

| Home | Busca | Lista de Desejos | Alertas |
|------|-------|------------------|---------|
| ![Home](docs/screenshots/home.png) | ![Busca](docs/screenshots/search.png) | ![Wishlist](docs/screenshots/wishlist.png) | ![Alertas](docs/screenshots/alerts.png) |

| Detalhes | Configurações |
|----------|---------------|
| ![Detail](docs/screenshots/detail.png) | ![Settings](docs/screenshots/settings.png) |

> 💡 *Adicione screenshots reais na pasta `docs/screenshots/` após buildar o app*

---

## ✨ Funcionalidades

### 🔍 **Busca & Descoberta**
- Busca textual inteligente (nome, marca, tags)
- Filtros por **10 categorias**: Camisas, Bolas, Chuteiras, Acessórios, Goleiro, Treino, **Meias, Luvas, Bolsas, Caneleiras**
- Ordenação: Relevância, Menor Preço, Maior Preço, Maior Desconto, Mais Recentes
- **36 lojas** monitoradas: grandes varejistas, marketplaces, lojas de clubes, marcas diretas

### 💰 **Monitoramento de Preços**
- Histórico completo com **gráfico interativo** (Canvas)
- Estatísticas: menor/maior preço, média, economia potencial
- Detecção automática de **novos menores preços históricos**

### ❤️ **Lista de Desejos (Watchlist)**
- Adicione produtos com **preço alvo personalizado**
- Notificações configuráveis: queda de preço, preço alvo atingido, volta ao estoque
- Indicador visual quando produto atinge preço alvo

### 🔔 **Sistema de Alertas Inteligente**
- **4 tipos de alerta**: Queda de preço, Preço alvo, Volta ao estoque, Novo menor preço
- Badge de não lidos na aba de alertas
- "Marcar todos como lidos" com um toque
- Histórico completo de alertas

### ⚙️ **Configurações Completas**
- Tema: Sistema / Claro / Escuro
- Idioma: PT / EN / ES
- Intervalo de verificação: 1h, 3h, 6h, 12h, 24h
- Categorias e lojas habilitadas individualmente
- Desconto mínimo para exibição (slider 0-80%)
- Exportar/importar dados, limpar cache

---

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌───────┐  │
│  │  Home   │ │ Search  │ │Watchlist│ │ Alerts  │ │Settings│  │
│  └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘ └───┬───┘  │
└───────┼────────────┼───────────┼───────────┼──────────┼──────┘
        ▼            ▼           ▼           ▼          ▼
┌─────────────────────────────────────────────────────────────┐
│                      VIEWMODELS                              │
│  HomeVM • SearchVM • WatchlistVM • AlertsVM • SettingsVM    │
│  DetailVM                                                    │
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                           │
│  Product • WatchlistItem • PriceHistory • Alert • AppSettings│
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                        DATA LAYER                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐   │
│  │   Room DB    │  │  Repository  │  │  Remote/Worker   │   │
│  │  (Offline)   │◄─┤  (Single    │─►│  (Sync + BG)     │   │
│  └──────────────┘  │   Source)   │  └──────────────────┘   │
│                    └──────────────┘                          │
└─────────────────────────────────────────────────────────────┘
```

### Padrões Utilizados
- **MVVM + Clean Architecture** - Separação clara de responsabilidades
- **Repository Pattern** - Abstração de fontes de dados
- **Unidirectional Data Flow** - StateFlow + Compose
- **Dependency Injection** - Hilt (compile-time safety)
- **Offline-First** - Room como source of truth

---

## 🛠️ Stack Tecnológico

| Camada | Tecnologia | Versão |
|--------|------------|--------|
| **Language** | Kotlin | 1.9.23 |
| **UI** | Jetpack Compose | 1.6 |
| **Design** | Material 3 | 1.3 |
| **DI** | Hilt | 2.48 |
| **Database** | Room | 2.6 |
| **Network** | Retrofit + OkHttp | 2.11 / 4.12 |
| **Images** | Coil | 2.6 |
| **Background** | WorkManager | 2.9 |
| **Navigation** | Navigation Compose | 2.7 |
| **Serialization** | Kotlinx Serialization | 1.6 |
| **Async** | Coroutines + Flow | 1.7 |

---

## 🚀 Como Executar

### Pré-requisitos
- **Android Studio** Hedgehog (2023.1.1) ou superior
- **JDK 17** (configurado no `gradle.properties` ou AS)
- **Android SDK 34** (compileSdk/targetSdk)
- **Git** (para clonar)

### Clone e Build
```bash
# Clone o repositório
git clone https://github.com/SEU_USUARIO/TrackmyBoot.git
cd TrackmyBoot

# Build debug
./gradlew assembleDebug

# Instalar no device/emulador conectado
./gradlew installDebug
```

### Executar Testes
```bash
# Testes unitários
./gradlew test

# Testes instrumentados (precisa device/emulador)
./gradlew connectedAndroidTest

# Relatório de cobertura (se configurado Jacoco)
./gradlew jacocoTestReport
```

---

## 📁 Estrutura do Projeto

```
TrackmyBoot/
├── .github/
│   └── workflows/           # CI/CD (opcional)
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/trackmyboot/app/
│   │   │   │   ├── data/           # Data Layer
│   │   │   │   │   ├── db/         # Room: Entities, DAOs, Database
│   │   │   │   │   ├── remote/     # API Services
│   │   │   │   │   ├── repository/ # Repositories
│   │   │   │   │   ├── receiver/   # Broadcast Receivers
│   │   │   │   │   └── worker/     # WorkManager Workers
│   │   │   │   ├── di/             # Hilt Modules
│   │   │   │   ├── domain/         # Domain Layer
│   │   │   │   │   └── model/      # Domain Models (10 categorias, 36 lojas)
│   │   │   │   ├── presentation/   # Presentation Layer
│   │   │   │   │   ├── viewmodel/  # ViewModels (6)
│   │   │   │   │   └── MainActivity.kt
│   │   │   │   └── ui/             # UI Layer
│   │   │   │       ├── components/ # Reusable Composables
│   │   │   │       ├── preview/    # @Preview Composables
│   │   │   │       ├── screens/    # Screen Composables (6)
│   │   │   │       └── theme/      # Material3 Theme (Opção 3)
│   │   │   ├── res/                # Resources (values, drawable, xml)
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                   # Unit Tests
│   │   └── androidTest/            # Instrumented Tests
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── .gitignore
├── LICENSE
└── README.md
```

---

## 🔧 Personalização Rápida

### Adicionar Nova Loja
```kotlin // domain/model/Product.kt
enum class Store(...) {
    // ... existentes
    NOVA_LOJA("Nova Loja", "https://novaloja.com.br", "https://logo.novaloja.com.br/logo.png")
}
```

### Adicionar Nova Categoria
```kotlin // domain/model/Product.kt
enum class ProductCategory(...) {
    // ... existentes
    NOVA_CAT("Nova Categoria", "ic_nova_cat")
}
```

### Alterar Intervalo Padrão de Sync
```kotlin // presentation/viewmodel/SettingsViewModel.kt
val priceCheckIntervals = listOf(1, 3, 6, 12, 24) // em horas
```

### Trocar Tema (Design System)
```kotlin // ui/theme/Color.kt - Escolha uma opção:
import com.trackmyboot.app.ui.theme.Option1_CampoVerde      // Clássico
import com.trackmyboot.app.ui.theme.Option2_NoiteEstadio     // Dark Premium
import com.trackmyboot.app.ui.theme.Option3_GramadoPremium   // ✅ Atual (Elegante)
import com.trackmyboot.app.ui.theme.Option4_TorcidaVibrante  // Jovem
import com.trackmyboot.app.ui.theme.Option5_TechEsportivo    // Tech/Gamer
```

---

## 🔔 Permissões Android

| Permissão | Uso | Versão Mínima |
|-----------|-----|---------------|
| `INTERNET` | Acesso à API/Imagens | API 1 |
| `ACCESS_NETWORK_STATE` | Verificar conectividade | API 1 |
| `POST_NOTIFICATIONS` | Alertas de preço | API 33 (Android 13) |
| `RECEIVE_BOOT_COMPLETED` | Iniciar worker no boot | API 1 |
| `FOREGROUND_SERVICE` | Worker em background | API 28 |
| `FOREGROUND_SERVICE_DATA_SYNC` | Sync de dados | API 34 |

---

## 🧪 Testes & Qualidade

```bash
# Lint (análise estática)
./gradlew lint

# Detekt (se configurado)
./gradlew detekt

# Verificar dependências desatualizadas
./gradlew dependencyUpdates
```

---

## 📦 Build de Release

```bash
# Gerar AAB para Play Store
./gradlew bundleRelease

# Gerar APK assinado
./gradlew assembleRelease
```

> Configure `signingConfigs` no `app/build.gradle.kts` com seu keystore antes do release.

---

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch: `git checkout -b feature/nova-funcionalidade`
3. Commit suas mudanças: `git commit -m 'feat: adiciona nova funcionalidade'`
4. Push para a branch: `git push origin feature/nova-funcionalidade`
5. Abra um Pull Request

### Padrão de Commits (Conventional Commits)
```
feat:     nova funcionalidade
fix:      correção de bug
docs:     documentação
style:    formatação (sem alterar lógica)
refactor: refatoração
test:     testes
chore:    manutenção/build
```

---

## 📄 Licença

Este projeto está licenciado sob a **Licença MIT** - veja o arquivo [LICENSE](LICENSE) para detalhes.

```
MIT License

Copyright (c) 2024 TrackmyBoot

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🙏 Agradecimentos

- **Material Design 3** - Design system moderno
- **Jetpack Compose** - UI declarativa revolucionária
- **Hilt** - DI simples e poderosa
- **Room** - SQLite abstraction robusta
- **Coil** - Image loading eficiente
- **WorkManager** - Background work confiável
- **Kotlinx Serialization** - JSON type-safe

---

## 📞 Contato

- **Issues**: [GitHub Issues](https://github.com/SEU_USUARIO/TrackmyBoot/issues)
- **Discussões**: [GitHub Discussions](https://github.com/SEU_USUARIO/TrackmyBoot/discussions)

---

<div align="center">

**Feito com ❤️ para torcedores e atletas que buscam o melhor preço**

⭐ **Deixe uma estrela se este projeto te ajudou!**

</div>