package com.etz.nips.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="Record")
@XmlType(propOrder={"instCode", "instName", "category"})
public class Record
{
  private String instCode;
  private String instName;
  private String category;
  
  @XmlElement(name="InstitutionCode")
  public String getInstCode()
  {
    return this.instCode;
  }
  
  public void setInstCode(String instCode)
  {
    this.instCode = instCode;
  }
  
  @XmlElement(name="InstitutionName")
  public String getInstName()
  {
    return this.instName;
  }
  
  public void setInstName(String instName)
  {
    this.instName = instName;
  }
  
  @XmlElement(name="Category")
  public String getCategory()
  {
    return this.category;
  }
  
  public void setCategory(String category)
  {
    this.category = category;
  }
}
