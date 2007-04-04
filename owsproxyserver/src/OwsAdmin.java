/**
 * Class file to generate the web.xml configuration from a list of services
 */
import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

public class OwsAdmin
{
    static final String WEB_STYLESHEET = "metaWeb.xsl";
    static final String WEB_XML = "web.xml";
    
    static void generateWebXml(String[] args) throws Throwable {

        File webXmlDirectory = new File(args[0]);
        
        String servicesFilename = "services.xml";
        if (args.length == 2)
            servicesFilename = args[1];

        File stylesheet = new File(webXmlDirectory, WEB_STYLESHEET);
        File datafile   = new File(webXmlDirectory, servicesFilename);

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(datafile);

        TransformerFactory tFactory =
            TransformerFactory.newInstance();
        
        StreamSource stylesource = new StreamSource(stylesheet);
        Transformer transformer = tFactory.newTransformer(stylesource);
        
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml. customer .org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(document);
        FileOutputStream fo = new FileOutputStream(new File(webXmlDirectory, WEB_XML));
        StreamResult result = new StreamResult(fo);
        transformer.transform(source, result);
    }

    public static void main (String args[])
    {
        if (args.length < 1 || args.length > 2) {
            System.err.println ("Usage: java OwsAdmin DIRECTORY [SERVICES_FILE]");
            System.exit (1);
        }

        try {
            generateWebXml(args);
        } catch (Throwable e) {
            System.out.println("Error while generating web.xml:");
            e.printStackTrace();
        }
    }
}
