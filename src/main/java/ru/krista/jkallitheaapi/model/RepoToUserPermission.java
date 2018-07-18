package ru.krista.jkallitheaapi.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Сущность свзяи разрешений, репозиториев и пользователей.
 */
@Entity
@Table(name = "repo_to_perm")
public class RepoToUserPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "repo_to_perm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "repo_to_perm_user_id_fkey"))
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "repository_id", foreignKey = @ForeignKey(name = "repo_to_perm_repository_id_fkey"))
    private Repository repository;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permission_id", foreignKey = @ForeignKey(name = "repo_to_perm_permission_id_fkey"))
    private Permission permission;

    /**
     * Создает связь разрешения, репозитория и пользователя.
     * @param user пользователь.
     * @param repository репозиторий.
     * @param permission разрешение.
     */
    public RepoToUserPermission(User user, Repository repository, Permission permission) {
        this.user = user;
        this.repository = repository;
        this.permission = permission;
    }

    public RepoToUserPermission() {
        //
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
