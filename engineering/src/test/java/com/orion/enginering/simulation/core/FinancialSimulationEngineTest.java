package com.orion.enginering.simulation.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.orion.engineering.simulation.core.GenericSimulationEngine;
import com.orion.engineering.simulation.event.TickEvent;
import com.orion.enginering.TestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FinancialSimulationEngineTest extends TestBase
{
    private GenericSimulationEngine engine;


    @BeforeEach
    void setUp()
    {
        engine = new GenericSimulationEngine();
    }


    @AfterEach
    void tearDown()
    {
        engine.close();
    }


    @Test
    @DisplayName("Should correctly calculate 1 year of compound interest")
    void testCompoundInterest()
    {
        // 1. Setup: $1000 at 5% annual interest
        var account = new SavingsAccount("retirement-fund", 1000.0, 0.05);
        engine.registerEntity(account);
        // 2. Schedule 12 monthly ticks
        for(long month = 1; month <= 12; month++)
        {
            engine.scheduleEvent(new TickEvent(month));
        }
        // 3. Run simulation
        engine.run(12);
        // Assert: 1000 * (1 + 0.05/12)^12 ≈ 1051.16
        assertEquals(1051.16, account.getBalance(), 0.01);
    }


    @Test
    @DisplayName("Should handle a mid-year deposit correctly")
    void testMidYearDeposit()
    {
        var account = new SavingsAccount("vacation-fund", 1000.0, 0.0); // 0% for simplicity
        engine.registerEntity(account);
        // Ticks for 12 months
        for(long month = 1; month <= 12; month++)
        {
            engine.scheduleEvent(new TickEvent(month));
        }
        // Add $500 at Month 6
        engine.scheduleEvent(new TransactionEvent(6, "vacation-fund", 500.0));
        engine.run(12);
        assertEquals(1500.0, account.getBalance());
    }
}
