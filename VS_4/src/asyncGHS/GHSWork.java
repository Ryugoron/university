package asyncGHS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import syncFramework.process.Work;
import vsFramework.Channel;

public class GHSWork implements Work, GHSNode {

	private int kID;
	private List<Integer> weights;
	private List<Channel> neigh;
	private boolean wasStarted = false;
	
	public GHSWork(int kID){
		this.neigh = new ArrayList<Channel>();
		this.weights = new ArrayList<Integer>();
		this.kID = kID;
	}
	
	@Override
	public void sendPhase() {
		// TODO Auto-generated method stub

	}

	@Override
	public void recvPhase() {
		// TODO Auto-generated method stub

	}

	@Override
	public void workPhase() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isEnded() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addNeighbor(Channel c, int weight) throws IllegalStateException {
		if(wasStarted) throw new IllegalStateException("Algorithm already started.");
		if(weight<0) throw new IllegalArgumentException("I can't stand you.");
		
		int index = this.neigh.indexOf(c);
		if(index != -1){
			this.weights.remove(index);
			this.neigh.remove(index);
		}
		
		this.weights.add(weight);
		this.neigh.add(c);
	}

	
	@Override
	public void start() {
		//Maybe sort
		this.wasStarted = true;
	}

}
