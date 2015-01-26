
import java.util.*;

public class State {
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