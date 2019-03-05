package pl.edu.pwr.wordnetloom.client.config;

import javafx.embed.swing.SwingFXUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * A singleton CDI provider that is used to load the resource bundle and provide
 * it for the CDI injection.
 */
@Singleton
public class ResourceProvider {

    public static Properties properties = new Properties();
    public static Map<String, Image> lexiconImages = new HashMap<>();

    @PostConstruct
    public void postConstruct() {

        FileInputStream file = null;
        String path = "./application.properties";

        //load the file handle for main.properties
        try {
            file = new FileInputStream(path);

            //load all the properties from this file
            properties.load(file);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        loadImage();
    }

    private Image loadFile(String filename) {
        InputStream file = null;
        String path = "./icons/" + filename;
        Image img = null;
        //load the file handle for main.properties
        try {
            file = new FileInputStream(path);
            img = new ImageIcon(path).getImage();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return img;
    }

    public static javafx.scene.image.Image getById(Long id) {
        if (id != null && lexiconImages.get("lexicon." + id) != null)
            return SwingFXUtils.toFXImage(toBufferedImage(lexiconImages.get("lexicon." + id)), null);
        return null;
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    private void loadImage() {
        properties.stringPropertyNames()
                .forEach(p -> {
                    if (p.startsWith("lexicon.")) {
                        Image i = loadFile(properties.getProperty(p));
                        lexiconImages.put(p, i);
                    }
                });
    }

    /*
     * Due to the @Produces annotation this resource bundle can be injected in all views.
     */
    @Produces
    private ResourceBundle defaultResourceBundle = ResourceBundle.getBundle("default");

    @Produces
    private Properties defaultApplicationProperties() {
        return properties;
    }
}
