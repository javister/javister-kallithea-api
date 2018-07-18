package ru.krista.jkallitheaapi.service;

import java.util.List;
import java.util.Set;

import ru.krista.jkallitheaapi.model.PermissionResponse;
import ru.krista.jkallitheaapi.model.User;
import ru.krista.jkallitheaapi.utils.BranchInfo;

/**
 * Сервис работы с репозиториями.
 */
public interface RepoKallitheaService {

    /**
     * Получает пользователей, имеющих права на запись в репозиторий.
     * @param repoName репозиторий.
     * @return список пользователей.
     */
    Set<User> getWritePermissionUsers(String repoName);

    /**
     * Получает ответ с разрешениями пользователя на репозиторий.
     * @param repoName репозиторий.
     * @param userName пользователь.
     * @return ответ с разрешениями пользователя.
     */
    PermissionResponse getUserOnRepoPermission(String repoName, String userName);

    /**
     * Получает родительский репозиторий.
     * @param forkRepositoryName репозиторий.
     * @return родительский репозиторий.
     */
    String getParent(String forkRepositoryName);

    /**
     * Получает список дочерних репозиториев.
     * @param repositoryName репозиторий.
     * @param nameFilter дополнительный фильтр по имени.
     * @return список дочерних репозиториев.
     */
    List<String> getChilds(String repositoryName, String nameFilter);

    /**
     * Получает список веток репозитория.
     * @param repositoryName репозиторий.
     * @return список веток репозитория.
     */
    List<BranchInfo> getBranches(String repositoryName);

    /**
     * Блокирует репозиорий.
     * @param repositoryName имя репозитория.
     * @param userName имя пользователя.
     */
    void lock(String repositoryName, String userName);

    /**
     * Разблокирует репозиорий.
     * @param repositoryName имя репозитория.
     * @param userName имя пользователя.
     */
    void unlock(String repositoryName, String userName);
}
