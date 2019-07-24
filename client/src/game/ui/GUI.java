package game.ui;

import java.time.LocalDateTime;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import game.AOGame;
import game.managers.AOInputProcessor;
import game.screens.GameScreen;
import game.ui.user.UserInformation;
import game.utils.Skins;


public class GUI implements Disposable {

    //public static final int CONSOLE_TOP_BORDER = 16;
    //public static final int CONSOLE_LEFT_BORDER = 5;
    private Stage stage;
    private ActionBar actionBar;
    private UserInformation userTable;
    private DialogText dialog;
    private AOConsole console;
    private OrthographicCamera camera;

    public GUI() {
        this.stage = new AOInputProcessor(this);
        this.actionBar = new ActionBar();
        this.userTable = new UserInformation();
        this.dialog = new DialogText();
        this.console = new AOConsole();
    }

    public ActionBar getActionBar() {
        return actionBar;
    }

    public Inventory getInventory() {
        return actionBar.getInventory();
    }

    public DialogText getDialog() {
        return dialog;
    }

    public AOConsole getConsole() {
        return console;
    }

    public UserInformation getUserTable() {
        return userTable;
    }

    public Stage getStage() {
        return stage;
    }

    public SpellView getSpellView() {
        return getActionBar().getSpellView();
    }

    public void takeScreenshot() {
        String screenshotPath = "Screenshots/Screenshot-" + LocalDateTime.now() + ".png";

        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);

        // this loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
        for (int i = 4; i < pixels.length; i += 4) {
            pixels[i - 1] = (byte) 255;
        }

        Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        PixmapIO.writePNG(Gdx.files.local(screenshotPath), pixmap);
        getConsole().addInfo("3...2...1...Say Cheese!. Screenshot saved in " + screenshotPath);
        pixmap.dispose();
    }

    public void initialize() {
        Skins.COMODORE_SKIN.getFont("flipped").setUseIntegerPositions(false);
        Skins.COMODORE_SKIN.getFont("flipped-shadow").setUseIntegerPositions(false);

        Table table = new Table();
        table.setFillParent(true);
        createConsole(table);
        createUserStatus(table);
        createActionBar(table);
        createDialogContainer(table);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);

    }

    public OrthographicCamera getCamera() {
        if (camera == null) {
            if (Gdx.app.getApplicationListener() instanceof AOGame) {
                AOGame game = (AOGame) Gdx.app.getApplicationListener();
                final Screen screen = game.getScreen();
                if (screen instanceof GameScreen) {
                    camera = ((GameScreen) screen).getGUICamera();
                }
            }
        }
        return camera;
    }

    private Actor createConsole(Table table) {
        console = new AOConsole();
        table.add(console).left().top();
        return console;
    }

    private Table createUserStatus(Table table) {
        userTable = new UserInformation();
        table.add(userTable).prefWidth(400).left().bottom().expandX();
        return userTable;
    }

    private Table createActionBar(Table table) {
        actionBar = new ActionBar();
        table.add(actionBar).right().expandY();
        return actionBar;
    }

    private Table createDialogContainer(Table table) {
        dialog = new DialogText();
        float width = getWidth() * 0.8f;
        dialog.setSize(width, dialog.getHeight());
        dialog.setPosition((getWidth() - width) / 2, getHeight() / 2);
        stage.addActor(dialog);
        return dialog;
    }

    private float getHeight() {
        return Gdx.graphics.getHeight();
    }

    private float getWidth() {
        return Gdx.graphics.getWidth();
    }

    public void draw() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }

}
