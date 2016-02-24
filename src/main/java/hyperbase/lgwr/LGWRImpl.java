package hyperbase.lgwr;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class LGWRImpl implements LGWR {

    static Logger LOG = Logger.getLogger(LGWRImpl.class);

    String filePath;

    FileWriter writer;

    BlockingQueue<Redo> queue;

    public LGWRImpl() {
        this(LGWRImpl.class.getResource("/").getPath() + "/hyper.redo");
    }

    public LGWRImpl(String filePath) {
        LOG.info(String.format("LGWR initializing at %s", filePath));
        this.filePath = filePath;
        try {
            this.writer = new FileWriter(filePath);
        } catch (IOException ex) {
            LOG.error(String.format("Redo log open failed %s.", filePath), ex);
            throw new IllegalStateException(ex);
        }

        queue = new LinkedBlockingQueue<Redo>();
        new Thread(new Dispatcher(queue, writer)).start();
        LOG.info("LGWR initialization completed.");
    }

    @Override
    public void append(Redo redo) {
        try {
            this.queue.put(redo);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Iterator<Redo> read() {
        return new RedoIterator(filePath);
    }


    static class RedoIterator implements Iterator<Redo> {

        LineIterator iterator;

        public RedoIterator(String filePath) {
            try {
                iterator = FileUtils.lineIterator(new File(filePath));
            } catch (IOException ex) {
                LOG.error(String.format("Redo log open failed %s.", filePath), ex);
                throw new IllegalStateException(ex);
            }
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Redo next() {
            String line = iterator.nextLine();
            String[] a = StringUtils.split(line, ':');
            return new Redo(a[0], Arrays.copyOfRange(a, 1, a.length - 1));
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
                    writer.write(String.format("%s:%s\n", redo.getAction(), StringUtils.join(redo.getData(), ',')));
                    writer.flush();
                } catch (IOException ex) {
                    LOG.error("Failed to write data to Redo log.", ex);
                    throw new IllegalStateException(ex);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

