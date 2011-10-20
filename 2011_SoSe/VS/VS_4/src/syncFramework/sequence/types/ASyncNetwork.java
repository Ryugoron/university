package syncFramework.sequence.types;

import java.util.ArrayList;
import java.util.List;

import syncFramework.SyncMessage;
import syncFramework.sequence.Sequencer;
import vsFramework.Channel;

class ASyncNetwork implements Sequencer
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
	private List<Channel> processes;
	private boolean running;
	
	public ASyncNetwork(){
		this.processes = new ArrayList<Channel>();
		running = false;
	}
	
	/**
	 * Im Asynchronen Fall machen wir eh nichts, also gibt es auch keinen Status.
	 */
	@Override
	public void tellStatus(boolean flag){
		return;
	}
	
	/**
	 * Startet alle Prozesse
	 */
	@Override
	public void start(){
		synchronized(this){
			if(!running){
				this.running = true;
				for (Channel c : processes) {
					c.send(new SyncMessage(SyncMessage.WORK_ACK));
				}
			}
		}
	}
	
	/**
	 * Hält jeden Prozess nach seiner aktuellen Runde an.
	 */
	@Override
	public void stop(){
		synchronized(this){
			if(running){
				this.running = false;
				for (Channel c : processes) {
					c.send(new SyncMessage(SyncMessage.PROCESS_ENDED));
				}
			}
		}
	}
	
	/**
	 * Fügt einen neuen Prozess zum Netzwerk hinzu.
	 * Sollte das Netzwerk laufen, wird er sofort gestartet.
	 * 
	 */
	@Override
	public void add(Channel channel){
		synchronized(this){
			this.processes.add(channel);
			if(running){
				channel.send(new SyncMessage(SyncMessage.WORK_ACK));
			}
		}
	}
	
	
	@Override
	public void remove(Channel channel){
		synchronized(this){
			this.processes.remove(channel);
			if(running){
				channel.send(new SyncMessage(SyncMessage.PROCESS_ENDED));
			}
		}
	}
	
}
