package com.jrm.app.service;

import com.jrm.app.model.CommunityPost;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class CommunityService {
    private final LinkedList<CommunityPost> posts = new LinkedList<>();

    public CommunityService() {
        posts.add(new CommunityPost("demo@user.com", "첫 로드맵 후기", "2주차까지 끝냈어요. 미션이 알차네요!"));
        posts.add(new CommunityPost("mentor@coach.com", "자료: Spring 입문", "공식 가이드와 함께 기록한 노션 링크 공유합니다."));
    }

    public List<CommunityPost> getPosts() {
        return Collections.unmodifiableList(posts);
    }

    public CommunityPost add(String author, String title, String content) {
        CommunityPost post = new CommunityPost(author, title, content);
        posts.addFirst(post);
        return post;
    }
}
