package ueb3;

public class Multiplikation {
	static Number schulMethode(long a, long b) {
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
			}
			// restlichen übertrag draufrechnen
			zeilenprodukt = zeilenprodukt + zehnerpotenz_stellenprodukt*carry;
			// letzte stelle des zweiten faktors abspalten
			faktor2 = faktor2/10;
			// auf gesamtprodukt draufrechnen (mit korrekter zehnerpotenz: in zeile i mit *10^i korrigieren)
			prod = prod + zeilenprodukt * zehnerpotenz_zeilenprodukt;
			zehnerpotenz_zeilenprodukt = zehnerpotenz_zeilenprodukt * 10;
		}
		
		
		return prod;
	}
	
	static Number karatsuba(long a, long b) {
		long faktor1 = a;
		long faktor2 = b;
		// bisher nur stumpf abgetippt um den algo hier stehen zu haben. stimmt natürlich bisher nicht (funzt daher auch nicht :-))
		
		
		int length_faktor1 = (int)Math.floor(Math.log10(faktor1)+1);
		int length_faktor2 =(int)Math.floor(Math.log10(faktor2)+1);
		int maxLength = Math.max(length_faktor1, length_faktor2);
		
		long halfExp = maxLength/2;
		// prototyping, do it better!
		long faktor1_H = faktor1 % 10^(halfExp);
		long faktor1_L = faktor1 / 10^(halfExp);
		long faktor2_H = faktor2 % 10^(halfExp);
		long faktor2_L = faktor2 / 10^(halfExp);
		
		long p1 = faktor1_H * faktor2_H;
		long p2 = faktor1_L * faktor2_L;
		long p3 = (faktor1_H + faktor1_L) * (faktor2_H + faktor2_L);
		
//		return p1 * (10^maxLength) + (p3 - p1 - p2) * (10^halfExp) + p2;
		return a*b; // :)
	}
	
	public static void main(String[] args) {
		System.out.println(Multiplikation.schulMethode(5l,685477580l));
		System.out.println(Multiplikation.karatsuba(5l,685477580l));
	}
}
