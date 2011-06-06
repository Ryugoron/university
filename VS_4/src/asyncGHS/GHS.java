package asyncGHS;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import syncFramework.process.types.SimpleASyncProcessConnect;
import syncFramework.sequence.types.SimpleAsyncConnection;
import vsFramework.BidirectionalPipe;

public class GHS {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleAsyncConnection net = new SimpleAsyncConnection();
		SimpleASyncProcessConnect pC = new SimpleASyncProcessConnect(net);
		int n = 10;
		float p = 1;
		
		if(args.length > 0){
			n = Integer.parseInt(args[0]);
		}
		if(args.length > 1){
			p = Float.parseFloat(args[1]);
		}
		
		Random ran = new Random();
		
		List<GHSWork> network = new ArrayList<GHSWork>();
		for(int i=0; i<n; i++){
			GHSWork w = new GHSWork(i);
			network.add(w);
			pC.connect(w);
		}
		
		for(int i=0; i<n; ++i){
			for(int j=0; j<n; ++j){
				if(i==j) continue;
				if(ran.nextDouble()<p || (i+1 % n)==j){
					GHSWork a,b;
					a = network.get(i);
					b = network.get(j);
					int weight = ran.nextInt(5*n);
					BidirectionalPipe pipe = new BidirectionalPipe();
					
					a.addNeighbor(pipe.gehtLeft(), weight);
					b.addNeighbor(pipe.gehtRight(), weight);
				}
			}
		}
		System.out.println("Starte Prozesse");
		//Wir starten die Dinger per Hand
		for(GHSWork w : network){
			w.start();
		}
		System.out.println("Starte Algorithmus");
		//Starten das Algorithmuses
		net.startNetwork();
	}

}
