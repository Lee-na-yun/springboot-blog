package site.metacoding.red.web.dto.response.boards;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PagingDto {
	private Integer blockCount;
	private Integer currentBlock;
	private Integer startPageNum;
	private Integer lastPageNum;

	//private Integer startnum;
	private Integer totalCount; //totalCount를 알아야 totalPage가 나옴
	private Integer totalPage; // =23/한페이지당 갯수 -> 23/10=2
	private Integer currentPage;
	private boolean isLast;
	private boolean isFirst;
	
	public void makeBlockInfo() {
		this.blockCount = 5;

		this.currentBlock = currentPage / blockCount;
		this.startPageNum = 1 + blockCount * currentBlock;
		this.lastPageNum = 5 + blockCount * currentBlock;

		if (totalPage < lastPageNum) {
			this.lastPageNum = totalPage;
		}
	}
}
