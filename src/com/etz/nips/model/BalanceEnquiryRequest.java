package com.etz.nips.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="BalanceEnquiryRequest")
public class BalanceEnquiryRequest
{
  private String sessionId;
  private String destInstCode;
  private String channel;
  private String authorizationCode;
  private String targetAccName;
  private String targetBVN;
  private String targetAccNo;
  
  @XmlElement(name="SessionID")
  public String getSessionId()
  {
    return this.sessionId;
  }
  
  public void setSessionId(String sessionId)
  {
    this.sessionId = sessionId;
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
}
