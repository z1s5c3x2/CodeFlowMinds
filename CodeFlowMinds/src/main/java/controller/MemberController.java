package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.dao.MemberDao;
import model.dto.MemberDto;

/**
 * Servlet implementation class MemberController
 */
@WebServlet("/MemberController")
public class MemberController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MemberController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		
        
        if( "isLogin".equals(request.getParameter("type"))) {
        	response.setContentType("application/json;charset=utf-8");
        	if(request.getSession().getAttribute("loginId") == null)
        		response.getWriter().print(false);
        	else 
        		response.getWriter().print(true);  
        }
        else if("login".equals(request.getParameter("type"))) {
	        String loginMid = MemberDao.getInstance().login(MemberDto.builder().mid(request.getParameter("mid"))
					.mpwd(request.getParameter("mpwd")).build());
	        if(loginMid != null)
	        {
	        	request.getSession().setAttribute("loginId", loginMid);
	        	response.getWriter().print(loginMid);
	        }else
	        {
	        	response.getWriter().print("fail");
	        }
        }
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			//회원가입
	        response.setContentType("application/json;charset=UTF-8");
			response.getWriter().print(MemberDao.getInstance().signUp(MemberDto.builder().mid(request.getParameter("mid"))
					.mpwd(request.getParameter("mpwd")).build()));
		}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 로그아웃
		request.getSession().setAttribute("loginId", null);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().print(true);
	}

}
