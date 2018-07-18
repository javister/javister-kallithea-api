package ru.krista.jkallitheaapi.repository;

import java.util.List;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;
import ru.krista.jkallitheaapi.model.Permission;
import ru.krista.jkallitheaapi.model.User;

/**
 * Репозиторий пользователей.
 */
@Repository
public interface UserRepository extends EntityRepository<User, Long> {

    /**
     * Ищет пользователя по имени.
     * @param name имя пользователя.
     * @return пользователь.
     */
    @Query("select u from User u where u.name = ?1")
    User findFirstByName(String name);

    /**
     * Ищет пользователей имеющих разрешения на работу с репозиторием.
     * @param repository репозиторий.
     * @param permissions разрешения.
     * @return список пользователей.
     */
    @Query("select u from User u join u.repoToUserPermissions rtup "
            + "where (rtup.repository = ?1)and(rtup.permission in ?2)")
    List<User> getUserRepoPermission(ru.krista.jkallitheaapi.model.Repository repository, List<Permission> permissions);

    /**
     * Ищет пользователей имеющих разрешения на работу с репозиторием.
     * @param repository репозиторий.
     * @param permission разрешения.
     * @return список пользователей.
     */
    @Query("select u from User u left join u.members ugm left join ugm.group g left join g.repoToUsersGroupPermissions rtugp "
            + "where (rtugp.repository = ?1)and(rtugp.permission in ?2)"
            + "and(g.active = true)and(g.inheritedPermissions = true)")
    List<User> getGroupRepoPermission(ru.krista.jkallitheaapi.model.Repository repository, List<Permission> permission);
}
