package ru.krista.jkallitheaapi.utils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Информация о ветке.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BranchInfo {

    private String name;
    private String id;

    public BranchInfo() {
        //
    }

    /**
     * Информация о ветке.
     * @param name наименование.
     * @param id идентификатор.
     */
    public BranchInfo(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }
}
