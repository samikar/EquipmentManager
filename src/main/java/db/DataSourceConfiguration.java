package db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
 
import javax.sql.DataSource;
 
@Configuration
public class DataSourceConfiguration {
 
    @Bean(name = "database")
    @ConfigurationProperties("database.datasource")
    @Primary
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }
 
    @Bean(name = "testdatabase")
    @ConfigurationProperties("testdatabase.datasource")
    public DataSource dataSource2(){
        return DataSourceBuilder.create().build();
    }
 
    @Bean(name="tm1")
    @Autowired
    @Primary
    DataSourceTransactionManager tm1(@Qualifier ("database") DataSource datasource) {
        DataSourceTransactionManager txm  = new DataSourceTransactionManager(datasource);
        return txm;
    }
 
    @Bean(name="tm2")
    @Autowired
    DataSourceTransactionManager tm2(@Qualifier ("testdatabase") DataSource datasource) {
        DataSourceTransactionManager txm  = new DataSourceTransactionManager(datasource);
        return txm;
    }
}