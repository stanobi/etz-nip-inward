package com.etz.nips.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="Header")
@XmlType(propOrder={"batchNo", "recNo", "channelCode", "tranxLocation"})
public class Header
{
  private String batchNo;
  private String recNo;
  private String channelCode;
  private String tranxLocation;
  
  @XmlElement(name="BatchNumber")
  public String getBatchNo()
  {
    return this.batchNo;
  }
  
  public void setBatchNo(String batchNo)
  {
    this.batchNo = batchNo;
  }
  
  @XmlElement(name="NumberOfRecords")
  public String getRecNo()
  {
    return this.recNo;
  }
  
  public void setRecNo(String recNo)
  {
    this.recNo = recNo;
  }
  
  @XmlElement(name="ChannelCode")
  public String getChannelCode()
  {
    return this.channelCode;
  }
  
  public void setChannelCode(String channelCode)
  {
    this.channelCode = channelCode;
  }
  
  @XmlElement(name="TransactionLocation")
  public String getTranxLocation()
  {
    return this.tranxLocation;
  }
  
  public void setTranxLocation(String tranxLocation)
  {
    this.tranxLocation = tranxLocation;
  }
}
