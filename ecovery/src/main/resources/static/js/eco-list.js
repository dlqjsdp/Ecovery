/**
 * 환경톡톡 목록 스크립트
 * 환경 커뮤니티 게시판의 글 목록을 비동기로 조회하고 렌더링하는 기능 수행
 * 검색, 페이징, 목록 렌더링 처리 포함
 * AJAX 방식으로 /api/env/list 호출하여 게시글 데이터를 로딩
 * @author : yukyeong
 * @fileName : eco-list.js
 * @since : 250722
 * @history
 *    - 250722 | yukyeong | 게시글 목록 비동기 로딩, 검색, 페이징 기능 구현
 */

document.addEventListener("DOMContentLoaded", function () {
    const searchForm = document.getElementById("searchForm");
    searchForm.addEventListener("submit", function (e) {
        e.preventDefault();
        loadEnvList(1); // 검색 시 1페이지부터
    });

    loadEnvList(); // 최초 로딩
});

function loadEnvList(pageNum = 1) {
    const keyword = document.getElementById("keyword").value.trim();

    const url = new URL("/api/env/list", window.location.origin);
    url.searchParams.set("pageNum", pageNum);
    url.searchParams.set("amount", 10);
    if (keyword) {
        url.searchParams.set("type", "TCW");
        url.searchParams.set("keyword", keyword);
    }

    fetch(url)
        .then(res => res.json())
        .then(data => {
            renderPostList(data.list);
            renderPagination(data.pageMaker, data.keyword);
        })
        .catch(err => console.error("목록 로딩 실패", err));
}

function renderPostList(posts) {
    const postList = document.getElementById("postList");
    postList.innerHTML = "";

    posts.forEach((post, index) => {
        const postItem = `
            <div class="post-item" onclick="viewPost(${post.envId})">
                <div class="post-number">${index + 1}</div>
                <div class="post-content">
                    <div class="post-title">${post.title}</div>
                    <div class="post-meta">
                        <span class="post-author">${post.nickname}</span>
                        <span>${formatDate(post.createdAt)}</span>
                        <span>조회 ${post.viewCount}</span>
                    </div>
                </div>
                <div class="post-stats post-views">👁️ ${post.viewCount}</div>
            </div>
        `;
        postList.insertAdjacentHTML("beforeend", postItem);
    });
}

// 상세 페이지로 이동하는 함수 추가
function viewPost(envId) {
    const keyword = document.getElementById("keyword").value.trim();
    const pageNum = document.querySelector(".page-btn.active")?.textContent || 1;

    let url = `/env/get?envId=${envId}&pageNum=${pageNum}`;
    if (keyword) {
        url += `&type=TCW&keyword=${encodeURIComponent(keyword)}`;
    }
    window.location.href = url;
}

function renderPagination(pageMaker, keyword) {
    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    // 이전 페이지 버튼
    if (pageMaker.prev) {
        pagination.innerHTML += `<a class="page-btn" onclick="loadEnvList(${pageMaker.startPage - 1})">‹</a>`;
    }

    // 페이지 번호들
    for (let i = pageMaker.startPage; i <= pageMaker.endPage; i++) {
        const active = i === pageMaker.cri.pageNum ? "active" : "";
        pagination.innerHTML += `<a class="page-btn ${active}" onclick="loadEnvList(${i})">${i}</a>`;
    }

    // 다음 페이지 버튼
    if (pageMaker.next) {
        pagination.innerHTML += `<a class="page-btn" onclick="loadEnvList(${pageMaker.endPage + 1})">›</a>`;
    }
}

function formatDate(dateTime) {
    const date = new Date(dateTime);
    const y = date.getFullYear();
    const m = ("0" + (date.getMonth() + 1)).slice(-2);
    const d = ("0" + date.getDate()).slice(-2);
    const h = ("0" + date.getHours()).slice(-2);
    const min = ("0" + date.getMinutes()).slice(-2);
    return `${y}.${m}.${d} ${h}:${min}`;
}
