package Game;

import Model.Account;
import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thong
 */
public class Game extends SimpleApplication implements ScreenController, GameConstant {

    private Account account;
    private Home home;
    private Scene scene;
//    private boolean isMute;
    private Nifty nifty;
    private Store store;
    private NiftyImage mute;
    private NiftyImage on;
    private String font = "Interface/font.fnt";
    private boolean finished;

    public static void main(String[] args) {
        Logger.getLogger("").setLevel(Level.WARNING);

        Game game = new Game();
        game.showSettings = false;
        AppSettings settings = new AppSettings(true);
        settings.setFrameRate(60);
        game.setSettings(settings);
        game.start();
    }

    public Game() {
        this.account = new Account();
        this.store = new Store(account);
    }

    public Account getAccount() {
        return account;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

//  
    public void setMute() {
        Screen screen = nifty.getCurrentScreen();
        Element element = screen.findElementByName("mute");

        if (!isMute[0]) {
            element.getRenderer(ImageRenderer.class).setImage(on);
        } else {
            element.getRenderer(ImageRenderer.class).setImage(mute);
        }
    }

    public Store getStore() {
        return store;
    }

    @Override
    public void simpleInitApp() {
        showSettings = false;
        setDisplayStatView(false);
        isMute[0] = false;
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setEnabled(false);

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        mute = nifty.getRenderEngine().createImage("Textures/2D Images/mute.png", false);
        on = nifty.getRenderEngine().createImage("Textures/2D Images/on.png", false);

//        nifty.setDebugOptionPanelColors(true);

        home = new Home(stateManager, this);
        scene = new Scene(stateManager, this);

        setUpScreen(nifty);
        rootNode.attachChild(home.getRootNode());
        home.setUpCammera();
        stateManager.attach(home);
        home.bind(nifty, nifty.getScreen("start"));

        nifty.gotoScreen("start");
        finished = true;
    }

    private void setUpScreen(Nifty nifty) {

        //<editor-fold defaultstate="collapsed" desc="start screen">
        nifty.addScreen("start", new ScreenBuilder("start") {
            {
                controller(home);

                layer(new LayerBuilder("foreground") {
                    {
                        childLayoutVertical();
                        panel(new PanelBuilder("top_panel") {
                            {
                                valignTop();
                                childLayout(ChildLayoutType.Center);
                                height("35%");
                                width("100%");

                                image(new ImageBuilder() {
                                    {
                                        filename("Textures/2D Images/fruitybang.png");
                                        valignBottom();
                                        alignCenter();
                                        height("100%");
                                        width("95%");
                                    }
                                });
                            }
                        });
                        ;

                        panel(new PanelBuilder("bottom_panel") {
                            {
                                alignLeft();
                                childLayoutHorizontal();
                                height("65%");
                                width("100%");

                                panel(new PanelBuilder("panel_play") {
                                    {
                                        alignRight();
                                        childLayoutCenter();
                                        height("100%");
                                        width("85%");

                                        image(new ImageBuilder("play") {
                                            {
                                                filename("Textures/2D Images/play.png");
                                                valign(VAlign.Center);
                                                alignRight();
                                                height("40%");
                                                width("40%");
                                                interactOnClick("chooseLevel()");
                                            }
                                        });
                                    }
                                });

                                panel(new PanelBuilder("panel_right") {
                                    {
                                        alignRight();
                                        childLayoutVertical();
                                        height("95%");
                                        width("12%");


                                        panel(new PanelBuilder("panel_right") {
                                            {
                                                height("10%");
                                                width("100%");
                                                interactOnClick("cheat()");
                                            }
                                        });

                                        panel(new PanelBuilder("panel_right") {
                                            {
                                                height("70%");
                                                width("100%");
                                            }
                                        });

                                        image(new ImageBuilder("mute") {
                                            {
                                                if (isMute[0]) {
                                                    filename("Textures/2D Images/mute.png");
                                                } else {
                                                    filename("Textures/2D Images/on.png");
                                                }
                                                alignRight();
                                                valignBottom();

                                                height("20%");
                                                width("100%");
                                                interactOnClick("mute()");
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }.build(nifty));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="level screen">
        nifty.addScreen("lv", new ScreenBuilder("lv") {
            {
                controller(home);
                layer(new LayerBuilder("foreground") {
                    {
                        childLayoutVertical();
                        panel(new PanelBuilder("top_panel") {
                            {
                                childLayoutVertical();
                                height("80%");
                                width("100%");

                                panel(new PanelBuilder("lv_1_5") {
                                    {
                                        alignRight();
                                        childLayoutHorizontal();
                                        height("50%");
                                        width("100%");
                                        for (int i = 0; i < 5; i++) {
                                            final int lv = i + 1;
                                            panel(new PanelBuilder("lv_" + lv) {
                                                {
                                                    alignCenter();
                                                    childLayoutCenter();
                                                    height("100%");
                                                    width("20%");

                                                    image(new ImageBuilder("Level " + lv) {
                                                        {
                                                            alignCenter();
                                                            valignCenter();
                                                            filename("Textures/2D Images/Level/" + lv + ".png");

                                                            height("60%");
                                                            width("60%");
                                                            interactOnClick("startLevel(" + lv + ")");
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });

                                panel(new PanelBuilder("lv_5_10") {
                                    {
                                        alignRight();
                                        childLayoutHorizontal();
                                        height("50%");
                                        width("100%");
                                        for (int i = 5; i < 9; i++) {
                                            final int lv = i + 1;
                                            panel(new PanelBuilder("lv_" + lv) {
                                                {
                                                    alignCenter();
                                                    childLayoutCenter();
                                                    height("100%");
                                                    width("20%");

                                                    image(new ImageBuilder("Level " + lv) {
                                                        {
                                                            alignCenter();
                                                            valignCenter();
                                                            filename("Textures/2D Images/Level/" + lv + ".png");

                                                            height("60%");
                                                            width("60%");
                                                            interactOnClick("startLevel(" + lv + ")");
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                        panel(new PanelBuilder("lv_" + 10) {
                                            {
                                                alignCenter();
                                                childLayoutCenter();
                                                height("100%");
                                                width("20%");

                                                image(new ImageBuilder("Level " + 10) {
                                                    {
                                                        alignCenter();
                                                        valignCenter();
                                                        filename("Textures/2D Images/Level/10.png");

                                                        height("43%");
                                                        width("70%");
                                                        interactOnClick("startLevel(10)");
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });

                        //<editor-fold defaultstate="collapsed" desc="bottom_panel">
                        panel(new PanelBuilder("bottom_panel") {
                            {
                                alignLeft();
                                childLayoutHorizontal();
                                height("20%");
                                width("100%");

                                panel(new PanelBuilder("back_panel") {
                                    {
                                        alignRight();
                                        childLayoutVertical();
                                        height("100%");
                                        width("20%");

                                        image(new ImageBuilder("Back") {
                                            {
                                                filename("Textures/2D Images/back.png");
                                                alignRight();

                                                height("70%");
                                                width("50%");
                                                interactOnClick("back()");
                                            }
                                        });
                                    }
                                });

                                panel(new PanelBuilder() {
                                    {
                                        height("100%");
                                        width("60%");
                                    }
                                });

                                panel(new PanelBuilder("store_panel") {
                                    {
                                        childLayoutHorizontal();
                                        image(new ImageBuilder("Store") {
                                            {
                                                alignCenter();
                                                valignTop();
                                                filename("Textures/2D Images/store.png");

                                                height("70%");
                                                width("50%");
                                                interactOnClick("store()");
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        //</editor-fold>
                    }
                });
            }
        }.build(nifty));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="store screen">
        nifty.addScreen("store", new ScreenBuilder("store") {
            {
                controller(home);
                layer(new LayerBuilder("foreground") {
                    {
                        //<editor-fold defaultstate="collased" desc="title_panel">
                        childLayoutVertical();
                        panel(new PanelBuilder("title_panel") {
                            {
                                childLayoutHorizontal();
                                height("5%");
                                width("100%");

                                panel(new PanelBuilder("money_panel") {
                                    {
                                        childLayoutVertical();
                                        height("100%");
                                        width("25%");

                                        text(new TextBuilder("money_title") {
                                            {
                                                text("Ruby");
                                                font("Interface/Fonts/Default.fnt");
                                                wrap(true);
                                                height("100%");
                                                width("100%");
                                            }
                                        });
                                    }
                                });

                                panel(new PanelBuilder("shield_title_panel") {
                                    {
                                        childLayoutVertical();
                                        height("100%");
                                        width("25%");

                                        text(new TextBuilder("shield_title") {
                                            {
                                                text("Shield");
                                                font("Interface/Fonts/Default.fnt");
                                                wrap(true);
                                                height("100%");
                                                width("100%");
                                            }
                                        });

                                    }
                                });

                                panel(new PanelBuilder("power_title_panel") {
                                    {
                                        childLayoutVertical();
                                        height("100%");
                                        width("25%");

                                        text(new TextBuilder("power_title") {
                                            {
                                                text("Power");
                                                font("Interface/Fonts/Default.fnt");
                                                wrap(true);
                                                height("100%");
                                                width("100%");
                                            }
                                        });
                                    }
                                });

                                panel(new PanelBuilder("multi_title_panel") {
                                    {
                                        childLayoutVertical();
                                        height("100%");
                                        width("25%");

                                        text(new TextBuilder("capacity_title") {
                                            {
                                                text("Bomb");
                                                font("Interface/Fonts/Default.fnt");
                                                wrap(true);
                                                height("100%");
                                                width("100%");
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        //</editor-fold>

                        //<editor-fold defaultstate="collapsed" desc="stat_panel">
                        panel(new PanelBuilder("stat_panel") {
                            {
                                childLayoutHorizontal();
                                height("5%");
                                width("100%");

                                panel(new PanelBuilder("money_stat_panel") {
                                    {
                                        childLayoutVertical();
                                        height("100%");
                                        width("25%");

                                        text(new TextBuilder("money") {
                                            {
                                                text(account.getMoney() + "");
                                                font("Interface/Fonts/Default.fnt");
                                                wrap(true);
                                                height("100%");
                                                width("100%");
                                            }
                                        });
                                    }
                                });

                                panel(new PanelBuilder("shield_item_panel") {
                                    {
                                        childLayoutVertical();
                                        height("100%");
                                        width("25%");

                                        text(new TextBuilder("shield") {
                                            {
                                                if (account.isShield()) {
                                                    text("Secured");
                                                } else {
                                                    text("Dangered");
                                                }
                                                font("Interface/Fonts/Default.fnt");
                                                wrap(true);
                                                height("100%");
                                                width("100%");
                                            }
                                        });
                                    }
                                });

                                panel(new PanelBuilder("power_item_panel") {
                                    {
                                        childLayoutVertical();
                                        height("100%");
                                        width("25%");

                                        text(new TextBuilder("power") {
                                            {
                                                text(account.getPower() + "/4 - " + account.getFruitString());
                                                font("Interface/Fonts/Default.fnt");
                                                wrap(true);
                                                height("100%");
                                                width("100%");
                                            }
                                        });
                                    }
                                });

                                panel(new PanelBuilder("multi_item_panel") {
                                    {
                                        childLayoutVertical();
                                        height("100%");
                                        width("25%");

                                        text(new TextBuilder("multi") {
                                            {
                                                text(account.getCapacity() + "/5");
                                                font("Interface/Fonts/Default.fnt");
                                                wrap(true);
                                                height("100%");
                                                width("100%");
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        //</editor-fold>

                        //<editor-fold defaultstate="collapsed" desc="midPanel">
                        panel(new PanelBuilder("mid_panel") {
                            {
                                childLayoutVertical();
                                height("70%");
                                width("100%");

                                panel(new PanelBuilder("space_panel") {
                                    {
                                        childLayoutVertical();
                                        height("70%");
                                        width("100%");
                                    }
                                });

                                // <editor-fold defaultstate="collapsed" desc="price panel">
                                panel(new PanelBuilder("price_panel") {
                                    {
                                        childLayoutHorizontal();
                                        panel(new PanelBuilder("power_price_panel") {
                                            {
                                                alignRight();
                                                childLayoutCenter();
                                                height("100%");
                                                width("33%");
                                                text(new TextBuilder("power_price") {
                                                    {
                                                        text(store.getPowerPrice());
                                                        font(font);
                                                        wrap(true);
                                                        height("100%");
                                                        width("100%");
                                                    }
                                                });
                                            }
                                        });

                                        panel(new PanelBuilder("shield_price_panel") {
                                            {
                                                alignRight();
                                                childLayoutCenter();
                                                height("100%");
                                                width("33%");
                                                text(new TextBuilder("shield_price") {
                                                    {
                                                        text(store.getShieldPrice());
                                                        font(font);
                                                        wrap(true);
                                                        height("100%");
                                                        width("100%");
                                                    }
                                                });
                                            }
                                        });

                                        panel(new PanelBuilder("multi_price_panel") {
                                            {
                                                alignRight();
                                                childLayoutCenter();
                                                height("100%");
                                                width("34%");
                                                text(new TextBuilder("multi_price") {
                                                    {
                                                        text(store.getBombPrice());
                                                        font(font);
                                                        wrap(true);
                                                        height("100%");
                                                        width("100%");
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                                //</editor-fold>

                                panel(new PanelBuilder("buy_button_panel") {
                                    {
                                        childLayoutHorizontal();
                                        height("20%");
                                        width("100%");

                                        panel(new PanelBuilder("power_panel") {
                                            {
                                                alignRight();
                                                childLayoutCenter();
                                                height("100%");
                                                width("33%");
                                                image(new ImageBuilder("Buy Power") {
                                                    {
                                                        filename("Textures/2D Images/buy.png");
                                                        height("70%");
                                                        width("70%");
                                                        visibleToMouse(true);
                                                        interactOnClick("buyPower()");
                                                    }
                                                });
                                            }
                                        });

                                        panel(new PanelBuilder("shield_panel") {
                                            {
                                                alignRight();
                                                childLayoutHorizontal();
                                                childLayoutCenter();
                                                height("100%");
                                                width("33%");
                                                image(new ImageBuilder("Buy Shield") {
                                                    {
                                                        filename("Textures/2D Images/buy.png");
                                                        height("70%");
                                                        width("70%");
                                                        visibleToMouse(true);
                                                        interactOnClick("buyShield()");
                                                    }
                                                });
                                            }
                                        });


                                        panel(new PanelBuilder("multi_panel") {
                                            {
                                                alignRight();
                                                childLayoutHorizontal();
                                                childLayoutCenter();
                                                height("100%");
                                                width("34%");
                                                image(new ImageBuilder("Buy Multi") {
                                                    {
                                                        filename("Textures/2D Images/buy.png");
                                                        height("70%");
                                                        width("70%");
                                                        visibleToMouse(true);
                                                        interactOnClick("buyMulti()");
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        //</editor-fold>

                        //<editor-fold defaultstate="collapsed" desc="bottom_panel">
                        panel(new PanelBuilder("bottom_panel") {
                            {
                                alignLeft();
                                childLayoutHorizontal();
                                height("20%");
                                width("100%");

                                panel(new PanelBuilder("back_panel") {
                                    {
                                        alignRight();
                                        childLayoutVertical();
                                        height("100%");
                                        width("20%");

                                        image(new ImageBuilder("Back") {
                                            {
                                                filename("Textures/2D Images/back.png");
                                                alignRight();

                                                height("70%");
                                                width("50%");
                                                interactOnClick("back()");
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        //</editor-fold>
                    }
                });
            }
        }.build(nifty));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="game screen">
        nifty.addScreen("game", new ScreenBuilder("game") {
            {
                controller(Game.this);
                layer(new LayerBuilder("foreground") {
                    {
                        childLayoutHorizontal();

                        //<editor-fold defaultstate="collapsed" desc="empty space">
                        panel(new PanelBuilder("space_panel") {
                            {
                                height("100%");
                                width("33%");
                            }
                        });
                        //</editor-fold>

                        panel(new PanelBuilder("panel_right") {
                            {
                                childLayoutVertical();
                                height("100%");
                                width("34%");

                                panel(new PanelBuilder("score_panel_ingame") {
                                    {
                                        childLayoutCenter();
                                        height("4%");
                                        width("100%");

                                        text(new TextBuilder("money_ingame") {
                                            {
                                                text(account.getMoney() + " Ruby");
                                                font("Interface/Fonts/Default.fnt");
                                                wrap(true);
                                                height("100%");
                                                width("100%");
                                            }
                                        });
                                    }
                                });

                                //<editor-fold defaultstate="collapsed" desc="empty space">
                                panel(new PanelBuilder("space_panel") {
                                    {
                                        childLayoutCenter();
                                        height("86%");
                                        width("100%");
                                    }
                                });//</editor-fold>

                                //<editor-fold defaultstate="collapsed" desc="bottom_panel">
                                panel(new PanelBuilder("pause_panel_ingame") {
                                    {
                                        childLayoutVertical();
                                        height("10%");
                                        width("100%");

                                        image(new ImageBuilder("pause_ingame") {
                                            {
                                                filename("Textures/2D Images/pause.png");
                                                alignCenter();

                                                height("100%");
                                                width("25%");
                                                interactOnClick("pause()");
                                            }
                                        });
                                    }
                                });
                                //</editor-fold>
                            }
                        });

                        //<editor-fold defaultstate="collapsed" desc="empty space">
                        panel(new PanelBuilder("space_panel2") {
                            {
                                height("100%");
                                width("33%");
                            }
                        });
                        //</editor-fold>
                    }
                });
            }
        }.build(nifty));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="endScreen">
        nifty.addScreen("end", new ScreenBuilder("end") {
            {
                controller(Game.this);
                layer(new LayerBuilder("foreground") {
                    {
                        childLayoutHorizontal();

                        //<editor-fold defaultstate="collapsed" desc="empty space">
                        panel(new PanelBuilder("space_panel21") {
                            {
                                height("100%");
                                width("25%");
                            }
                        });
                        //</editor-fold>

                        panel(new PanelBuilder("panel_right2") {
                            {
                                childLayoutVertical();
                                height("100%");
                                width("50%");

                                //<editor-fold defaultstate="collapsed" desc="empty space">
                                panel(new PanelBuilder("space_panel22") {
                                    {
                                        childLayoutCenter();
                                        height("20%");
                                        width("100%");
                                    }
                                });//</editor-fold>

                                panel(new PanelBuilder("score_panel_end") {
                                    {
                                        childLayoutVertical();
                                        height("50%");
                                        width("100%");

                                        text(new TextBuilder("money_end") {
                                            {
                                                text("Ruby: " + account.getMoney());
                                                font(font);
                                                height("20%");
                                                width("100%");
                                            }
                                        });

                                        text(new TextBuilder("score_end") {
                                            {
                                                text("Score " + account.getScore());
                                                font(font);
                                                height("20%");
                                                width("100%");
                                            }
                                        });

                                        //<editor-fold defaultstate="collapsed" desc="empty space">
                                        panel(new PanelBuilder("space_panel22") {
                                            {
                                                childLayoutCenter();
                                                height("40%");
                                                width("100%");
                                            }
                                        });//</editor-fold>
                                    }
                                });

                                panel(new PanelBuilder("pause_panel_end") {
                                    {
                                        childLayoutVertical();
                                        height("30%");
                                        width("100%");

                                        image(new ImageBuilder("menu") {
                                            {
                                                filename("Textures/2D Images/menu.png");
                                                alignCenter();

                                                height("50%");
                                                width("30%");
                                                interactOnClick("menu()");
                                            }
                                        });
                                    }
                                });
                            }
                        });

                        //<editor-fold defaultstate="collapsed" desc="empty space">
                        panel(new PanelBuilder("space_panel2") {
                            {
                                height("100%");
                                width("25%");
                            }
                        });
                        //</editor-fold>
                    }
                });
            }
        }.build(nifty));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="pauseScreen">
        nifty.addScreen("pause", new ScreenBuilder("pause") {
            {
                controller(Game.this);
                layer(new LayerBuilder("foreground") {
                    {
                        childLayoutHorizontal();

                        //<editor-fold defaultstate="collapsed" desc="empty space">
                        panel(new PanelBuilder("space_panel31") {
                            {
                                height("100%");
                                width("30%");
                            }
                        });
                        //</editor-fold>

                        panel(new PanelBuilder("panel_right3") {
                            {
                                childLayoutVertical();
                                height("100%");
                                width("50%");

                                //<editor-fold defaultstate="collapsed" desc="empty space">
                                panel(new PanelBuilder("space_panel22") {
                                    {
                                        childLayoutCenter();
                                        height("50%");
                                        width("100%");

                                        image(new ImageBuilder("back_pause") {
                                            {
                                                filename("Textures/2D Images/pausetext.png");
                                                alignCenter();

                                                height("60%");
                                                width("90%");
                                                interactOnClick("back()");
                                            }
                                        });
                                    }
                                });//</editor-fold>

                                panel(new PanelBuilder("score_panel_pause") {
                                    {
                                        childLayoutHorizontal();
                                        height("40%");
                                        width("100%");

                                        image(new ImageBuilder("back_pause") {
                                            {
                                                filename("Textures/2D Images/menu.png");
                                                alignCenter();

                                                height("40%");
                                                width("28%");
                                                interactOnClick("menu()");
                                            }
                                        });

                                        //<editor-fold defaultstate="collapsed" desc="empty space">
                                        panel(new PanelBuilder("space_panel22") {
                                            {
                                                childLayoutCenter();
                                                height("100%");
                                                width("34%");
                                            }
                                        });//</editor-fold>

                                        image(new ImageBuilder("menu_pause") {
                                            {
                                                filename("Textures/2D Images/resume.png");
                                                alignCenter();

                                                height("37%");
                                                width("25%");
                                                interactOnClick("resume()");
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        //<editor-fold defaultstate="collapsed" desc="empty space">
                        panel(new PanelBuilder("space_panel2") {
                            {
                                height("100%");
                                width("30%");
                            }
                        });
                        //</editor-fold>
                    }
                });
            }
        }.build(nifty));
        //</editor-fold>
    }

    //<editor-fold defaultstate="collapsed" desc="startPlaying()">
    protected void startPlaying(byte lvl) {
        rootNode.detachAllChildren();
        stateManager.detach(home);

        rootNode.attachChild(scene.getRootNode());
        stateManager.attach(scene);

        switch (lvl) {
            case 1:
                scene.init(lvl, account, (byte) (lvl + 1), (byte) 100);
                break;
            case 2:
                scene.init(lvl, account, (byte) (lvl + 1), (byte) 100);
                break;
            case 3:
                scene.init(lvl, account, (byte) (lvl + 1), (byte) 100);
                break;
            case 4:
                scene.init(lvl, account, (byte) (lvl + 1), (byte) 100);
                break;
            case 5:
                scene.init(lvl, account, (byte) (lvl + 1), (byte) 100);
                break;
            case 6:
                scene.init(lvl, account, (byte) (lvl + 1), (byte) 100);
                break;
            case 7:
                scene.init(lvl, account, (byte) (lvl + 1), (byte) 80);
                break;
            case 8:
                scene.init(lvl, account, (byte) (lvl + 1), (byte) 80);
                break;
            case 9:
                scene.init(lvl, account, (byte) (lvl + 1), (byte) 80);
                break;
            case 10:
                scene.init(lvl, account, (byte) 5, (byte) 100);
                break;
        }


        nifty.gotoScreen("game");
        updateMoneyIngame();
    }
    //</editor-fold>

    public void updateMoneyIngame() {
        Screen screen = nifty.getCurrentScreen();

        try {
            Element element = screen.findElementByName("money_ingame");
            element.getRenderer(TextRenderer.class).setText(account.getMoney() + " Ruby");
        } catch (Exception e) {
            System.out.println("Catched Exception: " + e.toString());
        }
    }

    public void updateMoney() {
        Screen screen = nifty.getCurrentScreen();
        try {
            Element element = screen.findElementByName("money");
            element.getRenderer(TextRenderer.class).setText(account.getMoney() + " Ruby");
        } catch (Exception e) {
            System.out.println("Catched Exception: " + e.toString());
        }
    }

    public void pause() {
        scene.pause(true);
        nifty.gotoScreen("pause");
    }

    public void end() {
        nifty.gotoScreen("end");

        try {
            Screen screen = nifty.getCurrentScreen();
            Element element = screen.findElementByName("money_end");
            element.getRenderer(TextRenderer.class).setText("Ruby " + account.getMoney());

            System.out.println("adasdasdassadsaadadasdasdd " + account.getMoney());
            System.out.println("adasdasdassadsaadadasdasdd " + account.getScore());


            element = screen.findElementByName("score_end");
            element.getRenderer(TextRenderer.class).setText("Score " + account.getScore());
        } catch (Exception e) {
            System.out.println("Catched Exception: " + e.toString());
        }
    }

    public void resume() {
        scene.pause(false);
        nifty.gotoScreen("game");
    }

    public void menu() {
        inputManager.clearMappings();
        rootNode.detachAllChildren();
        stateManager.detach(scene);
        rootNode.attachChild(home.getRootNode());
        home.setUpCammera();
        stateManager.attach(home);
        nifty.gotoScreen("start");
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
    }

    @Override
    public void onStartScreen() {
//        if(nifty.getCurrentScreen().getScreenId().equals("game")){
//            home.getWelcomeSound().stop();
//        }
//        
    }

    @Override
    public void onEndScreen() {
    }
}
