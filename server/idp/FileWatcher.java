package idp;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.net.Socket;
import java.io.ObjectOutputStream;

public class FileWatcher{

    private Socket socket;
    private String path;
    private ObjectOutputStream oos=null;
    private int input_len;
    int cnt=1;

    public FileWatcher(Socket s, String path, int input_len) {
        this.socket = s;
        this.path = path;
        this.input_len = input_len*2+1;
    }

    public void watch() {
        try {
            oos  = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Watching directory for the changes");

            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path directory = Path.of(path);
            WatchKey watchKey = directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
            while (true) {
                for (WatchEvent<?> event : watchKey.pollEvents()) {

                    // STEP5: Get file name from even context
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;

                    Path fileName = pathEvent.context();

                    // STEP6: Check type of event.
                    WatchEvent.Kind<?> kind = event.kind();

                    // STEP7: Perform necessary action with the event
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE ) {
                        cnt= cnt+ 1;
                        float percent = (cnt/input_len)*100;
                        oos.writeObject("2");
                        oos.writeObject("Executing.."+String.valueOf(percent));
                        System.out.println("Executing.."+String.valueOf(percent));
                     }
                }
                // STEP8: Reset the watch key everytime for continuing to use it for further event polling
                boolean valid = watchKey.reset();
                if (!valid) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

