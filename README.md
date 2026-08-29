# BACK

## Swagger

Documentação dos endpoints: http://localhost:8080/swagger-ui/index.html#/

A interface do Swagger agora está configurada para autenticação Bearer JWT. Para testar endpoints protegidos:

1. Inicie a aplicação com o comando:
   ```bash
   ./mvnw spring-boot:run
   ```
2. Acesse a documentação em: http://localhost:8080/swagger-ui/index.html
3. No topo da página, clique em "Authorize".
4. No campo de valor, insira o token no formato:
   ```text
   Bearer <seu-token-jwt>
   ```
5. Em seguida, qualquer endpoint protegido pode ser testado diretamente pela UI do Swagger.

## Como gerar o token JWT

Use o endpoint de autenticação da API:

- Método: POST
- URL: http://localhost:8080/auth/login
- Content-Type: application/json

Exemplo de payload:
```json
{
  "login": "admin",
  "senha": "admin123"
}
```

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
- Se o banco estiver vazio, será necessário criar um gestor válido antes de autenticar. O usuário padrão de teste é:
  ```json
  {
    "login": "admin",
    "senha": "admin123"
  }
  ```

## Testando no Postman

1. Faça a requisição POST para `{{baseUrl}}/auth/login` com o JSON acima.
2. O retorno inclui o campo `token`.
3. Salve esse token na variável `token` da collection.
4. Nos endpoints protegidos, use o cabeçalho:
   ```http
   Authorization: Bearer {{token}}
   ```