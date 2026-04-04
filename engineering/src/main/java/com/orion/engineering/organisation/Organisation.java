package com.orion.engineering.organisation;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Organisation
{
    private UUID id;
    private String name;
}
