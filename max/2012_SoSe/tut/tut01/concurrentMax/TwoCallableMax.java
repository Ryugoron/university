import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;


/**
 *
 * This Example will compute the Maximum of an
 * array with Java Executors.
 *
 */
public class TwoCallableMax{
    static Integer[] array;
    
    /**
    *
    * This Class will compute a partial maximum
    * of the array.
    *
    */ 
    static class MCall implements Callable<Integer>{
        private int start;
        
        /**
        *
        * This Thread will first inspect the array[start]
        * and will continue with stepsize two.
        *
        */ 
        public MCall(int start){
            this.start = start;
        }
        
        /**
        * @inheritdoc
        */ 
        public Integer call(){
            Integer max = Integer.MIN_VALUE;
            for(int i = start; i<array.length; i += 2){
                max = Math.max(array[i], max);
            }
            return max;
        }
    }



    /**
    *
    * This method will compute a Collection of MCalls, it will produce a
    * thread for each taks.
    *
    * @return maximum of array
    */
    public static Integer executeWork(){
        
        //
        // We create an Exector with two threads
        // and give to Tasks to be solved by the
        // two threads
        //
        ExecutorService es = Executors.newFixedThreadPool(2);
        Future<Integer> fut_result1 = es.submit(new MCall(0));
        Future<Integer> fut_result2 = es.submit(new MCall(1));

        Integer res1, res2;
        //
        // Waits for the two results to return 
        //
        res1 = waitFor(fut_result1);
        res2 = waitFor(fut_result2);
        
        
        es.shutdown();
        return Math.max(res1, res2);
    }

    public static void main(String[] args){
       parseArgs(args);
       generateArray();

       int result = executeWork();
       System.out.println("max"+ (verbose ? "("+printArray(array)+")" : "(array)")+" = "+result);
    }


    // Size of the array
    static int len = 10;
    static boolean verbose = false;

    static Random gen = new Random(System.currentTimeMillis());
    
    /**
    *
    * This method will parse the start input.
    * If -s followed by a number is in the input, the 
    * arraysize will be set to this value. If -p
    * followed by a number is in the input, the number
    * of threads will be set to this value. If -t 
    * followed by a number is in the inpt, the number
    * of tasks will be set to this value.
    */ 
    static void parseArgs(String[] args){
        for(int i=0; i<args.length; i += 2){
            if(args[i].equals("-s")){
                try{ len = Integer.parseInt(args[i+1]); } catch(Exception e) { len = 10; }
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
        array = new Integer[len];
        for(int i=0; i<len; ++i){
            array[i] = gen.nextInt(100000);
        }
    }

    
    /**
    *
    * Formates an array to be printed on the console.
    *
    */
    public static String printArray(Integer[] array){
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

    /**
    *
    * Takes a Future objects and waits, till it
    * is computed.
    *
    * This method will really wait even if there
    * was an InterruptedException.
    *
    */  
    static <E> E waitFor(Future<E> f){
        boolean coveringCondition = true;
        E ret = null;
        while(coveringCondition){
            try{
                ret = f.get();
                coveringCondition = false;
            } catch (InterruptedException e) {}
              catch (ExecutionException e) {}
        }    
        return ret;
    }

}
