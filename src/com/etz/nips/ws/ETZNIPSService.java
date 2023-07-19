package com.etz.nips.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(targetNamespace="http://ws.nips.etz.com", name="ETZNIPSService")
public abstract interface ETZNIPSService
{
  @WebMethod(operationName="nameenquirysingleitem")
  public abstract String nameEnquiry(@WebParam(name="request") String paramString);
  
  @WebMethod(operationName="balanceenquiry")
  public abstract String balanceEnquiry(@WebParam(name="request") String paramString);
  
  @WebMethod(operationName="fundtransfersingleitem_dc")
  public abstract String fundTransfer(@WebParam(name="request") String paramString);
  
  @WebMethod(operationName="fundtransferAdvice_dc")
  public abstract String fundTransferAdvice(@WebParam(name="request") String paramString);
  
  @WebMethod(operationName="financialinstitutionlist")
  public abstract String financialInstList(@WebParam(name="request") String paramString);
  
  @WebMethod(operationName="mandateadvice")
  public abstract String mandateAdvice(@WebParam(name="request") String paramString);
  
  @WebMethod(operationName="fundtransfersingleitem_dd")
  public abstract String fundTransferDD(@WebParam(name="request") String paramString);
  
  @WebMethod(operationName="fundtransferAdvice_dd")
  public abstract String fundTransferAdviceDD(@WebParam(name="request") String paramString);
  
  @WebMethod(operationName="amountblock")
  public abstract String amountBlock(@WebParam(name="request") String paramString);
  
  @WebMethod(operationName="amountUnblock")
  public abstract String amountUnblock(@WebParam(name="request") String paramString);
  
  @WebMethod(operationName="accountblock")
  public abstract String accountBlock(@WebParam(name="request") String paramString);
  
  @WebMethod(operationName="accountUnblock")
  public abstract String accountUnblock(@WebParam(name="request") String paramString);
}
