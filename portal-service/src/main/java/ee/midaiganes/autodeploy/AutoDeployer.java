package ee.midaiganes.autodeploy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import ee.midaiganes.javax.servlet.PortalPluginListener;
import ee.midaiganes.util.StringPool;

public class AutoDeployer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(AutoDeployer.class);
    private final WatchService watcher;
    private final Path autodeployDir;
    private final Path webappsDir;
    private final DocumentBuilderFactory documentBuilderFactory;
    private boolean run = true;

    public AutoDeployer(Path autodeployDir, Path webappsDir) throws IOException {
        this.autodeployDir = autodeployDir;
        this.webappsDir = webappsDir;
        log.info("AUTODEPLOY DIR: '" + autodeployDir.toFile().getAbsolutePath() + "'; WEBAPPS DIR: '" + this.webappsDir.toFile().getAbsolutePath() + "'");
        watcher = FileSystems.getDefault().newWatchService();
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        // WatchKey key =
        autodeployDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    @Override
    public void run() {
        while (run) {
            WatchKey key = null;
            try {
                key = watcher.take();// throws
                for (WatchEvent<?> event : key.pollEvents()) {
                    try {
                        doWithWatchEvent(event);
                    } catch (ZipException e) {
                        log.info(e.getMessage(), e);
                    }
                }
            } catch (ClosedWatchServiceException e) {
                log.info("AUTODEPLOYER STOPPED");
                break;
            } catch (InterruptedException e) {
                log.info("autodeployer interrupted");
                log.debug(e.getMessage(), e);
            } catch (RuntimeException | IOException e) {
                log.error(e.getMessage(), e);
            } finally {
                if (key != null) {
                    key.reset();
                }
            }
        }
    }

    private void doWithWatchEvent(WatchEvent<?> event) throws ZipException, IOException {
        WatchEvent.Kind<?> kind = event.kind();
        if (!kind.equals(StandardWatchEventKinds.OVERFLOW)) {
            WatchEvent<?/* Path */> ev = event;
            Path name = (Path) ev.context();
            Path child = autodeployDir.resolve(name);
            if (isReadableWarFileCreated(kind, child)) {
                doWithWarFile(child);
            }
        }
    }

    private boolean isReadableWarFileCreated(WatchEvent.Kind<?> kind, Path child) {
        if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE) || kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
            if (Files.isRegularFile(child, LinkOption.NOFOLLOW_LINKS)) {
                if (Files.isReadable(child)) {
                    return true;
                }
                log.warn("not readable file: '{}'", child);
            } else {
                log.warn("not regular file: '{}'", child);
            }
        } else {
            log.warn("kind: '{}'", kind);
        }
        return false;
    }

    private void doWithWarFile(Path warFilePath) throws ZipException, IOException {
        File childFile = warFilePath.toFile();
        try (ZipFile warFile = new ZipFile(childFile, ZipFile.OPEN_READ, Charsets.UTF_8)) {
            TempZipFile tempZipFile = new TempZipFile();
            try (ZipOutputStream tempZipFileStream = tempZipFile.getZipOutputStream()) {
                Enumeration<? extends ZipEntry> e = warFile.entries();
                while (e.hasMoreElements()) {
                    doWithZipEntry(warFile, tempZipFileStream, e.nextElement());
                }
            }
            Path source = tempZipFile.file.toPath();
            Path target = this.webappsDir.resolve(childFile.getName());
            try {
                Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                log.debug(e.getMessage(), e);
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("Moved '{}' to '{}'.", source, target);
        }
    }

    private void doWithZipEntry(ZipFile warFile, ZipOutputStream tempZipFile, ZipEntry entry) throws IOException {
        try {
            String name = entry.getName();
            log.debug("zip-entry: '{}'", name);
            if ((name.equals("WEB-INF/web.xml") || name.equals("/WEB-INF/web.xml")) && !entry.isDirectory()) {
                doWithWebXmlZipEntry(warFile, tempZipFile, entry);
            } else {
                tempZipFile.putNextEntry(entry);
                if (!entry.isDirectory()) {
                    try (InputStream in = new BufferedInputStream(warFile.getInputStream(entry))) {
                        ByteStreams.copy(in, tempZipFile);
                    }
                }
            }
        } finally {
            tempZipFile.closeEntry();
        }
    }

    private void doWithWebXmlZipEntry(ZipFile warFile, ZipOutputStream tempZipFile, ZipEntry entry) {
        try (InputStream is = new BufferedInputStream(warFile.getInputStream(entry))) {
            try {
                Document doc = documentBuilderFactory.newDocumentBuilder().parse(is);
                Node webapp = doc.getFirstChild();

                NodeList filters = doc.getElementsByTagName("filter");
                NodeList servlets = doc.getElementsByTagName("servlet");

                Node listenerPosition = filters.getLength() != 0 ? filters.item(0) : servlets.item(0);
                listenerPosition = listenerPosition != null ? listenerPosition : doc.getElementsByTagName("jsp-config").item(0);
                if (listenerPosition == null) {
                    log.warn("filter, servlet or jsp-config not found! Plugin will not work!");
                }
                webapp.insertBefore(createListenerElement(doc, PortalPluginListener.class.getName()), listenerPosition);

                //
                DOMSource source = new DOMSource(doc);
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, StringPool.UTF_8);
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                // write to tmp zip entry
                tempZipFile.putNextEntry(new ZipEntry(entry.getName()));
                transformer.transform(source, new StreamResult(tempZipFile));
            } catch (SAXException | ParserConfigurationException | TransformerConfigurationException | TransformerFactoryConfigurationError e) {
                log.error(e.getMessage(), e);
            } catch (TransformerException e) {
                log.error(e.getMessage(), e);
            }
        } catch (/* JAXBException | */IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static Element createListenerElement(Document doc, String listenerClass) {
        Element listener = doc.createElement("listener");
        listener.appendChild(createTextNode(doc, "listener-class", listenerClass));
        return listener;
    }

    private static Element createTextNode(Document doc, String name, String value) {
        Element el = doc.createElement(name);
        el.appendChild(doc.createTextNode(value));
        return el;
    }

    public void stop() {
        try {
            this.run = false;
            watcher.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static class TempZipFile {
        private final File file;

        public TempZipFile() throws IOException {
            file = Files.createTempFile(null, null).toFile();
        }

        public ZipOutputStream getZipOutputStream() throws FileNotFoundException {
            return new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file), 1024), Charsets.UTF_8);
        }
    }
}
