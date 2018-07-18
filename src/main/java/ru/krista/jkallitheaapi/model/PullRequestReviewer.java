package ru.krista.jkallitheaapi.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Ревьюверы пул-реквеста.
 */
@Entity
@Table(name = "pull_request_reviewers")
public class PullRequestReviewer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "pull_request_reviewers_seq",
            sequenceName = "pull_request_reviewers_pull_requests_reviewers_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pull_request_reviewers_seq")
    @Column(name = "pull_requests_reviewers_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "pull_request_reviewers_user_id_fkey"))
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pull_request_id", foreignKey = @ForeignKey(name = "pull_request_reviewers_pull_request_id_fkey"))
    private PullRequest pullRequest;

    @Transient
    private String status;

    public PullRequestReviewer() {
        //
    }

    /**
     * Создает ифнормацию о ревьювере пул-реквеста.
     * @param user пользователь.
     * @param pullRequest PR.
     */
    public PullRequestReviewer(User user, PullRequest pullRequest) {
        this.user = user;
        this.pullRequest = pullRequest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PullRequest getPullRequest() {
        return pullRequest;
    }

    public void setPullRequest(PullRequest pullRequest) {
        this.pullRequest = pullRequest;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("user")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getUserName() {
        return user == null ? null : user.getName();
    }
}
