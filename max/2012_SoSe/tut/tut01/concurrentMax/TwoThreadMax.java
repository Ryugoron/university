import java.util.Random;

/**
 *
 * This Example will compute the maximum of a list concurrent with two threads.
 *
 */
public class TwoThreadMax{
    
    // stores the array in which we search for the maximum 
    static int[] array;
    
    // saves the returnvalues of both threads
    static int[] erg;

    /**
    *
    * The MThread capsules the function, that will compute the
    * maximum. Form a startpoint it will always advances in steps
    * of the size 2.
    *
    * If the object is created with 0, it will search all even values.
    * If the object is created with 1, it will search all odd values.
    *
    */
    static class MThread implements Runnable{

        private final int start;
        
        /**
        *
        * Creates a {@see MThread}. Depending on the start, it will
        * either serach the odd values (start = 1) or the even values (start = 0)
        *
        *@arg start - Defines a startpoint from which the Thread will start to work
        */ 
        public MThread(int start){
            this.start = start;
        }

        /**
        *
        * @inheritdoc
        */ 
        public void run(){ 
            int max = TwoThreadMax.array[this.start];
            for(int i = this.start + 2; i < TwoThreadMax.array.length; i += 2){
                max = Math.max(TwoThreadMax.array[i], max);
            }
            erg[start] = max;
        }
    }

    /**
    *
    * This method will take an array of to MThreads and return the
    * maximum of both return values.
    *
    * The methode will start them, wait for the termination und compute
    * the final result.
    */ 
    public static int executeWork() {
        Thread t1 = new Thread(new MThread(0));
	      Thread t2 = new Thread(new MThread(1));
        
        t1.start();
        t2.start();
	
	while(true){ try { t1.join(); break; } catch (InterruptedException e) {} }
	while(true){ try { t2.join(); break; } catch (InterruptedException e) {} }
        
	return Math.max(TwoThreadMax.erg[0], TwoThreadMax.erg[1]);
    }

    public static void main(String[] args){
        TwoThreadMax.erg = new int[2];
        generateArray(10);

        int result = TwoThreadMax.executeWork();
        System.out.println("max(" + TwoThreadMax.printArray(array) + ") = " + result);
    }

    static Random gen = new Random(System.currentTimeMillis());
    
    /**
    *
    * Generates the array.
    * It will produce {@see len} random values in the interval
    * [0,100000).
    *
    */
    static void generateArray(int length){
        TwoThreadMax.array = new int[length];
        for(int i=0; i < TwoThreadMax.array.length; ++i){
            TwoThreadMax.array[i] = gen.nextInt(100000);
        }
    }
    
    /**
    *
    * Formats an Array to be printed.
    *
    */
    public static String printArray(int[] array){
        StringBuffer erg = new StringBuffer();
        erg.append('[');
        if(array.length > 0){
            erg.append(array[0]);
            for(int i = 1; i < array.length; ++i){
                erg.append(',');
								erg.append(array[i]);
            }
        }
        erg.append(']');
        return erg.toString();
    }
}
