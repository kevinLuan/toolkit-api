package cn.feiliu.taskflow.toolkit.mail;

import java.util.List;

public class AutoReply {

    private static final String SMTP_SERVER = "smtp.126.com";
    private static final String IMAP_SERVER = "imap.126.com";

    private static final String REPLY_BODY = "您好，您的邮件已收到，我们会在24小时内处理。感谢您的理解！";
    public static void main(String[] args) throws Exception {
        String email = System.getenv("EMAIL");
        String password = System.getenv("PASSWORD");
        EmailService emailService = EmailService.of(email,password);
        List<EmailService.EmailInfo> emailInfos = emailService.getUnreadEmail();
        for (EmailService.EmailInfo emailInfo : emailInfos) {
            System.out.println(emailInfo);
            emailService.replyMail(emailInfo.getEmailId(),REPLY_BODY);
        }
    }
}
