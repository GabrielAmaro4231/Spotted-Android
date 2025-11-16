# ✈️ Spotted

O **Spotted** é um aplicativo desenvolvido para **entusiastas da aviação (plane spotters)** registrarem e organizarem seus encontros com aeronaves.  
Basta informar o **prefixo da aeronave** e o **aeroporto** onde a foto foi tirada — o app consulta automaticamente a **JetAPI.dev** para preencher o **modelo** e a **companhia aérea**. Assim, o usuário cria um catálogo pessoal de avistamentos, como uma “Pokédex” para aviões.

---

## 📱 Funcionalidades Principais

- Login social com **Google** (via **Supabase**)
- Registro de aeronaves avistadas informando:
    - Prefixo da aeronave ✈️
    - Aeroporto onde a foto foi tirada 🛫
    - Foto da aeronave 📸
    - Modelo e companhia aérea (obtidos automaticamente via JetAPI.dev)
- Armazenamento dos dados no **Supabase Database**
- Upload das fotos no **Supabase Storage**
- Lista de encontros exibida na Home
- Visualização, edição e exclusão dos registros
- Botão **Compartilhar**, copiando o link público da imagem para a área de transferência

---

## 🧩 Tecnologias Utilizadas

- **Linguagem:** Kotlin
- **Framework:** Android Jetpack Compose
- **Backend:** Supabase
    - Google OAuth
    - Supabase Database
    - Supabase Storage
- **API Externa:** JetAPI.dev (consulta do modelo e da companhia aérea)

---

## 🏗️ Fluxo do Aplicativo

### 🔐 1. Tela de Login
Ao abrir o app pela primeira vez, o usuário vê uma tela simples contendo o título e um botão **“Login with Google”**.

### 🏠 2. Home Screen
Após o login, o usuário chega à **Home**, que lista todos os encontros registrados.  
Se não houver registros, a lista aparece vazia.

No canto inferior direito, há um **Floating Action Button (FAB)** que leva o usuário para a tela de cadastro.

### ➕ 3. Tela de Adicionar Aeronave
Nesta tela o usuário deve:

1. Inserir o **prefixo**
2. Inserir o **aeroporto**
3. Escolher uma **imagem da galeria**

Após isso, o app:

- Consulta a **JetAPI.dev** usando o prefixo
- Obtém automaticamente **modelo** e **companhia aérea**
- Salva os dados no **Supabase Database**
- Envia a imagem para o **Supabase Storage**

O novo encontro aparece na Home imediatamente.

### 👁️ 4. Visualização do Encontro
Ao tocar em um card da lista, o usuário é levado à tela de detalhes em **modo somente leitura**, exibindo:

- Prefixo
- Aeroporto
- Foto da aeronave

Há também o botão **Compartilhar**, que copia o link público da imagem para a área de transferência.

### ✏️ 5. Edição
Na mesma tela, o usuário pode entrar no **modo de edição**, podendo:

- Alterar o **aeroporto**
- Alterar a **imagem**

O **prefixo é fixo** e não pode ser modificado.

### 🗑️ 6. Exclusão
Também é possível excluir o encontro, removendo o registro do banco de dados.

---

## 🗄️ Estrutura do Banco de Dados (Supabase)

O projeto utiliza duas tabelas principais no **Supabase Database**:  
**`posts`** — onde ficam armazenados os avistamentos cadastrados pelo usuário,  
e **`airport_list`** — tabela que contém os aeroportos disponíveis para seleção durante o cadastro.

### ✈️ Tabela: `posts`

Armazena cada registro de aeronave avistada. Cada item exibido na Home provém desta tabela.

| Coluna             | Tipo                     | Descrição |
|--------------------|---------------------------|-----------|
| `id`               | bigint                    | Identificador único do post |
| `created_at`       | timestamptz               | Timestamp da criação do registro |
| `user_id`          | uuid                      | ID do usuário autenticado |
| `aircraft_prefix`  | text                      | Prefixo da aeronave |
| `airport_id`       | bigint                    | Referência ao aeroporto na tabela `airport_list` |
| `aircraft_model`   | text                      | Modelo da aeronave (obtido via JetAPI.dev) |
| `aircraft_airline` | text                      | Companhia aérea (via JetAPI.dev) |
| `image_path`       | text                      | Caminho do arquivo no Supabase Storage |

---

### 🛫 Tabela: `airport_list`

Lista todos os aeroportos disponíveis para seleção no app.  
É uma tabela **pré-preenchida**, geralmente através da importação de um arquivo CSV contendo aeroportos oficiais (ICAO/IATA). Você pode obter essa lista em portais internacionais de aviação e importar diretamente no Supabase.

| Coluna           | Tipo           | Descrição |
|------------------|----------------|-----------|
| `id`             | bigint         | Identificador único do aeroporto |
| `created_at`     | timestamptz    | Timestamp da criação do registro |
| `airport_name`   | text           | Nome completo do aeroporto |
| `airport_icao`   | text           | Código ICAO (ex: SBGR) |
| `airport_iata`   | text           | Código IATA (ex: GRU) |
| `airport_city`   | text           | Cidade do aeroporto |

Essa estrutura permite:

- Seleção rápida e consistente de aeroportos na tela de cadastro
- Evitar erros de digitação
- Futuro suporte para filtros, buscas e ordenações

---

## 🌐 Inspirações do Projeto

O **Spotted** foi fortemente inspirado nos principais sites de fotografia aeronáutica do mundo, que são referência para milhares de spotters:

### 📸 [JetPhotos](https://www.jetphotos.com/)
Reconhecido pela sua enorme comunidade global, o JetPhotos inspira a ideia de catálogo visual de avistamentos, com foco em organização, prefixos e metadados de aeronaves.

### 🛩️ [Planespotters.net](https://www.planespotters.net/)
Inspirou principalmente os elementos de ficha técnica das aeronaves, incluindo modelo, companhia aérea e informações agregadas vindas de APIs públicas.

### 🌍 [Airliners.net](https://www.airliners.net/)
Referência histórica no mundo do spotting, serviu como inspiração para a estrutura geral de registros fotográficos, além da ênfase em detalhes de cada aeronave.

Essas plataformas influenciaram tanto a estrutura visual quanto a experiência de uso do **Spotted**, trazendo para o mobile conceitos já consolidados na comunidade internacional de entusiastas da aviação.

---

## ⚙️ Como Executar o Projeto

1. **Clone este repositório**
2. Abra o projeto no **Android Studio**
3. Configure o Supabase:
    - Crie um projeto no [Supabase](https://supabase.com/)
    - Ative **Google OAuth**, **Database** e **Storage**
    - Configure as tabelas e policies necessárias
4. Adicione suas credenciais do Supabase no arquivo de configuração do app
5. Execute o aplicativo em um dispositivo físico ou emulador Android

---

## 📸 Sobre o Projeto

Este aplicativo foi desenvolvido como parte de um projeto acadêmico de pós-graduação, com o objetivo de aplicar conceitos de desenvolvimento mobile nativo e integração com serviços modernos de backend como Supabase e APIs públicas.
