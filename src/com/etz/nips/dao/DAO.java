package com.etz.nips.dao;

import com.etz.nips.dbconnection.SybaseCon;
import com.etz.nips.model.FinancialInstitutionListRequest;
import com.etz.nips.model.Header;
import com.etz.nips.model.MandateAdviceRequest;
import com.etz.nips.model.Record;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import org.apache.log4j.Logger;

public class DAO
{
  private static final Logger LOG = Logger.getLogger(SybaseCon.class);
  
  private void closeConnection(Connection con)
  {
    try
    {
      con.close();
    }
    catch (SQLException e)
    {
      LOG.error("closeConnection SQLException: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  public void saveRequest(String sessionID, String source, String method, Date date, String channel, String destCode, String responseCode, String ip)
  {
    Connection con = SybaseCon.getDBConnection();
    if (con != null)
    {
      String insertSQL = "INSERT INTO NIBS_REQUEST_LOG (SESSION_ID, SOURCE_CODE, METHOD_CALL, TRANSACTION_DATE, CHANNEL_ID, DESTINATION_CODE, RESPONSE_CODE, REMOTE_IP) VALUES (?,?,?,?,?,?,?,?)";
      try
      {
        PreparedStatement ps = con.prepareStatement(insertSQL);
        ps.setString(1, sessionID);
        ps.setString(2, source);
        ps.setString(3, method);
        ps.setTimestamp(4, new Timestamp(date.getTime()));
        
        ps.setString(5, channel);
        ps.setString(6, destCode);
        ps.setString(7, responseCode);
        ps.setString(8, ip);
        ps.executeUpdate();
        LOG.info("saveRequest - " + method + " request saved!");
      }
      catch (SQLException e)
      {
        LOG.error("saveRequest SQLException: " + e.getMessage());
        e.printStackTrace();
      }
      finally
      {
        closeConnection(con);
      }
    }
  }
  
  public String mandateExist(String mandateRefNo, String debitAccNo)
  {
    Connection con = SybaseCon.getDBConnection();
    String response = null;
    if (con != null)
    {
      response = checkMandate(con, mandateRefNo, debitAccNo);
      closeConnection(con);
    }
    return response;
  }
  
  private String checkMandate(Connection con, String mandateRefNo, String debitAccNo)
  {
    String selectSQL = "SELECT * FROM NIBS_MANDATE_ADVICE_REQUESTS WHERE REF = ? AND DEBIT_ACCOUNT = ?";
    String record = null;
    try
    {
      PreparedStatement ps = con.prepareStatement(selectSQL);
      ps.setString(1, mandateRefNo);
      ps.setString(2, debitAccNo);
      ResultSet rs = ps.executeQuery();
      if (rs.next())
      {
        double amount = rs.getDouble("AMOUNT");
        record = String.valueOf(amount);
      }
      else
      {
        record = "NOT FOUND";
      }
    }
    catch (SQLException e)
    {
      LOG.error("checkMandate SQLException: " + e.getMessage());
      e.printStackTrace();
    }
    LOG.info("checkMandate: " + record);
    return record;
  }
  
  public String getResponseCode(String method, String sessionID)
  {
    String responseCode = null;
    Connection con = SybaseCon.getDBConnection();
    if (con != null)
    {
      ResultSet rs = getRequestDetails(con, method, sessionID);
      if (rs != null) {
        try
        {
          if (rs.next()) {
            responseCode = rs.getString("RESPONSE_CODE");
          }
        }
        catch (SQLException e)
        {
          LOG.error("getRespCode SQLException: " + e.getMessage());
          e.printStackTrace();
        }
        finally
        {
          closeConnection(con);
        }
      }
    }
    return responseCode;
  }
  
  private ResultSet getRequestDetails(Connection con, String method, String sessionID)
  {
    ResultSet rs = null;
    String selectSQL = "SELECT * FROM NIBS_REQUEST_LOG WHERE METHOD_CALL = ? AND SESSION_ID = ?";
    try
    {
      PreparedStatement ps = con.prepareStatement(selectSQL);
      ps.setString(1, method);
      ps.setString(2, sessionID);
      rs = ps.executeQuery();
    }
    catch (SQLException e)
    {
      LOG.error("getRequestDetails SQLException: " + e.getMessage());
      e.printStackTrace();
    }
    return rs;
  }
  
  public void insertTnx(String sessionId, String ref, String channel, String amount, String source, String dest, String respCode, String eTzRespCode, Date date, String payRef, String beneficiaryAccNo, String debitAccNo, String mandateRef, String tnxType, String transNarration, String ip)
  {
    Connection con = SybaseCon.getDBConnection();
    if (con != null)
    {
      String insertSQL = "INSERT INTO NIBS_TRANSACTION_LOG (SESSION_ID, UNIQUE_TRANSID, CHANNEL_ID, TRANSACTION_AMOUNT, SOURCE_CODE, DESTINATION_CODE, RESPONSE_CODE, SOURCE_RESPONSE_CODE, TRANSACTION_DATE, PAYMENT_REFERENCE, MANDATE_REFERENCE, BENEFICIARY_ACCOUNT_NUMBER, DEBIT_ACCOUNT_NUMBER, TRANS_TYPE, TRANS_NARRATION, REMOTE_IP) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      try
      {
        PreparedStatement ps = con.prepareStatement(insertSQL);
        ps.setString(1, sessionId);
        ps.setString(2, ref);
        ps.setString(3, channel);
        ps.setDouble(4, Double.parseDouble(amount));
        ps.setString(5, source);
        ps.setString(6, dest);
        ps.setString(7, respCode);
        ps.setString(8, eTzRespCode);
        ps.setTimestamp(9, new Timestamp(date.getTime()));
        
        ps.setString(10, payRef);
        ps.setString(11, mandateRef);
        ps.setString(12, beneficiaryAccNo);
        ps.setString(13, debitAccNo);
        ps.setString(14, tnxType);
        ps.setString(15, transNarration);
        ps.setString(16, ip);
        ps.executeUpdate();
        LOG.info("insertTnx - " + tnxType + " transaction saved!");
      }
      catch (SQLException e)
      {
        LOG.error("insertTnx SQLException: " + e.getMessage());
        e.printStackTrace();
      }
      finally
      {
        closeConnection(con);
      }
    }
  }
  
  public String getUniqueRef(String sessionID)
  {
    Connection con = SybaseCon.getDBConnection();
    String ref = null;
    if (con != null)
    {
      String selectSQL = "SELECT DISTINCT UNIQUE_TRANSID FROM NIBS_TRANSACTION_LOG WHERE SESSION_ID = ?";
      try
      {
        PreparedStatement ps = con.prepareStatement(selectSQL);
        ps.setString(1, sessionID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
          ref = rs.getString("UNIQUE_TRANSID");
        }
      }
      catch (SQLException e)
      {
        LOG.error("getUniqueRef SQLException: " + e.getMessage());
        e.printStackTrace();
      }
      finally
      {
        closeConnection(con);
      }
    }
    return ref;
  }
  
  public void updateTnx(String sessionID, String respCode, String eTzRespCode)
  {
    Connection con = SybaseCon.getDBConnection();
    if (con != null)
    {
      String updateSQL = "UPDATE NIBS_TRANSACTION_LOG SET SOURCE_RESPONSE_CODE = ?, RESPONSE_CODE = ? WHERE SESSION_ID = ?";
      try
      {
        PreparedStatement ps = con.prepareStatement(updateSQL);
        ps.setString(1, eTzRespCode);
        ps.setString(2, respCode);
        ps.setString(3, sessionID);
        ps.executeUpdate();
        LOG.info("updateTnx - transaction updated!");
      }
      catch (SQLException e)
      {
        LOG.error("updateTnx SQLException: " + e.getMessage());
        e.printStackTrace();
      }
      finally
      {
        closeConnection(con);
      }
    }
  }
  
  public boolean insertInstitution(FinancialInstitutionListRequest filr)
  {
    Connection con = SybaseCon.getDBConnection();
    HashMap<String, String> process = new HashMap();
    if (con != null)
    {
      String batchNo = filr.getHeader().getBatchNo();
      for (Record r : filr.getRecords()) {
        if (!check4Inst(con, r))
        {
          LOG.info("Inserting institution: " + r.getInstName() + "; Code: " + r.getInstCode());
          String insertSQL = "INSERT INTO NIBS_INSTITUTIONS (CODE, NAME, CATEGORY, BATCH_NUMBER) VALUES (?,?,?,?)";
          try
          {
            PreparedStatement ps = con.prepareStatement(insertSQL);
            ps.setString(1, r.getInstCode());
            ps.setString(2, r.getInstName());
            ps.setString(3, r.getCategory());
            ps.setString(4, batchNo);
            ps.execute();
            LOG.info("Institution: " + r.getInstName() + "; Code: " + r.getInstCode() + " inserted.");
            process.put(r.getInstName(), "INSERTED");
          }
          catch (SQLException e)
          {
            LOG.error("insertInstitution SQLException: " + e.getMessage());
            process.put(r.getInstName(), "NOT INSERTED");
            e.printStackTrace();
          }
        }
        else
        {
          LOG.info("Institution: " + r.getInstName() + "; Code: " + r.getInstCode() + " already exist.");
          process.put(r.getInstName(), "INSERTED");
        }
      }
      closeConnection(con);
    }
    LOG.info("All Financial List inserted:: " + (!process.containsValue("NOT INSERTED")));
    return !process.containsValue("NOT INSERTED");
  }
  
  private boolean check4Inst(Connection con, Record r)
  {
    boolean exist = false;
    String selectSQL = "SELECT * FROM NIBS_INSTITUTIONS WHERE CODE = ?";
    try
    {
      PreparedStatement ps = con.prepareStatement(selectSQL);
      ps.setString(1, r.getInstCode());
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        exist = true;
      }
    }
    catch (SQLException e)
    {
      LOG.error("check4Inst SQLException: " + e.getMessage());
      e.printStackTrace();
    }
    return exist;
  }
  
  public String verifyMandateAdvice(MandateAdviceRequest mdr)
  {
    String record = null;
    Connection con = SybaseCon.getDBConnection();
    if (con != null)
    {
      record = checkMandate(con, mdr.getMandateRefNo(), mdr.getDebitAccNo());
      closeConnection(con);
    }
    return record;
  }
  
  public void saveMandate(MandateAdviceRequest mdr)
  {
    Connection con = SybaseCon.getDBConnection();
    if (con != null)
    {
      String done = insertMandate(con, mdr);
      LOG.info("saveMandate: " + done);
      closeConnection(con);
    }
  }
  
  private String insertMandate(Connection con, MandateAdviceRequest mdr)
  {
    String record = null;
    String insertSQL = "INSERT INTO NIBS_MANDATE_ADVICE_REQUESTS (REF, DEBIT_ACCOUNT, AMOUNT, BENEFICIARY_ACCOUNT, REQUEST_DATE) VALUES (?,?,?,?,?)";
    try
    {
      PreparedStatement ps = con.prepareStatement(insertSQL);
      ps.setString(1, mdr.getMandateRefNo());
      ps.setString(2, mdr.getDebitAccNo());
      ps.setDouble(3, Double.parseDouble(mdr.getAmount()));
      ps.setString(4, mdr.getBeneficiaryAccNo());
      
      ps.setTimestamp(5, new Timestamp(new Date().getTime()));
      ps.executeUpdate();
      record = "INSERTED";
    }
    catch (SQLException e)
    {
      LOG.error("insertMandate SQLException: " + e.getMessage());
      e.printStackTrace();
    }
    return record;
  }
  
  public String checkRef(String action, String refCode, String targetAccNo, boolean unblock)
  {
    Connection con = SybaseCon.getDBConnection();
    String response = null;
    if (con != null)
    {
      ResultSet rs = refExist(con, action, refCode, targetAccNo, unblock);
      if (rs != null) {
        try
        {
          if (rs.next()) {
            response = "EXIST";
          } else {
            response = "NOT FOUND";
          }
        }
        catch (SQLException e)
        {
          LOG.error("checkRef SQLException: " + e.getMessage());
          e.printStackTrace();
        }
      }
      closeConnection(con);
    }
    return response;
  }
  
  private ResultSet refExist(Connection con, String action, String refCode, String targetAccNo, boolean unblock)
  {
    String selectSQL = "SELECT * FROM NIBS_BLOCK WHERE BLOCK_REF = ? AND ACCOUNT = ? AND TYPE = ?";
    if (unblock) {
      selectSQL = selectSQL + " AND UNBLOCKED = ?";
    }
    ResultSet rs = null;
    try
    {
      PreparedStatement ps = con.prepareStatement(selectSQL);
      ps.setString(1, refCode);
      ps.setString(2, targetAccNo);
      ps.setString(3, action);
      if (unblock) {
        ps.setString(4, "No");
      }
      rs = ps.executeQuery();
    }
    catch (SQLException e)
    {
      LOG.error("refExist SQLException: " + e.getMessage());
      e.printStackTrace();
    }
    return rs;
  }
  
  public Double getBlockAmount(String refCode)
  {
    double blockAmount = -1.0D;
    Connection con = SybaseCon.getDBConnection();
    if (con != null)
    {
      ResultSet rs = getBlockByRef(con, refCode);
      if (rs != null) {
        try
        {
          if (rs.next()) {
            blockAmount = rs.getDouble("AMOUNT");
          }
        }
        catch (SQLException e)
        {
          LOG.error("getBlockAmount SQLException: " + e.getMessage());
          e.printStackTrace();
        }
        finally
        {
          closeConnection(con);
        }
      }
    }
    return Double.valueOf(blockAmount);
  }
  
  private ResultSet getBlockByRef(Connection con, String refCode)
  {
    String selectSQL = "SELECT * FROM NIBS_BLOCK WHERE BLOCK_REF = ? and RESPONSE_CODE = ? AND TYPE = ?";
    ResultSet rs = null;
    try
    {
      PreparedStatement ps = con.prepareStatement(selectSQL);
      ps.setString(1, refCode);
      ps.setString(2, "00");
      ps.setString(3, "AMOUNT BLOCK");
      rs = ps.executeQuery();
    }
    catch (SQLException e)
    {
      LOG.error("getBlockByRef SQLException: " + e.getMessage());
      e.printStackTrace();
    }
    return rs;
  }
}
