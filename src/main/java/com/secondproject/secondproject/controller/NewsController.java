package com.secondproject.secondproject.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final RestTemplate restTemplate;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    /**
     * 예시 호출:
     *   GET /api/news?query=대학교&size=4
     *
     * 프론트에서는 이 엔드포인트만 호출하면 되고,
     * 이 컨트롤러가 네이버 뉴스 API를 대신 호출해서 결과 JSON 그대로 넘겨줌.
     */
    @GetMapping
    public ResponseEntity<String> getNews(
            @RequestParam(defaultValue = "대학교") String query,
            @RequestParam(name = "size", defaultValue = "4") int size
    ) {

        // 네이버 뉴스 검색 API URL 만들기
        String url = UriComponentsBuilder
                .fromHttpUrl("https://openapi.naver.com/v1/search/news.json")
                .queryParam("query", query)
                .queryParam("display", size)  // 가져올 개수
                .queryParam("sort", "date")   // 최신순
                .toUriString();

        // 헤더에 Client ID / Secret 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        // 네이버 API 호출
        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        // 받은 JSON 그대로 프론트로 전달
        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }
}
