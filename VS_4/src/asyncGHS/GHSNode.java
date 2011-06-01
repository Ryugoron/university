package asyncGHS;

import vsFramework.Channel;


public interface GHSNode {

	/** 
	 * adds an adjacent Node by providing a channel to it
	 * @param c a a bidirectional channel to the neighbor
	 * @param weight weight of the edge
	 * throws an IllegalStateException when called after start was invoked
	 */
	public void addNeighbor(Channel c, int weight ) throws IllegalStateException;
	
	/**
	 * start running the GHS algorithm
	 */
	public void start();
	
	
}
