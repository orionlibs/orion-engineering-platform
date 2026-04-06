package com.orion.engineering.data.storage;

import org.springframework.stereotype.Service;

@Service(value = "mockDatabase")
public class MockDatabase implements Database
{
    @Override
    public void save(Object model)
    {

    }
}
