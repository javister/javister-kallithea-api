package ru.krista.jkallitheaapi.service;

import net.sf.json.JSONObject;
import ru.krista.jkallitheaapi.model.Comment;
import ru.krista.jkallitheaapi.model.PullRequest;

import javax.mail.MessagingException;

/**
 * Сервис работы с отправкой писем.
 */
public interface MailService {

    /**
     * Обрабатывает почту.
     * @param pullRequest PR.
     * @param comment комментарий.
     * @param jSonarData jSonarData.
     * @throws MessagingException в случае ошибки.
     */
    void processMail(PullRequest pullRequest, Comment comment, JSONObject jSonarData) throws MessagingException;

    /**
     * Отправляет письмо.
     * @param recipient получатель.
     * @param subject тема.
     * @param text текст.
     * @throws MessagingException в случае ошибки.
     */
    void sendMail(String recipient, String subject, String text) throws MessagingException;
}
