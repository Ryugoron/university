import java.util.Random;

public class Max{
    static int[] arr, erg;
    
    static volatile int counter = 0;

    static class MThread implements Runnable{
        int a, e;
        
        public MThread(int a, int e){
            this.a = a;
            this.e = e;
        }

        public void run(){
            if (a+1 == e){
                erg[a] = arr[a];
            } else if (a+2 == e){ 
                erg[a] = arr[a] > arr[a+1] ? arr[a] : arr[a+1];
            } else {
                int m = (a+e)/2;
                Thread left = new Thread(new MThread(a,m));
                counter = counter + 1;
                left.start();
                (new MThread(m,e)).run();
                try{ left.join(); } catch (InterruptedException e) {}
                erg[a] = erg[a] > erg[m] ? erg[a] : erg[m];
            }
        }
    }

    public static void main(String[] args){
        genArray(args);
        (new MThread(0,arr.length)).run();
        System.out.println("max("+printArray(arr)+") = "+erg[0]);
        System.out.println("Used : "+counter+" Threads");
    }













    static Random gen = new Random(System.currentTimeMillis());
    static void genArray(String[] args){
        int len;
        try{ len = Integer.parseInt(args[0]); } catch(Exception e) {len = 20; }
        arr = new int[len];
        erg = new int[len];
        for(int i=0; i<len; ++i){
            arr[i] = gen.nextInt(100000);
        }
    }


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
