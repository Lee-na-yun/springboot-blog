package site.metacoding.red.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import site.metacoding.red.domain.users.Users;
import site.metacoding.red.domain.users.UsersDao;
import site.metacoding.red.web.dto.request.users.JoinDto;
import site.metacoding.red.web.dto.request.users.LoginDto;

@RequiredArgsConstructor		// 디펜던시인젝션
@Controller
public class UsersController {
	
	private final HttpSession session; //스프링이 서버시작시에 ioc컨테이너에 보관함
	private final UsersDao usersDao;		// 컴퍼지션
	
	@GetMapping("/logout")
	public String logout() {	// = 세션 영역을 날려버리는것 = invalidate
		session.invalidate();
		return "redirect:/";
	}
	
	@PostMapping("/login")		// 로그인만 예외로 select인데 post로 함!
	public String login(LoginDto loginDto) {
		Users usersPS = usersDao.login(loginDto);
		if(usersPS != null) { // 로직 체크 1.로그인 인증됨
			session.setAttribute("principal", usersPS);	// 무슨타입을 넣을지 몰라서 object타입을 넣음 ==> 꺼낼때는 users타입으로 return됨(다운캐스팅)
			return "redirect:/";
		}else {	// 인증안됨
			return "redirect:/loginForm";
		}
		 // 메인페이지 돌려주면 됨
	}
	
//	@PostMapping("/login")		// 로그인만 예외로 select인데 post로 함!
//	public String login(LoginDto loginDto, HttpServletRequest request) {
//		Users usersPS = usersDao.login(loginDto);
//		if(usersPS != null) { // 로직 체크 1.로그인 인증됨
//			HttpSession session = request.getSession(); // session 영역에 접근하는 주소 <-접근하려면 request가 필요함
//			return "redirect:/";
//		}else {	// 인증안됨
//			return "redirect:/loginForm";
//		}
//		 // 메인페이지 돌려주면 됨
//	}
	
	@PostMapping("/join")
	public String join(JoinDto joinDto) {
		usersDao.insert(joinDto);
		return "redirect:/loginForm"; 	// 리다이렉트 해야함! 밑에 loginForm이 만들어져 있으니
	}

	@GetMapping("/loginForm")
	public String loginForm() {
		return "users/loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "users/joinForm";
	}
}
