/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Huang;

/**
 *
 * @author Shaowei Huang
 */
 
/**
 * We create all food objects used by the simulation in one place, here.
 * This allows us to safely check equality via reference, rather than by
 * structure/values.
 *
 */
public class FoodType {
    public static final Food burger = new Food("burger",500);
	public static final Food fries = new Food("fries",350);
	public static final Food coffee = new Food("coffee",100);
}
