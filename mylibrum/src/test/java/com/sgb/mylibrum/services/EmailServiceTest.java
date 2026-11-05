package com.sgb.mylibrum.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.repositories.ConfiguracaoRepository;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private ConfiguracaoRepository repository;

    @Test
    void deveFalharQuandoEmailSmtpNaoEstaConfigurado() {
        when(repository.findByFilialIdAndChave(1L, ConfiguracaoChaves.SMTP_EMAIL)).thenReturn(Optional.empty());

        EmailService service = new EmailService(repository);

        assertThrows(IllegalStateException.class, () -> service.enviar(1L, "a@b.com", "Assunto", "Texto"));
    }

    @Test
    void deveUsarCredenciaisDasConfiguracoesDaFilial() {
        JavaMailSenderImpl sender = mock(JavaMailSenderImpl.class);
        when(repository.findByFilialIdAndChave(1L, ConfiguracaoChaves.SMTP_EMAIL))
                .thenReturn(Optional.of(config("smtp.email", "biblioteca@exemplo.com")));
        when(repository.findByFilialIdAndChave(1L, ConfiguracaoChaves.SMTP_SENHA))
                .thenReturn(Optional.of(config("smtp.senha", "segredo")));
        when(repository.findByFilialIdAndChave(1L, ConfiguracaoChaves.SMTP_HOST))
                .thenReturn(Optional.of(config("smtp.host", "smtp.exemplo.com")));
        when(repository.findByFilialIdAndChave(1L, ConfiguracaoChaves.SMTP_PORTA))
                .thenReturn(Optional.of(config("smtp.porta", "2525")));

        EmailService service = new EmailService(repository) {
            @Override
            JavaMailSenderImpl criarSender(String host, int porta, String usuario, String senha) {
                assertEquals("smtp.exemplo.com", host);
                assertEquals(2525, porta);
                assertEquals("biblioteca@exemplo.com", usuario);
                assertEquals("segredo", senha);
                return sender;
            }
        };

        service.enviar(1L, "leitor@exemplo.com", "Assunto", "Texto");

        ArgumentCaptor<SimpleMailMessage> mensagem = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(mensagem.capture());
        assertEquals("biblioteca@exemplo.com", mensagem.getValue().getFrom());
        assertEquals("leitor@exemplo.com", mensagem.getValue().getTo()[0]);
        assertEquals("Assunto", mensagem.getValue().getSubject());
    }

    @Test
    void deveConfigurarStartTlsNaPortaPadraoESslNa465() {
        EmailService service = new EmailService(repository);

        assertTrue(service.criarSender("h", 587, "u", "s").getJavaMailProperties()
                .containsKey("mail.smtp.starttls.enable"));
        assertTrue(service.criarSender("h", 465, "u", "s").getJavaMailProperties()
                .containsKey("mail.smtp.ssl.enable"));
    }

    private Configuracao config(String chave, String valor) {
        Configuracao c = new Configuracao();
        c.setChave(chave);
        c.setValor(valor);
        return c;
    }
}
