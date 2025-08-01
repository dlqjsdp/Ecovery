/**
 * GreenCycle 환경톡톡 게시글 단건 조회 스크립트
 * URL의 noticeId를 기반으로 API를 통해 게시글 상세 정보를 비동기로 불러와 화면에 표시함
 * 제목, 작성자, 작성일, 조회수, 내용을 동적으로 렌더링
 *
 * @author : yukyeong
 * @fileName : notice-get.js
 * @since : 250722
 * @history
 - 250722 | yukyeong | 게시글 단건 조회 비동기 처리 기능 최초 작성
 - 250730 | yukyeong | 전체 리팩토링:
 - 카테고리 코드 → 이름 변환(categoryMap) 적용
 - 작성자 닉네임 비동기 렌더링
 - 글 삭제 버튼 이벤트 바인딩 기능 추가
 - 본문 내용에 포함된 이미지 자동 렌더링 처리
 - 250731 | yukyeong | 수정일이 있을 경우 '작성일:' → '수정일:'로 표시 및 날짜 변경 처리
 */


const categoryMap = {
    "important": "중요 공지",
    "service": "서비스",
    "maintenance": "점검",
    "event": "이벤트",
    "general": "일반"
};

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
            // 날짜 포맷 처리
            const displayDate = (data.updatedAt && data.updatedAt !== data.createdAt)
                ? formatDate(data.updatedAt)
                : formatDate(data.createdAt);

            const dateLabel = document.querySelector(".notice-date span:first-child");
            if (data.updatedAt && data.updatedAt !== data.createdAt) {
                dateLabel.textContent = "수정일:";
            }

            const views = new Intl.NumberFormat().format(data.viewCount) + '회';

            // 게시글 정보 렌더링
            document.getElementById("notice-title").textContent = data.title;
            document.getElementById("notice-author").textContent = data.nickname ?? "알 수 없음";
            document.getElementById("notice-avatar").textContent = data.avatar ?? '👨‍💼';
            document.getElementById("notice-role").textContent = data.role ?? "";
            document.getElementById("notice-date").textContent = displayDate;
            document.getElementById("notice-views").textContent = views;
            document.getElementById("notice-content").innerHTML = data.content;

            // ✅ JS에서 category 코드 → 표시 이름 매핑
            if (data.category) {
                const displayName = categoryMap[data.category] || "기타";
                document.getElementById("notice-category").textContent = displayName;
                document.getElementById("notice-category-wrapper").style.display = 'block';
            }

            // ✅ 글 수정 버튼 href 설정
            const editBtn = document.getElementById("editBtn");
            if (editBtn) {
                editBtn.href = `/notice/modify/${data.noticeId}`;
            }

            // ✅ 글 삭제 버튼 이벤트 바인딩
            const deleteBtn = document.getElementById("deleteBtn");
            if (deleteBtn) {
                deleteBtn.addEventListener("click", function () {
                    deletePost(data.noticeId);
                });
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

function deletePost(noticeId) {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    fetch(`/api/notice/remove/${noticeId}`, {
        method: 'DELETE'
    })
        .then(res => {
            if (!res.ok) throw new Error("삭제 실패");
            return res.json();
        })
        .then(data => {
            alert(data.message); // "삭제되었습니다."
            window.location.href = "/notice/list";
        })
        .catch(err => {
            console.error("삭제 오류", err);
            alert("삭제 중 오류가 발생했습니다.");
        });
}

