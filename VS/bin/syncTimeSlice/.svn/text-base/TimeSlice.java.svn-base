package syncTimeSlice;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import syncFramework.process.ProcessConnect;
import syncFramework.process.types.SimpleProcessConnect;
import syncFramework.sequence.types.SimpleConnection;
import vsFramework.BidirectionalPipe;

public class TimeSlice {
	public static void main(String[] args) {
		int n = 5,k = 0;
		List<TimeSliceWork> arbeitArbeit = new LinkedList<TimeSliceWork>();
		
		if(args.length == 1){
			n = Integer.valueOf(args[0]);
		}
		
		if(args.length == 2){
			k = Integer.valueOf(args[1]);
		}
		
		
		SimpleConnection sCon = new SimpleConnection();
		ProcessConnect pCon = new SimpleProcessConnect(sCon);
		
		sCon.beVerbose(true);
		
		boolean[] ids = new boolean[2*n];
		Random gen = new Random();
		for(int i=0; i< n; i++){
			int id;
			do{ id=gen.nextInt(2*n); }while(ids[id]);
			
			ids[id] = true;
			
			System.out.println("ID: "+id);
			
			TimeSliceWork w = new TimeSliceWork(id+k,n);
			arbeitArbeit.add(w);
			pCon.connect(w);
		}
		
		BidirectionalPipe p;
		
		for(int i=0; i<n-1; i++){
			p = new BidirectionalPipe();
			arbeitArbeit.get(i).setNext(p.gehtLeft());
			arbeitArbeit.get(i+1).setLast(p.gehtRight());
		}
		
		p = new BidirectionalPipe();
		arbeitArbeit.get(n-1).setNext(p.gehtLeft());
		arbeitArbeit.get(0).setLast(p.gehtRight());
		
		sCon.startNetwork();
	}
}
