package ru.krista.jkallitheaapi.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.krista.jkallitheaapi.model.PullRequest;

/**
 * Класс-ответ по репозиторям (простое описание пул-реквеста).
 */
public class SimplePullRequestInfo {

    private Long id;
    private String title;

    /**
     * Создает ответ по репозиторям (простое описание пул-реквеста).
     * @param pullRequest PR.
     */
    public SimplePullRequestInfo(PullRequest pullRequest) {
        this.id = pullRequest.getId();
        this.title = pullRequest.getTitle();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
