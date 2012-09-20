package mx.ferreyra.dogmap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlParser extends DefaultHandler 
{
	private boolean location,lat,lon;
	public String latitude;
	public String longitude;

	public void characters(char[] ch, int start, int length)
			throws SAXException 
	{
		super.characters(ch, start, length);
		if(location) 
		{
			if(lat) 
			{
				latitude=new String(ch, start, length);
			}
			if(lon) 
			{
				longitude=new String(ch, start, length);
			}
		}
	}

	public void endDocument() throws SAXException 
	{
		super.endDocument();
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException 
			{
		super.endElement(uri, localName, qName);
		if(localName.equalsIgnoreCase("location")) 
		{
			location=false;
		}
		if(localName.equalsIgnoreCase("lat"))
		{
			lat=false;
		}
		if(localName.equalsIgnoreCase("lng"))
		{
			lon=false;
		}
	}

	public void startDocument() throws SAXException 
	{
		super.startDocument();
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if(localName.equalsIgnoreCase("location")) 
		{
			location=true;
		}
		if(localName.equalsIgnoreCase("lat"))
		{
			lat=true;
		}
		if(localName.equalsIgnoreCase("lng"))
		{
			lon=true;
		}
	}

}
