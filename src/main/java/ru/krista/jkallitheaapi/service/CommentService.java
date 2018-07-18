package ru.krista.jkallitheaapi.service;

import ru.krista.jkallitheaapi.model.Comment;
import ru.krista.jkallitheaapi.model.PullRequest;
import ru.krista.jkallitheaapi.model.User;

/**
 * Сервис работы с комментариями.
 */
public interface CommentService {

    /**
     * Удаляет предыдущий комментарий пользователя PR.
     * @param user пользователь.
     * @param pullRequest PR.
     */
    void removePreviousComment(User user, PullRequest pullRequest);

    /**
     * Создает комментарий.
     * @param comment комментарий.
     * @return созданный комментарий.
     */
    Comment createComment(Comment comment);
}
