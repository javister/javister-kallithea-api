package ru.krista.jkallitheaapi.utils;

import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

/**
 * YamlReader.
 */
public class ModYamlReader {

    public ModYamlReader() {
        //
    }

    /**
     * Читает properties из YAML файла.
     * @param filePath путь к файлу.
     * @return properties.
     */
    public Properties read(String filePath) {
        Properties result = new Properties();
        try (InputStreamReader fileReader = new InputStreamReader(Files.newInputStream(Paths.get(filePath)), StandardCharsets.UTF_8)) {
            YamlReader reader = new YamlReader(fileReader);
            Map map = (Map) reader.read();
            result.putAll(map);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать yaml файл.", e);
        }
        return result;
    }
}
