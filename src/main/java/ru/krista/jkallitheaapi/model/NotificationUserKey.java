package ru.krista.jkallitheaapi.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Составной ключ сущности связи уведомлений с пользователем-получаетелем.
 */
@Embeddable
public class NotificationUserKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "user_to_notification_user_id_fkey"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", foreignKey = @ForeignKey(name = "user_to_notification_notification_id_fkey"))
    private Notification notification;


    public NotificationUserKey() {
        //
    }

    /**
     * Создает составной ключ сущности связи уведомлений с пользователем-получаетелем.
     * @param user пользователь.
     * @param notification уведомление.
     */
    public NotificationUserKey(User user, Notification notification) {
        this.user = user;
        this.notification = notification;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
