package com.etz.nips.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="FinancialInstitutionListResponse")
public class FinancialInstitutionListResponse
{
  private String batchNo;
  private String destInstCode;
  private String channel;
  private String recNo;
  private String respCode;
  
  @XmlElement(name="BatchNumber")
  public String getBatchNo()
  {
    return this.batchNo;
  }
  
  public void setBatchNo(String batchNo)
  {
    this.batchNo = batchNo;
  }
  
  @XmlElement(name="DestinationInstitutionCode")
  public String getDestInstCode()
  {
    return this.destInstCode;
  }
  
  public void setDestInstCode(String destInstCode)
  {
    this.destInstCode = destInstCode;
  }
  
  @XmlElement(name="ChannelCode")
  public String getChannel()
  {
    return this.channel;
  }
  
  public void setChannel(String channel)
  {
    this.channel = channel;
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
  
  @XmlElement(name="ResponseCode")
  public String getRespCode()
  {
    return this.respCode;
  }
  
  public void setRespCode(String respCode)
  {
    this.respCode = respCode;
  }
}
