package site.metacoding.red.web;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import site.metacoding.red.domain.users.Users;

@RequiredArgsConstructor
@Controller
public class BoardsController {
	
	private final HttpSession session;

	@GetMapping({"/","/boards"})
	public String getBoardList() {
		return "boards/main";
	}
	
	@GetMapping("/boards/{id}")
	public String getBoardList(@PathVariable Integer id) {
		return "boards/detail";
	}
	
	@GetMapping("/boards/writeForm")
	public String writeForm() {
		Users principal = (Users) session.getAttribute("principal"); // UsersController에서 login할 때 넣어줬으니 가져와야함!
		if(principal == null) {
			return "redirect:/loginForm";
		}else {
			return "boards/writeForm";
		} // 글쓰기 페이지는 인증만 되면 됨!
		
	}
}
