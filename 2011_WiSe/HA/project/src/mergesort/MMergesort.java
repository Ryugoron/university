package mergesort;


/**
 * MMergesort will sort a List with mergesort, if each divided list
 * has more elements than m. If the size is below m, than selection sort
 * will be used.
 * @author Max
 *
 * @param <E> - Type of the elements to sort.
 */
public class MMergesort<E extends Comparable<E>>{

	private int m;
	
	private long compCounter;
	private long sortTime;
	
	private E[] array, mergeArray;
	
	/**
	 * This constructor takes the m, that describes the lower bound
	 * for the elements of the divide steps. Below the size of m
	 * selection sort will be used.
	 * @param m
	 */
	public MMergesort(int m){
		this.m = m;
	}
	
	/**
	 * Sorts the given array and generates a
	 * new Statistic for this run.
	 * @param toSort
	 * @return
	 */
	public E[] sort(E[] toSort){
		array = toSort;
		
		//This is used to have a second array of the same size to merge in later
		//The required space for the sort is 2n \in O(n)
		mergeArray = toSort.clone();	
		
		
		compCounter = 0;
		long startTime = System.currentTimeMillis();
		
		if(array.length > 1) sortHelper();
		
		sortTime = System.currentTimeMillis() - startTime;
		
		return array;
	}
	
	public String getStatistic(){
		return "Last sorting took '"+compCounter+" steps' and needed '"+sortTime+" ms'.";
	}
	
	/**
	 * The sort Helper will initialiy partitionate the array in (array.length div m) parts
	 * and sortes them with selection Sort.
	 * After this step it will begin to merge every single part
	 */
	private void sortHelper(){
		//First, use selection sort for parts below size of m:
		if(m > 1){
			int start = 0;
			int end = m > array.length ? array.length : m;
			while(start < array.length){
				selectionSort(start, end);
				start = start + m;
				end = end + m;
				end = end > array.length ? array.length : end;
			}
		}
		//now we begin merging the parts
		int partSize = (m > 1) ? m : 1;
		while(partSize < array.length){
			int start = 0;
			int middle = start + partSize;
			int end = middle + partSize;
			end = end > array.length ? array.length : end;
			while(start < array.length){
				if(middle>start && end>middle){
					merge(start, middle, end);
				}
				start = start + partSize;
				middle = middle + partSize;
				end = middle + partSize;
				end = end > array.length ? array.length : end;
			}
			partSize *= 2;
		}
	}
	
	/**
	 * Merges the interval [start,end).
	 * If [start,middle) and [middle,end) is sorted,
	 * then [start,end) will be sorted afterwards.
	 * @param start - Left and of the interval (inclusive)
	 * @param end - Right end of the interval (exculsive)
	 * @param middle - Cut of the to intervals to merge.
	 */
	private void merge(int start,int middle, int end){
		System.arraycopy(array, start, mergeArray, start, middle-start);
		int fst=start, snd=middle, pos = start;
		while(fst < middle && snd < end){
			if(fst == middle){
				//Copy the secend until the end
				for(int i = snd; i<end; i++){
					array[pos] = array[i];
					pos++;
				}
				return; //Unnötig, aber hier wird er danach rausspringen
			}else if(snd == end){
				//Copy the first until the end
				for(int i = fst; i<middle; i++){
					array[pos] = mergeArray[i];
					pos++;
				}	
				return; //Unnötig, aber hier wird er danach raus springen
			}else{
				//Wenn beides noch offen ist
				compCounter++;
				if(mergeArray[fst].compareTo(array[snd]) < 0){
					array[pos] = mergeArray[fst];
					fst++;
				}else{
					array[pos] = array[snd];
					snd++;
				}
				pos++;
			}
		}
	}
	
	/**
	 * Sorts an interval [start, end) view selection sort.
	 * @param start of the interval (inclusive)
	 * @param end of the interval (exclusive)
	 */
	private void selectionSort(int start, int end){
		int swap;
		E save;
		for(int i = start; i< end - 1; i++){
			swap = i;
			for(int j=i; j< end - 1; j++){
				compCounter++;
				if(array[j].compareTo(array[swap]) < 0) swap = j;
			}
			save = array[i];
			array[i] = array[swap];
			array[swap] = save;
		}
	}
}
