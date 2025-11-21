package com.jrm.app.service;

import com.jrm.app.model.JobPost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class CrawlerService {
    private final CopyOnWriteArrayList<JobPost> cachedPosts = new CopyOnWriteArrayList<>();

    public List<JobPost> getCachedPosts() {
        return Collections.unmodifiableList(cachedPosts);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void crawlDaily() {
        try {
            cachedPosts.clear();
            cachedPosts.addAll(parseSampleHtml());
        } catch (IOException e) {
            // In a production setting this would be logged and sent to observability tools
            e.printStackTrace();
        }
    }

    private List<JobPost> parseSampleHtml() throws IOException {
        List<JobPost> results = new ArrayList<>();
        String sampleHtml = new String(this.getClass().getClassLoader()
                .getResourceAsStream("sample-posts.html").readAllBytes(), StandardCharsets.UTF_8);
        Document doc = Jsoup.parse(sampleHtml);
        Elements posts = doc.select("article.job");
        for (Element post : posts) {
            String id = post.attr("data-id");
            String title = post.selectFirst("h3").text();
            String company = post.selectFirst(".company").text();
            String url = post.selectFirst("a.apply").attr("href");
            String location = post.selectFirst(".location").text();
            LocalDate deadline = LocalDate.parse(post.selectFirst(".deadline").text());
            List<String> keywords = post.select(".keywords li").eachText();
            results.add(new JobPost(id, company, title, url, location, deadline, keywords));
        }
        return results;
    }
}
