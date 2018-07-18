package ru.krista.jkallitheaapi.service.impl;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import ru.krista.jkallitheaapi.model.Comment;
import ru.krista.jkallitheaapi.model.PullRequest;
import ru.krista.jkallitheaapi.model.User;
import ru.krista.jkallitheaapi.repository.CommentRepository;
import ru.krista.jkallitheaapi.service.CommentService;
import ru.krista.jkallitheaapi.service.StatusService;

/**
 * Реализация сервиса для работы с комментариями.
 */
@Stateless
public class CommentServiceImpl implements CommentService {

    @Inject
    private CommentRepository repository;

    @Inject
    private StatusService statusService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removePreviousComment(User user, PullRequest pullRequest) {
        repository.findByUserAndPullRequest(user, pullRequest).forEach(comment -> {
            statusService.removeByComment(comment);
            repository.remove(comment);
        });
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Comment createComment(Comment comment) {
        return repository.saveAndFlushAndRefresh(comment);
    }
}
