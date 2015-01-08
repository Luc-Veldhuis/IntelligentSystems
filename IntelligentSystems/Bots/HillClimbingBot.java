
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/*
 RandomBot - an example bot that picks up one of his planets and send half of the ships 
 from that planet to a random target planet.

 Not a very clever bot, but showcases the functions that can be used.
 Overcommented for educational purposes.
 */
public class HillClimbingBot {


	/*
	 * Function that gets called every turn. This is where to implement the strategies.
	 */

	public static void DoTurn(PlanetWars pw, int counter) {
		List<Planet> myPlanets = pw.MyPlanets();

		Planet source = myPlanets.get(0); //Select first planet
		//Select planet with most ships
		for(int i=0; i<myPlanets.size(); i++){
			if(source.NumShips() < myPlanets.get(i).NumShips()){
				source = myPlanets.get(i);
			}
		}
		
		List<Planet> allPlanets = pw.NotMyPlanets();
		Planet dest = new Planet(100,1,1000,0,0,0); //Temporary planet with horrible heuristics

		
		for(int i=0; i<allPlanets.size(); i++){
			//Check if planet index i has better heuristics
			Planet p = allPlanets.get(i);
			
			if(growthFleetHeuristic(allPlanets.get(i), pw) > growthFleetHeuristic(dest, pw)){
				dest = allPlanets.get(i);
			}
			
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
		
		//Can be finetuned 
		return (growth*2 - ((double)ships/pw.NumShips(1))*8)/10;
		
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
