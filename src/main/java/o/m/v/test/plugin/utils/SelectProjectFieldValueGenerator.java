package o.m.v.test.plugin.utils;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class SelectProjectFieldValueGenerator implements ValuesGenerator {
    @ComponentImport
    private ProjectManager projectManager;

    @Autowired
    public SelectProjectFieldValueGenerator(ProjectManager projectManager) {
        this.projectManager = projectManager;
    }

    @Override
    public Map getValues(Map map) {
        //Clear map, because it has initial information.
        map.clear();

        List<Project> projectList = projectManager.getProjectObjects();
        if (projectList.size() > 0) {
            for (Project project : projectList) {
                map.put(project.getKey(), project.getName());
            }
        }
        return map;
    }
}
