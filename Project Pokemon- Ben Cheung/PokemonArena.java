/**
 * @(#)PokemonArena.java
 * The class file for the entire pokeon arena- pokemon text file io, picking the pokemon and ranomly generating the matches
 * calls the methods in the Pokemon and Attack Classes
 * This is the file that the user runs in order to play the Pokemon Arena simulation. What happens is that the user gets to see a large list of
 * Pokemon choices that are loaded and organized into  ArrayLists from the "pokemon.txt" file. It will continue to prompt the user to select 4 DIFFERENT Pokemon
 * Once the selection process is over, all the unchosen Pokemon are enemy Pokemon and the user must battle the Pokemon and defeat each one to win the title
 * <Trainer Supreme>. each Pokemon is randomly chosen to battle, randomly choosese moves they use and the order in which the Pokemons move [goes first or second] is also based on a 50%  probability]
 * The user is presented with 3 options after choosing a valid option from their Pokemon team: 1. Attack 2. Retreat[Switch with another Pokemon] 3. Pass[ Recover Energy]
 * If the user chooses Attack, the moves show up and allow the user to pick a valid move. These Pokemon exchange moves till the other is defeated.
 * If all the user's pokemon[4 pokemon] faint- the game is over and the user loses.
 *
 * @author Ben Cheung
 * @version 1.00 2014/12/5
 */
import java.io.*;
import java.util.*;

public class PokemonArena {  
	public static final int NAME=0, HP=1, TYPE=2, RESISTANCE=3, WEAKNESS=4, NUMATTACK=5;
	static ArrayList<Pokemon>allPokemons = new ArrayList<Pokemon>();// holds all pokemons
	static ArrayList<Pokemon>chosen = new ArrayList<Pokemon>();//holds user's choice  pokemons
	static ArrayList<Pokemon>enemyPokes = new ArrayList<Pokemon>();//holds all the enemyPokes that are not the user's choice\
	
	static Scanner Kb = new Scanner(System.in); // set scanner variable from java util
	
  	public static int checkValid(int range1, int range2, int userinput){ //this method is vital to help speed up the coding along with helping it check if each input is valid 
  		/* This method is used to check if the user's input is valid. 
  		 *Example: If it's valid it returns the user's choice
  		 * "yes or no"; if not it returns 0 which will tellthe choosePokemon class to tell the user to enter a valid option
  		 * Everytime the user needs to input a value for an answer, this valid checker checks if the input is valid
  		 */
  		if(userinput > range2){//if user input is not within range
  			return 0;
  		}
  		if(userinput < range1 ){//if user input is not within range
  			return 0;
  		}
  		else{
  			return userinput; //if the choice is valid
  		}
  	}
  		
	public static int start(){; 
		/* This is a method for choosing randomly which Pokemon goes first- 50% chance*/
		Random number = new Random();
		int moveoption = number.nextInt(2);//if 0, user start, if 1, enemy starts
		return moveoption;
	}

  	public static void main(String[]args){
  		/*This method is to load the file, read it and split the information by the ",". It then splits and adds 
  		  the information to the appropriate sections based on the final integer variable value. Example: The NAME is position 0 
  		  and is added to the allPokemons arraylist as well as the enemyPokes arraylist */
  		  
    	try{ //time to open the file and read it
    		Scanner pokeFile = new Scanner(new FileReader("pokemon.txt")); //reads file called pokemon.txt
    		int number = Integer.parseInt(pokeFile.nextLine());   //# of lines in the textfile
		    while(pokeFile.hasNextLine()){	//splits the pokemon text file	
		    	String line = pokeFile.nextLine(); //place each line into the String line variable
		    	ArrayList<Attack> attacks = new ArrayList<Attack>();//Array for the possbile pokemon attacks
		    	String[]poke = line.split(","); //split at the ","
		    	
		    	int numAtt = Integer.parseInt(poke[NUMATTACK]); 
		    		
				for(int i = 1; i <= numAtt; i++){
					int valnum = i*4;
		    		Attack pokeAtk = new Attack(poke[valnum+2],Integer.parseInt(poke[valnum+3]),Integer.parseInt(poke[valnum+4]),poke[valnum+5]);
		    		// the poke[i*4+2] , etc. is for obtaining all the info from the attack and adding each attack info selection into a specific "Attack" group in the list
		    		// EXAMPLE: Gyarados,100,water,leaf,earth,2,Dragon Rage,30,50, ,Bubblebeam,40,40,disable
		    		// Given the example, the poke[i*4+2],poke[i*4+3],poke[i*4+4],poke[i*4+5] get the "2,Dragon Rage,30,50"
		    		// As "i" increases- the next Attack will be added to the list
		    		attacks.add(pokeAtk); //adds all attacks for the pokemon to the attacks array
				}
				Pokemon info = new Pokemon(poke[NAME],Integer.parseInt(poke[HP]),poke[TYPE],poke[RESISTANCE],poke[WEAKNESS], attacks);
				enemyPokes.add(info);//adds pokemon for future use that are not the user's choice
				allPokemons.add(info);//adds all pokemon info	
	    	}
    	}
    	catch(IOException ex){
    		System.out.println(ex);	//if the file isn't there.
    	}
    	//Welcome message and user prompt and nice space formatting and layout
	    System.out.println("\n----------------------------Welcome to Pokemon Arena----------------------------\n\t\t\t\t\t Please select 4 pokemon for your team\n");
	    for(int i = 1; i <= 4; i++){
	    	System.out.printf("Pokemon Choice Number %d: \n",i);
	    	System.out.println("--------------------------------------------------------------------------------");
	    	pokeCHOOSE(allPokemons);//calls the choose Pokemon method and places the allPokemons ArrayList in it
	    }
	    /*Next steps of codes focus on matching the user's pokemon with the enemyPokes which are randomly paired with the user's pokemon.
	      It shuffles the enemy pokemon array list; then it displays which pokemon it has to battle. If the checkIFgameOver method is True;
	      then the game is finished and the program breaks
	      */
	      
	    //The shuffle(List<?>) method is used to randomly permute the specified list using a default source of randomness.
	 	Collections.shuffle(enemyPokes);         //enemy pokemon battles are random; this shuffles all the pokemon in the enemyPoke Arraylist
		for(int a = 0; a < enemyPokes.size(); a++){
			String e = enemyPokes.get(a).name;
			System.out.printf("\n A Wild %s has appeared! \n",e);
			battle(a);			//battle every single enemy pokemon
			
			if(checkIFgameOver() == true){//this is the condition for if all user pokemon have fainted
				System.out.println("All your pokemon have fainted, game over- thanks for playing!");
				break ;
			}
			
			else{ //this heals all non-fainted pokemon by 20 health points after each battle
				for(int f = 0; f < 4; f++){ //f is for faint
					int c = chosen.get(f).hp;//c for chosen poke 
					if(c > 0){ //this function heals all the pokemon that have health over zero
						c += 20; //heals 
						
						if(c > chosen.get(f).initialHp){//this limits the pokemon from more health points then it had initally.
							c = chosen.get(f).initialHp; //sets it back to full health if the hp is greater than the max hp.
						}	
					}				
				}
			}
			System.out.println("\nAll your Non-Fainted Pokemon have regained 20 Health points \n"); //tell the user that all pokemon given 20 hp 
		}
		if(checkIFgameOver() == false){// this condition is only if the user has won the game and defeated all enemy pokemon
			System.out.println("Congratulations, All enemy pokemons have been defeated.You have beaten them all and now have earned the title [Trainer Supreme]! \n"); 
		}		
  	}

  	public static void pokeCHOOSE(ArrayList<Pokemon>allPokes){
  	/* this method is the entire user pokemon choosing system. It displays a nice looking list of choices. It first makes sure that the user's choice is a valid choice. The user choses
  	 * a pokemon by its designated number. If that number that the user inputs does not match a pokemon's number (not in the range); it tells the
  	 * user to pick a valid option. Once the user selects the valid option(s), then it displays all the attacks of that pokemon. It then prompts the
  	 * user to confirm the choice and if yes- adds it to the chosen pokemon array; if no- then it recalls the entire method
  	 */
		for(int i = 0; i < allPokemons.size(); i += 4){//loop which adds 4 while i is less than the total amount of elements in the allPokemons Arraylist
			if(allPokemons.size() > 3){
				System.out.printf("%-2d  %-12s \t%-2d  %-12s \t%-2d  %-12s \t%-2d  %-12s \n",(i+1),allPokes.get(i).name,(i+2),allPokes.get(i+1).name,(i+3),allPokes.get(i+2).name,(i+4),allPokes.get(i+3).name);
				System.out.println("--------------------------------------------------------------------------------");
			}//display all pokemon names wih nice string formatting
		}
		int choice = Kb.nextInt();
		//The java.util.ArrayList.size() method returns the number of elements in this list i.e the size of the list.
		if(checkValid(1,allPokemons.size(),choice) == 0){  //if the user input fails the valid checker then it prompts user to choose a valid option
			System.out.println("\n Please choose a pokemon listed \n");
			pokeCHOOSE(allPokemons);
		}
		else{
			System.out.println("Pokemon Attacks:\n");
			int cho = choice - 1;
			for(int i = 0; i < allPokemons.get(cho).attacks.size() ; i++){ //display the  pokemon's attacks
				System.out.println(allPokemons.get(cho).attacks.get(i).toString()); //it gets the string formatting from "toString" from the Attack class
			}
			
			System.out.println("\n Are you sure you want to use this Pokemon? \n 1.yes \t 2.no \n"); //prompt the user and ask if they are sure
			int ans = Kb.nextInt();
			
			while(checkValid(1,2,ans) == 0){ //makes sure that option is valid based on the method "checkValid"
				System.out.println("\n Please choose a valid option");
				System.out.println("\n Are you sure you want to add this pokemon to your party? \n 1.yes \t 2.no \n");
				ans = Kb.nextInt(); //will keep looping until user decides to cooperate and pick a valid option
			}
			
			if(ans == 2){ //this means that the user's answer was "no" and it will recall the choosePokemon method so allow the user to choose a different one
				pokeCHOOSE(allPokemons);
			}		
			if(ans == 1){ //this means that the user's answer was "yes"
				int repeat = choice - 1;
				if(chosen.contains(allPokemons.get(repeat))){//checks for repeat choices
					System.out.println("\n You may not choose the SAME pokemon twice!\n"); //tells user that no repeats are allowed
					pokeCHOOSE(allPokemons); //recall the choose pokemon method
				}
				int repeat2 = choice - 1;
				chosen.add(allPokemons.get(repeat2));//add chosen pokemons to the chosen list which will be their pokemon "party"
				enemyPokes.remove(allPokemons.get(repeat2));//remove chosen pokemon from enemyPokes list which will mean that the pokemon will not fight itself
			}

		}
	}
	
	public static int choosePoke(ArrayList<Pokemon>chosen){//choose one of 4 pokemon to use in battle
	/* This is the choosing Pokemon method which displays the pokemon chosen in battle against the enemy Pokemon.
	 **/
		System.out.println("Which Pokemon do you wish to use?\n");
		System.out.printf("\n1. %-5s \t\t 2. %-5s \t\t 3. %-5s \t\t 4. %-5s\n",chosen.get(0).name,chosen.get(1).name,chosen.get(2).name,chosen.get(3).name);
		int pokemn = Kb.nextInt();//this is which pokemon the user selects in the form of an integer value
		
		while(checkValid(1,4,pokemn) == 0){
			System.out.println("\tChoice INVALID! Please choose from the listed pokemons\n");
			System.out.printf("\n1. %-5s \t\t 2. %-5s \t\t 3. %-5s \t\t 4. %-5s\n",chosen.get(0).name,chosen.get(1).name,chosen.get(2).name,chosen.get(3).name);
		
			pokemn = Kb.nextInt();//this is which pokemon the user selects in the form of an integer value
		}
		return pokemn - 1;	
	}
	
	public static void battle(int currentenemy){// currentenemy = current enemy in battle
	/* This method is where the battle takes place between the user and the enemy pokemon- it randomly selects a pokemon from the
	 * enemy arraylist and matches it with the pokemon chosen by the user. Here the method checks to make sure the pokemon can battle [not fainted]
	 * and displays which moves have been used, which actions were done, etc. [Example: pass, retreat, etc.] as well as all status issues [example: stun]
	 * It basically takes in all the values of the Pokemon class by calling the methods located in that class and displays the results accordingly.
	 * Example: User Pokemon's health is under zero- > displays that the user pokemon has fainted and can no longer be used in battle; if the user 
	 * selects that fainted pokemon- this method will tell them to pick another one. 
	 **/
		
		Pokemon enemyPokemon = enemyPokes.get(currentenemy); //the enemy pokemon that the user will need to battle with
		int pokemn = choosePoke(chosen); //tell user to select pokemon to battle with
		Pokemon myPokemon = chosen.get(pokemn);
		
		
		if(myPokemon.pokemonFaint(myPokemon) == true){
			System.out.printf("%s has already fainted, please choose another non-fainted pokemon\n",myPokemon.name);
			battle(currentenemy);
		}
		
		else{		//user poke has not fainted
			System.out.printf("%s, I choose you! \n",myPokemon.name);
			
			if(start() == 0){		//user starts	
				System.out.printf("\n\t\t - User Starts - \n");
				while(myPokemon.pokemonFaint(myPokemon) == false){
					
					if(myPokemon.pokemonFaint(myPokemon) == false){
						if (enemyPokemon.pokemonFaint(enemyPokemon) == false){ //if they both don't faint- recover energy
							recoverEnergy();
						}
					}
					if(enemyPokemon.pokemonFaint(enemyPokemon) == false){//battle does not continue after the enemy pokemon faints
						myPokeAtk(myPokemon,enemyPokemon,currentenemy);
						
						if(enemyPokemon.pokemonStunned == true){
							recoverEnergy();
							enemyPokemon.pokemonStunned = false;//resets stun ability flag
							myPokeAtk(myPokemon,enemyPokemon,currentenemy);
						}
						else{
							enemyAtk(myPokemon,enemyPokemon,currentenemy);
							if(myPokemon.pokemonFaint(myPokemon) == true){
								System.out.printf("%s has fainted\n",myPokemon.name);
								
								if(checkIFgameOver() == true){  //if all user pokemons have fainted
									break;
								}
								else{
									recoverEnergy();
									battle(currentenemy);//choose another Pokemon to battle with
								}
							}														
						}
						if(enemyPokemon.pokemonFaint(enemyPokemon) == true){
							recoverEnergy(); //enemy pokemon faints message
							System.out.printf("%s has fainted\n",enemyPokemon.name);
							break;
						}	
					}
										
				}				
								
			}
			else{	//This means that the enemy Pokemon starts.	
				System.out.printf("\n\t\t - CPU Starts - \n");					
				while(enemyPokemon.pokemonFaint(enemyPokemon) == false){ //while Enemy Pokemon has not fainted
					int enemyattack = enemyPokemon.randomAtk(enemyPokemon); //choose random attack/move
					
					if(enemyPokemon.pokemonFaint(enemyPokemon) == false){
						if(myPokemon.pokemonFaint(myPokemon) == false){
							recoverEnergy(); //if both Pokemon haven't fainted; recover energy for both
						}
					}	
					if(myPokemon.pokemonFaint(myPokemon) == true){
						System.out.printf("%s has fainted\n",myPokemon.name); //tell user that the user pokemon has fainted
						if(checkIFgameOver() == true){
							break;
						} //game over is true
						else{
							recoverEnergy(); //if game over is not true, recover all the poke's energy and call battle fucntion
							battle(currentenemy);
						}
					}		


					else{
						myPokeAtk(myPokemon,enemyPokemon,currentenemy);

						if(enemyPokemon.pokemonFaint(enemyPokemon) == true){
							System.out.printf("%s has fainted\n",enemyPokemon.name);//tell user that enemy pokemon has fainted
							recoverEnergy(); //recover the energy
						}
					}
					if(enemyattack == -1 || enemyattack <= -1){ //Enemy Pokemon has no energy to perform any attack or move so they pass
						System.out.printf("%s has no energy left to attack, %s passes\n",enemyPokemon.name,enemyPokemon.name);
						myPokeAtk(myPokemon,enemyPokemon,currentenemy);
						
						if(enemyPokemon.pokemonFaint(enemyPokemon) == true){ //if pokemon faints
							System.out.printf("%s fainted\n",enemyPokemon.name);
							break;
						}
						else{
							enemyAtk(myPokemon,enemyPokemon,currentenemy);
							
							if(myPokemon.pokemonFaint(myPokemon) == true){
								System.out.printf("%s has fainted\n",myPokemon.name);
								if(checkIFgameOver() == true){
									break;
								}
								else{
									recoverEnergy();
									battle(currentenemy);
								}
							}	
							if(enemyPokemon.pokemonStunned == true){ //if the enemy Pokemon is stunned, only recover energy and set the stunned flag to false
								recoverEnergy();
								enemyPokemon.pokemonStunned = false;					
								enemyAtk(myPokemon,enemyPokemon,currentenemy);
							}
													
						}
					}
					else{ 
						enemyPokemon.pokeAttack(enemyPokemon,enemyattack, myPokemon);
					}
				}
				
			}
		}
  	}
	
  	public static void recoverEnergy(){
  		/* Recover energy method which recovers the pokemon's energy by 10 up to a max of 50
  		 **/
  		int arraylistsizepoke = allPokemons.size();
		for(int i = 0; i < arraylistsizepoke; i++){
			allPokemons.get(i).energy += 10;// all pokemon regain 10 energy points
			if(allPokemons.get(i).energy > 50){//max energy regained does not exceed 50 hp
				allPokemons.get(i).energy = 50;
			}
		}
		System.out.println("All Pokemon have recovered 10 energy points\n");//notify the user
	} 
		
	public static boolean checkIFgameOver(){//checks to see if game is over
	// Confirms that all user Pokemon have no health and sets the boolean gameOver flag to true if so; else- it is false
		boolean checkIFgameOver = false;
		Pokemon myPokemon4 = chosen.get(3); //make variables for the chosen pokemon
		Pokemon myPokemon3 = chosen.get(2);
		Pokemon myPokemon2 = chosen.get(1);
		Pokemon myPokemon1 = chosen.get(0);
		if(myPokemon1.hp <= 0){// less than or equal  to 0 in case of error with health system
			if(myPokemon2.hp <= 0){
				if(myPokemon3.hp <= 0){
					if (myPokemon4.hp <= 0){
						checkIFgameOver = true;//if all user pokemons have fainted; then game over condition is true
					}
				}
			}
		}
		return checkIFgameOver;
	}   
	
	public static void myPokeAtk(Pokemon myPokemon, Pokemon enemyPokemon, int nxtPoke){
		/* If it's valid- it will display 
	 * that pokemon's options: Attack, Retreat, Pass. If user picks Attack-> displays the pokemon moves, 
	 *if user picks Retreat- switch Pokemons and display options, if user picks Pass, pokemon regains energy and does nothing.
	 *  nxtPoke is an integer representing next enemy pokemon
	 **/
		System.out.printf("What will %s do? \n", myPokemon.name);
		//display options for user
		System.out.printf("1.  Attack \t2.  Retreat \t3.  Pass\t");
		
		int option = Kb.nextInt(); // this is to keep track of users choice from 1->3 : 1. Attack, 2. Retreat, 3. Pass
		
		while(checkValid(1,3,option) == 0){ //only 3 options are avaliable (1 -> 3); this is for invalid choice
			System.out.println("Please choose a valid option.\n");
			System.out.printf("1. Attack \t2. Retreat \t3. Pass\t");
			option = Kb.nextInt();
		}
		if(option == 3){//pass (recover energy)
			System.out.printf("%s passes\n",myPokemon.name);
			enemyAtk(myPokemon,enemyPokemon,nxtPoke); //enemy's move
			//System.out.printf("\n%s has restored 10 energy points\n",myPokemon.name);
			recoverEnergy(); //recover all energy
			
		}
		if(option == 2){//retreat (choose another pokemon)
			System.out.printf("%s, come back!\n",myPokemon.name); //this is for changing pokemon
			battle(nxtPoke); //call battle for the next pokemon
		}
		if(option == 1){//attack option (assault the enemy Pokemon or use a move)
			for(int i = 1; i <= myPokemon.attacks.size(); i++){//print move options 
				System.out.println(i + ". " + myPokemon.attacks.get(i-1)); 
			}					
									
			System.out.printf("Which attack will %s use? \n",myPokemon.name);//choose attack
			int moveoptions = Kb.nextInt(); // this is the user's number for the attack/move they wish to use after they chose
			// the attack option "#1" for the "option variable
			
			while(checkValid(1, myPokemon.attacks.size(), moveoptions) == 0){ //if move is valid (Attack, Retreat,or pass)[in range from 1->3]
				System.out.println("Please choose a valid option\n");
				System.out.printf("Which attack will %s use? \n",myPokemon.name);
					moveoptions = Kb.nextInt();
			}
			
			if(myPokemon.pokeHasEnergy(myPokemon,moveoptions - 1) == false){ // if the user Pokemon has no energy
				System.out.printf("%s has no energy left to perform this attack/move\n",myPokemon.name);
				myPokeAtk(myPokemon,enemyPokemon,nxtPoke); 
			}
			else{//has energy to move/attack
				myPokemon.pokeAttack(myPokemon,moveoptions - 1,enemyPokemon);
				if(enemyPokemon.pokemonStunned == true){//enemy cannot attack since no energy
					enemyPokemon.pokemonStunned = false; //set the stun back to false so next turn may have possbility of moving
					recoverEnergy(); //recover all energy
					myPokemon.pokeAttack(myPokemon,moveoptions - 1,enemyPokemon);
				}							
			}				
		}
	}
	
	public static void enemyAtk(Pokemon myPokemon, Pokemon enemyPokemon, int currentenemy){
		/* This is for all the enemy moves and options that the enemy can perform
		 **/
		int enemyattack = enemyPokemon.randomAtk(enemyPokemon);//choose a random attack/move for enemy poke to use
		
		if(enemyattack == -1){//if enemy has no energy to perform any attack (pass)
			System.out.printf("%s has no energy left to attack, %s passes\n",enemyPokemon.name,enemyPokemon.name); //tell user that enemy pokemon passed; no energy is gained by this type of pass
			myPokeAtk(myPokemon,enemyPokemon,currentenemy); // allow  user pokemon to attack/move
		}
		else{ //this means the enemy pokemon has energy
			enemyPokemon.pokeAttack(enemyPokemon,enemyattack, myPokemon); //let enemy pokemon attack
			if(myPokemon.pokemonStunned == true){ 
				myPokemon.pokemonStunned = false; //for user stunned pokemon
				
				if(myPokemon.pokemonFaint(myPokemon) == true){ //if user pokemon faints
					System.out.printf("%s fainted\t",myPokemon.name); //tell user that their pokemon fainted
					if(checkIFgameOver() == false){ //if all user pokemon are still not all fainted
						recoverEnergy(); //recover all remaining poke's energy
						battle(currentenemy); //and call battle to battle current enemy pokemon
					}
				}
				else{//if user pokemon didn't faint
					recoverEnergy(); //recover
					enemyAtk(myPokemon,enemyPokemon,currentenemy);//commence enemy pokemon attack
				}
			}		
		}
	}
}