public class Client extends Thread implements CommunicationInterface {

    Communication communication;
    boolean attackStage=false;
    boolean windowClose=false;
    String time;
    boolean myMove;
    boolean end=false;
    ServerClientData data;
    String toSend="";
    String move;


    Client(Communication communication)
    {
        this.communication=communication;
        move=new String();
    }

    @Override
    public void startC() {
        start();
    }

    @Override
    public void run() {
        data=new ServerClientData();

        try {
            clientLoop();
        }
        catch (Exception e)
        {
            end=true;
        }
    }

    void clientLoop()
    {
        while(!end){

            updataToServer();

            getDataFromServer();

            String tmp=toSend+"";
            communication.sendInformation(tmp);
            CheckOrder order=new CheckOrder(communication.getInformation());
            int i=data.readOrder(order);
            order(tmp,i);
        }
    }

    void getDataFromServer()
    {
        String serverClose = communication.getInformation();
        time = communication.getInformation();
        String tmp = communication.getInformation();
        myMove=Boolean.parseBoolean(tmp);
        if(serverClose.equals("true"))
            end=true;
    }

    void updataToServer()
    {
        communication.sendInformation(windowClose+"");
        if(move.equals("MyMove"))
        {
            communication.sendInformation(move+"");
            move="";
        }
        else
            communication.sendInformation(myMove+"");
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

    public void setSzpieg(String pos,int gold)
    {
        toSend="Pos "+pos +" gold: "+gold+" ";
    }

    void order(String tmp,int i)
    {
        if(i==6)
            attackStage=true;
        if(i==5)
            def();

        if(toSend.equals("Accepted") && tmp.equals(toSend))
            def();

        if(toSend.equals("Ready") && tmp.equals(toSend))
            def();

        if(i==7 || tmp.equals("YOU WIN"))
        {
            if(i==7)
                data.win=true;
            def();
            end=true;
        }

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
    public String getTime(){return time+"";}

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

    @Override
    public void setMyMove(boolean myMove) {
        move="MyMove";
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

    @Override
    public void def() {
        toSend="";
        data.defoliate();
    }
}