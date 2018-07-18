package ru.krista.jkallitheaapi.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Сущность связи уведомлений с пользователь-получателем.
 */
@Entity
@Table(name = "user_to_notification")
public class NotificationUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private NotificationUserKey key;

    @Column(name = "read")
    private Boolean isRead;

    public NotificationUser() {
        //
    }

    /**
     * Создает сущность связи уведомлений с пользователь-получателем.
     * @param key ключ.
     */
    public NotificationUser(NotificationUserKey key) {
        this.key = key;
        this.isRead = Boolean.FALSE;
    }

    public NotificationUserKey getKey() {
        return key;
    }

    public void setKey(NotificationUserKey key) {
        this.key = key;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}
