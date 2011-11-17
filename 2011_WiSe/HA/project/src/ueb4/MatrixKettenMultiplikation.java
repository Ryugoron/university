package ueb4;

import java.util.Random;

public class MatrixKettenMultiplikation {

	/**
	 * Dieses Feld wird für die dynamische Implementierung der Kettenmultiplikation verwendet.
	 */
	private static int[][] dyn;
	
	/**
	 * Dieses Feld speichert, auf welchem weg wir zu einem Feld in dyn kamen.
	 */
	private static int[][] back;
	
	/**
	 * Feld von Matrizen, damit wir diese bei der Rekursion nicht immer mit rein geben müssen
	 */
	private static Matrix[] matToMult;
	
	/**
	 * Diese Methode multipliziert die Matrizen einfach von links nach
	 * rechts auf und benutzt dabei den Algorithmus von Strassen.
	 * 
	 * @param matrices Feld von Matrizen (M_1 , M_2, ..., M_n), die mulipliziert werden sollen.
	 * @return Ergebnis von M_1 x M_2 x ... x M_n
	 */
	public static Matrix naive_ketten_multiplikation(Matrix[] matrices){
		if(matrices.length == 0) throw new IllegalArgumentException("No Matrix was given.");
		Matrix erg = matrices[0];
		for(int i = 1; i < matrices.length; i++){
			erg = Matrix.matmult(erg, matrices[i]);
		}
		return erg;
	}
	
	/**
	 * Diese Methode berechnet for der eigentlichen Multiplikation
	 * die beste Klammerung und Multipliziert erst dann.
	 * 
	 * @param matrices Feld von Matrizen (M_1 , M_2, ..., M_n), die mulipliziert werden sollen.
	 * @return Ergebnis von M_1 x M_2 x ... x M_n
	 */
	public static Matrix kettenMultiplikation(Matrix[] matrices){
		if(matrices.length == 0) throw new IllegalArgumentException("No Matrix was given.");
		if(matrices.length == 1) return matrices[0];
		
		//Unter 4 erg sich bei uns, dass es günstiger ist sofort zu berechnen
		if(matrices.length < 4) return naive_ketten_multiplikation(matrices);
		
		//Ab hier setzt der in Aufgabenteil 3a) beschriebene Algorithmus ein
		int n = matrices.length;
		
		dyn = new int[n][n];
		back = new int[n][n];
		
		for(int i = 0; i < n; i++){
			dyn[i][i] = 0;
			back[i][i] = -1;
		}
		for(int j = 0; j< n; j++){
			for(int i = 0; i<j; i++){
				int acc = Integer.MAX_VALUE;
				int a = 0;
				for(int x=i; x<j; x++){
					int mom = dyn[i][x] + dyn[x+1][j] + matrices[i].getColumnDimension() * matrices[j].getRowDimension()*(2*matrices[x].getRowDimension());
					if(mom < acc){
						acc = mom;
						a = x;
					}
				}
				dyn[i][j] = acc;
				back[i][j] = a;
			}
		}
		
		//nachdem wir nun den besten Wertbestimmt haben, führen wir eine Rekursion durch, die uns die Matrizen multipliziert
		matToMult = matrices;
		return multByReverse(0, n-1);
	}
	
	/**
	 * Diese Methode wird nur funktionieren, wenn sie von kettenMUltiplikation aus aufgerufen wird
	 * @param i stellt die Position der i ten Matrix dar
	 * @param j stellt die Position der j ten Matrix dar
	 * @return multiplizierte Matrizen
	 */
	private static Matrix multByReverse(int i, int j){
		if(i == j){
			return matToMult[i];
		}
		int a = back[i][j];
		return Matrix.matmult(multByReverse(i,a), multByReverse(a+1,j));
	}
	
	private static boolean output = false;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length == 1){
			if(args[0].equals("-o")) output = true; 
		}
		int n = 100;
		testLauf(n);
	}
	
	/**
	 * Führt eine Kettenmultiplikation auf n zufälligen Matrizen aus.
	 * @param n Anzahl der Matrizen
	 */
	private static void testLauf(int n){
		Matrix[] test = getRandomMatrices(n);
		if(output){
			System.out.println("Die folgenden Matrizen wurden multipliziert:");
			for(int i = 0; i<n; i++){
				System.out.println("Matrix "+i+"\n"+test[i]+"\n\n");
			}
		}
		Matrix erg;
		long start = System.currentTimeMillis();
		erg = naive_ketten_multiplikation(test);
		long time = System.currentTimeMillis() - start;
		System.out.println("Die naive Implementierung brauchte: "+time+"\n\n");
		if(output) System.out.println("Ergebnis:\n"+erg);
		
		start = System.currentTimeMillis();
		erg = kettenMultiplikation(test);
		time = System.currentTimeMillis() - start;
		System.out.println("Die dynamische Implementierung brauchte: "+time+"\n\n");
		if(output) System.out.println("Ergebnis:\n"+erg);
		
	}
	
	private static int maxDim = 200;
	
	/**
	 * Erzeugt n Matrizen, so dass die Dimensionen aufeinanderfolgendert
	 * Matrizen für die Multiplikation stimmt.
	 * @param n Anzahl der Matrizen
	 * @return n Matrizen zufälliger Dimension und Einträgen
	 */
	private static Matrix[] getRandomMatrices(int n){
		Matrix[] erg = new Matrix[n];
		Random gen = new Random();
		int lastDim = gen.nextInt(maxDim-1) + 1;
		int newDim;
		for(int i = 0; i<n; i++){
			newDim = gen.nextInt(maxDim-1) + 1;
			erg[i] = new Matrix(lastDim, newDim);
			lastDim = newDim;
			for(int x=0; x<erg[i].getColumnDimension(); x++){
				for(int y=0; y<erg[i].getRowDimension(); y++){
					erg[i].set(x, y, gen.nextInt(5));
				}
			}
		}
		return erg;
	}

}
