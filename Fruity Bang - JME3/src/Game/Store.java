package Game;

import Model.Account;

/**
 * @author Cole
 */
public class Store {

    private int bombPrice = 100;
    private int powerPrice = 100;
    private int shieldPrice = 300;
    private Account account;

    public Store(Account account) {
        this.account = account;
        init();
    }

    public void init() {
        switch (account.getCapacity()) {
            case 1:
                bombPrice = 150;
                break;
            case 2:
                bombPrice = 250;
                break;
            case 3:
                bombPrice = 420;
                break;
            case 4:
                bombPrice = 850;
                break;
            default:
                bombPrice = -1;
        }

        //shield
        if (account.isShield()) {
            shieldPrice = -1;
        } else {
            shieldPrice = 300;
        }

        //power
        if (account.getFruit() == Account.APPLE) {
            switch (account.getPower()) {
                case 1:
                    powerPrice = 200;
                    break;
                case 2:
                    powerPrice = 450;
                    break;
                case 3:
                    powerPrice = 700;
                    break;
                case 4:
                    powerPrice = 1200;
                    break;
                default:
                    powerPrice = -1;
            }
        } else if (account.getFruit() == Account.STRAWBERRY) {
            switch (account.getPower()) {
                case 1:
                    powerPrice = 300;
                    break;
                case 2:
                    powerPrice = 550;
                    break;
                case 3:
                    powerPrice = 800;
                    break;
//                case 4:
//                    powerPrice = 1300;
//                    break;
                default:
                    powerPrice = -1;
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="get String()">
    public String getBombPrice() {
        if (bombPrice == -1) {
            return "Max";
        } else {
            return bombPrice + " Ruby";
        }
    }

    public String getShieldPrice() {
        if (shieldPrice == -1) {
            return "Max";
        } else {
            return shieldPrice + " Ruby";
        }
    }

    public String getPowerPrice() {
        if (powerPrice == -1) {
            return "Max";
        } else {
            return powerPrice + " Ruby";
        }
    }
    //</editor-fold>

    public boolean buyBomb() {
        int money = account.getMoney();

        if (bombPrice != -1 && money >= bombPrice) {
            account.setCapacity(account.getCapacity() + 1);
            account.setMoney(money - bombPrice);
            init();
            return true;
        }
        return false;
    }

    public boolean buyPower() {
        int money = account.getMoney();

        if (account.getPower() < 4 && money >= powerPrice) {
            account.setPower(account.getPower() + 1);
            account.setMoney(money - powerPrice);
            init();
            return true;
        } else {
            if (account.getPower() == 4 && account.getFruit() == Account.APPLE && money >= powerPrice) {
                account.setPower(1);
                account.setMoney(money - powerPrice);
                account.setFruit(Account.STRAWBERRY);
                init();
                return true;
            }
        }
        return false;
    }

    public boolean buyShield() {
        int money = account.getMoney();

        if (shieldPrice != -1 && money >= shieldPrice) {
            account.setShield(true);
            account.setMoney(money - shieldPrice);
            init();
            return true;
        }
        return false;
    }
}
