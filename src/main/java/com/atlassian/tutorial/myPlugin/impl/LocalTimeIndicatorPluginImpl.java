package com.atlassian.tutorial.myPlugin.impl;

import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.tutorial.myPlugin.api.LocalTimeIndicatorPlugin;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LocalTimeIndicatorPluginImpl extends AbstractJiraContextProvider
        implements LocalTimeIndicatorPlugin {

    private String[] formattedLocalTimes;

    @Override
    public Map getContextMap(ApplicationUser applicationUser, JiraHelper jiraHelper) {
        this.formattedLocalTimes = new TimeDataFormatter(jiraHelper).getFormattedLocalTimes();
        HashMap hashMap = new HashMap();
        hashMap.put("results", this.formattedLocalTimes);
        return hashMap;
    }

    @Override
    public String[] getFormattedLocalTimes() {
        return formattedLocalTimes;
    }
}
