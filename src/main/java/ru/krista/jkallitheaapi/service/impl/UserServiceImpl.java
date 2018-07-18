package ru.krista.jkallitheaapi.service.impl;

import java.util.List;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import ru.krista.jkallitheaapi.model.Permission;
import ru.krista.jkallitheaapi.model.Repository;
import ru.krista.jkallitheaapi.model.User;
import ru.krista.jkallitheaapi.repository.UserRepository;
import ru.krista.jkallitheaapi.service.UserService;

/**
 * Реализация сервиса для работы с пользователями.
 */
public class UserServiceImpl implements UserService {

    @Inject
    private UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByName(String name) {
        try {
            return userRepository.findFirstByName(name);
        } catch (NoResultException e) {
            throw new IllegalArgumentException(String.format("Не найден пользователь с именем %s", name), e);
        }
    }



    @Override
    public List<User> getRepoPermissions(Repository repository, List<Permission> permissions) {
        return userRepository.getUserRepoPermission(repository, permissions);
    }

    @Override
    public List<User> getRepoGroupPermissions(Repository repository, List<Permission> permissions) {
        return userRepository.getGroupRepoPermission(repository, permissions);
    }
}
