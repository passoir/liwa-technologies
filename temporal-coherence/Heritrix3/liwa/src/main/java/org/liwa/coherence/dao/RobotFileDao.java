package org.liwa.coherence.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.liwa.coherence.db.ConnectionPool;
import org.liwa.coherence.db.Queries;

public class RobotFileDao {
	private static final String ROBOT_FILE_SEQ = "robot_file_seq";

	private ConnectionPool connectionPool;

	private Queries queries;

	private static Object monitor = new Object();

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

	public int insertCrawledRobotFile(String url) throws SQLException {
		long id = this.getNextValue(ROBOT_FILE_SEQ);
		Connection c = connectionPool.getConnection();
		try {
			PreparedStatement ps = c.prepareStatement(queries
					.getInsertRobotFileQuery());
			ps.setLong(1, id);
			ps.setString(2, url);
			ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			ps.executeUpdate();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			c.close();
			throw e;
		}
		return (int)id;
	}

	private long getNextValue(String sequence) throws SQLException {
		Connection c = connectionPool.getConnection();
		long next = 1;
		synchronized (monitor) {
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
		}
		return next;
	}
}
