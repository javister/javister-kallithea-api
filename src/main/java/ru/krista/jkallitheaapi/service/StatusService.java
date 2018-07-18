package ru.krista.jkallitheaapi.service;

import java.util.List;

import ru.krista.jkallitheaapi.model.Comment;
import ru.krista.jkallitheaapi.model.PullRequest;
import ru.krista.jkallitheaapi.model.PullRequestStatus;
import ru.krista.jkallitheaapi.model.Repository;
import ru.krista.jkallitheaapi.model.User;

/**
 * Сервис для работы со статусами.
 */
public interface StatusService {

    /**
     * Ищет все статусы PR.
     * @return статусы PR.
     */
    List<PullRequestStatus> findAll();

    /**
     * Удаляет по комментарию.
     * @param comment комментарий.
     */
    void removeByComment(Comment comment);

    /**
     * Ищет статус пользователя.
     * @param lastRevision последняя ревизия.
     * @param user пользователь.
     * @param pullRequest PR.
     * @return статус пользователя.
     */
    String findUserStatus(String lastRevision, User user, PullRequest pullRequest);

    /**
     * Создает новый статус.
     * @param user пользователь.
     * @param pullRequest PR.
     * @param lowStatus lowStatus.
     * @param comment комментарий.
     * @param revisions ревизиии.
     */
    void createNewStatus(User user, PullRequest pullRequest, String lowStatus, Comment comment,
            List<String> revisions);

    /**
     * Обрабатывает предыдущие статусы.
     * @param revisions ревизии.
     * @param requestRepository репозиторий.
     */
    void processPreviousStatuses(List<String> revisions, Repository requestRepository);
}
