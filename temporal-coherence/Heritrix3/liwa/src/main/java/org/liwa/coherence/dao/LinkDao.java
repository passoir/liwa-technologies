package org.liwa.coherence.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.liwa.coherence.db.ConnectionPool;
import org.liwa.coherence.db.Queries;
import org.liwa.coherence.pojo.Link;

public class LinkDao {

	private ConnectionPool connectionPool;

	private Queries queries;

	public ConnectionPool getConnectionPool() {
		return connectionPool;
	}

	public void setConnectionPool(ConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}

	public Queries getQueries() {
		return queries;
	}

	public void setQueries(Queries queries) {
		this.queries = queries;
	}

	public ResultSet getLinks(Connection c, long pageId) throws SQLException {
		PreparedStatement ps = c.prepareStatement(queries.getLinksQuery());
		ps.setLong(1, pageId);
		return ps.executeQuery();
	}

	public void insertLinks(List<Link> links) throws SQLException {
		Connection c = connectionPool.getConnection();
		try {
			PreparedStatement ps = c
					.prepareStatement(queries.getInsertLinksQuery());
			int i = 0;
			int batchSize = 5000;
			boolean executed = false;
			for (Link l : links) {
				this.addParameters(ps, l);
				i++;
				if (i > batchSize) {
					ps.executeBatch();
					executed = true;
					i = 0;
				} else {
					ps.addBatch();
					executed = false;
				}
			}
			if (!executed) {
				ps.executeBatch();
			}
			ps.close();
			c.commit();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.close();
			throw e;
			
		}
	}

	private void addParameters(PreparedStatement ps, Link l)
			throws SQLException {
		if (l.getCrawlId() != -1 && l.getFromPageId() != -1
				&& l.getFromUrlId() != -1 && l.getToUrlId() != -1
				&& l.getFromSiteId() != -1) {
			ps.setLong(1, l.getFromPageId());
			ps.setLong(2, l.getCrawlId());
			ps.setLong(3, l.getFromUrlId());
			ps.setLong(4, l.getFromSiteId());
			if (l.getToSiteId() != -1) {
				ps.setLong(5, l.getToSiteId());
			} else {
				ps.setNull(5, Types.NUMERIC);
			}
			ps.setLong(6, l.getToUrlId());
			ps.setString(7, l.getType());
		}
	}

}
