package utils;

import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 发送邮箱验证码
 */
@Slf4j
public class MailCodeUtils {

    public static void sendMail(String goal, String code) {
        // 收件人电子邮箱
        String to = goal;

        // 发件人电子邮箱
        String from = "827626824@qq.com";

        // 指定发送邮件的主机为 qq邮件服务器
        String host = "smtp.qq.com";

        // 获取系统属性
        Properties properties = System.getProperties();

        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");

        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("827626824@qq.com", "tofdtrbsnmqdbbaj");
            }
        });

        try {
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(from));

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: 头部头字段
            message.setSubject("【验证码】");

            // 设置消息体
            message.setText(code);

            // 发送消息
            Transport.send(message);
            log.info("{} send to {} with {}", from, to, code);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
