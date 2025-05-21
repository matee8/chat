package hu.progtech.chat.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ViewManager {
    private static final Logger LOGGER = LogManager.getLogger(ViewManager.class);
    private final Stage primaryStage;
    private final Map<String, SceneDetails> scenes = new HashMap<>();

    private static class SceneDetails {
        Parent root;
        Object controller;

        SceneDetails(Parent root, Object controller) {
            this.root = root;
            this.controller = controller;
        }
    }

    public ViewManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void addScene(String name, String fxmlPath, Object controllerInstance) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            loader.setControllerFactory(param -> controllerInstance);

            Parent root = loader.load();
            scenes.put(name, new SceneDetails(root, controllerInstance));
            LOGGER.debug("Added scene: {} from FXML: {}", name, fxmlPath);
        } catch (IOException e) {
            LOGGER.error("Failed to load FXML for scene {}: {}", name, fxmlPath, e);
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }

    public void activate(String name) {
        SceneDetails sceneDetails = scenes.get(name);
        if (sceneDetails == null) {
            LOGGER.error("Cannot activate scene '{}': Not found.", name);
            return;
        }

        if (sceneDetails.controller instanceof InitializableController controller) {
            controller.onViewShown();
        }

        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(sceneDetails.root));
        } else {
            primaryStage.getScene().setRoot(sceneDetails.root);
        }
        primaryStage.sizeToScene();
        LOGGER.info("Activated scene: {}", name);
    }

    public Object getController(String name) {
        SceneDetails details = scenes.get(name);
        return (details != null) ? details.controller : null;
    }
}
