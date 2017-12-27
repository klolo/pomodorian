package pl.klolo.pomodoro.engine;


import javafx.fxml.FXMLLoader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Klasa odpowiada za ladowanie widoków fxml za pomoca springa.
 */
@Slf4j
@Getter
@Component
public class SpringFxmlLoader {

    private static final String BUNDLES_PATH = "application";

    private static String defaultLocale = "pl";

    private static final ApplicationContext applicationContext
            = new AnnotationConfigApplicationContext(SpringApplicationConfig.class);

    public static ResourceBundle resourceBundle;

    public Object load(final String url) {
        log.info("Load stage: {}", url);

        try (final InputStream fxmlStream = getClass().getClassLoader().getResourceAsStream(url)) {
            final FXMLLoader loader = new FXMLLoader();
            resourceBundle = ResourceBundle.getBundle(BUNDLES_PATH, new Locale(defaultLocale));
            loader.setResources(resourceBundle);
            loader.setControllerFactory(applicationContext::getBean);

            final URL resource = getClass().getClassLoader().getResource(url);
            loader.setLocation(resource);
            return loader.load(fxmlStream);
        }
        catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public static void changeLocale(final String locale) {
        defaultLocale = locale;
    }
}