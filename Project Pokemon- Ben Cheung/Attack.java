/**
 * @(#)Attack.java
 * @author Ben Cheung
 * Attack class which helps format the attack display in choose pokemon. Only method in here is the toStrings
 ** @version 1.00 2014/12/2
 */

public class Attack {
	String name, special;
	int energy, damage;	
    public Attack(String name, int energy, int damage, String special) {
    	this.damage = damage;
    	this.special = special;
    	this.name = name;
    	this.energy = energy;
    	
    	
    }
    public String toString(){
    	// nice string formatting for the pokemon stats
    	// \t = tab, \n = newline
    	return String.format("\t< Pokemon Move: %-12s > \nDamage: %-4d  \nEnergy: %-4s  \nSpecial: %-4s \n",name, damage,energy, special);
    }    
}