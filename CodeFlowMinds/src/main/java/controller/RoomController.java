package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.dao.MemberDao;
import model.dto.MemberDto;
import model.dto.RoomDto;
import service.QuestionRead;

/**
 * Servlet implementation class RoomController
 */
@WebServlet("/RoomController")
public class RoomController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RoomController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		response.setContentType("application/json;charset=UTF-8");
		String _t = request.getParameter("type");
		
		ObjectMapper objectMapper = new ObjectMapper();
		String result = "";
		if(_t.equals("getList"))
		{
			for(int i=0;i < ServerSocket.roomList.size();i++ )
            {
                
                if(ServerSocket.roomList.get(i) == null) continue;
                if(ServerSocket.roomList.get(i).getMemberVector().isEmpty())
                {
                    ServerSocket.roomList.set(i, null);
                }
            }			
			// 계층형 Dto 안에 클라이언트들에게 필요 없는 정보들이 담겨있어 새로운 리스트에 필요한 정보들만 담아서 전송
			ArrayList<RoomDto> resp = new ArrayList<RoomDto>();
			ServerSocket.roomList.forEach( r->{ if( r == null ) { return;}
			
			resp.add(RoomDto.builder()
					.roomNum(r.getRoomNum())
					.allRound(r.getAllRound())
					.limitPlayer(r.getLimitPlayer())
					.nowPlayer(r.getMemberVector().size())
					.nowStatus(r.isNowStatus())
					.pwd(r.getPwd())
					.build());

			});
			
			response.getWriter().print(objectMapper.writeValueAsString(resp));
		}
		// 방장확인
		else if( _t.equals("isAdmin")) {
			int rno = Integer.parseInt(request.getParameter("rno"));
			String id = request.getParameter("id");
			Vector<RoomDto> list = ServerSocket.roomList;
			for(int i = 0; i < list.size(); i++) {
				
				if(list.get(i) == null) break;
				
				
				// 방번호 같고 멤버벡터 0번째 인덱스가 같은 id일 때 true 응답
				if( rno == list.get(i).getRoomNum() && id.equals(list.get(i).getMemberVector().get(0).getMid())) {
					response.getWriter().print(true); return;
				}			
			}
			response.getWriter().print(false);			
		}
		// 방이 이미 실행상태인지
		else if(_t.equals("isAlreadyStart")) {
			
			int rno = Integer.parseInt(request.getParameter("rno"));
			Vector<RoomDto> list = ServerSocket.roomList;
			for(int i = 0; i < list.size(); i++) {
				
				if(list.get(i) == null) break;
				
				// 방번호 같고 현재 상태가 true면 true 응답
				if( rno == list.get(i).getRoomNum() && list.get(i).isNowStatus()) {
					response.getWriter().print(true); return; 
				}			
			}
			response.getWriter().print(false);	
		}
		// 랭킹 가져오기
		else if(_t.equals("getRanking")) {
			response.getWriter().print(objectMapper.writeValueAsString(MemberDao.getInstance().getRanking()));						
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int limitPlayer = Integer.parseInt( request.getParameter("limitPlayer"));
		int allRound = Integer.parseInt(request.getParameter("allRound")) ;
		String pwd = request.getParameter("pwd");
		// 반환된 방 번호
		int rno = 0;
		
		for(int i = 0; i < 50; i++) {
			
			if( ServerSocket.roomList.get(i) == null ) {
				
				ServerSocket.roomList.set( i ,
						RoomDto.builder().
							roomNum(i).allRound(allRound).pwd(pwd).limitPlayer(limitPlayer).
								memberVector(new Vector<>()).build());
				rno = i;
				System.out.println("방생성 완료 " + ServerSocket.roomList.get(i).toString());
				break;
			}			
		}
		
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().print(rno);	
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int rno = Integer.parseInt(request.getParameter("rno"));
		String type = request.getParameter("type");
		
		System.out.println("pwd"+ServerSocket.roomList.get(rno).getPwd());
		
		// 방 입장 전 비밀번호 확인 로직
		if("checkRpwd".equals(type)) {
			
			String rpwd = request.getParameter("rpwd");
			// 비밀번호 확인과 더불어 제한인원을 초과하는지도 판단해야함
			// false면 비밀번호 불일치, true면 입장, overSize면 제한인원 초과
			String result = "false";
			for(int i=0;i<ServerSocket.roomList.size();i++) {
				
				if(ServerSocket.roomList.get(i) == null)
					continue;
				if(ServerSocket.roomList.get(i).getRoomNum() == rno && rpwd.equals(ServerSocket.roomList.get(i).getPwd())) {System.out.println("트루니???");
					result = "true";}
				if(ServerSocket.roomList.get(i).getMemberVector().size() >= ServerSocket.roomList.get(i).getLimitPlayer())
					result = "overSize";
				
			}
			
			response.getWriter().print(result);	
			
		}else if( "updateRecord".equals(type)) {
			ServerSocket.roomList.get(rno).getMemberVector().forEach( m ->{
				m.setWinCount(0);
			});
			String mid = request.getParameter("mid");
			int index = 0;
			int highWinCount = 0;
					Vector<MemberDto> list = ServerSocket.roomList.get(rno).getMemberVector();
			for( int i = 0; i < list.size(); i++ ) {
				if( list.get(i).getWinCount() > highWinCount  ) 							
					highWinCount = list.get(i).getWinCount();						
				
				if( mid.equals(list.get(i).getMid()) ) 
					index = i;
				
			}
			
			// 게임 안에서 가장 승리 카운트가 높다면 1로 설정
			boolean result = 
				MemberDao.getInstance().updateRecord(
					MemberDto.builder().
						mid(mid).winCount(
									( list.get(index).getWinCount() == highWinCount ) ? 1 : 0 ).build());
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().print(result);
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	public void init(ServletConfig config) throws ServletException {
		// 서버소켓 배열 null로 초기화
		for( int i=0; i < 50; i++)
			{ServerSocket.roomList.add(null);}
		
		QuestionRead.categoryList.add("sport");
		QuestionRead.categoryList.add("fruit");
		QuestionRead.categoryList.add("fourIdioms");
		QuestionRead.categoryList.add("food");
		QuestionRead.categoryList.add("random");
	      
	}
}
