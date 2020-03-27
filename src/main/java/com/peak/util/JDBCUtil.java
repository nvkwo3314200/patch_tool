package com.peak.util;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JDBCUtil {
    private static final Logger logger = Logger.getLogger(JDBCUtil.class);
    private static List<Connection> connList;

    public static final List<Connection> getConnection() {
        if(connList == null) {
            connList = new ArrayList<>();
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                String[] urls = PropertyUtil.getValue("sqlServer.url").split("\\|");
                String[] unames = PropertyUtil.getValue("sqlServer.uname").split("\\|");
                String[] pwds = PropertyUtil.getValue("sqlServer.pwd").split("\\|");
                for(int i = 0; i < urls.length; i++) {
                    Connection conn = null;
                    try {
                        conn = DriverManager.getConnection(urls[i], unames[i], pwds[i]);
                        connList.add(conn);
                    } catch (SQLException e) {
                        logger.error(e);
                    }
                }
            } catch (ClassNotFoundException e) {
                logger.error(e);
            }
        }
        return connList;
    }

    public static void runScript(String script, String name) {
        List<Connection> connList = getConnection();
        connList.forEach(conn -> {
            try (Statement smt = conn.createStatement()){
                smt.execute(script);
                logger.info(String.format("%s run %s success", conn.getMetaData().getURL().split(";")[0], name));
            } catch (SQLException e) {
                try {
                    logger.error(String.format("%s run %s success", conn.getMetaData().getURL().split(";")[0], name));
                } catch (SQLException ex) {
                }
                logger.error(e.getMessage());
            }
        });
    }

    public static void release() {
        connList.forEach(conn -> {
            if(conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        });
    }

    public static void main(String[] args) {
        getConnection();
    }

}
