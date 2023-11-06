package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.dto.MemberDto;

public class MemberDao {
	public Connection conn; // DB연동 객체
	public PreparedStatement ps; // 연동된 DB에서 SQL 조작하는 객체
	public ResultSet rs; // SQL 조작 결과를 가져오는 객체

	private static MemberDao instance = new MemberDao();
	public static MemberDao getInstance() {return instance;}
	private MemberDao () {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");	
			this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeflowminds","root","1234");
			System.out.println("안내] DB연동 성공");
		} catch (Exception e) {
			System.out.println("안내] DB연동 실패 : " + e);
		}
	}
	
	public boolean signUp(MemberDto mdt){
		try {
			String sql ="insert into member(mid,mpwd) values(?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, mdt.getMid());
			ps.setString(2, mdt.getMpwd());
			return 1 == ps.executeUpdate();				
		} catch (Exception e) {
			System.out.println("가입 에러"+e);
		}
		
		return false;
	}

	public String login(MemberDto mdt){
		try {
			String sql = "select mid from member where mid = ? and mpwd = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, mdt.getMid());
			ps.setString(2, mdt.getMpwd());
			rs = ps.executeQuery();
			return rs.next() ? rs.getString(1): null;
		} catch (Exception e) {
			System.out.println("로그인 에러 "+e);
		}
		return null;
		
	}
	// 전적 업데이트
	public synchronized boolean updateRecord( MemberDto mdt ) {
		try {
			String sql = "select mno from member where mid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1,mdt.getMid());
			rs = ps.executeQuery();
			if( rs.next() ) {
				sql = "update member set mcount = mcount + 1 , mwincount = ? + mwincount where mno = ?";
				ps = conn.prepareStatement(sql);
				ps.setInt(1 , mdt.getWinCount()); ps.setInt(2, rs.getInt(1));
				ps.executeUpdate();
				return true;
			}			
		}catch(Exception e) {e.printStackTrace();}
		return false;
	}
	// 랭킹 가져오기
	public List<MemberDto> getRanking(){
		List<MemberDto> list = new ArrayList<>();
		try {// 승리 횟수 내림차순 
			String sql = "select * from member order by mwincount desc limit 5;";
			rs = conn.prepareStatement(sql).executeQuery();		
			while(rs.next()) {
				list.add( MemberDto.builder().
						mid(rs.getString("mid")).winCount(rs.getInt("mwincount")).build());
			}
		return list;
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
}
