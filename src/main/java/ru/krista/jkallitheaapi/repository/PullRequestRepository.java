package ru.krista.jkallitheaapi.repository;

import java.util.List;

import org.apache.deltaspike.data.api.EntityGraph;
import org.apache.deltaspike.data.api.EntityPersistenceRepository;
import org.apache.deltaspike.data.api.FullEntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;
import ru.krista.jkallitheaapi.model.PullRequest;

/**
 * Репозиторий пул-реквестов.
 */
@Repository
public interface PullRequestRepository extends EntityPersistenceRepository<PullRequest, Long>,
        FullEntityRepository<PullRequest, Long> {

    /**
     * Ищет открытые PR по имени репозитория.
     * @param repoName имя репозитория.
     * @return список PR.
     */
    @Query("select distinct pr from PullRequest pr where not(pr.status = 'closed')and(pr.otherRepository.name = ?1) "
            + "order by id")
    List<PullRequest> findAllOrderByIdAsc(String repoName);

    /**
     * Ищет детальную информацию по PR.
     * @param id идентификатор PR.
     * @return PR.
     */
    @Query("select distinct pr from PullRequest pr where pr.id = ?1")
    @EntityGraph(paths = {"reviewers", "user", "repository", "otherRepository"})
    PullRequest findForDetails(Long id);
}
