package org.liwa.coherence.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.liwa.coherence.db.ConnectionPool;
import org.liwa.coherence.db.Queries;

public class UrlDao {
	private static final String URLS_SEQ = "urls_seq";

	private static Object monitor = new Object();

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

	public long getUrlId(String url) throws SQLException {
		long id = -1;
		synchronized (monitor) {
			id = this.getExistingUrlId(url);
			if (id == -1) {
				id = this.insertUrl(url);
			}
		}
		return id;
	}

	public long getExistingUrlId(String url) throws SQLException {
		long id = -1;
		Connection c = connectionPool.getConnection();
		try {
			PreparedStatement ps = c.prepareStatement(queries.getUrlQuery());
			ps.setString(1, url);
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

	private long insertUrl(String url) throws SQLException {
		long id = this.getNextValue(URLS_SEQ);
		Connection c = connectionPool.getConnection();
		try {
			PreparedStatement ps = c.prepareStatement(queries
					.getInsertUrlQuery());
			ps.setLong(1, id);
			ps.setString(2, url);
			ps.executeUpdate();
			ps.close();
			c.commit();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(url);
			e.printStackTrace();
			c.close();
			System.exit(-1);
			// throw e;

		}
		return id;
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
