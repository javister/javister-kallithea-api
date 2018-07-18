package ru.krista.jkallitheaapi.service.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import ru.krista.jkallitheaapi.model.Comment;
import ru.krista.jkallitheaapi.model.PullRequest;
import ru.krista.jkallitheaapi.model.PullRequestStatus;
import ru.krista.jkallitheaapi.model.Repository;
import ru.krista.jkallitheaapi.model.User;
import ru.krista.jkallitheaapi.repository.StatusRepository;
import ru.krista.jkallitheaapi.service.StatusService;

/**
 * Реализация сервиса для работы со статусами.
 */
@Stateless
public class StatusServiceImpl implements StatusService {

    @Inject
    private StatusRepository repository;

    @Override
    public List<PullRequestStatus> findAll() {
        return repository.findAll();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeByComment(Comment comment) {
        List<PullRequestStatus> removeStatuses = repository.findAllByComment(comment);
        removeStatuses.forEach(repository::remove);
    }

    @Override
    public String findUserStatus(String lastRevision, User user, PullRequest pullRequest) {
        PullRequestStatus status = repository.findUserStatus(lastRevision, user, pullRequest);
        return status == null ? null : status.getStatus();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processPreviousStatuses(List<String> revisions, Repository requestRepository) {
        List<PullRequestStatus> statuses = repository.findByRepositoryAndRevisions(revisions, requestRepository);
        for (PullRequestStatus pullRequestStatus : statuses) {
            pullRequestStatus.setVersion(pullRequestStatus.getVersion() + 1);
            repository.save(pullRequestStatus);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createNewStatus(User user, PullRequest pullRequest, String lowStatus, Comment comment,
            List<String> revisions) {
        Date now = new Date();
        for (String revision : revisions) {
            PullRequestStatus pullRequestStatus = new PullRequestStatus(user, pullRequest, comment, now);
            pullRequestStatus.setStatus(lowStatus);
            pullRequestStatus.setVersion(0);
            pullRequestStatus.setRevision(revision);
            repository.save(pullRequestStatus);
        }
    }
}
