package org.teamapps.application.api.config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import com.thoughtworks.xstream.converters.basic.IntConverter;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

public class ApplicationConfigXml<CONFIG> {

	public CONFIG readConfigFile(String xml) {
		XStream xstream = createXStream();
		return (CONFIG) xstream.fromXML(xml);
	}

	public String getConfigXml(CONFIG config) {
		XStream xStream = createXStream();
		return xStream.toXML(config);
	}

	private static XStream createXStream() {
		XStream xstream = new XStream(new DomDriver()) {
			@Override
			protected void setupConverters() {
			}
		};
		xstream.registerConverter(new ReflectionConverter(xstream.getMapper(), xstream.getReflectionProvider()), XStream.PRIORITY_VERY_LOW);
		xstream.registerConverter(new BooleanConverter(), XStream.PRIORITY_NORMAL);
		xstream.registerConverter(new IntConverter(), XStream.PRIORITY_NORMAL);
		xstream.registerConverter(new StringConverter(), XStream.PRIORITY_NORMAL);
		xstream.registerConverter(new CollectionConverter(xstream.getMapper()), XStream.PRIORITY_NORMAL);
		xstream.addPermission(NoTypePermission.NONE);
		xstream.addPermission(NullPermission.NULL);
		xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
		xstream.ignoreUnknownElements();
		xstream.allowTypes(new Class[]{
				String.class
		});
		return xstream;
	}
}
