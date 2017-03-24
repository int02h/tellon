package com.dpforge.tellon.app.config;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class XmlAppConfigReader extends AppConfigReader {
    private static final String PROJECT_TAG = "project";

    @Override
    AppConfig readConfig(String path) throws IOException {
        final Document doc;
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new File(path));
        } catch (Exception e) {
            throw new IOException(e);
        }

        doc.getDocumentElement().normalize();

        final AppConfig appConfig = new AppConfig();

        final NodeList projectNodes = doc.getElementsByTagName(PROJECT_TAG);
        for (int i = 0; i < projectNodes.getLength(); i++) {
            final Node node = projectNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                appConfig.addProject(readProject((Element) node));
            }
        }

        return appConfig;
    }

    private ProjectConfig readProject(Element element) {
        final ProjectConfig.Builder builder = new ProjectConfig.Builder();

        builder.name(element.getAttribute("name"));
        builder.path(element.getAttribute("path"));

        final NodeList contactNodes = element.getElementsByTagName("master-contact");
        final List<String> masterContacts = new ArrayList<>(contactNodes.getLength());
        for (int i = 0; i < contactNodes.getLength(); i++) {
            final Node node = contactNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                masterContacts.add(node.getTextContent().trim());
            }
        }

        builder.masterContacts(masterContacts);
        return builder.build();
    }
}
