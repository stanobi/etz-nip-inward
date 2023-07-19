package com.etz.nips.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="BalanceEnquiryResponse")
public class BalanceEnquiryResponse
{
  private String sessionID;
  private String destInstCode;
  private String channel;
  private String authorizationCode;
  private String targetAccName;
  private String targetBVN;
  private String targetAccNo;
  private String balance;
  private String respCode;
  
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
  public String getChannel()
  {
    return this.channel;
  }
  
  public void setChannel(String channel)
  {
    this.channel = channel;
  }
  
  @XmlElement(name="AuthorizationCode")
  public String getAuthorizationCode()
  {
    return this.authorizationCode;
  }
  
  public void setAuthorizationCode(String authorizationCode)
  {
    this.authorizationCode = authorizationCode;
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
  
  @XmlElement(name="AvailableBalance")
  public String getBalance()
  {
    return this.balance;
  }
  
  public void setBalance(String balance)
  {
    this.balance = balance;
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
