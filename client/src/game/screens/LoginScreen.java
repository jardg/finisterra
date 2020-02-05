package game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import game.AOGame;
import game.ClientConfiguration;
import game.handlers.AOAssetManager;
import game.handlers.MusicHandler;
import game.network.ClientResponseProcessor;
import game.network.GameNotificationProcessor;
import game.systems.network.ClientSystem;
import game.utils.Skins;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import shared.network.lobby.JoinLobbyRequest;
import shared.util.Messages;

import static game.utils.Resources.CLIENT_CONFIG;

public class LoginScreen extends AbstractScreen {

    private ClientSystem clientSystem;
    private ClientConfiguration config;

    private TextField username;
    private List<ClientConfiguration.Network.Server> serverList;

    private boolean canConnect = true;

    public LoginScreen() {
        super();
        init();
        // utilice bgmusic  para subir gradualmente el sonido.
        bGMusic ();
    }

    void bGMusic() {
        Music firstBGMusic = MusicHandler.FIRSTBGM;
        firstBGMusic.setVolume ( 0 );
        firstBGMusic.play ( );
        firstBGMusic.setLooping ( true );
        // incrementa el sonido gradualmente hasta llegar al 34%
        float MUSIC_FADE_STEP = 0.01f;
        Timer.schedule ( new Timer.Task ( ) {
            @Override
            public void run() {
                if (firstBGMusic.getVolume ( ) < 0.34f)
                    firstBGMusic.setVolume ( firstBGMusic.getVolume ( ) + MUSIC_FADE_STEP );
                else {
                    this.cancel ( );
                }
            }
        }, 0, 0.6f );
    }

    @Override
    protected void keyPressed(int keyCode) {
        if (keyCode == Input.Keys.ENTER && this.canConnect) {
            //Connect
            connectThenLogin();

            //Prevent multiple simultaneous connections.
            this.canConnect = false;
        }
    }

    private void init() {
        clientSystem = new ClientSystem("127.0.0.1", 7666); // @todo implement empty constructor
        clientSystem.setNotificationProcessor(new GameNotificationProcessor());
        clientSystem.setResponseProcessor(new ClientResponseProcessor());

        // TODO MusicHandler.playMusic(101);
    }

    @Override
    void createContent() {
        config = ClientConfiguration.loadConfig(CLIENT_CONFIG); // @todo hotfix

        Window loginWindow = new Window("", getSkin());
        Label userLabel = new Label("User", getSkin());
        this.username = new TextField("", getSkin());
        username.setMessageText("User Name");

        TextButton loginButton = new TextButton("Connect", getSkin());
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                connectThenLogin();
            }

        });

        loginWindow.getColor().a = 0.8f;
        loginWindow.add(userLabel);
        loginWindow.row();
        loginWindow.add(username).width(200);
        loginWindow.row();
        loginWindow.add(loginButton).padTop(20).expandX().row();
        getMainTable().add(loginWindow).width(400).height(300).row();

        serverList = new List<>(Skins.DEFAULT_SKIN);
        serverList.setItems(config.getNetwork().getServers());
        ScrollPane scrollPane = new ScrollPane(serverList, Skins.DEFAULT_SKIN);
        scrollPane.setScrollingDisabled(true, false);
        getMainTable().add(scrollPane).size(400, 200).row();

        /** @todo revisar -- {@link ScrollPane} no es dinamico, habría que reinicializarlo o algo asi */
        TextButton addServerButton = new TextButton("Agregar servidor", Skins.DEFAULT_SKIN);
        addServerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((TextButton)actor).isPressed()) {
                    TextField serverName, serverHostname, serverPort;
                    serverName = new TextField("", Skins.DEFAULT_SKIN);
                    serverName.setMessageText("Nombre del servidor");
                    serverHostname = new TextField("", Skins.DEFAULT_SKIN);
                    serverHostname.setMessageText("IP del servidor");
                    serverPort = new TextField("", Skins.DEFAULT_SKIN);
                    serverPort.setMessageText("Puerto del servidor");
                    Dialog dialog = new Dialog("Agregar servidor", Skins.DEFAULT_SKIN) {
                        @Override
                        protected void result(Object object) {
                            serverList.getItems().add(new ClientConfiguration.Network.Server(serverName.getText(), serverHostname.getText(), Integer.parseInt(serverPort.getText())));
                            super.result(object);
                        }
                    };
                    dialog.getContentTable().add(serverName).row();
                    dialog.getContentTable().add(serverHostname).row();
                    dialog.getContentTable().add(serverPort);
                    dialog.button("Agregar");
                    dialog.show(getStage());
                }
            }
        });
        getMainTable().add(addServerButton).width(400);

        getStage().setKeyboardFocus(username);
    }

    private void connectThenLogin() {

        if (this.canConnect) {
            String user = username.getText();

            ClientConfiguration.Network.Server server = serverList.getSelected();
            if (server == null)
                return;
            String ip = server.getHostname();
            int port = server.getPort();

            if (clientSystem.getState() != MarshalState.STARTING && clientSystem.getState() != MarshalState.STOPPING) {
                if (clientSystem.getState() != MarshalState.STOPPED)
                    clientSystem.stop();
                if (clientSystem.getState() == MarshalState.STOPPED) {

                    clientSystem.getKryonetClient().setHost(ip);
                    clientSystem.getKryonetClient().setPort(port);

                    clientSystem.start();
                    if (clientSystem.getState() == MarshalState.STARTED) {
                        clientSystem.getKryonetClient().sendToAll(new JoinLobbyRequest(user));
                        this.canConnect = false;
                    } else if (clientSystem.getState() == MarshalState.FAILED_TO_START) {
                        AOAssetManager assetManager = AOGame.getGlobalAssetManager();

                        Dialog dialog = new Dialog(assetManager.getMessages(Messages.FAILED_TO_CONNECT_TITLE), getSkin());
                        dialog.text(assetManager.getMessages(Messages.FAILED_TO_CONNECT_DESCRIPTION));
                        dialog.button("OK");
                        dialog.show(getStage());
                        this.canConnect = true;
                    }
                }
            }
        }
    }

    public ClientSystem getClientSystem() {
        return clientSystem;
    }
}
