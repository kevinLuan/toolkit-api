package cn.feiliu.taskflow.toolkit.mail;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import lombok.Data;
import lombok.SneakyThrows;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-11-11
 */
public class EmailService {
    String email;
    String password;

    public EmailService(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static EmailService of(String email, String password) {
        return new EmailService(email, password);
    }

    public List<EmailInfo> getUnreadEmail() throws MessagingException, IOException {
        Properties prop = System.getProperties();
        prop.put("mail.store.protocol", "imap");
        prop.put("mail.imap.host", "imap.126.com");
        prop.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(prop);
        // 使用imap会话机制，连接服务器
        IMAPStore store = (IMAPStore) session.getStore("imap");
        store.connect(email, password);
        store.id(getIAM());

        IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX"); // 收件箱
        folder.open(Folder.READ_ONLY);

        Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        List<EmailInfo> result = new ArrayList<>();
        for (Message message : messages) {
            EmailInfo emailInfo = getEmailInfo(message, folder);
            result.add(emailInfo);
        }
        folder.close(false);
        store.close();
        return result;
    }

    private EmailInfo getEmailInfo(Message message, IMAPFolder folder) throws MessagingException, UnsupportedEncodingException {
        InternetAddress address = (InternetAddress) message.getFrom()[0];
        EmailContent content = parseOriginalMailContent(message);
        EmailInfo emailInfo = new EmailInfo();
        emailInfo.setSubject(message.getSubject());
        emailInfo.setEmailId(folder.getUID(message));
        emailInfo.setFromAddress(address.getAddress());
        emailInfo.setAllRecipients(getAllRecipients(message));
        emailInfo.setContent(content);
        emailInfo.setSentDate(message.getSentDate());
        return emailInfo;
    }

    private String getAllRecipients(Message message) throws MessagingException, UnsupportedEncodingException {
        List<String> addresses = new ArrayList<>();
        for (Address address : message.getAllRecipients()) {
            String decodedAddress = MimeUtility.decodeText(address.toString());
            addresses.add(decodedAddress);
        }
        return String.join(",", addresses);
    }
    @Data
    static class EmailInfo {
        Long emailId;
        String subject;
        private String fromAddress;
        private EmailContent content;
        private String allRecipients;
        private Date sentDate;

        public String getSendDateTime(){
            return new Timestamp(sentDate.getTime()).toString();
        }
    }

    @SneakyThrows
    private EmailContent parseOriginalMailContent(Message message) {
        EmailContent content = new EmailContent();
        if (message.isMimeType("text/plain")) {
            content.setTextContent(message.getContent().toString());
        } else if (message.isMimeType("text/html")) {
            content.setHtmlContent(message.getContent().toString());
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    content.setTextContent(bodyPart.getContent().toString());
                } else if (bodyPart.isMimeType("text/html")) {
                    content.setHtmlContent(bodyPart.getContent().toString());
                }
            }
        }
        return content;
    }

    @Data
    public static class EmailContent {
        private String textContent;
        private String htmlContent;

        String getContext() {
            return htmlContent != null ? htmlContent : textContent;
        }
    }

    private Map<String,String>getIAM(){
        HashMap IAM = new HashMap();
        IAM.put("name", "Peblla");
        IAM.put("version", "1.0.1");
        IAM.put("vendor", "myclient.kevin");
        IAM.put("support-email", "hello@test.com");
        return IAM;
    }

    public void replyMail(Long messageUid, String mailContent) throws MessagingException, UnsupportedEncodingException {
        Session session = Session.getInstance(getSmtpProperties());
        // 使用imap会话机制，连接服务器
        IMAPStore store = (IMAPStore) session.getStore("imap");
        store.connect("smtp.126.com", email, password);
        store.id(getIAM());

        IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX"); // 收件箱
        folder.open(Folder.READ_ONLY);
//        folder.open(Folder.READ_WRITE); 使用该方式后，网易126邮箱对未读邮箱只能读取一次。
        Message originalMessage = folder.getMessageByUID(messageUid);
        EmailInfo emailInfo = getEmailInfo(originalMessage, folder);
        Message replyMessage = originalMessage.reply(false);
        replyMessage.setFrom(new InternetAddress(email));
        replyMessage.setContent(mailContent + "<br/><br/>" +
                "<html>\n" +
                "<head></head>\n" +
                "<body>\n" +
                "<div class=\"history-quote-wrapper\" id=\"lark-mail-quote-"+emailInfo.getEmailId()+"\">\n" +
                "    <div data-html-block=\"quote\" data-mail-html-ignore=\"\">\n" +
                "        <div style=\"border-left: none; padding-left: 0px;\" class=\"adit-html-block adit-html-block--collapsed\">\n" +
                "            <div>\n" +
                "                <div class=\"adit-html-block__attr history-quote-meta-wrapper history-quote-gap-tag\"\n" +
                "                     id=\"lark-mail-meta-l0sUAjusK\"\n" +
                "                     style=\"padding: 12px; background: rgb(245, 246, 247); color: rgb(31, 35, 41); border-radius: 4px; margin-top: 24px; margin-bottom: 12px;\">\n" +
                "                    <div style=\"word-break: break-word;\">\n" +
                "                        <div style=\"\" class=\"lme-line-signal\"><span style=\"white-space:nowrap;\">发件人：</span> <a\n" +
                "                                data-mailto=\"mailto:"+emailInfo.getFromAddress()+"\" class=\"quote-head-meta-mailto\"\n" +
                "                                style=\"overflow-wrap: break-word; white-space: pre-wrap; hyphens: none; word-break: break-word; cursor: pointer; text-decoration: none; color: inherit;\"\n" +
                "                                href=\"mailto:"+emailInfo.getFromAddress()+"\">"+emailInfo.getFromAddress()+"</a></div>\n" +
                "                        <div style=\"\" class=\"lme-line-signal\"><span style=\"white-space:nowrap;\">时间：</span> "+emailInfo.getSendDateTime()+
                "                        </div>\n" +
                "                        <div style=\"\" class=\"lme-line-signal\"><span style=\"white-space:nowrap;\">主题：</span> Re:\n" +
                emailInfo.getSubject() +
                "                        </div>\n" +
                "                        <div style=\"\" class=\"lme-line-signal\"><span style=\"white-space:nowrap;\">收件人：</span> <span\n" +
                "                                style=\"white-space: nowrap;\">" + emailInfo.getAllRecipients() + "</span></div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class=\"lme-line-signal\">" +
                "                    <div style=\"word-break:break-word\" id=\"editor_version_7.29.0_z95BGvcC\">\n" +
                "                        <div style=\"margin-top:4px;margin-bottom:4px;line-height:1.6\">\n" +
                "                            <div style=\"font-size:14px\" dir=\"auto\" class=\"lme-line-signal\">" + emailInfo.getContent().getHtmlContent() + "</div>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>" +
                "", "text/html; charset=utf-8");
        Transport transport = session.getTransport("smtp");
        transport.connect("smtp.126.com", email, password);
        transport.sendMessage(replyMessage, replyMessage.getAllRecipients());
        store.close();
    }

    private Properties getSmtpProperties() {
        Properties smtpProperties = new Properties();
        smtpProperties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        smtpProperties.put("mail.smtp.host", "smtp.126.com");
        smtpProperties.put("mail.smtp.port", "465");
        smtpProperties.put("mail.smtp.auth", "true");
        smtpProperties.put("mail.smtp.starttls.enable", "true");
        smtpProperties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return smtpProperties;
    }

}
