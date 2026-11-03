# Frigus_BackEnd

Painel administrativo do Frigus (Java 21, Servlet/JSP, Tomcat 10.1 e PostgreSQL) e a landing page do projeto.

## O que tem aqui
- **Landing page** (`index.html`, `sobre-nos.html`, `suporte.html`): páginas públicas, com o botão "Área restrita" que leva ao login.
- **Acesso** (`/login`, `/cadastro`, `/cadastro/senha`): entrar e criar conta em duas etapas.
- **Painel** (`/admin/...`): visão geral, usuários, empresas, geladeiras, frigoríficos, produtos, receitas, lista de compras, planos e configurações.
- **Telas de erro** (401, 403, 404 e 500) e **tela de carregamento** com o Friguinho.

## Como rodar
1. Crie um arquivo `.env` na raiz com `DB_URL`, `DB_USER` e `DB_PASSWORD` (o arquivo não vai para o Git).
2. Para criar as tabelas, rode o script `Frigus_SQL.sql` no banco.
3. Gere o `.war` com `./mvnw clean package` e coloque em um Tomcat 10.1, ou use o `Dockerfile`.

No Render (ou outro serviço), as três variáveis do passo 1 são configuradas no próprio serviço.

## Organização dos arquivos
- `src/main/java/com/example/servlet`: `Controller` (servlets), `Dao`, `Model`, `Filter` e `Util`.
- `src/main/webapp/WEB-INF/pages`: páginas JSP do painel, do acesso e dos erros.
- `src/main/webapp/assets`: `css`, `js`, `img`, `icones` e `animacoes`.
