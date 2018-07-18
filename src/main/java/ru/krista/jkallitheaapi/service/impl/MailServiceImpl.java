package ru.krista.jkallitheaapi.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import ru.krista.jkallitheaapi.beans.ProjectParams;
import ru.krista.jkallitheaapi.model.Comment;
import ru.krista.jkallitheaapi.model.PullRequest;
import ru.krista.jkallitheaapi.service.MailService;

import static ru.krista.jkallitheaapi.utils.CommonUtils.checkPath;

/**
 * Реализация сервиса для работы с отправкой писем.
 */
@Stateless
public class MailServiceImpl implements MailService {

    private static final String SUBJECT_MAIL_PATTERN = "[HG] [%s: Comment] %s pull request #%d from %s";
    private static final String SUBJECT_MAIL_WITHOUT_BRANCH_PATTERN = "[HG] [%s: Comment] %s pull request #%d";

    private static final String MAIL_RECEIVER_PERSONAL = "SonarQube";
    private static final String MAIL_CONTENT_TYPE = "text/html; charset=utf-8";

    @Resource(mappedName = "java:jboss/mail/kallitheaMail")
    private Session session;

    @Inject
    private ProjectParams projectParams;

    @Override
    public void processMail(PullRequest pullRequest, Comment comment, JSONObject data) throws MessagingException {
        JSONObject mailInfo = data.optJSONObject("mailInfo");
        if (mailInfo == null || pullRequest.getUser() == null || pullRequest.getOtherRepository() == null
                || StringUtils.isBlank(pullRequest.getUser().getEmail())) {
            return;
        }
        // формируем тему письма
        String branchName = "";
        if (StringUtils.isNotBlank(pullRequest.getOrgRef())) {
            String[] branchInfo = pullRequest.getOrgRef().split(":");
            branchName = branchInfo.length > 1 ? branchInfo[1] : "";
        }
        String subject;
        if (StringUtils.isBlank(branchName)) {
            subject = String.format(SUBJECT_MAIL_WITHOUT_BRANCH_PATTERN, mailInfo.optString("ruStatus", ""),
                    pullRequest.getOtherRepository(), pullRequest.getId());
        } else {
            subject = String.format(SUBJECT_MAIL_PATTERN, mailInfo.optString("ruStatus", ""),
                    pullRequest.getOtherRepository().getName(), pullRequest.getId(), branchName);
        }
        String body = mailInfo.optString("body", "");
        if (StringUtils.isNotBlank(body)) {
            body = body.replace("%commentId%", String.valueOf(comment.getId()))
                    .replace("%pullRequestRepo%", checkPath(pullRequest.getOtherRepository().getName()));
        }
        sendMail(pullRequest.getUser().getEmail(), subject, body);
    }

    @Override
    public void sendMail(String recipient, String subject, String text) throws MessagingException {
        Message smtpMessage = new MimeMessage(session);
        try {
            smtpMessage.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Ошибка кодирования темы письма", e);
        }
        smtpMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        String mailFrom = projectParams.getMailFrom();
        try {
            smtpMessage.setFrom(new InternetAddress(mailFrom, MAIL_RECEIVER_PERSONAL));
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Ошибка кодирования отправителя письма", e);
        }
        smtpMessage.setSentDate(new Date());
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(text, MAIL_CONTENT_TYPE);
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        smtpMessage.setContent(multipart);
        Transport.send(smtpMessage);
    }
}
