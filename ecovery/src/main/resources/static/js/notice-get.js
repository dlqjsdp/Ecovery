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
    const noticeId = urlParams.get("noticeId");

    if (!noticeId) {
        alert("잘못된 접근입니다.");
        return;
    }

    fetch(`/api/notice/get/${noticeId}`)
        .then(res => {
            if (!res.ok) throw new Error("데이터 조회 실패");
            return res.json();
        })
        .then(data => {
            document.getElementById("postTitle").textContent = data.title;
            document.getElementById("postAuthor").textContent = data.nickname;
            document.getElementById("postDate").textContent = formatDate(data.createdAt);
            document.getElementById("postViews").textContent = data.viewCount;
            document.getElementById("postContent").textContent = data.content;
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
