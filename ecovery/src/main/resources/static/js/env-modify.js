/**
 * GreenCycle 환경톡톡 게시글 수정 스크립트
 * - 기존 게시글의 제목, 내용, 카테고리, 이미지 등을 수정할 수 있음
 * - 등록 페이지와 구조 통일
 *
 * @author : yukyeong
 * @fileName : env-modify.js
 * @since : 250731
 * @history
 *   - 250731 | yukyeong | 게시글 수정용 스크립트 최초 작성
 *                        - 카테고리 옵션 로딩 및 선택값 반영
 *                        - 기존 제목, 내용, 카테고리 바인딩 처리
 *                        - 글자 수 실시간 카운트 기능 적용
 *                        - 수정 유효성 검사 및 PUT API 연동
 *                        - 미리보기 모달 기능 추가
 *  - 250801 | yukyeong | 본문 이미지 수정 시 삭제된 이미지 URL 추출 및 서버 전송(deleteContentImgUrls)
 *                       - 기존 vs 수정 후 이미지 목록 비교하여 삭제 대상 식별
 *                       - contentImgUrls와 함께 envFormDto로 전송하여 DB 반영 처리
 *  - 250805 | yukyeong | 삭제된 본문 이미지 식별 시 공백/대소문자 차이로 인해 삭제되지 않는 문제 해결 (normalize 함수 추가)
 */


// ✅ 전역 변수 선언
let deleteContentImgUrls = [];

document.addEventListener("DOMContentLoaded", function () {
    loadCategoriesForModify(window.envCategory);
    setupCharacterCounters();
});

// ✅ 카테고리 옵션 동적 렌더링
function loadCategoriesForModify(selectedCategory) {
    const categories = [
        { id: "policy", name: "정책/제도" },
        { id: "news", name: "환경 뉴스" },
        { id: "issue", name: "환경 이슈" },
        { id: "tips", name: "생활 속 팁" }
    ];

    const select = document.getElementById("categorySelect");
    select.innerHTML = '<option value="">카테고리를 선택하세요</option>';

    categories.forEach(category => {
        const option = document.createElement("option");
        option.value = category.id;
        option.textContent = category.name;
        if (category.id === selectedCategory) {
            option.selected = true;
        }
        select.appendChild(option);
    });
}

// ✅ 제목/내용 글자 수 실시간 카운트
function setupCharacterCounters() {
    const titleInput = document.getElementById("postTitle");

    // 제목
    titleInput.addEventListener("input", () => {
        document.getElementById("titleCount").textContent = titleInput.value.length;
    });
    document.getElementById("titleCount").textContent = titleInput.value.length;

    // 본문 (Toast UI Editor)
    editor.setHTML(window.initialContent || ""); // 초기 내용 설정
    const updateContentCount = () => {
        const plainText = editor.getHTML().replace(/<[^>]*>/g, "").trim();
        document.getElementById("contentCount").textContent = plainText.length;
    };
    editor.on("change", updateContentCount);
    updateContentCount();
}

// ✅ 유효성 검사
function validateForm() {
    let isValid = true;

    const title = document.getElementById("postTitle");
    const contentText = editor.getMarkdown().trim();
    const category = document.getElementById("categorySelect");

    document.getElementById("titleError").textContent = "";
    document.getElementById("contentError").textContent = "";
    document.getElementById("categoryError").textContent = "";

    if (title.value.trim() === "") {
        document.getElementById("titleError").textContent = "제목을 입력해주세요.";
        isValid = false;
    }
    if (contentText === "") {
        document.getElementById("contentError").textContent = "내용을 입력해주세요.";
        isValid = false;
    }
    if (!category.value) {
        document.getElementById("categoryError").textContent = "카테고리를 선택해주세요.";
        isValid = false;
    }

    return isValid;
}

// 기존 본문 이미지 URL 목록 추출
function extractImageUrlsFromHtml(html) {
    const tempDiv = document.createElement("div");
    tempDiv.innerHTML = html;
    const imgTags = tempDiv.querySelectorAll("img");
    return Array.from(imgTags).map(img => img.getAttribute("src"));
}

// ✅ 게시글 수정 처리
async function submitModifiedPost() {
    if (!validateForm()) return;

    const title = document.getElementById("postTitle").value.trim();
    const content = editor.getHTML();
    const category = document.getElementById("categorySelect").value;

    const envDto = {
        envId: window.envId,
        title: title,
        content: content,
        category: category
    };

    // ✅ (1) 수정 전/후 이미지 URL 목록 추출
    const prevImgUrls = extractImageUrlsFromHtml(window.initialContent || "");
    const newImgUrls = extractImageUrlsFromHtml(content);

    // ✅ (2) 공백, 대소문자 차이 방지를 위한 정규화 함수
    const normalize = (url) => url?.trim().toLowerCase();


    // ✅ (2) 삭제된 이미지 URL 식별
    const deleteContentImgUrls = prevImgUrls
        .map(normalize)
        .filter(url => !newImgUrls.map(normalize).includes(url));

    console.log("삭제될 본문 이미지 목록:", deleteContentImgUrls);

    // ✅ (3) 본문에 남아 있는 이미지 리스트도 서버로 전송
    const contentImgUrls = newImgUrls;

    // ✅ (4) JSON 형태로 envFormDto 구성
    const envFormDtoJson = {
        envDto,
        contentImgUrls,
        deleteContentImgUrls
    };

    const formData = new FormData();
    formData.append("envFormDto", new Blob([JSON.stringify(envFormDtoJson)], { type: "application/json" }));

    // ✅ 3. 이미지 파일 추가 (기존 로직 유지)
    const fileInput = document.getElementById("fileInput");
    if (fileInput && fileInput.files.length > 0) {
        for (let i = 0; i < fileInput.files.length; i++) {
            formData.append("envImgFiles", fileInput.files[i]);
        }
    }

    try {
        const response = await fetch(`/api/env/modify/${window.envId}`, {
            method: "PUT",
            body: formData
        });

        const result = await response.json();

        if (response.ok) {
            alert("게시글이 성공적으로 수정되었습니다.");
            window.location.href = `/env/list`;
        } else {
            alert(result.message || "수정에 실패했습니다.");
        }
    } catch (err) {
        console.error("수정 중 오류:", err);
        alert("서버 오류가 발생했습니다.");
    }
}

// ✅ 미리보기 기능
function previewPost() {
    const title = document.getElementById("postTitle").value.trim();
    const content = editor.getHTML();

    if (!title || content.replace(/<[^>]*>/g, "").trim() === "") {
        alert("미리보기를 위해 제목과 내용을 입력해주세요.");
        return;
    }

    const previewHtml = `<h2>${title}</h2>${content}`;
    document.getElementById("previewContent").innerHTML = previewHtml;
    document.getElementById("previewModal").style.display = "block";
}

function closePreview() {
    document.getElementById("previewModal").style.display = "none";
}
