package com.sgb.mylibrum.services;

import java.util.Optional;
import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.repositories.ConfiguracaoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Envia e-mails usando o servidor SMTP definido nas configurações do sistema da filial
 * (chaves smtp.email, smtp.senha e, opcionalmente, smtp.host e smtp.porta).
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String HOST_PADRAO = "smtp.gmail.com";
    private static final int PORTA_PADRAO = 587;
    private static final int TIMEOUT_MS = 10_000;

    private final ConfiguracaoRepository configuracaoRepository;

    public void enviar(Long filialId, String destino, String assunto, String texto) {
        String email = valor(filialId, ConfiguracaoChaves.SMTP_EMAIL)
                .orElseThrow(() -> new IllegalStateException(
                        "Configuração '" + ConfiguracaoChaves.SMTP_EMAIL + "' não encontrada para a filial " + filialId));
        String senha = valor(filialId, ConfiguracaoChaves.SMTP_SENHA)
                .orElseThrow(() -> new IllegalStateException(
                        "Configuração '" + ConfiguracaoChaves.SMTP_SENHA + "' não encontrada para a filial " + filialId));
        String host = valor(filialId, ConfiguracaoChaves.SMTP_HOST).orElse(HOST_PADRAO);
        int porta = valor(filialId, ConfiguracaoChaves.SMTP_PORTA).map(this::lerPorta).orElse(PORTA_PADRAO);

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(email);
        mensagem.setTo(destino);
        mensagem.setSubject(assunto);
        mensagem.setText(texto);

        criarSender(host, porta, email, senha).send(mensagem);
    }

    JavaMailSenderImpl criarSender(String host, int porta, String usuario, String senha) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(porta);
        sender.setUsername(usuario);
        sender.setPassword(senha);
        sender.setDefaultEncoding("UTF-8");

        Properties props = sender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        if (porta == 465) {
            props.put("mail.smtp.ssl.enable", "true");
        } else {
            props.put("mail.smtp.starttls.enable", "true");
        }
        props.put("mail.smtp.connectiontimeout", String.valueOf(TIMEOUT_MS));
        props.put("mail.smtp.timeout", String.valueOf(TIMEOUT_MS));
        props.put("mail.smtp.writetimeout", String.valueOf(TIMEOUT_MS));
        return sender;
    }

    private Optional<String> valor(Long filialId, String chave) {
        return configuracaoRepository.findByFilialIdAndChave(filialId, chave)
                .filter(c -> !Boolean.FALSE.equals(c.getAtivo()))
                .map(Configuracao::getValor)
                .map(String::trim)
                .filter(v -> !v.isEmpty());
    }

    private int lerPorta(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Configuração '" + ConfiguracaoChaves.SMTP_PORTA + "' inválida: " + valor);
        }
    }
}
