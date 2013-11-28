package Model;

/**
 * @author Thong
 */
public class Account {

    public static final int STRAWBERRY = 1;
    public static final int APPLE = 2;
    private int capacity = 1;
    private int power = 1;
    private int money = 0;
    private int fruit = APPLE;
    private long score = 0;
    private boolean shield;

    public Account() {
        reset();
    }

    public final void reset() {
        capacity = 1;
        power = 1;
        shield = false;
        fruit = APPLE;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public int getFruit() {
        return fruit;
    }

    public String getFruitString() {
        if (fruit == APPLE) {
            return "Apple";
        } else {
            return "Strawberry";
        }
    }

    public void setFruit(int fruit) {
        this.fruit = fruit;
    }

    public boolean isShield() {
        return shield;
    }

    public void setShield(boolean shield) {
        this.shield = shield;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
