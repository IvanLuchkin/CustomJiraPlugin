package com.atlassian.tutorial.myPlugin.impl;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.timezone.TimeZoneManager;
import com.atlassian.jira.user.ApplicationUser;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class TimeDataFormatter {
    private final TimeZoneManager timeZoneManager;
    private final JiraHelper jiraHelper;
    private final CommentManager commentManager;
    private final IssueManager issueManager;
    private ZonedDateTime localTimeForCurrentUser;

    public TimeDataFormatter(JiraHelper jiraHelper) {
        this.jiraHelper = jiraHelper;
        this.commentManager = ComponentAccessor.getCommentManager();
        this.timeZoneManager = ComponentAccessor.getComponent(TimeZoneManager.class);
        this.issueManager = ComponentAccessor.getIssueManager();
    }

    public String[] getFormattedLocalTimes() {
        Map<String, Object> contextParams = jiraHelper.getContextParams();
        List<ApplicationUser> issueRelatedUsers = getIssueRelatedUsers(
                issueManager.getIssueObject((contextParams.get("issue")).toString()));

        ApplicationUser currentUser = (ApplicationUser) contextParams.get("user");
        this.localTimeForCurrentUser = getLocalTimeForUser(currentUser);
        issueRelatedUsers.removeAll(new ArrayList<ApplicationUser>() {{
            add(currentUser);
        }});

        List<String> usernamesWithTimeZones = formatUsersLocalTimes(issueRelatedUsers, currentUser);
        return usernamesWithTimeZones.toArray(new String[]{});
    }

    private List<String> formatUsersLocalTimes(
            List<ApplicationUser> issueRelatedUsers, ApplicationUser currentUser) {

        List<String> usernamesWithTimeZones = issueRelatedUsers
                .stream()
                .distinct()
                .filter(Objects::nonNull)
                .map(this::formatLocalTime)
                .collect(Collectors.toList());
        usernamesWithTimeZones.add(0, formatLocalTimeForCurrentUser(currentUser));
        return usernamesWithTimeZones;
    }

    private String formatLocalTime(ApplicationUser applicationUser) {
        ZonedDateTime localTimeForUser = getLocalTimeForUser(applicationUser);
        long differenceInHours = Duration.between(
                localTimeForCurrentUser.toLocalDateTime(), localTimeForUser.toLocalDateTime()).toHours();
        return String.format("%s [%sH] (%s)",
                applicationUser.getDisplayName(),
                differenceInHours,
                DateTimeFormatter.RFC_1123_DATE_TIME.format(localTimeForUser));
    }

    private String formatLocalTimeForCurrentUser(ApplicationUser applicationUser) {
        return String.format("%s (you) (%s)",
                applicationUser.getDisplayName(),
                DateTimeFormatter.RFC_1123_DATE_TIME.format(getLocalTimeForUser(applicationUser)));
    }

    private List<ApplicationUser> getIssueRelatedUsers(Issue currentIssue) {
        List<ApplicationUser> issueRelatedUsers = commentManager.getComments(currentIssue)
                .stream()
                .map(Comment::getAuthorApplicationUser).collect(Collectors.toList());
        issueRelatedUsers.add(currentIssue.getAssignee());
        issueRelatedUsers.add(currentIssue.getReporter());
        return issueRelatedUsers;
    }

    private ZonedDateTime getLocalTimeForUser(ApplicationUser applicationUser) {
        TimeZone userTimeZone = timeZoneManager.getTimeZoneforUser(applicationUser);
        return ZonedDateTime.now(userTimeZone.toZoneId());
    }
}
