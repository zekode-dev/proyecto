package com.example.proyecto.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.proyecto.repository",
        includeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
                classes = com.example.proyecto.repository.LogoRepository.class
        ),
        entityManagerFactoryRef = "sqliteEntityManagerFactory",
        transactionManagerRef = "sqliteTransactionManager"
)
public class SqliteDataSourceConfig {

    @Value("${spring.datasource.sqlite.url}")
    private String url;

    @Value("${spring.datasource.sqlite.driver-class-name}")
    private String driverClassName;

    @Bean(name = "sqliteDataSource")
    public DataSource sqliteDataSource() {
        return DataSourceBuilder.create()
                .url(url)
                .driverClassName(driverClassName)
                .build();
    }

    @Bean(name = "sqliteEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sqliteEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("sqliteDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.archive.autodetection", "none");
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");

        return builder
                .dataSource(dataSource)
                .packages(com.example.proyecto.model.logo.class)
                .properties(properties)
                .persistenceUnit("sqlite")
                .build();
    }

    @Bean(name = "sqliteTransactionManager")
    public PlatformTransactionManager sqliteTransactionManager(
            @Qualifier("sqliteEntityManagerFactory") EntityManagerFactory sqliteEntityManagerFactory) {
        return new JpaTransactionManager(sqliteEntityManagerFactory);
    }
}