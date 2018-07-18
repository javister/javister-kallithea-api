package ru.krista.jkallitheaapi.service;

import java.util.List;

import ru.krista.jkallitheaapi.model.PullRequest;
import ru.krista.jkallitheaapi.model.PullRequestReviewer;
import ru.krista.jkallitheaapi.model.User;

/**
 * Сервис для работы с ревьюверами.
 */
public interface ReviewerService {

    /**
     * Получает список всех ревьюверов.
     * @return список всех ревьюверов.
     */
    List<PullRequestReviewer> findAll();

    /**
     * Устанавливает статусы PR.
     * @param pullRequest PR.
     */
    void setStatuses(PullRequest pullRequest);

    /**
     * Проверяет и создает ревьювера.
     * @param user пользователь.
     * @param pullRequest PR.
     */
    void checkAndCreateReviewer(User user, PullRequest pullRequest);
}
