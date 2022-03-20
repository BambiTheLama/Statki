public class Connect extends Thread{
    Communication communication;
    Boolean Connected=false;
    String ip;
    int port;
    String who;
    boolean stop=false;

    Communication getCommunication()
    {
        return communication;
    }
    Connect(String who,int port,String ip)
    {
        this.port=port;
        this.who=who;
        this.ip=ip;
    }
    Boolean getConnected(){
        return Connected;
    }

    void stopthis()
    {
        stop=true;
    }


    @Override
    public void run() {
        boolean end=false;
        while(!end && !stop)
        {
            if(!Connected && !stop)
            {
                try{
                    communication=new Communication(who,port,ip);
                    Connected=false;
                    if(who.equals("server"))
                    {
                        System.out.println("1");
                        communication.sendInformation("work");
                        System.out.println("2 ");
                        String tmp=communication.getInformation();
                        System.out.println("3 "+tmp);
                        Connected=true;
                        end=true;


                    }
                    else if(who.equals("client"))
                    {
                        System.out.println("1");
                        String tmp=communication.getInformation();
                        System.out.println("2 "+tmp);
                        communication.sendInformation(tmp);
                        System.out.println("3 "+tmp);
                        Connected=true;
                        end=true;


                    }
                }
                catch (Exception e)
                {
                    Connected=false;
                    end=false;
                }
            }
        }
        if(!stop)
            System.out.println("Polaczono");

    }
}
