package org.liwa.coherence.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.liwa.coherence.db.ConnectionPool;
import org.liwa.coherence.db.Queries;

public class SiteDao {

	private static final String SITES_SEQ = "sites_seq";
	
	private ConnectionPool connectionPool;

	private Queries queries;

	public Queries getQueries() {
		return queries;
	}

	public void setQueries(Queries queries) {
		this.queries = queries;
	}

	public ConnectionPool getConnectionPool() {
		return connectionPool;
	}

	public void setConnectionPool(ConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}
	
	public long getSiteIdForUrl(String url) throws SQLException {
		String domain = this.getDomain(url);
		if(domain == null || domain.length() == 0){
			return -1;
		}
		long id = this.getSiteId(domain);
		if (id == -1) {
			id = this.insertSite(domain);
		}
		return id;
	}
	
	private long getSiteId(String domain) throws SQLException {
		Connection c = connectionPool.getConnection();
		long id = -1;
		try {
			PreparedStatement ps = c.prepareStatement(queries.getSiteQueries());
			ps.setString(1, domain);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				id = rs.getLong(1);
			}
			rs.close();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.close();
			throw e;
			
		}
		return id;
	}
	

	private long insertSite(String domain) throws SQLException {
		long id = this.getNextValue(SITES_SEQ);
		Connection c = connectionPool.getConnection();
		try {
			PreparedStatement ps = c.prepareStatement(queries.getInsertSiteQueries());
			ps.setLong(1, id);
			ps.setString(2, domain);
			ps.executeUpdate();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.close();
			throw e;
			
		}
		return id;
	}

	private String getDomain(String url) {
		int schemeIndex = url.indexOf("://");
		String domain = "";
		if (schemeIndex != -1) {
			String withoutScheme = url.substring(schemeIndex + 3);
			int slashIndex = withoutScheme.indexOf("/");
			if (slashIndex != -1) {
				domain = withoutScheme.substring(0, slashIndex);
			} else {
				domain = withoutScheme;
			}
		}
		return domain;
	}


	private long getNextValue(String sequence) throws SQLException {
		Connection c = connectionPool.getConnection();
		long next = 0;
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(queries.getSequenceStart() + " "
					+ sequence + " " + queries.getSequenceEnd());
			
			if (r.next()) {
				next = r.getLong(1);
			}
			r.close();
			s.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.close();
			throw e;
			
		}
		return next;
	}
}
