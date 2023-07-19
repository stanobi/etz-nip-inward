/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.etz.nips.util;

/**
 *
 * @author olayiwola.okunola
 */
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;
public class Misc {
    private static final Logger LOG = Logger.getLogger(Misc.class);
  
  public Marshaller getMarshaller(Class aClass)
  {
    Marshaller m = null;
    try
    {
      JAXBContext context = JAXBContext.newInstance(new Class[] { aClass });
      m = context.createMarshaller();
    }
    catch (JAXBException ex)
    {
      LOG.error("getMarshaller - JAXBException: " + ex.getMessage());
    }
    return m;
  }
  
  public Unmarshaller getUnMarshaller(Class aClass)
  {
    Unmarshaller u = null;
    try
    {
      JAXBContext context = JAXBContext.newInstance(new Class[] { aClass });
      u = context.createUnmarshaller();
    }
    catch (JAXBException ex)
    {
      LOG.error("getUnMarshaller - JAXBException: " + ex.getMessage());
    }
    return u;
  }
  
  public String getPropertyValue(String prop)
  {
    String propValue = "";
    try
    {
      Properties applicationProperties = new Properties();
      
      FileInputStream inputStream = new FileInputStream("nibss-nip.properties");
      
      applicationProperties.load(inputStream);
      propValue = applicationProperties.getProperty(prop);
      LOG.info(prop + ": " + propValue);
    }
    catch (Exception e)
    {
      LOG.error("Could not get the value of property: " + prop);
      e.printStackTrace();
    }
    return propValue;
  }
  
  public long generateRandom(int length)
  {
    Random random = new Random();
    char[] digits = new char[length];
    digits[0] = ((char)(random.nextInt(9) + 49));
    for (int i = 1; i < length; i++) {
      digits[i] = ((char)(random.nextInt(10) + 48));
    }
    return Long.parseLong(new String(digits));
  }
  
  public String getPaymentRef(String op)
  {
    String random = UUID.randomUUID().toString().replace("-", "");
    String paymentRef = "NIBSS-NIPS" + random.substring(random.length() - 10) + "-" + op + "-eTz";
    return paymentRef;
  }
}
