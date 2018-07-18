package ru.krista.jkallitheaapi.repository;

import java.util.List;

import org.apache.deltaspike.data.api.EntityPersistenceRepository;
import org.apache.deltaspike.data.api.FullEntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.SingleResultType;
import ru.krista.jkallitheaapi.model.Comment;
import ru.krista.jkallitheaapi.model.PullRequest;
import ru.krista.jkallitheaapi.model.PullRequestStatus;
import ru.krista.jkallitheaapi.model.User;

/**
 * Репозиторий статусов пул-реквестов.
 */
@Repository
public interface StatusRepository extends EntityPersistenceRepository<PullRequestStatus, Long>,
        FullEntityRepository<PullRequestStatus, Long> {

    /**
     * Ищет статус PR по пользователю и последней ревизии.
     * @param lastRevision последняя ревизия.
     * @param user пользователь.
     * @param pullRequest PR.
     * @return статус PR.
     */
    @Query(value =
            "select st from PullRequestStatus st where (st.revision = ?1)and(st.user = ?2)and(st.pullRequest = ?3)"
                    + "and(version = (select min(st2.version) from PullRequestStatus st2 where "
                    + "(st2.revision = ?1)and(st2.user = ?2)and(st2.pullRequest = ?3))))", singleResult = SingleResultType.OPTIONAL)
    PullRequestStatus findUserStatus(String lastRevision, User user, PullRequest pullRequest);

    /**
     * Ищет статусы PR по комментарию.
     * @param comment комментарий.
     * @return статусы PR.
     */
    @Query("select st from PullRequestStatus st where st.comment = ?1")
    List<PullRequestStatus> findAllByComment(Comment comment);

    /**
     * Ищет статусы PR по списку ревизий.
     * @param revisions список ревизий.
     * @param repository репозиторий.
     * @return статусы PR.
     */
    @Query("select st from PullRequestStatus st where (st.revision in ?1)and(st.repository = ?2) order by st.version desc")
    List<PullRequestStatus> findByRepositoryAndRevisions(List<String> revisions,
            ru.krista.jkallitheaapi.model.Repository repository);
}
