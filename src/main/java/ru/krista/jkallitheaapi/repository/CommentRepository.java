package ru.krista.jkallitheaapi.repository;

import java.util.List;

import org.apache.deltaspike.data.api.EntityPersistenceRepository;
import org.apache.deltaspike.data.api.FullEntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;
import ru.krista.jkallitheaapi.model.Comment;
import ru.krista.jkallitheaapi.model.PullRequest;
import ru.krista.jkallitheaapi.model.User;

/**
 * Репозиторий комментов.
 */
@Repository
public interface CommentRepository extends EntityPersistenceRepository<Comment, Long>,
        FullEntityRepository<Comment, Long> {

    /**
     * Ищет комментарии пользователя к PR.
     * @param user пользователь.
     * @param pullRequest PR.
     * @return список комментариев.
     */
    @Query("select c from Comment c where (c.user = ?1)and(c.pullRequest = ?2)")
    List<Comment> findByUserAndPullRequest(User user, PullRequest pullRequest);
}
