package com.orion.engineering.simulation.core;

import com.orion.computationalplatform.simulation.event.SimulationEvent;

public interface SimulationEntity
{
    String getID();


    void onEvent(SimulationEvent event);
}
