package com.etz.nips.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="FTAdviceCreditResponse")
public class FTAdviceCreditResponse
{
  private String sessionID;
  private String nameEnquiryRef;
  private String destInstCode;
  private String channelCode;
  private String beneficiaryAccName;
  private String beneficiaryAccNo;
  private String beneficiaryBVN;
  private String beneficiaryKYCLevel;
  private String originatorAccName;
  private String originatorAccNo;
  private String originatorBVN;
  private String originatorKYCLevel;
  private String transLocation;
  private String narration;
  private String paymentRef;
  private String amount;
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
  
  @XmlElement(name="NameEnquiryRef")
  public String getNameEnquiryRef()
  {
    return this.nameEnquiryRef;
  }
  
  public void setNameEnquiryRef(String nameEnquiryRef)
  {
    this.nameEnquiryRef = nameEnquiryRef;
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
  
  @XmlElement(name="BeneficiaryAccountName")
  public String getBeneficiaryAccName()
  {
    return this.beneficiaryAccName;
  }
  
  public void setBeneficiaryAccName(String beneficiaryAccName)
  {
    this.beneficiaryAccName = beneficiaryAccName;
  }
  
  @XmlElement(name="BeneficiaryAccountNumber")
  public String getBeneficiaryAccNo()
  {
    return this.beneficiaryAccNo;
  }
  
  public void setBeneficiaryAccNo(String beneficiaryAccNo)
  {
    this.beneficiaryAccNo = beneficiaryAccNo;
  }
  
  @XmlElement(name="BeneficiaryBankVerificationNumber")
  public String getBeneficiaryBVN()
  {
    return this.beneficiaryBVN;
  }
  
  public void setBeneficiaryBVN(String beneficiaryBVN)
  {
    this.beneficiaryBVN = beneficiaryBVN;
  }
  
  @XmlElement(name="BeneficiaryKYCLevel")
  public String getBeneficiaryKYCLevel()
  {
    return this.beneficiaryKYCLevel;
  }
  
  public void setBeneficiaryKYCLevel(String beneficiaryKYCLevel)
  {
    this.beneficiaryKYCLevel = beneficiaryKYCLevel;
  }
  
  @XmlElement(name="OriginatorAccountName")
  public String getOriginatorAccName()
  {
    return this.originatorAccName;
  }
  
  public void setOriginatorAccName(String originatorAccName)
  {
    this.originatorAccName = originatorAccName;
  }
  
  @XmlElement(name="OriginatorAccountNumber")
  public String getOriginatorAccNo()
  {
    return this.originatorAccNo;
  }
  
  public void setOriginatorAccNo(String originatorAccNo)
  {
    this.originatorAccNo = originatorAccNo;
  }
  
  @XmlElement(name="OriginatorBankVerificationNumber")
  public String getOriginatorBVN()
  {
    return this.originatorBVN;
  }
  
  public void setOriginatorBVN(String originatorBVN)
  {
    this.originatorBVN = originatorBVN;
  }
  
  @XmlElement(name="OriginatorKYCLevel")
  public String getOriginatorKYCLevel()
  {
    return this.originatorKYCLevel;
  }
  
  public void setOriginatorKYCLevel(String originatorKYCLevel)
  {
    this.originatorKYCLevel = originatorKYCLevel;
  }
  
  @XmlElement(name="TransactionLocation")
  public String getTransLocation()
  {
    return this.transLocation;
  }
  
  public void setTransLocation(String transLocation)
  {
    this.transLocation = transLocation;
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
  
  @XmlElement(name="PaymentReference")
  public String getPaymentRef()
  {
    return this.paymentRef;
  }
  
  public void setPaymentRef(String paymentRef)
  {
    this.paymentRef = paymentRef;
  }
  
  @XmlElement(name="Amount")
  public String getAmount()
  {
    return this.amount;
  }
  
  public void setAmount(String amount)
  {
    this.amount = amount;
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
