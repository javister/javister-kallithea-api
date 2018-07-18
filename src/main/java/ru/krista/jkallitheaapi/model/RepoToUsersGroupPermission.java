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
 * Сущность свзяи разрешений, групп пользователей и репозиториев.
 */
@Entity
@Table(name = "users_group_repo_to_perm")
public class RepoToUsersGroupPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "users_group_to_perm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users_group_id", foreignKey = @ForeignKey(name = "users_group_repo_to_perm_users_group_id_fkey"))
    private UsersGroup group;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "repository_id", foreignKey = @ForeignKey(name = "users_group_repo_to_perm_repository_id_fkey"))
    private Repository repository;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permission_id", foreignKey = @ForeignKey(name = "users_group_repo_to_perm_permission_id_fkey"))
    private Permission permission;

    /**
     * Создает связь разрешения, группы пользователей и репозитория.
     * @param group группа пользователей.
     * @param repository репозиторий.
     * @param permission разрешение.
     */
    public RepoToUsersGroupPermission(UsersGroup group, Repository repository, Permission permission) {
        this.group = group;
        this.repository = repository;
        this.permission = permission;
    }

    public RepoToUsersGroupPermission() {
        //
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsersGroup getGroup() {
        return group;
    }

    public void setGroup(UsersGroup group) {
        this.group = group;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
