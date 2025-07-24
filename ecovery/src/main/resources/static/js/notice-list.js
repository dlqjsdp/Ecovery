/**
 * 공지사항 목록 스크립트
 * 공지사항 게시판의 글 목록을 비동기로 조회하고 렌더링하는 기능 수행
 * 검색, 페이징, 목록 렌더링 처리 포함
 * AJAX 방식으로 /api/notice/list 호출하여 게시글 데이터를 로딩
 * @author : yukyeong
 * @fileName : notice-list.js
 * @since : 250723
 */

document.addEventListener("DOMContentLoaded", function () {
    const searchForm = document.getElementById("searchForm");
    searchForm.addEventListener("submit", function (e) {
        e.preventDefault();
        loadNoticeList(1); // 검색 시 1페이지부터
    });

    loadNoticeList(); // 최초 로딩
});

function loadNoticeList(pageNum = 1) {
    const keyword = document.getElementById("keyword").value.trim();

    const url = new URL("/api/notice/list", window.location.origin);
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
        .catch(err => console.error("공지사항 목록 로딩 실패", err));
}

function renderPostList(posts) {
    const postList = document.getElementById("postList");
    postList.innerHTML = "";

    posts.forEach((post, index) => {
        const postItem = `
            <div class="post-item" onclick="viewNotice(${post.noticeId})">
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

// 공지사항 상세 페이지로 이동
function viewNotice(noticeId) {
    const keyword = document.getElementById("keyword").value.trim();
    const pageNum = document.querySelector(".page-btn.active")?.textContent || 1;

    let url = `/notice/get?noticeId=${noticeId}&pageNum=${pageNum}`;
    if (keyword) {
        url += `&type=TCW&keyword=${encodeURIComponent(keyword)}`;
    }
    window.location.href = url;
}

function renderPagination(pageMaker, keyword) {
    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    // 이전 버튼
    if (pageMaker.prev) {
        pagination.innerHTML += `<a class="page-btn" onclick="loadNoticeList(${pageMaker.startPage - 1})">‹</a>`;
    }

    // 페이지 번호
    for (let i = pageMaker.startPage; i <= pageMaker.endPage; i++) {
        const active = i === pageMaker.cri.pageNum ? "active" : "";
        pagination.innerHTML += `<a class="page-btn ${active}" onclick="loadNoticeList(${i})">${i}</a>`;
    }

    // 다음 버튼
    if (pageMaker.next) {
        pagination.innerHTML += `<a class="page-btn" onclick="loadNoticeList(${pageMaker.endPage + 1})">›</a>`;
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
