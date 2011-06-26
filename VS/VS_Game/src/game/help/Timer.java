package game.help;

public class Timer {
	public Timer(int intervall, TimerHandler h) {

	}

	class TimerThread extends Thread {
		int intervall;
		TimerHandler h;

		public TimerThread(int intervall, TimerHandler h) {
			this.intervall = intervall;
			this.h = h;
			this.start();
		}

		public void run() {
			while (true) {
				try {
					Thread.sleep(intervall);
				} catch (InterruptedException e) {
				}
				h.onTick();
			}
		}
	}
}
