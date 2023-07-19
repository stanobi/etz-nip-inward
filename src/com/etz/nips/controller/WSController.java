package com.etz.nips.controller;

import com.etz.http.etc.Card;
import com.etz.http.etc.HttpHost;
import com.etz.http.etc.TransCode;
import com.etz.http.etc.VCardRequest;
import com.etz.http.etc.XProcessor;
import com.etz.http.etc.XRequest;
import com.etz.http.etc.XResponse;
import com.etz.nips.dao.DAO;
import com.etz.nips.model.FinancialInstitutionListRequest;
import com.etz.nips.model.MandateAdviceRequest;
import com.etz.nips.util.Misc;
import com.google.gson.JsonObject;
import java.util.Date;
import org.apache.log4j.Logger;

public class WSController
{
  private static String AUTOSWITCH_IP;
  private static String AUTOSWITCH_PORT;
  private static String KEY;
  private static String SCHEME_CODE;
  private static String BE_CHANNEL_ID;
  private static String FT_CHANNEL_ID;
  private static Logger LOG;
  private DAO nipDAO;
  private final static String ACCOUNT_NUMBER_PREFIX = "234";
  
  public WSController()
  {
    AUTOSWITCH_IP = new Misc().getPropertyValue("AUTOSWITCH_IP");
    AUTOSWITCH_PORT = new Misc().getPropertyValue("AUTOSWITCH_PORT");
    KEY = new Misc().getPropertyValue("AUTOSWITCH_SECURE_KEY");
    SCHEME_CODE = new Misc().getPropertyValue("AUTOSWITCH_SCHEME_CODE");
    BE_CHANNEL_ID = new Misc().getPropertyValue("BE_CHANNEL_ID");
    FT_CHANNEL_ID = new Misc().getPropertyValue("FT_CHANNEL_ID");
    LOG = Logger.getLogger(WSController.class);
    this.nipDAO = new DAO();
  }
  
  public String getCardDetails(String accountNumber)
  {
    JsonObject obj = new JsonObject();
    String desc = null;
    XProcessor processor = new XProcessor();
    HttpHost host = new HttpHost();
    host.setServerAddress(AUTOSWITCH_IP);
    host.setPort(Integer.valueOf(AUTOSWITCH_PORT).intValue());
    host.setSecureKey(KEY);
    
    VCardRequest request = new VCardRequest();
    request.setOtherReference("ETZ-NIPS" + new Misc().generateRandom(10));
    request.setMobileNumber(ACCOUNT_NUMBER_PREFIX+accountNumber);
    request.setRequestType(TransCode.CARDQUERY);
    request.setSchemeCode(SCHEME_CODE);
    try
    {
      VCardRequest response = processor.process(host, request);
      LOG.info("getCardDetails - Response code: " + response.getResponse());
      if (response.getResponse() == 0)
      {
        LOG.info("getCard CardNumber: " + response.getCardNumber());
        LOG.info("getCard AccountNumber: " + response.getAccountNumber());
        LOG.info("getCard Status: " + response.getStatus());
        desc = response.getFirstName().trim() + " " + response.getLastName().split("~")[0].trim() + "~" + response.getCardNumber().trim() + "~" + response.getCardBalance();
        obj.addProperty("Response", "SUCCESS");
        obj.addProperty("ResponseDetails", desc);
      }
      else
      {
        desc = "Fetching Mobile Money details Failed:: ResponseCode: " + response.getResponse();
        obj.addProperty("Response", "ERROR");
        obj.addProperty("ResponseDetails", desc);
        LOG.error(desc);
      }
    }
    catch (Exception ex)
    {
      desc = "getCardDetails - AUTOCLIENT Error: " + ex.getMessage();
      obj.addProperty("Response", "SEVERE_ERROR");
      obj.addProperty("ResponseDetails", desc);
      LOG.error(desc);
    }
    return obj.toString();
  }
  
  public void saveRequest(String sessionID, String source, String method, Date date, String channel, String destCode, String responseCode, String ip)
  {
    this.nipDAO.saveRequest(sessionID, source, method, date, channel, destCode, responseCode, ip);
  }
  
  public String mandateExist(String mandateRefNo, String debitAccNo)
  {
    return this.nipDAO.mandateExist(mandateRefNo, debitAccNo);
  }
  
  public String getResponseCode(String method, String sessionID)
  {
    return this.nipDAO.getResponseCode(method, sessionID);
  }
  
  public String autoclientTnx(String op, String num, String merchant, String pin, String expDate, String ref, String tnxDesc, double fee, double amount)
  {
    LOG.info("OP: " + op);
    LOG.info("MERCHANT: " + merchant);
    LOG.info("NUM: " + num);
    LOG.info("PIN: " + pin);
    LOG.info("EXPDATE: " + expDate);
    LOG.info("REF: " + ref);
    LOG.info("TNXDESC: " + tnxDesc);
    LOG.info("FEE: " + fee);
    LOG.info("AMOUNT: " + amount);
    
    XProcessor processor = new XProcessor();
    String desc = "";
    JsonObject ob = new JsonObject();
    HttpHost host = new HttpHost();
    host.setServerAddress(AUTOSWITCH_IP);
    host.setPort(Integer.valueOf(AUTOSWITCH_PORT).intValue());
    host.setSecureKey(KEY);
    
    Card card = new Card();
    card.setCardPin(pin);
    card.setCardNumber(num);
    card.setCardExpiration(expDate);
    
    XRequest req = new XRequest();
    req.setCard(card);
    if (op.equalsIgnoreCase("BalanceEnquiry"))
    {
      req.setTransCode(TransCode.BALANCE);
      req.setChannelId(BE_CHANNEL_ID);
    }
    else if ((op.equalsIgnoreCase("FundTransfer")) || (op.equalsIgnoreCase("FundTransferAdvice")))
    {
      if (op.equalsIgnoreCase("FundTransfer")) {
        req.setTransCode(TransCode.NOTIFICATION);
      } else if (op.equalsIgnoreCase("FundTransferAdvice")) {
        req.setTransCode(TransCode.REVERSAL);
      }
      req.setMerchantCode(merchant);
      req.setChannelId(FT_CHANNEL_ID);
      req.setTransAmount(amount);
    }
    req.setDescription(tnxDesc);
    req.setFee(fee);
    req.setReference(ref);
    try
    {
      XResponse res = processor.process(host, req);
      LOG.info(op + " - Response code: " + res.getResponse());
      if (res.getResponse() == 0)
      {
        if (op.equalsIgnoreCase("BalanceEnquiry"))
        {
          desc = String.valueOf(res.getBalance());
          LOG.info(op + " - Balance: " + res.getBalance());
        }
        else
        {
          desc = op + " ResponseCode: " + res.getResponse();
          LOG.info(op + " ResponseCode: " + res.getResponse());
        }
        ob.addProperty("Response", "SUCCESS");
        ob.addProperty("ResponseDetails", desc);
      }
      else if ((res.getResponse() == -1) || (res.getResponse() == 32) || (res.getResponse() == 31))
      {
        if (op.equalsIgnoreCase("BalanceEnquiry"))
        {
          desc = String.valueOf(res.getBalance());
          LOG.info(op + " - Balance: " + res.getBalance());
        }
        else
        {
          desc = op + " ResponseCode: " + res.getResponse();
          LOG.info(op + " ResponseCode: " + res.getResponse());
        }
        ob.addProperty("Response", "ERRORTIMEOUT");
        ob.addProperty("ResponseDetails", desc);
      }
      else
      {
        desc = op + " ResponseCode: " + res.getResponse();
        ob.addProperty("Response", "ERROR");
        ob.addProperty("ResponseDetails", desc);
        LOG.error("FAILED:: " + desc);
      }
    }
    catch (Exception e)
    {
      desc = "autoclientTnx - AUTOCLIENT ERROR: " + e.getMessage();
      ob.addProperty("Response", "SEVERE_ERROR");
      ob.addProperty("ResponseDetails", desc);
      LOG.error(desc);
    }
    return ob.toString();
  }
  
  public void insertTnx(String sessionId, String ref, String channel, String amount, String source, String dest, String respCode, String eTzRespCode, Date date, String payRef, String beneficiaryAccNo, String debitAccNo, String mandateRef, String tnxType, String transNarration, String ip)
  {
    this.nipDAO.insertTnx(sessionId, ref, channel, amount, source, dest, respCode, eTzRespCode, date, payRef, beneficiaryAccNo, debitAccNo, mandateRef, tnxType, transNarration, ip);
  }
  
  public String getUniqueRef(String sessionID)
  {
    return this.nipDAO.getUniqueRef(sessionID);
  }
  
  public void updateTnx(String sessionID, String respCode, String eTzRespCode)
  {
    this.nipDAO.updateTnx(sessionID, respCode, eTzRespCode);
  }
  
  public boolean insertInstitution(FinancialInstitutionListRequest filr)
  {
    return this.nipDAO.insertInstitution(filr);
  }
  
  public String verifyMandateAdvice(MandateAdviceRequest mdr)
  {
    JsonObject ob = new JsonObject();
    String response = this.nipDAO.verifyMandateAdvice(mdr);
    if (response != null) {
      ob.addProperty("Response", response);
    } else {
      ob.addProperty("Response", "ERROR");
    }
    return ob.toString();
  }
  
  public void saveMandate(MandateAdviceRequest mdr)
  {
    this.nipDAO.saveMandate(mdr);
  }
  
  public String checkRef(String action, String refCode, String targetAccNo, boolean unblock)
  {
    return this.nipDAO.checkRef(action, refCode, targetAccNo, unblock);
  }
  
  public Double getBlockAmount(String refCode)
  {
    return this.nipDAO.getBlockAmount(refCode);
  }
}
