package org.smart4j.framework.helper;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.util.PropsUtil;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库操作助手
 */
public final class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);
    private static final QueryRunner QUERY_RUNNER;
    private static final ThreadLocal<Connection> CONNECTION_HOLDER ;
    private static final BasicDataSource DATA_SOURCE;
    static {
        CONNECTION_HOLDER = new ThreadLocal<Connection>();
        QUERY_RUNNER = new QueryRunner();
        Properties conf = PropsUtil.loadProps("config.properties");

        String driver = conf.getProperty("jdbc.driver");
        String url = conf.getProperty("jdbc.url");
        String username = conf.getProperty("jdbc.username");
        String password = conf.getProperty("jdbc.password");

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(driver);
        DATA_SOURCE.setUrl(url);
        DATA_SOURCE.setUsername(username);
        DATA_SOURCE.setPassword(password);
    }
    /**
     * 获取数据连接
     */
    public static Connection getConnerction(){
        Connection conn =CONNECTION_HOLDER.get();
        try{
            conn = DATA_SOURCE.getConnection();
        } catch (SQLException e) {
            LOGGER.error("get connection failure",e);
        }
        finally {
            CONNECTION_HOLDER.set(conn);
        }
        return conn;
    }


    /**
     * 开启事务
     */
    public static void beginTransaction(){
        Connection conn =getConnerction();
        if (conn != null){
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                LOGGER.error("begin transaction failure",e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLDER.set(conn);
            }
        }
    }

    /**
     * 提交事务
     */
    public static void commitTransaction(){
        Connection conn = getConnerction();
        if (conn != null){
            try {
                conn.commit();
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("commit transaction failure",e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }

    /**
     * 事务回滚
     */
    public static void rollbackTransaction(){
        Connection conn = getConnerction();
        if (conn != null){
            try {
                conn.rollback();
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("rollback transaction failure",e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 关闭数据链接

     public static void closeConnection(){
     Connection conn =CONNECTION_HOLDER.get();
     if (conn != null){
     try {
     conn.close();
     }catch (SQLException e){
     LOGGER.error("clsoe connection failure" , e);
     }finally {
     CONNECTION_HOLDER.remove();
     }
     }
     }
     */

}
