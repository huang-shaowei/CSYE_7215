/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Huang;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author Shaowei Huang
 */
/**
 * A Machine is used to make a particular Food. Each Machine makes just one kind
 * of Food. Each machine has a capacity: it can make that many food items in
 * parallel; if the machine is asked to produce a food item beyond its capacity,
 * the requester blocks. Each food item takes at least item.cookTimeMS
 * milliseconds to produce.
 */
public class Machine {

    public final String machineName;
    public final Food machineFoodType;
    public final int machineCapacity;
    public Queue<Food> processingList;
    //YOUR CODE GOES HERE...

    /**
     * The constructor takes at least the name of the machine, the Food item it
     * makes, and its capacity. You may extend it with other arguments, if you
     * wish. Notice that the constructor currently does nothing with the
     * capacity; you must add code to make use of this field (and do whatever
     * initialization etc. you need).
     */
    public Machine(String nameIn, Food foodIn, int capacityIn) {
        this.machineName = nameIn;
        this.machineFoodType = foodIn;
        this.machineCapacity = capacityIn;
        this.processingList = new LinkedList<Food>();
        //YOUR CODE GOES HERE...
    }

    /**
     * This method is called by a Cook in order to make the Machine's food item.
     * You can extend this method however you like, e.g., you can have it take
     * extra parameters or return something other than Object. It should block
     * if the machine is currently at full capacity. If not, the method should
     * return, so the Cook making the call can proceed. You will need to
     * implement some means to notify the calling Cook when the food item is
     * finished.
     */
    public void makeFood(Food food, Cook cook, int orderNum) throws InterruptedException {
        // YOUR CODE GOES HERE...
        synchronized (this.processingList) {
            while (this.processingList.size() >= this.machineCapacity) {
                this.processingList.wait();
            }
            this.processingList.add(food);
            CookAnItem cookItem = new CookAnItem(cook, orderNum);
            Thread itemThread = new Thread(cookItem);
            itemThread.start();
        }

    }

    //THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IT JUST ONE IDEA
    private class CookAnItem implements Runnable {

        Cook cook;
        int orderNum;

        public CookAnItem(Cook cook, int orderNum) {
            this.cook = cook;
            this.orderNum = orderNum;
        }

        public void run() {

            try {
                //YOUR CODE GOES HERE...
                synchronized (Simulation.availMachines) {
                    while (!(Simulation.availMachines.size() < Simulation.events.get(0).simParams[3])) {
                        try {
                            Simulation.availMachines.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Simulation.availMachines.notifyAll();
                }

                Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this, machineFoodType));
                Thread.sleep(machineFoodType.cookTimeMs);
                Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, machineFoodType));
                Simulation.logEvent(SimulationEvent.cookFinishedFood(cook, machineFoodType, orderNum));
                
                synchronized (processingList) {
                    processingList.poll();
                    processingList.notifyAll();
                }
                synchronized (cook.finishedFood) {
                    cook.finishedFood.add(machineFoodType);
                    cook.finishedFood.notifyAll();
                }
                
            } catch (InterruptedException e) {
                System.out.println("Food Completed Thread interrupted");
            }

        }
    }

    public String toString() {
        return machineName;
    }
}
