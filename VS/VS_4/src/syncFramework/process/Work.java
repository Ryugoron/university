package syncFramework.process;

public interface Work {
	
	public void sendPhase();
	public void recvPhase();
	public void workPhase();
	
	public boolean isEnded();
}
