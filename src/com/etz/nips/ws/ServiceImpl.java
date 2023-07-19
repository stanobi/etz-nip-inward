package com.etz.nips.ws;

import com.etz.nips.controller.WSController;
import com.etz.nips.events.QueueProducer;
import com.etz.nips.model.AccountBlockRequest;
import com.etz.nips.model.AccountBlockResponse;
import com.etz.nips.model.AccountUnblockRequest;
import com.etz.nips.model.AccountUnblockResponse;
import com.etz.nips.model.AmountBlockRequest;
import com.etz.nips.model.AmountBlockResponse;
import com.etz.nips.model.AmountUnblockRequest;
import com.etz.nips.model.AmountUnblockResponse;
import com.etz.nips.model.BalanceEnquiryRequest;
import com.etz.nips.model.BalanceEnquiryResponse;
import com.etz.nips.model.FTAdviceCreditRequest;
import com.etz.nips.model.FTAdviceCreditResponse;
import com.etz.nips.model.FTAdviceDebitRequest;
import com.etz.nips.model.FTAdviceDebitResponse;
import com.etz.nips.model.FTSingleCreditRequest;
import com.etz.nips.model.FTSingleCreditResponse;
import com.etz.nips.model.FTSingleDebitRequest;
import com.etz.nips.model.FTSingleDebitResponse;
import com.etz.nips.model.FinancialInstitutionListRequest;
import com.etz.nips.model.FinancialInstitutionListResponse;
import com.etz.nips.model.Header;
import com.etz.nips.model.MandateAdviceRequest;
import com.etz.nips.model.MandateAdviceResponse;
import com.etz.nips.model.NESingleResponse;
import com.etz.nips.model.NameEnquirySI;
import com.etz.nips.util.Conversion;
import com.etz.nips.util.Misc;
import com.etz.nips.util.NIBSSChannelCode;
import com.etz.nips.util.NIBSSReasonCode;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import nfp.ssm.core.SSMLib;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

@WebService(portName="ETZNIPSServicePort", serviceName="ETZNIPSServiceImpl", targetNamespace="http://ws.nips.etz.com", endpointInterface="com.etz.nips.ws.ETZNIPSService")
public class ServiceImpl
  implements ETZNIPSService
{
  @Resource
  private WebServiceContext wsContext;
  private static String NIBCARDNUMBER;
  private static String ETZCODE;
  private static String CARDPIN;
  private static String CARDEXPDATE;
  private static String KEYPASS;
  private static final Logger LOG = Logger.getLogger(ServiceImpl.class);
  private boolean loaded = false;
  private WSController wsControl = new WSController();
  private DecimalFormat df = new DecimalFormat("#.00");
  
  public ServiceImpl()
  {
    if (!this.loaded) {
      loadInfo();
    }
  }
  
  private void loadInfo()
  {
    try
    {
      Properties applicationProperties = new Properties();
      
      FileInputStream inputStream = new FileInputStream("nibss-nip.properties");
      
      applicationProperties.load(inputStream);
      NIBCARDNUMBER = applicationProperties.getProperty("LIVE_nipCardNo");
      ETZCODE = applicationProperties.getProperty("LIVE_eTzCode");
      CARDPIN = applicationProperties.getProperty("CARD_PIN");
      CARDEXPDATE = applicationProperties.getProperty("CARD_EXPIRY_DATE");
      KEYPASS = applicationProperties.getProperty("LIVE_eTzKEYPASS");
      LOG.info(" fundgate nibCardNumber >>>> " + NIBCARDNUMBER);
      LOG.info(" eTz NIBSS Code >>>> " + ETZCODE);
      this.loaded = true;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      this.loaded = false;
    }
  }
  
  private String make(String op, String req)
  {
    SSMLib ssmLib = NIPSCred.getSSMlibObj();
    String retreq = "";
    if (op.equalsIgnoreCase("encrypt")) {
      retreq = ssmLib.encryptMessage(req);
    } else if (op.equalsIgnoreCase("decrypt")) {
      retreq = ssmLib.decryptFile(req, KEYPASS);
    }
    return retreq;
  }
  
  private String getIPAddress()
  {
    MessageContext mc = this.wsContext.getMessageContext();
    HttpServletRequest httpRequest = (HttpServletRequest)mc.get("javax.xml.ws.servlet.request");
    String ip = httpRequest.getRemoteAddr();
    if ((ip == null) || ((ip != null) && ((ip.trim().equals("172.16.10.5")) || (ip.trim().equals("127.0.0.01")) || (ip.trim().equals("localhost"))))) {
      ip = getClientIPAddress(httpRequest);
    }
    return ip;
  }
  
  private String getClientIPAddress(HttpServletRequest httpRequest)
  {
    String clientIP = httpRequest.getHeader("X-FORWARDED-FOR");
    if ((clientIP == null) || (clientIP.length() == 0) || ("unknown".equalsIgnoreCase(clientIP))) {
      clientIP = httpRequest.getHeader("Proxy-Client-IP");
    }
    if ((clientIP == null) || (clientIP.length() == 0) || ("unknown".equalsIgnoreCase(clientIP))) {
      clientIP = httpRequest.getHeader("WL-Proxy-Client-IP");
    }
    if ((clientIP == null) || (clientIP.length() == 0) || ("unknown".equalsIgnoreCase(clientIP))) {
      clientIP = httpRequest.getHeader("HTTP_CLIENT_IP");
    }
    if ((clientIP == null) || (clientIP.length() == 0) || ("unknown".equalsIgnoreCase(clientIP))) {
      clientIP = httpRequest.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if ((clientIP == null) || (clientIP.length() == 0) || ("unknown".equalsIgnoreCase(clientIP))) {
      clientIP = httpRequest.getRemoteAddr();
    }
    return clientIP;
  }
  
  public String nameEnquiry(String request)
  {
    LOG.info("NE EncryptedRequest: " + request);
    String decryptReq = make("decrypt", request);
    LOG.info("NE DecryptedRequest: " + decryptReq);
    String xmlResponse = null;
    String responseCode = "";String accName = "";String kycLevel = "";
    NameEnquirySI ne = (NameEnquirySI)new Conversion().jaxbXML2Obj(decryptReq, NameEnquirySI.class);
    NESingleResponse nesr = new NESingleResponse();
    if (ne != null)
    {
      if (!NIBSSChannelCode.getNIBSSChannelCode(ne.getChannelCode()).equalsIgnoreCase("Unknown Code"))
      {
        if ((ne.getAccountNumber() == null) || (ne.getAccountNumber().equals("")))
        {
          responseCode = "07";
          LOG.error("Account number: " + ne.getAccountNumber() + " is invalid or does not exist.");
        }
        else
        {
          String response = this.wsControl.getCardDetails(ne.getAccountNumber());
          JsonObject ob = (JsonObject)new JsonParser().parse(response);
          String rspCode = ob.get("Response").getAsString();
          if (rspCode.equalsIgnoreCase("SUCCESS"))
          {
            accName = ob.get("ResponseDetails").getAsString().split("~")[0];
            kycLevel = "2";
            responseCode = "00";
          }
          else if (rspCode.equalsIgnoreCase("ERROR"))
          {
            responseCode = "25";
          }
          else if (rspCode.equalsIgnoreCase("SEVERE_ERROR"))
          {
            responseCode = "91";
          }
        }
      }
      else
      {
        responseCode = "17";
        LOG.error("Channel Code: " + ne.getChannelCode() + " is not a valid NIBSS Channel Code.");
      }
    }
    else
    {
      responseCode = "96";
      LOG.error("Could not read request!");
    }
    nesr.setAccountName(accName);
    nesr.setBankVerificationNumber(ne.getAccountNumber() == null ? "" : ne.getAccountNumber());
    nesr.setResponseCode(responseCode);
    nesr.setKYCLevel(kycLevel);
    nesr.setAccountNumber(ne.getAccountNumber() == null ? "" : ne.getAccountNumber());
    nesr.setChannelCode(ne.getChannelCode() == null ? "" : ne.getChannelCode());
    nesr.setDestinationInstitutionCode(ne.getDestinationInstitutionCode() == null ? "" : ne.getDestinationInstitutionCode());
    nesr.setSessionID(ne.getSessionID() == null ? "" : ne.getSessionID());
    if ((ne.getSessionID() != null) || (!ne.getSessionID().equals(""))) {
      this.wsControl.saveRequest(ne.getSessionID(), ne.getSessionID().substring(0, 6), "Name Enquiry", new Date(), nesr.getChannelCode(), nesr.getDestinationInstitutionCode(), responseCode, getIPAddress());
    }
    xmlResponse = new Conversion().jaxbObj2XML(nesr, NESingleResponse.class);
    String encRes = make("encrypt", xmlResponse);
    return encRes;
  }
  
  public String balanceEnquiry(String request)
  {
    LOG.info("BE EncryptedRequest: " + request);
    String responseCode = "";String balance = "";
    String decryptReq = make("decrypt", request);
    LOG.info("BE DecryptedRequest: " + decryptReq);
    BalanceEnquiryRequest ber = (BalanceEnquiryRequest)new Conversion().jaxbXML2Obj(decryptReq, BalanceEnquiryRequest.class);
    BalanceEnquiryResponse beres = new BalanceEnquiryResponse();
    if (ber != null)
    {
      if (!NIBSSChannelCode.getNIBSSChannelCode(ber.getChannel()).equalsIgnoreCase("Unknown Code"))
      {
        if ((ber.getTargetAccNo() == null) || (ber.getTargetAccNo().equals("")))
        {
          responseCode = "07";
          LOG.error("Account: " + ber.getTargetAccNo() + " is invalid or does not exist.");
        }
        else
        {
          String mandateAmount = this.wsControl.mandateExist(ber.getAuthorizationCode(), ber.getTargetAccNo());
          if (mandateAmount != null)
          {
            if (mandateAmount.equalsIgnoreCase("NOT FOUND"))
            {
              responseCode = "05";
              LOG.error("No mandate for operation!");
            }
            else
            {
              String req = this.wsControl.getCardDetails(ber.getTargetAccNo());
              JsonObject cardObj = (JsonObject)new JsonParser().parse(req);
              String rspCode = cardObj.get("Response").getAsString();
              if (rspCode.equalsIgnoreCase("SUCCESS"))
              {
                balance = cardObj.get("ResponseDetails").getAsString().split("~")[2];
                responseCode = "00";
              }
              else if (rspCode.equalsIgnoreCase("ERROR"))
              {
                responseCode = "25";
              }
              else if (rspCode.equalsIgnoreCase("SEVERE_ERROR"))
              {
                responseCode = "91";
              }
            }
          }
          else
          {
            responseCode = "96";
            LOG.error("Something went wrong with mandateAmount from BE!");
          }
        }
      }
      else
      {
        responseCode = "17";
        LOG.error("Channel Code: " + ber.getChannel() + " is not a valid NIBSS Channel Code.");
      }
    }
    else
    {
      responseCode = "96";
      LOG.error("Could not read request!");
    }
    beres.setSessionID(ber.getSessionId() == null ? "" : ber.getSessionId());
    beres.setDestInstCode(ber.getDestInstCode() == null ? "" : ber.getDestInstCode());
    beres.setChannel(ber.getChannel() == null ? "" : ber.getChannel());
    beres.setAuthorizationCode(ber.getAuthorizationCode() == null ? "" : ber.getAuthorizationCode());
    beres.setTargetAccName(ber.getTargetAccName() == null ? "" : ber.getTargetAccName());
    beres.setTargetBVN(ber.getTargetBVN() == null ? "" : ber.getTargetBVN());
    beres.setTargetAccNo(ber.getTargetAccNo() == null ? "" : ber.getTargetAccNo());
    beres.setBalance(balance);
    beres.setRespCode(responseCode);
    if ((ber.getSessionId() != null) || (!ber.getSessionId().equals(""))) {
      this.wsControl.saveRequest(ber.getSessionId(), ber.getSessionId().substring(0, 6), "Balance Enquiry", new Date(), beres.getChannel(), beres.getDestInstCode(), responseCode, getIPAddress());
    }
    String xmlResponse = new Conversion().jaxbObj2XML(beres, BalanceEnquiryResponse.class);
    String encRes = make("encrypt", xmlResponse);
    return encRes;
  }
  
  public String fundTransfer(String request)
  {
    LOG.info("FT-DirectCredit EncryptedRequest: " + request);
    String decryptReq = make("decrypt", request);
    LOG.info("FT-DirectCredit DecryptedRequest: " + decryptReq);
    String ref = new Misc().getPaymentRef("FT");
    String responseCode = "";String eTzRespCode = "";
    FTSingleCreditRequest ftscr = (FTSingleCreditRequest)new Conversion().jaxbXML2Obj(decryptReq, FTSingleCreditRequest.class);
    FTSingleCreditResponse ftscres = new FTSingleCreditResponse();
    if (ftscr != null)
    {
      if (!NIBSSChannelCode.getNIBSSChannelCode(ftscr.getChannelCode()).equalsIgnoreCase("Unknown Code"))
      {
        if ((ftscr.getBeneficiaryAccNo() != null) || (!ftscr.getBeneficiaryAccNo().equals("")))
        {
          try
          {
            double actualAmount = Double.parseDouble(ftscr.getAmount());
            if (actualAmount <= 0.0D)
            {
              responseCode = "05";
              eTzRespCode = "-1";
              LOG.error("Amount cannot be 0 or less than 0; Amount entered: " + ftscr.getAmount());
            }
            else if ((ftscr.getAmount().contains(".")) && (ftscr.getAmount().substring(ftscr.getAmount().indexOf(".") + 1).length() > 2))
            {
              responseCode = "05";
              eTzRespCode = "-1";
              LOG.error("Amount failed NIBBS format...not 2dp; Amount entered: " + ftscr.getAmount());
            }
            else
            {
              String response4NE = "00";
              if ((ftscr.getNameEnquiryRef() != null) && (!ftscr.getNameEnquiryRef().equals("")) && (ftscr.getNameEnquiryRef().equalsIgnoreCase("null"))) {
                response4NE = this.wsControl.getResponseCode("Name Enquiry", ftscr.getNameEnquiryRef());
              }
              if (response4NE != null)
              {
                if (response4NE.equals("00"))
                {
//                  String req = this.wsControl.getCardDetails(ftscr.getBeneficiaryAccNo());
//                  JsonObject cardObj = (JsonObject)new JsonParser().parse(req);
//                  String cardNum = cardObj.get("ResponseDetails").getAsString().split("~")[1];
                  responseCode = "00";
                }
                else
                {
                  LOG.info("Prior NE was not successful, ignore FT.");
                  responseCode = "12";
                }
              }
              else
              {
                responseCode = "12";
                LOG.info("Could not fetch Response Code for prior NE or");
                LOG.error("NameEnquiryRef: " + ftscr.getNameEnquiryRef() + " doesn't exist. Please do a NE first before an FT");
              }
            }
          }
          catch (Exception e)
          {
            responseCode = "13";
            LOG.error("Invalid amount:: Amount entered: " + ftscr.getAmount());
          }
        }
        else
        {
          responseCode = "07";
          LOG.error("Account: " + ftscr.getBeneficiaryAccNo() + " is invalid or does not exist.");
        }
      }
      else
      {
        responseCode = "17";
        LOG.error("Channel Code: " + ftscr.getChannelCode() + " is not a valid NIBSS Channel Code.");
      }
    }
    else
    {
      responseCode = "96";
      LOG.error("Could not read request!");
    }
    ftscres.setSessionID(ftscr.getSessionID() == null ? "" : ftscr.getSessionID());
    ftscres.setNameEnquiryRef(ftscr.getNameEnquiryRef() == null ? "" : ftscr.getNameEnquiryRef());
    ftscres.setDestInstCode(ftscr.getDestInstCode() == null ? "" : ftscr.getDestInstCode());
    ftscres.setChannelCode(ftscr.getChannelCode() == null ? "" : ftscr.getChannelCode());
    ftscres.setBeneficiaryAccName(ftscr.getBeneficiaryAccName() == null ? "" : ftscr.getBeneficiaryAccName());
    ftscres.setBeneficiaryAccNo(ftscr.getBeneficiaryAccNo() == null ? "" : ftscr.getBeneficiaryAccNo());
    ftscres.setBeneficiaryKYCLevel(ftscr.getBeneficiaryKYCLevel() == null ? "" : ftscr.getBeneficiaryKYCLevel());
    ftscres.setBeneficiaryBankVerificationNo(ftscr.getBeneficiaryBankVerificationNo() == null ? "" : ftscr.getBeneficiaryBankVerificationNo());
    ftscres.setOriginatorAccName(ftscr.getOriginatorAccName() == null ? "" : ftscr.getOriginatorAccName());
    ftscres.setOriginatorAccNo(ftscr.getOriginatorAccNo() == null ? "" : ftscr.getOriginatorAccNo());
    ftscres.setOriginatorBankVerificationNo(ftscr.getOriginatorBankVerificationNo() == null ? "" : ftscr.getOriginatorBankVerificationNo());
    ftscres.setOriginatorKYCLevel(ftscr.getOriginatorKYCLevel() == null ? "" : ftscr.getOriginatorKYCLevel());
    ftscres.setTransactionLocation(ftscr.getTransactionLocation() == null ? "" : ftscr.getTransactionLocation());
    ftscres.setNarration(ftscr.getNarration() == null ? "" : StringEscapeUtils.unescapeHtml3(ftscr.getNarration()));
    ftscres.setPaymentRef(ftscr.getPaymentRef() == null ? "" : ftscr.getPaymentRef());
    ftscres.setAmount(ftscr.getAmount() == null ? "" : ftscr.getAmount());
    ftscres.setRespCode(responseCode);
    if ((ftscr.getSessionID() != null) || (!ftscr.getSessionID().equals("")))
    {
      this.wsControl.saveRequest(ftscr.getSessionID(), ftscr.getSessionID().substring(0, 6), "Fund Transfer - DC", new Date(), ftscres.getChannelCode(), ftscres.getDestInstCode(), responseCode, getIPAddress());
      
      this.wsControl.insertTnx(ftscres.getSessionID(), ref, ftscres.getChannelCode(), ftscres.getAmount(), ftscres.getSessionID().substring(0, 6), ftscres.getDestInstCode(), responseCode, eTzRespCode, new Date(), ftscres.getPaymentRef(), ftscres.getBeneficiaryAccNo(), ftscres.getOriginatorAccNo(), "", "FT-DIRECT CREDIT", StringEscapeUtils.unescapeHtml3(ftscres.getNarration()), getIPAddress());
      
      pushToQueue(ftscres.getSessionID(), responseCode);
    }
    String xmlResponse = new Conversion().jaxbObj2XML(ftscres, FTSingleCreditResponse.class);
    String encRes = make("encrypt", xmlResponse);
    return encRes;
  }
  
  private void pushToQueue(String sessionId, String responseCode) {
      
      if(!"00".equals(responseCode)) {
          return;
      }
      
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                new QueueProducer().sendSession(sessionId);
            }
        },60, TimeUnit.SECONDS);
      
  }
  
  public String fundTransferAdvice(String request)
  {
    LOG.info("FTAdvice EncryptedRequest: " + request);
    String decryptReq = make("decrypt", request);
    LOG.info("FTAdvice DecryptedRequest: " + decryptReq);
    String ref = "";
    String responseCode = "";
    FTAdviceCreditRequest ftacr = (FTAdviceCreditRequest)new Conversion().jaxbXML2Obj(decryptReq, FTAdviceCreditRequest.class);
    FTAdviceCreditResponse ftacres = new FTAdviceCreditResponse();
    if (ftacr != null)
    {
      if (!NIBSSChannelCode.getNIBSSChannelCode(ftacr.getChannelCode()).equalsIgnoreCase("Unknown Code"))
      {
        if ((ftacr.getBeneficiaryAccNo() != null) || (!ftacr.getBeneficiaryAccNo().equals("")))
        {
          try
          {
            double actualAmount = Double.parseDouble(ftacr.getAmount());
            if (actualAmount <= 0.0D)
            {
              responseCode = "05";
              LOG.error("Amount cannot be 0 or less than 0; Amount entered: " + ftacr.getAmount());
            }
            else if ((ftacr.getAmount().contains(".")) && (ftacr.getAmount().substring(ftacr.getAmount().indexOf(".") + 1).length() > 2))
            {
              responseCode = "05";
              LOG.error("Amount failed NIBBS format...not 2dp; Amount entered: " + ftacr.getAmount());
            }
            else
            {
              String response4FT = this.wsControl.getResponseCode("Fund Transfer - DC", ftacr.getSessionID());
              if (response4FT != null)
              {
                if (response4FT.equalsIgnoreCase("00"))
                {
                  String req = this.wsControl.getCardDetails(ftacr.getBeneficiaryAccNo());
                  JsonObject cardObj = (JsonObject)new JsonParser().parse(req);
                  String cardNum = cardObj.get("ResponseDetails").getAsString().split("~")[1];
                  ref = this.wsControl.getUniqueRef(ftacr.getSessionID());
                  if (ref != null)
                  {
                    String response = this.wsControl.autoclientTnx("FundTransferAdvice", NIBCARDNUMBER, cardNum, CARDPIN, CARDEXPDATE, ref, ftacr.getNarration(), 0.0D, actualAmount);
                    
                    JsonObject fTAObj = (JsonObject)new JsonParser().parse(response);
                    String rspCode = fTAObj.get("Response").getAsString();
                    if (rspCode.equalsIgnoreCase("SUCCESS")) {
                      responseCode = "00";
                    } else if (rspCode.equalsIgnoreCase("ERROR")) {
                      responseCode = "96";
                    } else if (rspCode.equalsIgnoreCase("SEVERE_ERROR")) {
                      responseCode = "96";
                    }
                  }
                  else
                  {
                    responseCode = "96";
                    LOG.error("Error getting ref no.");
                  }
                }
                else
                {
                  LOG.info("Prior FT was not successful, ignore reversal.");
                  responseCode = "12";
                }
              }
              else
              {
                responseCode = "12";
                LOG.info("Could not fetch Response Code for prior FT");
              }
            }
          }
          catch (Exception e)
          {
            responseCode = "13";
            LOG.error("Invalid amount:: Amount entered: " + ftacr.getAmount());
          }
        }
        else
        {
          responseCode = "07";
          LOG.error("Account: " + ftacr.getBeneficiaryAccNo() + " is invalid or does not exist.");
        }
      }
      else
      {
        responseCode = "17";
        LOG.error("Channel Code: " + ftacr.getChannelCode() + " is not a valid NIBSS Channel Code.");
      }
    }
    else
    {
      responseCode = "96";
      LOG.error("Could not read request!");
    }
    ftacres.setSessionID(ftacr.getSessionID() == null ? "" : ftacr.getSessionID());
    ftacres.setNameEnquiryRef(ftacr.getNameEnquiryRef() == null ? "" : ftacr.getNameEnquiryRef());
    ftacres.setDestInstCode(ftacr.getDestInstCode() == null ? "" : ftacr.getDestInstCode());
    ftacres.setChannelCode(ftacr.getChannelCode() == null ? "" : ftacr.getChannelCode());
    ftacres.setBeneficiaryAccName(ftacr.getBeneficiaryAccName() == null ? "" : ftacr.getBeneficiaryAccName());
    ftacres.setBeneficiaryAccNo(ftacr.getBeneficiaryAccNo() == null ? "" : ftacr.getBeneficiaryAccNo());
    ftacres.setBeneficiaryBVN(ftacr.getBeneficiaryBVN() == null ? "" : ftacr.getBeneficiaryBVN());
    ftacres.setBeneficiaryKYCLevel(ftacr.getBeneficiaryKYCLevel() == null ? "" : ftacr.getBeneficiaryKYCLevel());
    ftacres.setOriginatorAccName(ftacr.getOriginatorAccName() == null ? "" : ftacr.getOriginatorAccName());
    ftacres.setOriginatorAccNo(ftacr.getOriginatorAccNo() == null ? "" : ftacr.getOriginatorAccNo());
    ftacres.setOriginatorBVN(ftacr.getOriginatorBVN() == null ? "" : ftacr.getOriginatorBVN());
    ftacres.setOriginatorKYCLevel(ftacr.getOriginatorKYCLevel() == null ? "" : ftacr.getOriginatorKYCLevel());
    ftacres.setTransLocation(ftacr.getTransLocation() == null ? "" : ftacr.getTransLocation());
    ftacres.setNarration(ftacr.getNarration() == null ? "" : StringEscapeUtils.unescapeHtml3(ftacr.getNarration()));
    ftacres.setPaymentRef(ftacr.getPaymentRef() == null ? "" : ftacr.getPaymentRef());
    ftacres.setAmount(ftacr.getAmount() == null ? "" : ftacr.getAmount());
    ftacres.setRespCode(responseCode);
    if ((ftacr.getSessionID() != null) || (!ftacr.getSessionID().equals("")))
    {
      this.wsControl.saveRequest(ftacr.getSessionID(), ftacr.getSessionID().substring(0, 6), "Fund Transfer Advice - DC", new Date(), ftacres.getChannelCode(), ftacres.getDestInstCode(), responseCode, getIPAddress());
      if (responseCode.equals("00")) {
        this.wsControl.updateTnx(ftacres.getSessionID(), ftacres.getRespCode(), "R");
      }
    }
    String xmlResponse = new Conversion().jaxbObj2XML(ftacres, FTAdviceCreditResponse.class);
    String encRes = make("encrypt", xmlResponse);
    return encRes;
  }
  
  public String financialInstList(String request)
  {
    LOG.info("Financial List EncryptedRequest: " + request);
    String decryptReq = make("decrypt", request);
    LOG.info("Financial List DecryptedRequest: " + decryptReq);
    String responseCode = "";
    FinancialInstitutionListRequest filr = (FinancialInstitutionListRequest)new Conversion().jaxbXML2Obj(decryptReq, FinancialInstitutionListRequest.class);
    FinancialInstitutionListResponse filres = new FinancialInstitutionListResponse();
    if (filr != null)
    {
      if (Integer.valueOf(filr.getHeader().getRecNo()).intValue() > 50)
      {
        responseCode = "30";
        LOG.error("Number of record exceeds 50!");
      }
      else if (!NIBSSChannelCode.getNIBSSChannelCode(filr.getHeader().getChannelCode()).equalsIgnoreCase("Unknown Code"))
      {
        if (this.wsControl.insertInstitution(filr))
        {
          responseCode = "00";
        }
        else
        {
          responseCode = "96";
          LOG.error("Not all records were inserted, please try again");
        }
      }
      else
      {
        responseCode = "17";
        LOG.error("Channel Code: " + filr.getHeader().getChannelCode() + " is not a valid NIBSS Channel Code.");
      }
    }
    else
    {
      responseCode = "96";
      LOG.error("Could not read request!");
    }
    filres.setBatchNo(filr.getHeader().getBatchNo() == null ? "" : filr.getHeader().getBatchNo());
    filres.setDestInstCode(ETZCODE);
    filres.setChannel(filr.getHeader().getChannelCode() == null ? "" : filr.getHeader().getChannelCode());
    filres.setRecNo(filr.getHeader().getRecNo() == null ? "" : filr.getHeader().getRecNo());
    filres.setRespCode(responseCode);
    
    String xmlResponse = new Conversion().jaxbObj2XML(filres, FinancialInstitutionListResponse.class);
    String encRes = make("encrypt", xmlResponse);
    return encRes;
  }
  
  public String mandateAdvice(String request)
  {
    LOG.info("Mandate Advice EncryptedRequest: " + request);
    String decryptReq = make("decrypt", request);
    LOG.info("Mandate Advice DecryptedRequest: " + decryptReq);
    String responseCode = "";
    MandateAdviceRequest mdr = (MandateAdviceRequest)new Conversion().jaxbXML2Obj(decryptReq, MandateAdviceRequest.class);
    MandateAdviceResponse mdres = new MandateAdviceResponse();
    if (mdr != null)
    {
      if (!NIBSSChannelCode.getNIBSSChannelCode(mdr.getChannelCode()).equalsIgnoreCase("Unknown Code"))
      {
        if ((mdr.getDebitAccNo() != null) || (!mdr.getDebitAccNo().equals("")))
        {
          try
          {
            double mandateAmount = Double.parseDouble(mdr.getAmount());
            if (mandateAmount <= 0.0D)
            {
              responseCode = "05";
              LOG.error("Amount cannot be 0 or less than 0; Amount entered: " + mdr.getAmount());
            }
            else if ((mdr.getAmount().contains(".")) && (mdr.getAmount().substring(mdr.getAmount().indexOf(".") + 1).length() > 2))
            {
              responseCode = "05";
              LOG.error("Amount failed NIBBS format...not 2dp; Amount entered: " + mdr.getAmount());
            }
            else
            {
              String response = this.wsControl.verifyMandateAdvice(mdr);
              JsonObject ob = (JsonObject)new JsonParser().parse(response);
              if (ob.get("Response").getAsString().equalsIgnoreCase("NOT FOUND"))
              {
                responseCode = "00";
                LOG.info("Mandate advice request successful!");
              }
              else if (ob.get("Response").getAsString().trim().equalsIgnoreCase("ERROR"))
              {
                responseCode = "96";
                LOG.error("An error occurred!");
              }
              else
              {
                responseCode = "26";
                LOG.error("Mandate already exist");
                LOG.info("Given amount: " + mdr.getAmount() + " | From record, amount: " + ob.get("Response").getAsString().trim());
              }
            }
          }
          catch (Exception e)
          {
            responseCode = "13";
            LOG.error("Invalid amount:: Amount entered: " + mdr.getAmount());
          }
        }
        else
        {
          responseCode = "07";
          LOG.error("Account: " + mdr.getDebitAccNo() + " is invalid or does not exist.");
        }
      }
      else
      {
        responseCode = "17";
        LOG.error("Channel Code: " + mdr.getChannelCode() + " is not a valid NIBSS Channel Code.");
      }
    }
    else
    {
      responseCode = "96";
      LOG.error("Could not read request!");
    }
    mdres.setSessionId(mdr.getSessionId() == null ? "" : mdr.getSessionId());
    mdres.setDestInstCode(mdr.getDestInstCode() == null ? "" : mdr.getDestInstCode());
    mdres.setChannelCode(mdr.getChannelCode() == null ? "" : mdr.getChannelCode());
    mdres.setMandateRefNo(mdr.getMandateRefNo() == null ? "" : mdr.getMandateRefNo());
    mdres.setAmount(mdr.getAmount() == null ? "" : mdr.getAmount());
    mdres.setDebitAccName(mdr.getDebitAccName() == null ? "" : mdr.getDebitAccName());
    mdres.setDebitAccNo(mdr.getDebitAccNo() == null ? "" : mdr.getDebitAccNo());
    mdres.setDebitBVN(mdr.getDebitBVN() == null ? "" : mdr.getDebitBVN());
    mdres.setDebitKYCLevel(mdr.getDebitKYCLevel() == null ? "" : mdr.getDebitKYCLevel());
    mdres.setBeneficiaryAccName(mdr.getBeneficiaryAccName() == null ? "" : mdr.getBeneficiaryAccName());
    mdres.setBeneficiaryAccNo(mdr.getBeneficiaryAccNo() == null ? "" : mdr.getBeneficiaryAccNo());
    mdres.setBeneficiaryBVN(mdr.getBeneficiaryBVN() == null ? "" : mdr.getBeneficiaryBVN());
    mdres.setBeneficiaryKYCLevel(mdr.getBeneficiaryKYCLevel() == null ? "" : mdr.getBeneficiaryKYCLevel());
    mdres.setRespCode(responseCode);
    if ((mdr.getSessionId() != null) || (!mdr.getSessionId().equals("")))
    {
      this.wsControl.saveRequest(mdr.getSessionId(), mdr.getSessionId().substring(0, 6), "Mandate Advice Request", new Date(), mdr.getChannelCode(), mdr.getDestInstCode(), responseCode, getIPAddress());
      if (responseCode.equals("00")) {
        this.wsControl.saveMandate(mdr);
      }
    }
    String xmlResponse = new Conversion().jaxbObj2XML(mdres, MandateAdviceResponse.class);
    String encRes = make("encrypt", xmlResponse);
    return encRes;
  }
  
  public String fundTransferDD(String request)
  {
    LOG.info("FT-DirectDebit EncryptedRequest: " + request);
    String decryptReq = make("decrypt", request);
    LOG.info("FT-DirectDebit DecryptedRequest: " + decryptReq);
    String ref = new Misc().getPaymentRef("FTDD");
    String responseCode = "";String eTzRespCode = "";
    FTSingleDebitRequest ftsdr = (FTSingleDebitRequest)new Conversion().jaxbXML2Obj(decryptReq, FTSingleDebitRequest.class);
    FTSingleDebitResponse ftsdres = new FTSingleDebitResponse();
    if (ftsdr != null)
    {
      if (!NIBSSChannelCode.getNIBSSChannelCode(ftsdr.getChannelCode()).equalsIgnoreCase("Unknown Code"))
      {
        if ((ftsdr.getDebitAccNo() != null) || (!ftsdr.getDebitAccNo().equals("")))
        {
          if ((ftsdr.getMandateRefNo() != null) || (!ftsdr.getMandateRefNo().equals("")))
          {
            try
            {
              double actualAmount = Double.parseDouble(ftsdr.getAmount());
              double tranxFee = Double.parseDouble(ftsdr.getTranxFee());
              if (actualAmount <= 0.0D)
              {
                responseCode = "05";
                LOG.error("Amount/Transaction Fee invalid; Amount entered: " + ftsdr.getAmount() + " | Transaction Fee: " + ftsdr.getTranxFee());
              }
              else if (((ftsdr.getAmount().contains(".")) && (ftsdr.getAmount().substring(ftsdr.getAmount().indexOf(".") + 1).length() > 2)) || ((ftsdr.getTranxFee().contains(".")) && (ftsdr.getTranxFee().substring(ftsdr.getTranxFee().indexOf(".") + 1).length() > 2)))
              {
                responseCode = "05";
                LOG.error("Amount/fee failed NIBBS format...not 2dp; Amount entered: " + ftsdr.getAmount() + " | Tranx Fee: " + ftsdr.getTranxFee());
              }
              else
              {
                String mandateAmount = this.wsControl.mandateExist(ftsdr.getMandateRefNo(), ftsdr.getDebitAccNo());
                if (mandateAmount != null)
                {
                  if (mandateAmount.equalsIgnoreCase("NOT FOUND"))
                  {
                    responseCode = "05";
                    LOG.info("No mandate found!");
                  }
                  else
                  {
                    mandateAmount = this.df.format(Double.parseDouble(mandateAmount));
                    if (mandateAmount.equalsIgnoreCase(ftsdr.getAmount()))
                    {
                      responseCode = "05";
                      LOG.info("FundTransfer-DD not supported on eTz platform!");
                    }
                    else
                    {
                      responseCode = "05";
                      LOG.error("Invalid Debit amount: Mandate amount (" + mandateAmount + ") != debit amount " + "(" + ftsdr.getAmount() + ")");
                    }
                  }
                }
                else
                {
                  responseCode = "96";
                  LOG.error("Something went wrong with mandateAmount from FT-DirectDebit!");
                }
              }
            }
            catch (Exception e)
            {
              responseCode = "13";
              LOG.error("Invalid amount/transaction fee:: Amount: " + ftsdr.getAmount() + "; Transaction fee: " + ftsdr.getTranxFee());
            }
          }
          else
          {
            responseCode = "71";
            LOG.error("Empty Mandate Reference Number");
          }
        }
        else
        {
          responseCode = "07";
          LOG.error("Account: " + ftsdr.getDebitAccNo() + " is invalid or does not exist.");
        }
      }
      else
      {
        responseCode = "17";
        LOG.error("Channel Code: " + ftsdr.getChannelCode() + " is not a valid NIBSS Channel Code.");
      }
    }
    else
    {
      responseCode = "96";
      LOG.error("Could not read request!");
    }
    ftsdres.setSessionID(ftsdr.getSessionID() == null ? "" : ftsdr.getSessionID());
    ftsdres.setNameEnquiryRef(ftsdr.getNameEnquiryRef() == null ? "" : ftsdr.getNameEnquiryRef());
    ftsdres.setDestInstCode(ftsdr.getDestInstCode() == null ? "" : ftsdr.getDestInstCode());
    ftsdres.setChannelCode(ftsdr.getChannelCode() == null ? "" : ftsdr.getChannelCode());
    ftsdres.setDebitAccName(ftsdr.getDebitAccName() == null ? "" : ftsdr.getDebitAccName());
    ftsdres.setDebitAccNo(ftsdr.getDebitAccNo() == null ? "" : ftsdr.getDebitAccNo());
    ftsdres.setDebitBVN(ftsdr.getDebitBVN() == null ? "" : ftsdr.getDebitBVN());
    ftsdres.setDebitKYC(ftsdr.getDebitKYC() == null ? "" : ftsdr.getDebitKYC());
    ftsdres.setBeneficiaryAccName(ftsdr.getBeneficiaryAccName() == null ? "" : ftsdr.getBeneficiaryAccName());
    ftsdres.setBeneficiaryAccNo(ftsdr.getDebitAccNo() == null ? "" : ftsdr.getDebitAccNo());
    ftsdres.setBeneficiaryBVN(ftsdr.getBeneficiaryBVN() == null ? "" : ftsdr.getBeneficiaryBVN());
    ftsdres.setBeneficiaryKYC(ftsdr.getBeneficiaryKYC() == null ? "" : ftsdr.getBeneficiaryKYC());
    ftsdres.setTranxLocation(ftsdr.getTranxLocation() == null ? "" : ftsdr.getTranxLocation());
    ftsdres.setNarration(ftsdr.getNarration() == null ? "" : StringEscapeUtils.unescapeHtml3(ftsdr.getNarration()));
    ftsdres.setPayRef(ftsdr.getPayRef() == null ? "" : ftsdr.getPayRef());
    ftsdres.setMandateRefNo(ftsdr.getMandateRefNo() == null ? "" : ftsdr.getMandateRefNo());
    ftsdres.setTranxFee(ftsdr.getTranxFee() == null ? "" : ftsdr.getTranxFee());
    ftsdres.setAmount(ftsdr.getAmount() == null ? "" : ftsdr.getAmount());
    ftsdres.setRespCode(responseCode);
    








    String xmlResponse = new Conversion().jaxbObj2XML(ftsdres, FTSingleDebitResponse.class);
    String encRes = make("encrypt", xmlResponse);
    return encRes;
  }
  
  public String fundTransferAdviceDD(String request)
  {
    LOG.info("FTAdvice-DD EncryptedRequest: " + request);
    String decryptReq = make("decrypt", request);
    LOG.info("FTAdvice-DD DecryptedRequest: " + decryptReq);
    String responseCode = "";
    FTAdviceDebitRequest ftadr = (FTAdviceDebitRequest)new Conversion().jaxbXML2Obj(decryptReq, FTAdviceDebitRequest.class);
    FTAdviceDebitResponse ftadres = new FTAdviceDebitResponse();
    if (ftadr != null)
    {
      if (!NIBSSChannelCode.getNIBSSChannelCode(ftadr.getChannelCode()).equalsIgnoreCase("Unknown Code"))
      {
        if ((ftadr.getDebitAccNo() != null) || (!ftadr.getDebitAccNo().equals("")))
        {
          if ((ftadr.getMandateRefNo() != null) || (!ftadr.getMandateRefNo().equals("")))
          {
            try
            {
              double actualAmount = Double.parseDouble(ftadr.getAmount());
              double tranxFee = Double.parseDouble(ftadr.getTransFee());
              if (actualAmount <= 0.0D)
              {
                responseCode = "05";
                LOG.error("Amount cannot be 0 or less than 0; Amount entered: " + ftadr.getAmount());
              }
              else
              {
                String response4FTDD = this.wsControl.getResponseCode("Fund Transfer - DD", ftadr.getSessionID());
                if (response4FTDD != null)
                {
                  String mandateAmount = this.wsControl.mandateExist(ftadr.getMandateRefNo(), ftadr.getDebitAccNo());
                  if (mandateAmount != null)
                  {
                    if (mandateAmount.equalsIgnoreCase("NOT FOUND"))
                    {
                      responseCode = "05";
                      LOG.info("No mandate found!");
                    }
                    else
                    {
                      mandateAmount = this.df.format(Double.parseDouble(mandateAmount));
                      if (mandateAmount.equalsIgnoreCase(ftadr.getAmount()))
                      {
                        if (response4FTDD.equalsIgnoreCase("00"))
                        {
                          responseCode = "05";
                          LOG.info("FundTransferAdvice-DD not supported on eTz platform!");
                        }
                        else
                        {
                          responseCode = "05";
                          LOG.info("Prior FTDD was not successful, ignore reversal.");
                        }
                      }
                      else
                      {
                        responseCode = "05";
                        LOG.error("Invalid Amount");
                      }
                    }
                  }
                  else
                  {
                    responseCode = "96";
                    LOG.error("SOmething went wrong with mandateAmount from FTAdvice-DirectDebit!");
                  }
                }
                else
                {
                  responseCode = "96";
                  LOG.error("Could not get response code for prior FTDD");
                }
              }
            }
            catch (Exception e)
            {
              responseCode = "13";
              LOG.error("Invalid amount/transaction fee:: Amount: " + ftadr.getAmount() + "; Transaction fee: " + ftadr.getTransFee());
            }
          }
          else
          {
            responseCode = "71";
            LOG.error("Empty Mandate Reference Number");
          }
        }
        else
        {
          responseCode = "07";
          LOG.error("Account: " + ftadr.getDebitAccNo() + " is invalid or does not exist.");
        }
      }
      else
      {
        responseCode = "17";
        LOG.error("Channel Code: " + ftadr.getChannelCode() + " is not a valid NIBSS Channel Code.");
      }
    }
    else
    {
      responseCode = "96";
      LOG.error("Could not read request!");
    }
    ftadres.setSessionID(ftadr.getSessionID() == null ? "" : ftadr.getSessionID());
    ftadres.setNameEnquiryRef(ftadr.getNameEnquiryRef() == null ? "" : ftadr.getNameEnquiryRef());
    ftadres.setDestInstCode(ftadr.getDestInstCode() == null ? "" : ftadr.getDestInstCode());
    ftadres.setChannelCode(ftadr.getChannelCode() == null ? "" : ftadr.getChannelCode());
    ftadres.setDebitAccName(ftadr.getDebitAccName() == null ? "" : ftadr.getDebitAccName());
    ftadres.setDebitAccNo(ftadr.getDebitAccNo() == null ? "" : ftadr.getDebitAccNo());
    ftadres.setDebitBVN(ftadr.getDebitBVN() == null ? "" : ftadr.getDebitBVN());
    ftadres.setDebitKYCLevel(ftadr.getDebitKYCLevel() == null ? "" : ftadr.getDebitKYCLevel());
    ftadres.setBeneficiaryAccName(ftadr.getBeneficiaryAccName() == null ? "" : ftadr.getBeneficiaryAccName());
    ftadres.setBeneficiaryAccNo(ftadr.getBeneficiaryAccNo() == null ? "" : ftadr.getBeneficiaryAccNo());
    ftadres.setBeneficiaryBVN(ftadr.getBeneficiaryBVN() == null ? "" : ftadr.getBeneficiaryBVN());
    ftadres.setBeneficiaryKYCLevel(ftadr.getBeneficiaryKYCLevel() == null ? "" : ftadr.getBeneficiaryKYCLevel());
    ftadres.setTransLocation(ftadr.getTransLocation() == null ? "" : ftadr.getTransLocation());
    ftadres.setNarration(ftadr.getNarration() == null ? "" : StringEscapeUtils.unescapeHtml3(ftadr.getNarration()));
    ftadres.setPaymentRef(ftadr.getPaymentRef() == null ? "" : ftadr.getPaymentRef());
    ftadres.setMandateRefNo(ftadr.getMandateRefNo() == null ? "" : ftadr.getMandateRefNo());
    ftadres.setTransFee(ftadr.getTransFee() == null ? "" : ftadr.getTransFee());
    ftadres.setAmount(ftadr.getAmount() == null ? "" : ftadr.getAmount());
    ftadres.setRespCode(responseCode);
    






    String xmlResponse = new Conversion().jaxbObj2XML(ftadres, FTAdviceDebitResponse.class);
    String encRes = make("encrypt", xmlResponse);
    return encRes;
  }
  
  public String amountBlock(String request)
  {
    LOG.info("AmountBlock EncryptedRequest: " + request);
    String decryptReq = make("decrypt", request);
    LOG.info("Amount DecryptedRequest: " + decryptReq);
    String responseCode = "";
    AmountBlockRequest abr = (AmountBlockRequest)new Conversion().jaxbXML2Obj(decryptReq, AmountBlockRequest.class);
    AmountBlockResponse abres = new AmountBlockResponse();
    if (abr != null)
    {
      if (!NIBSSChannelCode.getNIBSSChannelCode(abr.getChannelCode().trim()).equalsIgnoreCase("Unknown Code"))
      {
        if (abr.getRefCode().trim().length() > 30)
        {
          responseCode = "69";
          LOG.error("Reference Code: " + abr.getRefCode().trim() + " is greater than 30.");
        }
        else if (!NIBSSReasonCode.getNIBSSReason(abr.getReasonCode().trim()).equalsIgnoreCase("Unknown Code"))
        {
          if ((abr.getTargetAccNo() != null) || (!abr.getTargetAccNo().equals("")))
          {
            try
            {
              double amt = Double.parseDouble(abr.getAmount());
              if (amt <= 0.0D)
              {
                responseCode = "05";
                LOG.error("Amount cannot be 0 or less than 0; Amount entered: " + abr.getAmount());
              }
              else if ((abr.getAmount().contains(".")) && (abr.getAmount().substring(abr.getAmount().indexOf(".") + 1).length() > 2))
              {
                responseCode = "05";
                LOG.error("Amount failed NIBBS format...not 2dp; Amount entered: " + abr.getAmount());
              }
              else
              {
                String rsp = this.wsControl.checkRef("AMOUNT BLOCK", abr.getRefCode(), abr.getTargetAccNo(), false);
                if (rsp != null)
                {
                  if (rsp.equalsIgnoreCase("EXIST"))
                  {
                    responseCode = "26";
                    LOG.error("Duplicate record!");
                  }
                  else
                  {
                    responseCode = "05";
                    LOG.info("AmountBlock not supported on eTz platform!");
                  }
                }
                else
                {
                  responseCode = "96";
                  LOG.error("An error occurred checking amount block ref!");
                }
              }
            }
            catch (Exception e)
            {
              responseCode = "13";
              LOG.error("Invalid amount:: Amount entered: " + abr.getAmount());
            }
          }
          else
          {
            responseCode = "07";
            LOG.error("Account: " + abr.getTargetAccNo() + " is invalid or does not exist.");
          }
        }
        else
        {
          responseCode = "69";
          LOG.error("Reason code: " + abr.getRefCode().trim() + " is not a valid NIBSS Reason code.");
        }
      }
      else
      {
        responseCode = "17";
        LOG.error("Channel Code: " + abr.getChannelCode() + " is not a valid NIBSS Channel Code.");
      }
    }
    else
    {
      responseCode = "96";
      LOG.error("Could not read request!");
    }
    abres.setSessionID(abr.getSessionID() == null ? "" : abr.getSessionID());
    abres.setDestInstCode(abr.getDestInstCode() == null ? "" : abr.getDestInstCode());
    abres.setChannelCode(abr.getChannelCode() == null ? "" : abr.getChannelCode());
    abres.setRefCode(abr.getRefCode() == null ? "" : abr.getRefCode());
    abres.setTargetAccName(abr.getTargetAccName() == null ? "" : abr.getTargetAccName());
    abres.setTargetBVN(abr.getTargetBVN() == null ? "" : abr.getTargetBVN());
    abres.setTargetAccNo(abr.getTargetAccNo() == null ? "" : abr.getTargetAccNo());
    abres.setReasonCode(abr.getReasonCode() == null ? "" : abr.getReasonCode());
    abres.setNarration(abr.getNarration() == null ? "" : StringEscapeUtils.unescapeHtml3(abr.getNarration()));
    abres.setAmount(abr.getAmount() == null ? "" : abr.getAmount());
    abres.setRespCode(responseCode);
    








    String xmlResponse = new Conversion().jaxbObj2XML(abres, AmountBlockResponse.class);
    String encRes = make("encrypt", xmlResponse);
    return encRes;
  }
  
  public String amountUnblock(String request)
  {
    LOG.info("AmountUnblock DecryptedRequest: " + request);
    String decryptReq = make("decrypt", request);
    LOG.info("AmountUnblock DecryptedRequest: " + decryptReq);
    String responseCode = "";
    Double new_blockAmount = null;Double amt = null;Double blockAmount = null;
    AmountUnblockRequest aur = (AmountUnblockRequest)new Conversion().jaxbXML2Obj(decryptReq, AmountUnblockRequest.class);
    AmountUnblockResponse aures = new AmountUnblockResponse();
    if (aur != null)
    {
      if (!NIBSSChannelCode.getNIBSSChannelCode(aur.getChannelCode().trim()).equalsIgnoreCase("Unknown Code"))
      {
        if (aur.getRefCode().trim().length() > 30)
        {
          responseCode = "70";
          LOG.error("Reference Code: " + aur.getRefCode().trim() + " is greater than 30.");
        }
        else if (!NIBSSReasonCode.getNIBSSReason(aur.getReasonCode().trim()).equalsIgnoreCase("Unknown Code"))
        {
          if ((aur.getTargetAccNo() != null) || (!aur.getTargetAccNo().equals("")))
          {
            try
            {
              amt = Double.valueOf(Double.parseDouble(aur.getAmount()));
              double amt_double = amt.doubleValue();
              if (amt_double <= 0.0D)
              {
                responseCode = "05";
                LOG.error("Amount cannot be 0 or less than 0; Amount entered: " + aur.getAmount());
              }
              else if ((aur.getAmount().contains(".")) && (aur.getAmount().substring(aur.getAmount().indexOf(".") + 1).length() > 2))
              {
                responseCode = "05";
                LOG.error("Amount failed NIBBS format...not 2dp; Amount entered: " + aur.getAmount());
              }
              else
              {
                String rsp = this.wsControl.checkRef("AMOUNT BLOCK", aur.getRefCode(), aur.getTargetAccNo(), true);
                if (rsp != null)
                {
                  if (rsp.equalsIgnoreCase("EXIST"))
                  {
                    blockAmount = this.wsControl.getBlockAmount(aur.getRefCode());
                    double blockAmount_double = blockAmount.doubleValue();
                    if (blockAmount_double == -1.0D)
                    {
                      responseCode = "96";
                      LOG.error("An error occurred getting block amount");
                    }
                    else
                    {
                      LOG.info("AMOUNT: " + amt_double);
                      LOG.info("DB AMOUNT: " + blockAmount_double);
                      LOG.info("TEST: " + (amt_double > blockAmount_double));
                      if (amt_double > blockAmount_double)
                      {
                        responseCode = "70";
                        LOG.info("Invalid Amount: amount > block amount");
                      }
                      else if (amt_double <= blockAmount_double)
                      {
                        responseCode = "05";
                        LOG.info("AmountUnblock not supported on eTz platform!");
                      }
                    }
                  }
                  else
                  {
                    responseCode = "70";
                    
                    LOG.error("Not found!");
                  }
                }
                else
                {
                  responseCode = "96";
                  LOG.error("An error occurred!");
                }
              }
            }
            catch (Exception e)
            {
              responseCode = "13";
              LOG.error("Invalid amount:: Amount entered: " + aur.getAmount());
            }
          }
          else
          {
            responseCode = "07";
            LOG.error("Account: " + aur.getTargetAccNo() + " is invalid or does not exist.");
          }
        }
        else
        {
          responseCode = "69";
          LOG.error("Reason code: " + aur.getRefCode().trim() + " is not a valid NIBSS Reason code.");
        }
      }
      else
      {
        responseCode = "17";
        LOG.error("Channel Code: " + aur.getChannelCode() + " is not a valid NIBSS Channel Code.");
      }
    }
    else
    {
      responseCode = "96";
      LOG.error("Could not read request!");
    }
    aures.setSessionID(aur.getSessionID() == null ? "" : aur.getSessionID());
    aures.setDestInstCode(aur.getDestInstCode() == null ? "" : aur.getDestInstCode());
    aures.setChannelCode(aur.getChannelCode() == null ? "" : aur.getChannelCode());
    aures.setRefCode(aur.getRefCode() == null ? "" : aur.getRefCode());
    aures.setTargetAccName(aur.getTargetAccName() == null ? "" : aur.getTargetAccName());
    aures.setTargetBVN(aur.getTargetBVN() == null ? "" : aur.getTargetBVN());
    aures.setTargetAccNo(aur.getTargetAccNo() == null ? "" : aur.getTargetAccNo());
    aures.setReasonCode(aur.getReasonCode() == null ? "" : aur.getReasonCode());
    aures.setNarration(aur.getNarration() == null ? "" : StringEscapeUtils.unescapeHtml3(aur.getNarration()));
    aures.setAmount(aur.getAmount() == null ? "" : aur.getAmount());
    aures.setRespCode(responseCode);
    














    String xmlResponse = new Conversion().jaxbObj2XML(aures, AmountUnblockResponse.class);
    String encRes = make("encrypt", xmlResponse);
    return encRes;
  }
  
  public String accountBlock(String request)
  {
    LOG.info("AccountBlock EncryptedRequest: " + request);
    String decryptReq = make("decrypt", request);
    LOG.info("AccountBlock DecryptedRequest: " + decryptReq);
    String responseCode = "";
    AccountBlockRequest abr = (AccountBlockRequest)new Conversion().jaxbXML2Obj(decryptReq, AccountBlockRequest.class);
    AccountBlockResponse abres = new AccountBlockResponse();
    if (abr != null)
    {
      if (!NIBSSChannelCode.getNIBSSChannelCode(abr.getChannelCode().trim()).equalsIgnoreCase("Unknown Code"))
      {
        if (abr.getRefCode().trim().length() > 30)
        {
          responseCode = "69";
          LOG.error("Reference Code: " + abr.getRefCode().trim() + " is greater than 30.");
        }
        else if (!NIBSSReasonCode.getNIBSSReason(abr.getReasonCode().trim()).equalsIgnoreCase("Unknown Code"))
        {
          if ((abr.getTargetAccNo() != null) || (!abr.getTargetAccNo().equals("")))
          {
            String rsp = this.wsControl.checkRef("ACCOUNT BLOCK", abr.getRefCode(), abr.getTargetAccNo(), false);
            if (rsp != null)
            {
              if (rsp.equalsIgnoreCase("EXIST"))
              {
                responseCode = "26";
                LOG.error("Duplicate record!");
              }
              else
              {
                responseCode = "05";
                LOG.info("AccountBlock not supported on eTz platform!");
              }
            }
            else
            {
              responseCode = "96";
              LOG.error("An error occurred!");
            }
          }
          else
          {
            responseCode = "07";
            LOG.error("Account: " + abr.getTargetAccNo() + " is invalid or does not exist.");
          }
        }
        else
        {
          responseCode = "69";
          LOG.error("Reason code: " + abr.getRefCode().trim() + " is not a valid NIBSS Reason code.");
        }
      }
      else
      {
        responseCode = "17";
        LOG.error("Channel Code: " + abr.getChannelCode() + " is not a valid NIBSS Channel Code.");
      }
    }
    else
    {
      responseCode = "96";
      LOG.error("Could not read request!");
    }
    abres.setSessionID(abr.getSessionID() == null ? "" : abr.getSessionID());
    abres.setDestInstCode(abr.getDestInstCode() == null ? "" : abr.getDestInstCode());
    abres.setChannelCode(abr.getChannelCode() == null ? "" : abr.getChannelCode());
    abres.setRefCode(abr.getRefCode() == null ? "" : abr.getRefCode());
    abres.setTargetAccName(abr.getTargetAccName() == null ? "" : abr.getTargetAccName());
    abres.setTargetBVN(abr.getTargetBVN() == null ? "" : abr.getTargetBVN());
    abres.setTargetAccNo(abr.getTargetAccNo() == null ? "" : abr.getTargetAccNo());
    abres.setReasonCode(abr.getReasonCode() == null ? "" : abr.getReasonCode());
    abres.setNarration(abr.getNarration() == null ? "" : StringEscapeUtils.unescapeHtml3(abr.getNarration()));
    abres.setRespCode(responseCode);
    









    String xmlResponse = new Conversion().jaxbObj2XML(abres, AccountBlockResponse.class);
    String encRes = make("encrypt", xmlResponse);
    return encRes;
  }
  
  public String accountUnblock(String request)
  {
    LOG.info("AccountUnblock DecryptedRequest: " + request);
    String decryptReq = make("decrypt", request);
    LOG.info("AccountUnblock DecryptedRequest: " + decryptReq);
    String responseCode = "";
    AccountUnblockRequest aur = (AccountUnblockRequest)new Conversion().jaxbXML2Obj(decryptReq, AccountUnblockRequest.class);
    AccountUnblockResponse aures = new AccountUnblockResponse();
    if (aur != null)
    {
      if (!NIBSSChannelCode.getNIBSSChannelCode(aur.getChannelCode().trim()).equalsIgnoreCase("Unknown Code"))
      {
        if (aur.getRefCode().trim().length() > 30)
        {
          responseCode = "70";
          LOG.error("Reference Code: " + aur.getRefCode().trim() + " is greater than 30.");
        }
        else if (!NIBSSReasonCode.getNIBSSReason(aur.getReasonCode().trim()).equalsIgnoreCase("Unknown Code"))
        {
          if ((aur.getTargetAccNo() != null) || (!aur.getTargetAccNo().equals("")))
          {
            String rsp = this.wsControl.checkRef("ACCOUNT BLOCK", aur.getRefCode(), aur.getTargetAccNo(), true);
            if (rsp != null)
            {
              if (rsp.equalsIgnoreCase("EXIST"))
              {
                responseCode = "05";
                LOG.info("AccountUnblock not supported on eTz platform!");
              }
              else
              {
                responseCode = "69";
                LOG.error("Not found!");
              }
            }
            else
            {
              responseCode = "96";
              LOG.error("An error occurred!");
            }
          }
          else
          {
            responseCode = "07";
            LOG.error("Account: " + aur.getTargetAccNo() + " is invalid or does not exist.");
          }
        }
        else
        {
          responseCode = "69";
          LOG.error("Reason code: " + aur.getRefCode().trim() + " is not a valid NIBSS Reason code.");
        }
      }
      else
      {
        responseCode = "17";
        LOG.error("Channel Code: " + aur.getChannelCode() + " is not a valid NIBSS Channel Code.");
      }
    }
    else
    {
      responseCode = "96";
      LOG.error("Could not read request!");
    }
    aures.setSessionID(aur.getSessionID() == null ? "" : aur.getSessionID());
    aures.setDestInstCode(aur.getDestInstCode() == null ? "" : aur.getDestInstCode());
    aures.setChannelCode(aur.getChannelCode() == null ? "" : aur.getChannelCode());
    aures.setRefCode(aur.getRefCode() == null ? "" : aur.getRefCode());
    aures.setTargetAccName(aur.getTargetAccName() == null ? "" : aur.getTargetAccName());
    aures.setTargetBVN(aur.getTargetBVN() == null ? "" : aur.getTargetBVN());
    aures.setTargetAccNo(aur.getTargetAccNo() == null ? "" : aur.getTargetAccNo());
    aures.setReasonCode(aur.getReasonCode() == null ? "" : aur.getReasonCode());
    aures.setNarration(aur.getNarration() == null ? "" : StringEscapeUtils.unescapeHtml3(aur.getNarration()));
    aures.setRespCode(responseCode);
    
    String xmlResponse = new Conversion().jaxbObj2XML(aures, AccountUnblockResponse.class);
    String encRes = make("encrypt", xmlResponse);
    return encRes;
  }
}
