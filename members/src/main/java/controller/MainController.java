package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import board.Board;
import board.BoardDAO;
import member.Member;
import member.MemberDAO;

@WebServlet("*.do") // '/'이하의 경로에서 do로 끝나는 확장자는 모두 허용
public class MainController extends HttpServlet {
	
	private static final long serialVersionUID = 10L;
	//필드
	MemberDAO mDAO;
	BoardDAO bDAO;
	
    public MainController() { //생성자
        mDAO = new MemberDAO();
        bDAO = new BoardDAO();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//한글 인코딩
		request.setCharacterEncoding("utf-8");
		//응답할 컨텐츠 유형
		response.setContentType("text/html; charset=utf-8");
		
		//경로 설정
		//uri - 컨텍스트(/) + 파일(.do)
		String uri = request.getRequestURI();
		System.out.println(uri);
		//command 패턴
		String command = uri.substring(uri.lastIndexOf("/"));
		System.out.println(command);
		//이동 페이지
		String nextPage = "";
		//세션 객체 생성
		HttpSession session = request.getSession();
		
		//view에 출력 객체 생성
		PrintWriter out = response.getWriter();
		
		if(command.equals("/memberlist.do")) {
			//회원정보를 db에서 가져옴
			List<Member> memberList = mDAO.getMemberList();
			//모델 생성
			request.setAttribute("memberList", memberList);
			//이동할 페이지
			nextPage = "/member/memberlist.jsp";
		}else if(command.equals("/joinform.do")) {
			//get 방식
			nextPage = "/member/joinform.jsp";
		}else if(command.equals("/insertmember.do")) {
			//빈 회원 객체를 생성해서 데이터를 받아서 세팅
			//폼 데이터 받기
			String id = request.getParameter("id");
			String passwd = request.getParameter("passwd");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			String gender = request.getParameter("gender");
			//객체에 데이터 세팅
			Member m = new Member();
			m.setId(id);
			m.setPasswd(passwd);
			m.setName(name);
			m.setEmail(email);
			m.setGender(gender);
			//db에 저장함
			mDAO.insertMember(m);
			//회원 가입후 이동
			nextPage = "/index.jsp";
		}else if(command.equals("/memberview.do")) {
		   String id = request.getParameter("id");
			
			Member member = mDAO.getMember(id);
			
			//모델 생성
			request.setAttribute("member", member);
			
			nextPage = "/member/memberview.jsp";
		}else if(command.equals("/loginform.do")) { //로그인 폼페이지 이동
			nextPage = "/member/loginform.jsp";
		}else if(command.equals("/login.do")) { //로그인 처리
			//아이디와 비번 파라미터 받기
			String id = request.getParameter("id");
			String passwd = request.getParameter("passwd");
			//빈 객체를 생성해서 아이디와 비번 세팅해줌
			Member m = new Member();
			m.setId(id);
			m.setPasswd(passwd);
			//로그인 인증
			boolean result = mDAO.checkLogin(m);
			if(result) { //result가 true이면 세션 발급
				session.setAttribute("sessionId", id);
				//로그인 후 페이지 이동
				nextPage = "/index.jsp";
			}else {
				//에러를 모델로 보내기
				String error = "아이디나 비밀번호를 다시 확인해주세요.";
				request.setAttribute("error", error);
				//에러 발생후 페이지 이동
				nextPage ="/member/loginform.jsp";
			}	
		}else if(command.equals("/logout.do")) {
			session.invalidate(); //모든 세션 삭제
			nextPage = "/index.jsp";
		}
		
		//게시판
		if(command.equals("/boardlist.do")) {
			//db에서 list를 가져옴
			List<Board> boardList = bDAO.getBoardList();
			//모델로 생성
			request.setAttribute("boardList", boardList);
			
			nextPage = "/board/boardlist.jsp";
		}else if(command.equals("/writeform.do")) {
			nextPage = "/board/writeform.jsp";
		}else if(command.equals("/write.do")) {
			//폼 데이터 받기
			String title = request.getParameter("title");
			String content = request.getParameter("content");
			//세션 가져오기
			String id = (String)session.getAttribute("sessionId");
			
			//db에 저장
			Board b = new Board();
			b.setTitle(title);
			b.setContent(content);
			b.setId(id);
			//write 메서드 실행
			bDAO.write(b);
		}
		
		if(command.equals("/write.do")) {
			//새로고침하면 게시글 중복 생성 문제 해결
			response.sendRedirect("/boardlist.do");
		}else {
			RequestDispatcher dispatch = 
					request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
		}
	}
}
