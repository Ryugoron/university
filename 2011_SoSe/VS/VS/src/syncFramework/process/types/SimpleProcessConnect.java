package syncFramework.process.types;


import syncFramework.process.ProcessConnect;
import syncFramework.process.Work;
import syncFramework.process.types.ProcessConnectFactory.ProcessConnectType;
import syncFramework.sequence.types.SimpleConnection;
import vsFramework.BidirectionalPipe;
import vsFramework.Channel;

public class SimpleProcessConnect extends ProcessConnect{
	
	private SimpleConnection con;
	
	public SimpleProcessConnect(SimpleConnection con){
		super(ProcessConnectType.CENTRAL_SYNC);
		this.con = con;
	}
	
	/**
	 * Nimmt eine einfache Bidirektionale Verbindung und trägt diese
	 * direkt in die Connection(d.h. den Sequencer) ein.
	 */
	@Override
	protected Channel getConnection() {
		BidirectionalPipe c = new BidirectionalPipe();
		con.establishConnection(c.gehtLeft());
		return c.gehtRight();
	}
	
	/**
	 * Wird in dieser Implementierung nicht benötigt.
	 */
	@Override
	protected void disconnect(Work work) {
	}
	
	/**
	 * WIrd in dieser Implementierung nicht benötigt.
	 */
	@Override
	protected void disconnect(Channel channel) {
	}
	
}
