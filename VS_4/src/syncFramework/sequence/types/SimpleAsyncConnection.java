package syncFramework.sequence.types;

import syncFramework.sequence.Connection;
import syncFramework.sequence.types.SequencerFactory.SequencerFactoryType;
import vsFramework.Channel;

public class SimpleAsyncConnection extends Connection{
	public SimpleAsyncConnection() {
		super(SequencerFactoryType.ASYNC);
	}

	public void startNetwork(){
		super.startNetwork();
	}
	
	public void establishConnection(Channel channel){
		super.establishConnection(channel);
	}
}
