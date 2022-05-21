public class Server extends Thread implements CommunicationInterface {

    Communication communication;
    boolean attackStage=false;
    boolean windowClose=false;
    Clock clock;
    boolean myMove=true;
    boolean end=false;
    int time;
    int attackTurnTime;
    int placeShipTime;
    ServerClientData data;
    String toSend="";
    boolean readyToStart=false;

    Server(Communication communication,int placeShipTime,int attackTurnTime)
    {
        this.communication=communication;
        this.placeShipTime=placeShipTime;
        this.attackTurnTime=attackTurnTime;
        clock=new Clock(placeShipTime);
        clock.start();
    }

    @Override
    public void startC() {
        start();
    }

    @Override
    public void run() {

        data=new ServerClientData();
        try {
            serverLoop();
        }
        catch (Exception e)
        {
            end=true;
        }

    }

    void serverLoop()
    {
        while(!end){
            updata();
            getDataFromClient();
            updataToClient();

            CheckOrder order=new CheckOrder(communication.getInformation());
            int i=data.readOrder(order);

            if(!attackStage)
            {
                if((i==8 || toSend.equals("Ready")) && setReadyToStart())
                {
                    continue;
                }
                placeShipStage();
                continue;
            }
            String tmp=toSend;
            communication.sendInformation(tmp);
            if(i==5)
            {
                toSend="Accepted";
            }
            if(i==8 || tmp.equals("YOU WIN"))
            {
                data.defoliate();
                end=true;
            }

        }
    }

    void updata()
    {
        if(windowClose)
            end=true;
        if(toSend.equals("Accepted"))
        {
            toSend="";
            myMove=!myMove;
            data.defoliate();
            setTime(attackTurnTime);
        }
        updataTime();

    }

    void updataTime()
    {
        time = clock.getTime();
        if(attackStage)
        {
            if(time>0)
                return;
            toSend="Accepted";
            return;
        }
        if(time>0)
            return;
        setTime(attackTurnTime);
    }

    void placeShipStage()
    {
        if(time>0)
        {
            communication.sendInformation("Knock");
        }
        else
        {
            attackStage=true;
            setTime(attackTurnTime);
            communication.sendInformation("Knock Knock");
        }
    }


    void updataToClient()
    {
        communication.sendInformation(windowClose+"");
        communication.sendInformation(time/60+":"+(time%60<10?"0":"")+time%60);
        communication.sendInformation(!myMove+"");
    }

    void getDataFromClient()
    {
        String isEnd=communication.getInformation();
        String move=communication.getInformation();
        System.out.println(move);
        if(move.equals("MyMove"))
        {
            myMove=true;
            toSend="Accepted";
        }
        if(isEnd.equals("true"))
            end=true;
    }

    public void setAttackRes(int[] res, int size,int gold)
    {
        if(res==null)
            return;
        String tmp="AttackRes "+size;

        for(int i=0;i<size;i++)
            tmp+=" "+res[i];
        tmp+=" gold: "+gold+" ";
        toSend=tmp;
        data.setAttackRes(res,size,gold);
    }

    public void setAttackPos(String[] pos, int size)
    {
        if(pos==null)
            return;
        String tmp="Attack "+size;
        for(int i=0;i<size;i++)
            tmp+=" "+pos[i];
        toSend=tmp;
        data.setAttackPos(pos,size);
    }
    public void setToSend(String send)
    {
        this.toSend=send;
    }

    void setTime(int time)
    {
        clock=null;
        clock=new Clock(time);
        clock.start();
    }

    @Override
    public void setMyMove(boolean move)
    {
        myMove=!move;
        setTime(attackTurnTime);
    }

    public void setSzpieg(String pos,int gold)
    {
        toSend="Pos "+pos +" gold: "+gold+" ";
    }

    @Override
    public boolean isAttackStage() {
        return attackStage;
    }

    @Override
    public boolean getMyMove() {
        return myMove;
    }

    @Override
    public String getTime(){return time/60+":"+(time%60<10?"0":"")+time%60;}

    @Override
    public String[] getAttackPos()
    {
        return data.getAttackPos();
    }

    @Override
    public int[] getAttackRes() {
        return data.getAttackRes();
    }

    @Override
    public int getSize()
    {
        return data.getSize();
    }

    @Override
    public void end()
    {
        end=true;
    }

    @Override
    public boolean getSzpieg(){
        return data.getSzpieg();
    }

    @Override
    public int getGold() {
        return data.getGold();
    }

    boolean setReadyToStart()
    {
        toSend="";
        if(!readyToStart)
        {
            System.out.println("GOTOW");
            readyToStart=true;
            return false;
        }
        System.out.println("START");
        attackStage=true;
        setTime(attackTurnTime);
        communication.sendInformation("Knock Knock");
        return true;
    }

    @Override
    public boolean getEnd()
    {
        return end;
    }
    @Override
    public boolean getWin()
    {
        return data.getWin();
    }

    @Override
    public void setLost()
    {
        toSend="YOU WIN";
    }
}
