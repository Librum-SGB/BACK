
MANTENHA O DOCKER ABERTO!!

## Execução do sistema

Para apagar banco persistido e recriá-lo do zero:

```powershell
docker compose down -v
```

Para iniciar o ambiente, na pasta \BACK\mylibrum

```powershell
docker compose up -d --build
```

## Swagger

Documentação dos endpoints: http://localhost:8080/swagger-ui/index.html#/

A interface do Swagger agora está configurada para autenticação Bearer JWT. Para testar endpoints protegidos:

## Como gerar o token JWT

Use o endpoint de autenticação da API:

- Método: POST
- URL: http://localhost:8080/auth/login
- Content-Type: application/json

Resposta esperada:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "message": "Login realizado com sucesso"
}
```
Copie o valor do campo `token` e cole no Swagger no botão "Authorize" com o prefixo `Bearer `.

## Observações

- Os endpoints protegidos ficam em `/api/**`.
- O endpoint `/auth/login`, os de recuperação de senha (`/auth/esqueci-senha` e `/auth/redefinir-senha`) e as rotas públicas do Swagger ficam liberados sem autenticação.
- As credenciais abaixo são somente para desenvolvimento e são inseridas pela migration V3 em um banco novo.

### Recuperação de senha por e-mail

O envio usa o servidor SMTP definido nas **configurações do sistema** (`/api/configuracoes`) da filial do usuário, sem variáveis de ambiente. Cadastre estas chaves para a filial:

| Chave | Obrigatória | Descrição |
| --- | --- | --- |
| `smtp.email` | Sim | E-mail remetente, também usado como usuário do SMTP |
| `smtp.senha` | Sim | Senha do e-mail (no Gmail, use uma "senha de app"). É mascarada (`********`) nas respostas da API |
| `smtp.host` | Não | Servidor SMTP (padrão `smtp.gmail.com`) |
| `smtp.porta` | Não | Porta SMTP (padrão `587`, STARTTLS; `465` usa SSL) |

Fluxo:

1. `POST /auth/esqueci-senha` com `{"email": "..."}`: sempre responde 200, sem revelar se o e-mail existe. O link enviado aponta para `{app.frontend-url}/redefinir-senha?token=...` (`app.frontend-url` pode ser definido por `APP_FRONTEND_URL`, padrão `http://localhost:4200`).
2. `POST /auth/redefinir-senha` com `{"token": "...", "novaSenha": "..."}`: o token vale por 30 minutos, é de uso único e só seu hash fica no banco. Token inválido, expirado ou já usado retorna 400.
3. Há um intervalo mínimo de 1 minuto entre solicitações para o mesmo usuário; uma nova solicitação invalida o token anterior.

### Permissões por função

As regras são aplicadas por rota em `SecurityConfig` (403 em JSON quando negado, 401 sem token) e, para regras que dependem do registro, no controller via `AutorizacaoService`. Rotas não listadas são negadas.

| Recurso | Admin | Bibliotecária | Assistente | Usuário geral |
| --- | --- | --- | --- | --- |
| Materiais, exemplares, autores, editoras, gêneros: leitura | ✅ | ✅ | ✅ | ✅ |
| Mesmos recursos: criar/editar | ✅ | ✅ | ✅ | ❌ |
| Mesmos recursos: excluir | ✅ | ✅ | ❌ | ❌ |
| Estantes | ✅ | ✅ | criar/editar/ler | ❌ |
| Empréstimos: consultar | ✅ | ✅ | ✅ | só os próprios |
| Empréstimos: criar, devolver, renovar | ✅ | ✅ | ✅ | ❌ |
| Empréstimos: editar/excluir | ✅ | ✅ | ❌ | ❌ |
| Multas: consultar | ✅ | ✅ | ✅ | só as próprias |
| Multas: registrar pagamento | ✅ | ✅ | ✅ | ❌ |
| Multas: criar/editar/excluir | ✅ | ✅ | ❌ | ❌ |
| Usuários: listar/consultar | ✅ | ✅ | ✅ | só o próprio |
| Usuários: criar/excluir/bloquear | ✅ | só assistentes e usuários gerais | ❌ | ❌ |
| Usuários: editar | ✅ | assistentes, usuários gerais e o próprio registro (sem elevar função) | ❌ | só o próprio (sem alterar função, bloqueio, filial, limite ou matrícula) |
| Filiais | ✅ | leitura | leitura | ❌ |
| Configurações | ✅ | ❌ | ❌ | ❌ |
| Tarefas (CRUD das próprias) | ✅ | ✅ | ✅ | ✅ |

Tarefas: a tarefa sempre pertence ao usuário logado; só o dono pode editar ou excluir. O Usuário geral lista e consulta apenas as próprias; a equipe consulta as de qualquer usuário. Dashboard e relatórios ainda não possuem endpoints.

### Usuários de teste por função

Todas estas contas usam a senha `senha123`.

| E-mail | Função |
| --- | --- |
| `admin@gmail.com` | `ADMIN` |
| `bibliotecaria@gmail.com` | `BIBLIOTECARIA` |
| `assistente@gmail.com` | `ASSISTENTE` |
| `usuario@gmail.com` | `USUARIO` |