package com.etz.nips.util;

import java.util.HashMap;

public class NIBSSChannelCode
{
  private static final HashMap<String, String> ncc = new HashMap();
  
  static
  {
    ncc.put("1", "Bank Teller");
    ncc.put("2", "Internet Banking");
    ncc.put("3", "Mobile Phones");
    ncc.put("4", "POS Terminals");
    ncc.put("5", "ATM");
    ncc.put("6", "Vendor/Merchant Web Portal");
    ncc.put("7", "Third – Party Payment Platform");
    ncc.put("8", "Other Channels");
  }
  
  public static String getNIBSSChannelCode(String code)
  {
    String channel = (String)ncc.get(code);
    return channel == null ? "Unknown Code" : channel;
  }
}
