package ee.midaiganes.util;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XmlUtil {
    public static <A> A unmarshal(Class<A> clazz, InputStream in) throws JAXBException {
        return clazz.cast(JAXBElement.class.cast(getUnmarshaller(clazz).unmarshal(in)).getValue());
    }

    public static <A> A unmarshalWithoutJAXBElement(Class<A> clazz, InputStream in) throws JAXBException {
        return clazz.cast(getUnmarshaller(clazz).unmarshal(in));
    }

    public static void marshal(Object o, OutputStream os) throws JAXBException {
        getMarshaller(o.getClass()).marshal(o, os);
    }

    private static JAXBContext getContext(Class<?> clazz) throws JAXBException {
        return JAXBContext.newInstance(clazz.getPackage().getName(), clazz.getClassLoader());
    }

    private static Unmarshaller getUnmarshaller(Class<?> clazz) throws JAXBException {
        return getContext(clazz).createUnmarshaller();
    }

    private static Marshaller getMarshaller(Class<?> clazz) throws JAXBException {
        return getContext(clazz).createMarshaller();
    }
}
