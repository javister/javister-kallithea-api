package ru.krista.jkallitheaapi.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import ru.krista.jkallitheaapi.beans.ProjectParams;
import ru.krista.jkallitheaapi.model.Permission;
import ru.krista.jkallitheaapi.model.PermissionResponse;
import ru.krista.jkallitheaapi.model.RepoToUserPermission;
import ru.krista.jkallitheaapi.model.Repository;
import ru.krista.jkallitheaapi.model.User;
import ru.krista.jkallitheaapi.repository.RepoKallitheaRepository;
import ru.krista.jkallitheaapi.service.PermissionService;
import ru.krista.jkallitheaapi.service.RepoKallitheaService;
import ru.krista.jkallitheaapi.service.UserService;
import ru.krista.jkallitheaapi.utils.BranchInfo;
import ru.krista.jkallitheaapi.utils.HttpManager;

/**
 * Реализация сервиса для работы с репозиториями.
 */
@Stateless
public class RepoKallitheaServiceImpl implements RepoKallitheaService {

    private static final String DEFAULT_USER_NAME = "default";
    private static final String REPOSITORY_NOT_FOUND_MSG = "Не найден репозиторий с именем %s";

    @Inject
    private UserService userService;

    @Inject
    private ProjectParams projectParams;

    @Inject
    private RepoKallitheaRepository repoKallitheaRepository;

    @Inject
    private PermissionService permissionService;

    @Override
    public Set<User> getWritePermissionUsers(String repoName) {
        Repository repository = repoKallitheaRepository.findByName(repoName);
        if (repository == null) {
            throw new IllegalArgumentException(String.format(REPOSITORY_NOT_FOUND_MSG, repoName));
        }
        List<Permission> writeRepoPermission = permissionService.getRepoUserWritePermission();
        if (writeRepoPermission.isEmpty()) {
            throw new IllegalStateException("Не найден разрешения на запись пользователем в репозитории");
        }
        // Разрешение группы на администрирование
        writeRepoPermission = writeRepoPermission.stream().filter(permission ->
                PermissionService.ADMIN_PERMISSION.equals(permission.getName())).collect(Collectors.toList());
        Set<User> result = new HashSet<>();
        Set<User> removeUsers = new HashSet<>();
        // 1. Получаем всех пользователей
        List<User> allUsers = userService.findAll();
        // 2. Смотрим разрешения репозитория
        for (RepoToUserPermission userPermission : repository.getRepoToUserPermissions()) {
            User user = userPermission.getUser();
            // пользователь по умолчанию
            if (DEFAULT_USER_NAME.equals(user.getName())) {
                // разрешение для всех пользователей
                if (writeRepoPermission.contains(userPermission.getPermission())) {
                    result.addAll(allUsers);
                }
            } else {
                if (writeRepoPermission.contains(userPermission.getPermission())) {
                    result.add(user);
                } else {
                    removeUsers.add(user);
                }
            }
        }
        // 3. Разрешение группы
        result.addAll(userService.getRepoGroupPermissions(repository, writeRepoPermission));
        result.removeAll(removeUsers);
        return result;
    }

    /**
     * Выбор приоритетного разрешения.
     *
     * @param groupPermission   разрешение группы
     * @param userPermission    разрешение пользователя
     * @param defaultPermission разрешения default пользователя
     *
     * @return приоритетное разрешение
     */
    private String processPermission(String groupPermission, String userPermission, String defaultPermission) {
        // явно не заданы разрешения групп и пользователей - default разрешения
        if (groupPermission.isEmpty() && userPermission.isEmpty()) {
            return defaultPermission;
        }
        // разрешение группы не задано - берем разрешение пользователя
        if (groupPermission.isEmpty()) {
            return userPermission;
        }
        // разрешение пользователя не задано - берем разрешение группы (оно не пустое)
        if (userPermission.isEmpty()) {
            return groupPermission;
        }
        // разрешение пользователя приоритетней
        switch (userPermission) {
            // явно указано отсуствие разрешения либо админское разрешение - берем его
            case PermissionService.NONE_PERMISSION:
            case PermissionService.ADMIN_PERMISSION:
                return userPermission;
            // указано разрешение на запись, возможно группа даёт разрешение на администрирование - берем его
            case PermissionService.WRITE_PERMISSION:
                return PermissionService.ADMIN_PERMISSION.equals(groupPermission) ? groupPermission : userPermission;
            // указано разрешение на чтение - если для группы указано разрешение на админа или запись, то берем его
            default:
                return PermissionService.ADMIN_PERMISSION.equals(groupPermission)
                        || PermissionService.WRITE_PERMISSION.equals(groupPermission) ? groupPermission
                        : userPermission;
        }
    }

    /**
     * Перевод разрешения в строку ответа.
     *
     * @param permission разрешение
     *
     * @return строка ответа
     */
    private PermissionResponse permissionToResult(String permission) {
        switch (permission) {
            case PermissionService.ADMIN_PERMISSION:
                return PermissionResponse.ADMIN;
            case PermissionService.WRITE_PERMISSION:
                return PermissionResponse.WRITE;
            default:
                return PermissionResponse.NONE;
        }
    }

    @Override
    public PermissionResponse getUserOnRepoPermission(String repoName, String userName) {
        Repository repository = repoKallitheaRepository.findByName(repoName);
        if (repository == null) {
            throw new IllegalArgumentException(String.format(REPOSITORY_NOT_FOUND_MSG, repoName));
        }
        List<Permission> writeRepoPermission = permissionService.getRepoUserWritePermission();
        if (writeRepoPermission.isEmpty()) {
            throw new IllegalStateException("Не найдены разрешения записи в репозитории");
        }
        // здесь проверка, что пользователь существует
        User user = userService.getUserByName(userName);
        // Разрешение группы на запись
        List<Permission> writePermission = writeRepoPermission.stream().filter(permission ->
                PermissionService.WRITE_PERMISSION.equals(permission.getName())).collect(Collectors.toList());
        List<User> writeUsers = userService.getRepoGroupPermissions(repository, writePermission);
        // Разрешение группы на администрирование
        List<Permission> adminPermission = writeRepoPermission.stream().filter(permission ->
                PermissionService.ADMIN_PERMISSION.equals(permission.getName())).collect(Collectors.toList());
        List<User> adminUsers = userService.getRepoGroupPermissions(repository, adminPermission);
        // право группы для пользователя
        String writeUserPermission = writeUsers.contains(user) ? PermissionService.WRITE_PERMISSION : "";
        String groupPermission = adminUsers.contains(user) ? PermissionService.ADMIN_PERMISSION
                : writeUserPermission;
        // право пользователя (имеет больший приоритет чем право группы)
        String userPermission = "";
        String defaultPermission = "";
        for (RepoToUserPermission repoUserPermission : repository.getRepoToUserPermissions()) {
            User pUser = repoUserPermission.getUser();
            // либо отдельное право по пользователю, либо общее правило default
            if (DEFAULT_USER_NAME.equals(pUser.getName())) {
                defaultPermission = repoUserPermission.getPermission().getName();
            } else if (pUser.equals(user)) {
                userPermission = repoUserPermission.getPermission().getName();
            }
        }
        // выбор правильного разрешения и перевод его в строку ответа
        return permissionToResult(processPermission(groupPermission, userPermission, defaultPermission));
    }

    private Repository findRepo(String name) {
        Repository repository = repoKallitheaRepository.findByName(name);
        if (repository == null) {
            throw new IllegalArgumentException(String.format(REPOSITORY_NOT_FOUND_MSG, name));
        }
        return repository;
    }

    @Override
    public String getParent(String forkRepositoryName) {
        Repository repository = findRepo(forkRepositoryName);
        if (repository.getParentRepository() == null) {
            throw new IllegalArgumentException(String.format("Не найден родитель для репозитория %s", forkRepositoryName));
        }
        return Optional.ofNullable(repository.getParentRepository().getName()).orElse("<Безымянный>");
    }

    @Override
    public List<String> getChilds(String repositoryName, String nameFilter) {
        Repository repository = findRepo(repositoryName);
        List<String> result = new ArrayList<>();
        if (repository.getForks() != null) {
            repository.getForks().stream().map(fork -> Optional.ofNullable(fork.getName()))
                    .filter(Optional::isPresent).map(Optional::get)
                    .filter(name -> StringUtils.isBlank(nameFilter) || Pattern.compile(nameFilter).matcher(name).find())
                    .forEach(result::add);
        }
        return result;
    }

    @Override
    public List<BranchInfo> getBranches(String repositoryName) {
        Repository repository = findRepo(repositoryName);
        String apiKey = projectParams.getStandartApiKey();
        String standardApiUrl;
        if (StringUtils.isBlank(apiKey)) {
            standardApiUrl = String.format("%s%s/refs-data", projectParams.getKallitheaHome(),
                    repository.getName());
        } else {
            standardApiUrl = String.format("%s%s/refs-data?api_key=%s", projectParams.getKallitheaHome(),
                    repository.getName(), apiKey);
        }
        byte[] rawResponse = HttpManager.selectContent(standardApiUrl);
        JSONObject jResponse = JSONObject.fromObject(new String(rawResponse, StandardCharsets.UTF_8));
        JSONArray jResults = jResponse.getJSONArray("results");
        JSONObject jFirst = jResults.getJSONObject(0);
        JSONArray jBranchInfo = jFirst.getJSONArray("children");
        final int branchCount = jBranchInfo.size();
        List<BranchInfo> result = new ArrayList<>(branchCount);
        for (int i = 0; i < branchCount; i++) {
            JSONObject jBranch = jBranchInfo.getJSONObject(i);
            result.add(new BranchInfo(jBranch.getString("text"), jBranch.getString("id")));
        }
        return result;
    }

    private File getLockFile(Repository repository) {
        String result = projectParams.getRepositoryHome();
        if (StringUtils.isBlank(result)) {
            throw new IllegalStateException("Не указан каталог хранилища репозиториев блокирование/разблокирование "
                    + "невозможно");
        }
        File repoHome = new File(result);
        if (!repoHome.exists()) {
            throw new IllegalStateException(String.format("Не удалось найти каталог хранилища репозиториев '%s'", result));
        }
        File hgHome = new File(repoHome, String.format("%s/.hg", repository.getName()));
        if (!hgHome.exists()) {
            throw new IllegalStateException(String.format("Не удалось найти .hg каталог хранилища репозиториев для пути "
                    + "'%s'", hgHome.getAbsolutePath()));
        }
        return new File(hgHome, projectParams.getLockFileName());
    }

    @Override
    public void lock(String repositoryName, String userName) {
        Repository repository = findRepo(repositoryName);
        PermissionResponse permission = getUserOnRepoPermission(repositoryName, userName);
        if (permission != PermissionResponse.ADMIN) {
            throw new IllegalStateException(String.format("Пользователь %s не имеет прав на блокирование репозитория %s",
                    repository.getName(), userName));
        }
        File lockFile = getLockFile(repository);
        try {
            if (lockFile.exists()) {
                String lockUser = FileUtils.readFileToString(lockFile, StandardCharsets.UTF_8);
                throw new IllegalStateException(String.format("репозиторий %s уже заблокирован пользователем %s",
                        repository.getName(), lockUser));
            }
            FileUtils.writeStringToFile(lockFile, userName, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Ошибка создания файла блокировки: %s", e.getMessage()), e);
        }
    }

    @Override
    public void unlock(String repositoryName, String userName) {
        Repository repository = findRepo(repositoryName);
        // проверяем наличие пользователя в базе
        userService.getUserByName(userName);
        File lockFile = getLockFile(repository);
        if (!lockFile.exists()) {
            throw new IllegalStateException(String.format("Репозиторий %s незаблокирован", repository.getName()));
        }
        try {
            String lockUser = FileUtils.readFileToString(lockFile, StandardCharsets.UTF_8);
            if (!Objects.equals(userName, lockUser)) {
                throw new IllegalStateException(String.format("попытка разблокировки репозитория %s другим пользователем",
                        repository.getName()));
            }
            FileUtils.forceDelete(lockFile);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Ошибка раблокировки: %s", e.getMessage()), e);
        }
    }
}
