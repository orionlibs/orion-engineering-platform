package com.orion.enginering.simulation.core;

import com.orion.engineering.simulation.core.SimulationContext;
import com.orion.engineering.simulation.core.SimulationEntity;
import com.orion.engineering.simulation.event.SimulationEvent;
import com.orion.engineering.simulation.event.TickEvent;

public class SavingsAccount implements SimulationEntity
{
    private final String id;
    private double balance;
    private final double annualInterestRate;


    public SavingsAccount(String id, double initialDeposit, double annualInterestRate)
    {
        this.id = id;
        this.balance = initialDeposit;
        this.annualInterestRate = annualInterestRate;
    }


    @Override
    public String getID()
    {
        return id;
    }


    @Override
    public void onEvent(SimulationEvent event)
    {
        // Java 25 Pattern Matching for switch
        switch(event)
        {
            case TickEvent t -> applyMonthlyInterest();
            case TransactionEvent e ->
            {
                this.balance += e.amount();
                // Use SimulationContext to get the 'official' simulation time
                // regardless of the event's internal timestamp
                long time = SimulationContext.CURRENT_TIME.get();
                System.out.printf("[%d Mo] %s: Transaction of $%.2f. New Balance: $%.2f%n",
                                time, id, e.amount(), balance);
            }
            default ->
            {
                // Ignore unknown events safely
            }
        }
    }


    private void applyMonthlyInterest()
    {
        // Avoid division by zero and handle 0% interest cases
        if(annualInterestRate == 0)
        {
            return;
        }
        double monthlyRate = annualInterestRate / 12.0;
        double interestEarned = balance * monthlyRate;
        balance += interestEarned;
        long time = SimulationContext.CURRENT_TIME.get();
        System.out.printf("[%d Mo] %s: Interest Earned: $%.2f. Balance: $%.2f%n",
                        time, id, interestEarned, balance);
    }


    public double getBalance()
    {
        return balance;
    }
}
