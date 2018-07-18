package ru.krista.jkallitheaapi.service;

import java.util.List;

import ru.krista.jkallitheaapi.model.Permission;
import ru.krista.jkallitheaapi.model.Repository;
import ru.krista.jkallitheaapi.model.User;

/**
 * Сервис для работы с пользователями.
 */
public interface UserService {

    /**
     * Ищет всех пользователей.
     * @return все пользователи.
     */
    List<User> findAll();

    /**
     * Ищет пользователь по имени.
     * @param name имя пользователя.
     * @return пользователь.
     */
    User getUserByName(String name);

    /**
     * Ищет разрешения репозитория.
     * @param repository репозиторий.
     * @param permissions разрешения.
     * @return список пользователей.
     */
    List<User> getRepoPermissions(Repository repository, List<Permission> permissions);

    /**
     * Ищет разрешения репозитория.
     * @param repository репозиторий.
     * @param permissions разрешения.
     * @return список пользователей.
     */
    List<User> getRepoGroupPermissions(Repository repository, List<Permission> permissions);
}
