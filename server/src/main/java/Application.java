import com.home.mouse.server.controller.MouseController;

import java.awt.*;
import java.io.IOException;

public class Application {

    static {
        try {
            NativeUtils.loadLibraryFromJar("/lib/libopencv_java412.dylib");
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
    }

    public static void main(String[] args) throws AWTException, IOException {
        System.setProperty("apple.awt.UIElement", "true");

        NativeUtils.loadLoggingConfiguration();

        new MouseController(new Robot(), getPort()).start();
    }

    private static int getPort() {
        String port = System.getProperty("port");
        int portInt = 6666;
        if (port != null && !port.isEmpty()) {
            portInt = Integer.valueOf(port);
        }
        return portInt;
    }


}
