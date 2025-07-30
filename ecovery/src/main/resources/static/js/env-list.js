/**
 * 환경톡톡 목록 스크립트
 * 환경 커뮤니티 게시판의 글 목록을 비동기로 조회하고 렌더링하는 기능 수행
 * 검색, 페이징, 목록 렌더링 처리 포함
 * AJAX 방식으로 /api/env/list 호출하여 게시글 데이터를 로딩
 * @author : yukyeong
 * @fileName : env-list.js
 * @since : 250722
 * @history
 *    - 250722 | yukyeong | 게시글 목록 비동기 로딩, 검색, 페이징 기능 구현
 */

// HTML 문서의 모든 요소가 완전히 로딩된 후에 실행할 코드
document.addEventListener("DOMContentLoaded", function () { // 웹 페이지의 구조(DOM)가 모두 만들어졌을 때 이 안의 코드를 실행해라
    const searchForm = document.getElementById("searchForm"); // HTML에서 <form id="searchForm">인 요소를 찾아 searchForm 변수에 담음
    searchForm.addEventListener("submit", function (e) { // 폼이 제출될 때 실행할 코드를 등록함 (예: 검색 버튼 클릭, 엔터 입력)
        e.preventDefault(); // 기본 폼 제출 동작(새로고침)을 막음 (자바스크립트로 목록을 비동기로 불러오기 위해)
        loadEnvList(1); // 검색어가 입력되었으므로, 1페이지부터 검색 결과를 다시 불러오도록 요청
    }); // searchForm.addEventListener의 끝 — 폼 제출 이벤트 등록 완료

    loadEnvList(); // 페이지 처음 열렸을 때, 검색 조건 없이 전체 게시글 목록을 가져옴
}) // DOMContentLoaded의 끝 — HTML 로딩 후 실행되는 초기화 작업 완료


// 목록을 비동기로 불러오는 함수
function loadEnvList(pageNum = 1) { // pageNum은 현재 몇 번째 페이지를 요청할지 나타내는 매개변수 (기본값은 1)
    // 검색창에 입력한 값을 가져옴 (<input id="searchInput">)
    // .trim()은 앞뒤 공백 제거 (예: " 환경 " → "환경")
    const keyword = document.getElementById("searchInput").value.trim();

    const url = new URL("/api/env/list", window.location.origin); // API 호출을 위한 URL 객체 생성
    url.searchParams.set("pageNum", pageNum); // URL에 pageNum=1 같은 쿼리 파라미터를 추가
    url.searchParams.set("amount", 10); // url.searchParams.set("amount", 10);
    if (keyword) { // 검색어가 존재하면 (빈 값이 아니면)
        url.searchParams.set("type", "TCW"); // 검색 타입을 설정함
        url.searchParams.set("keyword", keyword); // 검색어도 URL 파라미터에 추가함 (예: keyword=환경)
    }

    fetch(url) // 위에서 만든 URL로 GET 요청을 보냄 (AJAX)
        .then(res => res.json()) // 응답을 JSON 형식으로 변환
        .then(data => { // JSON 파싱이 완료되면 그 데이터를 data라는 변수로 받아서 처리 시작
            renderPostList(data.list); // 응답에서 list라는 속성(게시글 배열)을 꺼내서 목록 렌더링 함수에 넘김
            renderPagination(data.pageMaker, data.keyword); // 응답에서 pageMaker 속성(페이징 정보)을 꺼내서 페이지 버튼 그려줌, keyword는 검색어를 유지한 채 페이징할 수 있게 넘김
        })
        .catch(err => console.error("목록 로딩 실패", err)); // 만약 fetch나 파싱 중 에러가 발생하면 콘솔에 오류 메시지 출력
}

// 서버에서 받은 게시글 목록 데이터를 화면에 출력하는 함수
function renderPostList(posts) { // posts는 게시글 리스트 배열 (예: data.list)
    const postList = document.getElementById("postList"); // HTML에서 게시글이 출력될 영역인 <div id="postList"> 요소를 가져옴
    postList.innerHTML = ""; // 기존에 표시되어 있던 게시글 목록을 모두 지움 (검색하거나 페이지 이동할 때 새로 그리기 위해)

    // 서버에서 받아온 posts 배열을 하나씩 순회함
    // 각 post 객체는 하나의 게시글 정보이고, index는 현재 순번(0부터 시작)
    posts.forEach((post, index) => {
        const postItem = `
            <div class="post-item" onclick="viewPost(${post.envId})"> <!-- onclick 속성: 이 게시글을 클릭하면 viewPost(post.envId) 함수 호출 -->
                <div class="post-number">${index + 1}</div> <!-- 리스트 상에서 게시글의 순번을 표시 (index + 1 → 1부터 시작) -->
                <div class="post-content">
                    <div class="post-title">${post.title}</div> <!-- 게시글의 제목을 보여주는 영역 -->
                    <div class="post-meta">
                        <span class="post-author">${post.nickname}</span> <!-- 작성자 이름 표시 (post.nickname) -->
                        <span>${formatDate(post.createdAt)}</span>  <!-- 게시글 작성일을 예쁘게 포맷해서 표시 (formatDate() 함수 사용) -->
                        <span>조회 ${post.viewCount}</span> <!-- 조회수 출력 (post.viewCount) -->
                    </div>
                </div>
                <div class="post-stats post-views">👁️ ${post.viewCount}</div> <!-- 오른쪽에 눈 아이콘과 함께 조회수를 다시 한 번 강조 표시 -->
            </div> <!-- post-item -->
        `;
        // // 완성한 HTML을 postList 요소의 맨 아래에 추가, 여러 게시글을 순서대로 차례차례 쌓기 위함
        postList.insertAdjacentHTML("beforeend", postItem);
    }); // forEach 반복 종료 (모든 게시글 렌더링 완료)
}

// 상세 페이지로 이동하는 함수 추가
function viewPost(envId) { // 사용자가 게시글 목록에서 글을 클릭하면 이 함수가 호출되어 상세 페이지로 이동함
    const keyword = document.getElementById("searchInput").value.trim(); // 검색창에 입력된 검색어를 가져옴
    const pageNum = document.querySelector(".page-btn.active")?.textContent || 1; // 현재 페이지 번호를 가져옴

    let url = `/env/get?envId=${envId}&pageNum=${pageNum}`; // 상세 페이지의 URL을 만듦
    if (keyword) { // 검색어가 입력되어 있다면
        url += `&type=TCW&keyword=${encodeURIComponent(keyword)}`; // URL에 검색 조건을 추가
    }
    window.location.href = url; // 최종적으로 만들어진 URL로 페이지 이동 (클릭한 게시글의 상세보기 화면으로 이동)
}


// 페이징 정보(pageMaker)를 기반으로 페이지 번호 버튼과 이전/다음 버튼을 동적으로 생성하는 함수
function renderPagination(pageMaker, keyword) { // pageMaker: 서버에서 받은 페이징 정보 객체, keyword: 검색어
    const pagination = document.getElementById("pagination"); // <div id="pagination"> 요소를 가져와서 버튼을 추가할 준비를 함
    pagination.innerHTML = ""; // 기존에 표시되어 있던 페이지 버튼들을 모두 초기화 (지움)

    // 이전 페이지 버튼
    if (pageMaker.prev) { // 이전 페이지 그룹이 있을 경우에만 < 버튼을 보이게 함
        // < 버튼을 HTML로 추가, 누르면 loadEnvList()를 호출해서 이전 페이지 그룹을 로딩
        pagination.innerHTML += `<a class="page-btn" onclick="loadEnvList(${pageMaker.startPage - 1})">‹</a>`;
    }

    // 페이지 번호들
    for (let i = pageMaker.startPage; i <= pageMaker.endPage; i++) { // 현재 페이지 그룹(startPage ~ endPage)을 반복하면서 각 페이지 번호 버튼을 생성함
        const active = i === pageMaker.cri.pageNum ? "active" : ""; // 현재 페이지와 같은 번호에는 active 클래스를 추가해서 스타일 강조
        pagination.innerHTML += `<a class="page-btn ${active}" onclick="loadEnvList(${i})">${i}</a>`; // 각 페이지 번호를 HTML로 추가, 클릭하면 해당 페이지 번호로 loadEnvList(i) 실행됨
    }

    // 다음 페이지 버튼
    if (pageMaker.next) { // 다음 페이지 그룹이 존재할 경우에만 > 버튼 표시
        // > 버튼을 HTML로 추가, 클릭 시 endPage + 1로 이동
        pagination.innerHTML += `<a class="page-btn" onclick="loadEnvList(${pageMaker.endPage + 1})">›</a>`;
    }
}

// 날짜와 시간을 예쁘게 포맷해서 "YYYY.MM.DD HH:mm" 형식으로 반환해주는 함수
function formatDate(dateTime) { // 인자 dateTime은 ISO 문자열 또는 Date 객체로 들어올 수 있음 (예: "2025-07-29T14:32:00")
    const date = new Date(dateTime); // 전달받은 dateTime을 자바스크립트 Date 객체로 변환
    const y = date.getFullYear(); // 연도를 4자리 숫자로 추출 (예: 2025)
    const m = ("0" + (date.getMonth() + 1)).slice(-2); // 월을 2자리 형식으로 추출
    const d = ("0" + date.getDate()).slice(-2); // 일을 2자리로 추출
    const h = ("0" + date.getHours()).slice(-2); // 시(hour)를 2자리로 추출 (0~23시 기준)
    const min = ("0" + date.getMinutes()).slice(-2); // 분(minute)을 2자리로 추출
    return `${y}.${m}.${d} ${h}:${min}`; // 최종 문자열을 YYYY.MM.DD HH:mm 형식으로 반환
}
