package syncFramework.process;

import syncFramework.process.ProcessConnectFactory.ProcessConnectType;
import vsFramework.Channel;

/**
 * Hilfsklasse um mit der Taktgeber einer Verbindung aufzubauen.
 * Da die Art der Verbindung nicht bekannt ist, müssen
 * konkrete Ausprägungen benutzt werden.
 * 
 * @author max
 *
 */
public abstract class ProcessConnect {
	
	private ProcessConnectFactory factory;
	
	public ProcessConnect(ProcessConnectType t){
		this.factory = new ProcessConnectFactory(t);
	}
	
	/**
	 * Liefert eine neue Verbindung zum Taktgeber.
	 * 
	 * @return neue Verbindung
	 */
	protected abstract Channel getConnection();
	
	protected abstract void disconnect(Work work);
	
	protected abstract void disconnect(Channel channel);
	
	/**
	 * 
	 * Erzeugt einen neunen Process, der die Arbeit erledigt,
	 * holt sich eine neue Verbindung und verbindet die beiden.
	 * 
	 * @param work
	 */
	public void connect(Work work){
		this.factory.getSyncProcess(work, this.getConnection());
	}
}
