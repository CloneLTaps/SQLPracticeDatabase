package CloneLTaps.me.SQLPracticeDatabase;

import CloneLTaps.me.SQLPracticeDatabase.gui.ScreenGui;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import java.util.LinkedHashMap;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class UpdateSettingsRunnable implements Runnable {
    private final Map<String, Object> settingsMap = new LinkedHashMap<>();
    private final AtomicBoolean shutDown = new AtomicBoolean(false);
    private final BlockingQueue<ScreenGui> blockingQueue;

    /**
     * I am updating the config right away in case the program gets terminated prematurely. Since I dont want to freeze
     * the main thread I am handling the file write operations on a separate thread. All values being accessed from
     * FileManager are final which is inherently thread safe in Java (only while reading).
     */
    public UpdateSettingsRunnable() {
        this.blockingQueue = new LinkedBlockingQueue<>();;
    }

    @Override
    public void run() {
        while(!shutDown.get()) {
            try {
                final ScreenGui screenGui = blockingQueue.take();

                final int sizeOfFont = screenGui.getSizeOfFont();
                final String fontName = screenGui.getFontName();

                settingsMap.clear();
                settingsMap.put("fontsize", sizeOfFont);
                settingsMap.put("font", fontName);
                try {
                    // These options keeps the correct "pretty" structure of yml
                    final DumperOptions options = new DumperOptions();
                    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                    options.setPrettyFlow(true);

                    final Yaml yml = new Yaml(options);
                    final FileWriter writer = new FileWriter(screenGui.getSettingsFile().getAbsoluteFile());
                    yml.dump(settingsMap, writer);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertElement(final ScreenGui obj) {
        blockingQueue.add(obj);
    }

    public void shutDownThread() {
        this.shutDown.set(true);
    }
}
