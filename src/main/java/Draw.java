import com.raylib.Jaylib;
import com.raylib.Raylib;
import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.*;
import com.raylib.Jaylib;
import com.raylib.Raylib;
import static com.raylib.Jaylib.*;
import static com.raylib.Jaylib.BLACK;
import static com.raylib.Raylib.DrawRectangle;
import static com.raylib.Raylib.DrawText;
import static com.raylib.Raylib.GetMouseX;
import static com.raylib.Raylib.GetMouseY;


public class Draw {

    int eqSize;
    int sizeBetweenEqAndMap;
    int cell;
    int sizeBetweenMaps;
    int startEnemyMapLocation;

    byte n;
    int height;
    int width;
    int sizeText;
    static Jaylib.Color[] shipColor;
    Jaylib.Color textColor;
    Raylib.Color mapLineColor;
    Jaylib.Color mapAttackMiss;
    Jaylib.Color mapAttackHit;
    Jaylib.Color mapAttackHit2;
    Jaylib.Color mapAttackHit3;
    Jaylib.Color shipColorContour;
    Jaylib.Color shipColorContour2;
    Jaylib.Color shipColor2;
    private boolean isPlacingShipTime;
    private boolean isMyMove;
    private int numberOfShipToPlace;
    private int numberOfShipAlive;
    private int time;
    private int gold;
    Texture[] shipTexture=new Texture[5];
    Texture[] menuShipTexture=new Texture[6];
    Texture[] attackTexture=new Texture[6];
    private static Font font=GetFontDefault();
    int tmpShipOnMap=0;
    int shipOnMap=0;
    int [][] shipOnMapPosition;
    int []numberOfBombs=new int[6];
    int[][] attackWhiteList;
    boolean KristiFlag;

    public static void setShipColor(Jaylib.Color[] shipColor)
    {
        Draw.shipColor =shipColor;
    }

    Draw(Jaylib.Color textColor, Jaylib.Color mapAttackHit, Jaylib.Color mapAttackMiss) {
        this.textColor= textColor;
        mapLineColor= BLACK;
        this.mapAttackMiss= mapAttackMiss;
        this.mapAttackHit= mapAttackHit;
        mapAttackHit2= new Jaylib.Color(mapAttackHit.r(),mapAttackHit.g(),mapAttackHit.b(),100);
        mapAttackHit3= new Jaylib.Color(mapAttackHit.r(),mapAttackHit.g(),mapAttackHit.b(),100);
        shipColorContour=new Jaylib.Color(55,55,255,100);
        shipColorContour2=new Jaylib.Color(55,255,255,100);
        shipColor2=new Jaylib.Color(55,255,255,25);
        font=LoadFont("resources/czciaki/comici.ttf");
        if(shipColor==null)
        {
            shipColor=new Jaylib.Color[5];
            for(int i=0;i<5;i++)
                shipColor[i]=new Jaylib.Color((i*30+69)%255,(75+i*69)%255,(42+15*i)%255,255);
        }
    }

    Draw(byte n,int width,int height,int eqSize,int sizeBetweenEqAndMap,int cell,int sizeBetweenMaps,int sizeText,int startEnemyMapLocation,Jaylib.Color []Color,int [][]attackWhiteList,boolean KristiFlag) {
        this(Color[0],Color[1],Color[2]);
        this.n=n;
        this.width=width;
        this.height=height;
        this.eqSize=eqSize;
        this.sizeBetweenEqAndMap=sizeBetweenEqAndMap;
        this.cell=cell;
        this.sizeBetweenMaps=sizeBetweenMaps;
        this.sizeText=sizeText;
        this.startEnemyMapLocation=startEnemyMapLocation;
        this.attackWhiteList=attackWhiteList;
        this.KristiFlag=KristiFlag;
        for(int i=0;i<5;i++)
        {
            shipTexture[i]=LoadTexture("resources/"+(i+1)+".png");
        }
        for(int i=0;i<6;i++)
        {
            menuShipTexture[i]=LoadTexture("resources/Menu/"+(i+1)+".png");
        }
        for(int i=0;i<6;i++)
        {
            attackTexture[i]=LoadTexture("resources/atak"+(i+1)+".png");
        }
    }

    void clear() {
        for(int i=0;i<5;i++)
            UnloadTexture(shipTexture[i]);
        for(int i=0;i<6;i++)
        {
            UnloadTexture(menuShipTexture[i]);
            UnloadTexture(attackTexture[i]);
        }
    }

    void draw(byte[][] myMap,byte[][] enemyMap,int numberOfAttack,int attackMode,int numberOfShoot,int [][]raidMap,int[][]ship,boolean rotate,int shiptype) {
        Raylib.DrawLine(0,eqSize,width,eqSize,BLACK);
        Jaylib.Vector2 tmp;
        drawMap(sizeBetweenEqAndMap,myMap,"Twoja Mapa");
        tmp=new Jaylib.Vector2(0,40);
        DrawTextEx(font,"Gold :"+gold,tmp,sizeText,(1f/10f)*sizeText*1,textColor);

        if(isPlacingShipTime)
        {
            drawPlaceShipStage(ship,rotate,shiptype);
        }
        else
        {
            drawAttackStage(enemyMap,numberOfAttack,attackMode,numberOfShoot,raidMap);
        }

    }

    void drawPlaceShipStage(int[][]ship,boolean rotate,int shiptype)
    {
        Raylib.Vector2 tmp=new Jaylib.Vector2(0,0);
        DrawTextEx(font,"Pozostaly czas ustawiania : "+(time>=60?time/60+" min " +time%60 +" s":time +" s"),tmp,20,(1f/10f)*sizeText*1,textColor);
        tmp=new Jaylib.Vector2(0,20);
        DrawTextEx(font,"Statki :"+numberOfShipToPlace,tmp,sizeText,(1f/10f)*sizeText*1,textColor);
        tmp=new Jaylib.Vector2(0,40);
        DrawTextEx(font,"Gold :"+gold,tmp,sizeText,(1f/10f)*sizeText*1,textColor);
        drawShipTable(ship,rotate,shiptype);

        drawOnMyMap(shiptype,rotate);

        if(KristiFlag) {
            for(int i=0;i<6;i++)
            {

                Jaylib.Rectangle rec=new Jaylib.Rectangle(0,0,256,256);
                Jaylib.Rectangle rec2=new Jaylib.Rectangle(startEnemyMapLocation+i%3*160,eqSize+sizeBetweenEqAndMap+i/3*160,100,100);
                int x=GetMouseX();
                int y=GetMouseY();
                if(x>rec2.x() && x<rec2.x()+rec2.width() && y>rec2.y() && y<rec2.y()+rec2.height() && attackWhiteList[0][i]==0)
                {
                    if(gold<attackWhiteList[1][i])
                        DrawRectangleRec(rec2,RED);
                    else
                        DrawRectangleRec(rec2,GREEN);
                }

                tmp=new Jaylib.Vector2(0,0);
                DrawRectangleLinesEx(rec2,2,BLACK);

                DrawTexturePro(attackTexture[i],rec,rec2,tmp,0,WHITE);

                tmp=new Jaylib.Vector2(rec2.x(),rec2.y());
                DrawTextEx(font,numberOfBombs[i]+"",tmp,20,(1f/10f)*sizeText*1,textColor);
                tmp.y(tmp.y()+rec2.height());
                DrawTextEx(font,attackWhiteList[1][i]+"$",tmp,20,(1f/10f)*sizeText*1,textColor);
                if(attackWhiteList[0][i]==1)
                {
                    Jaylib.Vector2 tmp2=new Jaylib.Vector2(rec2.x()+rec2.width(),rec2.y());
                    DrawLineEx(tmp,tmp2,2,RED);
                    tmp2.y(tmp2.y()+rec2.height());
                    tmp.y(rec2.y());
                    DrawLineEx(tmp,tmp2,2,RED);
                }
            }
        }
    }

    void drawAttackStage(byte[][] enemyMap,int numberOfAttack,int attackMode,int numberOfShoot,int [][]raidMap)
    {
        Raylib.Vector2 tmp;
        drawMap(startEnemyMapLocation,enemyMap,"Mapa Wroga");
        if(isMyMove)
        {
            tmp=new Jaylib.Vector2(0,0);
            DrawTextEx(font,"Moja Tura :"+time/100+" s",tmp,20,(1f/10f)*sizeText*1,textColor);

            drawOnEnemyMap(attackMode,numberOfAttack,raidMap,numberOfShoot);
        }
        else
        {
            tmp=new Jaylib.Vector2(0,0);
            DrawTextEx(font,"Przeciwnika Tura :"+time/100+" s",tmp,20,(1f/10f)*sizeText*1,textColor);
        }
        tmp=new Jaylib.Vector2(0,20);
        DrawTextEx(font,"Statki :"+numberOfShipAlive,tmp,20,(1f/10f)*sizeText*1,textColor);

        int start=startEnemyMapLocation+60;
        for(int i=0;i<6;i++)
        {
            if(i==attackMode-1)
                DrawRectangle(start+i*80,8,64,64,GREEN);
            else
                DrawRectangle(start+i*80,8,64,64,RED);
            Jaylib.Rectangle rec=new Jaylib.Rectangle(0,0,256,256);
            Jaylib.Rectangle rec2=new Jaylib.Rectangle(start+i*80,8,64,64);
            Jaylib.Vector2 v=new Jaylib.Vector2(0,0);
            tmp=new Jaylib.Vector2(start+i*80,72);

            DrawTextEx(font,KristiFlag?""+numberOfBombs[i]:attackWhiteList[1][i]+"$",tmp,20,(1f/10f)*sizeText*1,textColor);
            try{
                DrawTexturePro(attackTexture[i],rec,rec2,v,0,WHITE);
            }
            catch (Exception e)
            {

            }
        }
    }

    void drawShipTable(int [][]ship,boolean rotate,int type)
    {
        int startX=startEnemyMapLocation-sizeBetweenMaps+sizeBetweenEqAndMap;
        int startY=eqSize+sizeBetweenEqAndMap+n*cell/2-192;
        DrawRectangle(startX,startY,128,320,RED);
        DrawRectangle(startX+64,eqSize+sizeBetweenEqAndMap+n*cell/2+128,64,64,RED);
        if(type>-1 && type<5)
        {
            DrawRectangle(startX,startY+type*64,128,64,GREEN);
        }
        for(int i=0;i<5;i++)
        {
            Jaylib.Rectangle rec=new Jaylib.Rectangle(startX,startY+i*64,64,64);
            DrawRectangleLinesEx(rec,2,BLACK);
            rec=new Jaylib.Rectangle(startX+64,startY+i*64,64,64);
            DrawRectangleLinesEx(rec,2,BLACK);
        }
        Jaylib.Rectangle rec=new Jaylib.Rectangle(startX+64,startY+320,64,64);
        DrawRectangleLinesEx(rec,2,BLACK);

        Raylib.Vector2 start=new Raylib.Vector2();

        if(rotate)
            start.x(startX+128);
        else
            start.x(startX+64);
        Raylib.Vector2 tmp;
        DrawTexture(menuShipTexture[5], (int) start.x()-(rotate?64:0), startY+64*5,WHITE);
        for(int i=0;i<5;i++)
        {
            start.y(startY+64*i);

            Jaylib.Rectangle texture=new Jaylib.Rectangle(0,0,255,255);
            Jaylib.Rectangle position=new Jaylib.Rectangle(start.x(),start.y()+(rotate?64:0),64,64);
            Jaylib.Vector2 vec=new Jaylib.Vector2(rotate?64:0,0);
            Raylib.DrawTexturePro(menuShipTexture[i],texture,position,vec,rotate?90:0,WHITE);


            vec=new Jaylib.Vector2( start.x()+3-(rotate ?64:0),(int) start.y());
            DrawTextEx(font,""+(i+1),vec,16,(1f/10f)*sizeText*1,textColor);


        }

        start.x(startX+6);
        try{
            for(int i=0;i<5;i++)
            {
                start.y(startY+64*i);
                tmp=new Jaylib.Vector2( start.x()+20,start.y()+16);
                DrawTextEx(font,""+ship[1][i],tmp,32,(1f/10f)*sizeText*1,textColor);
            }

        }
        catch (Exception ignored)
        {

        }



    }

    void drawOnMyMap(int shipType,boolean rotate)
    {
        int x=GetMouseX();
        int y=GetMouseY();
        x-=sizeBetweenEqAndMap;
        y-=(sizeBetweenEqAndMap+eqSize);
        x/=cell;
        y/=cell;
        if(x>=0 && x<n && y>=0 && y<n)
        {
            int tmpx=x*cell+sizeBetweenEqAndMap;
            int tmpy=y*cell+sizeBetweenEqAndMap+eqSize;
            Jaylib.Rectangle rec;

            switch (shipType){
                case 4:
                    if(rotate && x-2>=0)
                    {
                        rec=new Jaylib.Rectangle(tmpx-cell*2,tmpy,cell,cell);
                        Jaylib.DrawRectangleRec(rec,shipColor2);
                        Jaylib.DrawRectangleLinesEx(rec,3,shipColorContour2);
                    }
                    else if(!rotate && y-2>=0)
                    {
                        rec=new Jaylib.Rectangle(tmpx,tmpy-cell*2,cell,cell);
                        Jaylib.DrawRectangleRec(rec,shipColor2);
                        Jaylib.DrawRectangleLinesEx(rec,3,shipColorContour2);
                    }
                    else
                        break;

                case 3:
                    if(rotate && x+2<n)
                    {
                        rec=new Jaylib.Rectangle(tmpx+cell*2,tmpy,cell,cell);
                        Jaylib.DrawRectangleRec(rec,shipColor2);
                        Jaylib.DrawRectangleLinesEx(rec,3,shipColorContour2);
                    }
                    else if(!rotate && y+2<n)
                    {
                        rec=new Jaylib.Rectangle(tmpx,tmpy+cell*2,cell,cell);
                        Jaylib.DrawRectangleRec(rec,shipColor2);
                        Jaylib.DrawRectangleLinesEx(rec,3,shipColorContour2);
                    }
                    else
                        break;
                case 2:
                    if(rotate && x-1>=0)
                    {
                        rec=new Jaylib.Rectangle(tmpx-cell,tmpy,cell,cell);
                        Jaylib.DrawRectangleRec(rec,shipColor2);
                        Jaylib.DrawRectangleLinesEx(rec,3,shipColorContour2);

                    }
                    else if(!rotate && y-1>=0)
                    {
                        rec=new Jaylib.Rectangle(tmpx,tmpy-cell,cell,cell);
                        Jaylib.DrawRectangleRec(rec,shipColor2);
                        Jaylib.DrawRectangleLinesEx(rec,3,shipColorContour2);
                    }
                    else
                        break;
                case 1:
                    if(rotate && x+1<n)
                    {
                        rec=new Jaylib.Rectangle(tmpx+cell,tmpy,cell,cell);
                        Jaylib.DrawRectangleRec(rec,shipColor2);
                        Jaylib.DrawRectangleLinesEx(rec,3,shipColorContour2);
                    }
                    else if(!rotate && y+1<n)
                    {
                        rec=new Jaylib.Rectangle(tmpx,tmpy+cell,cell,cell);
                        Jaylib.DrawRectangleRec(rec,shipColor2);
                        Jaylib.DrawRectangleLinesEx(rec,3,shipColorContour2);
                    }
                    else
                        break;
                case 0:
                    rec=new Jaylib.Rectangle(tmpx,tmpy,cell,cell);
                    Jaylib.DrawRectangleRec(rec,shipColor2);
                    Jaylib.DrawRectangleLinesEx(rec,3,shipColorContour2);
                    break;
            }

        }

    }

    void drawOnEnemyMap(int attackMode,int numberOfAttack,int[][]raidMap,int numberOfShoot)
    {
        int x=GetMouseX();
        int y=GetMouseY();
        x-=startEnemyMapLocation;
        y-=(sizeBetweenEqAndMap+eqSize);
        x/=cell;
        y/=cell;
        if(isOnEnemyMap(x,y))
        {
            int startX=x*cell+startEnemyMapLocation;
            int startY=y*cell+sizeBetweenEqAndMap+eqSize;
            drawX(startX,startY,mapAttackHit2);
            switch(attackMode)
            {
                case 1:
                    for(int i=0;i<3;i++)
                        for(int j=0;j<3;j++)
                        {
                            if(isOnEnemyMap(x-1+i,y-1+j))
                                drawX(startX+cell*(i-1),startY+cell*(j-1),mapAttackHit3);

                        }
                    break;
                case 2:
                    for(int i=0;i<5;i++)
                    {

                        if(isOnEnemyMap(x-2+i,y-2+i))
                            drawX(startX+cell*(i-2),startY+cell*(i-2),mapAttackHit3);
                        if(isOnEnemyMap(x-2+i,y+2-i))
                            drawX(startX+cell*(i-2),startY+cell*(2-i),mapAttackHit3);

                    }
                    break;
                case 3:
                    for(int i=0;i<5;i++)
                    {

                        if(isOnEnemyMap(x-2+i,y))
                            drawX(startX+cell*(-2+i),startY,mapAttackHit3);
                        if(isOnEnemyMap(x,y-2+i))
                            drawX(startX,startY+cell*(-2+i),mapAttackHit3);

                    }
                    break;
                case 4:
                    Raylib.Vector2 tmp2=new Jaylib.Vector2(startX+cell*2,startY-cell*2);
                    DrawTextEx(font,""+numberOfAttack,tmp2,20,(1f/10f)*sizeText*1,textColor);
                    for(int i=0;i<3;i++)
                    {
                        if(isOnEnemyMap(x-1+i,y))
                            drawX(startX+cell*(-1+i),startY,mapAttackHit3);
                        if(isOnEnemyMap(x,y-1+i))
                            drawX(startX,startY+cell*(-1+i),mapAttackHit3);
                    }
                    if(raidMap!=null)
                    {
                        Jaylib.Color tmp= new Jaylib.Color(255,255,0,200);
                        startX=startEnemyMapLocation;
                        startY=sizeBetweenEqAndMap+eqSize;
                        for(int i=0;i<numberOfShoot;i++)
                        {
                            if(isOnEnemyMap(raidMap[i][1],raidMap[i][0]))
                                drawX(startX+cell*raidMap[i][1],startY+cell*raidMap[i][0],tmp );
                        }
                    }
                    break;

            }
        }
    }

    void drawX(int x, int y, Jaylib.Color color) {
        Jaylib.Vector2 start=new Jaylib.Vector2(x,y);
        Jaylib.Vector2 end =new Jaylib.Vector2(x+cell,y+cell);
        Jaylib.DrawLineEx(start,end,3,color);
        start.x(x+cell);
        end.x(x);
        Jaylib.DrawLineEx(start,end,3,color);
    }

    void drawMap(int x,byte[][] usedMap,String name) {
        if(name.equals("Twoja Mapa"))
        {
            for(int i=0;i<tmpShipOnMap;i++)
            {
                Jaylib.Rectangle texture=new Jaylib.Rectangle(0,0,shipTexture[shipOnMapPosition[i][0]].width(),shipTexture[shipOnMapPosition[i][0]].height());
                Jaylib.Rectangle rec=new Jaylib.Rectangle(shipOnMapPosition[i][1],shipOnMapPosition[i][2],shipOnMapPosition[i][3],shipOnMapPosition[i][4]);
                Jaylib.Vector2 vec=new Jaylib.Vector2(shipOnMapPosition[i][5]==0?0:cell,0);
                DrawTexturePro(shipTexture[shipOnMapPosition[i][0]],texture,rec,vec,shipOnMapPosition[i][5],WHITE);
            }
        }

        Jaylib.Vector2 tmp;
        for(byte i=0;i<=n;i++)
        {
            Jaylib.DrawLine(x,i*cell+sizeBetweenEqAndMap+eqSize,x+n*cell,i*cell+sizeBetweenEqAndMap+eqSize,mapLineColor);
            if(i!=n)
            {
                tmp=new Jaylib.Vector2(x-sizeBetweenEqAndMap/2,i*cell+sizeBetweenEqAndMap+eqSize);
                DrawTextEx(font,""+(i+1),tmp,sizeText,(1f/10f)*sizeText*1,textColor);
            }
        }
        for(byte i=0;i<=n;i++)
        {
            Jaylib.DrawLine(x+i*cell,sizeBetweenEqAndMap+eqSize,x+i*cell,n*cell+sizeBetweenEqAndMap+eqSize,mapLineColor);
            if(i!=n)
            {
                tmp=new Jaylib.Vector2(i*cell+cell/4+x,sizeBetweenEqAndMap/2+eqSize);
                DrawTextEx(font,(char)('a'+i)+"",tmp,sizeText,(1f/10f)*sizeText*1,textColor);
            }
        }
        for(int i=0;i<n;i++)
            for(int j=0;j<n;j++)
            {
                Jaylib.Rectangle rec=new Jaylib.Rectangle(x+cell*j,sizeBetweenEqAndMap+eqSize+i*cell,cell,cell);
                switch(usedMap[i][j]) {
                    case 0:
                        break;
                    case 1:
                        Jaylib.DrawCircle(x+j*cell+cell/2,sizeBetweenEqAndMap+eqSize+i*cell+cell/2,cell/2,mapAttackMiss);
                        Jaylib.DrawCircle(x+j*cell+cell/2,sizeBetweenEqAndMap+eqSize+i*cell+cell/2,cell/2-3,WHITE);
                        break;
                    case 2:
                        Jaylib.Vector2 start=new Jaylib.Vector2(x+cell*j,sizeBetweenEqAndMap+eqSize+i*cell);
                        Jaylib.Vector2 end =new Jaylib.Vector2(x+cell*(j+1),sizeBetweenEqAndMap+eqSize+(i+1)*cell);
                        Jaylib.DrawLineEx(start,end,3,mapAttackHit);
                        start.x(x+cell*(j+1));
                        end.x(x+cell*(j));
                        Jaylib.DrawLineEx(start,end,3,mapAttackHit);
                        break;
                    case 8:
                        if(IsKeyDown(KEY_TAB))
                        {
                            Jaylib.DrawCircle(x+j*cell+cell/2,sizeBetweenEqAndMap+eqSize+i*cell+cell/2,cell/2,GREEN);
                            Jaylib.DrawCircle(x+j*cell+cell/2,sizeBetweenEqAndMap+eqSize+i*cell+cell/2,cell/2-3,DARKGREEN);
                        }

                        break;
                }
            }

        Raylib.Vector2 tmp2=new Jaylib.Vector2(x,n*cell+eqSize+sizeBetweenEqAndMap);
        DrawTextEx(font,name,tmp2,20,(1f/10f)*sizeText*1,textColor);

    }

    void gameEnd(boolean isOpponentLeft,boolean isSomeoneWin,boolean lost) {
        if(isOpponentLeft)
        {

            Jaylib.DrawText("WYGRALES",432,(int)((1.0/3.0)*height)+2,100,BLACK);
            Jaylib.DrawText("WYGRALES",430,(int)((1.0/3.0)*height),100,RED);
        }

        if(isSomeoneWin) {
            if (lost) {

                Jaylib.DrawText("PRZEGRALES",432,(int)((1.0/3.0)*height)+2,100,BLACK);
                Jaylib.DrawText("PRZEGRALES",430,(int)((1.0/3.0)*height),100,RED);
            }
            else
            {

                Jaylib.DrawText("WYGRALES",432,(int)((1.0/3.0)*height)+2,100,BLACK);
                Jaylib.DrawText("WYGRALES",430,(int)((1.0/3.0)*height),100,RED);
            }
        }

    }

    boolean isOnEnemyMap(int x,int y) {
        if(x>=0 && x<n && y>=0 && y<n)
            return true;
        return false;
    }

    public void setNumberOfShipAlive(int numberOfShipAlive) {
        this.numberOfShipAlive = numberOfShipAlive;
    }

    public void setNumberOfShipToPlace(int numberOfShipToPlace) {
        this.numberOfShipToPlace = numberOfShipToPlace;
    }

    public void setMyMove(boolean myMove) {
        isMyMove = myMove;
    }

    public void setIsPlacingShipTime(boolean isplacingShipTime) {
        this.isPlacingShipTime = isplacingShipTime;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setShipOnMap(int shipOnMap)
    {
        this.shipOnMap=shipOnMap;
        tmpShipOnMap=0;
        shipOnMapPosition=new int[shipOnMap][6];
    }

    public void setShipOnMapPosition(int type,int x,int y,int width,int height,int rotation)
    {
        shipOnMapPosition[tmpShipOnMap][0]=type;
        shipOnMapPosition[tmpShipOnMap][1]=x;
        shipOnMapPosition[tmpShipOnMap][2]=y;
        shipOnMapPosition[tmpShipOnMap][3]=width;
        shipOnMapPosition[tmpShipOnMap][4]=height;
        shipOnMapPosition[tmpShipOnMap][5]=rotation;
        tmpShipOnMap++;
    }

    public void setGold(int gold)
    {
        this.gold=gold;
    }

    public void setNumberOfBombs(int []numberOfBombs)
    {
        this.numberOfBombs=numberOfBombs;
    }
}
