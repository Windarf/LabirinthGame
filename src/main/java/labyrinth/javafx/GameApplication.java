package labyrinth.javafx;

import com.gluonhq.ignite.guice.GuiceContext;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import labyrinth.results.GameResultRepository;
import labyrinth.util.javafx.ControllerHelper;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.List;

public class GameApplication extends Application {

    private final GuiceContext context = new GuiceContext(this, () -> List.of(
            new AbstractModule() {
                @Override
                protected void configure() {
                    bind(GameResultRepository.class).in(Singleton.class);
                }
            }
    ));

    @Inject
    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage stage) throws IOException {
        Logger.info("Starting application");
        context.init();
        stage.setTitle("Labyrinth Game");
        Image icon = new Image("/images/icon.png");
        stage.getIcons().add(icon);
        stage.setResizable(false);
        ControllerHelper.loadAndShowFXML(fxmlLoader, "/fxml/menu.fxml", stage);


    }

}
