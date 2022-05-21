import com.raylib.Jaylib;
import com.raylib.Raylib;
import static com.raylib.Jaylib.*;


public class DrawBoard {
    MapParameters parameters;
    Texture[] ship;
    Texture[] attack;
    Texture[] shipOnMap;
    int[] shipType;
    Jaylib.Rectangle[] shipPos;
    boolean isRotate[];
    int numberOfShip=0;
    int maxShip;
    Jaylib.Rectangle screen =new Jaylib.Rectangle(0,0,1280,720);
    private int[][] attackWhiteList;
    private int[] bombs;
    String []toBombs=new String[6];
    boolean flagKristi=false;

    DrawBoard(MapParameters parameters,int numberOfShip,int [][]attackWhiteList,boolean flagKristi,int []bombs)
    {
        Colors.defolt();
        this.parameters = parameters;
        ship      = new Texture[6];
        attack    = new Texture[6];
        shipOnMap = new Texture[5];
        for(int i = 0 ; i < 6 ; i++)
        {
            ship[i]=LoadTexture("resources/Menu/"+(i+1)+".png");
            attack[i]=LoadTexture("resources/atak"+(i+1)+".png");
        }
        for(int i=0;i<5;i++)
        {
            shipOnMap[i]=LoadTexture("resources/"+(i+1)+".png");
        }
        shipPos=new Jaylib.Rectangle[numberOfShip];
        shipType=new int[numberOfShip];
        isRotate=new boolean[numberOfShip];
        maxShip=numberOfShip;
        this.attackWhiteList = attackWhiteList;
        this.bombs = bombs;
        this.flagKristi=flagKristi;
    }

    void draw(Map map,boolean placeShipStage,boolean myMove)
    {
        drawShipOnMap();
        drawMap(map.getMyMap(), parameters.getStartMyLocationMap());

        if(!placeShipStage)
            drawMap(map.getEnemyMap(), parameters.getStartEnemyLocationMap());
        DrawLine(0,100,1280,100,BLACK);

        Colors.updata(placeShipStage,myMove);
        DrawRectangleLinesEx(screen,5,Colors.screanColor);
        if(!placeShipStage)
        {
            drawMyMove(myMove);

        }
    }

    void drawBombsMouse(int x,int y,int bombUse)
    {
        int mapSize = parameters.getCell() * parameters.getN();
        int startX = x - parameters.getStartEnemyLocationMap();
        if(startX < 0 || startX > mapSize)
            return;
        int startY =y- parameters.getMapStartY() ;
        if(startY < 0 || startY > mapSize)
            return;
        x-=startX%parameters.getCell();
        y-=startY%parameters.getCell();
        DrawOnMap.drawBombWhereMouse(x,y, parameters.getCell(), bombUse);
    }

    void drawPlaceAttack(int [][] pos,int numberOfAttack)
    {
        if(pos==null)
            return;
        int startX = parameters.getStartEnemyLocationMap();
        int startY = parameters.getMapStartY();
        int cell= parameters.getCell();
        for(int i=0;i<numberOfAttack;i++)
            DrawOnMap.drawAttack(pos[i][0] * cell + startX,pos[i][1] * cell + startY, parameters.getCell());
    }

    void drawShipMouse(int x,int y,int shipUse,boolean rotate)
    {
        if(shipUse<0)
            return;
        int mapSize = parameters.getCell() * parameters.getN();
        int startX = x - parameters.getStartMyLocationMap();
        int startY =y- parameters.getMapStartY() ;

        int start=parameters.getCell() * shipUse/2;
        int end=parameters.getCell() * ((shipUse+1)/2);

        if(startX - (rotate?start:0) < 0 || startX + (rotate?end:0) > mapSize)
            return;

        if(startY - (rotate?0:start) < 0 || startY + (rotate?0:end) > mapSize)
            return;

        x-=startX%parameters.getCell();
        y-=startY%parameters.getCell();

        if(rotate)
            x+=((shipUse+1)/2+1) * parameters.getCell();
        else
            y-=shipUse/2 * parameters.getCell();

        DrawOnMap.drawShipWhereMouse(x,y, parameters.getCell(), shipUse+1,rotate,shipOnMap[shipUse]);
    }

    void drawMap(int[][]map,int x)
    {
        int n = parameters.getN();
        int cell=parameters.getCell();
        int startY = parameters.getMapStartY();
        int endY = startY + n * cell;

        for(int i = 0 ; i <= n ; i++)
        {

            Jaylib.DrawLine(x + cell * i, startY,x + cell * i, endY, BLACK);
        }
        Raylib.Vector2 vec=new Jaylib.Vector2(0,startY-cell-5);
        for(int i=0;i<n;i++)
        {
            vec.x(x+cell*i+cell/3);
            DrawTextEx(Colors.font,""+(char)(i+'a'),vec,cell,1/10*cell,Colors.textColor);
        }
        vec.x(x-cell);
        for(int i=0;i<n;i++)
        {
            vec.y(startY+cell*i);
            DrawTextEx(Colors.font,""+(i+1),vec,cell,1/10*cell,Colors.textColor);
        }
        int endX=x + n * cell;
        for(int i = 0 ; i <= n ; i++)
        {
            Jaylib.DrawLine(x,startY + cell * i, endX,startY + cell * i, BLACK);
        }
        for(int i=0;i<n;i++)
            for(int j=0;j<n;j++)
            {
                DrawOnMap.drawSign(map[i][j],x + cell * j,startY + cell * i, cell);
            }
    }

    void drawTime(String time)
    {
        Raylib.Vector2 vec=new Jaylib.Vector2(15,40);
        DrawRectangle(10,30,70,40,RED);
        DrawTextEx(Colors.font,time,vec,32,3,Colors.textColor);
    }

    void drawMyMove(boolean myMove)
    {
        Raylib.Vector2 vec=new Jaylib.Vector2(245,40);
        DrawRectangle(240,30,220,40,RED);
        DrawTextEx(Colors.font,(myMove?"Moja ":"Przeciwka ")+"Tura",vec,32,3,Colors.textColor);
    }

    void drawGold(int gold)
    {
        Raylib.Vector2 vec=new Jaylib.Vector2(105,40);
        DrawRectangle(100,30,120,40,RED);
        DrawTextEx(Colors.font,gold+"$",vec,32,3,Colors.textColor);
    }

    void drawHowManyShip(int numberOfShip)
    {
        Raylib.Vector2 vec=new Jaylib.Vector2(485,40);
        DrawRectangle(480,30,140,40,RED);
        DrawTextEx(Colors.font,"Statki:"+numberOfShip,vec,32,3,Colors.textColor);
    }

    void endTurn(boolean end)
    {
        Raylib.Vector2 vec=new Jaylib.Vector2(245,40);
        DrawRectangle(240,30,220,40,end?GREEN:RED);
        DrawTextEx(Colors.font,"Koniec Tury",vec,32,3,Colors.textColor);
    }

    void drawShip(int[] numberShip,int usedShip,boolean rotate)
    {
        int x = parameters.getStartShipLocationX();
        int y = parameters.getStartShipLocationY();
        int sizeX = parameters.getShipSizeX();
        int sizeY = parameters.getShipSizeY();
        Raylib.Vector2 vec = new Raylib.Vector2();
        vec.x(x+sizeX/2+4);

        Jaylib.Rectangle pos=new Jaylib.Rectangle(x+(rotate?sizeX:sizeX/2),y,sizeX/2,sizeY);
        Jaylib.Rectangle texture=new Jaylib.Rectangle(0,0,256,256);
        Raylib.Vector2 vec2=new Raylib.Vector2();

        for(int i=0;i<5;i++)
        {
            DrawRectangle(x,y + sizeY * i, sizeX, sizeY, i == usedShip ? GREEN : RED);

            DrawTexturePro(ship[i], texture, pos, vec2, rotate ? 90 : 0, WHITE);
            pos.y(y+(i+1)*sizeY);
            vec.y(sizeY*i+y);
            DrawTextEx(Colors.font,""+(i+1),vec,16,2,Colors.textColor);

            vec.y(sizeY*i+y+5);
            vec.x(x+16);
            DrawTextEx(Colors.font,numberShip[i]+"",vec,56,8,Colors.textColor);
            vec.x(x+sizeX/2+4);

        }
        for(int i=0;i<6;i++)
            Jaylib.DrawLine(x,y + sizeY * i,x + sizeX,y + sizeY * i,BLACK);
        for(int i=0;i<3;i++)
        {
            Jaylib.DrawLine(x + sizeX/2 * i,y,x + sizeX/2 * i,y+sizeY*5,BLACK);
        }
        DrawRectangle(x+sizeX/2,y + sizeY * 5, sizeX/2, sizeY,RED);
        texture.width(64);
        texture.height(64);
        if(rotate)
            pos.x(x+sizeX/2);
        DrawTexturePro(ship[5],texture,pos,vec2,0,WHITE);
        DrawLine(x + sizeX/2,y + sizeY * 5,x + sizeX/2,y+ sizeY * 6,BLACK);
        DrawLine(x + sizeX,y + sizeY * 5,x + sizeX,y+ sizeY * 6,BLACK);
        DrawLine(x + sizeX/2,y + sizeY * 6,x + sizeX,y+ sizeY * 6,BLACK);
    }

    void drawBombs(int usedBomb)
    {
        int x=parameters.getPowerLocationX();
        int y=parameters.getPowerLocationY();
        int size=parameters.getSizePower();
        int sizeBetween=parameters.getSizeBetweenPowers();
        Jaylib.Rectangle pos=new Jaylib.Rectangle(x,y,size,size);
        Raylib.Vector2 vec=new Jaylib.Vector2(0,0);
        Jaylib.Rectangle texture=new Jaylib.Rectangle(0,0,256,256);
        Raylib.Vector2 posText=new Jaylib.Vector2(x,y+size+5);
        for(int i=0;i<6;i++)
        {
            DrawRectangleRec(pos,i+1==usedBomb?GREEN:RED);
            DrawRectangleLinesEx(pos,2,BLACK);
            DrawTexturePro(attack[i],texture,pos,vec,0,WHITE);
            String text=flagKristi?bombs[i]+"":attackWhiteList[1][i]+" $";
            DrawTextEx(Colors.font,text,posText,20,2,Colors.textColor);
            if(attackWhiteList[0][i]==1)
            {
                DrawOnMap.drawX((int) pos.x()+2,y,size, BLACK);
                DrawOnMap.drawX((int) pos.x()-2,y,size, BLACK);
                DrawOnMap.drawX((int) pos.x(),y,size, RED);

            }

            pos.x(x + (i + 1) * (sizeBetween+size));
            posText.x(pos.x());

        }
    }

    void drawBombsShop()
    {
        int x=parameters.getPowerBuyLocationX();
        int y=parameters.getPowerBuyLocationY();
        int size=parameters.getSizeBuyButton();
        int sizeBetween= parameters.getSizeBetweenBuyButton();
        Jaylib.Rectangle texture=new Jaylib.Rectangle(0,0,256,256);
        Jaylib.Rectangle pos=new Jaylib.Rectangle(0,0,size,size);
        Jaylib.Vector2 vec=new Jaylib.Vector2(0,0);
        Jaylib.Vector2 textpos=new Jaylib.Vector2(0,0);
        for(int i=0;i<6;i++)
        {
            pos.x(x + (size+sizeBetween)*(i%3));
            pos.y(y + (size+sizeBetween)*(i/3));
            DrawRectangleRec(pos,RED);
            DrawTexturePro(attack[i],texture,pos,vec,0,WHITE);
            if(attackWhiteList[0][i]==1)
            {
                DrawOnMap.drawX((int) pos.x()-2, (int) pos.y(),size,BLACK);
                DrawOnMap.drawX((int) pos.x()+2, (int) pos.y(),size,BLACK);
                DrawOnMap.drawX((int) pos.x(), (int) pos.y(),size,RED);
            }
            textpos.x(pos.x());
            textpos.y(pos.y());
            DrawTextEx(Colors.font,bombs[i]+"",textpos,30,1,Colors.textColor);
            textpos.y(textpos.y()+size+5);
            DrawTextEx(Colors.font,attackWhiteList[1][i]+"$",textpos,30,1,Colors.textColor);
        }
    }

    void drawShipOnMap()
    {
        Jaylib.Rectangle texture= new Jaylib.Rectangle(0,0,0,0);
        Jaylib.Vector2 posMove=new Jaylib.Vector2(0,0);
        for(int i=0;i<numberOfShip;i++)
        {
            int type=shipType[i];
            texture.width(shipOnMap[type].width());
            texture.height(shipOnMap[type].height());
            DrawTexturePro(shipOnMap[type],texture,shipPos[i],posMove,isRotate[i]?90:0,WHITE);
        }
    }

    void addShip(int startX,int startY,int type,boolean rotate)
    {
        if(numberOfShip>=maxShip)
            return;
        int cell= parameters.getCell();
        int startShipX = parameters.getStartMyLocationMap() + (startX + (rotate ? type + 1 :0) ) * cell;
        int startShipY = parameters.getMapStartY() + startY * cell;
        int height = cell * (type + 1);
        int wight = cell;
        Jaylib.Rectangle pos = new Jaylib.Rectangle(startShipX,startShipY,wight,height);
        shipPos[numberOfShip] = pos;
        shipType[numberOfShip] = type;
        isRotate[numberOfShip] = rotate;
        numberOfShip++;
    }

}
