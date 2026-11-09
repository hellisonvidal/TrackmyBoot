<p align="center"><img src="docs/icon-512.png" width="128" alt="Ícone do TrackmyBoot"></p>

# TrackmyBoot ⚽

App Android para **monitorar ofertas de equipamentos de futebol**: camisas, chuteiras, bolas, meiões,
luvas de goleiro, caneleiras, calções, agasalhos e acessórios. Os preços vêm de várias lojas on-line,
e você pode filtrar tudo pelo **clube para quem torce**.

> Versão 0.1.0, a primeira. Kotlin, Jetpack Compose e Material 3. Android 8.0 (API 26) ou mais novo.

## Funcionalidades

| Aba | O que faz |
|-----|-----------|
| **Início** | Grade de categorias e atalhos para os seus clubes |
| **Clubes** | Busca de clubes e seleções pelo nome ou apelido ("Galo", "Timão", "SPFC"...). A ⭐ marca os clubes para quem você torce. Na tela do clube aparecem as ofertas dele, filtradas por categoria |
| **Buscar** | Busca livre em todas as lojas, com filtro por categoria |
| **Alertas** | Produtos que você acompanha, com preço alvo opcional, menor preço visto e um gráfico do histórico. A cada 6 horas o app confere os preços de novo e manda uma notificação quando algum baixa |
| **Configurações** | Avisos de queda de preço e lojas consultadas. Dá para ligar e desligar cada loja e **cadastrar lojas novas**, como a loja oficial do seu clube, se ela usar VTEX ou Shopify |

Em todas as listas você pode ordenar por maior desconto, menor preço ou maior preço.

## Como os preços são capturados

Cada loja usa um **conector** que depende da plataforma de e-commerce dela (`app/src/main/java/com/trackmyboot/app/data/source/`):

| Conector | Como captura | Lojas embutidas |
|----------|--------------|-----------------|
| `WebPageSource` | Abre a página de busca da loja e lê os dados de produto embutidos nela (JSON-LD do schema.org, `__NEXT_DATA__` do Next.js e outros blocos JSON) | Netshoes, Centauro, Dafiti, Magazine Luiza, Nike, Adidas, Puma |
| `VtexSource` | API pública `/api/catalog_system/pub/products/search` | FutFanatics, Decathlon, Penalty, Umbro, Topper |
| `MercadoLivreSource` | API `api.mercadolibre.com/sites/MLB/search` | Mercado Livre |
| `ShopifySource` | `/search/suggest.json` e `/products/<handle>.js` | (lojas cadastradas pelo usuário) |
| Só atalho | Abre a busca no site ou app da loja | Amazon, Shopee |

- Todas as lojas ligadas são consultadas **em paralelo**. Se uma falha ou demora mais de 20 s, as outras continuam, e a tela informa quais não responderam.
- Os resultados passam por um filtro de categoria e de clube. Os títulos são comparados sem acentos e com os apelidos dos clubes, então "Camisa do Galo" conta como Atlético Mineiro e "interna" não conta como Inter.
- No fim de cada busca há a seção **"Ver direto nas lojas"**, com atalhos que abrem a mesma busca nas lojas que não retornaram ofertas (Amazon, Shopee e as que bloquearam a consulta).
- O app só mostra preços reais obtidos na internet. Não há mais dados de demonstração.

### ⚠️ Limitações conhecidas (leia antes de publicar)

- **As lojas embutidas não foram testadas contra os sites reais.** O ambiente de desenvolvimento não alcançava os sites das lojas, então os conectores foram testados só com respostas simuladas. Uma loja que use outra plataforma, não traga dados estruturados na página de busca ou bloqueie robôs vai aparecer como "sem resposta", e o atalho para o site dela continua funcionando. Para ajustar uma loja, edite `data/catalog/StoreCatalog.kt`.
- **Amazon e Shopee** não permitem consulta automática sem credenciais (a Amazon exige a Product Advertising API, para afiliados). Por enquanto o app abre a busca direto nelas.
- A API de busca do Mercado Livre pode exigir um token de acesso (OAuth). Sem ele, a loja aparece como indisponível.
- Algumas lojas bloqueiam acesso automatizado com captcha ou WAF. Antes de distribuir o app, revise os termos de uso de cada loja. Programas de afiliados ou APIs oficiais são um caminho mais estável.
- Não existe um conector genérico para lojas de clubes, porque cada clube usa uma plataforma diferente. Por isso o app permite cadastrar a loja oficial pelo endereço e vinculá-la ao clube. Ela então entra automaticamente nas buscas desse clube.

## Arquitetura

```
app/src/main/java/com/trackmyboot/app/
├── TrackmyBootApp.kt        # Application + AppContainer (injeção de dependências manual)
├── MainActivity.kt          # Navegação (Navigation Compose) e barra inferior
├── data/
│   ├── model/               # Category, Club, StoreInfo, Offer
│   ├── catalog/             # Lista de clubes e de lojas embutidas
│   ├── source/              # Conectores: página web (JSON-LD/Next.js), VTEX, Shopify, Mercado Livre
│   ├── local/               # Room: produtos monitorados e histórico de preços
│   ├── prefs/               # DataStore: clubes favoritos, lojas ligadas, lojas personalizadas
│   ├── worker/              # WorkManager: conferência periódica e notificações
│   └── OfferRepository.kt   # Busca em paralelo, filtros, cache e monitoramento
├── ui/                      # Telas, componentes, tema e ViewModels
└── util/
```

## Ícone

O ícone foi criado para o app: uma **sacola de ofertas** verde-limão com uma **lupa e uma bola de futebol** dentro, que representa buscar e comparar ofertas, e uma etiqueta vermelha de **“%”** pendurada na alça. O fundo é azul-marinho. Arquivos:

- `res/drawable/ic_launcher_foreground.xml`, `ic_launcher_background.xml` e `ic_launcher_monochrome.xml` formam o ícone adaptativo e o ícone temático do Android 13+
- `res/drawable/ic_notification.xml` é o ícone das notificações (a lupa)
- `docs/icon.svg` e `docs/icon-512.png` servem para a Play Store e para divulgação

Os escudos dos clubes no app são **estilizados** (cores e iniciais), sem usar marcas registradas.

## Compilar e rodar

Requisitos: JDK 17 ou mais novo e o Android SDK 35 (ou o Android Studio Ladybug ou mais novo).

```bash
./gradlew assembleDebug        # gera app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest    # testes unitários
./gradlew lintDebug
```

O CI do GitHub Actions roda os testes, o lint e gera o APK de debug como artefato a cada push.

## Próximos passos sugeridos

- Validar cada loja embutida em um aparelho real e ajustar `StoreCatalog`
- Token OAuth do Mercado Livre e conectores para outras plataformas (Magento, Salesforce Commerce)
- Backend próprio para capturar os preços no servidor, evitando bloqueios e economizando bateria
- Busca por tamanho e numeração, e comparação do mesmo produto entre lojas

## Licença

MIT. Veja [LICENSE](LICENSE).
