package ru.krista.jkallitheaapi.repository;

import org.apache.deltaspike.data.api.EntityPersistenceRepository;
import org.apache.deltaspike.data.api.FullEntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.SingleResultType;
import ru.krista.jkallitheaapi.model.Repository;

/**
 * Репозиторий репозиториев каллизеи.
 */
@org.apache.deltaspike.data.api.Repository
public interface RepoKallitheaRepository extends EntityPersistenceRepository<Repository, Long>,
        FullEntityRepository<Repository, Long> {

    /**
     * Ищет репозиторий по наименованию.
     * @param name наименование.
     * @return репозиторий.
     */
    @Query(value = "select r from Repository r where r.name = ?1", singleResult = SingleResultType.OPTIONAL)
    Repository findByName(String name);

}
