package ru.krista.jkallitheaapi.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.krista.jkallitheaapi.utils.CommonUtils;

/**
 * Пул-реквест.
 */
@Entity
@Table(name = "pull_requests")
public class PullRequest implements Serializable {

    private static final long serialVersionUID = 3212551759991023441L;

    @Id
    @Column(name = "pull_request_id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "pull_requests_user_id_fkey"))
    private User user;

    @Column(name = "revisions", columnDefinition = "TEXT")
    private String revisions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_repo_id", foreignKey = @ForeignKey(name = "pull_requests_org_repo_id_fkey"))
    private Repository repository;

    @Column(name = "org_ref")
    private String orgRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "other_repo_id", foreignKey = @ForeignKey(name = "pull_requests_other_repo_id_fkey"))
    private Repository otherRepository;

    @Column(name = "other_ref")
    private String otherRef;

    @OneToMany(mappedBy = "pullRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PullRequestReviewer> reviewers;

    /**
     * Пустой конструктор.
     */
    public PullRequest() {
        //
    }

    /**
     * Инициализирующий конструктор.
     *
     * @param id              идентификатор
     * @param title           заголовок
     * @param description     описание
     * @param status          статус
     * @param createDate      дата создания
     * @param updateDate      дата обновления
     * @param user            пользователь
     * @param revisions       ревизия
     * @param repository      репозиторий
     * @param orgRef          ссылка на что-то (?)
     * @param otherRepository другой репозиторий
     * @param otherRef        ссылка на что-то другое (?)
     */
    public PullRequest(Long id, String title, String description, String status, Date createDate, Date updateDate,
            User user, String revisions, Repository repository, String orgRef, Repository otherRepository,
            String otherRef) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.user = user;
        this.revisions = revisions;
        this.repository = repository;
        this.orgRef = orgRef;
        this.otherRepository = otherRepository;
        this.otherRef = otherRef;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    public Date getCreateDate() {
        return createDate;
    }

    public String getDescription() {
        return description;
    }

    public Long getId() {
        return id;
    }

    public String getOrgRef() {
        return orgRef;
    }

    public String getOtherRef() {
        return otherRef;
    }

    public Repository getOtherRepository() {
        return otherRepository;
    }

    public Repository getRepository() {
        return repository;
    }

    public String getRevisions() {
        return revisions;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    public Date getUpdateDate() {
        return updateDate;
    }

    public User getUser() {
        return user;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setOrgRef(String orgRef) {
        this.orgRef = orgRef;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOtherRef(String otherRef) {
        this.otherRef = otherRef;
    }

    public void setOtherRepository(Repository otherRepository) {
        this.otherRepository = otherRepository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setRevisions(String revisions) {
        this.revisions = revisions;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<PullRequestReviewer> getReviewers() {
        return reviewers;
    }

    public void setReviewers(Set<PullRequestReviewer> reviewers) {
        this.reviewers = reviewers;
    }

    @Override
    public String toString() {
        return String.format("id: %d; title: %s", id, CommonUtils.safeToString(title));
    }
}
