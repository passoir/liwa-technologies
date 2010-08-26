package org.liwa.coherence.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;

public class ConnectionPool implements InitializingBean{
	private static Map<String, DataSource> map = new HashMap<String, DataSource>();
	
	
	private DataSource dataSource;
	
	private String dataSourceClass;
	private String dataSourceName;
	private String serverName;
	private String databaseName;
	private String user;
	private String password;
	private int maxConnections;
	
	
	
	public String getDataSourceClass() {
		return dataSourceClass;
	}

	public void setDataSourceClass(String dataSourceClass) {
		this.dataSourceClass = dataSourceClass;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public Connection getConnection() throws SQLException{
		return dataSource.getConnection();
	}
	
	

	public void afterPropertiesSet() throws Exception {
		if(map == null){
			map = new HashMap<String, DataSource>();
		}
		dataSource = map.get(dataSourceName);
		if(dataSource == null){
			Class dataSourceClazz = Class.forName(dataSourceClass);
			dataSource = (DataSource) Class.forName(dataSourceClass).newInstance();
			Class[] stringParameter = new Class[]{String.class};
			Class[] intParameter = new Class[]{int.class};
			dataSourceClazz.getMethod("setUser", stringParameter).invoke(dataSource, new Object[]{user});
			dataSourceClazz.getMethod("setDataSourceName", stringParameter).invoke(dataSource, new Object[]{dataSourceName});
			dataSourceClazz.getMethod("setDatabaseName", stringParameter).invoke(dataSource, new Object[]{databaseName});
			dataSourceClazz.getMethod("setServerName", stringParameter).invoke(dataSource, new Object[]{serverName});
			dataSourceClazz.getMethod("setPassword", stringParameter).invoke(dataSource, new Object[]{password});
			dataSourceClazz.getMethod("setMaxConnections", intParameter).invoke(dataSource, new Object[]{maxConnections});
			map.put(dataSourceName, dataSource);
		}		
	}
}
