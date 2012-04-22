import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;

public class ConstCallableMax{
    static Integer[] array;

    static class MCall implements Callable<Integer>{
        private int start, step;

        public MCall(int start, int step){
            this.start = start;
            this.step = step;
        }
   
        public Integer call(){
            Integer max = Integer.MIN_VALUE;
            for(int i = start; i<array.length; i += step){
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
    public static Integer executeWork(Collection<Callable<Integer>> toRun){
        ExecutorService es = Executors.newFixedThreadPool(process);
        List<Future<Integer>> res = new LinkedList<Future<Integer>>();
        boolean coveringCondition = true;
        while(coveringCondition){
            try{
                res = es.invokeAll(toRun);
                coveringCondition = false;
            } catch (InterruptedException e) {}
        }

        Integer result = Integer.MIN_VALUE;
        for(Future<Integer> erg : res){
            coveringCondition = true;
            while(coveringCondition){
                try{
                    result = Math.max(result, erg.get());
                    coveringCondition = false;
                } catch (ExecutionException e) {}
                  catch (InterruptedException e) {}
            }
        }
        es.shutdown();
        return result;
    }

    public static void main(String[] args){
       parseArgs(args);
       generateArray();
       Collection<Callable<Integer>> pr = generateWork();

       int result = executeWork(pr);
       System.out.println("max("+(verbose ? printArray(array) : "array")+") = "+result);
    }


    // Size of the array and number of Threads
    static int len = 10, process = 2, tasks = 2;
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
            } else if (args[i].equals("-p")){
                try{ process = Integer.parseInt(args[i+1]); } catch(Exception e) { process = 2;}
            } else if(args[i].equals("-t")){
                try{ tasks = Integer.parseInt(args[i+1]); } catch(Exception e) {tasks = 2;}
            } else if(args[i].equals("-verbose")){
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
    * This method will generate {@see process} number of Tasks.
    * Each thread will get a startpoint, a stepsize
    * s.t. there will be no interference.
    *
    */ 
    static Collection<Callable<Integer>> generateWork(){
        List<Callable<Integer>> run = new LinkedList<Callable<Integer>>();
        for(int i = 0; i<tasks; i++){
            run.add(new MCall(i, tasks));
        }
        return run;
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
}
