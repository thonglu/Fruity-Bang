package Game;

/**
 *
 * @author Thong
 */
public interface GameConstant {

    //the place holder of map
    static final float PLACE_HOLDER = 5;
    /**
     * include boundery
     */
    static final int MAP_HEIGHT = 17;
    /**
     * include boundery
     */
    static final int MAP_WIDTH = 21;
    
    static final byte[][][] POSITIONS = new byte[MAP_WIDTH][MAP_HEIGHT][1];
    
    static final Object[][][] BUSH_POS = new Object[MAP_WIDTH][MAP_HEIGHT][1];
    
    static final Object[][] SA_POS = new Object[1][2];
    
    static final Object[][] WORM_POS = new Object[10][2];
    
    static final Object[][][] RUBY_POS = new Object[MAP_WIDTH][MAP_HEIGHT][1];
    
    static final Object[][][] BOMB_POS = new Object[MAP_WIDTH][MAP_HEIGHT][1];
    
    //constant value of physic thing on map
    static final byte EMPTY = 0;
    static final byte BUSH = 1;
    static final byte ROCK = 2;
    static final byte WORM = 3;
    static final byte FRUIT = 4;
    static final byte RUBY = 5;
    static final byte GATE = 6;
    
    //mute
    final static boolean[] isMute = new boolean[1];
//    static boolean isMute = false;
    
    //OBject
    final static String STRAWBERRY_OBJECT ="Models/strawberry/strawberry.j3o";
    final static String APPLE_OBJECT = "Models/apple/Apple.j3o";
    
    final static String SRTAWBERRY_MAT = "Materials/strawberry.j3m";
    final static String APPLE_MAT = "Materials/apple.j3m";
            
}
