/**
 * @(#)Pokemon.java
 *
 * This is the Pokemon class that the Pokemon Arena class calls.
 *It contains the necessary calculations, status info and moves, as well as special moves and abitites. This does the many 50% chance coin flips, attack calculations and sets the True/False boolean flags.
 * @author Ben Cheung
 * @version 1.00 2014/12/1
 */

import java.util.*;
public class Pokemon{
	/*Define all variables , privates, publics and arraylists
	 **/
	private boolean successAtk = true, usePokeEnergy = true, disabledPokemon = false;
	public boolean pokemonStunned = false;
	public int initialHp, hp, energy;
	ArrayList<Attack>attacks; //get attack arraylist
	public String name,type,resist,weakness;

	public Pokemon(String name, int hp, String type, String resist, String weakness,ArrayList<Attack>attacks ){
		//constructor stuff
		this.initialHp=hp;//starting health point, will not be effected during battle
		this.hp=initialHp;//current health of pokemon, will be effeced during battle
		this.type=type;
		this.resist=resist;
		this.energy=50;
		this.weakness=weakness;
		this.attacks=attacks;		
		this.name=name;
	}
    
	public int randomAtk(Pokemon enemy){;
	/* A method for the attack of the enemy pokemon. Includes if the enemy pokemon has energy to attack ,or no energy to attack.
	 */
		boolean move = false; //set the move flag to false
		ArrayList<Integer>movesAbletoBeUsed = new ArrayList<Integer>(); //create ArrayList for moves still avaliable for use
		Random number = new Random();//choose random number
		
		for(int i = 0; i < enemy.attacks.size(); i++){
			if(pokeHasEnergy(enemy,i) == true){//if pokemon has enough energy to perform the attack
				movesAbletoBeUsed.add(i);//add avaliable attacks to list
				move = true;	
			}
		}
		if(movesAbletoBeUsed.size() == 0){//if pokemon has no energy to perform any attack (pass)
			return -1;
		}
		int moveoption = number.nextInt(movesAbletoBeUsed.size());//choose random moveoptions of avaliable attacks		
		return moveoption;
	}
	
	public void pokeAttack(Pokemon current,int atkInfo, Pokemon enemy){
		// This method is for the Pokemon's attack- it checks if the pokemon has a special or not
		// for attacks that have worked- it calls the atk calculation method; for specials- it calls the special method
		
		Attack pokeAtk = current.attacks.get(atkInfo);
		if(pokeAtk.special.equals(" ")){//case for no specials [blank]
			System.out.printf("%s used %s\n",current.name,pokeAtk.name);
		}
			
		if (successAtk == true){//if the attack was successful
			atkCalculation(current,atkInfo,enemy); //damage calculations
		}
				
		else{//case for special attacks
			specials(current,atkInfo,enemy);
		}

	}

	public void specials(Pokemon current,int atkInfo,Pokemon enemy){//current us the current pokemon
		//A method which checks for all special attacks-many of which have the 50% rate of succeeding
		Attack pokeAtk = current.attacks.get(atkInfo);
		//recharge ability	
		if(pokeAtk.special.equals("recharge")){// this is all based on the notepad file- depending if the selected pokemon has a special or not
			System.out.printf("%s has used %s\n",current.name,pokeAtk.name);
			recharge(current);//this adds 20 energy to the current pokemon
		}
		//Stun Special; it has a 50% success rate
		else if(pokeAtk.special.equals("stun")){
			System.out.printf("%s has used %s\n",current.name,pokeAtk.name);
			stun(current,enemy); //stuns the current enemy
		}
		//Wild Card Special- 50% success rate
		else if(pokeAtk.special.equals("wild card")){
			System.out.printf("%s has used %s\n",current.name,pokeAtk.name);
			//this case is for if the passfailresult is either True or False. If it fails, then the wild card special
			// doesn't happen
			if(passFailresult() == false){
				System.out.printf("%s has failed\n",current.name);
				successAtk = false;
				setEnergy(current,pokeAtk.energy);
			}
			else{
				System.out.println("Wild Card Special Successfull!\n");
				successAtk = true;//the attack is true and the pass/fail 50% chance can occur again to see if it's successful again
			}
		}
		//Disable special move
		else if(pokeAtk.special.equals("disable")){
			if(enemy.disabledPokemon == true){//can only be disabled once
				System.out.printf("%s has already been disabled\n",enemy.name);
			}
			
			else{
				System.out.printf("%s used %s\n%s has been disabled\n",current.name,pokeAtk.name,enemy.name);
				for(int i = 0; i < enemy.attacks.size(); i++){
					enemy.attacks.get(i).damage -= 10;//minimize all enemy atk damage by 10 points
					if(enemy.attacks.get(i).damage < 0){
						enemy.attacks.get(i).damage = 0;//attack damage can never go below 0.
					}
					disabledPokemon = true;
				}
			}
		}	
		//Wild Storm - 50% chance of move success rate and has a 50% chance of free wild storm which has no energy cost
		else if(pokeAtk.special.equals("wild storm")){
			setEnergy(current,pokeAtk.energy);
			if(passFailresult() == false){ //the move special fails on the very first attempt
				successAtk = false;
				System.out.printf("%s has failed to use %s\n",current.name,pokeAtk.name);
			}
			else{ //the special move has success on first attempt
				System.out.println("Wild Storm Activated!\n");
				usePokeEnergy = false;
				wildStormMOVE(current,atkInfo,enemy,true);
			}
		}	
	}
	
	public void wildStormMOVE(Pokemon current,int atkInfo,Pokemon enemy, boolean success){//based off first success
	//wildstorm method for the wildstorm special; what happens is that users have a percent of activating attack depending on a
	// 50% basis; if its successful on the first attack-> it can potentially "continue" as a 50% chance of it pass/failing occurs again
		Attack pokeAtk = current.attacks.get(atkInfo);
		if(enemy.hp > 0){ //enemy poke has not fainted
			if(success == true){
				successAtk = true;
				System.out.printf("%s used %s\n",current.name,pokeAtk.name);
				atkCalculation(current,atkInfo,enemy);	
				usePokeEnergy = false;  //only use energy on first wild storm attack
				wildStormMOVE(current,atkInfo,enemy,passFailresult());//this allows another wildstorm to be issued with 50% pass/fail rate
			}	
			else{ //atk not successful- ability ends and successatk is set to false
				System.out.println("Wild Storm move has ended\n");
				successAtk = false;//atk no longer successful
			}
		}
		else{ //enemy poke fainted; move ends
			System.out.println("Wild Storm has ENDED\n"); //user notification
			successAtk = false;  //wildstorm stops after enemy Pokemon faints
		}
	}
	
	public void atkCalculation(Pokemon current,int atkInfo,Pokemon enemy){
		// The method which completes damage calculations as well as effectiveness
		Attack pokeAtk = current.attacks.get(atkInfo);
		int damage = pokeAtk.damage;
		if(enemy.hp <= 0){
			enemy.hp = 0; //keeps the healthpoints from going negative for enemy
		}
		if (current.hp <= 0){
			current.hp = 0; //keeps the hp for user from going negative
		}
		if(successAtk == true){
			if(current.type.equals(enemy.resist)){			
				System.out.println("It is not very effective\n");
				damage /= 2; //divide damage in half
			}
			else if(current.type.equals(enemy.weakness)){
				System.out.println("It is very effective!\n");
				damage *= 2; //double damage given
			}
			enemy.hp -= damage; //remove hp by the amount of damage issued
			if(usePokeEnergy == true){
				setEnergy(current,pokeAtk.energy); //this is the leftover energy after performing the attack
			}
		}			

		//this displays the current stats of the pokemons in the battle with nice formatting
		System.out.println("\t\t\tCurrent Pokemon Stats: \n");
		System.out.println("-------------------------------------------------------------------------");
		System.out.printf("\t%-11s: HP: %-3d Energy: %-4d\n",enemy.name,enemy.hp,enemy.energy);
		System.out.printf("\t%-11s: HP: %-3d Energy: %-4d\n",current.name,current.hp,current.energy);
		System.out.println("-------------------------------------------------------------------------");
	}
	
	public void setEnergy(Pokemon current,int value){// current is the pokemon's current energy and value is the energy cost of move/attack
		//does the energy calculation after an attack is used
		current.energy -= value;
		if(current.energy <= 0){ //this prevents negative energy
			current.energy = 0;
		}
	}

    public void recharge(Pokemon pokeMON){//used for "recharge" special
    /* The recharge method for the recharge move.
     */
    	pokeMON.energy += 20; // add 20 energy to the pokemon which used it
    	if(pokeMON.energy > 50){ //if the pokeMON's energy passes 50, sets it back to 50 
			pokeMON.energy = 50;
		}
		System.out.printf("%s has recovered 20 energy points\n",pokeMON.name);//tell user the status change
    }

    public void stun(Pokemon myPokemon,Pokemon enemy){//Method for "stun" special
    /* This method is for the stun move; Tells user if pokemon fainted to stun or stunned.
     * It uses boolean flags to help determine whether each condition is true or false; same thing as pass fail as well as start method
     *Takes the result from the random 50% generator and determines whether a stun is successful or not
     */
    	if(passFailresult() == true){ //passfail result is the flag for the 50% random method that randomly chooses an outcome
    		enemy.pokemonStunned = true;
			System.out.printf("%s has been stunned, %s pass\n", enemy.name, enemy.name);
		}
		else{
			System.out.printf("%s failed to stun %s\n",myPokemon.name,enemy.name);
			pokemonStunned = false;
		}
    }
   
 	public static boolean pokemonFaint(Pokemon pokeMON){
 		/* The method for pokemon fainting. Uses a boolean flag to mark if its true or false
 		 */
		boolean faint = false;
		if(pokeMON.hp <= 0){//checks if pokemon has fainted; if health is less or equal to 0, pokemon has fainted
			faint = true;
		}
		return faint;
	}

	public boolean pokeHasEnergy(Pokemon current, int atkInfo){
		//the method which checks if the pokemon has enough energy and it sets the has_energy flag either to true or false depending on the attack cost
		boolean pokeHasEnergy = false;
		Attack pokeAtk = current.attacks.get(atkInfo);
		if(current.energy >= pokeAtk.energy){//checks if pokemon has enough energy to do attack
			pokeHasEnergy = true;				// pokemon has enough energy to perform selected attack
		}
		return pokeHasEnergy;
	}
	
	public boolean passFailresult(){
		/* This method is for choosing a random outcome. True OR False. Same function as the "Start method" in the class: pokemonarena
		 */
    	boolean pass = false; //random 50% outcome trial
    	Random number = new Random();
		int resultvar = number.nextInt(2);//50% chance for the move/attack
		if(resultvar == 0){
			pass = true;
		}
		return pass;
    }
}