package com.nightchat.config;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 数据库配置类,对应于application.properties中的对应配置
 */
@Configuration
public class DataBaseConfig {
	@Autowired
	private DBConfig config;

	@Bean
	public DataSource dataSource() {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass(config.getDriver());
			dataSource.setJdbcUrl(config.getUrl());
			dataSource.setUser(config.getUser());
			dataSource.setPassword(config.getPassword());
			dataSource.setMinPoolSize(5);
			dataSource.setMaxPoolSize(50);
			dataSource.setMaxIdleTime(5000);
			dataSource.setCheckoutTimeout(20000);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			return null;
		}
		return dataSource;
	}

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource());
		sessionFactoryBean.setPackagesToScan("com.nightchat.entity");
		Properties hibernateProperties = new Properties();
		hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
		hibernateProperties.put("hibernate.show_sql", config.isShowSql());
		hibernateProperties.put("hibernate.hbm2ddl.auto", config.getHbm2ddlAuto());
		sessionFactoryBean.setHibernateProperties(hibernateProperties);
		return sessionFactoryBean;
	}

	@Bean
	public HibernateTransactionManager transactionManager() {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(sessionFactory().getObject());
		return transactionManager;
	}
}
