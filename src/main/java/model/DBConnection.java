package model;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBConnection {
  private static volatile DataSource dataSource;

  private DBConnection() {}

  public static DataSource getDataSource() {
    if (dataSource == null) {
      synchronized (DBConnection.class) {
        if (dataSource == null) {
          try {
            Context init = new InitialContext();
            Context env = (Context) init.lookup("java:comp/env");
            dataSource = (DataSource) env.lookup("jdbc/whiTee");
          } catch (NamingException e) {
            throw new RuntimeException("Cannot initialize DataSource", e);
          }
        }
      }
    }
    return dataSource;
  }
}

