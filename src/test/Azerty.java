package test;

import java.util.Date;
import java.util.Formatter;
import java.util.List;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import product.USSDMenu;

@SuppressWarnings("unused")
public class Azerty {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Date start = new Date();
		
		// System.out.println(new Formatter().format("%4d", 1024));
		// System.out.println(new Formatter().format("%04d", 104));
		// System.out.println(new Formatter().format("%.2f", 1.057));
		
		// attributes : type = [text|number|msisdn|static] ; network = [on|off] ; ton = [National|International] ; value = [value_to_fixe]
		// in case type equals to msisdn, set attribute <<ton>> required
		// in case type equals to msisdn, set attribute <<network>> if needed (<<ton>> must be set to 'National')
		// in case type equals to static, set attribute <<value>> required

		Element racine = new Element("SERVICE-CODE-" + "250");
		Document document = new Document(racine);

		// body
		Element body = new Element("menu");
		racine.addContent(body);

		// body.addContent(new Element("choice-1").addContent(new Element("choice-1").addContent(new Element("input").setAttribute("type","msisdn").setAttribute("network", "on"))).addContent(new Element("choice-2").addContent(new Element("input").setAttribute("type","number"))).addContent(new Element("choice-3").addContent(new Element("input").setAttribute("type","msisdn"))).addContent(new Element("choice-4").addContent(new Element("input").setAttribute("type","msisdn").setAttribute("network", "off"))).addContent(new Element("choice-5").addContent(new Element("input").setAttribute("type","text"))).addContent(new Element("choice-6").addContent(new Element("input").setAttribute("type","static").setAttribute("value", "80"))));
		body.addContent(new Element("choice-1").addContent(new Element("choice-1").addContent(new Element("input").setAttribute("type","msisdn").setAttribute("ton","National").setAttribute("network", "on"))).addContent(new Element("choice-2").addContent(new Element("input").setAttribute("type","msisdn").setAttribute("ton","National").setAttribute("network", "on"))).addContent(new Element("choice-3").addContent(new Element("input").setAttribute("type","msisdn").setAttribute("ton","National").setAttribute("network", "on"))).addContent(new Element("choice-4").addContent(new Element("input").setAttribute("type","msisdn").setAttribute("ton","National").setAttribute("network", "on"))).addContent(new Element("choice-5").addContent(new Element("input").setAttribute("type","msisdn").setAttribute("ton","National").setAttribute("network", "on"))).addContent(new Element("choice-6").addContent(new Element("input").setAttribute("type","msisdn").setAttribute("ton","National").setAttribute("network", "on"))));
		body.addContent(new Element("choice-2"));

	    /*Element racine = new Element("choice-250");
	    Document document = new Document(racine);*/

		// System.out.println(new Date().getTime() - start.getTime());
		
	    // afficher(document);
	    afficher(new USSDMenu().getContent(250));
	    
	    // System.out.println(new Date().getTime() - start.getTime());

	    System.out.println(document.getRootElement().getContent());
	    System.out.println((document.getRootElement()).getChild("menu").getContentSize());
	    System.out.println((document.getRootElement()).getChild("menu").getContent());

	    @SuppressWarnings("unchecked")
		List<Element> children = (document.getRootElement()).getChild("menu").getChildren();
	    System.out.println(children);

	    // System.out.println((document.getRootElement()).getChild("menu").getChild("choice-1").getChildren("choice-1").get(0).getChild("input").getAttributeValue("network"));
	    System.out.println((document.getRootElement()).getChild("menu").getChild("choice-1").getChild("choice-4").getChild("input").getAttributeValue("type"));
	    System.out.println((document.getRootElement()).getChild("menu").getChild("choice-1").getChild("choice-4").getContentSize());
	    System.out.println((document.getRootElement()).getChild("menu").getChild("choice-1").getChild("choice-4").getContent());
	    System.out.println((document.getRootElement()).getChild("menu").getChild("choice-1").getChild("choice-4").getChild("input").getContentSize());
	    System.out.println((document.getRootElement()).getChild("menu").getChild("choice-1").getChild("choice-4").getChild("input").getContent());
	    System.out.println((document.getRootElement()).getChild("menu").getChild("choice-1").getChild("choice-4").getChild("input").getChildren().size());
	    
	    // System.out.println(new Date().getTime() - start.getTime());
	}

	protected static void afficher(Document document) {
	    try {
	      XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
	      sortie.output(document, System.out);

	    } catch (java.io.IOException e) {

	    }
	  }

}
