package com.hocztms.utils;

import com.hocztms.entity.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.Message;


@Component
public class EamilUtils {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private JavaMailSenderImpl javaMailSenderImpl;


    private static String senderAdress = "1107578936@qq.com";

    private static String sendername = "二手交易市场";

    /*
    发送找回密码文件
     */
    public void sendGetPasswordEamil(Email email) {
        MimeMessagePreparator messagePreparator = message -> {
            message.setFrom(senderAdress);
            message.setRecipients(Message.RecipientType.TO, email.getAddress());
            message.setSubject(email.getTheme());
            message.setText("<html><body>"
                    + "你好：" + email.getReceiver()
                    + "<br>" + "你的密钥是：    " + email.getSecret()
                    + "<br>" + "记住10分钟有效"
                    + "<br>" + "from :" + sendername
                    + "</body></html>");
        };
        try {
            this.javaMailSender.send(messagePreparator);
        } catch (MailException e) {
            throw new RuntimeException("邮箱发送失败");
        }
    }
    /*
    发送注册邮箱文件
     */
    public void sendCodeEmail(Email email) {
        MimeMessagePreparator messagePreparator = message -> {
            message.setFrom(senderAdress);
            message.setRecipients(Message.RecipientType.TO, email.getAddress());
            message.setSubject(email.getTheme());
            message.setText("<html><body>"+"<br>"
                    +"您本次注册验证码为 "   +"<br>"
                    + email.getSecret()
                    + "<br>" + "from :" + sendername
                    + "</body></html>");
        };
        try {
            this.javaMailSender.send(messagePreparator);
        } catch (MailException e) {
            throw new RuntimeException("邮箱格式不正确");
        }
    }

    /*
    发送一般通知文件
     */
    public void sendEamil(Email email) {
        MimeMessagePreparator messagePreparator = message -> {
            message.setFrom(senderAdress);
            message.setRecipients(Message.RecipientType.TO, email.getAddress());
            message.setSubject(email.getTheme());
            message.setText("<html><body>"+"<br>"
                    +"您好: " + email.getReceiver() + "<br>"
                    + email.getMsg()
                    + "<br>" + "from :" + sendername
                    + "</body></html>");
        };
        try {
            this.javaMailSender.send(messagePreparator);
        } catch (MailException e) {
            throw new RuntimeException("邮箱格式不正确");
        }
    }
}
