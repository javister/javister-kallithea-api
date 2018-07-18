package ru.krista.jkallitheaapi.model;

/**
 * Ответ по разрешению пользователя.
 */
public enum PermissionResponse {

    /**
     * Администратор.
     */
    ADMIN("manager"),
    /**
     * Разрешение на запись.
     */
    WRITE("default"),
    /**
     * Не первый и сторой вариант.
     */
    NONE("none");

    private final String response;

    /**
     * Создает ответ по разрешению пользователя.
     * @param response ответ по разрешению пользователя.
     */
    PermissionResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
