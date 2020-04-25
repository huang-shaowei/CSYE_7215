/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Huang;

import java.util.List;

/**
 *
 * @author Shaowei Huang
 */
/**
 * Customers are simulation actors that have two fields: a name, and a list of
 * Food items that constitute the Customer's order. When running, a customer
 * attempts to enter the coffee shop (only successful if the coffee shop has a
 * free table), place its order, and then leave the coffee shop when the order
 * is complete.
 */
public class Customer implements Runnable {

    //JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
    private final String name;
    private final List<Food> order;
    private final int priority;
    private final int orderNum;
    private final long eatingTimeMs;

    private static int runningCounter = 0;

    /**
     * You can fell free modify this constructor. It must take at least the name
     * and order but may take other parameters if you would find adding them
     * useful.
     */
    public Customer(String name, List<Food> order, int priority) {
        this.name = name;
        this.order = order;
        this.priority = priority;
        this.eatingTimeMs = 1000;
        this.orderNum = ++runningCounter;
    }

    public String toString() {
        return name;
    }

    public List<Food> getOrder() {
        return this.order;
    }

    public int getPriority() {
        return this.priority;
    }

    public int getOrderNum() {
        return this.orderNum;
    }

    /**
     * This method defines what a Customer does: The customer attempts to enter
     * the coffee shop (only successful when the coffee shop has a free table),
     * place its order, and then leave the coffee shop when the order is
     * complete.
     */
    public void run() {
        //YOUR CODES GOES HERE...

        // Customer starting
        Simulation.logEvent(SimulationEvent.customerStarting(this));

        // customer enter the coffee shop if there is an avaiable table, otherwise wait.
        synchronized (Simulation.customerInShop) {

            // customer wait for an available table.
            while (Simulation.customerInShop.size() >= Simulation.availTables) {

                try {
                    Simulation.customerInShop.wait();

                } catch (InterruptedException e) {
                    System.out.println("Customer Thread interrupted when entering the shop");
                }
            }

            // customer enter the shop
            Simulation.customerInShop.add(this);
            //System.out.println("Number of customer in shop = " + Simulation.customerInShop.size());
            Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));

        }

        // customer place the order 
        synchronized (Simulation.customerOrderList) {
            synchronized (Simulation.customerOrderCompleted) {
                if (this.priority == 0) {
                    Simulation.customerOrderList.add(this);
                    //Simulation.goldOrderQueue.add(new Order(this.priority, this.orderNum, this, this.order));
                    Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order, this.orderNum));
                    System.out.println("Gold customer placed order " + this.orderNum);
                } else {
                    Simulation.customerOrderList.add(this);
                    //Simulation.regularOrderQueue.add(new Order(this.priority, this.orderNum, this, this.order));
                    Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order, this.orderNum));
                    System.out.println("Regular customer placed order " + this.orderNum);
                }

                Simulation.customerOrderList.notifyAll();
                // Put customer into pending completed order
                Simulation.customerOrderCompleted.put(this, false);
            }
        }

        // After placing order, waiting for the order to be completed 
        synchronized (Simulation.customerOrderCompleted) {

            while (!Simulation.customerOrderCompleted.get(this)) {
                try {
                    Simulation.customerOrderCompleted.wait();
                } catch (InterruptedException e) {
                    System.out.println("Customer Thread interrupted when waiting for the order");
                }
            }

            // Cutomer received order 
            Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, this.order, this.orderNum));
            Simulation.customerOrderCompleted.notifyAll();
        }

        // Customer eating foods 
        try {
            System.out.println(this.name + " is eating food");
            Thread.sleep(this.eatingTimeMs);
        } catch (InterruptedException e) {
            System.out.println("Customer Thread interrupted when eating food");
        }

        // Customer leaves the shop after eating food. 
        synchronized (Simulation.customerInShop) {
            Simulation.customerInShop.remove(this);
            Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
            Simulation.customerInShop.notifyAll();
        }

    }
}
