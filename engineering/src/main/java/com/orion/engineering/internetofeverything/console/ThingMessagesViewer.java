package com.orion.engineering.internetofeverything.console;

import com.orion.engineering.internetofeverything.thing.ThingMessagesPosted;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ThingMessagesViewer
{
    private ThingMessagesPosted messages;


    public void pause()
    {

    }


    public void clearViewer()
    {

    }
}
