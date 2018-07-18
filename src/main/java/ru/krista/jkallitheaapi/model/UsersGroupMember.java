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
 * Сущность связи группы с пользователями (вынесено в отдельную сущность, т.к. есть таблица в базе с доп. полями).
 */
@Entity
@Table(name = "users_groups_members")
public class UsersGroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "users_group_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users_group_id", foreignKey = @ForeignKey(name = "users_groups_members_users_group_id_fkey"))
    private UsersGroup group;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "users_groups_members_user_id_fkey"))
    private User user;

    /**
     * Создает связь группы пользователей с пользователем.
     * @param group группа.
     * @param user пользователь.
     */
    public UsersGroupMember(UsersGroup group, User user) {
        this.group = group;
        this.user = user;
    }

    public UsersGroupMember() {
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

    public UsersGroup getGroup() {
        return group;
    }

    public void setGroup(UsersGroup group) {
        this.group = group;
    }
}
