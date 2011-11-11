package ueb3;

import java.math.BigInteger;
import java.util.Random;

public class MultiplikationBin {
	
	private static int counter;
	public static int getCount(){
		return counter;
	}
	
	public static int hybridSwitch = 8;
	
	public static BigInteger schulMultiplikation(BigInteger a, BigInteger b){
		counter = 0;
		return schulMultiplikationStat(a, b);
	}
	
	private static BigInteger schulMultiplikationStat(BigInteger a, BigInteger b){
		BigInteger erg = new BigInteger("0");
		for(int i = 0; i< a.bitLength(); i++){
			//Wir iterieren einmal über die erste Zahl
			if(a.testBit(i)){
				//Und wenn das Bit gesetzt ist, addieren wir die n Stellen auf die Zahl drauf
				erg = erg.add(b.shiftLeft(i));
				counter += b.bitLength(); // Wir müssen die Stellen von b oft addieren
			}
		}
		return erg;
	}
	
	public static BigInteger karatsuba(BigInteger a, BigInteger b){
		counter = 0;
		return karatsubaStat(a, b);
	}
	
	private static BigInteger karatsubaStat(BigInteger a, BigInteger b){
		int n = Math.max(a.bitLength(), b.bitLength());
		
		if(n <= 2){
			counter += 4; // Wir multiplizieren 2 Zahlen nach einer einfachen Methode. Dazu nehmen wir n^2 an.
			return a.multiply(b);
		}
		
		int half = n / 2;
		/*
		 *  Wir shiften den unteren "Nibble" der Zahl weg, von oben werden 0en eingefüllt
		 *  
		 *  a_n...a_(n/2+1) a_(n/2) ... a_0 => 0...0 a_n ... a_(n/2 + 1) | (weggeshifted) a_(n/2) ... a_0 
		 */
		BigInteger a_H = a.shiftRight(half);	
		/*
		 * Wir schieben das obere Stück wieder auf seine alte Position
		 * Und löschen diesen danach von der urspünglichen Zahl.
		 * 
		 * a_n...a_(n/2+1) a_(n/2) ... a_0 - (a_n ... a_(n/2 + 1) << (n/2)
		 * => a_n...a_(n/2+1) a_(n/2) ... a_0 - a_n .. a_(n/2+1) 0 ... 0
		 * => 0 .. 0 a_(n/2) ... a_0
		 * 
		 */
		BigInteger a_L = a.subtract(a_H.shiftLeft(half)); 
		
		BigInteger b_H = b.shiftRight(half);
		BigInteger b_L = b.subtract(b_H.shiftLeft(half));
		
		/*
		 * Formel aus der Vorlesung, mit 3 Rekursionsschritten
		 */
		BigInteger high = karatsubaStat(a_H, b_H);
		BigInteger middle = karatsubaStat(a_H.add(a_L), b_H.add(b_L));
		BigInteger low = karatsubaStat(a_L, b_L);
		
		counter +=2; //Für die beiden Additionen in middle
		
		BigInteger highShifted = high.shiftLeft(2*half); //we need half, because it is rounded the right way
		BigInteger middleShifted = middle.subtract(high.add(low)).shiftLeft(half); // (middle - high -low)*2^(half)
		
		// 2 Additionen in middleShifted und 2 im Ergebnis
		counter += 4;
		return highShifted.add(middleShifted).add(low);
		
		//return a.multiply(b);
	}
	
	public static BigInteger karatsubaHybrid(BigInteger a, BigInteger b){
		counter = 0;
		return karatsubahybridStat(a,b);
	}
	
	private static BigInteger karatsubahybridStat(BigInteger a, BigInteger b){
		int n = Math.max(a.bitLength(), b.bitLength());
		
		if(n <= hybridSwitch){
			return schulMultiplikationStat(a, b);
		}
		
		int half = n / 2;
		// Wir shiften den unteren "Nibble" der Zahl weg, von oben werden 0en eingefüllt
		BigInteger a_H = a.shiftRight(half);	
		//Wir schieben das obere Stück wieder auf seine alte Position
		//Und löschen diesen danach vom alten.
		BigInteger a_L = a.subtract(a_H.shiftLeft(half)); 
		
		BigInteger b_H = b.shiftRight(half);
		BigInteger b_L = b.subtract(b_H.shiftLeft(half));
		
		BigInteger high = karatsubahybridStat(a_H, b_H);
		BigInteger middle = karatsubahybridStat(a_H.add(a_L), b_H.add(b_L));
		BigInteger low = karatsubahybridStat(a_L, b_L);
		
		counter +=2; //Für die beiden Additionen in middle
		
		BigInteger highShifted = high.shiftLeft(2*half); //we need half, because it is rounded the right way
		BigInteger middleShifted = middle.subtract(high.add(low)).shiftLeft(half); // (middle - high -low)*2^(half)
		
		// 2 Additionen in middleShifted und 2 im Ergebnis
		counter += 4;
		return highShifted.add(middleShifted).add(low);
	}
	
	public static void main(String[] args){
		if(args.length == 1){
			if(args[0]=="-a"){
				testA();
				return;
			}
			if(args[0]=="-b"){
				testB();
				return;
			}
		}
		testB();
	}
	
	private static void testA(){
		String aString = "", bString = "";
		BigInteger a,b;
		int length;
		int time1,time2,time3;
		for(int i=1;i<100; i++){
			//Wir nehmen die hier das i module 10, weil Random auf java 7 bei uns mist produzierte.
			aString = aString+(i%10);
			bString = bString+((i+3)%10);
			a = new BigInteger(aString);
			b = new BigInteger(bString);
			length = Math.max(a.bitLength(), b.bitLength());
			schulMultiplikation(a, b);
			time1 = getCount();
			karatsuba(a, b);
			time2 = getCount();
			karatsubaHybrid(a, b);
			time3 = getCount();
			System.out.println(length+"-Stellen - Schulmethode brauchte: "+time1+", Karatsuba brauchte: "+time2+", Hybrid brauchte: "+time3);
		}
	}
	
	private static void testB(){
		BigInteger a,b;
		int length = 400;
		int time1,time2,time3;

		a = new BigInteger(length,new Random(912));
		b = new BigInteger(length, new Random(232));
		for(int i = 1; i < 209; i++){
			hybridSwitch = i;
			length = Math.max(a.bitLength(), b.bitLength());
			schulMultiplikation(a, b);
			time1 = getCount();
			karatsuba(a, b);
			time2 = getCount();
			karatsubaHybrid(a, b);
			time3 = getCount();
			System.out.println(i+"-Umschaltpunkt - Hybrid brauchte: "+time3);
		}
	}
}
