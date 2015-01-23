import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/* A bit smarter kind of bot, who searches for its strongest planet and then attacks the weakest planet.
 The score is computed based on the number of ships.
 */

public class BullyBotEnhanced {
		
	public static void DoTurn(PlanetWars pw, int turnCounter) {
		
		//Pick my planet with best heuristics
		Planet source = pickSourcePlanet(pw);

		//Find the weakest enemy planet
		Planet dest = getEnemyPlanet(pw);
		
		//Get the best conquerable enemy planet
		Planet p = bestConquerPlanet(pw, source);
		
		//If a planet we can conquer was found
		if(p != null){
			dest = p;
		}
		
		// (3) Attack!
		if (source != null && dest != null) {
			pw.IssueOrder(source, dest);
		}
	}
	
	public static Planet pickSourcePlanet(PlanetWars pw){
		List<Planet> allPlanets = pw.MyPlanets();
		
		Planet p = allPlanets.get(0); //Starting planet
		
		for(int i=0; i< allPlanets.size(); i++){
			Planet temp = allPlanets.get(i);
			if(p.NumShips() <= temp.NumShips()){
				if(temp.GrowthRate() <= p.GrowthRate()){
					p = temp;
				}
				//VALUE HERE CAN BE MODIFIED, 5 IS THE AMOUNT OF SHIPS DIFFERENCE (MAYBE USE PERCENTAGE OF TOTAL SHIPS INSTEAD?)
				else if((temp.NumShips() - p.NumShips()) >= 5){
					p = temp;
				}
				//If temp has more growth, but less than 5 planets different, don't assign temp, use previous p instead
				else{
					//Do nothing
				}
			}
			
			
		}
		
		return p;
	}

	public static Planet getEnemyPlanet(PlanetWars pw){
		Planet dest = new Planet(100,1,5000,0,0,0); //Temporary planet with horrible heuristics;
		double destScore = Double.MAX_VALUE;
		
		if(pw.EnemyPlanets().size() > 1){
			
			for (Planet planetToAttack : pw.EnemyPlanets()) {
				double score = (double) (planetToAttack.NumShips());
				//store if smaller than previous score(amount of ships)
				if (score < destScore){
					destScore = score;
					dest = planetToAttack;
				}
			}
			
		}else{
			dest = pw.EnemyPlanets().get(0);
		}
		
		return dest;
	}
	
	public static Planet bestConquerPlanet(PlanetWars pw, Planet source){
		Planet p = new Planet(100,1,5000,0,0,0);
		//Search for through all planets where we can conquer to attack the one with highest growth
		for(Planet enemyPlanet : pw.EnemyPlanets()){
			
			//Out of all planets we can conquer, select the one with highest growth rate
			if(growthFleetHeuristic(pw, enemyPlanet) > growthFleetHeuristic(pw, p) && enemyPlanet.NumShips() < (source.NumShips()/2)){
				p = enemyPlanet;
			}
			
		}
		if(p.NumShips()==5000){
			return null;
		}
		return p;
	}
	
	public static double growthFleetHeuristic(PlanetWars pw, Planet p){
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
	public Planet[] lengths(PlanetWars pw, Planet source){
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
						counter++;
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
