
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

### Usuários de teste por função

Todas estas contas usam a senha `senha123`.

| E-mail | Função |
| --- | --- |
| `admin@gmail.com` | `ADMIN` |
| `bibliotecaria@gmail.com` | `BIBLIOTECARIA` |
| `assistente@gmail.com` | `ASSISTENTE` |
| `usuario@gmail.com` | `USUARIO` |