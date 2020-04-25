/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Huang;

import static Huang.Simulation.Braun;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author Shaowei Huang
 */
/**
 * Cooks are simulation actors that have at least one field, a name. When
 * running, a cook attempts to retrieve outstanding orders placed by Eaters and
 * process them.
 */
public class Cook implements Runnable {

    private final String name;
    private Customer currentCustomer;
    private Order currentOrder;
    private boolean availToCook;
    Queue<Food> finishedFood = new LinkedList<Food>();;

    /**
     * You can fell free modify this constructor. It must take at least the
     * name, but may take other parameters if you would find adding them useful
     *
     * @param: the name of the cook
     */
    public Cook(String name) {
        this.name = name;
        this.availToCook = true;

    }

    public String toString() {
        return name;
    }

    /**
     * This method executes as follows. The cook tries to retrieve orders placed
     * by Customers. For each order, a List<Food>, the cook submits each Food
     * item in the List to an appropriate Machine, by calling makeFood(). Once
     * all machines have produced the desired Food, the order is complete, and
     * the Customer is notified. The cook can then go to process the next order.
     * If during its exection the cook is interrupted (i.e., some other thread
     * calls the interrupt() method on it, which could raise
     * InterruptedException if the cook is blocking), then it terminates.
     */
    public void run() {

        Simulation.logEvent(SimulationEvent.cookStarting(this));
        try {
            while (true) {
                //YOUR CODES GOES HERE...
                synchronized (Simulation.customerOrderList) {

                    // Keep waiting until a customer place order
                    System.out.println("# of customer orders pending : " + Simulation.customerOrderList.size());
                    while (Simulation.customerOrderList.isEmpty()) {
                        Simulation.customerOrderList.wait();
                    }

                    if (this.availToCook) {
                        this.currentCustomer = Simulation.customerOrderList.remove();
                        this.currentOrder = new Order(this.currentCustomer.getPriority(), this.currentCustomer.getOrderNum(), this.currentCustomer, this.currentCustomer.getOrder());
                        Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, this.currentOrder.getFoodList(), this.currentCustomer.getOrderNum()));
                        Simulation.customerOrderList.notifyAll();
                        //System.out.println("This customer's priority is ----------> " + this.currentCustomer.getPriority());
                        availToCook = false;
                    }

                    for (int i = 0; i < this.currentOrder.getFoodList().size(); i++) {

                        Food food = this.currentOrder.getFoodList().get(i);

                        // If food is burger
                        if (food.equals(FoodType.burger)) {
                            synchronized (Simulation.Grill.processingList) {
                                while (!(Simulation.Grill.processingList.size() < Simulation.Grill.machineCapacity)) {
                                    Simulation.Grill.processingList.wait();
                                }
                                Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.burger, currentCustomer.getOrderNum()));
                                Simulation.Grill.makeFood(FoodType.burger, this, currentCustomer.getOrderNum());
                                Simulation.Grill.processingList.notifyAll();
                            }
                        } // if food is fries
                        else if (food.equals(FoodType.fries)) {
                            synchronized (Simulation.Burner.processingList) {
                                while (!(Simulation.Burner.processingList.size() < Simulation.Burner.machineCapacity)) {
                                    Simulation.Burner.processingList.wait();
                                }
                                Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.fries, currentCustomer.getOrderNum()));
                                Simulation.Burner.makeFood(FoodType.fries, this, currentCustomer.getOrderNum());
                                Simulation.Burner.processingList.notifyAll();

                            }
                        } // if food is coffee
                        else if (food.equals(FoodType.coffee)) {
                            synchronized (Simulation.Braun.processingList) {
                                while (!(Simulation.Braun.processingList.size() < Simulation.Braun.machineCapacity)) {
                                    Simulation.Braun.processingList.wait();
                                }
                                Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.coffee, currentCustomer.getOrderNum()));
                                Simulation.Braun.makeFood(FoodType.coffee, this, currentCustomer.getOrderNum());
                                Simulation.Braun.processingList.notifyAll();

                            }
                        }
                    }
                    // System.out.println(finishedFood.size());
                    // Wait for machine to cook food
                    synchronized (finishedFood) {
                        while (!(finishedFood.size() == currentCustomer.getOrder().size())) {
                            finishedFood.wait();
                            finishedFood.notifyAll();
                        }
                    }
                    
                    // Machine completed making all food, thus cook complete order
                    Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, currentCustomer.getOrderNum()));
                    this.availToCook = true;
                    
                    // Let customer know that the order is complete
                    synchronized(Simulation.customerOrderCompleted) {
                        Simulation.customerOrderCompleted.put(currentCustomer, true);
                        Simulation.customerOrderCompleted.notifyAll();
                    }
                    
                    // Initialize a new food list from the next order.
                    finishedFood = new LinkedList<Food>();
                }

            }
        } catch (InterruptedException e) {
            // This code assumes the provided code in the Simulation class
            // that interrupts each cook thread when all customers are done.
            // You might need to change this if you change how things are 
            // done in the Simulation class.
            Simulation.logEvent(SimulationEvent.cookEnding(this));
        }
    }
}
