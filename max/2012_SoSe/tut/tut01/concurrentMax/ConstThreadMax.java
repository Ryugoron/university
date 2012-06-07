import java.util.Random;


/**
 *<p>
 * This Example will create a random array of Integers nad compute
 * Maximum with a given number of threads.
 *</p>
 *
 * <p>
 * You may change\\
 * arraysize : -s [SIZE]\\
 * thread count : -p [COUNT]
 * </p>
 *
 */
public class ConstThreadMax{
    
    // Array that will store the random integers
    static int[] array;

    // Array that will store the results of each thread
    static int[] erg;
    
    /**
    *
    * This Class capsules the computation of the maximum for the threads.
    * It will compute an parital maximum from a given startpoint and a
    * stepsize an will store the result in erg[slot].
    *
    */ 
    static class MThread implements Runnable{
        int start;
        int step;
        int slot;
        
        /**
        *
        * Creates a new Task for a thread.
        *
        * @arg start - Startpoint, from which the Thread my compute.
        * @arg step - Stepsize in which the array will be insected.
        * @arg slot - slot in the erg-array in which the result wil be stored.
        */
        public MThread(int start, int step, int slot){
            this.start = start;
            this.step = step;
            this.slot = slot;
        }

        /**
        *@inheritdoc
        */ 
        public void run(){ 
            int max = Integer.MIN_VALUE;
            for(int i = start; i<array.length; i += step){
                max = Math.max(array[i], max);
            }
            erg[slot] = max;
        }
    }

    /**
    *
    * This method will run an array of Tasks and combine the
    * result. For each task a thread will be spawned.
    * Next it waits for their termination and at last
    * the results in {@see erg} will be combined and return.
    *
    * @return maximum of array
    */
    public static int executeWork(Runnable[] toRun){
        Thread[] t = new Thread[toRun.length];
        for(int i = 0; i<process; i++){
            t[i] = new Thread(toRun[i]);
            t[i].start();
        }
        for(Thread thread : t){
            boolean b = true;
            while(b) { try { thread.join(); b = false; } catch(InterruptedException e){} }
        }
        int result = Integer.MIN_VALUE;
        for(int i = 0; i<process; i++){
            result = Math.max(result, erg[i]);
        }
        return result;
    }

    public static void main(String[] args){
       parseArgs(args);
       generateArray();
       Runnable[] pr = generateWork();

       int result = executeWork(pr);
       System.out.println("max("+(verbose ? printArray(array) : "array")+") = "+result);
    }


    // Size of the array and number of Threads
    static int len = 10, process = 2;
    static boolean verbose = false;

    static Random gen = new Random(System.currentTimeMillis());
    
    /**
    *
    * This method will parse the start input.
    * If -s followed by a number is in the input, the 
    * arraysize will be set to this value. If -p
    * followed by a number is in the input, the number
    * of threads will be set to this value. 
    *
    */ 
    static void parseArgs(String[] args){
        for(int i=0; i<args.length; i += 2){
            if(args[i].equals("-s")){
                try{ len = Integer.parseInt(args[i+1]); } catch(Exception e) { len = 10; }
            } else if (args[i].equals("-p")){
                try{ process = Integer.parseInt(args[i+1]); } catch(Exception e) { process = 2;}
            } else if (args[i].equals("-verbose")){
                verbose = true;
            }
        }
    }
    
    /**
    *
    * This method will create an array of random numbers.
    * This size of the array will be {@see len}
    *
    */ 
    static void generateArray(){
        array = new int[len];
        for(int i=0; i<len; ++i){
            array[i] = gen.nextInt(100000);
        }
    }

    /**
    *
    * This method will generate {@see process} number of threads.
    * Each thread will get a startpoint, a stepsize and a storeplace
    * s.t. there will be no interference.
    *
    */ 
    static Runnable[] generateWork(){
        erg = new int[process];
        Runnable[] run = new Runnable[process];
        for(int i = 0; i<process; i++){
            run[i] = new MThread(i, process, i);
        }
        return run;
    }
    
    /**
    *
    * Formates an array to be printed on the console.
    *
    */
    public static String printArray(int[] array){
        String erg = "";
        erg += "[";
        if(array.length > 0){
            erg +=array[0];
            for(int i=1; i<array.length; ++i){
                erg += ","+array[i];
            }
        }
        erg += "]";
        return erg;
    }
}
