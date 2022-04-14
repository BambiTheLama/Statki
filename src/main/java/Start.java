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
    static String who = null;
    
    public static void main(String[] args) {

        while(true)
        {
            MenuStart start=new MenuStart();
            start.start();
            boolean end=false;

            while(!start.getEnd() && !end)
            {
                if(start.getMenuStage()==3)
                {
                    end = createServer(start);
                }
                else if(start.getMenuStage()==4)
                {
                    end = joinToServer(start);
                }
                try{
                    sleep(100);
                }
                catch (Exception ignored)
                {}
            }
            if(start.getEndAndDontContet())
            {
                return;
            }
            setData();
            Jaylib.Color []Colors=start.getColors();
            boolean play=true;
            int gold=startGold;
            int[] bombs=null;
            int [][]shiptmp=new int[3][5];
            while(play)
            {
                for(int i=0;i<3;i++)
                    for(int j=0;j<5;j++)
                        shiptmp[i][j]=ship[i][j];
                MainGameCore mainGameCore=new MainGameCore(communication,who, (byte) mapSize,shiptmp,attackWhiteList,moveTime,startTime,gold,Colors, KristiFlag);
                if(bombs!=null && KristiFlag)
                    mainGameCore.setNumberOfBombs(bombs);
                play=mainGameCore.main();
                if(KristiFlag)
                    bombs=mainGameCore.getNumberOfBombs();
                gold=mainGameCore.startGold;
                play=checkEnd(play);
            }
            communication.close();
        }

    }

    static boolean checkEnd(boolean play) {
        if(who.equals("server"))
        {
            String tmp2=communication.getInformation();
            boolean playtmp=Boolean.parseBoolean(tmp2);
            play=play&&playtmp;
            communication.sendInformation(play+"");
            return play;
        }
        else
        {
            communication.sendInformation(play+"");
            String tmp2=communication.getInformation();
            return Boolean.parseBoolean(tmp2);
        }
    }

    static void setData() {
        if(who.equals("server"))
            sendGameInformation();
        else
            getGameInformation();
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

    static void getGameParameters(MenuStart start) {
        mapSize=start.mapSize;
        startTime=start.startTime;
        moveTime=start.moveTime;
        ship= start.ship;
        attackWhiteList=start.attackWhiteList;
        startGold=start.startGold;
        KristiFlag= start.KristiFlag;
    }

    static boolean createServer(MenuStart start) {
        who= "server";
        communication=new Communication();
        while(!communication.tryConnect(start.getWho(),start.getPort(),start.getIp()) && start.getMenuStage()==3 &&!start.getEnd())
        {
            try{
                sleep(100);
            }
            catch (Exception ignored)
            {}
        }
        if(start.getMenuStage()!=3 ||start.getEnd()) {
            communication.close();
        }
        if(start.getMenuStage()==3)
        {
            getGameParameters(start);
            if(communication.connect)
            {
                start.setEnd(true);
                return true;
            }
        }
        return false;
    }

    static boolean joinToServer(MenuStart start) {
        who= "client";
        if(communication.tryConnect(start.getWho(),start.getPort(),start.getIp()))
        {
            start.setEnd(true);
            return true;
        }
        return false;
    }
}
