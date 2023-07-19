package com.etz.nips.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="NESingleRequest")
@XmlType(propOrder={"sessionID", "destinationInstitutionCode", "channelCode", "accountNumber"})
public class NameEnquirySI
{
  private String SessionID;
  private String DestinationInstitutionCode;
  private String ChannelCode;
  private String AccountNumber;
  
  @XmlElement(name="SessionID")
  public String getSessionID()
  {
    return this.SessionID;
  }
  
  public void setSessionID(String SessionID)
  {
    this.SessionID = SessionID;
  }
  
  @XmlElement(name="DestinationInstitutionCode")
  public String getDestinationInstitutionCode()
  {
    return this.DestinationInstitutionCode;
  }
  
  public void setDestinationInstitutionCode(String DestinationInstitutionCode)
  {
    this.DestinationInstitutionCode = DestinationInstitutionCode;
  }
  
  @XmlElement(name="ChannelCode")
  public String getChannelCode()
  {
    return this.ChannelCode;
  }
  
  public void setChannelCode(String ChannelCode)
  {
    this.ChannelCode = ChannelCode;
  }
  
  @XmlElement(name="AccountNumber")
  public String getAccountNumber()
  {
    return this.AccountNumber;
  }
  
  public void setAccountNumber(String AccountNumber)
  {
    this.AccountNumber = AccountNumber;
  }
}
