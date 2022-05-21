import com.raylib.Jaylib;

import static com.raylib.Jaylib.GREEN;
import static com.raylib.Jaylib.RED;
import static com.raylib.Raylib.DrawRectangle;

public class Collision {
    boolean isPress;
    int mouseX;
    int mouseY;
    MapParameters parameters;

    Collision(MapParameters parameters)
    {
        this.parameters=parameters;
    }

    void upData()
    {
        mouseX= Jaylib.GetMouseX();
        mouseY=Jaylib.GetMouseY();
        isPress=Jaylib.IsMouseButtonPressed(0);
    }

    String placeShipCollision()
    {
        if(!isPress)
            return "";
        int x=(mouseX-parameters.getStartMyLocationMap())/ parameters.getCell();
        int y=(mouseY-parameters.getMapStartY())/parameters.getCell();
        if(x<0 || x>=parameters.getN() || y<0 || y>=parameters.getN())
            return "";
        String res=y+"";
        res+=(char)(x+'a');
        return res;
    }

    int checkShipCollision(int usedShip)
    {
        if(!isPress)
            return usedShip;
        int x=mouseX-parameters.getStartShipLocationX();
        int y=mouseY-parameters.getStartShipLocationY();
        if(x<0 || y<0 || x > parameters.getShipSizeX() || y > parameters.getShipSizeY() * 6)
            return usedShip;

        y/=parameters.getShipSizeY();
        if(y != 5)
            return usedShip == y ? -1 : y;

        if(x - parameters.getShipSizeX()/2 > 0)
            return usedShip+5;

        return usedShip;
    }

    String attackCollision()
    {
        if(!isPress)
            return "";
        int x=(mouseX-parameters.getStartEnemyLocationMap())/parameters.getCell();
        int y=(mouseY-parameters.getMapStartY())/parameters.getCell();
        if(x<0 || x>=parameters.getN() || y<0 || y>=parameters.getN() )
            return "";

        return y+""+(char)(x+'a');
    }

    int attackModeCollision(int attackMode)
    {
        if(!isPress)
            return attackMode;
        int x=mouseX - parameters.getPowerLocationX();
        int y=mouseY - parameters.getPowerLocationY();
        if(y<0 || y>parameters.getSizePower())
            return attackMode;
        for(int i = 0 ; i < 6 ; i++)
        {
            int size = i * (parameters.getSizePower() + parameters.getSizeBetweenPowers());
            if(x < size)
                return attackMode;
            if(x < size + parameters.getSizePower())
                return attackMode == i+1 ? 0 : i + 1;
        }
        return attackMode;
    }

    boolean endPlaceShip()
    {
        if(!isPress)
            return false;
        int x=mouseX-240;
        int y=mouseY-30;
        if(x>=0 && x<= 120 && y>=0 && y<=70)
            return true;
        return false;
    }

    int buyAttackCollision()
    {
        if(!isPress)
            return 0;
        int x=mouseX - parameters.getPowerBuyLocationX();
        int y=mouseY - parameters.getPowerBuyLocationY();
        if(x<0 || y<0 || y > parameters.getSizeBuyButton() * 2 + parameters.getSizeBetweenBuyButton())
            return 0;
        for(int i = 0 ; i < 6 ; i++)
        {
            int size=i % 3 * (parameters.getSizeBuyButton() + parameters.getSizeBetweenBuyButton());
            int size2=i/3 *(parameters.getSizeBuyButton() + parameters.getSizeBetweenBuyButton());
            if(x < size && y <size2)
                continue;
            if(x < size + parameters.getSizeBuyButton() && y < size2 + parameters.getSizeBuyButton())
                return i+1;
        }
        return 0;
    }

    int getMouseX()
    {
        return mouseX;
    }

    int getMouseY()
    {
        return mouseY;
    }

    boolean getMousePress()
    {
        return isPress;
    }
}
