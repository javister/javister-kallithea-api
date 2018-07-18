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
 * Сущность групп (каталогов) репозиториев.
 */
@Entity
@Table(name = "groups")
public class RepositoryGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "group_id")
    private Long id;

    @Column(name = "group_name", unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "groups_user_id_fkey"))
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_parent_id", foreignKey = @ForeignKey(name = "groups_group_parent_id_fkey"))
    private RepositoryGroup parent;

    /**
     * Создает группу (каталог) репозиториев.
     * @param name наименование.
     * @param owner владелец.
     * @param parent родитель.
     */
    public RepositoryGroup(String name, User owner, RepositoryGroup parent) {
        this.name = name;
        this.owner = owner;
        this.parent = parent;
    }

    public RepositoryGroup() {
        //
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

    public RepositoryGroup getParent() {
        return parent;
    }

    public void setParent(RepositoryGroup parent) {
        this.parent = parent;
    }
}
