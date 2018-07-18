package ru.krista.jkallitheaapi.model;

import java.io.Serializable;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Сущность групп пользователей.
 */
@Entity
@Table(name = "users_groups")
public class UsersGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "users_group_id")
    private Long id;

    @Column(name = "users_group_name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "users_groups_user_id_fkey"))
    private User user;

    @Column(name = "users_group_active")
    private Boolean active;

    @Column(name = "users_group_inherit_default_permissions")
    private Boolean inheritedPermissions;

    @JsonIgnore
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RepoToUsersGroupPermission> repoToUsersGroupPermissions;

    @JsonIgnore
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UsersGroupMember> members;

    /**
     * Группа пользователей.
     * @param name наименование.
     * @param user пользователь.
     * @param active активная.
     * @param inheritedPermissions наследует разрешения.
     */
    public UsersGroup(String name, User user, Boolean active, Boolean inheritedPermissions) {
        this.name = name;
        this.user = user;
        this.active = active;
        this.inheritedPermissions = inheritedPermissions;
    }

    public UsersGroup() {
        //
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getInheritedPermissions() {
        return inheritedPermissions;
    }

    public void setInheritedPermissions(Boolean inheritedPermissions) {
        this.inheritedPermissions = inheritedPermissions;
    }

    public Set<RepoToUsersGroupPermission> getRepoToUsersGroupPermissions() {
        return repoToUsersGroupPermissions;
    }

    public void setRepoToUsersGroupPermissions(
            Set<RepoToUsersGroupPermission> repoToUsersGroupPermissions) {
        this.repoToUsersGroupPermissions = repoToUsersGroupPermissions;
    }

    public Set<UsersGroupMember> getMembers() {
        return members;
    }

    public void setMembers(Set<UsersGroupMember> members) {
        this.members = members;
    }
}
