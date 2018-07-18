package ru.krista.jkallitheaapi.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.krista.jkallitheaapi.utils.CommonUtils;

/**
 * Пользователь.
 */
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "email"})})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements Serializable {

    private static final long serialVersionUID = -3257771687406380029L;

    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "active")
    private Boolean isActive;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RepoToUserPermission> repoToUserPermissions;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UsersGroupMember> members;

    /**
     * Пустой конструктор.
     */
    public User() {
        //
    }

    /**
     * Инициализирующий конструктор.
     *
     * @param id       идентификатор пользователя
     * @param name     имя пользователя
     * @param email    адрес пользователя
     * @param isActive активен ли пользователь
     */
    public User(Long id, String name, String email, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RepoToUserPermission> getRepoToUserPermissions() {
        return repoToUserPermissions;
    }

    public void setRepoToUserPermissions(Set<RepoToUserPermission> repoToUserPermissions) {
        this.repoToUserPermissions = repoToUserPermissions;
    }


    public Set<UsersGroupMember> getMembers() {
        return members;
    }

    public void setMembers(Set<UsersGroupMember> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return String.format("id: %s; name: %s; email: %s; isActive: %s", id.toString(), name,
                CommonUtils.safeToString(email), CommonUtils.safeToString(isActive));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        User other = (User) obj;
        return Objects.equals(other.getId(), this.id);
    }

    @Override
    public int hashCode() {
        return this.id == null ? super.hashCode() : this.id.hashCode();
    }
}
