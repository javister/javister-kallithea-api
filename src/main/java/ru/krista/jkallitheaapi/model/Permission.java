package ru.krista.jkallitheaapi.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Сущность разрешений.
 */
@Entity
@Table(name = "permissions")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "permission_id")
    private Long id;

    @Column(name = "permission_name")
    private String name;

    public Permission() {
        //
    }

    /**
     * Разрешение.
     * @param id идентификатор.
     * @param name наименование.
     */
    public Permission(Long id, String name) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Permission other = (Permission) obj;
        return Objects.equals(other.getId(), this.id);
    }

    @Override
    public int hashCode() {
        return this.id == null ? super.hashCode() : this.id.hashCode();
    }

}
