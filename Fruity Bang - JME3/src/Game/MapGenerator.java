package Game;

import GameController.WormGhost;
import GameModel.Bush;
import GameModel.Worm;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Dam Linh
 */
public class MapGenerator implements GameConstant {

    /**
     * available positions of the map. position[] is x location. positions[][]
     * is y location the third array has 1 element and indicate what object is
     * at the location x,y
     */
    private static Random random = new Random();

    public static void generate(ArrayList<Worm> worms, ArrayList<Bush> bushes) {
        initPositions();

        for (int i = 0; i < bushes.size(); i++) {
            byte x = 1;
            byte y = 1;
            while (!isAvalable(x, y)) {
                // from 1 to MAP_WIDTH -1
                x = (byte) (random.nextInt(MAP_WIDTH - 2) + 1);
                // from 1 to MAP_HEIGHT -1
                y = (byte) (random.nextInt(MAP_HEIGHT - 2) + 1);
            }
            POSITIONS[x][y][0] = BUSH;
            BUSH_POS[x][y][0] = bushes.get(i);
            bushes.get(i).render(x, y);
        }

        for (int i = 0; i < worms.size(); i++) {
            byte x = 1;
            byte y = 1;
            while (!isAvalable(x, y)
                    || (x <= 3 && y <= 3)) {// position of worms must be 3 cells away from SA
                // from 1 to MAP_WIDTH -1
                x = (byte) (random.nextInt(MAP_WIDTH - 2) + 1);
                // from 1 to MAP_HEIGHT -1
                y = (byte) (random.nextInt(MAP_HEIGHT - 2) + 1);
            }
            POSITIONS[x][y][0] = WORM;
            WORM_POS[i][0] = worms.get(i).getControl(WormGhost.class).getPhysicsLocation();
            WORM_POS[i][1] = worms.get(i);
            worms.get(i).render(x,y);
        }
//        printDebug();
    }

    private static void initPositions() {
        
        for (int i = 0; i < MAP_WIDTH; i++) {
            for (int j = 0; j < MAP_HEIGHT; j++) {
                POSITIONS[i][j][0] = EMPTY;
            }
            
        }
        
        for (int i = 0; i < MAP_WIDTH; i++) {
            POSITIONS[i][MAP_HEIGHT - 1][0] = ROCK;
            POSITIONS[i][0][0] = ROCK;
        }

        for (int i = 1; i < MAP_HEIGHT - 1; i++) {
            POSITIONS[0][i][0] = ROCK;
            POSITIONS[MAP_WIDTH - 1][i][0] = ROCK;
        }
        
        for (int i = 2; i < MAP_WIDTH - 2; i += 2) {
            for (int j = 2; j < MAP_HEIGHT - 2; j += 2) {
                POSITIONS[i][j][0] = ROCK;
            }
        }
    }

    private static boolean isAvalable(byte x, byte y) {
        if (POSITIONS[x][y][0] != EMPTY
                || (x <= 2 && y <= 2)) {// the position around Sa must be empty
            return false;
        } else {
            return true;
        }
    }
    
    private static void printDebug(){
        for (int i = POSITIONS[0].length - 1; i >= 0; i--) {
            for (int j = 0; j < POSITIONS.length; j++) {
                switch (POSITIONS[j][i][0]) {
                    case EMPTY:
                        System.out.print(" .");
                        break;
                    case ROCK:
                        System.out.print("#.");
                        break;
                    case BUSH:
                        System.out.print("*.");
                        break;
                    case WORM:
                        System.out.print("~.");
                }
            }
            System.out.println();
        }
        System.out.println("\n");
    }
}
