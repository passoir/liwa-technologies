package org.liwa.coherence.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.liwa.coherence.db.ConnectionPool;
import org.liwa.coherence.db.Queries;

public class CrawlDao {
	private static final String CRAWLS_SEQ = "crawls_seq";

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

	public long insertCrawl(String name) throws SQLException{
		long id = this.getNextValue(CRAWLS_SEQ);
		Connection c = connectionPool.getConnection();
		try {
			PreparedStatement ps = c.prepareStatement(queries.getInsertCrawlQuery());
			ps.setLong(1, id);
			ps.setString(2, name);
			ps.executeUpdate();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			c.close();
			throw e;
		}
		return id;
	}
	
	public long insertRecrawl(String name, long recrawledId) throws SQLException{
		long id = this.getNextValue(CRAWLS_SEQ);
		Connection c = connectionPool.getConnection();
		try {
			PreparedStatement ps = c.prepareStatement(queries.getInsertRecrawlQuery());
			ps.setLong(1, id);
			ps.setString(2, name);
			ps.setLong(3, recrawledId);
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


	private long getNextValue(String sequence) throws SQLException {
		Connection c = connectionPool.getConnection();
		long next = 1;
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(queries.getSequenceStart() + " "
					+ sequence + " " + queries.getSequenceEnd());
			next = -1;
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
