package com.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.Result;
import com.pet.entity.Comment;
import com.pet.entity.Post;
import com.pet.mapper.CommentMapper;
import com.pet.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired private PostMapper postMapper;
    @Autowired private CommentMapper commentMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "9") Integer pageSize) {
        Page<Post> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 0).orderByDesc(Post::getCreateTime);
        Page<Post> result = postMapper.selectPage(page, wrapper);
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("list", result.getRecords());
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Post post = postMapper.selectById(id);
        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>().eq(Comment::getPostId, id).orderByAsc(Comment::getCreateTime));
        Map<String, Object> data = new HashMap<>();
        data.put("post", post);
        data.put("comments", comments);
        return Result.success(data);
    }

    @PostMapping
    public Result<String> add(@RequestBody Post post, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        post.setUserId(userId);
        post.setUsername(username);
        post.setStatus(0);
        post.setLikeCount(0);
        postMapper.insert(post);
        return Result.success("发布成功", null);
    }

    @PostMapping("/{id}/like")
    public Result<String> like(@PathVariable Long id) {
        Post post = postMapper.selectById(id);
        post.setLikeCount(post.getLikeCount() + 1);
        postMapper.updateById(post);
        return Result.success("点赞成功", null);
    }

    @PostMapping("/{id}/comment")
    public Result<String> addComment(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        Comment c = new Comment();
        c.setPostId(id);
        c.setContent(body.get("content"));
        c.setUserId((Long) request.getAttribute("userId"));
        c.setUsername((String) request.getAttribute("username"));
        commentMapper.insert(c);
        return Result.success("评论成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        postMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
