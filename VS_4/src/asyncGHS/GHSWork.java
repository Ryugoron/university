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

public class GHSWork implements Work, GHSNode {
	private final int INIT = 1, MWOESEARCH = 2, MWOEREPORT = 3,
			KMWOEREPORT = 4, CONNECT = 5, ABSORB = 6,
			CHANGEROOT = 8, READY = 9, MWOESEARCHINIT = 10, END = 11;

	/**
	 * ID der Komponente
	 */
	private int kID;
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

	private int lastVisited = -1;

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
		this.parent = null;
		this.wasConnectAsked = new HashMap<Channel, String[]>();
		this.children = new ArrayList<Channel>();
		// identifiziert
	}

	@Override
	public void sendPhase() {
		if (send == null)
			return;
		switch (this.state) {
		case INIT:
			this.send = new SyncMessage((GHSMessage.INIT.get() + "\n"
					+ this.kID + "\n" + this.stufe).getBytes());
			for (Channel neighbor : this.children) {
				neighbor.send(this.send);
			}
			this.state = MWOESEARCHINIT;
			break;
		case MWOESEARCH:
			this.neigh.get(this.lastVisited).send(this.send);
			break;
		case MWOEREPORT:
			if (parent != null)
				this.parent.send(this.send);
			break;
		case KMWOEREPORT:
			this.MWOEFrom.send(this.send);
			break;
		case CONNECT:
			this.neigh.get(this.lastVisited).send(this.send);
			break;
		case ABSORB:
			this.MWOEFrom.send(this.send);
			break;
		case CHANGEROOT:
			//Nur warten, es passiert also nichts.
			break;
		case READY:
			this.parent.send(this.send);
			break;
		case END:
			for (Channel neighbor : this.children) {
				neighbor.send(this.send);
			}
			break;
		default:
			assert (false);
		}
	}

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

	@Override
	public void workPhase() {
		String incomingMessage = new String(this.received.getData());
		String[] parts = incomingMessage.split("\n");

		// Vereinfachung, da sonst weitere States nötig wären: Sofort antworten
		if (parts[0].equals(GHSMessage.TEST)) {
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
		case INIT:
			if (parts[0].equals(GHSMessage.INIT.get())) {
				this.kID = Integer.parseInt(parts[1]);
				this.stufe = Integer.parseInt(parts[2]);
			} else if (parts[0].equals(GHSMessage.CONNECT.get())) {
				this.send = null;
				this.wasConnectAsked.put(this.receivedFrom, Arrays.copyOfRange(
						parts, 1, 3));
				// NOTHINGNESS
				return;
			}
			break;
		case MWOESEARCHINIT:
			this.childrenSentMWOE = 0;
			this.MWOElastvisited = false;
			this.MWOE = Integer.MAX_VALUE;
			this.send = new SyncMessage(
					(GHSMessage.TEST.get() + "\n" + this.kID).getBytes());
			this.state = MWOESEARCH;
			return;
		case MWOESEARCH:
			if(parts[0].equals(GHSMessage.NOMWOE.get())){
				this.childrenSentMWOE++;
				if(parent != null){
					if(this.childrenSentMWOE == this.children.size() && this.MWOElastvisited){
						this.send = new SyncMessage(GHSMessage.NOMWOE.get().getBytes());
						this.state = MWOEREPORT;
					}
				}else {
					if(this.childrenSentMWOE == this.children.size() && this.MWOElastvisited){
						this.send = new SyncMessage(GHSMessage.END.get().getBytes());
						this.state = END;
					}
				}
			}
			if (parts[0].equals(GHSMessage.REJECT.get())) {
				this.deadneigh.add(this.neigh.indexOf(this.receivedFrom));
				this.lastVisited++;
				this.send = new SyncMessage(
						(GHSMessage.TEST.get() + "\n" + this.kID).getBytes());
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
				this.wasConnectAsked.put(this.receivedFrom, Arrays.copyOfRange(
						parts, 1, 3));
				// NOTHINGNESS
				return;
			}
			break;
		case MWOEREPORT:
			if (this.parent == null) {
				this.send = new SyncMessage((GHSMessage.REPORT.get())
						.getBytes());
			}
			this.state = KMWOEREPORT;
			break;
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
							this.send = new SyncMessage((GHSMessage.ABSORB
									.get()).getBytes());
						} else if (Integer.valueOf(this.wasConnectAsked
								.get(this.MWOEFrom)[1]) == this.stufe) {
							if (this.kID < Integer.valueOf(parts[1])) {
								this.state = CHANGEROOT;
							} else {
								this.state = ABSORB;
								this.send = new SyncMessage((GHSMessage.ABSORB
										.get()).getBytes());
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
				this.wasConnectAsked.put(this.receivedFrom, Arrays.copyOfRange(
						parts, 1, 3));
				this.send = null;
				return;
			} else if(parts[0].equals(GHSMessage.END.get())){
				this.state = END;
				this.send = new SyncMessage(GHSMessage.END.get().getBytes());
				this.finished = true;
			}
			break;
		case CONNECT:
			if (parts[0].equals(GHSMessage.CONNECT.get())) {
				this.send = null;
				if (!this.receivedFrom.equals(this.MWOEFrom)) {
					this.wasConnectAsked.put(this.receivedFrom, Arrays
							.copyOfRange(parts, 1, 3));
					return;
				} else if (Integer.valueOf(parts[2]) < this.stufe) {
					this.state = ABSORB;
					this.send = new SyncMessage((GHSMessage.ABSORB.get())
							.getBytes());
				} else if (Integer.valueOf(parts[2]) == this.stufe) {
					if (this.kID < Integer.valueOf(parts[1])) {
						this.state = CHANGEROOT;
					} else {
						this.state = ABSORB;
						this.send = new SyncMessage((GHSMessage.ABSORB.get())
								.getBytes());
					}
				} else {
					this.state = CHANGEROOT;
				}
			} else {
				// Darf nicht eintreten
				this.send = null;
			}
			break;
		case ABSORB:
			if (parts[0].equals(GHSMessage.CONNECT.get())) {
				this.wasConnectAsked.put(this.receivedFrom, Arrays.copyOfRange(
						parts, 1, 3));
				this.send = null;
				return;
			} else if (parts[0].equals(GHSMessage.READY.get())) {
				this.send = new SyncMessage(GHSMessage.READY.get().getBytes());
				this.state = READY;
			}
			break;
		case CHANGEROOT:
			if (parts[0].equals(GHSMessage.CHANGE_ROOT)) {
				if(this.parent != null){
					Channel oldParent = this.parent;
					this.parent = this.receivedFrom;
					oldParent.send(new SyncMessage(GHSMessage.CHANGE_ROOT.get().getBytes()));
					this.state = READY;
					this.send = null;
				}else{
					//Ich bin die Wurzel
					System.out.println("Habe mich verbunden: "+this.kID);
					this.parent = this.receivedFrom;
					this.state = READY;
					this.send = new SyncMessage(GHSMessage.READY.get().getBytes());
				}
			}else if(parts[0].equals(GHSMessage.READY)){
				this.state = INIT;
				this.send = null;
				this.parent.send(new SyncMessage(GHSMessage.READY.get().getBytes()));
			}else if (parts[0].equals(GHSMessage.CONNECT.get())) {
				this.wasConnectAsked.put(this.receivedFrom, Arrays.copyOfRange(
						parts, 1, 3));
				this.send = null;
				return;
			}
			break;
		case READY:
			if (parts[0].equals(GHSMessage.CONNECT.get())) {
				this.wasConnectAsked.put(this.receivedFrom, Arrays.copyOfRange(
						parts, 1, 3));
				this.send = null;
				return;
			}else if(parts[0].equals(GHSMessage.INIT.get())){
				this.state = INIT;
			}
			break;
		case END:
			this.finished = true;
			System.out.println("Ende Algorithmus: "+this.kID);
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

}