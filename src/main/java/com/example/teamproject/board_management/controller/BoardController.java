package com.example.teamproject.board_management.controller;

import com.example.teamproject.board_management.entity.Board;
import com.example.teamproject.board_management.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    final private BoardService boardService;

    @GetMapping("/list")
    public List<Board> boardList () {
        log.info("boardList()");
        return boardService.list();
    }
}