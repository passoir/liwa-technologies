package org.liwa.coherence.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import org.liwa.coherence.db.ConnectionPool;
import org.liwa.coherence.db.Queries;
import org.liwa.coherence.sitemap.CompressedUrl;
import org.liwa.coherence.sitemap.SitemapChangeRateProvider;

public class PublishedUrlDao {
	private static final String PUBLISHED_URLS_SEQ = "published_urls_seq";

	private static Object monitor = new Object();

	private ConnectionPool connectionPool;

	private int robotFileId = -1;

	private Queries queries;

	public int getRobotFileId() {
		return robotFileId;
	}

	public void setRobotFileId(int robotFileId) {
		this.robotFileId = robotFileId;
	}

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

	public String loadUrl(int id) throws SQLException {
		String query = queries.getPublishedUrlByIdQuery();
		String url = "";
		Connection c = connectionPool.getConnection();
		try {
			PreparedStatement ps = c.prepareStatement(query);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				url = rs.getString(1);
			}
			rs.close();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			c.close();
			throw e;
		}
		return url;
	}

	public CompressedUrl getCompressedUrl(String url) throws SQLException {
		String query = queries.getCompressedUrlQuery();
		CompressedUrl compressedUrl = new CompressedUrl();
		if (robotFileId != -1) {
			Connection c = connectionPool.getConnection();
			try {
				PreparedStatement ps = c.prepareStatement(query);
				ps.setInt(1, robotFileId);
				ps.setString(2, url);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					int urlId = rs.getInt(1);
					String frequency = rs.getString(2);
					double priority = rs.getDouble(3);
					compressedUrl.setId(urlId);
					compressedUrl
							.setChangeRate(SitemapChangeRateProvider.CHANGE_RATE_MAP
									.get(frequency.trim()));
					compressedUrl.setPriority(priority);
				}
				rs.close();
				ps.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				c.close();
				throw e;
			}
		}
		return compressedUrl;
	}

	public int getUrlId(String url) throws SQLException {
		String query = queries.getPublishedUrlIdQuery();
		int urlId = -1;
		if (robotFileId != -1) {
			Connection c = connectionPool.getConnection();
			try {
				PreparedStatement ps = c.prepareStatement(query);
				ps.setInt(1, robotFileId);
				ps.setString(2, url);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					urlId = rs.getInt(1);
				}
				rs.close();
				ps.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				c.close();
				throw e;
			}
		}
		return urlId;
	}

	public int insertPublishedUrl(String url, String frequency,
			double priority, Date lastModified) throws SQLException {
		long id = getUrlId(url);
		if (id == -1) {
			id = this.getNextValue(PUBLISHED_URLS_SEQ);
			Connection c = connectionPool.getConnection();
			try {
				PreparedStatement ps = c.prepareStatement(queries
						.getInsertPublishedUrlQuery());
				ps.setLong(1, id);
				ps.setString(2, url);
				ps.setInt(3, robotFileId);
				ps.setString(4, frequency);
				ps.setDouble(5, priority);
				if (lastModified == null) {
					ps.setNull(6, Types.TIMESTAMP);
				} else {
					ps.setTimestamp(6, new Timestamp(lastModified.getTime()));
				}
				ps.executeUpdate();
				ps.close();
				c.commit();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				// System.out.println(url);
				// e.printStackTrace();
				c.close();
				// System.exit(-1);
				throw e;

			}
		}else{
			return -1;
		}
		return (int) id;
	}

	private long getNextValue(String sequence) throws SQLException {
		Connection c = connectionPool.getConnection();
		long next = 0;
		synchronized (monitor) {
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
				// e.printStackTrace();
				c.close();
				throw e;

			}
		}
		return next;
	}
}
