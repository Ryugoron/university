package syncTimeSlice;

import syncFramework.process.ProcessConnect;
import syncFramework.process.types.SimpleProcessConnect;
import syncFramework.sequence.types.SimpleConnection;
import vsFramework.BidirectionalPipe;

public class TimeSlice {
	public static void main(String[] args) {
		SimpleConnection sCon = new SimpleConnection();
		ProcessConnect pCon = new SimpleProcessConnect(sCon);
		
		//Wir erzeugen 5 Prozesse
		TimeSliceWork w1 = new TimeSliceWork(4);
		TimeSliceWork w2 = new TimeSliceWork(6);
		TimeSliceWork w3 = new TimeSliceWork(12);
		TimeSliceWork w4 = new TimeSliceWork(15);
		TimeSliceWork w5 = new TimeSliceWork(7);
		
		//Ring erzeugen
		BidirectionalPipe p = new BidirectionalPipe();
		w1.setNext(p.gehtLeft()); w2.setLast(p.gehtRight());
		p = new BidirectionalPipe();
		w2.setNext(p.gehtLeft()); w3.setLast(p.gehtRight());
		p = new BidirectionalPipe();
		w3.setNext(p.gehtLeft()); w4.setLast(p.gehtRight());
		p = new BidirectionalPipe();
		w4.setNext(p.gehtLeft()); w5.setLast(p.gehtRight());
		p = new BidirectionalPipe();
		w5.setNext(p.gehtLeft()); w1.setLast(p.gehtRight());
		
		pCon.connect(w1); pCon.connect(w2);
		pCon.connect(w3); pCon.connect(w4);
		pCon.connect(w5);
		
		sCon.startNetwork();
	}
}
