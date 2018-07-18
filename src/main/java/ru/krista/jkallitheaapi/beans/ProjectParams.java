package ru.krista.jkallitheaapi.beans;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

/**
 * Параметры проекта.
 */
@ApplicationScoped
public class ProjectParams {

    private static final String LOCK_FILE_NAME_DEFAULT = "kallithea.lock";

    @Inject
    @ConfigurationValue("mailuser")
    private String mailUser;

    @Inject
    @ConfigurationValue("mailfrom")
    private String mailFrom;

    @Inject
    @ConfigurationValue("kallithea.home")
    private String kallitheaHome;

    @Inject
    @ConfigurationValue("repository.home")
    private String repositoryHome;

    @Inject
    @ConfigurationValue("kallithea.lock")
    private String lockFileName;

    @Inject
    @ConfigurationValue("kallithea.user.apiKey")
    private String userApiKey;

    public String getMailFrom() {
        return mailFrom;
    }

    public String getMailUser() {
        return mailUser;
    }

    public String getKallitheaHome() {
        return kallitheaHome;
    }

    public String getRepositoryHome() {
        return repositoryHome;
    }

    public String getLockFileName() {
        return StringUtils.isBlank(lockFileName) ? LOCK_FILE_NAME_DEFAULT : lockFileName;
    }

    public String getStandartApiKey() {
        return StringUtils.isBlank(userApiKey) ? null : userApiKey;
    }
}
