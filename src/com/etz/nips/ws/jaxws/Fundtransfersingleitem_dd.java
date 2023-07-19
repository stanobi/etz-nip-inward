package com.etz.nips.ws.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="fundtransfersingleitem_dd", namespace="http://ws.nips.etz.com")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="fundtransfersingleitem_dd", namespace="http://ws.nips.etz.com")
public class Fundtransfersingleitem_dd
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
