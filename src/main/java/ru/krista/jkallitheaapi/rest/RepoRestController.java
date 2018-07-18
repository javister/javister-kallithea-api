package ru.krista.jkallitheaapi.rest;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import net.sf.json.JSONArray;
import ru.krista.jkallitheaapi.service.RepoKallitheaService;

/**
 * Rest контроллер управления репозиториями.
 */
@Path("/repository")
public class RepoRestController {

    private static final Logger LOGGER = Logger.getLogger(RepoRestController.class.getName());

    @Inject
    private RepoKallitheaService repoKallitheaService;

    /**
     * Получить родительский репозиторий для форка.
     *
     * @param repositoryName имя форка
     *
     * @return имя родительского репозитория
     */
    @GET
    @Path("parent/{repo}")
    @Produces("text/plain; charset=utf-8")
    public Response doGetParent(@PathParam("repo") String repositoryName) {
        try {
            String fixedRepoName = repositoryName.replace('.', '/');
            return Response.ok().entity(repoKallitheaService.getParent(fixedRepoName)).build();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Получить список форков репозитория по определенному фильтру.
     *
     * @param repositoryName имя репозитория
     * @param nameFilter     фильтрация имени в виде регулярного выражения
     *
     * @return список форков как json-массив
     */
    @GET
    @Path("childs/{repo}")
    @Produces("application/json; charset=utf-8")
    public Response doGetChilds(@PathParam("repo") String repositoryName, @QueryParam("filter") String nameFilter) {
        try {
            String fixedRepoName = repositoryName.replace('.', '/');
            JSONArray result = new JSONArray();
            result.addAll(repoKallitheaService.getChilds(fixedRepoName, nameFilter));
            return Response.ok().entity(result).build();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Получить список открытых веток репозитория.
     *
     * @param repositoryName имя репозитория
     *
     * @return список открытых веток
     */
    @GET
    @Path("branches/{repo}")
    @Produces("application/json; charset=utf-8")
    public Response doGetBranches(@PathParam("repo") String repositoryName) {
        try {
            String fixedRepoName = repositoryName.replace('.', '/');
            JSONArray result = new JSONArray();
            result.addAll(repoKallitheaService.getBranches(fixedRepoName));
            return Response.ok().entity(result).build();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Заблокировать репозиторий.
     *
     * @param repositoryName имя репозитория (обязательный)
     * @param userName       имя пользователя (обязательный)
     *
     * @return сообщение "OK" если не было ошибок
     */
    @GET
    @Path("lock")
    @Produces("text/plain; charset=utf-8")
    public Response doLock(@QueryParam("repo") String repositoryName,
            @QueryParam("user") String userName) {
        try {
            String fixedRepoName = repositoryName.replace('.', '/');
            repoKallitheaService.lock(fixedRepoName, userName);
            return Response.ok().entity("OK").build();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Разблокировать репозиторий.
     *
     * @param repositoryName имя репозитория (обязательный)
     * @param userName       имя пользователя, заблокировавшего репозиторий (обязательный)
     *
     * @return сообщение "OK" если не было ошибок
     */
    @GET
    @Path("unlock")
    @Produces("text/plain; charset=utf-8")
    public Response doUnlock(@QueryParam("repo") String repositoryName,
            @QueryParam("user") String userName) {
        try {
            String fixedRepoName = repositoryName.replace('.', '/');
            repoKallitheaService.unlock(fixedRepoName, userName);
            return Response.ok().entity("OK").build();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
