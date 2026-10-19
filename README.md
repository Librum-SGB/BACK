
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
- O endpoint `/auth/login` e as rotas públicas do Swagger ficam liberadas sem autenticação.
- As credenciais abaixo são somente para desenvolvimento e são inseridas pela migration V3 em um banco novo.

### Usuários de teste por função

Todas estas contas usam a senha `senha123`.

| E-mail | Função |
| --- | --- |
| `admin@gmail.com` | `ADMIN` |
| `bibliotecaria@gmail.com` | `BIBLIOTECARIA` |
| `assistente@gmail.com` | `ASSISTENTE` |
| `usuario@gmail.com` | `USUARIO` |