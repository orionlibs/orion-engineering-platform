package com.orion.engineering.project.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Project
{
    private UUID id;
    private String name;
}
