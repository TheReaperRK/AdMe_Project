package cat.copernic.mavenproject1.logic;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class EmailLogic {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")  // Carga el correo del remitente desde application.properties
    private String fromEmail;

    public void sendPasswordResetEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Recuperación de contraseña");

            // Cuerpo del correo con HTML, usando "cid:logoImage" para referenciar la imagen adjunta
            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: auto; background: white; padding: 20px; border-radius: 10px; text-align: center;'>" +
                    "<img src='cid:logoImage' alt='Logo de AdMe' style='width: 100px; margin-bottom: 20px;'>" +
                    "<h2 style='color: #FFA500;'>Recuperación de Contraseña</h2>" +
                    "<p style='color: #333;'>Has solicitado restablecer tu contraseña. Usa el siguiente código:</p>" +
                    "<h3 style='color: #FFA500; font-size: 22px;'>" + token + "</h3>" +
                    "<p style='color: #555;'>Si no solicitaste este cambio, ignora este correo.</p>" +
                    "</div>" +
                    "</body></html>";

            helper.setText(htmlContent, true);  // Indicamos que es HTML

            // Adjuntar la imagen desde la carpeta resources
            ClassPathResource logo = new ClassPathResource("/images/ic_logo.png");
            helper.addInline("logoImage", logo);

            mailSender.send(message);
            System.out.println("Correo enviado con éxito");

        } catch (MessagingException e) {
            System.out.println("Error al enviar correo: " + e.getMessage());
        }
    }
}
