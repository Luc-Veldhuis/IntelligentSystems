import java.util.*;

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
			
			double myNumberOfShips = 0;
			double myGrowthRate = 0;
			double enemyNumberOfShips = 0;
			double enemyGrowthRate = 0;
			
			int myPlanets = 1;
			int enemyPlanets = 1;
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
			double totalNumberOfShips = myNumberOfShips + enemyNumberOfShips;
			double totalGrowthRate = myGrowthRate + enemyGrowthRate;
			return (((double)(myGrowthRate*2+myNumberOfShips*8))/(totalGrowthRate*2+totalNumberOfShips*8));	
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