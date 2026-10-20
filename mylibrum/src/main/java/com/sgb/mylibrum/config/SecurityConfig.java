package com.sgb.mylibrum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sgb.mylibrum.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String BIBLIOTECARIA = "BIBLIOTECARIA";
    private static final String ASSISTENTE = "ASSISTENTE";
    private static final String USUARIO = "USUARIO";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, ex) ->
                                escreverErro(response, HttpStatus.UNAUTHORIZED, "Autenticação necessária"))
                        .accessDeniedHandler((request, response, ex) ->
                                escreverErro(response, HttpStatus.FORBIDDEN, "Acesso negado")))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/error").permitAll()

                        // Configurações e filiais
                        .requestMatchers("/api/configuracoes/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/filiais/**").hasAnyRole(ADMIN, BIBLIOTECARIA, ASSISTENTE)
                        .requestMatchers("/api/filiais/**").hasRole(ADMIN)

                        // Tarefas (a regra de dono é aplicada no controller)
                        .requestMatchers("/api/tarefas/**").authenticated()

                        // Usuários (hierarquia e "próprio perfil" são aplicados no controller)
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasAnyRole(ADMIN, BIBLIOTECARIA, ASSISTENTE)
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").hasAnyRole(ADMIN, BIBLIOTECARIA)
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/*").hasAnyRole(ADMIN, BIBLIOTECARIA, USUARIO)
                        .requestMatchers("/api/usuarios/**").hasAnyRole(ADMIN, BIBLIOTECARIA)

                        // Empréstimos
                        .requestMatchers(HttpMethod.GET, "/api/emprestimos").hasAnyRole(ADMIN, BIBLIOTECARIA, ASSISTENTE)
                        .requestMatchers(HttpMethod.GET, "/api/emprestimos/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/emprestimos/**").hasAnyRole(ADMIN, BIBLIOTECARIA, ASSISTENTE)
                        .requestMatchers("/api/emprestimos/**").hasAnyRole(ADMIN, BIBLIOTECARIA)

                        // Multas
                        .requestMatchers(HttpMethod.GET, "/api/historico-multas").hasAnyRole(ADMIN, BIBLIOTECARIA, ASSISTENTE)
                        .requestMatchers(HttpMethod.GET, "/api/historico-multas/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/historico-multas/*/pagamento").hasAnyRole(ADMIN, BIBLIOTECARIA, ASSISTENTE)
                        .requestMatchers("/api/historico-multas/**").hasAnyRole(ADMIN, BIBLIOTECARIA)

                        // Acervo: estantes só para a equipe; demais cadastros legíveis por todos
                        .requestMatchers(HttpMethod.GET, "/api/estantes/**").hasAnyRole(ADMIN, BIBLIOTECARIA, ASSISTENTE)
                        .requestMatchers(HttpMethod.GET, "/api/materiais/**", "/api/exemplares/**",
                                "/api/autores/**", "/api/editoras/**", "/api/generos/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/materiais/**", "/api/exemplares/**",
                                "/api/autores/**", "/api/editoras/**", "/api/generos/**", "/api/estantes/**")
                                .hasAnyRole(ADMIN, BIBLIOTECARIA)
                        .requestMatchers("/api/materiais/**", "/api/exemplares/**", "/api/autores/**",
                                "/api/editoras/**", "/api/generos/**", "/api/estantes/**")
                                .hasAnyRole(ADMIN, BIBLIOTECARIA, ASSISTENTE)

                        .anyRequest().denyAll())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void escreverErro(HttpServletResponse response, HttpStatus status, String mensagem)
            throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\":\"" + mensagem + "\"}");
    }
}
