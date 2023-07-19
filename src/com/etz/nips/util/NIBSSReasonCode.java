package com.etz.nips.util;

import java.util.HashMap;

public class NIBSSReasonCode
{
  private static final HashMap<String, String> nrc = new HashMap();
  
  static
  {
    nrc.put("0001", "Suspected fraud");
    nrc.put("0002", "Security violation");
    nrc.put("0003", "Multiple cases of insufficient fund");
    nrc.put("0004", "Multiple cases of “Transfer limit Exceeded");
    nrc.put("0005", "Non-compliance with operating regulations");
    nrc.put("0006", "Identity theft");
    nrc.put("0007", "Duplicate transaction processing");
    nrc.put("0008", "Fraudulent multiple transactions");
    nrc.put("0009", "Payment made by other means");
    nrc.put("0010", "Purpose of payment not redeemed");
    nrc.put("0011", "Recurring transactions");
    nrc.put("1111", "Others");
  }
  
  public static String getNIBSSReason(String code)
  {
    String reason = (String)nrc.get(code);
    return reason == null ? "Unknown Code" : reason;
  }
}
