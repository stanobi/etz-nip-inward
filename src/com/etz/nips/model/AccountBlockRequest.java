package com.etz.nips.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="AccountBlockRequest")
@XmlType(propOrder={"sessionID", "destInstCode", "channelCode", "refCode", "targetAccName", "targetBVN", "targetAccNo", "reasonCode", "narration"})
public class AccountBlockRequest
{
  private String sessionID;
  private String destInstCode;
  private String channelCode;
  private String refCode;
  private String targetAccName;
  private String targetBVN;
  private String targetAccNo;
  private String reasonCode;
  private String narration;
  
  @XmlElement(name="SessionID")
  public String getSessionID()
  {
    return this.sessionID;
  }
  
  public void setSessionID(String sessionID)
  {
    this.sessionID = sessionID;
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
  public String getChannelCode()
  {
    return this.channelCode;
  }
  
  public void setChannelCode(String channelCode)
  {
    this.channelCode = channelCode;
  }
  
  @XmlElement(name="ReferenceCode")
  public String getRefCode()
  {
    return this.refCode;
  }
  
  public void setRefCode(String refCode)
  {
    this.refCode = refCode;
  }
  
  @XmlElement(name="TargetAccountName")
  public String getTargetAccName()
  {
    return this.targetAccName;
  }
  
  public void setTargetAccName(String targetAccName)
  {
    this.targetAccName = targetAccName;
  }
  
  @XmlElement(name="TargetBankVerificationNumber")
  public String getTargetBVN()
  {
    return this.targetBVN;
  }
  
  public void setTargetBVN(String targetBVN)
  {
    this.targetBVN = targetBVN;
  }
  
  @XmlElement(name="TargetAccountNumber")
  public String getTargetAccNo()
  {
    return this.targetAccNo;
  }
  
  public void setTargetAccNo(String targetAccNo)
  {
    this.targetAccNo = targetAccNo;
  }
  
  @XmlElement(name="ReasonCode")
  public String getReasonCode()
  {
    return this.reasonCode;
  }
  
  public void setReasonCode(String reasonCode)
  {
    this.reasonCode = reasonCode;
  }
  
  @XmlElement(name="Narration")
  public String getNarration()
  {
    return this.narration;
  }
  
  public void setNarration(String narration)
  {
    this.narration = narration;
  }
}
