import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import java.util.*;

public class AlphaBetaBot {
	static Logger logger;
	static FileHandler errorLog;
	
	static boolean evenDepth;
		
 	AlphaBetaBot(){
		try{
        	logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		}
		catch(Exception io){
			System.exit(1);
		}
		
	}
	
	public State move(PlanetWars pw, int depth){
		double alpha = -Double.MAX_VALUE;
		double beta = Double.MAX_VALUE;
		double bestScore = -Double.MAX_VALUE;
		double bestScore2 = 1;
		State bestMove = null;
		SimulatedPlanetWars simulation = createSimulation(pw);
		
		for(Planet myPlanet: simulation.MyPlanets()){
			for(Planet notMyPlanet: simulation.NotMyPlanets()){
								
				SimulatedPlanetWars childpw = simulation.clone();
				childpw.simulateAttack(myPlanet, notMyPlanet);
				
				if(bestMove == null){
					bestMove = new State(simulation, myPlanet, notMyPlanet);
				}
		        alpha = Math.max(alpha, miniMax(childpw, depth-1, alpha, beta));

		        if(alpha>bestScore){

		        	bestMove = new State(childpw, myPlanet, notMyPlanet);
		        	bestMove.setValue(alpha);
		        	bestScore = alpha;
		        	if(bestScore==1){
		        		return bestMove;
		        	}
		        }
			}
		}
		return bestMove;	
		}
		
	private double miniMax(SimulatedPlanetWars currentState, int depth, double alphaVal, double betaVal){
			double alpha = alphaVal;
			double beta = betaVal;
			double bestScore;
			
			if(depth==0){
				return currentState.evaluateState();
			}
			if(evenDepth && depth%2==0 && depth!=0){ //Max turn
				bestScore = 0;
				if(currentState.Winner() == 1){
					return 1; //because I win
				}
				for(Planet myPlanet: currentState.MyPlanets()){ 
					for(Planet notMyPlanet: currentState.NotMyPlanets()){
						SimulatedPlanetWars simulation = currentState.clone();
						simulation.simulateAttack(myPlanet, notMyPlanet);
						simulation.simulateGrowth();
						
						alpha = Math.max(alpha, miniMax(simulation, depth-1, alpha, beta));
						if(alpha>=bestScore){//Maybe check a larger difference than if alpha is simply bigger than beta?
					
							bestScore=alpha;
							if(bestScore==1){
								return bestScore;
							}
						}
					}
				}
				return bestScore;
			}else if(!evenDepth && depth%2==0 && depth!=0){ //Min turn
				bestScore = 1;
				if(currentState.Winner() == 2){
					return 0; //because I win
				}
				for(Planet enemyPlanet: currentState.EnemyPlanets()){
					for(Planet enemyAttackPlanet: currentState.EnemyAttackPlanets()){
						SimulatedPlanetWars simulation = currentState.clone();
						simulation.simulateAttack(2,enemyPlanet, enemyAttackPlanet);
						simulation.simulateGrowth();
						beta = Math.min(beta, miniMax(simulation, depth-1, alpha, beta));
						if(beta < bestScore){
							bestScore = beta;
							if(bestScore==0){
								return bestScore;
							}
						}
					}
				}
				return bestScore;	
			}else if(evenDepth && depth%2!=0){ //Min turn
				bestScore = 1;
				if(currentState.Winner() == 2){
					return 0; //because I win
				}
				for(Planet enemyPlanet: currentState.EnemyPlanets()){
					for(Planet enemyAttackPlanet: currentState.EnemyAttackPlanets()){
						SimulatedPlanetWars simulation = currentState.clone();
						simulation.simulateAttack(2,enemyPlanet, enemyAttackPlanet);
						simulation.simulateGrowth();
						beta = Math.min(beta, miniMax(simulation, depth-1, alpha, beta));
					if(beta < bestScore){
						bestScore = beta;
						if(bestScore==0){
							return bestScore;
						}
					}
				}
			}
			return bestScore;	
			}else{	//Max turn
				bestScore = 0;
				if(currentState.Winner() == 1){
					return 1; //because I win
				}
				for(Planet myPlanet: currentState.MyPlanets()){ 
					for(Planet notMyPlanet: currentState.NotMyPlanets()){
						SimulatedPlanetWars simulation = currentState.clone();
						simulation.simulateAttack(myPlanet, notMyPlanet);
						simulation.simulateGrowth();
						
						alpha = Math.max(alpha, miniMax(simulation, depth-1, alpha, beta));
						if(alpha>=bestScore){//Maybe check a larger difference than if alpha is simply bigger than beta?
					
							bestScore=alpha;
							if(bestScore==1){
								return bestScore;
							}
						}
					}
				}
				return bestScore;
			}
		}
	
	public void DoTurn(PlanetWars pw) {
				int depth = 2;
				if(depth%2==0){
					evenDepth = true;
				}else{
					evenDepth = false;
				}
				
				State result = move(pw,depth);//on larger maps we cannot handle depth=4
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
	
	/** Create the simulation environment. Returns a SimulatedPlanetWars instance.
	 * Call every time you want a new simulation environment.
	 * @param The original PlanetWars object
	 * @return SimulatedPlanetWars instance on which to simulate your attacks. Create a new one every time you want to try alternative simulations.
	 */
	public static SimulatedPlanetWars createSimulation(PlanetWars pw){
		return new SimulatedPlanetWars(pw);
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
								new AlphaBetaBot().DoTurn(pw);
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
	
}
