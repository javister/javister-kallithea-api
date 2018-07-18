package ru.krista.jkallitheaapi.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

/**
 * Валидатор возможности запуска приложения.
 * Пока проверяет только наличие файлов конфигурации в каталоге запуска.
 *
 * Логировать в этом классе, к сожаленю нельзя из-за ограничения Wildfly Swarm. Если очень нужно - писать в System.err.
 * @see <a href="https://issues.jboss.org/browse/WFLY-3152?focusedCommentId=13483660&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-13483660">https://issues.jboss.org/browse/WFLY-3152</a>
 */
@SuppressWarnings({"squid:S1166", "squid:S00103"})
public class Validator {

    public Validator() {
        //
    }

    private void checkPropertiesFile(File file, StringBuilder errors) {
        Properties properties = new Properties();
        try (InputStream fis = java.nio.file.Files.newInputStream(file.toPath())) {
            properties.load(fis);
            if (properties.isEmpty()) {
                errors.append(String.format("Файл свойств \"%s\" пустой.%n", file.getName()));
            }
        } catch (IOException e) {
            errors.append(String.format("Во время чтения файла свойств \"%s\" произошла ошибка: %s%n", file.getName(), e.toString()));
        }
    }

    private void checkYmlFile(String fileName, StringBuilder errors) {
        Properties properties = null;
        try {
            properties = new ModYamlReader().read(fileName);
            if (properties.isEmpty()) {
                errors.append(String.format("Файл конфигурации \"%s\" пустой.%n", fileName));
            }
        } catch (RuntimeException e) {
            errors.append(String.format("Во время чтения файла конфигурации \"%s\" произошла ошибка: %s%n", fileName, e.toString()));
        }
    }

    private boolean checkExistFile(File file, StringBuilder errors) {
        if (!file.exists()) {
            errors.append(String.format("Отсуствует обязательный файл \"%s\".%n", file.getName()));
        }
        return file.exists();
    }

    private String validateFile(List<String> fileNames) {
        StringBuilder errors = new StringBuilder();
        fileNames.stream().forEach(fileName -> {
            File checkFile = new File(fileName);
            switch (FilenameUtils.getExtension(fileName)) {
                case "properties":
                    if (checkExistFile(checkFile, errors)) {
                        checkPropertiesFile(checkFile, errors);
                    }
                    break;
                case "yml":
                    if (checkExistFile(checkFile, errors)) {
                        checkYmlFile(checkFile.getAbsolutePath(), errors);
                    }
                    break;
                default:
                    checkExistFile(checkFile, errors);
                    break;
            }
        });
        return errors.toString();
    }

    private String validateSettings(Properties settings, List<String> requireds) {
        StringBuilder errors = new StringBuilder();
        requireds.stream().forEach(required -> {
            if (!settings.containsKey(required)) {
                errors.append(String.format("Отсуствует обязательная настройка \"%s\" в файле настроек%n", required));
            }
        });
        return errors.toString();
    }

    /**
     * Читает и валидирует настройки приложения.
     * @return настройки приложения.
     */
    public Properties validate() {
        String settingsFile = System.getProperty(CommonUtils.SETTINGS_FILE_PROPERY, CommonUtils.SETTINGS_FILE);
        List<String> validateFiles = new ArrayList<>();
        validateFiles.add(settingsFile);
        String errors = validateFile(validateFiles);
        if (!errors.isEmpty()) {
            throw new IllegalStateException(errors);
        }
        Properties result = new ModYamlReader().read(settingsFile);
        List<String> requiredSettings = new ArrayList<>();
        requiredSettings.add(CommonUtils.DB_URL_SETTING);
        requiredSettings.add(CommonUtils.DB_LOGIN_SETTING);
        requiredSettings.add(CommonUtils.DB_PASSWORD_SETTING);
        requiredSettings.add(CommonUtils.SWARM_LOG_SETTING);
        requiredSettings.add("mailfrom");
        String errorsSettings = validateSettings(result, requiredSettings);
        if (!errorsSettings.isEmpty()) {
            throw new IllegalStateException(errorsSettings);
        }
        return result;
    }

}
