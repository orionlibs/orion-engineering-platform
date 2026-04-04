package com.orion.engineering.project;

import com.orion.engineering.project.model.Project;
import java.util.UUID;

public class ProjectService
{
    public Project createProject(String name)
    {
        return Project.builder()
                        .id(UUID.randomUUID())
                        .name(name)
                        .build();
    }


    public void updateProject(Project projectUpdated)
    {

    }
}
