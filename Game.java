/*
										IMPORTANT NOTICE

	YOU NEED JAVA 1.8 TO COMPILE THIS FILE DUE TO LINE 173 AND 206 NOT WORKING IN JAVA 1.7 AND EARLIER.

						This file was tested and compiled on Javac 1.8.0_31

*/

import java.net.*;
import java.io.*;
import java.util.*;
/*
	This file is the game, everytime a client enters something it runs this
	only certain methods are called depending on lots of variables and conditons.
	Most methods only run once per user and so processing power is not wasted.
	"theOutput" is used to display information on a per client basis, while System.
	out.print is used to display things on the server cmd for both players to see.
*/
public class Game {

	/*
		Static variables are shared amonst players and act as a way 
		to convey information between clients.
	*/
	private static ArrayList<Integer> totalInGame = new ArrayList<Integer>();
	private static ArrayList<Integer> tokensInCentre = new ArrayList<Integer>();
	private static ArrayList<Integer> playerGuesses = new ArrayList<Integer>();
	private static Object someObject = new Object();
	private static Object someOtherObject = new Object();
	private static int totalPlayers = 0;
	private static boolean a = true;
	private static int currentTurn = 1;
	private static int b = 0;
	private static int c = 0;
	private static boolean moreThanOnePlayer = false;
	private static Boolean playerGuess = false;
	private static Boolean checkForWinner = false;
	private int winningNum = 0;

	/*
		These normal variables are different from player to player 
		and they do not affect the others in the game.
	*/
	private String theOutput = "";
	private Boolean givenID = false;
	private Boolean doneTurn = false;
	private Boolean guessing = false;
	private int id = 0;
	private int tokenPlaced = 0;
	private int tokensLeft = 5;
	private int sum = 0;
	private int guess = 0;

	/*
		This method is run everytime a client presses enter, it does 5
		if checks and any associated methods and then returns "theOutput".
	
	*/
 	public String computeGame(String input){ 

	 	if(givenID == false){
	 		assignIDs();
		}
		if(b == 0){
			enoughPlayers();
		}
		if(doneTurn == false && moreThanOnePlayer == true){
			doTurn(input);
		}
		if(guessing == false && playerGuess == true){
			doGuesses(input);
		}
		if(checkForWinner == true){
			checkWinner();
		}
		return theOutput;
	}

	/*
		Assigns ID's to each of the players, the ID's correspond to the 
		players number. E.g Player 1 has ID 1. After the first game it 
		also switches the player ID's around to let the other player go 
		first. Variables between the players are not swapped.
	*/
	public synchronized void assignIDs(){
		if(id == 1){
			id = 2;
			totalPlayers = 2;
		} else if(id == 2){
			id = 1;
			totalPlayers = 2;
		}else{
			totalPlayers++;
			id = totalPlayers; 
		}
		if(id > 2){
			System.out.print("A player tried to connect to the game and was rejected.");
			theOutput = "You cannot join, the game is full.";
		}
		createLine();
		givenID = true;
		System.out.println("Player " + id + " connected");
		System.out.println("There are " + totalPlayers + " player(s) in the game");
	}

	/*
		Checks if there are more than one players in the game. In this method
		I am able to use wait(); to hold the first player while the second player 
		still hasn't entered the game. This method also only runs once.
	*/
	public synchronized void enoughPlayers(){
		synchronized(someObject){
			if(totalPlayers <= 1){
				System.out.println("We can't start yet. We need 2 players in the game.");
    			try{
    				someObject.wait();
    			}catch(InterruptedException e){ }
			} else {
  			synchronized(someObject){
				someObject.notifyAll();
			}
  			moreThanOnePlayer = true;
			b++;
 			}
		}
	}

	/*
		This method handles each of the players "placing" down tokens. If it is the 
		players turn they can choose to place down a number of tokens (not able to 
		be seen by the other player), if it is not their turn they cannot make their 
		turn until the other player has. I cannot use wait() here due to the fact that 
		I need the method to finish for "theOutput" to display.
	*/
	public synchronized String doTurn(String input){
		theOutput = "You are Player " + id + ". You have " + tokensLeft + " tokens, how many do you want to place down?";
		if(id == currentTurn){
			try{
				tokenPlaced = Integer.parseInt(input);
				if((tokenPlaced <= tokensLeft)&&(tokenPlaced >= 0)){	
					createLine();
				  	tokensInCentre.add(tokenPlaced);		  			
				  	totalInGame.add(tokensLeft);
					theOutput = "Thank you. Other players are now processing their moves.";
					System.out.println("Player " + currentTurn + " placed down a number of tokens.");
					doneTurn = true;
					if(currentTurn >= totalPlayers){
						playerGuess = true;
					}
					currentTurn++;
				} else {
					theOutput = "You can't place down that many, you only have " + tokensLeft + ".";
				}
			} catch(NumberFormatException e){ }
		} else {
			theOutput = "You are Player " + id + ". It's not your turn yet, please wait for others to do their move.";
		}
		return theOutput;
	}

	/*
		This method handles the players guessing how many coins are in the centre. As with the last 
		method, if it is not a players turn they have to wait for the other player to do their move
		before continuing, and I cannot use wait() due to needing the method to finish for "theOutput" 
		to display.
	*/
	public synchronized String doGuesses(String input){
		if(a == true){
			resetTurn();
		}
		if(id == currentTurn){
			sum = totalInGame.stream().mapToInt(Integer::intValue).sum();
			theOutput = "There are a total of " +  sum + " tokens in play. How many do you think have been put forward in total?"; 
			try{
				guess = Integer.parseInt(input);
				if(playerGuesses.contains(guess)){
			  		theOutput = "That number has already been guessed.  Here is a list of all the guesses = " + playerGuesses;
			  	} else {
			  		playerGuesses.add(guess);
			  		createLine();
			  		theOutput = "You guessed " + guess + ".";
			  		System.out.println("Player " + currentTurn + " guessed " + guess + ".");
			  		guessing = true;
			  		if(currentTurn == totalPlayers){
						checkForWinner = true;
					}
					currentTurn++;
			  	}
			}catch(NumberFormatException e){ }

		} else {
			theOutput = "It's not your turn to guess yet, please wait for others to do their move.";
		}
		return theOutput;
	}

	/*
		This mathod checks who's the winner, if anyone is is deducts their tokensLeft by 1
		and then restarts the round. If someones tokensLeft is equals 0, then it ends the 
		game.
	*/
	public synchronized String checkWinner(){
		createLine();
		System.out.println("We are calculating whether anyone is the winner.");
		sum = tokensInCentre.stream().mapToInt(Integer::intValue).sum();
		if(playerGuesses.contains(sum)){
			winningNum = playerGuesses.indexOf(sum);
			winningNum++;
			if(winningNum == id){
				System.out.println("Player " + id + "won this round!");
				tokensLeft--;
				if(tokensLeft == 0){
					System.out.println("Player " + id + " won the game!");
					System.out.println("Exiting Game.");
					try{
						Thread.sleep(1000);
					}catch(InterruptedException e){ }
					System.exit(0);
				}
			}
			holdPlayers();
		} else {
			if(c == 0){
				try{
					Thread.sleep(1000);
				}catch(InterruptedException e){ }
				System.out.println("Nobody guessed right! There was " + sum + " tokens.");
				theOutput = "Nobody guessed right! There was " + sum + " tokens.";
				holdPlayers();
			}
		}
		return theOutput;
	}

	/*
		This method creates a line in the Server cmd line. It's here mainly to make the 
		other methods look a bit cleaner.
	*/
	public synchronized void createLine(){
		System.out.println("----------------------------------------------------");
	}

	/*
		This method resets the currentTurn to 1.
	*/
	public synchronized void resetTurn(){
		currentTurn = 1;
		a = false;
	}

	/*
		This method holds the players in a wait() and only releases when a second player 
		runs this method. It's in it's own method to save space as it's run in 2 seperate 
		bits of the program.
	*/
	public synchronized void holdPlayers(){
		synchronized(someOtherObject){
			if(c == 0){
				c++;
				try{
	    			someOtherObject.wait();
	    			c = 0;
	    		}catch(InterruptedException e){ }
			}
		}
		synchronized(someOtherObject){
			someOtherObject.notifyAll();
			resetTurn();
			createLine();
			resetAll();
		}
	}

	/*
		This method resets all the important variables to the default state and
		empties the arrays.
	*/
	public synchronized void resetAll(){
		totalPlayers = 0;
		a = true;
		currentTurn = 1;
		b = 0;
		moreThanOnePlayer = false;
		playerGuess = false;
		checkForWinner = false;
		theOutput = "";
		givenID = false;
		doneTurn = false;
		guessing = false;
		tokenPlaced = 0;
		sum = 0;
		guess = 0;
		totalInGame.clear();
		tokensInCentre.clear();
		playerGuesses.clear();
	}
}