import java.io.InputStream;

public class Resources {
    public static InputStream getResourceAsStream(String config) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(config);
    }
}
