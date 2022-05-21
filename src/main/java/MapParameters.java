import com.raylib.Jaylib;
import com.raylib.Raylib;

public class MapParameters {
    final private int mapStartY;
    final private int startEnemyLocationMap;
    final private int startMyLocationMap;
    final private int cell;
    final private int n;

    final private int startShipLocationX;
    final private int startShipLocationY;
    final private int shipSizeX;
    final private int shipSizeY;

    final private int powerLocationX;
    final private int powerLocationY;
    final private int sizePower;
    final private int sizeBetweenPowers;

    final private int powerBuyLocationX;
    final private int powerBuyLocationY;
    final private int sizeBuyButton;
    final private int sizeBetweenBuyButton;

    MapParameters(int n)
    {
        mapStartY=150;
        startMyLocationMap = 50;
        cell=(22*20)/n;
        this.n = n;
        startEnemyLocationMap = n*cell+306;

        startShipLocationX = startEnemyLocationMap-206;
        startShipLocationY = n*cell/2-42;
        shipSizeX = 128;
        shipSizeY = 64;

        powerLocationX = startEnemyLocationMap;
        powerLocationY = 8;
        sizePower = 64;
        sizeBetweenPowers = 20;

        powerBuyLocationX = startEnemyLocationMap;
        powerBuyLocationY = mapStartY;
        sizeBuyButton = 100;
        sizeBetweenBuyButton = 60;


    }

    public int getMapStartY() {
        return mapStartY;
    }

    public int getStartEnemyLocationMap() {
        return startEnemyLocationMap;
    }

    public int getStartMyLocationMap() {
        return startMyLocationMap;
    }

    public int getCell() {
        return cell;
    }

    public int getN() {
        return n;
    }

    public int getStartShipLocationX() {
        return startShipLocationX;
    }

    public int getStartShipLocationY() {
        return startShipLocationY;
    }

    public int getShipSizeX() {
        return shipSizeX;
    }

    public int getShipSizeY() {
        return shipSizeY;
    }

    public int getPowerLocationX() {
        return powerLocationX;
    }

    public int getPowerLocationY() {
        return powerLocationY;
    }

    public int getSizePower() {
        return sizePower;
    }

    public int getSizeBetweenPowers() {
        return sizeBetweenPowers;
    }

    public int getPowerBuyLocationX() {
        return powerBuyLocationX;
    }

    public int getPowerBuyLocationY() {
        return powerBuyLocationY;
    }

    public int getSizeBuyButton() {
        return sizeBuyButton;
    }

    public int getSizeBetweenBuyButton() {
        return sizeBetweenBuyButton;
    }


}
