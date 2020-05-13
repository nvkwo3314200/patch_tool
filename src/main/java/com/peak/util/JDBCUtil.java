package com.peak.util;

import com.peak.db.DBObjectBean;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static List<DBObjectBean> queryDBObject(String sql) {
        List<Connection> connList = getConnection();
        List<DBObjectBean> list = new ArrayList<>();
        Connection conn = null;
        if(connList.size() > 0) {
            conn = connList.get(connList.size() - 1);
//            conn = connList.get(0);
        }
        if(conn != null) {
            try (Statement smt = conn.createStatement();
                ResultSet rs = smt.executeQuery(sql);){
                while (rs.next()) {
                    DBObjectBean bean = new DBObjectBean();
                    bean.setName(rs.getString("name").trim());
                    bean.setContentText(rs.getString("definition").trim());
                    bean.setLastUpdateTime(rs.getTimestamp("modify_date"));
                    bean.setType(rs.getString("type").trim());
                    list.add(bean);
                }
            } catch (Exception e) {
                logger.error("{}", e);
            }
        }
        return list;
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
