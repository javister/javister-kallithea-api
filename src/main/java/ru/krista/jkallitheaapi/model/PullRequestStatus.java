package ru.krista.jkallitheaapi.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Сущность статуса пул-реквеста.
 */
@Entity
@Table(name = "changeset_statuses")
public class PullRequestStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "changeset_status_id_seq", sequenceName = "changeset_statuses_changeset_status_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "changeset_status_id_seq")
    @Column(name = "changeset_status_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id", foreignKey = @ForeignKey(name = "changeset_statuses_repo_id_fkey"))
    private Repository repository;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "changeset_statuses_user_id_fkey"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pull_request_id", foreignKey = @ForeignKey(name = "changeset_statuses_pull_request_id_fkey"))
    private PullRequest pullRequest;

    @Column(name = "modified_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyDate;

    @Column(name = "revision", length = 40)
    private String revision;

    @Column(name = "status", length = 128)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changeset_comment_id", foreignKey = @ForeignKey(name = "changeset_statuses_changeset_comment_id_fkey"))
    private Comment comment;

    @Column(name = "version")
    private Integer version;

    public PullRequestStatus() {
        //
    }

    /**
     * Создает информцию о статусе PR.
     * @param user пользователь.
     * @param pullRequest PR.
     * @param comment комментарий.
     * @param modifyDate дата изменения.
     */
    public PullRequestStatus(User user, PullRequest pullRequest, Comment comment, Date modifyDate) {
        this.user = user;
        this.pullRequest = pullRequest;
        this.repository = pullRequest.getRepository();
        this.comment = comment;
        this.modifyDate = modifyDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public PullRequest getPullRequest() {
        return pullRequest;
    }

    public void setPullRequest(PullRequest pullRequest) {
        this.pullRequest = pullRequest;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
