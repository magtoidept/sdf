# TV Launcher (YouTube + Prime Video)

Launcher Android TV minimalista inspirado na Apple TV, com dois cartões (YouTube e Amazon Prime Video),
relógio discreto, navegação total pelo comando e aviso quando uma app não está instalada.

> Estado: o código foi escrito mas **ainda não foi compilado nem testado**. O primeiro passo abaixo
> compila-o automaticamente na nuvem e diz-te logo se há algum erro.

## 1. Obter o APK (sem Android Studio)
1. Cria uma conta gratuita em https://github.com
2. Cria um repositório novo (botão "New"), por exemplo `tv-launcher`.
3. Em "uploading an existing file", arrasta **todo o conteúdo desta pasta** (incluindo a pasta `.github`).
   Clica em "Commit changes".
4. Abre o separador **Actions** → "Build APK". Demora cerca de 5 minutos.
5. Quando ficar a verde, abre **Releases** (lado direito da página do repositório) e descarrega `TVLauncher.apk`.
   Esse é o link direto de descarga (pode ser aberto no browser da TV ou no Downloader).

## 2. Instalar na televisão
- Opção A (mais simples): instala a app **Downloader** (Play Store da TV), cola o link do `TVLauncher.apk`,
  e permite "instalar apps de fontes desconhecidas" para o Downloader quando pedido.
- Opção B: copia o APK para uma pen USB e abre-o com um gestor de ficheiros na TV.
- Opção C (ADB): `adb connect IP_DA_TV` e depois `adb install TVLauncher.apk`.

## 3. Definir como launcher predefinido
1. Prime Home. Se o sistema perguntar que ecrã inicial usar, escolhe "Início" e "Sempre".
2. Se não perguntar: Definições → Aplicações → Aplicações predefinidas (ou "Ecrã inicial") → escolhe "Início".
3. Se a TV não permitir (comum em Google TV / Android 12+), ativa a depuração USB/Wi-Fi nas Opções de programador e corre:
   `adb shell cmd package set-home-activity pt.launcher.tvlauncher/.MainActivity`
4. Se mesmo assim o botão Home abrir o original, a TV bloqueia a troca. Nesse caso abre "Início" a partir da lista de apps
   (ou associa-o a um botão com uma app de remapeamento).

## 4. Voltar ao ecrã inicial original
- Definições → Aplicações → "Início" → "Limpar predefinições" (ou define o launcher original como predefinido), ou
- `adb shell pm clear-home` não existe em todas as versões; o mais fiável é desinstalar:
  Definições → Aplicações → "Início" → Desinstalar, ou `adb uninstall pt.launcher.tvlauncher`.
O launcher original nunca é desativado nem removido.

## Notas
- Os logótipos são desenhados em código (aproximações simples), não os ficheiros oficiais. Podes substituí-los por PNG oficiais mais tarde.
- Começar automaticamente ao ligar a TV é decidido pelo sistema: se este launcher for o predefinido, o Android mostra-o ao arrancar.
