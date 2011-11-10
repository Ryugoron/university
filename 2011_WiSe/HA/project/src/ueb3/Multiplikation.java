package ueb3;

public class Multiplikation {
	private static long[] tenPow;
	private static int maxPows = 1000;
	
	private static int counter;
	
	public static int getCount(){
		return counter;
	}
	
	static{
		tenPow = new long[maxPows];
		tenPow[0] = 1l;
		for(int i = 1; i< maxPows; i++){
			tenPow[i] = 10l * tenPow[i-1];
		}
	}

	public static long schulMethode(long a, long b){
		counter = 0;
		return schulMethodeStat(a,b);
	}
	
	private static long schulMethodeStat(long a, long b) {
		
		long faktor1 = a;
		long faktor2 = b;
		
		int length_faktor1 = (int)Math.floor(Math.log10(faktor1)+1);
		int length_faktor2 =(int)Math.floor(Math.log10(faktor2)+1);
		
		long prod = 0;
		
		// übertrag bei multiplikation
		long carry = 0;
		long stelle_faktor1;
		long stelle_faktor2;
		
		
		int zehnerpotenz_zeilenprodukt = 1;
		
		// wir multiplizieren als erstes alle stellen des ersten faktors mit der ersten stelle des zweiten faktors
		// dann alle stellen des ersten faktors mit der zwiten stelle des zweiten faktors, etc...
		for (int i = 0; i < length_faktor2; ++i) {
			// hole letzte stelle des 2. faktors
			stelle_faktor2 = (faktor2%10);
			faktor1 = a;
			carry = 0;
			// das produkt, was bei der schulmethode in einer "zeile" steht
			long zeilenprodukt = 0;
			// wir multiplizieren von rechts nach links, darum müssen wir in der i-ten spalte noch *10^i rechnen
			int zehnerpotenz_stellenprodukt = 1;
			
			
			for (int j = 0; j < length_faktor1; ++j) {
				// letzte stelle des ersten faktors
				stelle_faktor1 = (faktor1%10);
				
				long tmp = stelle_faktor2 * stelle_faktor1 + carry;
				
				zeilenprodukt = zeilenprodukt + (tmp % 10)*zehnerpotenz_stellenprodukt;
				zehnerpotenz_stellenprodukt = zehnerpotenz_stellenprodukt * 10;
				carry = tmp / 10;
				
				// letzte stelle des faktors abspalten
				faktor1 = faktor1/10;
				
				counter += 3;
			}
			// restlichen übertrag draufrechnen
			zeilenprodukt = zeilenprodukt + zehnerpotenz_stellenprodukt*carry;
			// letzte stelle des zweiten faktors abspalten
			faktor2 = faktor2/10;
			// auf gesamtprodukt draufrechnen (mit korrekter zehnerpotenz: in zeile i mit *10^i korrigieren)
			prod = prod + zeilenprodukt * zehnerpotenz_zeilenprodukt;
			zehnerpotenz_zeilenprodukt = zehnerpotenz_zeilenprodukt * 10;
			
			counter += 2;
		}
		
		
		return prod;
	}
	
	public static long karatsuba(long a, long b){
		counter = 0;
		return karatsubaStat(a,b);
	}
	
	private static long karatsubaStat(long a, long b) {
		long faktor1 = a;
		long faktor2 = b;
		// bisher nur stumpf abgetippt um den algo hier stehen zu haben. stimmt natürlich bisher nicht (funzt daher auch nicht :-))
		
		
		int length_faktor1 = (int)Math.floor(Math.log10(faktor1)+1);
		int length_faktor2 =(int)Math.floor(Math.log10(faktor2)+1);
		int maxLength = Math.max(length_faktor1, length_faktor2);
		
//		if(a== 0 || b == 0){
//			return 0;
//		}
		
		if(maxLength <= 1){
			return schulMethodeStat(a,b);
		}
		
		//Pro Schritt haben wir 6
		//2 Um es in Karatsuba einzusetzen
		//4 Beim zusammensetzen der Zahlen nach der Rekursion
		counter += 6;
		
		int halfExp = maxLength / 2;
		// Das Div und mod stellt das Aufteilen der Zahlen dar
		long faktor1_L = faktor1 % tenPow[halfExp];
		long faktor1_H = faktor1 / tenPow[halfExp];
		long faktor2_L = faktor2 % tenPow[halfExp];
		long faktor2_H = faktor2 / tenPow[halfExp];
		
		long p1 = karatsubaStat(faktor1_H ,faktor2_H);
		long p2 = karatsubaStat(faktor1_L, faktor2_L);
		long p3 = karatsubaStat((faktor1_H + faktor1_L), (faktor2_H + faktor2_L));
		
		//Die Multiplikation mit den 10er Potenzen stellt unser Shiften dar
		return p1 * (tenPow[halfExp * 2]) + (p3 - p1 - p2) * (tenPow[halfExp]) + p2;
//		return a*b; // :)
	}
	
	private static long karatsubaStatTwo(long a, long b){
		return a*b;
	}
	
	public static void main(String[] args) {
		System.out.println(Multiplikation.schulMethode(1234151L,685475801L));
		System.out.println("Schul: "+getCount());
		System.out.println(Multiplikation.karatsuba(1234151L,685475801L));
		System.out.println("Karatsuba: "+getCount());
	}
}
