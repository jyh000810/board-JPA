package com.board.study.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.*;
import com.board.study.entity.Board;
import com.board.study.service.BoardService;

import net.bytebuddy.asm.Advice.OffsetMapping.Sort;

@Controller
public class BoardController {

	@Autowired
	private BoardService boardService;
	
	@GetMapping("/board/write")
	public String boardWriteForm() {
		
		return "boardwrite";
	}
	
	@PostMapping("/board/writepro")
	public String boardWritePro(Board board, MultipartFile file) throws Exception {
		
		boardService.write(board, file);
		return "boardlist";
	}
	
	@GetMapping("/board/list")
	public String boardList(Model model, @PageableDefault(page = 0, size = 10, sort = "id",
	direction = Direction.DESC) Pageable pageable, String searchKeyword) {
		
		Page<Board> list = null;
		if(searchKeyword == null) {
			list = boardService.boardList(pageable);  
		} else {
			list = boardService.boardSearchList(searchKeyword, pageable);
		}
		int nowPage = list.getPageable().getPageNumber();
		int startPage = Math.max(nowPage - 4, 1);
		int endPage = Math.min(nowPage + 5, list.getTotalPages());
		
		model.addAttribute("list", list);
		model.addAttribute("nowPage", nowPage);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		return "boardlist";
	}
	
	@GetMapping("/board/view")
	public String boardView(Model model, Integer id) {
		
		model.addAttribute("board", boardService.boardView(id));
		return "boardview";
	}
	
	@GetMapping("/board/delete")
	public String boardDelete(Integer id) {
		
		boardService.boardDelete(id);
		return "redirect:/board/list";
	}
	
	@GetMapping("/board/modify/{id}")
	public String boardModify(@PathVariable("id") Integer id,
								Model model) {
		
		model.addAttribute("board", boardService.boardView(id));
		return "boardmodify";
	}
	
	@PostMapping("/board/update/{id}")
	public String boardUpdate(@PathVariable("id") Integer id, Board board, MultipartFile file) throws Exception {
		Board boardTemp = boardService.boardView(id);
		
		boardTemp.setTitle(board.getTitle());
		boardTemp.setContent(board.getContent());
		boardService.write(boardTemp, file);
		return "redirect:/board/list";
	}
}
