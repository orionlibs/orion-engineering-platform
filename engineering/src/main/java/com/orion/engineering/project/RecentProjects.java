package com.orion.engineering.project;

import com.orion.engineering.project.model.Project;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecentProjects
{
    private List<Project> projectsRecentFirst;
}
