package ru.krista.jkallitheaapi.repository;

import org.apache.deltaspike.data.api.EntityPersistenceRepository;
import org.apache.deltaspike.data.api.FullEntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.SingleResultType;
import ru.krista.jkallitheaapi.model.PullRequest;
import ru.krista.jkallitheaapi.model.PullRequestReviewer;
import ru.krista.jkallitheaapi.model.User;

/**
 * Репозиторий ревьюверов.
 */
@Repository
public interface ReviewerRepository extends EntityPersistenceRepository<PullRequestReviewer, Long>,
        FullEntityRepository<PullRequestReviewer, Long> {

    /**
     * Ищет ревьювера PR по пользователю.
     * @param user пользователь.
     * @param pullRequest PR.
     * @return ревьювер PR.
     */
    @Query(value = "select ppr from PullRequestReviewer ppr where (ppr.user = ?1)and(ppr.pullRequest = ?2)",
            singleResult = SingleResultType.OPTIONAL)
    PullRequestReviewer findByUserAndPullRequest(User user, PullRequest pullRequest);
}
