package site.metacoding.red.web.dto.response.boards;

public class PagingDto {

	private Integer startnum;
	private Integer totalCount; //totalCount를 알아야 totalPage가 나옴
	private Integer totalPage; // =23/한페이지당 갯수 -> 23/10=2
	private Integer currentPage;
	private boolean isLast;
	private boolean isFirst;
}
