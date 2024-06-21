package com.sist.dao;

import java.sql.*;
import java.util.*;


public class GoodsDAO {
	private static GoodsDAO dao;
	private Connection conn;
	private PreparedStatement ps;
	private final String URL = "jdbc:oracle:thin:@192.168.10.124:1521:XE";
	public GoodsDAO() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (Exception e) {}
	}
	public void getConnection() {
		try { 
			conn = DriverManager.getConnection(URL, "hr3", "happy");
		} catch (Exception e) {}
	}
	public void disconnection() {
		try {
			if(ps != null) ps.close();
			if(conn != null) conn.close();
		} catch (Exception e) {}
	}
	public static GoodsDAO newInstance() {
		if(dao == null)
			dao = new GoodsDAO();
		return dao;
	}
	public List<GoodsVO> goodsListData(int page){
		List<GoodsVO> list = new ArrayList<GoodsVO>();
		try {
			getConnection();
			String sql = "SELECT goods_name, goods_poster, num "
					+ "FROM(SELECT goods_name, goods_poster, rownum as num "
					+ "FROM(SELECT goods_name, goods_poster "
					+ "FROM goods_all ORDER BY no ASC)) "
					+ "WHERE num BETWEEN ? AND ?";
			ps = conn.prepareStatement(sql);
			int rowSize = 12;
			int start = (rowSize * page) - (rowSize - 1);
			int end = rowSize * page;
			ps.setInt(1, start);
			ps.setInt(2, end);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				GoodsVO vo = new GoodsVO();
				vo.setName(rs.getString(1));
				vo.setPoster(rs.getString(2));
				list.add(vo);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			disconnection();
		}
		return list;
	}
	public int goodsTotalPage() {
		int page = 0;
		try {
			getConnection();
			String sql = "SELECT CEIL(COUNT(*) / 12.0) FROM goods_all";
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			rs.next();
			page = rs.getInt(1);
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			disconnection();
		}
		return page;
	}
}

