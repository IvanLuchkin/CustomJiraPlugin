$(document).ready(function() {
    setInterval(function() {
        JIRA.trigger(JIRA.Events.REFRESH_ISSUE_PAGE, [JIRA.Issue.getIssueId()]);
    }, 60000);
})
