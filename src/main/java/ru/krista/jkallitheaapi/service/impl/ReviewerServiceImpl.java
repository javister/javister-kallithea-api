package ru.krista.jkallitheaapi.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import ru.krista.jkallitheaapi.model.PullRequest;
import ru.krista.jkallitheaapi.model.PullRequestReviewer;
import ru.krista.jkallitheaapi.model.User;
import ru.krista.jkallitheaapi.repository.ReviewerRepository;
import ru.krista.jkallitheaapi.service.ReviewerService;
import ru.krista.jkallitheaapi.service.StatusService;

/**
 * Реализация сервиса для работы с ревьюверами.
 */
@Stateless
public class ReviewerServiceImpl implements ReviewerService {

    @Inject
    private ReviewerRepository repository;

    @Inject
    private StatusService statusService;

    @Override
    public List<PullRequestReviewer> findAll() {
        return repository.findAll();
    }

    @Override
    public void setStatuses(PullRequest pullRequest) {
        Set<PullRequestReviewer> reviewers = pullRequest.getReviewers();
        if (reviewers == null || reviewers.isEmpty() || StringUtils.isBlank(pullRequest.getRevisions())) {
            return;
        }
        List<String> revisions = Arrays.asList(pullRequest.getRevisions().split(":"));
        String lastRevision = revisions.get(revisions.size() - 1);
        for (PullRequestReviewer reviewer : reviewers) {
            if (reviewer.getUser() == null) {
                continue;
            }
            String status = statusService.findUserStatus(lastRevision, reviewer.getUser(), pullRequest);
            if (StringUtils.isBlank(status)) {
                status = "under_review";
            }
            reviewer.setStatus(status);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void checkAndCreateReviewer(User user, PullRequest pullRequest) {
        if (repository.findByUserAndPullRequest(user, pullRequest) == null) {
            repository.save(new PullRequestReviewer(user, pullRequest));
        }
    }
}
