package site.metacoding.red.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import site.metacoding.red.domain.boards.Boards;
import site.metacoding.red.domain.boards.BoardsDao;
import site.metacoding.red.domain.users.Users;
import site.metacoding.red.web.dto.request.boards.UpdateDto;
import site.metacoding.red.web.dto.request.boards.WriteDto;
import site.metacoding.red.web.dto.response.boards.MainDto;
import site.metacoding.red.web.dto.response.boards.PagingDto;

@RequiredArgsConstructor
@Controller
public class BoardsController {

	private final HttpSession session;
	private final BoardsDao boardsDao;

	@PostMapping("/boards/{id}/update")  // update만 영속화-변경-수정!
	public String update(@PathVariable Integer id, UpdateDto updateDto) {
		Users principal = (Users) session.getAttribute("principal");
		// 1. 영속화
		Boards boardsPS = boardsDao.findById(id); 

		// 비정상 요청 체크
		if (boardsPS == null) {
			return "errors/badPage";
		}
		// 인증 체크
		if (principal == null) {
			return "redirect:/loginForm";
		}
		// 권한 체크( 세션: principal.getId() & boardsPS의 userId를 비교 )
		if (principal.getId() != boardsPS.getUsersId()) {
			return "redirect:/boards/" + id;
		}
		
		// 2. 변경
		boardsPS.글수정(updateDto);
		// 3. 수행
		//boardsDao.update(updateDto.toEntity(id));
		boardsDao.update(boardsPS);
		
		return "redirect:/boards/" + id;
	}

	@GetMapping("/boards/{id}/updateForm") // boards에 업데이트할 수 있는 폼을 주세요.
	public String updateForm(@PathVariable Integer id, Model model) {
		Users principal = (Users) session.getAttribute("principal");
		Boards boardsPS = boardsDao.findById(id);

		// 비정상 요청 체크
		if (boardsPS == null) {
			return "errors/badPage";
		}
		// 인증 체크
		if (principal == null) {
			return "redirect:/loginForm";
		}
		// 권한 체크( 세션: principal.getId() & boardsPS의 userId를 비교 )
		if (principal.getId() != boardsPS.getUsersId()) {
			return "redirect:/boards/" + id;
		}

		model.addAttribute("boards", boardsPS);
		return "boards/updateForm"; // model에 담아서 가야함
	}

	@PostMapping("/boards/{id}/delete") // 원래 주소에 동사는 적으면 안됨!
	public String deleteBoards(@PathVariable Integer id) {
		// 1.영속화 시키기
		Boards boardsPS = boardsDao.findById(id);
		Users principal = (Users) session.getAttribute("principal");
		// 비정상 요청 체크
		if (boardsPS == null) { // if는 비정상 로직을 하게 해서 걸러내는 필터 역할을 하는게 좋다.
			return "errors/badPage"; // 글 상세보기 주소로
		}
		// 화면의 수정,삭제 버튼이 본인만 보여야함
		// 인증 체크
		if (principal == null) {
			return "redirect:/loginForm";
		}
		// 권한 체크( 세션: principal.getId() & boardsPS의 userId를 비교 )
		if (principal.getId() != boardsPS.getUsersId()) {
			return "redirect:/boards/" + id;
		}

		boardsDao.delete(id); // 핵심 기능
		return "redirect:/";
	}

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

	// http://localhost:8000/
	// http://localhost:8000/?page=0
	// 1번째 ?page=0&keyword=스프링
	@GetMapping({ "/", "/boards" })
	public String getBoardList(Model model, Integer page, String keyword) { // 0 -> 0, 1->10, 2->20
		System.out.println("dddddddddd : keyword : "+keyword);
		if (page == null) {
			page = 0;
		}
		int startNum = page * 3; // 1. 수정함
		
		if (keyword == null || keyword.isEmpty()) {
			System.out.println("=================================");
			List<MainDto> boardsList = boardsDao.findAll(startNum);
			PagingDto paging = boardsDao.paging(page, null);
			paging.makeBlockInfo(keyword);

			model.addAttribute("boardsList", boardsList);
			model.addAttribute("paging", paging);	
		} else {

			List<MainDto> boardsList = boardsDao.findSearch(startNum, keyword);
			PagingDto paging = boardsDao.paging(page, keyword);
			paging.makeBlockInfo(keyword);

			model.addAttribute("boardsList", boardsList);
			model.addAttribute("paging", paging);
		}
		return "boards/main";
		

		
		
	}

//	@GetMapping({ "/", "/boards" })
//	public String getBoardList(Model model) {
//		List<MainDto> boardsList = boardsDao.findAll();
//		model.addAttribute("boardsList", boardsList);
//		return "boards/main";
//	}

	@GetMapping("/boards/{id}")
	public String getBoardDetail(@PathVariable Integer id, Model model) {
		model.addAttribute("boards", boardsDao.findById(id));
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
