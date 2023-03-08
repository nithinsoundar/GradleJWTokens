package com.nithin.gradlejwttokens.AuthenticationService.MailConfig;


import com.nithin.gradlejwttokens.AuthenticationService.Model.User;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
public class MailConfiguration {

    private final FileUtils fileUtils;
    private final String FROM;

    private final String HOST;

    private final String PORT;

    private final String PASSWORD;

    @Autowired
    public MailConfiguration(FileUtils fileUtils, @Value("${spring.mail.username}") String from,
                             @Value("${spring.mail.host}") String host,
                             @Value("${spring.mail.port}") String port,
                             @Value("${spring.mail.password}") String password) {
        this.fileUtils = fileUtils;
        this.FROM = from;
        this.HOST = host;
        this.PORT = port;
        this.PASSWORD = password;
    }

    public void sendEmail(String toAddress, String subject, String message) throws MessagingException {

        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.starttls.required", "true");

        Session session = Session.getDefaultInstance(props);

        // Create a message with the specified information.
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
        msg.setSubject(subject, "UTF-8");
        msg.setText(message, "UTF-8");

        // Create a transport.
        Transport transport = session.getTransport();
        try {
            log.info("Sending Email now...standby...");
            // Connect to OCI Email Delivery using the SMTP credentials specified.
            transport.connect(HOST, FROM, PASSWORD);
            // Send email.
            transport.sendMessage(msg, msg.getAllRecipients());
            log.info("Email sent!");
            System.out.println("sent from " + FROM +
                    ", to " + toAddress +
                    "; server = " + HOST + ", port = " + PORT);
        } catch (Exception ex) {
            //Store mail details with error message
            log.warn("The email was not sent.");
            log.error("Error message: " + ex.getMessage());
        } finally {
            // Close & terminate the connection.
            transport.close();

        }
    }


    public boolean isValidEmail(String email) {
        String regex = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public void emailPdf(List<User> users, String toAddress, String subject, String text) throws Exception {

        Properties properties = new Properties();
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.starttls.required", "true");
        Session session = Session.getDefaultInstance(properties, null);

        MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setText(text);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        fileUtils.generatePdf(users, outputStream);
        byte[] bytes = outputStream.toByteArray();

        //construct the pdf body part
        DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");
        MimeBodyPart pdfBodyPart = new MimeBodyPart();
        pdfBodyPart.setDataHandler(new DataHandler(dataSource));
        pdfBodyPart.setFileName("user.pdf");

        //construct the mime multi part
        MimeMultipart mimeMultipart = new MimeMultipart();
        mimeMultipart.addBodyPart(textBodyPart);
        mimeMultipart.addBodyPart(pdfBodyPart);

        //create the sender/recipient addresses
        InternetAddress iaSender = new InternetAddress(FROM);
        InternetAddress iaRecipient = new InternetAddress(toAddress);

        //construct the mime message
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setSender(iaSender);
        mimeMessage.setSubject(subject);
        mimeMessage.setRecipient(Message.RecipientType.TO, iaRecipient);
        mimeMessage.setContent(mimeMultipart);

        Transport transport = session.getTransport();
        try {

            //send off the email
            log.info("Sending Email now...standby...");
            // Connect to OCI Email Delivery using the SMTP credentials specified.
            transport.connect(HOST, FROM, PASSWORD);
            // Send email.
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            log.info("Email sent!");
            System.out.println("sent from " + FROM +
                    ", to " + toAddress +
                    "; server = " + HOST + ", port = " + PORT);
        } catch(Exception ex) {
            log.warn("The email was not sent.");
            log.error("Error message: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            //clean off
            transport.close();
        }
    }

    public void emailPdfToAllAdmins(List<User> users, String[] admin, String subject, String text) throws Exception {

        Properties properties1 = new Properties();
        properties1.put("mail.smtp.host", HOST);
        properties1.put("mail.smtp.port", PORT);
        properties1.put("mail.smtp.auth", "true");
        properties1.put("mail.smtp.starttls.enable", "true");
        properties1.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties1.put("mail.smtp.starttls.required", "true");
        Session session = Session.getDefaultInstance(properties1, null);

        MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setText(text);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        fileUtils.generatePdf(users, outputStream);
        byte[] bytes = outputStream.toByteArray();

        //construct the pdf body part
        DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");
        MimeBodyPart pdfBodyPart = new MimeBodyPart();
        pdfBodyPart.setDataHandler(new DataHandler(dataSource));
        pdfBodyPart.setFileName("user.pdf");

        //construct the mime multi part
        MimeMultipart mimeMultipart = new MimeMultipart();
        mimeMultipart.addBodyPart(textBodyPart);
        mimeMultipart.addBodyPart(pdfBodyPart);

        //create the sender/recipient addresses
        InternetAddress iaSender = new InternetAddress(FROM);

        Address[] toAddresses = new Address[admin.length];
        for (int i = 0; i < admin.length; i++) {
            toAddresses[i] = new InternetAddress(admin[i]);
        }

        //construct the mime message
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setSender(iaSender);
        mimeMessage.setSubject(subject);
        mimeMessage.setRecipients(Message.RecipientType.TO, toAddresses );
        mimeMessage.setContent(mimeMultipart);

        Transport transport = session.getTransport();
        try {

            //send off the email
            log.info("Sending Email now...standby...");
            // Connect to OCI Email Delivery using the SMTP credentials specified.
            transport.connect(HOST, FROM, PASSWORD);
            // Send email.
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            log.info("Email sent!");
            System.out.println("sent from " + FROM +
                    ", to " + toAddresses +
                    "; server = " + HOST + ", port = " + PORT);
        } catch(Exception ex) {
            log.warn("The email was not sent.");
            log.error("Error message: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            //clean off
            transport.close();

            if(null != outputStream) {
                try { outputStream.close(); outputStream = null; }
                catch(Exception ex) { }
            }
        }
    }

    public void emailCsv( List<User> users, String toAddress, String subject, String text) throws Exception {

        FileWriter fileWriter = new FileWriter("users.csv");
        StatefulBeanToCsv<User> beanToCsv = new StatefulBeanToCsvBuilder<User>(fileWriter).build();
        beanToCsv.write(users);
        fileWriter.close();


        // Create a Multipart object
        Multipart multipart = new MimeMultipart();

        // Create a MimeBodyPart object for the CSV file attachment
        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource source = new FileDataSource("users.csv");
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName("users.csv");

        // Add the attachment to the Multipart object
        multipart.addBodyPart(attachmentPart);

        Properties properties1 = new Properties();
        properties1.put("mail.smtp.host", HOST);
        properties1.put("mail.smtp.port", PORT);
        properties1.put("mail.smtp.auth", "true");
        properties1.put("mail.smtp.starttls.enable", "true");
        properties1.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties1.put("mail.smtp.starttls.required", "true");
        Session session = Session.getDefaultInstance(properties1, null);

        MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setText(text);

        InternetAddress iaSender1 = new InternetAddress(FROM);
        InternetAddress iaRecipient1 = new InternetAddress(toAddress);

        //construct the mime message
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setSender(iaSender1);
        mimeMessage.setSubject(subject);
        mimeMessage.setRecipient(Message.RecipientType.TO, iaRecipient1);
        mimeMessage.setContent(multipart);

        Transport transport1 = session.getTransport();
        try {

            //send off the email
            log.info("Sending Email now...standby...");
            // Connect to OCI Email Delivery using the SMTP credentials specified.
            transport1.connect(HOST, FROM, PASSWORD);
            // Send email.
            transport1.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            log.info("Email sent!");
            System.out.println("sent from " + FROM +
                    ", to " + toAddress +
                    "; server = " + HOST + ", port = " + PORT);
        } catch(Exception ex) {
            log.warn("The email was not sent.");
            log.error("Error message: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            //clean off
            transport1.close();
        }
    }

    public void emailCsvToAllAdmins( List<User> users, String[] admin, String subject, String text) throws Exception {

        FileWriter fileWriter = new FileWriter("users.csv");
        StatefulBeanToCsv<User> beanToCsv = new StatefulBeanToCsvBuilder<User>(fileWriter).build();
        beanToCsv.write(users);
        fileWriter.close();


        // Create a Multipart object
        Multipart multipart = new MimeMultipart();

        // Create a MimeBodyPart object for the CSV file attachment
        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource source = new FileDataSource("users.csv");
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName("users.csv");

        // Add the attachment to the Multipart object
        multipart.addBodyPart(attachmentPart);

        Properties properties1 = new Properties();
        properties1.put("mail.smtp.host", HOST);
        properties1.put("mail.smtp.port", PORT);
        properties1.put("mail.smtp.auth", "true");
        properties1.put("mail.smtp.starttls.enable", "true");
        properties1.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties1.put("mail.smtp.starttls.required", "true");
        Session session = Session.getDefaultInstance(properties1, null);

        MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setText(text);

        InternetAddress iaSender1 = new InternetAddress(FROM);
        Address[] toAddresses = new Address[admin.length];
        for (int i = 0; i < admin.length; i++) {
            toAddresses[i] = new InternetAddress(admin[i]);
        }

        //construct the mime message
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setSender(iaSender1);
        mimeMessage.setSubject(subject);
        mimeMessage.setRecipients(Message.RecipientType.TO, toAddresses);
        mimeMessage.setContent(multipart);

        Transport transport1 = session.getTransport();
        try {

            //send off the email
            log.info("Sending Email now...standby...");
            // Connect to OCI Email Delivery using the SMTP credentials specified.
            transport1.connect(HOST, FROM, PASSWORD);
            // Send email.
            transport1.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            log.info("Email sent!");
            System.out.println("sent from " + FROM +
                    ", to " + toAddresses +
                    "; server = " + HOST + ", port = " + PORT);
        } catch(Exception ex) {
            log.warn("The email was not sent.");
            log.error("Error message: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            //clean off
            transport1.close();
        }
    }
}




