package syncFramework.process;

import vsFramework.Channel;

public class ProcessConnectFactory {
	public enum ProcessConnectType{
		CENTRAL_SYNC;
	};
	
	//Standardmodus
	private ProcessConnectType mode = ProcessConnectType.CENTRAL_SYNC;
	
	public ProcessConnectFactory(ProcessConnectType t){
		mode = t;
	}
	
	public SyncProcess getSyncProcess(Work work, Channel channel){
		if(mode == ProcessConnectType.CENTRAL_SYNC){
			return new CentralSyncProcess(work,channel);
		}
		return null;
	}
}
