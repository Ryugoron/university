package syncFramework.sequence;

import syncFramework.sequence.SequencerFactory.SequencerFactoryType;
import vsFramework.Channel;

/**
 * 
 * Stellt eine Grundfunktionalität für das
 * Herstellen einer Verbindung mit dem
 * Sequencer her. Für konkrete Verbindungen
 * sollten Unterklassen geblidet werden.<br /><br />
 * 
 * Für die meisten Anwendungen wird es sich anbieten einen
 * Thread auf Anfragen warten zu lassen.
 * 
 * Einfache lokale Algorithmen können aber auch einfach die Methoden sichtbar machen.
 * 
 * @author max
 *
 */
public abstract class Connection {
	
	private Sequencer sequencer;
	//Wir behalten die Factory, falls wir mehrere Sequencer haben
	// => ausfallsicherheit durch Replikation
	private SequencerFactory factory;
	
	public Connection(SequencerFactoryType t){
		factory = new SequencerFactory(t);
		this.sequencer = factory.getSequencer();
	}
	
	/**
	 * Mit diesem Befehl signalisiert man, dass 
	 * das erbaute Netzwerk nun anfangen soll zu arbeiten.
	 * Kann über empfangene Nachrichten funktionieren, oder explizit von außen
	 * indem die Methode als public erklärt wird.
	 */
	protected void startNetwork(){
		this.sequencer.start();
	}
	
	/**
	 * Mit diesem Befehl signalisiert man, dass 
	 * das erbaute Netzwerk angehalten werden soll.
	 */
	protected void stopNetwork(){
		this.sequencer.stop();
	}
	
	/**
	 * Mit diesem Befehl kann man eine Verbindung mit dem 
	 * zentralen Taktgeber herstellen. Wie die Verbindung
	 * aufgebaut wird und welche Channel gewählt werden,
	 * ist der konkreten Implementierung überlassen.
	 * 
	 * @param channel
	 */
	protected void establishConnection(Channel channel){
		this.sequencer.add(channel);
	}
	
	/**
	 * Mit diesem Befehl kann man explizit Verbindungen beenden.
	 * Sollte nach Möglichkeit nicht benutzt werden, da 
	 * der Process den man steuert sosnt nicht richtig funktionieren muss.
	 * 
	 * @param channel
	 */
	protected void terminateConnection(Channel channel){
		this.sequencer.remove(channel);
	}
	
	
}
