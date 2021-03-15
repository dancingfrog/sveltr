package com.ericsson.include;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InitConf {
  public static String uid = "auto";
  static {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
    System.setProperty("current.date", dateFormat.format(new Date()));
  }

  // static{
  // MDC.remove("user.uid");
  // MDC.put("user.uid",uid );
  // }

  // try
  // {
  // MDC.remove("user.uid");
  // MDC.put("user.uid",new MySessionHandler().getMyString());
  // }
  // catch (Exception e) {
  // // TODO: handle exception
  // }
}
