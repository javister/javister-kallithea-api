package ru.krista.jkallitheaapi;

import java.io.File;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import ru.krista.jkallitheaapi.beans.ProjectParams;
import ru.krista.jkallitheaapi.model.Comment;
import ru.krista.jkallitheaapi.model.Notification;
import ru.krista.jkallitheaapi.model.NotificationUser;
import ru.krista.jkallitheaapi.model.NotificationUserKey;
import ru.krista.jkallitheaapi.model.Permission;
import ru.krista.jkallitheaapi.model.PermissionResponse;
import ru.krista.jkallitheaapi.model.PullRequest;
import ru.krista.jkallitheaapi.model.PullRequestReviewer;
import ru.krista.jkallitheaapi.model.PullRequestStatus;
import ru.krista.jkallitheaapi.model.RepoToUserPermission;
import ru.krista.jkallitheaapi.model.RepoToUsersGroupPermission;
import ru.krista.jkallitheaapi.model.Repository;
import ru.krista.jkallitheaapi.model.RepositoryGroup;
import ru.krista.jkallitheaapi.model.User;
import ru.krista.jkallitheaapi.model.UsersGroup;
import ru.krista.jkallitheaapi.model.UsersGroupMember;
import ru.krista.jkallitheaapi.repository.PullRequestRepository;
import ru.krista.jkallitheaapi.repository.jpa.EntityManagerProducer;
import ru.krista.jkallitheaapi.rest.RepoRestController;
import ru.krista.jkallitheaapi.rest.RestController;
import ru.krista.jkallitheaapi.service.PullRequestService;
import ru.krista.jkallitheaapi.service.impl.PullRequestServiceImpl;
import ru.krista.jkallitheaapi.utils.BranchInfo;
import ru.krista.jkallitheaapi.utils.CommonUtils;
import ru.krista.jkallitheaapi.utils.HttpManager;
import ru.krista.jkallitheaapi.utils.Validator;

import static ru.krista.jkallitheaapi.utils.CommonUtils.*;

/**
 * Класс запуска сервера.
 */
// Thread.currentThread().getContextClassLoader() приводит к ошибке.
@SuppressWarnings("pmd:UseProperClassLoader")
public class StartServer {

    private static JAXRSArchive createDeployment() throws Exception {
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, WAR_NAME);
        deployment.addModule(DB_DRIVER_MODULE_NAME);
        deployment.addPackage(StartServer.class.getPackage())
                .addAsWebInfResource(
                        new ClassLoaderAsset("META-INF/persistence.xml", StartServer.class.getClassLoader()),
                        "classes/META-INF/persistence.xml")
                // обязательное добавление классов сущностей
                .addClasses(PullRequest.class, Repository.class, User.class, Comment.class,
                        PullRequestReviewer.class, PullRequestStatus.class, Notification.class, NotificationUser.class,
                        NotificationUserKey.class, Permission.class, RepoToUserPermission.class,
                        RepoToUsersGroupPermission.class, UsersGroup.class, UsersGroupMember.class,
                        RepositoryGroup.class, PermissionResponse.class)
                .addClasses(CommonUtils.class, ProjectParams.class, EntityManagerProducer.class, RestController.class,
                        RepoRestController.class, BranchInfo.class, HttpManager.class, RestStarterApplication.class)
                .addPackage(PullRequestRepository.class.getPackage())
                .addPackage(PullRequestService.class.getPackage())
                .addPackage(PullRequestServiceImpl.class.getPackage())
                .setContextRoot(CONTEXT_ROOT)
                .staticContent()
                .addAllDependencies();
        return deployment;
    }

    /**
     * Точка запуска приложения.
     * @param args аргументы командной сктроки.
     * @throws Exception в случае ошибок препятсвтующих запуску приложения.
     */
    public static void main(String[] args) throws Exception {

        // валидация и чтение настроек
        Validator validator = new Validator();
        Properties settings = validator.validate();
        // задаём порты для запуска
        String httpPort = settings.getProperty(SWARM_HTTP_PORT_SETTING) == null ? SWARM_HTTP_PORT_DEFAULT
                : settings.getProperty(SWARM_HTTP_PORT_SETTING);
        String managmentPort = settings.getProperty(SWARM_MANAGMENT_PORT_SETTING) == null ? SWARM_MANAGMENT_PORT_DEFAULT
                : settings.getProperty(SWARM_MANAGMENT_PORT_SETTING);
        final String mailHost = settings.getProperty(SWARM_MAIL_HOST_SETTING, SWARM_MAIL_HOST_DEFAULT);
        final int mailPort = safeToInt(settings.getProperty(SWARM_MAIL_PORT_SETTING, SWARM_MAIL_PORT_DEFAULT));

        System.setProperty(SWARM_HTTP_PORT_PROPERTY, httpPort);
        System.setProperty(SWARM_MANAGMENT_PORT_PROPERTY, managmentPort);

        String mailUser = settings.getProperty(SWARM_MAIL_USER_NAME_SETTING);
        String mailPass = settings.getProperty(SWARM_MAIL_PASS_SETTING);
        if (StringUtils.isNotBlank(mailUser) && StringUtils.isNotBlank(mailPass)) {
            System.setProperty(SWARM_MAIL_USER_NAME_PARAM, mailUser);
            System.setProperty(SWARM_MAIL_PASS_PARAM, mailPass);
        }
        String mailSSL = settings.getProperty(SWARM_MAIL_SSL_SETTING);
        if (StringUtils.isNotBlank(mailSSL)) {
            System.setProperty(SWARM_MAIL_SSL_PARAM, mailSSL);
        }
        String mailDebug = settings.getProperty(SWARM_MAIL_DEBUG_SETTING);
        if (StringUtils.isNotBlank(mailDebug)) {
            System.setProperty(SWARM_MAIL_DEBUG_PARAM, mailDebug);
        }
        // запуск
        new Swarm(false)
                .withConfig(
                        new File(System.getProperty(SETTINGS_FILE_PROPERY, SETTINGS_FILE))
                                .toURI().toURL())
                .fraction(createDSFraction(settings, "KalitheaDB"))
                .fraction(createJPAFraction("java:jboss/datasources/KalitheaDB"))
                .fraction(createUndertowFraction())
                .fraction(createLoggingFraction(settings.getProperty(SWARM_LOG_SETTING)))
                .fraction(createMailFraction(mailHost, mailPort))
/*
                .fraction(
                        ManagementFraction.createDefaultFraction()
                                .httpInterfaceManagementInterface((iface) -> {
                                    iface.allowedOrigin("http://localhost:"+httpPort);
                                    iface.securityRealm("ManagementRealm");
                                })
                                .securityRealm("ManagementRealm", (realm) -> {
                                    realm.inMemoryAuthentication( (authn)->{
                                        authn.add( "admin", "masterkey", true );
                                    });
                                    realm.inMemoryAuthorization( (authz)->{
                                        authz.add( "admin", "admin" );
                                    });
                                })
                )
*/
                .start()
                .deploy(createDeployment());
    }
}
