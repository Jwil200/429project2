public class UpdateTimer extends Thread {
    private boolean running;
    private int time;

    public UpdateTimer (int time) {
        this.time = time;
        running = true;

        this.start();
    }

    public void run () {
        int elapse = 0;
        while (running) {
            while (elapse < time) {
                try {
                    Thread.sleep(1);
                    elapse++;
                } catch (Exception e) {}
            }
            if (running) {
                Main.step();
                System.out.print("\nPeriodic updates sent.\n> ");
                elapse = 0;
            }
        }
    }

    public void close () {
        running = false;
    }
}
