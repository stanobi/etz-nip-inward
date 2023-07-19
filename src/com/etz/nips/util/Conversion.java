package com.etz.nips.util;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;

public class Conversion
{
  private static final Logger LOG = Logger.getLogger(Conversion.class);
  
  public String jaxbObj2XML(Object obj, Class aClass)
  {
    String xmlRequest = null;
    try
    {
      Marshaller m = new Misc().getMarshaller(aClass);
      m.setProperty("jaxb.formatted.output", Boolean.TRUE);
      StringWriter sw = new StringWriter();
      m.marshal(obj, sw);
      xmlRequest = sw.toString();
      LOG.info("xmlRequest: " + xmlRequest);
    }
    catch (JAXBException ex)
    {
      LOG.error("jaxbObj2XML JAXBException: " + ex.getMessage());
    }
    return xmlRequest;
  }
  
  public Object jaxbXML2Obj(String decryptRes, Class aClass)
  {
    Object obj = null;
    Unmarshaller u = new Misc().getUnMarshaller(aClass);
    try
    {
      obj = u.unmarshal(new StringReader(decryptRes));
    }
    catch (JAXBException ex)
    {
      LOG.error("jaxbXML2Obj - JAXBException: " + ex.getMessage());
    }
    return obj;
  }
}
