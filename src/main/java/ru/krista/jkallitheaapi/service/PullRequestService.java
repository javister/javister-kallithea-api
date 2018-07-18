package ru.krista.jkallitheaapi.service;

import java.util.List;

import ru.krista.jkallitheaapi.model.PullRequest;

/**
 * Сервис для работы с pull request'ами.
 */
public interface PullRequestService {

    /**
     * Ищет PR со всеми деталями.
     * @param id идентификатор PR.
     * @return PR со всеми деталями.
     */
    PullRequest findForDetails(Long id);

    /**
     * Ищет PR.
     * @param id идентификатор PR.
     * @return PR.
     */
    PullRequest findById(Long id);

    /**
     * Ищет все PR.
     * @return список PR.
     */
    List<PullRequest> findAll();

    /**
     * Ищет все PR репозитория.
     * @param repoName имя репозитория.
     * @return список PR.
     */
    List<PullRequest> findByRepositoryName(String repoName);

    /**
     * Обрабатывает запрос Sonar'a.
     * @param pullRequestId PR id.
     * @param userName имя пользователя.
     * @param sonarData sonarData
     * @param cleanPrevious cleanPrevious.
     */
    void processSonarRequest(Long pullRequestId, String userName, String sonarData, boolean cleanPrevious);

    /**
     * Закрывает PR.
     * @param id идентификатор PR.
     */
    void close(Long id);
}
