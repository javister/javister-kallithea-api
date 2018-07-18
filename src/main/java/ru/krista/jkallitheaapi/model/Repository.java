package ru.krista.jkallitheaapi.model;

import java.io.Serializable;
import java.util.Objects;
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
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Репозиторий.
 */
@Entity
@Table(name = "repositories", uniqueConstraints = {@UniqueConstraint(columnNames = {"repo_name"})})
public class Repository implements Serializable {

    private static final long serialVersionUID = 4611037131502569672L;

    @Id
    @Column(name = "repo_id")
    private Long id;

    @Column(name = "repo_name", unique = true)
    private String name;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "repositories_user_id_fkey"))
    private User owner;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "repositories_group_id_fkey"))
    private RepositoryGroup group;

    @JsonIgnore
    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RepoToUserPermission> repoToUserPermissions;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fork_id", foreignKey = @ForeignKey(name = "repositories_fork_id_fkey"))
    private Repository parentRepository;

    @JsonIgnore
    @OneToMany(mappedBy = "parentRepository", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Repository> forks;

    /**
     * Пустой конструктор.
     */
    public Repository() {
        // должен быть обязательно
    }

    /**
     * Инициализирующий конструктор.
     *
     * @param id   идентификатор репозитория
     * @param name имя репозитория
     */
    public Repository(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public RepositoryGroup getGroup() {
        return group;
    }

    public void setGroup(RepositoryGroup group) {
        this.group = group;
    }

    public Set<RepoToUserPermission> getRepoToUserPermissions() {
        return repoToUserPermissions;
    }

    public void setRepoToUserPermissions(Set<RepoToUserPermission> repoToUserPermissions) {
        this.repoToUserPermissions = repoToUserPermissions;
    }

    public Repository getParentRepository() {
        return parentRepository;
    }

    public Set<Repository> getForks() {
        return forks;
    }

    public void setForks(Set<Repository> forks) {
        this.forks = forks;
    }

    public void setParentRepository(Repository parentRepository) {
        this.parentRepository = parentRepository;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Repository that = (Repository) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? super.hashCode() : id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("id: %s; name: %s", id.toString(), name);
    }
}
