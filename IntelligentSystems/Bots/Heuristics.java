
import java.util.Arrays;
import java.util.List;

public class Heuristics {
	
	public Heuristics(){
		
	}
	
	public Planet pickSourcePlanet(PlanetWars pw){
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
	
	/**
	 * Will return the planet containing the least amount of ships for a specific player
	 * If two planets contain the same (least)amount, the one with highest growth rate will be returned.
	 * @param pw: PlanetWars object
	 * @param player: the player to get planet with lowest ships from
	 * @return
	 */
	public Planet minFleetPlanet(PlanetWars pw, int player){
		Planet p = new Planet(100, player, 10000, 1, 1, 1);

		List<Planet> allPlanets;
		if(player == 0){
			allPlanets = pw.NeutralPlanets();
			if(allPlanets.size() == 0){ //If there are no neutral, pick enemy instead
				allPlanets = pw.EnemyPlanets();
			}
		}else if(player == 1){
			allPlanets = pw.MyPlanets();
		}else{
			allPlanets = pw.EnemyPlanets();
		}
		
		//Find the planet with the least amount of ships, if there are two identical, choose the one with highest
		//Growth rate.
		for(int i=0; i<allPlanets.size(); i++){
			if(allPlanets.get(i).NumShips() < p.NumShips()) {
				p = allPlanets.get(i);
			}else if(allPlanets.get(i).NumShips() == p.NumShips()){
				if(allPlanets.get(i).GrowthRate() > p.GrowthRate()){
					p = allPlanets.get(i);
				}else{
					//Do nothing
				}
			}
		}
		
		return p;
	}
	
	/**
	 * Retrieve the heuristic for ratio between growth and fleet of certain planet
	 * @param Planet p
	 * @return ratio: -infinity - 1 where 1 is best and -infinity is worst
	 */
	public static double growthFleetHeuristic(PlanetWars pw, Planet p){
		//GrowthRate() returns 0-5
		int growth = p.GrowthRate();
		//fleets() returns number of ships
		int ships = p.NumShips();
	
		//Values can be fine tuned 
		return (growth*2 - ((double)ships/pw.NumShips(1))*8)/10;
	}

	/**
	* Will calculate heuristics of entire board, instead of planet
	* Does not work because of class casting(SimulatedPlanetWars ipv PlanetWars)
	* @param PlanetWars pw
	* @return ratio: 0 is bad 1 is good
	*
	*/

	public double gameHeuristic(PlanetWars pw){
		int myNumberOfShips = 0;
		int myGrowthRate = 0;
		int enemyNumberOfShips = 0;
		int enemyGrowthRate = 0;
		for(Planet p:pw.MyPlanets()){
			myNumberOfShips += p.NumShips();
			myGrowthRate += p.GrowthRate();
		}
		for(Planet p:pw.EnemyPlanets()){
			enemyNumberOfShips += p.NumShips();
			enemyGrowthRate += p.GrowthRate();		
		}
		int totalNumberOfShips = myNumberOfShips + enemyNumberOfShips;
		int totalGrowthRate = myGrowthRate + enemyGrowthRate;
		return (((double)(myGrowthRate*2+myNumberOfShips*8))/(totalGrowthRate*2+totalNumberOfShips*8));

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
}
