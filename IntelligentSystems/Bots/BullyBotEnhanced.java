
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/* A bit smarter kind of bot, who searches for its strongest planet and then attacks the weakest planet.
 The score is computed based on the number of ships.
 */

public class BullyBotEnhanced {
		
	public static void DoTurn(PlanetWars pw) {
		// (1) Find my strongest planet.
		Planet source = pickSourcePlanet(pw);

		// (2) Find the weakest enemy planet
		Planet dest = new Planet(100,1,5000,0,0,0); //Temporary planet with horrible heuristics;
		double destScore = Double.MAX_VALUE;
		
		if(pw.EnemyPlanets().size() > 1){
			
			for (Planet planetToAttack : pw.EnemyPlanets()) {
				double score = (double) (planetToAttack.NumShips());
				if (score < destScore && ((source.NumShips()/2) > planetToAttack.NumShips())) {
					destScore = score;
					dest = planetToAttack;
				}
			}
			
		}else{
			List<Planet> allPlanets = pw.NeutralPlanets();
			
			for(int i=0; i<allPlanets.size(); i++){
				//Check if planet index i has better heuristics
				Planet p = allPlanets.get(i);
				
				if(Heuristics.growthFleetHeuristic(pw,allPlanets.get(i)) > Heuristics.growthFleetHeuristic(pw,p)){
					dest = allPlanets.get(i);
				}
				
			}
		}
		
		//Security if no planet was selected, select enemy planet
		if (dest.NumShips() == 5000){
			dest = pw.EnemyPlanets().get(0);
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
						DoTurn(pw);
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
