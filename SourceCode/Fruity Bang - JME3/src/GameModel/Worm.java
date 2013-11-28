package GameModel;

import Game.GameConstant;
import GameController.WormGhost;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.ArrayDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Thong
 */
public class Worm extends CuteModel implements GameConstant, AnimEventListener {

    private Vector3f walkDirection = new Vector3f();
    private Vector3f prevWalkDirection = new Vector3f();
    private byte previousDirection = 0;
    private CharacterControl wormChar;
    private AnimChannel animationChannel;
    private final float speed;
    private static final byte LEFT = 1;
    private static final byte RIGHT = 2;
    private static final byte UP = 3;
    private static final byte DOWN = 4;
    private static final byte LEFT_TEMP = 5;
    private static final byte RIGHT_TEMP = 6;
    private static final byte UP_TEMP = 7;
    private static final byte DOWN_TEMP = 8;
    private static final byte STUCKED = 0;
    private static final byte STOP = -1;
    private ArrayDeque<Byte> wayList;
    private Future kuteFuture = null;
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
    private Callable<ArrayDeque<Byte>> findWay;
    private byte[] saLocation;
    private final byte TIMES_PER_CELL = 10;
    private byte counter = 0;
    private final int IGNORE_FRAME;
    private boolean isSmart = true;
    private boolean isDead = false;
    private byte smartLv = 10;

    //<editor-fold defaultstate="collapsed" desc="contructor">
    public Worm(Spatial s, float speedRatio, byte smartLv) {
        super(s);
        setName("Worm");
        SphereCollisionShape capsule = new SphereCollisionShape(PLACE_HOLDER);
        wormChar = new WormGhost(capsule, this);

        AnimControl animationControl = s.getControl(AnimControl.class);
        animationControl.addListener(this);

        animationChannel = s.getControl(AnimControl.class).createChannel();
        addControl(wormChar);

        speed = (float) (PLACE_HOLDER / TIMES_PER_CELL + .05f) * speedRatio * 0.5f;
        this.smartLv = smartLv;
        if (smartLv == 10) {
            IGNORE_FRAME = 3;
        } else if (smartLv == 8) {
            IGNORE_FRAME = 7;
        } else if (smartLv == 6) {
            IGNORE_FRAME = 14;
        } else {
            IGNORE_FRAME = 40;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="render">
    public void render(int x, int y) {
        s.rotate(0, -(float) (Math.PI / 2), 0);
        render(x, y, wormChar, .4f);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="move">
    private void move(byte direction) {
//        System.out.println(
//                "Worm MOVE Worm MOVE Worm MOVE Worm MOVE Worm MOVE Worm MOVE ");
        if ("Die".equals(animationChannel.getAnimationName())) {
            return;
        }
        if (isPause) {
            walkDirection.set(0, 0, 0);
            wormChar.setWalkDirection(walkDirection);
            return;
        }

        switch (direction) {
            case LEFT:
                walkDirection.addLocal(speed, 0, 0);
                previousDirection = LEFT;
//                Logger.getLogger("Fruity").log(Level.WARNING, "Worm walk left");
                break;
            case RIGHT:
                walkDirection.addLocal(-speed, 0, 0);
//                Logger.getLogger("Fruity").log(Level.WARNING, "Worm walk right");
                break;
            case UP:
                walkDirection.addLocal(0, 0, speed);
//                Logger.getLogger("Fruity").log(Level.WARNING, "Worm walk up");
                break;
            case DOWN:
                walkDirection.addLocal(0, 0, -speed);
//                Logger.getLogger("Fruity").log(Level.WARNING, "Worm walk down");
                break;
            case STOP:
                walkDirection.addLocal(0, 0, 0);
                break;
            default:
                Logger.getLogger("Fruity").log(Level.WARNING, "Worm move with NO direction");
                walkDirection = new Vector3f(prevWalkDirection);
        }

        if (!"Walk".equals(animationChannel.getAnimationName())) {
            animationChannel.setAnim("Walk", 0.0f);
        }
        wormChar.setViewDirection(walkDirection);
        wormChar.setWalkDirection(walkDirection);
        for (int i = 0; i < WORM_POS.length; i++) {
            if (WORM_POS[i][1] != null && WORM_POS[i][1].equals(this)) {
                WORM_POS[i][0] = wormChar.getPhysicsLocation();
            }
        }
        prevWalkDirection = new Vector3f(walkDirection);
        walkDirection.set(0, 0, 0);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getPositionOnMap">
    private byte[] getPositionOnMap(Vector3f position) {
        byte x = (byte) Math.round((-position.x / PLACE_HOLDER + MAP_WIDTH - 1) / 2);
        byte y = (byte) Math.round((position.z / PLACE_HOLDER + MAP_HEIGHT - 1) / 2);

        return new byte[]{x, y};
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="checkAround">
    private boolean checkAround(int x, int y, byte direction) {
        switch (direction) {
            case UP:
                y++;
                break;
            case DOWN:
                y--;
                break;
            case LEFT:
                x--;
                break;
            case RIGHT:
                x++;
                break;
        }
        if (x >= POSITIONS.length - 1 || x < 1 || y >= POSITIONS[x].length - 1 || y < 1) {
            return false;
        }

        byte position = POSITIONS[x][y][0];
        if (position != ROCK && position != BUSH) {
            return true;
        } else {
            return false;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="findDirection">
    private byte findDirection(byte x, byte y) {// find from location[x][y] to saLocation
        byte dx = (byte) (x - saLocation[0]);
        byte dy = (byte) (y - saLocation[1]);
//        System.out.println("dx: " + dx + " --- dy: " + dy);

        if (dx < 0) {   //Sa is on the right
            if (checkAround(x, y, RIGHT)) {
                return RIGHT;
            } else if (dy < 0) {
                if (checkAround(x, y, UP)) {
                    return UP;
                }
            } else if (dy > 0) {// Sa is on the lower side
                if (checkAround(x, y, DOWN)) {
                    return DOWN;
                }
            }

        } else if (dx > 0) {        // Sa is on the left
            if (checkAround(x, y, LEFT)) {
                return LEFT;
            } else if (dy < 0) {
                if (checkAround(x, y, UP)) {
                    return UP;
                }
            } else if (dy > 0) {// Sa is on the lower side
                if (checkAround(x, y, DOWN)) {
                    return DOWN;
                }
            }
        } else {// same column
            if (dy < 0) {   //Sa is on the upper side
                if (checkAround(x, y, UP)) {
                    return UP;
                }
            } else {// Sa is on the lower side
                if (checkAround(x, y, DOWN)) {
                    return DOWN;
                }
            }
        }
        return STUCKED;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="findDirectionSmart">
    private byte findDirectionSmart(byte x, byte y, byte preDirection) {
        byte dx = (byte) (x - saLocation[0]);
        byte dy = (byte) (y - saLocation[1]);
//        System.out.println("Smarter find direction: dx: " + dx + " --- dy: " + dy);

        //<editor-fold defaultstate="collapsed" desc="Sa Upper Side (dy < 0)">
        if (dy < 0) { // Sa is in upper side
            if (checkAround(x, y, UP) && preDirection != DOWN_TEMP) {//should not go back last step
                return UP;
            } else {
                if (dx > 0) {
                    if (checkAround(x, y, LEFT) && preDirection != RIGHT_TEMP) {//should not go back last step
                        return LEFT;

                    } else if (checkAround(x, y, DOWN) && preDirection != UP_TEMP) {//should not go back last step
                        return DOWN_TEMP;
                    } else if (checkAround(x, y, RIGHT) && preDirection != LEFT_TEMP) {//should not go back last step
                        return RIGHT_TEMP;
                    } else {
                        return STUCKED;
                    }
                } else if (dx < 0) {// if worm needs move RIGHT (horizontally)
                    if (checkAround(x, y, RIGHT) && preDirection != LEFT_TEMP) {//should not go back last step
                        return RIGHT;

                    } else if (checkAround(x, y, DOWN) && preDirection != UP_TEMP) {//should not go back last step
                        return DOWN_TEMP;
                    } else if (checkAround(x, y, LEFT) && preDirection != RIGHT_TEMP) {//should not go back last step
                        return LEFT_TEMP;
                    } else {
                        return STUCKED;
                    }
                } else { // worm does not need to move on X axis
                    if (checkAround(x, y, LEFT) && preDirection != RIGHT_TEMP) {//should not go back last step
                        return LEFT_TEMP;

                    } else if (checkAround(x, y, DOWN) && preDirection != UP_TEMP) {//should not go back last step
                        return DOWN_TEMP;
                    } else if (checkAround(x, y, RIGHT) && preDirection != LEFT_TEMP) {//should not go back last step
                        return RIGHT_TEMP;
                    } else {
                        return STUCKED;
                    }
                }
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Sa lower Side (dy > 0)">
        } else if (dy > 0) { // Sa is in lower side
            if (checkAround(x, y, DOWN) && preDirection != UP_TEMP) {//should not go back last step
                return DOWN;
            } else {
                if (dx > 0) {
                    if (checkAround(x, y, LEFT) && preDirection != RIGHT_TEMP) {//should not go back last step
                        return LEFT;

                    } else if (checkAround(x, y, UP) && preDirection != DOWN_TEMP) {//should not go back last step
                        return UP_TEMP;
                    } else if (checkAround(x, y, RIGHT) && preDirection != LEFT_TEMP) {//should not go back last step
                        return RIGHT_TEMP;
                    } else {
                        return STUCKED;
                    }
                } else if (dx < 0) {// if worm needs move RIGHT (horizontally)
                    if (checkAround(x, y, RIGHT) && preDirection != LEFT_TEMP) {//should not go back last step
                        return RIGHT;

                    } else if (checkAround(x, y, UP) && preDirection != DOWN_TEMP) {//should not go back last step
                        return UP_TEMP;
                    } else if (checkAround(x, y, LEFT) && preDirection != RIGHT_TEMP) {//should not go back last step
                        return LEFT_TEMP;
                    } else {
                        return STUCKED;
                    }
                } else { // worm does not need to move on X axis
                    if (checkAround(x, y, LEFT) && preDirection != RIGHT_TEMP) {//should not go back last step
                        return LEFT_TEMP;

                    } else if (checkAround(x, y, RIGHT) && preDirection != LEFT_TEMP) {//should not go back last step
                        return RIGHT_TEMP;
                    } else if (checkAround(x, y, UP) && preDirection != DOWN_TEMP) {//should not go back last step
                        return UP_TEMP;
                    } else {
                        return STUCKED;
                    }
                }
            }
            //</editor-fold>  

            //<editor-fold defaultstate="collapsed" desc="Sa is on same row (dy = 0)">
        } else { // Sa is on same row
            if (dx > 0) {
                if (checkAround(x, y, LEFT)) {
                    return LEFT;

                } else if (checkAround(x, y, UP) && preDirection != DOWN_TEMP) {//should not go back last step
                    return UP_TEMP;
                } else if (checkAround(x, y, RIGHT) && preDirection != LEFT_TEMP) {//should not go back last step
                    return RIGHT_TEMP;
                } else if (checkAround(x, y, DOWN) && preDirection != UP_TEMP) {//should not go back last step
                    return DOWN_TEMP;
                } else {
                    return STUCKED;
                }
            } else if (dx < 0) {
                if (checkAround(x, y, RIGHT)) {
                    return RIGHT;

                } else if (checkAround(x, y, UP) && preDirection != DOWN_TEMP) {//should not go back last step
                    return UP_TEMP;
                } else if (checkAround(x, y, LEFT) && preDirection != RIGHT_TEMP) {//should not go back last step
                    return LEFT_TEMP;
                } else if (checkAround(x, y, RIGHT) && preDirection != LEFT_TEMP) {//should not go back last step
                    return RIGHT_TEMP;
                } else {
                    return STUCKED;
                }
            } else { // worm does not need to move on X axis
                if (checkAround(x, y, LEFT) && preDirection != RIGHT_TEMP) {//should not go back last step
                    return LEFT_TEMP;

                } else if (checkAround(x, y, RIGHT) && preDirection != LEFT_TEMP) {//should not go back last step
                    return RIGHT_TEMP;
                } else if (checkAround(x, y, UP) && preDirection != DOWN_TEMP) {//should not go back last step
                    return UP_TEMP;
                } else {
                    return STUCKED;
                }
            }
        }
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="moveTo (called from Scene)">
    public void moveTo(Vector3f saLocation) {
        counter++;
        if (counter == IGNORE_FRAME) {
            counter = 0;
            return;
        }
        try {
            if (wayList == null && kuteFuture == null) {
                this.saLocation = getPositionOnMap(saLocation);
//                System.out.println("Sa location: x = " + this.saLocation[0] + " y = " + this.saLocation[1]);
                findWay = new FindWayCallable(
                        getPositionOnMap(wormChar.getPhysicsLocation()), this.saLocation);
                kuteFuture = executor.submit(findWay);
            } else if (kuteFuture != null) {
                if (kuteFuture.isDone()) {
                    wayList = (ArrayDeque<Byte>) kuteFuture.get();
//                    System.out.println(wayList.toString() + "  WAYLIST\n\n\n");
                    kuteFuture = null;
                } else if (kuteFuture.isCancelled()) {
                    kuteFuture = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (wayList != null) {
            if (wayList.isEmpty()) {
                wayList = null;
                kuteFuture = null;
            } else {
                byte nextDirection = wayList.poll();
                if (previousDirection != nextDirection) {
                    move(STOP);
                } else {
                    move(nextDirection);
                }
                previousDirection = nextDirection;
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="die">
    public void die() {
        isDead = true;
        animationChannel.setAnim("Die");
    }

    public boolean isDead() {
        return isDead;
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if (animName.equals("Die")) {
            scene.wormDie(this);
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="FindWayCallable">
    private class FindWayCallable implements Callable {

        private byte[] wormLocation;
        private byte[] saLocation;
        private boolean needBecomeSmarter = false;

        public FindWayCallable(byte[] wormLocation, byte[] saLocation) {
            this.wormLocation = wormLocation;
            this.saLocation = saLocation;
        }

        @Override
        public ArrayDeque call() throws Exception {
            ArrayDeque wayList = new ArrayDeque();

            byte[] saLocation = this.saLocation;
            byte currentX = wormLocation[0];
            byte currentY = wormLocation[1];
//            System.out.println("Callable: " + this.toString() + " in while loop: ");

            // worm thinks for 10 steps ahead only. It re-thinks if it thinks more than 10 steps
            byte count = 0;
            byte prevDirection = STUCKED;
            try {
                loop:
                while (count <= smartLv
                        && (currentX != saLocation[0] || currentY != saLocation[1])) {
                    byte direction = STUCKED;
                    if (needBecomeSmarter && isSmart) {
                        direction = findDirectionSmart(currentX, currentY, prevDirection);
                    } else {
                        direction = findDirection(currentX, currentY);
                    }
                    switch (direction) {
                        case STUCKED:
                            if (!needBecomeSmarter) {
                                needBecomeSmarter = true;
//                                System.out.println("Worm become MORE MORE MORE MORE MORE MORE SMARTER");
                                count = 0;
                                continue loop;
                            } else {// if becomming Smarter still gets stuck then dont think
                                break loop;
                            }

                        case UP_TEMP:
                            prevDirection = UP_TEMP;
                            direction = UP;
                            currentY++;
                            break;
                        case UP:
                            prevDirection = UP;
                            currentY++;
                            break;

                        case DOWN_TEMP:
                            prevDirection = DOWN_TEMP;
                            direction = DOWN;
                            currentY--;
                            break;
                        case DOWN:
                            prevDirection = DOWN;
                            currentY--;
                            break;

                        case LEFT_TEMP:
                            prevDirection = LEFT_TEMP;
                            direction = LEFT;
                            currentX--;
                            break;
                        case LEFT:
                            prevDirection = LEFT;
                            currentX--;
                            break;

                        case RIGHT_TEMP:
                            prevDirection = RIGHT_TEMP;
                            direction = RIGHT;
                            currentX++;
                            break;
                        case RIGHT:
                            prevDirection = RIGHT;
                            currentX++;
                            break;
                    }
                    for (int i = 0; i < TIMES_PER_CELL; i++) {
                        wayList.add(direction);
                    }
                    count++;
//                    System.out.println("WORM : currentX: " + currentX + " currentY: " + currentY);
                }
            } catch (Exception e) {
                System.out.println("Catched Exception ");
                e.printStackTrace();
            }
            return wayList;
        }
    }
    //</editor-fold>
}
