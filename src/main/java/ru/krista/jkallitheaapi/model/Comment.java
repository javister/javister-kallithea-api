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
 * Комментарии.
 */
@Entity
@Table(name = "changeset_comments")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "comment_seq", sequenceName = "changeset_comments_comment_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id", foreignKey = @ForeignKey(name = "changeset_comments_repo_id_fkey"))
    private Repository repository;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "changeset_comments_user_id_fkey"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pull_request_id", foreignKey = @ForeignKey(name = "changeset_comments_pull_request_id_fkey"))
    private PullRequest pullRequest;

    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "modified_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyDate;

    @Column(name = "revision", length = 40)
    private String revision;

    @Column(name = "line_no", length = 10)
    private String rowNum;

    @Column(name = "f_path", length = 1000)
    private String relativePath;

    @Column(name = "text", columnDefinition = "text")
    private String message;

    /**
     * Обязательный конструктор.
     */
    public Comment() {
        //
    }

    /**
     * Создает комментарий.
     * @param user пользователь.
     * @param pullRequest PR.
     * @param createDate дата создания.
     * @param modifyDate дата изменения.
     * @param message сообщение.
     */
    public Comment(User user, PullRequest pullRequest, Date createDate, Date modifyDate, String message) {
        this.user = user;
        this.pullRequest = pullRequest;
        this.repository = pullRequest.getRepository();
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public PullRequest getPullRequest() {
        return pullRequest;
    }

    public void setPullRequest(PullRequest pullRequest) {
        this.pullRequest = pullRequest;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRowNum() {
        return rowNum;
    }

    public void setRowNum(String rowNum) {
        this.rowNum = rowNum;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
