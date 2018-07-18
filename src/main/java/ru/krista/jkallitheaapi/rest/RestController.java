package ru.krista.jkallitheaapi.rest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import ru.krista.jkallitheaapi.model.Comment;
import ru.krista.jkallitheaapi.model.PullRequest;
import ru.krista.jkallitheaapi.model.User;
import ru.krista.jkallitheaapi.service.CommentService;
import ru.krista.jkallitheaapi.service.PullRequestService;
import ru.krista.jkallitheaapi.service.RepoKallitheaService;
import ru.krista.jkallitheaapi.service.ReviewerService;
import ru.krista.jkallitheaapi.service.SimplePullRequestInfo;
import ru.krista.jkallitheaapi.service.StatusService;
import ru.krista.jkallitheaapi.service.UserService;

/**
 * Основной rest-контроллер.
 */
@Path("/pullrequest")
public class RestController {

    private static final Logger LOGGER = Logger.getLogger(RestController.class.getName());

    @Inject
    private PullRequestService service;

    @Inject
    private ReviewerService reviewerService;

    @Inject
    private StatusService statusService;

    @Inject
    private UserService userService;

    @Inject
    private CommentService commentService;

    @Inject
    private RepoKallitheaService repoKallitheaService;

    /**
     * Получить детальную информацию о конкретном пул-реквесте.
     *
     * @param id идентификатор пул-реквеста
     *
     * @return полная ифформация
     */
    @GET
    @Path("detail/{id}")
    @Produces("application/json; charset=utf-8")
    public Response doGetDetail(@PathParam("id") Long id) {
        try {
            PullRequest pullRequest = service.findForDetails(id);
            if (pullRequest == null) {
                throw new IllegalArgumentException(String.format("Не найден пул-реквест с id = %d", id));
            }
            reviewerService.setStatuses(pullRequest);
            return Response.ok().entity(pullRequest).build();
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Ошибка получения информации о pull request'e", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Получить информацию по всем открытым пул-реквестам указанного репозитория.
     *
     * @param repoName имя репозитория
     *
     * @return полная ифформация
     */
    @GET
    @Path("open/{repo}")
    @Produces("application/json; charset=utf-8")
    public List<SimplePullRequestInfo> doGet(@PathParam("repo") String repoName) {
        if (StringUtils.isBlank(repoName)) {
            throw new IllegalArgumentException("Не указано наименование репозитория.");
        }
        String fixedRepoName = repoName.replace('.', '/');
        return service.findByRepositoryName(fixedRepoName).stream()
                .map(SimplePullRequestInfo::new).collect(Collectors.toList());
    }

    /**
     * Обработать отчет сонара по пул-реквесту для определенного пользователя.
     *
     * @param id            идентификатор пул-реквеста
     * @param userName      пользователь
     * @param cleanPrevious очищать ли предыдущие комменты
     * @param sData         данные сонара
     *
     * @return полная ифформация
     */
    @POST
    @Path("sonar/report/{id}")
    public Response postComment(@PathParam("id") Long id, @QueryParam("user") String userName,
            @DefaultValue("true") @QueryParam("clean") boolean cleanPrevious, String sData) {
        try {
            service.processSonarRequest(id, userName, sData, cleanPrevious);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.ok().build();
    }

    /**
     * Добавить ревьювера для пулреквеста.
     *
     * @param id       идентификатор пул-реквеста (передается через путь)
     * @param userName пользователь-ревьювер (передается через query параметры)
     * @param status   статус ревьювера
     *
     * @return полная ифформация
     */
    @GET
    @Path("{id}/reviewer/add")
    public Response addReviewer(@PathParam("id") Long id, @QueryParam("user") String userName,
            @DefaultValue("under_review") @QueryParam("status") String status) {
        try {
            PullRequest pullRequest = service.findById(id);
            User user = userService.getUserByName(userName);
            Date commentDate = new Date();
            Comment comment = commentService
                    .createComment(new Comment(user, pullRequest, commentDate, commentDate, ""));
            // 1. Проверка в ревьюверах
            reviewerService.checkAndCreateReviewer(user, pullRequest);
            // 1.5 Увеличиваем на 1 версии всех предыдущих статусов
            List<String> revisions = Arrays.asList(pullRequest.getRevisions().split(":"));
            // изменение предыдущих статусов и создание новых должны быть в отдельных вызовах
            statusService.processPreviousStatuses(revisions, pullRequest.getRepository());
            // 2. Создание записи о статусе
            statusService
                    .createNewStatus(user, pullRequest, status.toLowerCase(Locale.getDefault()), comment, revisions);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.ok().build();
    }

    /**
     * Закрыть пул-реквест.
     *
     * @param id идентификатор пул-реквеста (передается через путь)
     *
     * @return полная ифформация
     */
    @GET
    @Path("{id}/close")
    public Response closePullRequest(@PathParam("id") Long id) {
        try {
            service.close(id);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.ok().build();
    }

    /**
     * Получить разрешение пользователя относительно указанного репозитория.
     *
     * @param repoName наименование репозитория
     * @param userName имя пользователя
     *
     * @return разрешение пользователя
     */
    @GET
    @Path("user/permission")
    @Produces("text/plain; charset=utf-8")
    public Response getRepositoryWriteUserList(@QueryParam("repo") String repoName,
            @QueryParam("user") String userName) {
        try {
            String fixedRepoName = repoName.replace('.', '/');
            return Response.ok().entity(repoKallitheaService.getUserOnRepoPermission(fixedRepoName, userName)
                    .getResponse()).build();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Получить список пользователей с разрешением на запись в указанный репозиторий.
     *
     * @param repoName наименование репозитория
     *
     * @return список пользователей
     */
    @GET
    @Path("permission/{repo}")
    @Produces("application/json; charset=utf-8")
    public Response getRepositoryWriteUserList(@PathParam("repo") String repoName) {
        try {
            String fixedRepoName = repoName.replace('.', '/');
            return Response.ok().entity(repoKallitheaService.getWritePermissionUsers(fixedRepoName)).build();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
