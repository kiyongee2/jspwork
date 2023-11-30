package board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import common.JDBCUtil;
import member.Member;

public class BoardDAO {
	Connection conn = null;         //db 연결 및 종료
	PreparedStatement pstmt = null; //sql 처리
	ResultSet rs = null;            //검색한 데이터셋
	
	//게시글 목록
	public List<Board> getBoardList(){
		List<Board> boardList = new ArrayList<>();
		
		try {
			//db 연결
			conn = JDBCUtil.getConnection();
			//sql 처리
			String sql = "SELECT * FROM board ORDER BY createdate DESC";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();  //검색한 데이터셋(모음)
			while(rs.next()) {
				Board b = new Board();
				b.setBno(rs.getInt("bno"));
				b.setTitle(rs.getString("title"));
				b.setContent(rs.getString("content"));
				b.setCreateDate(rs.getTimestamp("createdate"));
				b.setModifyDate(rs.getTimestamp("modifydate"));
				b.setHit(rs.getInt("hit"));
				b.setFilename(rs.getString("filename"));
				b.setId(rs.getString("id"));
				
				boardList.add(b); //list에 b 객체 저장함
			}
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(conn, pstmt, rs);
		}
		return boardList;
	}
	
	//글쓰기 처리
	public void write(Board b) {
		try {
			//db 연결
			conn = JDBCUtil.getConnection();
			//sql 처리
			String sql = "INSERT INTO board (bno, title, content, id) "
					+ "VALUES (seq_bno.NEXTVAL, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, b.getTitle());
			pstmt.setString(2, b.getContent());
			pstmt.setString(3, b.getId());
			//sql 실행
			pstmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(conn, pstmt);
		}
	}
	
}//dao 클래스 닫기
