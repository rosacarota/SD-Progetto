package model;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBConnection {

    private static DataSource dataSource;

    private DBConnection() {}

    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = initDataSource();
        }
        return dataSource;
    }

    private static DataSource initDataSource() {
        try {
            Context init = new InitialContext();
            Context env = (Context) init.lookup("java:comp/env");
            return (DataSource) env.lookup("jdbc/whiTee");
        } catch (NamingException e) {
            throw new ExceptionInInitializerError("Failed to initialize DataSource: " + e.getMessage());
        }
    }
}
