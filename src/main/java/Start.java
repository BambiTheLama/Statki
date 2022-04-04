import com.raylib.Jaylib;

import static java.lang.Thread.sleep;

public class Start {
    
    static int mapSize=0;
    static int startTime=0;
    static int moveTime=0;
    static int[][] ship=new int[3][5];
    static int[][] attackWhiteList=new int[2][6];
    static int startGold=0;
    static Communication communication=new Communication();
    static boolean KristiFlag=false;

    
    public static void main(String[] args) throws InterruptedException {

        while(true)
        {
            Jaylib.Color tmp=new Jaylib.Color(245,12,0,255);
            MenuStart start=new MenuStart();
            start.start();
            String who = null;
            boolean End=false;

            while(!start.getEnd() && !End)
            {
                //System.out.println("pentla "+start.getMenuStage());
                if(start.getMenuStage()==3)
                {
                    who=new String("server");
                    communication=new Communication();
                    while(!communication.tryConnect(start.getWho(),start.getPort(),start.getIp()) && start.getMenuStage()==3 &&!start.getEnd())
                    {

                    }
                    if(start.getMenuStage()!=3 ||start.getEnd()) {
                        communication.tryConnect("client",start.getPort(),start.getIp());
                        start.port++;
                    }
                    if(start.getMenuStage()==3)
                    {
                        mapSize=start.mapSize;
                        startTime=start.startTime;
                        moveTime=start.moveTime;
                        ship= start.ship;
                        attackWhiteList=start.attackWhiteList;
                        startGold=start.startGold;
                        KristiFlag= start.KristiFlag;
                        if(communication.connect)
                        {
                            End=true;
                            start.setEnd(true);
                        }
                    }
                    else
                    {
                        End=false;
                    }
                }
                else if(start.getMenuStage()==4 && start.tryConnect)
                {
                    who=new String("client");
                    System.out.println("JESTES W DOLACZANIA KLIENTA"+start.getWho() +" ip "+start.getIp()+" port "+start.getPort());

                    if(communication.tryConnect(start.getWho(),start.getPort(),start.getIp()))
                    {
                        End=true;
                        start.setEnd(true);
                    }

                }
                else if(start.getMenuStage()==5)
                {

                }
                try{
                    sleep(100);
                }
                catch (Exception e)
                {

                }
            }
            if(start.getEndAndDontContet())
            {
                return;
            }

            if(who.equals("server"))
                sendGameInformation();
            else if(who.equals("client"))
                getGameInformation();
            Jaylib.Color []Colors=start.getColors();
            boolean play=true;
            int gold=startGold;
            int[] bombs=null;
            while(play)
            {
                MainGameCore mainGameCore=new MainGameCore(communication,who, (byte) mapSize,ship,attackWhiteList,moveTime,startTime,gold,Colors, KristiFlag);
                if(bombs!=null && KristiFlag)
                    mainGameCore.setNumberOfBombs(bombs);
                play=mainGameCore.main();
                if(KristiFlag)
                    bombs=mainGameCore.getNumberOfBombs();
                gold=mainGameCore.startGold;
                if(who.equals("server"))
                {

                    String tmp2=communication.getInformation();
                    boolean playtmp=Boolean.parseBoolean(tmp2);
                    play=play&&playtmp;
                    communication.sendInformation(play+"");
                }
                else if(who.equals("client"))
                {
                    communication.sendInformation(play+"");
                    String tmp2=communication.getInformation();
                    play=Boolean.parseBoolean(tmp2);
                }

            }
        }

    }

    static void sendGameInformation() {
        communication.sendInformation(mapSize+"");
        communication.sendInformation(moveTime+"");
        communication.sendInformation(startTime+"");
        communication.sendInformation(startGold+"");
        for(int i=0;i<5;i++)
        {

            communication.sendInformation(ship[0][i]+"");
            communication.sendInformation(ship[1][i]+"");
            communication.sendInformation(ship[2][i]+"");
        }
        for(int i=0;i<6;i++)
        {
            communication.sendInformation(attackWhiteList[0][i]+"");
            communication.sendInformation(attackWhiteList[1][i]+"");
        }
        communication.sendInformation(KristiFlag+"");
    }

    static void getGameInformation() {

        String tmp=communication.getInformation();
        mapSize=Integer.parseInt(tmp);

        tmp=communication.getInformation();
        moveTime=Integer.parseInt(tmp);

        tmp=communication.getInformation();
        startTime=Integer.parseInt(tmp);

        tmp=communication.getInformation();
        startGold=Integer.parseInt(tmp);

        ship=new int[3][5];
        for(int i=0;i<5;i++)
        {
            tmp=communication.getInformation();
            ship[0][i]=Integer.parseInt(tmp);
            tmp=communication.getInformation();
            ship[1][i]=Integer.parseInt(tmp);
            tmp=communication.getInformation();
            ship[2][i]=Integer.parseInt(tmp);
        }
        for(int i=0;i<6;i++)
        {
            tmp=communication.getInformation();
            attackWhiteList[0][i]=Integer.parseInt(tmp);
            tmp=communication.getInformation();
            attackWhiteList[1][i]=Integer.parseInt(tmp);
        }
        tmp=communication.getInformation();
        KristiFlag=Boolean.parseBoolean(tmp);
    }
}
