package AITetris.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import AITetris.Model.Rank;

public class RankDBController {

    private Connection conn;
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1k5rmf\\ht1a";
    private static final String URL = "jdbc:mysql://alcoholcoding.com/alcoholcoding";
    private static final String TABLE = "Tetris";

    public RankDBController() {

	System.out.println("DB URL	 : " + URL);
	System.out.println("USERNAME : " + USERNAME);
	System.out.println("TABLE	 : " + TABLE);

	try {
	    Class.forName("com.mysql.jdbc.Driver");
	    conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	    System.out.println("클래스 적재 실패");
	} catch (SQLException e) {
	    e.printStackTrace();
	    System.out.println("연결 실패");
	}
    }

    public void insertRank(Rank rank) {
	String sql = "insert into " + TABLE + " values(?, ?, ?);";
	PreparedStatement pstmt = null;

	try {

	    pstmt = conn.prepareStatement(sql);
	    pstmt.setString(1, rank.getName());
	    pstmt.setString(2, rank.getType());
	    pstmt.setInt(3, rank.getPoint());

	    System.out.println("SQL : " + pstmt.toString());

	    pstmt.executeUpdate();

	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (pstmt != null && !pstmt.isClosed())
		    pstmt.close();
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	}

    }

    public void deleteAll() {
	String sql = "delete from " + TABLE + " where name like '*'";
	PreparedStatement pstmt = null;
	try {
	    pstmt = conn.prepareStatement(sql);
	    pstmt.executeUpdate();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (pstmt != null && !pstmt.isClosed())
		    pstmt.close();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    public List<Rank> selectAll() {
	String sql = "select * from " + TABLE + ";";
	PreparedStatement pstmt = null;

	List<Rank> list = new ArrayList<Rank>();

	try {
	    pstmt = conn.prepareStatement(sql);
	    ResultSet re = pstmt.executeQuery();

	    while (re.next()) {
		Rank rank = new Rank(re.getString("Name"), re.getString("Type"), re.getInt("Point"));
		list.add(rank);
	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (pstmt != null && !pstmt.isClosed())
		    pstmt.close();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	return list;
    }

}
