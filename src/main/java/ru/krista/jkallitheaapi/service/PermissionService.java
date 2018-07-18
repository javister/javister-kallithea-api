package ru.krista.jkallitheaapi.service;

import java.util.List;

import ru.krista.jkallitheaapi.model.Permission;

/**
 * Сервис для работы с разрешениями.
 */
public interface PermissionService {

    String NONE_PERMISSION = "repository.none";

    String WRITE_PERMISSION = "repository.write";

    String ADMIN_PERMISSION = "repository.admin";

    List<Permission> getRepoUserWritePermission();

    List<Permission> getRepoGroupWritePermission();
}
