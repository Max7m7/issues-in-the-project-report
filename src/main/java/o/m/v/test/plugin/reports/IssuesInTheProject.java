package o.m.v.test.plugin.reports;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.query.Query;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Scanned
public class IssuesInTheProject extends AbstractReport {
    private static final Logger log = Logger.getLogger(IssuesInTheProject.class);

    @ComponentImport
    private final SearchProvider searchProvider;
    @ComponentImport
    private final ProjectManager projectManager;

    private String projectKey;
    private String projectName;

    @Autowired
    public IssuesInTheProject(SearchProvider searchProvider, ProjectManager projectManager) {
        this.searchProvider = searchProvider;
        this.projectManager = projectManager;
    }

    @Override
    public String generateReportHtml(ProjectActionSupport projectActionSupport, Map map) throws Exception {
        Map<String, String> issueKeySummaryMap = new HashMap<>();

        //Get issues on the project.
        JqlQueryBuilder queryBuilder = JqlQueryBuilder.newBuilder();
        Query query = queryBuilder.where().project(projectKey).buildQuery();

        SearchResults searchResults = searchProvider
                .search(query, projectActionSupport.getLoggedInUser(), PagerFilter.getUnlimitedFilter());

        if (searchResults != null) {
            issueKeySummaryMap = searchResults.getIssues().stream()
                    .collect(Collectors.toMap(Issue::getKey, Issue::getSummary));
        }

        Map<String, Object> velocityParams = new HashMap<>();
        velocityParams.put("issueKeySummaryMap", issueKeySummaryMap);
        velocityParams.put("projectName", projectName);

        return descriptor.getHtml("view", velocityParams);
    }

    @Override
    public void validate(ProjectActionSupport action, Map params) {
        projectKey = ParameterUtils.getStringParam(params, "projectKey");

        if (projectKey == null || projectManager.getProjectByCurrentKey(projectKey) == null) {
            action.addError("projectKey", action.getText("Please select a valid project."));
            log.error("Invalid projectKey.");
        } else {
            projectName = projectManager.getProjectByCurrentKey(projectKey).getName();
        }
    }
}
