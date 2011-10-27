package mergesort;

import java.util.Random;

public class MergeTest {
	
	public static void main(String[] args) {
		
		boolean b = false;
		
		if(args.length > 0){
			if(args[0].equals("-p")){
				b = true;
			}
		}
		
		System.out.println("Test f�r M = 10, 100, 1000 und n = 10, 100, 1000, 100000 soweit sinnvoll.");
		
		int[] ms = {1, 2, 3, 4, 5, 6, 7, 10, 100, 1000};
		int[] ns = {1000, 100000, 1000000, 10000000};
		
		Random gen = new Random(System.currentTimeMillis());
		Mergesort<Integer> merge = new Mergesort<Integer>();
		MMergesort<Integer> mmerge;
		
		for(int n : ns){
			Integer[] sort = new Integer[n];
			Integer[] save = new Integer[n];
			for(int i = 0; i<n; i++){
				sort[i] = gen.nextInt();
			}
			System.out.println("---- n = "+n+" -----------");
			System.arraycopy(sort, 0, save, 0, sort.length);
			merge.sort(sort);
			if(b) MergeTest.printArray(sort);
			System.out.print("Mergesort:"+merge.getStatistic()+"\n");
			for(int m : ms){
				System.arraycopy(save, 0, sort, 0, sort.length);
				mmerge = new MMergesort<Integer>(m);
				mmerge.sort(sort);
				if(b) MergeTest.printArray(sort);
				System.out.print("MMergesort(m="+m+"):"+mmerge.getStatistic()+"\n");
			}
		}
			
		
		
	}
	
	private static <E> void printArray(E[] array){
		System.out.print("array[");
		for(E e : array){
			System.out.print(e);
		}
		System.out.println("]");
	}

}
