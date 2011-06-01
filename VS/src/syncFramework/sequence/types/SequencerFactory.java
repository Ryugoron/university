package syncFramework.sequence.types;

import syncFramework.sequence.Sequencer;

public class SequencerFactory {
	public enum SequencerFactoryType{
		CENTRAL_SEQUENCER,
		ASYNC;
	};
	
	SequencerFactoryType type;
	
	public SequencerFactory(SequencerFactoryType t){
		type = t;
	}
	
	public Sequencer getSequencer(){
		switch (type) {
		case CENTRAL_SEQUENCER:
			return new CentralSequencer();
		case ASYNC:
			return new ASyncNetwork();
		default:
			return null;
		}
	}
}
