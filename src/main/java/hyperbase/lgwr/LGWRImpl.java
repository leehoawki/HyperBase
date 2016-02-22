package hyperbase.lgwr;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class LGWRImpl implements LGWR {

    static Logger LOG = Logger.getLogger(LGWRImpl.class);

    FileWriter writer;

    BlockingQueue<Redo> queue = new LinkedBlockingQueue<Redo>();

    public LGWRImpl() {
        this(LGWRImpl.class.getResource("/").getPath() + "/redo", "hyper.redo");
    }

    public LGWRImpl(String dirPath, String filename) {
        LOG.info(String.format("LGWR initializing at %s", dirPath));
        File dir = new File(dirPath);
        String filePath = dirPath + "/" + filename;
        if (!dir.exists()) {
            dir.mkdirs();
        } else if (!dir.isDirectory()) {
            LOG.error(String.format("LGWR dirPath %s conflicts. Initialization failed.", dirPath));
            throw new IllegalStateException();
        }

        try {
            this.writer = new FileWriter(filePath);
        } catch (IOException ex) {
            LOG.error(String.format("Redo log open failed %s.", filePath), ex);
            throw new IllegalStateException(ex);
        }
        new Thread(new Dispatcher(queue, writer)).start();
        LOG.info("LGWR initialization completed.");
    }

    @Override
    public void append(Redo redo) {
        try {
            this.queue.put(redo);
        } catch (InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    static class Dispatcher implements Runnable {

        FileWriter writer;

        BlockingQueue<Redo> queue;

        public Dispatcher(BlockingQueue<Redo> queue, FileWriter writer) {
            this.queue = queue;
            this.writer = writer;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Redo redo = queue.take();
                    writer.write(String.format("%s:%s\n", redo.action, redo.data));
                    writer.flush();
                } catch (IOException ex) {
                    LOG.error("", ex);
                    throw new IllegalStateException(ex);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

