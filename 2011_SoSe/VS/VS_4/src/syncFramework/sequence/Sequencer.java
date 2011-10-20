package syncFramework.sequence;

import vsFramework.Channel;

public interface Sequencer {
	public void start();
	public void stop();
	
	public void tellStatus(boolean flag);
	
	public void add(Channel channel);
	public void remove(Channel channel);
}
