package ru.krista.jkallitheaapi.repository;

import java.util.List;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;
import ru.krista.jkallitheaapi.model.Permission;

/**
 * Репозиторий разрешений.
 */
@Repository
public interface PermissionRepository extends EntityRepository<Permission, Long> {

    /**
     * Ищер разрешения по именам.
     * @param names список имен.
     * @return список разрешений.
     */
    @Query(value = "select p from Permission p where name in ?1")
    List<Permission> findByNames(List<String> names);
}
