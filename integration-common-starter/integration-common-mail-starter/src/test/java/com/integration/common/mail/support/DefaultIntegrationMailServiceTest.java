package com.integration.common.mail.support;

import com.integration.common.mail.MailAttachment;
import com.integration.common.mail.autoconfigure.MailIntegrationProperties;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultIntegrationMailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    private final MailIntegrationProperties integrationProperties = new MailIntegrationProperties();
    private final MailProperties mailProperties = new MailProperties();

    @BeforeEach
    void stubMimeMessage() {
        Session session = Session.getInstance(new Properties());
        lenient().when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage(session));
    }

    @Test
    void sendText_usesSpringMailUsernameAsFromWhenEmail() throws Exception {
        mailProperties.setUsername("noreply@example.com");
        DefaultIntegrationMailService service =
                new DefaultIntegrationMailService(javaMailSender, integrationProperties, mailProperties);

        service.sendText("user@example.com", "Hello", "plain body");

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender).send(captor.capture());
        MimeMessage sent = captor.getValue();
        assertThat(sent.getSubject()).isEqualTo("Hello");
        assertThat(sent.getAllRecipients()[0].toString()).contains("user@example.com");
    }

    @Test
    void sendText_prefersIntegrationFromAddress() throws Exception {
        mailProperties.setUsername("ignored@example.com");
        integrationProperties.setFromAddress("real-sender@example.com");
        DefaultIntegrationMailService service =
                new DefaultIntegrationMailService(javaMailSender, integrationProperties, mailProperties);

        service.sendText("user@example.com", "S", "B");

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender).send(captor.capture());
        assertThat(captor.getValue().getFrom()[0].toString()).contains("real-sender@example.com");
    }

    @Test
    void sendText_withAttachments_usesMultipart() throws Exception {
        mailProperties.setUsername("noreply@example.com");
        DefaultIntegrationMailService service =
                new DefaultIntegrationMailService(javaMailSender, integrationProperties, mailProperties);
        List<MailAttachment> attachments = List.of(
                new MailAttachment("note.txt", "hello".getBytes(StandardCharsets.UTF_8), "text/plain"));

        service.sendText("user@example.com", "Sub", "Body", attachments);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender).send(captor.capture());
        assertThat(captor.getValue().getContent()).isInstanceOf(MimeMultipart.class);
        MimeMultipart multipart = (MimeMultipart) captor.getValue().getContent();
        assertThat(multipart.getCount()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void resolveFrom_throwsWhenNoSenderConfigured() {
        DefaultIntegrationMailService service =
                new DefaultIntegrationMailService(javaMailSender, integrationProperties, mailProperties);

        assertThatThrownBy(() -> service.sendText("u@e.com", "S", "B"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No sender address");
    }

    @Test
    void sendText_rejectsEmptyRecipients() {
        mailProperties.setUsername("a@b.com");
        DefaultIntegrationMailService service =
                new DefaultIntegrationMailService(javaMailSender, integrationProperties, mailProperties);

        assertThatThrownBy(() -> service.sendText(java.util.List.of(), "S", "B"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
