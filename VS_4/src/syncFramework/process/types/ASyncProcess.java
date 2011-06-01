package syncFramework.process.types;

import syncFramework.SyncMessage;
import syncFramework.process.SyncProcess;
import syncFramework.process.Work;
import vsFramework.Channel;

/**
 * 
 * Diese Klasse stellt eine sehr vereinfachte Form des
 * CentralSyncProcess dar. Sie wird eigentlich nur
 * zum periodischen Aufruf der Methoden verwendet
 * und zum Starten und Stoppen des Netzwerkes.
 * 
 * @author max
 *
 */
class ASyncProcess implements SyncProcess{
	
	private Work work;
	private Channel syncChannel;
	
	public ASyncProcess(Work work, Channel syncChannel){
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
	}
	
	@Override
	public void callRecv() {
		this.work.recvPhase();
	}
	
	@Override
	public void callWork() {
		work.workPhase();
	}
	
	
	private class ProcessThread extends Thread{
		
		public void run(){
			
			//Wir warten zunächst darauf, dass wir starten dürfen.
			do{}while(!syncChannel.recv().getData().equals(SyncMessage.WORK_ACK));
			
			
			do{
					callSend();
					callRecv();
					callWork();
					
			//Sobald wir ein nrecv() haben, kann ich noch ein Stopp einbauen		
			/*
			 * if(syncChannel.nrecv().getData().equals(SyncMessage.PRECESS_ENDED)){
			 * 	do{}while(!syncChannel.recv().getData().equals(SyncMessage.WORK_ACK));
			 * }		
			 */
			}while(!work.isEnded());
			
					//Brauchen wir, damit alle letzten Nachrichten noch raus kommen.
					// Aber die sync darf nicht mehr beachtet werden
					work.sendPhase();
		}
	}

}