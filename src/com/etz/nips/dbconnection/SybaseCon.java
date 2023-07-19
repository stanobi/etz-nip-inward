/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.etz.nips.dbconnection;

/**
 *
 * @author olayiwola.okunola
 */

import java.io.PrintStream;
import java.sql.Connection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

public class SybaseCon {
    private static final Logger LOG = Logger.getLogger(SybaseCon.class);
  private static Context initCtx;
  
  static
  {
    try
    {
      initCtx = new InitialContext();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public static Connection getDBConnection()
  {
    try
    {
      DataSource ds = (DataSource)initCtx.lookup("java:pmtSybaseDB");
      return ds.getConnection();
    }
    catch (Exception ce)
    {
      System.out.println("WARNING::Error occured trying to connect to PMTdb database " + ce.getMessage());
      LOG.error("Connection Error: " + ce.getMessage());
      ce.printStackTrace();
    }
    return null;
  }
}
