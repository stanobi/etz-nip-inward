package com.etz.nips.ws;

import com.etz.nips.util.Misc;
import nfp.ssm.core.SSMLib;
import org.apache.log4j.Logger;

public class NIPSCred
{
  private static Logger LOG = Logger.getLogger(NIPSCred.class);
  private static final String PUBLIC_KEY = new Misc().getPropertyValue("PUBLIC_KEY");
  private static final String PRIVATE_KEY = new Misc().getPropertyValue("PRIVATE_KEY");
  
  public static SSMLib getSSMlibObj()
  {
    return new SSMLib(PUBLIC_KEY, PRIVATE_KEY);
  }
}
