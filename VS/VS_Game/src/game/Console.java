package game;

public interface Console {
	
	/**
	 * <p>Verschiedene Felder in der GUI, die sich im Spiel gut machen würden.
	 * Diese Liste wird sich höchstwahrscheinlich über die Weiterentwicklung des
	 * Spiels auch selbst verändern.</p>
	 * 
	 * <p>Es bietet sich an hier eine höhere Kopplung zu riskieren, um das Problem
	 * später leichter zu handeln</p>
	 * 
	 * @author Max
	 *
	 */
	public enum StdFd{
		StdOut(1),
		Planets(2),
		Market(3);
		
		private int i;
		private StdFd(int i){
			this.i = i;
		}
		
		public int get(){
			return this.i;
		}
	}
	
	void setInputHandler(InputHandler handler);
	
	/**
	 * <p>Gibt einen Text auf der Standardconsole 
	 * StdOut aus.</p>
	 * <p>StdOut ist als fd = 0 zu finden {@link Console#println(int, String)}</p> 
	 * 
	 * @param text - Zeile an auszugebendem Text
	 */
	void println(String text);
	
	/**
	 * 
	 * <p>Gibt einen Text auf einem bestimmten Filedescriptor aus.</p>
	 * <p>Sollte der fd in der Implementierung nicht vorhanden sein,
	 * wird eine IllegalArgumentException geworfen</p>
	 *  
	 * 
	 * @param fd - Fildescriptor auf den geschrieben werden soll
	 * @param text - Zeile an auszugebendem Text
	 */
	void println(int fd, String text) throws IllegalArgumentException;
	
	void println(StdFd fd, String text) throws IllegalArgumentException;
	
	void clear();
	
	void clear(int fd) throws IllegalArgumentException;
	
	void clear(StdFd fd) throws IllegalArgumentException;
	/**
	 * <p>Testet, ob ein bestimmter fd in der Implementierung vorhanden ist.</p>
	 * <p>Dies ist vor allem Praktisch um zu testen, ob die Standard fd gesetzt sind.</p>
	 * 
	 * @param fd ist der Filedescriptor der geprüft werden soll
	 * @return true iff fd exists
	 */
	boolean testFd(int fd);
	
	boolean testFd(StdFd fd);
	
	void setVisible(boolean visible);
	String waitForName();
}
