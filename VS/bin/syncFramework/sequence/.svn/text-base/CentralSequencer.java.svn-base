package syncFramework.sequence;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import syncFramework.SyncMessage;
import vsFramework.Channel;

/**
 * 
 * Ein Sequencer sorgt innerhalb eines asynchronen Netzes
 * für einen synchronen Ablauf eines Algorithmuses.<br />
 * 
 * Zugriff erhält man über die Klasse ({@link}Connection
 * 
 * @author max,alex
 *
 */
class CentralSequencer implements Sequencer
{
	
	/*
	 * Wir benutzen 3 Listen, damit wärend einer Runde niemand
	 * herrausfällt oder hinzukommt.
	 * Im Synchronen Fall kann sowas ohnehin nur zu beginn und Ende einer
	 * Runder passieren.
	 * Über <code>syncProcesses</code> muss nicht synchronisiert
	 * werden, weil der <code>syncThread</code> als einziger darauf
	 * zugreift.
	 */
	private List<Channel> syncProcesses;
	private List<Channel> newProcesses;
	private List<Channel> delProcesses;
	
	
	/*
	 * Thread zum benachrichtigen der 
	 * Prozesse des Algorithmus. Die Variable run
	 * zeigt dabei an, ob der Thread läuft.
	 */
	private SyncThread thread;
	private boolean running, shouldRun;
	
	private boolean statusLog;
	
	public CentralSequencer(){
		this.syncProcesses = new ArrayList<Channel>();
		this.newProcesses = new LinkedList<Channel>();
		this.delProcesses = new LinkedList<Channel>();
		this.running = false;
		this.shouldRun = false;
		this.thread = new SyncThread();
		this.statusLog = false;
	}
	
	public CentralSequencer(boolean flag){
		this();
		this.statusLog = flag;
	}
	
	@Override
	public void tellStatus(boolean flag){
		this.statusLog = flag;
	}
	
	/**
	 * Startet den Thread, falls es Prozesse gibt,
	 * die synchronisiert werden müssen. Sonst
	 * setzt er nur die Flag, dass beim Eintragen des
	 * nächsten Prozesses der Thread gestartet wird.
	 */
	@Override
	public void start(){
		this.shouldRun = true;
		synchronized (newProcesses) {
			if(!this.syncProcesses.isEmpty()){
				this.running = true;
				this.thread.start();
			}
		}
	}
	
	/**
	 * Hält den Thread nach der aktuellen Runde an.
	 */
	@Override
	public void stop(){
		this.shouldRun = this.running = false;
	}
	
	/**
	 * Fügt einen neuen Channel in die Lister der zu
	 * synchronisierenden Prozessen hinzu.
	 * Sollte der Thread gerade nicht laufen, wird er
	 * 'neu' gestartet, wenn er denn laufen soll.
	 * 
	 * Die Pipe muss Bidirectional sein, damit beide
	 * kommunizieren können.
	 * 
	 * @param channel
	 */
	@Override
	public void add(Channel channel){
		synchronized (this.newProcesses) {
			this.newProcesses.add(channel);
			if(this.newProcesses.isEmpty() && this.shouldRun){
				this.running = true;
				this.thread.start();
			}
		}
	}
	
	
	/**
	 * 
	 * Nimmt einen Prozess aus der Lister der zu synchronisierenden.
	 * Kann von außen aufgerufen werden, wird aber meist, durch den
	 * SyncProcess simuliert.
	 * Sollten beim Durchlauf des Threads keine Prozesse mehr in
	 * der Liste verbleiben, so wird der Prozess gestoppt.
	 * 
	 * Kommt wieder einer hinzu, so wird der Thread in <code>add</code>
	 * neu gestartet.
	 * 
	 * @param channel
	 */
	@Override
	public void remove(Channel channel){
		synchronized (this.delProcesses) {
			this.delProcesses.add(channel);
		}
	}
	
	
	/**
	 * 
	 * Dieser Thread enthält die Schleife, die alle Prozesse 
	 * synchronisiert. Die Synchronisation läuft in 3 Schritten.<br /><br />
	 * 
	 * 	1. Alle Threads senden ihre aktuellen Nachrichten. <br />
	 *  2. Alle Threads empfangen ihre Nachrichten.<br />
	 *  3. Alle Threads verändern ihren Zustand gemäß empfangener Nachricht.<br />
	 *  <br /><br />
	 *  
	 *  Da zwischen den letzen beiden Schritten kein Nachrichtenaustausch 
	 *  von nöten ist, wird auch keine Synchronisation erfolgen. Es wird also
	 *  nur nach 1. und nach 3. synchronisiert.
	 * 
	 * @author max
	 *
	 */
	private class SyncThread extends Thread{
		
		/**
		 * In dieser Methode wird kontrolliert ob alle Prozesse
		 * die Bestätigung für das Abschicken ihrer Nachrichten
		 * geschickt haben.
		 */
		private void allSend(){
			boolean rightMessage;
			for (Channel process : syncProcesses) {
				do{
					rightMessage = (process.recv().getData().equals(SyncMessage.PROCESS_SENDED));
				}while(!rightMessage);
			}
		}
		
		/**
		 * Sendet an alle Threads die Bestätigung, dass Schritt 1.
		 * fertig ist. 
		 */
		private void sendAck(){
			for (Channel process : syncProcesses) {
				process.send(new SyncMessage(SyncMessage.SEND_ACK));
			}
		}
		
		/**
		 * In dieser Methode wird kontrolliert ob alle Prozesse
		 * die Bestätigung für das Ausführen ihrer 
		 * Zustandsüberführungsfunktion gesendet haben.<br />
		 * An dieser Stelle (nur dieser) wird überprüft, ob ein
		 * Process fertig ist.
		 */
		private void allWork(){
			boolean rightMessage;
			for (Channel process : syncProcesses) {
				do{
					rightMessage = (process.recv().getData().equals(SyncMessage.PROCESS_ENDED));
					if(rightMessage) remove(process);
					else rightMessage = (process.recv().getData().equals(SyncMessage.PROCESS_WORKED));
				}while(!rightMessage);
			}
		}
		
		/**
		 * Sendet an alle Threads die Bestätigung, dass Schritt 3.
		 * fertig ist. <br />
		 * Da erst nach dieser Nachricht die Prozesse neue
		 * Nachrichten schicken können, wird beim starten des Threads
		 * an alle diese Nachricht gesendet
		 */
		private void workAck(){
			for (Channel process : syncProcesses) {
				process.send(new SyncMessage(SyncMessage.WORK_ACK));
			}
		}
		
		public void run(){
			
			synchronized(newProcesses){
				syncProcesses.addAll(newProcesses);
				newProcesses.clear();
			}
			
			this.workAck();
			
			while(running){
				
				//Normaler Ablauf eines Synchronen Algorithmuses
				if(statusLog) System.out.println("-- send round");
				this.allSend(); 
				if(statusLog) System.out.println(".... received all ACKs");
				this.sendAck();
				if(statusLog) System.out.println("-- receive & statechange round");
				this.allWork();
				if(statusLog) System.out.println(".... received all ACKs");
				
				//Hinzufügen neuer Prozesse
				synchronized(newProcesses){
					syncProcesses.addAll(newProcesses);
					newProcesses.clear();
				}
				
				//Löschen nicht gebrauchte Prozesse
				synchronized(newProcesses){
					syncProcesses.removeAll(delProcesses);
					delProcesses.clear();
					if(delProcesses.isEmpty()) running = false;
				}
				
				this.workAck();
			}
			
		}
	}
}
