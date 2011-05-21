package syncTimeSlice;

import syncFramework.SyncMessage;
import syncFramework.process.AbstractWork;
import vsFramework.Channel;

/**
 * Diese Klasse stellt den eigentlichen TimeSlice Algorithmus dar.
 * Da wir die erste Nachricht schon kenne, wird die erste Nachricht
 * auf 0 gesetzt.<br /><br />
 * 
 * Vorraussetzung ist dabei, dass ID >= 0 für alle Prozesse gilt.
 * 
 * @author max,alex
 *
 */
public class TimeSliceWork extends AbstractWork{

	private int id,phase,round,anzProcess;
	
	private int leaderID;
	
	private boolean electionComplete = false;
	
	public TimeSliceWork(int id){
		super();
		this.id = id;
		this.leaderID = -1;
		this.phase = this.round = 0;
	}
	
	public TimeSliceWork(int id, Channel next, Channel last){
		super(next, last);
		this.id = id;
		this.leaderID = -1;
		this.phase = this.round = 0;
	}
	
	public void setAnzThreads(int anz){
		this.anzProcess = anz;
	}

	@Override
	public void workPhase() {
		
		String m = new String(this.received.getData());
		
		if(this.leaderID == -1){
			//Wenn ich letzte Runde den Gewinner mitgeteilt habe
			this.electionComplete = true;
		}else if(m == ""){
			//Mein Vorgänger hat nicht gewonnen oder teilt den Sieger mit.
			if(!this.electionComplete && this.id == this.phase){
				System.out.println("GEWONNEN: "+this.id);
				this.message = new SyncMessage((id+"").getBytes());
				this.leaderID = this.id;
			}else{
				//nächste Runde
				if(++round == anzProcess){
					round = 0;
					++anzProcess;
				}
			}
		}else{
			//Ich bekomm eine ID => ID ist neuer leader
			this.leaderID = Integer.valueOf(m);
			this.message = new SyncMessage((leaderID+"").getBytes());
		}
	}

	@Override
	public boolean isEnded() {
		return this.electionComplete;
	}
}
