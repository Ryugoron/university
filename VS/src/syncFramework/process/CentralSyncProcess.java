package syncFramework.process;

import syncFramework.SyncMessage;
import vsFramework.Channel;

/**
 * 
 * Diese Klasse sorgt dafür, dass ein Prozess
 * synchonisert über einen zentralen Sequencer läuft.
 * 
 * Der Channel muss explizit angegeben werden, damit man
 * in der Wahl des Sequencer frei bleibt.
 * 
 * @author max
 *
 */
class CentralSyncProcess implements SyncProcess{
	
	private Work work;
	private Channel syncChannel;
	
	public CentralSyncProcess(Work work, Channel syncChannel){
		this.work = work;
		this.syncChannel = syncChannel;
		(new ProcessThread()).start();
	}
	
	@Override
	public void cancelConnection() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void callSend() {
		work.sendPhase();
		syncChannel.send(new SyncMessage(SyncMessage.PROCESS_SENDED));
		do{}while(syncChannel.recv().getData().equals(SyncMessage.SEND_ACK));
	}
	
	@Override
	public void callRecv() {
		this.work.recvPhase();
	}
	
	@Override
	public void callWork() {
		work.workPhase();
		
		//Aufgrund des Aufbaus im Sequencer muss statt PROCESS_WORKED
		//ein PROCESS_ENDED gesendet werden, damit der Sequencer
		//diesen Process austrägt
		if(work.isEnded())
			syncChannel.send(new SyncMessage(SyncMessage.PROCESS_ENDED));
		else{
		     syncChannel.send(new SyncMessage(SyncMessage.PROCESS_WORKED));
		     do{}while(syncChannel.recv().getData().equals(SyncMessage.WORK_ACK));
		}
	}
	
	
	private class ProcessThread extends Thread{
		
		public void run(){
			
			//Wir könnten auch umsortieren und diese Schleife eliminieren,
			//aber so wird in der Schleife klarer, was wir machen.
			do{}while(syncChannel.recv().getData().equals(SyncMessage.WORK_ACK));
			
			do{
				callSend();
				callRecv();
				callWork();
				
			}while(!work.isEnded());
		}
	}

}
