package kr.pe.kwonnam.ctmtest;

import kr.pe.kwonnam.ctmtest.connectioncount.DatetimeDao;
import kr.pe.kwonnam.ctmtest.connectioncount.DatetimeService;
import kr.pe.kwonnam.ctmtest.connectioncount.DatetimeServiceImpl;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
@EnableTransactionManagement
public class ApplicationContextConfig implements TransactionManagementConfigurer {

    public static final String H2_DRIVER_CLASS_NAME = "org.h2.Driver";

    // 리턴 타입이 BasicDataSource인 것은 테스트를 위한 것임. 실전에서는 DataSource로 리턴할 것.
    @Bean(destroyMethod = "close")
    public BasicDataSource firstDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(H2_DRIVER_CLASS_NAME);
        dataSource.setUrl("jdbc:h2:mem:first");

        dataSource.setMaxActive(5); // small max active

        initBookTable(dataSource);

        return dataSource;
    }

    private void initBookTable(BasicDataSource dataSource) {
        try (Connection con = dataSource.getConnection()) {
            Statement stmt = con.createStatement();
            stmt.execute("CREATE TABLE books (ID INT, title VARCHAR(50), author VARCHAR(30))");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean(destroyMethod = "close")
    public BasicDataSource secondDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(H2_DRIVER_CLASS_NAME);
        dataSource.setUrl("jdbc:h2:mem:second");

        dataSource.setMaxActive(10); // bigger max active

        initPersonTable(dataSource);

        return dataSource;
    }

    private void initPersonTable(BasicDataSource dataSource) {
        try (Connection con = dataSource.getConnection()) {
            Statement stmt = con.createStatement();
            stmt.execute("CREATE TABLE people (ID INT, name VARCHAR(50), birthdate TIMESTAMP)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean(destroyMethod = "close")
    public BasicDataSource thirdDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(H2_DRIVER_CLASS_NAME);
        dataSource.setUrl("jdbc:h2:mem:third");

        dataSource.setMaxActive(10);

        return dataSource;
    }

    @Bean
    public DataSource firstLazyDataSource() {
        return new LazyConnectionDataSourceProxy(firstDataSource());
    }

    @Bean
    public DataSource secondLazyDataSource() {
        return new LazyConnectionDataSourceProxy(secondDataSource());
    }

    @Bean
    public DataSource thirdLazyDataSource() {
        return new LazyConnectionDataSourceProxy(thirdDataSource());
    }

    @Bean
    public PlatformTransactionManager firstTransactionManager() {
//        return new DataSourceTransactionManager(firstLazyDataSource());
        return new DataSourceTransactionManager(firstDataSource());
    }

    @Bean
    public PlatformTransactionManager secondTransactionManager() {
//        return new DataSourceTransactionManager(secondLazyDataSource());
        return new DataSourceTransactionManager(secondDataSource());
    }

    @Bean
    public PlatformTransactionManager thirdTransactionManager() {
        return new DataSourceTransactionManager(thirdDataSource());
    }

    @Bean
    public PlatformTransactionManager doubleChainedTransactionManager() {
        return new ChainedTransactionManager(firstTransactionManager(), secondTransactionManager());
    }

    @Bean
    public PlatformTransactionManager tripleChainedTransactionManager() {
        return new ChainedTransactionManager(firstTransactionManager(), secondTransactionManager(), thirdTransactionManager());
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return tripleChainedTransactionManager();
    }

    @Bean
    public DatetimeDao firstDatetimeDao() {
        return new DatetimeDao(firstDataSource());
    }

    @Bean
    public DatetimeDao secondDatetimeDao() {
        return new DatetimeDao(secondDataSource());
    }

    @Bean
    public DatetimeService datetimeService() {
        return new DatetimeServiceImpl(firstDatetimeDao(), secondDatetimeDao());
    }
}