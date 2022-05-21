public class Clock extends Thread{
    private int time=0;
    Clock(int i)
    {
        time=i;
    }

    @Override
    public void run() {
        while(time>0)
        {
            time--;
            try {
                sleep(1_000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    void setTime(int time)
    {
        this.time=time;
    }
    int getTime()
    {
        return time;
    }
}
