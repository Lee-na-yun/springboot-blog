package site.metacoding.red.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import site.metacoding.red.domain.boards.BoardsDao;
import site.metacoding.red.domain.users.Users;
import site.metacoding.red.web.dto.request.boards.WriteDto;
import site.metacoding.red.web.dto.response.boards.MainDto;

@RequiredArgsConstructor
@Controller
public class BoardsController {

	private final HttpSession session;
	private final BoardsDao boardsDao;

	@PostMapping("/boards") // writeBoards코드 리팩토링
	public String writeBoards(WriteDto writeDto) {
		// 1) 세션에 접근해서 세션값을 확인한다. 그때 Users로 다운캐스팅하고 키값은 princial로 한다.
		// 2) principal이 null인지 확인하고 null이면 loginForm을 리다이렉션해준다.
		// 3) boardsDao에 접근해서 insert 메소드를 호출한다.
		// 3)-조건1: Dto를 entity로 변환해서 인수로 담아준다.
		// 3)-조건2: entity에는 세션의 principal의 getId가 필요하다.

		Users principal = (Users) session.getAttribute("principal");

		if (principal == null) { // 부가적인 코드 --> 다 들어가는 코드니까 나중에 메소드로 빼야함!
			return "redirect:/loginForm";
		} // else 적으면 더러운 코드!

		// 핵심코드 // WriteDto & Boards 에 적음
		boardsDao.insert(writeDto.toEntity(principal.getId())); // boards에 인서트할건데 사용자로부터 받은값인dto를 인서트할거고...
		// 모든걸 메서드화시켜서 읽히게 만들어야 함
		return "redirect:/";
	}

//	@PostMapping("/boards")
//	public String writeBoards(WriteDto writeDto) { //WriteDto는 통신에 들어오는 데이터가 아님
//		Users principal = (Users) session.getAttribute("principal");
//		Boards boards = new Boards();
//		boards.setTitle(writeDto.getTitle());
//		boards.setContent(writeDto.getContent());
//		boards.setUsersId(principal.getId());
//		boardsDao.insert(boards); //WriteDto는 userId가 없기 때문에 boardsDao로 받아야함
//		return "redirect:/";
//	} // writeDto에 to ENTITY를 만들어서 쓰는게 위 코드보다 편함! // 인증체크도 해야함

	@GetMapping({ "/", "/boards" })
	public String getBoardList(Model model) {
		List<MainDto> boardsList = boardsDao.findAll();
		model.addAttribute("boardsList", boardsList);
		return "boards/main";
	}

	@GetMapping("/boards/{id}")
	public String getBoardList(@PathVariable Integer id, Model model) {
		model.addAttribute("boards",boardsDao.findById(id));
		return "boards/detail";
	}

	@GetMapping("/boards/writeForm")
	public String writeForm() { // 들고올 데이터가 없으므로 model 필요 없음!
		Users principal = (Users) session.getAttribute("principal"); // UsersController에서 login할 때 넣어줬으니 가져와야함!
		if (principal == null) {
			return "redirect:/loginForm";
		}
		return "boards/writeForm";
		// 글쓰기 페이지는 인증만 되면 됨!

	}
}
