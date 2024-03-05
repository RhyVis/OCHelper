package com.rhynia.ochelper.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {
    private final Path path;
    private final String jdbcSqlite = "jdbc:sqlite:";

    @Primary
    @Bean(name = "itemDataSource")
    @Qualifier("itemDataSource")
    public DataSource itemDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.sqlite.JDBC")
                .type(HikariDataSource.class)
                .url(jdbcSqlite + path.getDatabasePath() + "DATA_ITEM.db").build();
    }

    @Bean(name = "fluidDataSource")
    @Qualifier("fluidDataSource")
    public DataSource fluidDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.sqlite.JDBC")
                .type(HikariDataSource.class)
                .url(jdbcSqlite + path.getDatabasePath() + "DATA_FLUID.db").build();
    }

    @Bean(name = "energyDataSource")
    @Qualifier("energyDataSource")
    public DataSource energyDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.sqlite.JDBC")
                .type(HikariDataSource.class)
                .url(jdbcSqlite + path.getDatabasePath() + "DATA_ENERGY.db").build();
    }

    @Bean(name = "itemJdbcTemplate")
    public JdbcTemplate itemJdbcTemplate(
            @Qualifier("itemDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "fluidJdbcTemplate")
    public JdbcTemplate fluidJdbcTemplate(
            @Qualifier("fluidDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "energyJdbcTemplate")
    public JdbcTemplate energyJdbcTemplate(
            @Qualifier("energyDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
