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
public class Order {
    private int priority;
    private int orderNum;
    private Customer customer;
    private List<Food> foodList;
    private boolean completed;
    
    public Order (int priority, int orderNum, Customer customer, List<Food> foodList) {
        this.priority = priority;
        this.orderNum = orderNum;
        this.customer = customer;
        this.foodList = foodList;
        this.completed = false;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public int getOrderNum() {
        return orderNum;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public List<Food> getFoodList() {
        return foodList;
    }
    
    public boolean getStatus() {
        return completed;
    }
    
    public void setStatus(boolean completed) {
        this.completed = completed;
    }
    
    
}
