package syncFramework.sequence;

public class SequencerFactory {
	public enum SequencerFactoryType{
		CENTRAL_SEQUENCER;
	};
	
	SequencerFactoryType type;
	
	public SequencerFactory(SequencerFactoryType t){
		type = t;
	}
	
	public Sequencer getSequencer(){
		if(this.type ==SequencerFactoryType.CENTRAL_SEQUENCER){
			return new CentralSequencer();
		}
		
		return null;
	}
}
