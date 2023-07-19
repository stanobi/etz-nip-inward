package com.etz.nips.ws.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="financialinstitutionlistResponse", namespace="http://ws.nips.etz.com")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="financialinstitutionlistResponse", namespace="http://ws.nips.etz.com")
public class FinancialInstitutionListResponse
{
  @XmlElement(name="return")
  private String _return;
  
  public String getReturn()
  {
    return this._return;
  }
  
  public void setReturn(String new_return)
  {
    this._return = new_return;
  }
}
