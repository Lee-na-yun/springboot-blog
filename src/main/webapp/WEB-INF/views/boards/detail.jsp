<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="../layout/header.jsp"%>

<div class="container">
	<br /> <br />
	
	<br>
	<div>
		<h3>${boards.title}</h3>
	</div>
	<hr />

	<div>${boards.content}</div>
	 
	 <br>
	 <div class="d-flex">
	<form>
		<button class="btn btn-dark">수정하러가기</button>
	</form>
	<form action="/boards/${boards.id}/delete" method="post">
		<button class="btn btn-danger">삭제</button>
	</form>
	</div>

</div>

<%@ include file="../layout/footer.jsp"%>

