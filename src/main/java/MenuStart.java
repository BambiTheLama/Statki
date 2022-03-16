import com.raylib.Jaylib;
import com.raylib.Raylib;
import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.WindowShouldClose;

public class MenuStart {

    String who;
    String ip;
    String port;
    int mapSize;
    int startTime;
    int moveTime;
    int[][] ship;
    byte[][] attackWhiteList;
    int startGold;


    public static boolean main(String[] arg) {
        InitWindow(500,400,"MENU");
        while (!WindowShouldClose()) {
            BeginDrawing();
            ClearBackground(WHITE);
            EndDrawing();
        }
        return true;
    }


}
