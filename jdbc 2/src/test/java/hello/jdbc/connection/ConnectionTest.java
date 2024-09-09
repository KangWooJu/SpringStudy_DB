package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class ConnectionTest {

    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(ConnectionConst.URL,ConnectionConst.USERNAME,ConnectionConst.PASSWORD);
        Connection con2 = DriverManager.getConnection(ConnectionConst.URL,ConnectionConst.USERNAME,ConnectionConst.PASSWORD);
        log.info("Connection = {} , class = {}",con1,con1.getClass());
        log.info("Connection = {} , class = {}",con2,con2.getClass());
    }

    @Test
    void dataSourceDriverManager() throws SQLException{
        //DriverManagerDataSource - 항상 새로운 커넥션을 획득
        DriverManagerDataSource dataSource = new DriverManagerDataSource(ConnectionConst.URL,ConnectionConst.USERNAME,ConnectionConst.PASSWORD);
        useDataSoruce(dataSource);
    }

    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        //커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(ConnectionConst.URL);
        dataSource.setUsername(ConnectionConst.USERNAME);
        dataSource.setPassword(ConnectionConst.PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        useDataSoruce(dataSource);
        Thread.sleep(1000);
    }


    private void useDataSoruce(DataSource dataSource)throws SQLException{
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();


        log.info("Connection = {} , class = {}",con1,con1.getClass());
        log.info("Connection = {} , class = {}",con2,con2.getClass());
    }
}
