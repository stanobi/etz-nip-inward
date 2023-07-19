package com.etz.nips.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="NESingleResponse")
public class NESingleResponse
{
  private String sessionID;
  private String destinationInstitutionCode;
  private String channelCode;
  private String accountNumber;
  private String accountName;
  private String bankVerificationNumber;
  private String KYCLevel;
  private String responseCode;
  
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
  public String getDestinationInstitutionCode()
  {
    return this.destinationInstitutionCode;
  }
  
  public void setDestinationInstitutionCode(String destinationInstitutionCode)
  {
    this.destinationInstitutionCode = destinationInstitutionCode;
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
  
  @XmlElement(name="AccountNumber")
  public String getAccountNumber()
  {
    return this.accountNumber;
  }
  
  public void setAccountNumber(String accountNumber)
  {
    this.accountNumber = accountNumber;
  }
  
  @XmlElement(name="AccountName")
  public String getAccountName()
  {
    return this.accountName;
  }
  
  public void setAccountName(String accountName)
  {
    this.accountName = accountName;
  }
  
  @XmlElement(name="BankVerificationNumber")
  public String getBankVerificationNumber()
  {
    return this.bankVerificationNumber;
  }
  
  public void setBankVerificationNumber(String bankVerificationNumber)
  {
    this.bankVerificationNumber = bankVerificationNumber;
  }
  
  @XmlElement(name="KYCLevel")
  public String getKYCLevel()
  {
    return this.KYCLevel;
  }
  
  public void setKYCLevel(String KYCLevel)
  {
    this.KYCLevel = KYCLevel;
  }
  
  @XmlElement(name="ResponseCode")
  public String getResponseCode()
  {
    return this.responseCode;
  }
  
  public void setResponseCode(String responseCode)
  {
    this.responseCode = responseCode;
  }
}
