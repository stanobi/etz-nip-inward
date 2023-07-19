package com.etz.nips.ws.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="amountblock", namespace="http://ws.nips.etz.com")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="amountblock", namespace="http://ws.nips.etz.com")
public class AmountBlock
{
  @XmlElement(name="request")
  private String request;
  
  public String getRequest()
  {
    return this.request;
  }
  
  public void setRequest(String newRequest)
  {
    this.request = newRequest;
  }
}
