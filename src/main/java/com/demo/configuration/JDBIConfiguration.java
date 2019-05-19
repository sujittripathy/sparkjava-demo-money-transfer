package com.demo.configuration;

import org.h2.jdbcx.JdbcConnectionPool;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.util.Properties;

public class JDBIConfiguration {
	private static Jdbi jdbi;

	static {
		try {
			Properties properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db/db.properties"));
			JdbcConnectionPool connectionPool =
					JdbcConnectionPool.create(properties.getProperty("h2.url"),
							properties.getProperty("h2.username"),properties.getProperty("h2.password"));
			jdbi = Jdbi.create(connectionPool);
			jdbi.installPlugin(new SqlObjectPlugin());
			jdbi.open().createScript(ClasspathSqlLocator.findSqlOnClasspath("db/schema")).execute();
			jdbi.open().createScript(ClasspathSqlLocator.findSqlOnClasspath("db/data")).execute();
		} catch (Exception e) {
			throw new RuntimeException("Unable to initialize Database");
		}
	}

	public static Jdbi getJdbi() {
		return jdbi;
	}
}
