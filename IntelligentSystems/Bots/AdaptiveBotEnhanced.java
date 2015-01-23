import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class AdaptiveBotEnhanced {
	
	public static void DoTurn(PlanetWars pw, int counter) {		
		//Check if there is a really good neutral planet				
		if(checkForLowFleetPlanets(pw)){
			Planet temp = findBestPlanet(pw);
			Planet source = BullyBotEnhanced.pickSourcePlanet(pw);
			
			//Check if the neutral planet should be attacked or not
			if(counter > 2 && BullyBotEnhanced.growthFleetHeuristic(pw, temp) > 0.75){
				if(source.NumShips()/2 > temp.NumShips()){
					pw.IssueOrder(source, temp);
				}else{
					BullyBotEnhanced.DoTurn(pw, counter);
				}
			}else{
				LookaheadBot.DoTurn(pw);
			}
		}
		//If no neutral planet stands out, use BullyBotEnhanced
		else{
			BullyBotEnhanced.DoTurn(pw, counter);
		}
		
		return;
		
	}
	
	public static Planet findBestPlanet(PlanetWars pw){
		Planet p = pw.NeutralPlanets().get(0);
		
		for(int i=1; i<pw.NeutralPlanets().size(); i++){
			if(BullyBotEnhanced.growthFleetHeuristic(pw, pw.NeutralPlanets().get(i)) > BullyBotEnhanced.growthFleetHeuristic(pw, p)){
				p = pw.NeutralPlanets().get(i);
			}
		}
		
		return p;
	}
	
	public static boolean checkForLowFleetPlanets(PlanetWars pw){
		for(int i=0; i< pw.NeutralPlanets().size(); i++){
			if(pw.NeutralPlanets().get(i).GrowthRate() == 5 && pw.NeutralPlanets().get(i).NumShips() < 10){
				return true;
			}else if(pw.NeutralPlanets().get(i).GrowthRate() == 4 && pw.NeutralPlanets().get(i).NumShips() < 6){
				return true;
			}
		}
		return false;
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
