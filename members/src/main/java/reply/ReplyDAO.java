package reply;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.JDBCUtil;

//댓글을 추가, 수정, 삭제, 검색하는 클래스
public class ReplyDAO {
	Connection conn = null;         //db 연결 및 종료
	PreparedStatement pstmt = null; //sql 처리
	ResultSet rs = null;            //검색한 데이터셋
	
	//댓글 목록
	public List<Reply> getReplyList(int bno){
		List<Reply> replyList = new ArrayList<>();
		try {
			conn = JDBCUtil.getConnection();
			String sql = "SELECT * FROM reply WHERE bno = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bno);
			//sql 처리
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(conn, pstmt, rs);
		}
		return replyList;
	}
}
