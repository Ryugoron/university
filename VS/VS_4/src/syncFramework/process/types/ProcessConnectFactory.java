package syncFramework.process.types;

import syncFramework.process.SyncProcess;
import syncFramework.process.Work;
import vsFramework.Channel;

public class ProcessConnectFactory {
	public enum ProcessConnectType{
		CENTRAL_SYNC,
		ASYNC;
	};
	
	//Standardmodus
	private ProcessConnectType mode = ProcessConnectType.CENTRAL_SYNC;
	
	public ProcessConnectFactory(ProcessConnectType t){
		mode = t;
	}
	
	public SyncProcess getSyncProcess(Work work, Channel channel){
		switch (mode) {
		case CENTRAL_SYNC:
			return new CentralSyncProcess(work,channel);
		case ASYNC:
			return new ASyncProcess(work, channel);
		default:
			return null;
		}
	}
}
