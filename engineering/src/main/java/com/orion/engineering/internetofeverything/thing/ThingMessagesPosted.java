package com.orion.engineering.internetofeverything.thing;

import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ThingMessagesPosted
{
    private CopyOnWriteArrayList<ThingMessage> messages;
}
