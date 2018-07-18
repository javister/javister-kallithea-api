package ru.krista.jkallitheaapi.service.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.mail.MessagingException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import ru.krista.jkallitheaapi.model.Comment;
import ru.krista.jkallitheaapi.model.PullRequest;
import ru.krista.jkallitheaapi.model.Repository;
import ru.krista.jkallitheaapi.model.User;
import ru.krista.jkallitheaapi.repository.PullRequestRepository;
import ru.krista.jkallitheaapi.service.CommentService;
import ru.krista.jkallitheaapi.service.MailService;
import ru.krista.jkallitheaapi.service.PullRequestService;
import ru.krista.jkallitheaapi.service.ReviewerService;
import ru.krista.jkallitheaapi.service.StatusService;
import ru.krista.jkallitheaapi.service.UserService;
import ru.krista.jkallitheaapi.utils.CommonUtils;

/**
 * Реализация сервиса для работы с pull request'ами.
 */
@Stateless
public class PullRequestServiceImpl implements PullRequestService {

    private static final Logger LOGGER = Logger.getLogger(PullRequestServiceImpl.class.getName());

    private final PullRequestRepository pullRequestRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final MailService mailService;
    private final StatusService statusService;
    private final ReviewerService reviewerService;

    /**
     * Создает сервис для работы с pull request'ами.
     * @param pullRequestRepository PR реаозиторий.
     * @param userService сервис пользователей.
     * @param commentService сервис комментариев.
     * @param mailService сервис почты.
     * @param statusService сервис статусов.
     * @param reviewerService сервис ревьюверов.
     */
    @Inject
    public PullRequestServiceImpl(PullRequestRepository pullRequestRepository,
            UserService userService, CommentService commentService,
            MailService mailService, StatusService statusService, ReviewerService reviewerService) {
        this.pullRequestRepository = pullRequestRepository;
        this.userService = userService;
        this.commentService = commentService;
        this.mailService = mailService;
        this.statusService = statusService;
        this.reviewerService = reviewerService;
    }

    @Override
    public PullRequest findForDetails(Long id) {
        return pullRequestRepository.findForDetails(id);
    }

    @Override
    public PullRequest findById(Long id) {
        return pullRequestRepository.findBy(id);
    }

    @Override
    public List<PullRequest> findAll() {
        return pullRequestRepository.findAll();
    }

    @Override
    public List<PullRequest> findByRepositoryName(String repoName) {
        return pullRequestRepository.findAllOrderByIdAsc(repoName);
    }

    @Override
    public void processSonarRequest(Long pullRequestId, String userName, String sonarData, boolean cleanPrevious) {
        PullRequest pullRequest = pullRequestRepository.findBy(pullRequestId);
        if (pullRequest == null) {
            throw new IllegalArgumentException(String.format("Не найден пул-реквест с идентификатором %s.", pullRequestId));
        }
        if (StringUtils.isBlank(userName)) {
            throw new IllegalArgumentException("Не указано имя пользователя.");
        }
        User user = userService.getUserByName(userName);
        innerProcessSonarRequest(user, pullRequest, JSONObject.fromObject(sonarData), cleanPrevious);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void close(Long id) {
        PullRequest pullRequest = findById(id);
        if (pullRequest == null) {
            throw new IllegalArgumentException(String.format("Не найден пул-реквест с идентификатором %s.", id));
        }
        if (CommonUtils.PR_CLOSED_STATUS.equals(pullRequest.getStatus())) {
            return;
        }
        pullRequest.setStatus(CommonUtils.PR_CLOSED_STATUS);
        pullRequestRepository.save(pullRequest);
    }

    private void innerProcessSonarRequest(User user, PullRequest pullRequest, JSONObject jSonarData,
            boolean cleanPrevious) {
        if (cleanPrevious) {
            commentService.removePreviousComment(user, pullRequest);
        }
        processLineComments(user, pullRequest, jSonarData.optJSONArray("line"));
        createCommonCommentAndStatus(user, pullRequest, jSonarData);
    }

    private void processLineComments(User user, PullRequest pullRequest, JSONArray lineComments) {
        if (lineComments == null) {
            return;
        }
        Date now = new Date();
        for (int index = 0; index < lineComments.size(); index++) {
            JSONObject jLineComment = lineComments.getJSONObject(index);
            String line = jLineComment.optString("line");
            if (StringUtils.isBlank(line)) {
                throw new IllegalArgumentException(String.format("Найден строковый комментарий без указанного номера "
                        + "строки (line): %s%n", jLineComment.toString()));
            }
            String relativePath = jLineComment.optString("file");
            if (StringUtils.isBlank(relativePath)) {
                throw new IllegalArgumentException(String.format("Найден строковый комментарий без указанного пути к файлу "
                        + "(file): %s%n", jLineComment.toString()));
            }
            String message = jLineComment.optString("message");
            Integer order = jLineComment.optInt("order", 0);
            Date commentDate = createCommentDate(now, order);
            Comment lineComment = new Comment(user, pullRequest, commentDate, commentDate, message);
            lineComment.setRelativePath(relativePath);
            lineComment.setRowNum(line);
            commentService.createComment(lineComment);
        }
    }

    private void createCommonCommentAndStatus(User user, PullRequest pullRequest, JSONObject jSonarData) {
        String message = jSonarData.optString("common");
        String lowStatus = jSonarData.optString("status").toLowerCase(Locale.getDefault());
        Date commentDate = createCommentDate(new Date(), null);
        String statusMessage = "approved".equals(lowStatus) ? "Одобрено" : "Отклонено";
        String fixMessage = StringUtils.isNotBlank(message) ? message : statusMessage;
        Comment comment = new Comment(user, pullRequest, commentDate, commentDate, fixMessage);
        commentService.createComment(comment);
        Repository repository = pullRequest.getRepository();
        String revisions = pullRequest.getRevisions();
        if (repository == null || !("rejected".equals(lowStatus) || "approved".equals(lowStatus))
                || StringUtils.isBlank(revisions)) {
            return;
        }
        // 1. Проверка в ревьюверах
        reviewerService.checkAndCreateReviewer(user, pullRequest);
        // 1.5 Увеличиваем на 1 версии всех предыдущих статусов
        List<String> revisionsList = Arrays.asList(revisions.split(":"));
        statusService.processPreviousStatuses(revisionsList, pullRequest.getRepository());
        // 2. Создание записи о статусе
        statusService.createNewStatus(user, pullRequest, lowStatus, comment, revisionsList);
        try {
            mailService.processMail(pullRequest, comment, jSonarData);
        } catch (MessagingException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Ошибка при отправке почтового сообщения", e);
        }
    }

    private Date createCommentDate(Date date, Integer order) {
        Calendar result = Calendar.getInstance();
        result.setTime(date);
        if (order != null) {
            result.add(Calendar.SECOND, order);
        }
        return result.getTime();
    }

}
