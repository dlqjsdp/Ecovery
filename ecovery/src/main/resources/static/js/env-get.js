/**
 * GreenCycle 환경톡톡 게시글 단건 조회 스크립트
 * URL의 envId를 기반으로 API를 통해 게시글 상세 정보를 비동기로 불러와 화면에 표시함
 * 제목, 작성자, 작성일, 조회수, 내용을 동적으로 렌더링
 *
 * @author : yukyeong
 * @fileName : env-get.js
 * @since : 250722
 * @history
     - 250722 | yukyeong | 게시글 단건 조회 비동기 처리 기능 최초 작성
 */


document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const envId = urlParams.get("envId");

    if (!envId) {
        alert("잘못된 접근입니다.");
        return;
    }

    fetch(`/api/env/get/${envId}`)
        .then(res => {
            if (!res.ok) throw new Error("데이터 조회 실패");
            return res.json();
        })
        .then(data => {
            // 날짜 포맷 처리
            const formattedDate = formatDate(data.createdAt);

            // 조회수 포맷 처리
            const views = new Intl.NumberFormat().format(data.viewCount) + '회';

            // DOM에 렌더링
            document.getElementById("post-title").textContent = data.title;
            document.getElementById("post-author").textContent = data.author?.nickname ?? "알 수 없음";
            document.getElementById("post-avatar").textContent = data.author?.avatar ?? '👤';
            document.getElementById("post-date").textContent = formattedDate;
            document.getElementById("post-views").textContent = views;
            document.getElementById("post-content").innerHTML = data.content;

            // 카테고리 렌더링
            if (data.category?.displayName) {
                document.getElementById("post-category").textContent = data.category.displayName;
                document.getElementById("post-category-wrapper").style.display = 'block';
            }
        })
        .catch(err => {
            console.error("조회 실패", err);
            alert("게시글을 불러오는 중 오류가 발생했습니다.");
        });
});

function formatDate(dateTime) {
    const date = new Date(dateTime);
    const y = date.getFullYear();
    const m = ("0" + (date.getMonth() + 1)).slice(-2);
    const d = ("0" + date.getDate()).slice(-2);
    return `${y}.${m}.${d}`;
}
