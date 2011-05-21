package syncFramework.sequence.types;

import syncFramework.sequence.Connection;
import syncFramework.sequence.SequencerFactory.SequencerFactoryType;
import vsFramework.Channel;

public class SimpleConnection extends Connection{
	public SimpleConnection() {
		super(SequencerFactoryType.CENTRAL_SEQUENCER);
	}

	public void startNetwork(){
		super.startNetwork();
	}
	
	public void establishConnection(Channel channel){
		super.establishConnection(channel);
	}
}
