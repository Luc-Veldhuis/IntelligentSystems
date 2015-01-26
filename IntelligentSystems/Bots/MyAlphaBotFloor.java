
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import java.util.*;

/** Another smarter kind of bot, which implements a minimax algorithm with look-ahead of two turns.
 * It simulates the opponent using the BullyBot strategy and simulates the possible outcomes for any
 * choice of source and destination planets in the attack. The simulated outcome states are ranked by
 * the evaluation function, which returns the most promising one.
 * 
 * Try to improve this bot. For example, you can try to answer some of this questions. 
 * Can you come up with smarter heuristics/scores for the evaluation function? 
 * What happens if you run this bot against your bot from week1? 
 * How can you change this bot to beat your week1 bot? 
 * Can you extend the bot to look ahead more than two turns? How many turns do you want to look ahead?
 * Is there a smart way to make this more efficient?
 */

public class MyAlphaBotFloor {
	static Logger logger;
	static FileHandler errorLog;
	MyAlphaBotFloor(){
		try{
			//errorLog = new FileHandler("errorlog.txt");
			//SimpleFormatter formatter = new SimpleFormatter();  
        	//errorLog.setFormatter(formatter);
        	logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        	//logger.addHandler(errorLog);
		}
		catch(Exception io){
			System.exit(1);
		}
		
	}

	//Not to be confused with alphabet
	
	public State move(PlanetWars pw, int depth){
		double alpha = -Double.MAX_VALUE;
		double beta = Double.MAX_VALUE;
		double bestScore = -Double.MAX_VALUE;
		State bestMove = null;
		SimulatedPlanetWars simulation = createSimulation(pw);
		for(Planet myPlanet: simulation.MyPlanets()){
			for(Planet notMyPlanet: simulation.NotMyPlanets()){
								
				SimulatedPlanetWars childpw = simulation.clone();
				childpw.simulateAttack(myPlanet, notMyPlanet);
				
				if(bestMove == null){
					bestMove = new State(simulation, myPlanet, notMyPlanet); //or childpw instead of simulation
				}
		        alpha = Math.max(alpha, miniMax(childpw, depth-1, alpha, beta));

		        if(alpha>bestScore){

		        	bestMove = new State(childpw, myPlanet, notMyPlanet);
		        	bestMove.setValue(childpw.evaluateState());
		        	bestScore = alpha;
		        }
			}
		}
		pw.log("Check" + " " + bestMove.getValue());
		return bestMove;	
	}
		
		private double miniMax(SimulatedPlanetWars currentState, int depth, double alphaVal, double betaVal){//spw was State
			double alpha = alphaVal;
			double beta = betaVal;
			
			if(depth==0){
				currentState.pw.log(currentState.evaluateState());
				return currentState.evaluateState();
			}
			if(depth%2==0 && depth!=0){
				for(Planet myPlanet: currentState.MyPlanets()){// getSimulation().MyPlanets()){
					for(Planet notMyPlanet: currentState.NotMyPlanets()){//getSimulation().NotMyPlanets()){
						SimulatedPlanetWars simulation = currentState.clone();//.getSimulation();
						simulation.simulateAttack(myPlanet, notMyPlanet);
						simulation.simulateGrowth();
						
						alpha = Math.max(alpha, miniMax(simulation, depth-1, alpha, beta));
						if(alpha>=beta){ //Maybe check a larger difference than if alpha is simply bigger than beta?
							return alpha;
						}
					}
				}
				//simulation2.pw.log(alpha);

				return alpha;
			}
			else{
				for(Planet enemyPlanet: currentState.EnemyPlanets()){//getSimulation().EnemyPlanets()){
					for(Planet enemyAttackPlanet: currentState.NotMyPlanets()){//getSimulation().NotMyPlanets()){
						SimulatedPlanetWars simulation = currentState.clone();//.getSimulation();
						simulation.simulateAttack(2,enemyPlanet, enemyAttackPlanet);
						simulation.simulateGrowth();
						
						beta = Math.min(beta, miniMax(simulation, depth-1, alpha, beta));
					if(alpha>=beta){
						return beta;//alpha;
					}
				}
			}
			return beta;	
			}
		}
	
	
	
	/*

	public State findBestAttackPlanet(State state, int depth){
		if(depth==0){
			state.calculateValue();
			return state;
		}
		SimulatedPlanetWars simulation = state.getSimulation();
		State newState = new State(state);
		double value = -Double.MAX_VALUE;
		for(Planet myPlanet: simulation.MyPlanets()){
			for(Planet notMyPlanet: simulation.NotMyPlanets()){
				State tempState = new State(simulation, myPlanet, notMyPlanet);
				tempState.adjustPlanetWars();
				State worstState = findWorstDefendPlanet(tempState, depth-1);
				double worstValue = worstState.getValue();
				value=Math.max(value,worstValue);
				
				if(value>=newState.getBeta()){
					if (value==worstValue){
						return worstState;
					}else{
						return newState;
					}
=======
				if(worstValue > newState.getBeta()){
					break;
				}
				newState.setAlpha(Math.max(newState.getAlpha(),value));
				
				if(worstValue < value){
					newState.setSimulation(worstState.getSimulation());
					newState.setSource(myPlanet);
					newState.setDestination(notMyPlanet);
				

					newState.setBeta(worstState.getBeta());
					newState.setValue(worstValue);
				}
			}
		}
		return newState;
	}
	
	public State findWorstDefendPlanet(State state, int depth){
		if(depth==0){
			state.calculateValue();
			return state;
		}
		SimulatedPlanetWars simulation = state.getSimulation();
		State newState = new State(state);
		double value = -Double.MAX_VALUE;
		for(Planet enemyPlanet: simulation.EnemyPlanets()){
			for(Planet enemyAttackPlanet: simulation.EnemyAttackPlanets()){
				State tempState = new State(simulation, enemyPlanet, enemyAttackPlanet);
				tempState.adjustPlanetWars();
				State bestState = findBestAttackPlanet(tempState, depth-1);
				double bestValue = bestState.getValue();
				if(bestValue < newState.getAlpha()){
					break;
				}
				if(bestValue > value){
					newState.setSimulation(bestState.getSimulation());
					newState.setSource(enemyPlanet);
					newState.setDestination(enemyAttackPlanet);
					newState.setAlpha(bestState.getAlpha());
					newState.setBeta(Math.min(newState.getBeta(),bestValue));
					newState.setValue(bestValue);
					value = bestValue;
				}
			}
		}
		return newState;
	}
*/
	public void DoTurn(PlanetWars pw) {
		State result = move(pw,4);
		Planet source = result.getSource();
		Planet dest = result.getDestination();
		
		// Attack using the source and destinations that lead to the most promising state in the simulation
		if (source != null && dest != null) {
			pw.IssueOrder(source, dest);
		}
		else{
			logger.info("invalid planets");
		}
	}
	
	// don't change this
	public static void main(String[] args) {
		
		String line = "";
		String message = "";
		int c;
		try {
			while ((c = System.in.read()) >= 0) {
				switch (c) {
				case '\n':
					if (line.equals("go")) {
						PlanetWars pw = new PlanetWars(message);
						new MyAlphaBotFloor().DoTurn(pw);
						pw.FinishTurn();
						message = "";
					} else {
						message += line + "\n";
					}
					line = "";
					break;
				default:
					line += (char) c;
					break;
				}
			}
		} catch (Exception e) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			String stackTrace = writer.toString();
			System.err.println(stackTrace);
			System.exit(1); //just stop now. we've got a problem
		}
	}
	
	/**
	 * Create the simulation environment. Returns a SimulatedPlanetWars instance.
	 * Call every time you want a new simulation environment.
	 * @param The original PlanetWars object
	 * @return SimulatedPlanetWars instance on which to simulate your attacks. Create a new one everytime you want to try alternative simulations.
	 */
	public static SimulatedPlanetWars createSimulation(PlanetWars pw){
		return dummyBot.new SimulatedPlanetWars(pw);
	}
	
	
	/**
	 * Static LookaheadBot, used only to access SimulatedPlanetWars (DON'T CHANGE)
	 */
	static MyAlphaBotFloor dummyBot = new MyAlphaBotFloor();
	
	/**
	 * Class which provide the simulation environment, has same interface as PlanetWars 
	 * (except for Fleets, that are not used).
	 *
	 */
	public class SimulatedPlanetWars implements Cloneable{

		List<Planet> planets = new ArrayList<Planet>();
		public PlanetWars pw;
		public SimulatedPlanetWars(PlanetWars pw) {
			this.pw = pw;
			for (Planet planet: pw.Planets()){
				planets.add((Planet)planet.clone());
			}
		}
		
		public SimulatedPlanetWars(List<Planet> clonePlanets, PlanetWars pw){
			this.pw = pw;
			for(Planet planet: clonePlanets){
				planets.add(planet);
			}
		}
		
		public void simulateGrowth() {
			for (Planet p: planets){
				
				if(p.Owner() == 0)
					continue;
				
				Planet newp = new Planet(p.PlanetID(), p.Owner(), p.NumShips()+p.GrowthRate() , 
						p.GrowthRate(), p.X(), p.Y());
				
				planets.set(p.PlanetID(), newp);
			}
		}
		
		/**
		 * This function evaluates how promising a simulated state is.
		 * You can change it to anything that makes sense, using combinations 
		 * of number of planets, ships or growth rate.
		 * @param SimulatedPlanetWars pw
		 * @return score of the final state of the simulation
		 */
		public double evaluateState(){
			
			// CHANGE HERE
			
<<<<<<< HEAD:IntelligentSystems/Bots/MyAlphaBotFloor.java
			double myNumberOfShips = 0;
			double myGrowthRate = 0;
			double enemyNumberOfShips = 0;
			double enemyGrowthRate = 0;
=======
			int myNumberOfShips = 0;
			int myGrowthRate = 0;
			int enemyNumberOfShips = 0;
			int enemyGrowthRate = 0;
			int myPlanets = 1;
			int enemyPlanets = 1;
>>>>>>> 72595b03e997c6818647bd9b682d7c00f7afed5c:IntelligentSystems/Bots/MyAlphaBot.java
			for(Planet p:MyPlanets()){
				myNumberOfShips += p.NumShips();
				myGrowthRate += p.GrowthRate();
				myPlanets += 1;
			}
			for(Planet p:EnemyPlanets()){
				enemyNumberOfShips += p.NumShips();
				enemyGrowthRate += p.GrowthRate();		
				enemyPlanets +=1;
			}
<<<<<<< HEAD:IntelligentSystems/Bots/MyAlphaBotFloor.java
			double totalNumberOfShips = myNumberOfShips + enemyNumberOfShips;
			double totalGrowthRate = myGrowthRate + enemyGrowthRate;
			return (((double)(myGrowthRate*2+myNumberOfShips*8))/(totalGrowthRate*2+totalNumberOfShips*8));
			//return myNumberOfShips/enemyNumberOfShips;
=======
			int totalNumberOfShips = myNumberOfShips + enemyNumberOfShips;
			int totalGrowthRate = myGrowthRate + enemyGrowthRate;
			int i = 2;
			//return ( (NumPlanets()-myPlanets)*10000+(totalNumberOfShips - myNumberOfShips)*1 + (totalGrowthRate- myGrowthRate)*2);
			return (1-((double)(myGrowthRate*i+myNumberOfShips*(10-i))/(totalGrowthRate*i+totalNumberOfShips*(10-i))));
>>>>>>> 72595b03e997c6818647bd9b682d7c00f7afed5c:IntelligentSystems/Bots/MyAlphaBot.java
		}

		public void simulateAttack(int player, Planet source, Planet dest){
			
			if (source.Owner() != player){
				pw.log("ohoh");
				return;
			}
			
			
			// Simulate attack
			if (source != null && dest != null) {
						
				Planet newSource = new Planet(source.PlanetID(), source.Owner(), source.NumShips()/2 , 
						source.GrowthRate(), source.X(), source.Y());
				Planet newDest = new Planet(dest.PlanetID(), dest.Owner(), Math.abs(dest.NumShips()-source.NumShips()/2 ), 
						dest.GrowthRate(), dest.X(), dest.Y());
				
				if(dest.NumShips()< source.NumShips()/2){
					//change owner
					newDest.Owner(player);
				}
				
				planets.set(source.PlanetID(), newSource);
				planets.set(dest.PlanetID(), newDest);
			}


		}
		
		public void simulateAttack(Planet source, Planet dest){
			simulateAttack(1, source, dest);
		}
		
		
		public void simulateEnemyAttack(){
			Planet source = null;
			Planet dest = null;

			
			// (1) Apply your strategy
			double sourceScore = Double.MIN_VALUE;
			double destScore = Double.MAX_VALUE;
			
			for (Planet planet : planets) {
				if(planet.Owner() == 2) {// skip planets with only one ship
					if (planet.NumShips() <= 1)
						continue;
					
					//This score is one way of defining how 'good' my planet is. 
					double scoreMax = (double) planet.NumShips();
					
					if (scoreMax > sourceScore) {
						//we want to maximize the score, so store the planet with the best score
						sourceScore = scoreMax;
						source = planet;
					}
				}	
				
				// (2) Find the weakest enemy or neutral planet.
				if(planet.Owner() != 2){
					double scoreMin = (double) (planet.NumShips());
					//if you want to debug how the score is computed, decomment the System.err.instructions
		//			System.err.println("Planet: " +notMyPlanet.PlanetID()+ " Score: "+ score);
		//			System.err.flush();
					if (scoreMin < destScore) {
						//The way the score is defined, is that the weaker a planet is, the higher the score. 
						//So again, we want to select the planet with the best score
						destScore = scoreMin;
						dest = planet;
					}
				}
				
			}
			
			// (3) Simulate attack
			if (source != null && dest != null) {
				simulateAttack(2, source, dest);
			}

		}
		
		public List<Planet> Planets(){
			return planets;
		}
		
	    // Returns the number of planets. Planets are numbered starting with 0.
	    public int NumPlanets() {
	    	return planets.size();
	    }
		
	    // Returns the planet with the given planet_id. There are NumPlanets()
	    // planets. They are numbered starting at 0.
	    public Planet GetPlanet(int planetID) {
	    	return planets.get(planetID);
	    }
	    
	    // Return a list of all the planets owned by the current player. By
	    // convention, the current player is always player number 1.
	    public List<Planet> MyPlanets() {
			List<Planet> r = new ArrayList<Planet>();
			for (Planet p : planets) {
			    if (p.Owner() == 1) {
				r.add(p);
			    }
			}
			return r;
	    }
	    
	    // Return a list of all neutral planets.
	    public List<Planet> NeutralPlanets() {
		List<Planet> r = new ArrayList<Planet>();
		for (Planet p : planets) {
		    if (p.Owner() == 0) {
			r.add(p);
		    }
		}
		return r;
	    }

	    public List<Planet> EnemyAttackPlanets(){
	    	List<Planet> r = new ArrayList<Planet>();
	    	for(Planet p : planets){
	    		if(p.Owner() == 0 || p.Owner() == 1){
	    			r.add(p);
	    		}
	    	}
	    	return r;
	    }

	    // Return a list of all the planets owned by rival players. This excludes
	    // planets owned by the current player, as well as neutral planets.
	    public List<Planet> EnemyPlanets() {
		List<Planet> r = new ArrayList<Planet>();
		for (Planet p : planets) {
		    if (p.Owner() >= 2) {
			r.add(p);
		    }
		}
		return r;
	    }

	    // Return a list of all the planets that are not owned by the current
	    // player. This includes all enemy planets and neutral planets.
	    public List<Planet> NotMyPlanets() {
		List<Planet> r = new ArrayList<Planet>();
		for (Planet p : planets) {
		    if (p.Owner() != 1) {
			r.add(p);
		    }
		}
		return r;
	    }
	    
	    // Returns the distance between two planets, rounded up to the next highest
	    // integer. This is the number of discrete time steps it takes to get
	    // between the two planets.
		public int Distance(int sourcePlanet, int destinationPlanet) {
			Planet source = planets.get(sourcePlanet);
			Planet destination = planets.get(destinationPlanet);
			double dx = source.X() - destination.X();
			double dy = source.Y() - destination.Y();
			return (int) Math.ceil(Math.sqrt(dx * dx + dy * dy));
		}
	    
	    // If the game is not yet over (ie: at least two players have planets or
	    // fleets remaining), returns -1. If the game is over (ie: only one player
	    // is left) then that player's number is returned. If there are no
	    // remaining players, then the game is a draw and 0 is returned.
		public int Winner() {
			Set<Integer> remainingPlayers = new TreeSet<Integer>();
			for (Planet p : planets) {
				remainingPlayers.add(p.Owner());
			}
			switch (remainingPlayers.size()) {
			case 0:
				return 0;
			case 1:
				return ((Integer) remainingPlayers.toArray()[0]).intValue();
			default:
				return -1;
			}
		}

	    // Returns the number of ships that the current player has, either located
	    // on planets or in flight.
	    public int NumShips(int playerID) {
		int numShips = 0;
		for (Planet p : planets) {
		    if (p.Owner() == playerID) {
			numShips += p.NumShips();
		    }
		}
		return numShips;
	    }

	    

	    public void IssueOrder(Planet source, Planet dest) {
	    	simulateAttack(source,dest);
	    }
	    
	    public SimulatedPlanetWars clone(){
	    	List<Planet> copyList = new ArrayList<Planet>();
	    	for(Planet planet: planets){
	    		copyList.add((Planet)planet.clone());
	    	}
	    	SimulatedPlanetWars result = new SimulatedPlanetWars(copyList, pw);
	    	return result;
	    }
	    
	
	}

	public class State{
		private SimulatedPlanetWars sPw;
		private Planet source;
		private Planet destination;
		private double alpha;
		private double beta;
		private double value;

		public State(State state){
			sPw = state.sPw.clone();
			source = state.source;
			destination = state.destination;
			alpha = state.alpha;
			beta = state.beta;
			value = state.value;
		}

		public State(SimulatedPlanetWars sPw){
			this(sPw, null, null);

		}

		public State(SimulatedPlanetWars sPw, Planet source, Planet destination){
			this(sPw, source, destination, -Double.MAX_VALUE, Double.MAX_VALUE);
		}

		public State(SimulatedPlanetWars sPw, Planet source, Planet destination, double alpha, double beta){
			this.sPw = sPw.clone();
			if(source != null){
				this.source = (Planet)source.clone();
			}
			if(destination != null){
				this.destination = (Planet)destination.clone();
			}
			this.alpha = alpha;
			this.beta = beta;
			this.value = -Double.MAX_VALUE;
		}

		public Planet getSource(){
			return source;
		}
		public Planet getDestination(){
			return destination;
		}
		public double getAlpha(){
			return alpha;
		}

		public double getBeta(){
			return beta;
		}
		public double getValue(){
			return value;
		}

		public void calculateValue(){
			value = sPw.evaluateState();
		}
		public SimulatedPlanetWars getSimulation(){
			return sPw.clone();
		}
		public void adjustPlanetWars(){
			sPw.simulateAttack(source.Owner(), source, destination);
			sPw.simulateGrowth();
		}

		public void setSource(Planet source){
			this.source = source;
		}

		public void setDestination(Planet destination){
			this.destination = destination;
		}

		public void setSimulation(SimulatedPlanetWars simulation){
			this.sPw = simulation;
		}
		public void setAlpha(double alpha){
			this.alpha = alpha;
		}
		public void setBeta(double beta){
			this.beta = beta;
		}

		public void setValue(double value){
			this.value = value;
		}
	}
}
