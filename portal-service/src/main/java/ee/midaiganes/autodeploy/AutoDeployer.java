package ee.midaiganes.autodeploy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import ee.midaiganes.javax.servlet.PortalPluginListener;
import ee.midaiganes.servlet.PortletServlet;

public class AutoDeployer implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(AutoDeployer.class);
	// private final String autoDeployDir;
	private final WatchService watcher;
	private final Path dir;

	public AutoDeployer(String autoDeployDir) throws IOException {
		// this.autoDeployDir = autoDeployDir;
		log.info("AUTODEPLOY DIR: " + new File(autoDeployDir).getAbsolutePath());
		// System.getProperty("java.io.tmpdir");
		dir = Paths.get(autoDeployDir);
		watcher = FileSystems.getDefault().newWatchService();
		// WatchKey key =

		dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
	}

	@Override
	public void run() {
		while (true) {
			WatchKey key = null;
			try {
				key = watcher.take();// throws
				for (WatchEvent<?> event : key.pollEvents()) {
					doWithWatchEvent(event);
				}
			} catch (ClosedWatchServiceException e) {
				log.info("AUTODEPLOYER STOPPED");
				break;
			} catch (InterruptedException e) {
				log.info("autodeployer interrupted");
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
			Path child = dir.resolve(name);
			if (isReadableWarFileCreated(kind, child)) {
				doWithWarFile(child);
			}
		}
	}

	private boolean isReadableWarFileCreated(WatchEvent.Kind<?> kind, Path child) {
		if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
			if (Files.isRegularFile(child, LinkOption.NOFOLLOW_LINKS)) {
				if (Files.isReadable(child)) {
					return true;
				} else {
					log.warn("not readable file:" + child);
				}
			} else {
				log.warn("not regular file: " + child);
			}
		} else {
			log.warn("kind: " + kind);
		}
		return false;
	}

	private void doWithWarFile(Path warFilePath) throws ZipException, IOException {
		File childFile = warFilePath.toFile();
		try (ZipFile warFile = new ZipFile(childFile); ZipOutputStream tempZipFile = getTempZipFile(childFile.getName())) {
			Enumeration<? extends ZipEntry> e = warFile.entries();
			while (e.hasMoreElements()) {
				doWithZipEntry(warFile, tempZipFile, e.nextElement());
			}
		}
	}

	private void doWithZipEntry(ZipFile warFile, ZipOutputStream tempZipFile, ZipEntry entry) throws IOException {
		String name = entry.getName();
		log.error("zip-entry: " + name);
		if (name.equals("WEB-INF/web.xml") && !entry.isDirectory()) {
			tempZipFile.putNextEntry(entry);
			try (InputStream in = warFile.getInputStream(entry)) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1) {
					tempZipFile.write(buffer, 0, bytesRead);
				}
			}
			doWithWebXmlZipEntry(warFile, tempZipFile, entry);
		} else {
			tempZipFile.putNextEntry(entry);
			if (!entry.isDirectory()) {
				try (InputStream in = warFile.getInputStream(entry)) {
					int bytesRead;
					byte[] buffer = new byte[1024];
					while ((bytesRead = in.read(buffer)) != -1) {
						tempZipFile.write(buffer, 0, bytesRead);
					}
				}
			}
		}
		tempZipFile.closeEntry();
	}

	private void doWithWebXmlZipEntry(ZipFile warFile, ZipOutputStream tempZipFile, ZipEntry entry) {
		try (InputStream is = warFile.getInputStream(entry)) {
			try {
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
				Node webapp = doc.getFirstChild();

				NodeList filters = doc.getElementsByTagName("filter");
				NodeList servlets = doc.getElementsByTagName("servlet");
				NodeList servletMappings = doc.getElementsByTagName("servlet-mapping");

				Node listenerPosition = filters.getLength() != 0 ? filters.item(0) : servlets.item(0);

				webapp.insertBefore(createListenerElement(doc, PortalPluginListener.class.getName()), listenerPosition);
				webapp.insertBefore(createServletElement(doc, PortletServlet.class.getName()), servlets.item(0));
				webapp.insertBefore(createServletMappingElement(doc, PortletServlet.class.getName(), "/WEB-INF/portlet-servlet"), servletMappings.item(0));

				//
				DOMSource source = new DOMSource(doc);
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				StreamResult result = new StreamResult(baos);
				transformer.transform(source, result);
				System.out.println("XML = " + baos.toString("UTF-8"));
			} catch (SAXException | ParserConfigurationException | TransformerConfigurationException | TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (/* JAXBException | */IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	private static Element createServletElement(Document doc, String name) {
		Element servlet = doc.createElement("servlet");
		servlet.appendChild(createTextNode(doc, "description", name));
		servlet.appendChild(createTextNode(doc, "display-name", name));
		servlet.appendChild(createTextNode(doc, "servlet-name", name));
		servlet.appendChild(createTextNode(doc, "servlet-class", name));
		servlet.appendChild(createTextNode(doc, "load-on-startup", "1"));
		servlet.appendChild(createTextNode(doc, "async-supported", "true"));
		return servlet;
	}

	private static Element createServletMappingElement(Document doc, String name, String pattern) {
		Element servletMapping = doc.createElement("servlet-mapping");
		servletMapping.appendChild(createTextNode(doc, "servlet-name", name));
		servletMapping.appendChild(createTextNode(doc, "url-pattern", pattern));
		return servletMapping;
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

	private ZipOutputStream getTempZipFile(String name) throws FileNotFoundException, IOException {
		return new ZipOutputStream(new FileOutputStream(Files.createTempFile(name, ".war").toFile()));
	}

	public void stop() {
		try {
			watcher.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
