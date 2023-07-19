package com.etz.nips.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="FTAdviceDebitResponse")
public class FTAdviceDebitResponse
{
  private String sessionID;
  private String nameEnquiryRef;
  private String destInstCode;
  private String channelCode;
  private String debitAccName;
  private String debitAccNo;
  private String debitBVN;
  private String debitKYCLevel;
  private String beneficiaryAccName;
  private String beneficiaryAccNo;
  private String beneficiaryBVN;
  private String beneficiaryKYCLevel;
  private String transLocation;
  private String narration;
  private String paymentRef;
  private String mandateRefNo;
  private String transFee;
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
  
  @XmlElement(name="DebitAccountName")
  public String getDebitAccName()
  {
    return this.debitAccName;
  }
  
  public void setDebitAccName(String debitAccName)
  {
    this.debitAccName = debitAccName;
  }
  
  @XmlElement(name="DebitAccountNumber")
  public String getDebitAccNo()
  {
    return this.debitAccNo;
  }
  
  public void setDebitAccNo(String debitAccNo)
  {
    this.debitAccNo = debitAccNo;
  }
  
  @XmlElement(name="DebitBankVerificationNumber")
  public String getDebitBVN()
  {
    return this.debitBVN;
  }
  
  public void setDebitBVN(String debitBVN)
  {
    this.debitBVN = debitBVN;
  }
  
  @XmlElement(name="DebitKYCLevel")
  public String getDebitKYCLevel()
  {
    return this.debitKYCLevel;
  }
  
  public void setDebitKYCLevel(String debitKYCLevel)
  {
    this.debitKYCLevel = debitKYCLevel;
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
  
  @XmlElement(name="MandateReferenceNumber")
  public String getMandateRefNo()
  {
    return this.mandateRefNo;
  }
  
  public void setMandateRefNo(String mandateRefNo)
  {
    this.mandateRefNo = mandateRefNo;
  }
  
  @XmlElement(name="TransactionFee")
  public String getTransFee()
  {
    return this.transFee;
  }
  
  public void setTransFee(String transFee)
  {
    this.transFee = transFee;
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
