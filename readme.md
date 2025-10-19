# ✈️ Spotted

O **Spotted** é um aplicativo desenvolvido para **entusiastas da aviação (plane spotters)** registrarem, organizarem e acompanharem suas fotografias de aeronaves.  
Com ele, o usuário pode salvar informações como **matrícula**, **modelo**, **local**, **data/hora** e **foto** da aeronave, criando um catálogo pessoal de registros. Semelhante a uma “Pokédex”, mas para aviões vistos e fotografados.

---

## 📱 Funcionalidades Principais

- Login com conta do **Google** (via Firebase Authentication)
- Registro de aeronaves fotografadas com matrícula, modelo, local, data e foto
- Armazenamento das informações no **Firebase Firestore**
- Upload e sincronização das imagens no **Firebase Storage**
- Visualização, edição e exclusão dos registros salvos

---

## 🧩 Tecnologias Utilizadas

- **Linguagem:** Kotlin
- **Framework:** Android Jetpack Compose
- **Backend:** Firebase
    - Firebase Authentication
    - Firebase Firestore
    - Firebase Storage

---

## ⚙️ Como Executar o Projeto

1. **Clone este repositório**
2. **Abra o projeto no Android Studio.**
3. **Configure o Firebase:**
    - Crie um novo projeto no [Firebase Console](https://console.firebase.google.com/).
    - Ative os serviços **Authentication (Google Sign-In)**, **Cloud Firestore** e **Storage**.
    - Baixe o arquivo `google-services.json` e coloque-o na pasta `app/` do projeto.
4. **Execute o aplicativo:**
    - Conecte um dispositivo físico ou use um emulador Android.
    - Clique em **Run ▶️** no Android Studio.

---

## 📸 Sobre o Projeto

Este aplicativo foi desenvolvido como parte de um projeto acadêmico de pós-graduação, com o objetivo de aplicar conceitos de desenvolvimento mobile Android nativo e integração com serviços em nuvem (MBaaS).  
