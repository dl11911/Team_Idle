package com.idle.shoppingmall.Controller.ControllerView.ProductViews;

import com.idle.shoppingmall.Controller.ControllerView.ProductViews.DTO.CommentListResponse;
import com.idle.shoppingmall.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentListViewController {

    private final CommentService commentService;
    private final int PAGESIZE = 10;
    @PostMapping("/GET/commentList")
    public ResponseEntity<List<CommentListResponse>> commentList(@RequestParam(name="id", required = false) Long id,
                                                                 @RequestParam(name="page", defaultValue = "1") int page){
        int startpage = (page-1) * PAGESIZE;
        int endpage = startpage + PAGESIZE;
        List<CommentListResponse> comments = commentService.findCommentList(id, startpage, endpage);

        return ResponseEntity.ok().body(comments);
    }
}