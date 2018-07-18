package ru.krista.jkallitheaapi.service.impl;

import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

import ru.krista.jkallitheaapi.model.Permission;
import ru.krista.jkallitheaapi.repository.PermissionRepository;
import ru.krista.jkallitheaapi.service.PermissionService;

/**
 * Реализация сервиса для работы с разрешениями.
 */
public class PermissionServiceImpl implements PermissionService {

    private static final List<String> WRITE_REPO_USER_PERMISSION_NAME = Arrays
            .asList(WRITE_PERMISSION, ADMIN_PERMISSION);
    private static final List<String> WRITE_REPO_GROUP_PERMISSION_NAME = Arrays.asList("group.write", "group.admin");

    @Inject
    private PermissionRepository repository;

    @Override
    public List<Permission> getRepoUserWritePermission() {
        return repository.findByNames(WRITE_REPO_USER_PERMISSION_NAME);
    }

    @Override
    public List<Permission> getRepoGroupWritePermission() {
        return repository.findByNames(WRITE_REPO_GROUP_PERMISSION_NAME);
    }
}
