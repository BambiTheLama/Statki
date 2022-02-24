import com.raylib.Raylib;
import org.bytedeco.javacpp.BytePointer;

import static com.raylib.Jaylib.RAYWHITE;
import static com.raylib.Jaylib.VIOLET;
import static com.raylib.Raylib.*;

public class Main {

    public static void main(String args[]) {
        int width=1280,height=720;

        InitWindow(width, height, "Statki");

        SetTargetFPS(60);
        GameScreen game=new GameScreen((byte) 12);
        while (!WindowShouldClose()) {

            if(game.collision())
            {

            }
            BeginDrawing();
            ClearBackground(RAYWHITE);
            game.draw(width);

            EndDrawing();
        }
        CloseWindow();
    }
}