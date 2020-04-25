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
 * Food is what is prepared by Cooks, and ordered by Customers.  Food 
 * is defined by its name, and the amount of time it takes to prepare
 * by Machine.	It is an immutable class.
 */
public class Food {
	public final String name;
	public final int cookTimeMs;
	
	public Food(String name, int cookTimeMs) {
		this.name = name;
		this.cookTimeMs = cookTimeMs;
	}
	
	public String toString() {
		return name;
	}
    
}
