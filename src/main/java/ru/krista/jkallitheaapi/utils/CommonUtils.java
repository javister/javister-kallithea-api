package ru.krista.jkallitheaapi.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.wildfly.swarm.config.logging.Level;
import org.wildfly.swarm.config.undertow.BufferCache;
import org.wildfly.swarm.config.undertow.FilterConfiguration;
import org.wildfly.swarm.config.undertow.ServletContainer;
import org.wildfly.swarm.config.undertow.configuration.ResponseHeader;
import org.wildfly.swarm.config.undertow.server.HTTPListener;
import org.wildfly.swarm.config.undertow.server.host.FilterRef;
import org.wildfly.swarm.config.undertow.servlet_container.JSPSetting;
import org.wildfly.swarm.config.undertow.servlet_container.WebsocketsSetting;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.jpa.JPAFraction;
import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.mail.MailFraction;
import org.wildfly.swarm.undertow.UndertowFraction;

/**
 * Константы и методы общие для и для клиента и для сервера.
 */
public final class CommonUtils {

    public static final String SETTINGS_FILE_PROPERY = "settings_file";
    public static final String SWARM_HTTP_PORT_SETTING = "httpport";
    public static final String SWARM_MANAGMENT_PORT_SETTING = "managementport";
    public static final String SWARM_HTTP_PORT_PROPERTY = "swarm.http.port";
    public static final String SWARM_MANAGMENT_PORT_PROPERTY = "swarm.management.http.port";

    public static final String SWARM_MAIL_HOST_SETTING = "mailhost";
    public static final String SWARM_MAIL_PORT_SETTING = "mailport";
    public static final String SWARM_MAIL_USER_NAME_SETTING = "mailuser";
    public static final String SWARM_MAIL_PASS_SETTING = "mailpass";
    public static final String SWARM_MAIL_SSL_SETTING = "mailssl";
    public static final String SWARM_MAIL_DEBUG_SETTING = "maildebug";

    public static final String SWARM_MAIL_USER_NAME_PARAM = "swarm.mail.mail-sessions.kallitheaMail.smtp-server.username";
    public static final String SWARM_MAIL_PASS_PARAM = "swarm.mail.mail-sessions.kallitheaMail.smtp-server.password";
    public static final String SWARM_MAIL_SSL_PARAM = "swarm.mail.mail-sessions.kallitheaMail.smtp-server.ssl";
    public static final String SWARM_MAIL_DEBUG_PARAM = "swarm.mail.mail-sessions.kallitheaMail.debug";

    public static final String SWARM_HTTP_PORT_DEFAULT = "8081";
    public static final String SWARM_MANAGMENT_PORT_DEFAULT = "9995";

    public static final String SWARM_MAIL_HOST_DEFAULT = "localhost";
    public static final String SWARM_MAIL_PORT_DEFAULT = "25";

    public static final String WAR_NAME = "KalitheaApi.war";
    public static final String CONTEXT_ROOT = "kalitheaapi";

    public static final String PR_CLOSED_STATUS = "closed";
    public static final String PR_NEW_STATUS = "new";

    public static final String DB_DRIVER_MODULE_NAME = "org.postgresql";
    public static final String DB_DRIVER_CLASS_NAME = "org.postgresql.Driver";
    public static final String DB_DRIVER_DS_CLASS_NAME = "org.postgresql.xa.PGXADataSource";

    public static final String SETTINGS_FILE = "./settings.yml";

    public static final String DB_URL_SETTING = "databaseurl";
    public static final String DB_LOGIN_SETTING = "dblogin";
    // Пароль на самом деле не захардкожен, а берется из настроек.
    @SuppressWarnings("squid:S2068")
    public static final String DB_PASSWORD_SETTING = "dbpassword";

    public static final String SWARM_LOG_SETTING = "swarmlog";

    public static final String SERVER_HEADER = "server-header";
    public static final String X_POWERED_BY_HEADER = "x-powered-by-header";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS_HEADER = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER = "Access-Control-Allow-Credentials";
    public static final String ACCESS_CONTROL_MAX_AGE_HEADER = "Access-Control-Max-Age";

    public static final String SERVER_HEADER_VALUE = "WildFly/10";
    public static final String X_POWERED_BY_HEADER_VALUE = "Undertow/1";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN_VALUE = "*";
    public static final String ACCESS_CONTROL_ALLOW_METHODS_VALUE = "origin, content-type, accept, authorization, x-requested-with";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS_VALUE = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS_VALUE = "true";
    public static final String ACCESS_CONTROL_MAX_AGE_VALUE = "1209600";

    private CommonUtils() {
    }

    /**
     * Создает фракцию доступа к данным.
     * @param settings настройки.
     * @param dsName имя источника данных.
     * @return фракция доступа к данным.
     */
    // Пароль на самом деле не захардкожен, а берется из настроек.
    @SuppressWarnings("findsecbugs:HARD_CODE_PASSWORD")
    public static DatasourcesFraction createDSFraction(Properties settings, String dsName) {
        return new DatasourcesFraction()
                .jdbcDriver(DB_DRIVER_MODULE_NAME, driver -> {
                    driver.driverClassName(DB_DRIVER_CLASS_NAME);
                    driver.xaDatasourceClass(DB_DRIVER_DS_CLASS_NAME);
                    driver.driverModuleName(DB_DRIVER_MODULE_NAME);
                })
                .dataSource(dsName, dataSource -> {
                    dataSource.driverName(DB_DRIVER_MODULE_NAME);
                    dataSource.connectionUrl(System.getProperty(DB_URL_SETTING, settings.getProperty(DB_URL_SETTING)));
                    dataSource.userName(System.getProperty(DB_LOGIN_SETTING, settings.getProperty(DB_LOGIN_SETTING)));
                    dataSource.password(
                            System.getProperty(DB_PASSWORD_SETTING, settings.getProperty(DB_PASSWORD_SETTING)));

                });
    }

    /**
     * Создает JPA фракцию.
     * @param dataSource источник данных.
     * @return JPA фракция.
     */
    public static JPAFraction createJPAFraction(String dataSource) {
        return new JPAFraction().defaultDatasource(dataSource);
    }

    /**
     * Создает фракцию Undertow.
     * @return фракция Undertow.
     */
    public static UndertowFraction createUndertowFraction() {
        List<FilterRef> filterRefs = new ArrayList<>();
        filterRefs.add(new FilterRef(SERVER_HEADER));
        filterRefs.add(new FilterRef(X_POWERED_BY_HEADER));
        filterRefs.add(new FilterRef(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER));
        filterRefs.add(new FilterRef(ACCESS_CONTROL_ALLOW_METHODS_HEADER));
        filterRefs.add(new FilterRef(ACCESS_CONTROL_ALLOW_HEADERS_HEADER));
        filterRefs.add(new FilterRef(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER));
        filterRefs.add(new FilterRef(ACCESS_CONTROL_MAX_AGE_HEADER));
        List<ResponseHeader> headers = new ArrayList<>();
        headers.add(new ResponseHeader(SERVER_HEADER).headerName("Server")
                .headerValue(SERVER_HEADER_VALUE));
        headers.add(new ResponseHeader(X_POWERED_BY_HEADER).headerName("X-Powered-By")
                .headerValue(X_POWERED_BY_HEADER_VALUE));
        headers.add(
                new ResponseHeader(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER).headerName(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER)
                        .headerValue(ACCESS_CONTROL_ALLOW_ORIGIN_VALUE));
        headers.add(
                new ResponseHeader(ACCESS_CONTROL_ALLOW_METHODS_HEADER).headerName(ACCESS_CONTROL_ALLOW_METHODS_HEADER)
                        .headerValue(ACCESS_CONTROL_ALLOW_METHODS_VALUE));
        headers.add(
                new ResponseHeader(ACCESS_CONTROL_ALLOW_HEADERS_HEADER).headerName(ACCESS_CONTROL_ALLOW_HEADERS_HEADER)
                        .headerValue(ACCESS_CONTROL_ALLOW_HEADERS_VALUE));
        headers.add(new ResponseHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER)
                .headerName(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER)
                .headerValue(ACCESS_CONTROL_ALLOW_CREDENTIALS_VALUE));
        headers.add(new ResponseHeader(ACCESS_CONTROL_MAX_AGE_HEADER).headerName(ACCESS_CONTROL_MAX_AGE_HEADER)
                .headerValue(ACCESS_CONTROL_MAX_AGE_VALUE));

        return new UndertowFraction()
                .server("default-server", server -> {
                    server.httpListener(new HTTPListener("default")
                            .socketBinding("http")
                    );
                    server.host("default-host", host -> host.filterRefs(filterRefs));
                })
                .bufferCache(new BufferCache("default"))
                .servletContainer(new ServletContainer("default")
                        .websocketsSetting(new WebsocketsSetting())
                        .jspSetting(new JSPSetting())
                )
                .filterConfiguration(new FilterConfiguration().responseHeaders(headers));
    }

    /**
     * Создает фракцию логирования.
     * @param logFile путь к файлу логов.
     * @return фракция логирования.
     */
    public static LoggingFraction createLoggingFraction(String logFile) {
        return new LoggingFraction().fileHandler("FILE", f -> {
            Map<String, String> fileProps = new HashMap<>();
            fileProps.put("path", logFile);
            f.file(fileProps);
            f.level(Level.INFO);
            f.formatter("%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n");
        }).rootLogger(Level.INFO, "FILE");
    }

    /**
     * Создает потчовую фракцию.
     * @param host хост SMTP-сервера.
     * @param port порт SMTP-сервера.
     * @return почтовая фракция.
     */
    public static MailFraction createMailFraction(String host, int port) {
        return new MailFraction().smtpServer("kallitheaMail", s -> s.host(host).port(port));
    }

    /**
     * Преобразует объект в строку в int. В случает null возвращает "null".
     * @param str объект.
     * @return строка.
     */
    public static String safeToString(Object str) {
        return str == null ? "null" : str.toString();
    }

    /**
     * Преобразует строку в int. В случает ошибки возвращает 0.
     * @param value строка.
     * @return int.
     */
    public static int safeToInt(String value) {
        if (StringUtils.isNotBlank(value)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignore) {
                //
            }
        }
        return 0;
    }

    /**
     * Возвращает путь заканчивающийся на "/".
     * @param path путь.
     * @return путь заканчивающийся на "/".
     */
    public static String checkPath(String path) {
        return path.endsWith("/") ? path : String.format("%s/", path);
    }
}
