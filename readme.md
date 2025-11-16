[📺 Clique aqui para assistir o vídeo de apresentação do projeto](https://www.youtube.com/watch?v=-pqJkkD-DAM)

# ✈️ Spotted

O **Spotted** é um aplicativo desenvolvido para **entusiastas da aviação (plane spotters)** registrarem e organizarem seus encontros com aeronaves.  
A ideia central é simples: o usuário informa o **prefixo da aeronave** e o **aeroporto**, tira uma foto, e o app organiza tudo como uma espécie de “Pokédex” de aviões 📘✈️.

Este repositório contém **duas versões oficiais do projeto**, cada uma desenvolvida para uma disciplina diferente:

- **Versão Android - Local (Room Database)**
- **Versão Android - Supabase (Auth + Database + Storage)**

A **branch principal (`main`)** representa a **base comum** do projeto — o esqueleto inicial antes das implementações específicas de cada disciplina.

---

# 🔀 Branches do Projeto

Para facilitar a avaliação, cada professor pode acessar diretamente a versão correspondente clicando na branch correta.

---

## ✅ 1. Versão para a disciplina de Desenvolvimento de Aplicativos Android (Room Database)

**Branch:** [`android_room_implementation`](https://github.com/GabrielAmaro4231/Spotted-Android/tree/android_room_implementation)

Esta versão foi desenvolvida para a disciplina de **Desenvolvimento de Aplicativos Android**, utilizando:

- Android Jetpack Compose
- Room Database
- Fluxo local/offline
- CRUD completo localmente

---

## 🌐 2. Versão para a disciplina de Webservices e MBaaS (Supabase)

**Branch:** [`supabase_integration`](https://github.com/GabrielAmaro4231/Spotted-Android/tree/supabase_integration)

Esta versão foi desenvolvida posteriormente para a disciplina de **Webservices e MBaaS**, utilizando:

- Supabase Auth (Google OAuth)
- Supabase Database
- Supabase Storage
- Integração com API externa JetAPI.dev

---

# 📚 Sobre o Projeto

O **Spotted** foi concebido como um aplicativo para registrar avistamentos de aeronaves, inspirado em grandes plataformas do mundo do spotting:

- **[JetPhotos](https://www.jetphotos.com)**
- **[Planespotters.net](https://www.planespotters.net)**
- **[Airliners.net](https://www.airliners.net)**

A intenção é oferecer ao usuário um catálogo pessoal e organizado de registros, composto por:

- Prefixo da aeronave
- Aeroporto
- Foto
- Modelo e companhia aérea via API

---

# 🧩 Funcionalidades da Ideia Base (presentes na `main`)

A branch principal contém apenas a **estrutura inicial compartilhada** entre as versões:

- Navegação inicial entre telas
- Layouts e componentes base com Jetpack Compose
- Organização de pacotes
- Arquitetura inicial do app

As funcionalidades completas (persistência Room ou integração Supabase) estão nas suas respectivas branches.

---

# 📁 Estruturas Completas em Cada Branch

Cada branch contém seu próprio README detalhado com instruções de execução, arquitetura e explicações técnicas.
