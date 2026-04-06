package com.orion.engineering.internetofeverything.console;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ThingWebsocketMessage
{
    private String message;
}
