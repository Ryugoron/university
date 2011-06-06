package asyncGHS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import syncFramework.SyncMessage;
import syncFramework.process.Work;
import vsFramework.Channel;
import vsFramework.Message;

/**
 * <p>
 * Unsere Implementierung für den GHS Algorithmus. Eine genauere Erklärung zur
 * Funktionsweise findet sich in der schriftlichen Abgabe.
 * </p>
 * <p>
 * Hier werden wir nur einmal kurz auf die einzelnen Phasen eingehen.
 * </p>
 * 
 * @author max,alex
 * 
 */
public class GHSWork implements Work, GHSNode {
	private final int INIT = 1, MWOESEARCH = 2, MWOEREPORT = 3,
			KMWOEREPORT = 4, CONNECT = 5, ABSORB = 6, CHANGEROOT = 8,
			READY = 9, MWOESEARCHINIT = 10, END = 11;

	/**
	 * ID der Komponente
	 */
	private int kID;
	private int myID;
	/**
	 * ID des Knotens
	 */
	private int stufe = 0;
	private List<Integer> weights;
	private List<Channel> neigh;
	private List<Integer> deadneigh;
	private Map<Channel, String[]> wasConnectAsked;

	// Baum struktur
	private List<Channel> children;
	private Channel parent;

	private int state = INIT;
	private Channel receivedFrom = null;
	private boolean wasStarted = false;

	private int lastVisited = 0;

	protected Message send, received;

	private int childrenSentMWOE = 0;

	private Channel MWOEFrom;
	private boolean finished = false;
	private Integer MWOE = Integer.MAX_VALUE;

	private boolean MWOElastvisited = false;

	public GHSWork(int kID) {
		this.neigh = new ArrayList<Channel>();
		this.weights = new ArrayList<Integer>();
		this.deadneigh = new ArrayList<Integer>();
		this.kID = kID; // Initial ist jeder Knoten eine Komponente
		this.myID = kID;
		this.parent = null;
		this.wasConnectAsked = new HashMap<Channel, String[]>();
		this.children = new ArrayList<Channel>();
		this.send = null;
		this.state = INIT;
		// identifiziert
	}

	/**
	 * Wir brauchen im gegensatz zum TimeSlice Algorithmus eine asugefeiltere
	 * Send Methode.<br />
	 * Was auf jedenfall nötig ist, ist ein fein granularere Sicht, in welcher
	 * Phase wo hin geschickt werden soll.
	 */
	@Override
	public void sendPhase() {

		// Wir erklären hier schon etwas genauer, welche Nachrichten geschickt
		// werden,
		// weil man in der workPhase() anschließend recht wenig sehen wird.

		// Wir benötigen die NULL Nachricht, an manchen Stellen, damit mehrere
		// Zustandsüberführungen ausgeführt werden, ohne das dazwischen gesendet
		// wird.
		if (send == null)
			return;
		switch (this.state) {
		// Sind wir im State 'INIT', so senden wir jedem unserer Kinder die INIT
		// Nachricht.
		// Wir schicken diese nicht an alle, damit wir keine unnützen
		// Nachrichten durch den Graph jagen.
		// Da INIT von der Wurzel einer Komponente ausgeht und die Komponenten
		// schon durch einen
		// Baum (für den Teilgraph sogar MST) verbunden sind ist dies der
		// günstigste Weg
		// die Nachricht zu verbreiten. Sind wir damit fertig, gehen wir in
		// MWOE-SEARCH-INIT über.
		case INIT:
			this.send = new SyncMessage((GHSMessage.INIT.get() + "\n"
					+ this.kID + "\n" + this.stufe).getBytes());
			for (Channel neighbor : this.children) {
				neighbor.send(this.send);
			}
			this.state = MWOESEARCHINIT;
			break;

		// Hier schicken wir ein "TEST" an den kleinsten, noch nicht besuchten
		// Nachbarn.
		case MWOESEARCH:
			if (this.lastVisited < this.neigh.size()) {
				this.neigh.get(this.lastVisited).send(this.send);
			}
			break;

		// Wenn der Parent nicht null ist (d.h. wir sind nicht die Wurzel der
		// Komponente)
		// Senden wir die Nachricht über eine gefundene mwoe an unseren
		// Elternknoten weiter.
		case MWOEREPORT:
			if (parent != null)
				this.parent.send(this.send);
			break;

		// Hier wird entlang derjenigen Kanten, die die geringeste Kante
		// geliefert haben
		// der Befehl gesandt, dass die mwoe genommen werden soll.
		// Kommen wir bei der mwoe an, können wir dann ein Connect aus der
		// Komponente herraus senden.
		case KMWOEREPORT:
			this.MWOEFrom.send(this.send);
			break;

		// Haben wir die Nachricht bekommen, dass wir uns Connecten können,
		// senden wir
		// an den zu letzt benachrichtigten (das die mwoe sein muss) Knoten, das
		// wir connecten möchten.
		case CONNECT:
			if (this.lastVisited < this.neigh.size())
				this.neigh.get(this.lastVisited).send(this.send);
			break;

		// Wir haben nur noch eine Absorb-Phase, weil wir Merge in CONNECT und
		// in ABSORB aufgeteilt haben.
		// Wir senden in ABSORB nun den Befehl, dass die fremde Komponente
		// hinter der mwoe
		// sich auf uns einstellen soll.
		case ABSORB:
			this.MWOEFrom.send(this.send);
			break;

		// Wenn wir absorbiert werden, werden wir die Kante, über die CHANGEROOT
		// kommt, als
		// Parent betrachten und den alten Parent als child.
		// Nun senden wir an den alten Parent das ChangeROOT weiter. Dies wird
		// bis zur
		// alten Wurzel fortgesetzt. Nun sind die beiden komponenten verbunden
		// und wir gehen in die nächste Phase.
		case CHANGEROOT:
			// Nur warten, es passiert also nichts.
			break;

		// Nachdem wir die Wurzel geändert haben, senden wir über parent die
		// Nachricht READY an die neue Wurzel. Erhält die Wurzel die Nachricht,
		// kann diese eine neue Runde mit INIT einleiten.
		case READY:
			if (parent != null) {
				this.parent.send(this.send);
			}
			break;

		// Sollten wir con allen kindern NOMWOE erhalten haben, wissen wir,
		// das es keine Komponente mehr zum verbinden gibt. Ist der Graph
		// zusammenhängend,
		// wissen wir also, dass wir nur noch eine Zusammenhangskomponente haben
		// und der MST fertig gebildet ist.
		// Die Ergebnisse stehen in this.children und this.parent. Jeder Knoten
		// hat darüber hinaus die ID der Wurzel als kId gespeichert.
		case END:
			for (Channel neighbor : this.children) {
				neighbor.send(this.send);
			}
			break;
		default:
			assert (false);
		}
	}

	/**
	 * In der recvPhase, machen wir ein busy-waiting auf eine Nachricht von
	 * einem beliebigen Nachbern, dies ist nicht besonders effizient, aber wir
	 * hatten keine Zeit um ein Select zu imlementieren.<br />
	 * Wir haben außerdem 2 States herrausgenommen, die ohne ein recv ausgefhrt
	 * werden sollen. Damit wir das splitBy trotzdem verwenden können, haben wir
	 * eine leere Nachricht erschaffen.
	 */
	@Override
	public void recvPhase() {
		if (this.state == MWOESEARCHINIT || this.state == MWOEREPORT) {
			this.received = new SyncMessage("".getBytes());
			this.receivedFrom = null;
		} else {
			do {
				for (Channel chan : this.neigh) {
					this.received = chan.nrecv();
					if (this.received != null)
						this.receivedFrom = chan;
					break;
				}
			} while (this.received == null);
		}
	}

	/**
	 * In dieser Methode wird die Hauptarbeite erledigt, da hier die
	 * Zustandsüberführungsfunktion liegt. <br />
	 * <br />
	 * Allerdings ist das ganze Recht kompliziert, so dass es sich anbietet die
	 * schriftliche Abgabe zu lesen, als zu versuchen diesen Quellcode zu
	 * verstehen. <br />
	 * <br />
	 * Prinzipiell Funktionieren die einzelnen Schritte genau wie in sendPhase
	 * beschrieben.
	 */
	@Override
	public void workPhase() {

		System.out.println("Prozess " + this.myID + ", Komponente : "
				+ this.kID + ", Stufe : " + this.stufe + ", Zustand : "
				+ this.parsteState(this.state));
		// Zunächst teilen wir die Nachricht an "\n" auf
		// dieses Zeichen fügen wir zwischen jede Komponente einer Nachricht ein
		// und sie kommt selbst in in der Nachricht vor.
		String incomingMessage;
		String[] parts;
		if (this.received != null) {
			incomingMessage = new String(this.received.getData());
			parts = incomingMessage.split("\n");
		} else {
			// Wenn noch nichts da ist (nur initial Möglich)
			incomingMessage = "";
			parts = new String[1];
			parts[0] = "INITIAL";
		}

		// Auf eine Testanfrage reagieren wir sofort, da sonst noch
		// mehr Zustände nötig wären, als wir sie ohnehin schon haben
		if (parts[0].equals(GHSMessage.TEST.get())) {
			Message m;
			if (parts[1].equals("" + this.kID)) {
				m = new SyncMessage((GHSMessage.REJECT.get()).getBytes());
			} else {
				m = new SyncMessage((GHSMessage.ACCEPT.get()).getBytes());
			}
			this.receivedFrom.send(m);
			this.send = null;
			return;
		}

		switch (this.state) {
		// Bei Init übernehmen wir die Werte, die uns von der Wurzel zugeschickt
		// werden.
		// Wie in send beschrieben, senden wir die Nachricht weiter an alle
		// unsere Kinder.
		//
		// Sollten wir eine Connectanfrage bekommen werden wir Sie an dieser
		// Stelle ersteinmal
		// speichern. Die Parameter (2,3) werden mit gesichert. Dies ist
		// möglich,
		// da sich in unserem Algorithmus nach einem Connect weder kID noch
		// stufe ändern können.
		case INIT:
			if (parts[0].equals(GHSMessage.INIT.get())) {
				this.kID = Integer.parseInt(parts[1]);
				this.stufe = Integer.parseInt(parts[2]);
				this.send = new SyncMessage("".getBytes());
			} else if (parts[0].equals(GHSMessage.CONNECT.get())) {
				this.send = null;
				this.wasConnectAsked.put(this.receivedFrom,
						Arrays.copyOfRange(parts, 1, 3));
				// NOTHINGNESS
				return;
			} else if (parts[0].equals("INITIAL")) {
				this.send = new SyncMessage("".getBytes());
				// Ist nur für Wurzelknoten am Start einer RUnde wahr
			}
			break;

		// Mit dieser Nachricht initialiseren wir die Suche nach der mwoe
		// Da wir, bevor wir das erste mal TEST gesendet haben
		// nichts empfangen können, haben wir diesen Zustand eingeführt,
		// der die erste Nachricht raus schickt.
		case MWOESEARCHINIT:
			this.childrenSentMWOE = 0;
			if (lastVisited >= this.neigh.size())
				this.MWOElastvisited = true;
			else
				this.MWOElastvisited = false;
			if(!MWOElastvisited && this.children.size() == 0 ){
				this.state = MWOEREPORT;
				this.send = new SyncMessage(GHSMessage.NOMWOE.get().getBytes());
			}
			this.MWOE = Integer.MAX_VALUE;
			this.send = MWOElastvisited ? null : new SyncMessage(
					(GHSMessage.TEST.get() + "\n" + this.kID).getBytes());
			this.state = MWOESEARCH;
			return;

			// Die Hauptaufgabe dieses Zustandes ist es die mwoe des eigenen
			// Unterbaumes zu finden.
			// Dazu wartet er darauf, dass alle Kinder eine Nachricht geschickt
			// haben
			// und testet noch das kleinste Kind, dass er selber in der Liste
			// hat.
			// Von allen diesen, nimmt er das kleinste und schickt es an seinen
			// Vater.
			//
			// Bei allem was wir tun, achten wir noch darauf, dass es unter
			// umständen keine
			// ausgehenden Kanten mehr gibt. In dem Fall senden wir ein NOMWOE.
		case MWOESEARCH:
			if (parts[0].equals(GHSMessage.NOMWOE.get())) {
				this.childrenSentMWOE++;
				if (parent != null) {
					if (this.childrenSentMWOE == this.children.size()
							&& this.MWOElastvisited) {
						this.send = new SyncMessage(GHSMessage.NOMWOE.get()
								.getBytes());
						this.state = MWOEREPORT;
					}
				} else {
					if (this.childrenSentMWOE == this.children.size()
							&& this.MWOElastvisited) {
						this.send = new SyncMessage(GHSMessage.END.get()
								.getBytes());
						this.state = END;
					}
				}
			}

			// Wurde unsere Nachricht rejected, gehen wir zum nächsten Knoten
			// über, außer es gibt keine mehr.
			if (parts[0].equals(GHSMessage.REJECT.get())) {
				this.deadneigh.add(this.neigh.indexOf(this.receivedFrom));
				this.lastVisited++;
				if (lastVisited >= this.neigh.size()) {
					this.MWOElastvisited = true;
				} else {
					this.send = new SyncMessage(
							(GHSMessage.TEST.get() + "\n" + this.kID)
									.getBytes());
				}
				return;
			} else if (parts[0].equals(GHSMessage.ACCEPT.get())) {
				this.MWOElastvisited = true;
				if (childrenSentMWOE == this.children.size()) {
					if (this.weights.get(this.lastVisited) < this.MWOE) {
						this.MWOEFrom = this.receivedFrom;
						this.MWOE = this.weights.get(this.lastVisited);
					}
					this.send = new SyncMessage(
							(GHSMessage.REPORT.get() + "\n" + this.MWOE)
									.getBytes());
					this.state = MWOEREPORT;
				} else {
					if (this.weights.get(this.lastVisited) < this.MWOE) {
						this.MWOEFrom = this.receivedFrom;
						this.MWOE = this.weights.get(this.lastVisited);
					}
					this.send = null;
				}
				return;

				// Wir bekommen ein Nachricht über eine mwoe unsere Kinder.
			} else if (parts[0].equals(GHSMessage.REPORT.get())) {
				childrenSentMWOE++;
				if (childrenSentMWOE == this.children.size()
						&& this.MWOElastvisited) {
					if (this.weights.get(this.lastVisited) < this.MWOE) {
						this.MWOEFrom = this.receivedFrom;
						this.MWOE = this.weights.get(this.lastVisited);
					}
					this.send = new SyncMessage(
							(GHSMessage.REPORT.get() + "\n" + this.MWOE)
									.getBytes());
					this.state = MWOEREPORT;
				} else {
					if (this.weights.get(this.lastVisited) < this.MWOE) {
						this.MWOEFrom = this.receivedFrom;
						this.MWOE = this.weights.get(this.lastVisited);
					}
					this.send = null;
				}
				// this.sent
			} else if (parts[0].equals(GHSMessage.CONNECT.get())) {
				this.send = null;
				this.wasConnectAsked.put(this.receivedFrom,
						Arrays.copyOfRange(parts, 1, 3));
				return;
			} else if(this.childrenSentMWOE == this.children.size()
					&& this.MWOElastvisited){
				//Wenn wir nichts machen können haben wir gewartet
				if (this.weights.get(this.lastVisited) < this.MWOE) {
					this.MWOEFrom = this.receivedFrom;
					this.MWOE = this.weights.get(this.lastVisited);
				}
				this.send = new SyncMessage(
						(GHSMessage.REPORT.get() + "\n" + this.MWOE)
								.getBytes());
				this.state = MWOEREPORT;
			}
			break;

		// Recht leichte gewichtige Methode, die prüft, ob man selbst
		// eine Wurzel ist. Sollte dies der Fall sein, schickt man ein Report
		// Über die gespeicherten MWOEFrom zurück.
		// da die Wurzel diese Nachricht nur abfeuern kann, wenn alle unteren
		// Knoten sich gemeldet haben (also in der Phase KMWOEREPORT sind),
		// Können wir die Nachricht REPORT wieder verwenden, um zu
		// Signalisierren,
		// Das ein Connect durchgeführt werden soll.
		case MWOEREPORT:
			if (this.parent == null) {
				this.send = new SyncMessage(
						(GHSMessage.REPORT.get()).getBytes());
			}
			this.state = KMWOEREPORT;
			break;

		// In diese Zustand hängen die Knoten, wenn sie ihre MWOE nach
		// oben mitgeteilt haben. Sie können nun nur noch geändert werden, wenn
		// a) Eines ihrer Kinder oder sie selbst die mwoe besitzt
		// b) Die Wurzel in eine neue Phase eintritt und eine neue MWOE gesucht
		// wird.
		// In jedem Fall warten wir auf ein Report, um es an die MWOEFrom zu
		// schicken,
		// oder ein INIT um von vorne zu Beginnen.
		//
		// Auch hier darf das akzeptieren von CONNECT nicht ignoriert werden.
		case KMWOEREPORT:
			if (parts[0].equals(GHSMessage.REPORT.get())) {
				if (this.children.contains(this.receivedFrom)) {
					this.send = new SyncMessage(GHSMessage.REPORT.get()
							.getBytes());
					this.state = READY;
					this.send = null;
					return;
				} else {
					// Notlösung, damit ChangeRoot noch funktioniert
					this.send = new SyncMessage((GHSMessage.CONNECT.get()
							+ "\n" + this.kID + "\n" + this.stufe).getBytes());
					this.MWOEFrom.send(this.send);
					this.send = null;
					if (this.wasConnectAsked.containsKey(this.MWOEFrom)) {
						// Die kIDs sollten (und dürfen) an diesem Punkt nicht
						// mehr gleich sein
						if (Integer.valueOf(this.wasConnectAsked
								.get(this.MWOEFrom)[1]) < this.stufe) {
							this.state = ABSORB;
							this.send = new SyncMessage(
									(GHSMessage.ABSORB.get()).getBytes());
						} else if (Integer.valueOf(this.wasConnectAsked
								.get(this.MWOEFrom)[1]) == this.stufe) {
							if (this.kID < Integer.valueOf(parts[1])) {
								this.state = CHANGEROOT;
							} else {
								this.state = ABSORB;
								this.send = new SyncMessage(
										(GHSMessage.ABSORB.get()).getBytes());
							}
						} else {
							this.state = CHANGEROOT;
						}
					} else {
						this.state = CONNECT;
					}
					return;
				}
			} else if (parts[0].equals(GHSMessage.INIT.get())) {
				this.state = INIT;
				this.kID = Integer.parseInt(parts[1]);
				this.stufe = Integer.parseInt(parts[2]);
			} else if (parts[0].equals(GHSMessage.CONNECT.get())) {
				this.wasConnectAsked.put(this.receivedFrom,
						Arrays.copyOfRange(parts, 1, 3));
				this.send = null;
				return;
			} else if (parts[0].equals(GHSMessage.END.get())) {
				this.state = END;
				this.send = new SyncMessage(GHSMessage.END.get().getBytes());
				this.finished = true;
			} else if (parts[0].equals(GHSMessage.CHANGE_ROOT.get())) {
				if (this.parent != null) {
					Channel oldParent = this.parent;
					this.children.add(oldParent);
					this.parent = this.receivedFrom;
					oldParent.send(new SyncMessage(GHSMessage.CHANGE_ROOT.get()
							.getBytes()));
					this.state = READY;
					this.send = null;
				} else {
					// Ich bin die Wurzel
					this.parent = this.receivedFrom;
					this.state = READY;
					this.send = new SyncMessage(GHSMessage.READY.get()
							.getBytes());
				}
			}
			break;

		// Sind wir nun in der Phase CONNECT, dürfen wir
		// endlich auf die CONNECT anfragen reagieren.
		//
		// Wir schicken nun ersteinmal (da wir die mwoe besitzen)
		// eine CONNECT Nachricht über diese hinaus.
		//
		// Nun warten wir so lange, bis wir unsererseits ein Connect
		// bekommen. Sollten wir schon ein Connect von dieser MWOE
		// in unserer MAP haben, können wir gleich weiter machen.
		//
		// Handelt es sich um eine Komponente mit einer niedrigen Stufe
		// (syntax: "CONNECT\nkId\nStufe"), können wir ein ABSORB
		// schicken, und die Komponente so absorbieren.
		// Ist unsere Stufe gleich, gucken wir, ob unsere kId
		// kleiner der anderen ist. Ist dies der Fall schicken wir auch ein
		// absorb.
		// In allen anderen Fällen warten wir auf eine Nachricht von der andere
		// Seite und werden dann absorbiert.
		//
		// Hier drin ist das MERGE codiert, da bei gleicher Stufe die Komponente
		// mit
		// der höheren ID zum Leader bestimmt wird.
		case CONNECT:
			if (parts[0].equals(GHSMessage.CONNECT.get())) {
				this.send = null;
				if (!this.receivedFrom.equals(this.MWOEFrom)) {
					this.wasConnectAsked.put(this.receivedFrom,
							Arrays.copyOfRange(parts, 1, 3));
					return;
				} else if (Integer.valueOf(parts[2]) < this.stufe) {
					this.state = ABSORB;
					this.send = new SyncMessage(
							(GHSMessage.ABSORB.get()).getBytes());
				} else if (Integer.valueOf(parts[2]) == this.stufe) {
					if (this.kID < Integer.valueOf(parts[1])) {
						this.state = CHANGEROOT;
					} else {
						this.state = ABSORB;
						this.send = new SyncMessage(
								(GHSMessage.ABSORB.get()).getBytes());
					}
				} else {
					this.state = CHANGEROOT;
				}
			} else {
				// Darf nicht eintreten
				this.send = null;
			}
			break;

		// In ABSORB reagieren wir nur (abgesehen von CONNECT)
		// auf READY. Diese nachricht kommt, wenn die fremde Komponente
		// erfolgreich Absorbiert wurde.
		// Diese Nachrict schicken wir nun an unsere eigene Wurzel,
		// die eine neue Runde starten wird.
		case ABSORB:
			if (parts[0].equals(GHSMessage.CONNECT.get())) {
				this.wasConnectAsked.put(this.receivedFrom,
						Arrays.copyOfRange(parts, 1, 3));
				this.send = null;
				return;
			} else if (parts[0].equals(GHSMessage.READY.get())) {
				// Wir haben die Nachricht bekommen, das heißt auch, wir lassen
				// den Zähler vom kleinsten nicht verbundenen weiter rücken
				this.lastVisited++;
				if (this.parent == null) {
					// Wenn ich Wurzel bin
					this.stufe++; // Neue Runde
					// kID ist immer noch die der Wurzel
					this.state = INIT;
					this.send = new SyncMessage("".getBytes());
					this.children.add(this.receivedFrom);
				} else {
					// Noch nicht die Wurzel
					this.send = new SyncMessage(GHSMessage.READY.get()
							.getBytes());
					this.state = READY;
					this.children.add(this.receivedFrom);
				}
			}
			break;

		// In dieser Methode drehen wir alle Kanten entlang des Weges um
		// (genauere Erklärung siehe schriftliche Abgabe)
		// Dadurch erreichen wir, dass vom absorbierten ROOT nun eine
		// Verbindung (reine parent verbindung) über die mwoe zum neuen ROOT
		// steht.
		case CHANGEROOT:
			// Ich ja die Nachricht weiter, weil ich ja weiß, wies weiter geht
			if (this.parent != null) {
				// Jag die Nachricht weiter an die Wurzel und dreh den Weg um
				Channel oldParent = this.parent;
				this.children.add(oldParent);
				this.parent = this.receivedFrom;
				oldParent.send(new SyncMessage(GHSMessage.CHANGE_ROOT.get()
						.getBytes()));
				this.state = READY;
				this.send = null;
			} else {
				// Ich bin die Wurzel, direkt dran, als ist die mwoe verbidnung
				this.parent = this.MWOEFrom;
				this.state = READY;
				this.send = new SyncMessage(GHSMessage.READY.get().getBytes());
			}
			break;

		// Steht in allen Knoten, die erfolgreich eine MWOE gefunden haben.
		// Es wird nur noch auf eine neue Runde gewartet.
		case READY:
			if (parts[0].equals(GHSMessage.CONNECT.get())) {
				this.wasConnectAsked.put(this.receivedFrom,
						Arrays.copyOfRange(parts, 1, 3));
				this.send = null;
				return;
			} else if (parts[0].equals(GHSMessage.READY.get())) {
				if (this.parent == null) {
					this.stufe++; // Neue Runde
					// kID ist immer noch die der Wurzel
					this.state = INIT;
					this.send = new SyncMessage("".getBytes());
				} else {
					// Weiter an die Wurzel
					this.send = new SyncMessage(GHSMessage.READY.get()
							.getBytes());
				}
			} else if (parts[0].equals(GHSMessage.INIT.get())) {
				this.kID = Integer.parseInt(parts[1]);
				this.stufe = Integer.parseInt(parts[2]);
				this.state = INIT;
			}
			break;

		// Sollte eine Wurzel sehen, dass es keine mwoe mehr gibt,
		// wird sie alle angeschlossenen Konten informieren und sich beenden.
		case END:
			this.finished = true;
			this.send = new SyncMessage(GHSMessage.END.get().getBytes());
			break;
		default:
			assert (false);
		}
	}

	@Override
	public boolean isEnded() {
		return this.finished;
	}

	/**
	 * Fügt wie in der Schnittstelle wie verlangt eine Kante mit Gewicht hinzu.
	 * 
	 * @param c
	 *            Channel über den man den neuen Knoten erreicht.
	 * @param weight
	 *            Das Gewicht der neuen Kante.
	 */
	@Override
	public void addNeighbor(Channel c, int weight) throws IllegalStateException {
		if (wasStarted)
			throw new IllegalStateException("Algorithm already started.");
		if (weight < 0)
			throw new IllegalArgumentException("I can't stand you.");

		int index = this.neigh.indexOf(c);
		if (index != -1) {
			this.weights.remove(index);
			this.neigh.remove(index);
		}

		this.weights.add(weight);
		this.neigh.add(c);
	}

	/**
	 * In start() sortieren wir die Liste mit Nachbarn nach gewicht. Dies
	 * ermöglicht uns einen einfacheren Umgang innerhalb des Algorithmuses.
	 * 
	 * Wir verwenden einen naiven Algorithmus (selection sort). Aber die
	 * Laufzeit von O(n^2) macht den Algorithmus asymptotisch nicht langsamer,
	 * da es nur dazu addiert wird.
	 */
	@Override
	public void start() {
		// Sort
		List<Integer> intsToSort = new ArrayList<Integer>();
		List<Channel> channelToSort = new ArrayList<Channel>();
		intsToSort.addAll(this.weights);
		channelToSort.addAll(this.neigh);

		this.weights.clear();
		this.neigh.clear();
		for (int i = 0; i < intsToSort.size(); ++i) {
			int minimumPos = 0;

			for (int j = 0; j < intsToSort.size(); ++j) {
				if (intsToSort.get(j) < intsToSort.get(minimumPos)) {
					minimumPos = j;
				}
			}
			this.weights.add(intsToSort.get(minimumPos));
			this.neigh.add(channelToSort.get(minimumPos));
			intsToSort.remove(minimumPos);
			channelToSort.remove(minimumPos);
		}
		// Sorted

		this.wasStarted = true;
	}

	private String parsteState(int state) {
		switch (state) {
		case INIT:
			return "INIT";
		case MWOESEARCH:
			return "MWOESAERCH";
		case MWOEREPORT:
			return "MWOEREPORT";
		case KMWOEREPORT:
			return "KMWOEREPORT";
		case CONNECT:
			return "CONNECT";
		case ABSORB:
			return "ABSORB";
		case CHANGEROOT:
			return "CHANGEROOT";
		case READY:
			return "READY";
		case MWOESEARCHINIT:
			return "MWOESEARCHINIT";
		case END:
			return "END";
		default:
			return "undefinded";
		}
	}
}