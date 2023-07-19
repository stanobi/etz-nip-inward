/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.etz.nips.model;

/**
 *
 * @author olayiwola.okunola
 */

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="FinancialInstitutionListRequest")
public class FinancialInstitutionListRequest
{
  private Header header;
  private List<Record> records = new ArrayList();
  
  @XmlElement(name="Header", type=Header.class)
  public Header getHeader()
  {
    return this.header;
  }
  
  public void setHeader(Header header)
  {
    this.header = header;
  }
  
  @XmlElement(name="Record", type=Record.class)
  public List<Record> getRecords()
  {
    return this.records;
  }
  
  public void setRecords(List<Record> records)
  {
    this.records = records;
  }
}
