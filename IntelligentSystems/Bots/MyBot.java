
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.*;

/*
 RandomBot - an example bot that picks up one of his planets and send half of the ships 
 from that planet to a random target planet.

 Not a very clever bot, but showcases the functions that can be used.
 Overcommented for educational purposes.
 */
public class MyBot {
	
	static Planet startingPlanet;


	/*
	 * Function that gets called every turn. This is where to implement the strategies.
	 */

	public static void DoTurn(PlanetWars pw, int counter) {
		
		if(counter == 0){
			//FIRST MOVE, VERY IMPORTANT FOR GAME
			startingPlanet = pw.MyPlanets().get(0);
			startPlanet(pw);
			return;
		}
		int neutralPlanets = pw.NeutralPlanets().size();
		
		if(true){
			attackNeutralBot(pw);
			return;
		}
		
		int totalPlanetSize = 0;
		for (Planet p : pw.NeutralPlanets()) {
			totalPlanetSize += p.GrowthRate();
		}
		int averagePlanetSize = totalPlanetSize;
		if(neutralPlanets != 0){
			averagePlanetSize = Math.round(totalPlanetSize/pw.NeutralPlanets().size());
		}
		if(neutralPlanets != 0){
			averagePlanetSize = Math.round(totalPlanetSize/pw.NeutralPlanets().size());
		}
			
		//Use AdaptivityMap to get the bot which matches the current environment characteristics  
		String thisTurnBot = AdaptivityMap.get(neutralPlanets, averagePlanetSize);		
		
		if (thisTurnBot == null) {
			System.err.println("WARNING: You have not entered bot data for this case. Using default bot");
			DoRandomBotTurn(pw);
		} else {
			if (thisTurnBot.equals("attackNeutralBot")){
				System.err.println("AttackNeutralBot is going to play this turn");
				attackNeutralBot(pw);
			}
			if (thisTurnBot.equals("BullyBot")) {
				System.err.println("BullyBot is going to play this turn");
				DoBullyBotTurn(pw);
			} else if (thisTurnBot.equals("RandomBot")) {
				System.err.println("RandomBot is going to play this turn");
				DoRandomBotTurn(pw);
			} else {
				System.err.println("WARNING: Adaptivity map wants " + thisTurnBot +
									" to play this turn, but this strategy is not implemented in this bot! Using default bot");
				DoRandomBotTurn(pw);
			}
		}
	}
	
	/**
	 * Implementation of the bullybot strategy (copy pasted from the regular BullyBot.java)
	 * @param pw
	 */
	public static void DoBullyBotTurn(PlanetWars pw) {
		Planet source = null;
		double sourceScore = Double.MIN_VALUE;
		//Select my strongest planet to send ships from
		for (Planet myPlanet : pw.MyPlanets()) {
			if (myPlanet.NumShips() <= 1)
				continue;
			double score = (double) myPlanet.NumShips();
			if (score > sourceScore) {
				sourceScore = score;
				source = myPlanet;
			}
		}
		
		Planet dest = null;
		double destScore = Double.MAX_VALUE;
		//Select weakest destination planet
		for (Planet notMyPlanet : pw.NotMyPlanets()) {
			double score = (double) (notMyPlanet.NumShips());

			if (score < destScore) {
				destScore = score;
				dest = notMyPlanet;
			}
		}
		
		if (source != null && dest != null) {
			pw.IssueOrder(source, dest);
		}
	}
	
	/**
	 * Implementation of the RandomBot strategy (copy pasted from the regular RandomBot.java)
	 * @param pw
	 */
	public static void DoRandomBotTurn(PlanetWars pw) {

		Random random = new Random();
		
		Planet source = null;
		List<Planet> myPlanets = pw.MyPlanets();
		//Randomly select source planet
		if (myPlanets.size() > 0) {
			Integer randomSource = random.nextInt(myPlanets.size());
			source = myPlanets.get(randomSource);
		}
		
		Planet dest = null;
		List<Planet> allPlanets = pw.NotMyPlanets();
		//Randomly select destination planets
		if (allPlanets.size() > 0) {
			Integer randomTarget = random.nextInt(allPlanets.size());
			dest = allPlanets.get(randomTarget);
		}

		if (source != null && dest != null) {
			pw.IssueOrder(source, dest);
		}
	}
		
	/**
	 * Will use heuristics to choose the most suitable "neutral planet" and conquer it, 
	 * if no neutral planets exists "then this method should not be called", however, it will then
	 * attack the enemy planet with the lowest amount of ships.
	 * @param pw
	 */
	public static void attackNeutralBot(PlanetWars pw){
		Planet dest = new Planet(100,1,1000,0,0,0); //Temporary planet with horrible heuristics
		List<Planet> myPlanets = pw.MyPlanets();

		Planet source = myPlanets.get(0); //Select first planet
		
		if(pw.NeutralPlanets().size() == 0){ //if 0 neutral planets exist
			System.err.println("No neutral planets");
			dest = pw.EnemyPlanets().get(0);
			
			for(int i=0; i<pw.EnemyPlanets().size(); i++){
				Planet p = pw.EnemyPlanets().get(i);

				int temp = p.NumShips();
				
				if(temp < dest.NumShips()){
					dest = p;
				}
				
			}
		}else{
			for(int i=0; i<pw.NeutralPlanets().size(); i++){
				Planet p = pw.NeutralPlanets().get(i);
			
				double temp = growthFleetHeuristic(p, pw);
				if(temp >= growthFleetHeuristic(dest, pw)){
					dest = p;
				}
			}
		}
		
		for(int i=0; i<myPlanets.size(); i++){
			if(source.NumShips() < myPlanets.get(i).NumShips()){
				source = myPlanets.get(i);
			}
		}
		
		pw.IssueOrder(source, dest);
	}
	
	
	public static void startPlanet(PlanetWars pw){
		Planet[] planetDistanceArray;
		planetDistanceArray = lengths(pw,pw.MyPlanets().get(0));
		
		Planet source = startingPlanet;
		Planet dest = new Planet(100, 1, 500, 0, 0, 0); //Temporary planet with horrible heuristics
			
		//Get the biggest planets (bigger than size 3)that are closer to enemy then self
		for(int i=0; i<pw.NeutralPlanets().size(); i++){ //Don't consider enemy starting point?
			Planet p = pw.NeutralPlanets().get(i);

			//Check if the neutral planet is closer to enemy than self (starting points) ONLY FOR PARALLEL
			//if(pw.Distance(source.PlanetID(), p.PlanetID()) < pw.Distance(pw.EnemyPlanets().get(0).PlanetID(), p.PlanetID())){
			
			//Check that it has good growth vs neutral fleets heuristics. 
			
			double temp = growthFleetHeuristic(p, pw);
			if(temp >= growthFleetHeuristic(dest, pw)){
				dest = p;
			}
			
			//}
			
		}
		if(growthFleetHeuristic(dest, pw) <= 0.4){
			dest = pw.EnemyPlanets().get(0);
		}
		
		pw.IssueOrder(source, dest);
		
	}
	
	/**
	 * Retrieve the heuristic for ratio between growth and fleet of certain planet
	 * @param Planet p
	 * @return ratio: -infinity - 1 where 1 is best and -infinity is worst
	 */
	public static double growthFleetHeuristic(Planet p, PlanetWars pw){
		//GrowthRate() returns 0-5
		int growth = p.GrowthRate();
		//fleets() returns number of ships
		int ships = p.NumShips();
	
		//Values can be fine tuned 
		return (growth*2 - ((double)ships/pw.NumShips(1))*8)/10;
	}
	
	
	/**
	 * Returns an array containing all the planets (except source planet) sorted by their distance
	 * to the source planet. Position 0 will be the planet with the shortest distance
	 * 
	 * @return : array containing planet IDs sorted by distance to source. 
	 */
	public static Planet[] lengths(PlanetWars pw, Planet source){
		List<Planet> allPlanets = pw.NotMyPlanets();
		int[] lengthArray = new int[pw.NotMyPlanets().size()];
		Planet[] planetArray = new Planet[pw.NotMyPlanets().size()];

		for(int i=0; i<lengthArray.length; i++){
			lengthArray[i] = pw.Distance(source.PlanetID(), allPlanets.get(i).PlanetID());
		}
		Arrays.sort(lengthArray);
		
		for(int n=0; n<lengthArray.length; n++){
			for(int i=0; i<allPlanets.size(); i++){
				if(pw.Distance(source.PlanetID(), allPlanets.get(i).PlanetID()) == lengthArray[n]){
					planetArray[n] = allPlanets.get(i);
					i=allPlanets.size();
				}
				
			}
		}
		
		return planetArray;
	}

	public static void main(String[] args) {
		String line = "";
		String message = "";
		int c;
		int counter = 0;
		try {
			while ((c = System.in.read()) >= 0) {
				switch (c) {
				case '\n':
					if (line.equals("go")) {
						PlanetWars pw = new PlanetWars(message);
						DoTurn(pw,counter);
						pw.FinishTurn();
						message = "";
						counter++;
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
