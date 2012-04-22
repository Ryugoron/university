public class ThreadDemo {

  public static final int COUNT = 1000;

  public static volatile int count = 0;

  static class PThread implements Runnable {
    public void run() {
      for (int i = 0; i < COUNT; ++i) {
        System.out.print("p");
        System.out.flush();
	Thread.yield();
      }
    }
  }

  static class BThread implements Runnable {
    public void run() {
      for (int i = 0; i < COUNT; ++i) {
        System.out.print("b");
        System.out.flush();
	Thread.yield();
      }
    }
  }

  public static void main(String[] args) {
    Thread pthread = new Thread(new PThread());
    Thread bthread = new Thread(new BThread());
    pthread.start();
    bthread.start();
    try {
      pthread.join();
      bthread.join();
      System.out.println("\n\nEverything is done");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
