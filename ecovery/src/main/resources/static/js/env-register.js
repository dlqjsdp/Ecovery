/**
 * GreenCycle 환경톡톡 게시글 등록 스크립트
 * 게시글 등록 페이지에서 카테고리 로딩, 유효성 검사, 이미지 삽입, 미리보기 등
 * 다양한 작성 인터랙션 기능을 제공함
 *
 * @author : yukyeong
 * @fileName : env-register.js
 * @since : 250730
 * @history
 *   - 250730 | yukyeong | 게시글 작성 페이지 전용 스크립트 최초 작성
                            - 카테고리 동적 로딩(loadCategories)
                            - 제목/내용 글자 수 카운팅(setupCharacterCounters)
                            - 유효성 검사 및 등록 처리(submitPost)
                            - 이미지 첨부 시 <img> 태그 본문 삽입 처리
                            - 임시 이미지 업로드 API 연동(/api/env/upload-temp)
                            - 미리보기 모달 기능(previewPost)
 */

document.addEventListener("DOMContentLoaded", function () {
    window.onbeforeunload = null; // 제출 성공 시 호출하면 창 닫아도 경고 안뜸
    localStorage.removeItem("draft");         // 임시글 제거
    loadCategories();                         // 카테고리 옵션 추가
    setupCharacterCounters();                 // 글자 수 표시
    setupFormSubmit();                        // 유효성 검사 후 submit
    setupImageInsert();                       // 이미지 첨부 시 <img> 삽입
});

// ✅ 카테고리 동적 로딩
function loadCategories() {
    const categories = [
        { id: "policy", name: "정책/제도" },
        { id: "news", name: "환경 뉴스" },
        { id: "issue", name: "환경 이슈" },
        { id: "tips", name: "생활 속 팁" }
    ];

    const select = document.getElementById("categorySelect");
    categories.forEach(category => {
        const option = document.createElement("option");
        option.value = category.id;
        option.textContent = category.name;
        select.appendChild(option);
    });
}

// ✅ 제목/내용 글자 수 실시간 카운팅
function setupCharacterCounters() {
    const titleInput = document.getElementById("postTitle");
    const contentTextarea = document.getElementById("postContent");

    titleInput.addEventListener("input", () => {
        document.getElementById("titleCount").textContent = titleInput.value.length;
    });

    contentTextarea.addEventListener("input", () => {
        document.getElementById("contentCount").textContent = contentTextarea.value.length;
    });
}

// ✅ 유효성 검사
function validateForm() {
    let isValid = true;

    const title = document.getElementById("postTitle");
    const content = document.getElementById("postContent");
    const category = document.getElementById("categorySelect");

    document.getElementById("titleError").textContent = "";
    document.getElementById("contentError").textContent = "";
    document.getElementById("categoryError").textContent = "";

    if (title.value.trim() === "") {
        document.getElementById("titleError").textContent = "제목을 입력해주세요.";
        isValid = false;
    }
    if (content.value.trim() === "") {
        document.getElementById("contentError").textContent = "내용을 입력해주세요.";
        isValid = false;
    }
    if (!category.value) {
        document.getElementById("categoryError").textContent = "카테고리를 선택해주세요.";
        isValid = false;
    }

    return isValid;
}

// ✅ 폼 제출 이벤트 바인딩
function setupFormSubmit() {
    const form = document.getElementById("writeForm");
    form.addEventListener("submit", function (e) {
        e.preventDefault();
        submitPost();
    });
}

// ✅ 미리보기
function previewPost() {
    const title = document.getElementById("postTitle").value.trim();
    const content = document.getElementById("postContent").value.trim();

    if (!title || !content) {
        alert("미리보기를 위해 제목과 내용을 입력해주세요.");
        return;
    }

    const previewHtml = `
        <h2>${title}</h2>
        <p>${content.replace(/\n/g, "<br>")}</p>
    `;

    document.getElementById("previewContent").innerHTML = previewHtml;
    document.getElementById("previewModal").style.display = "block";
}

function closePreview() {
    document.getElementById("previewModal").style.display = "none";
}

// ✅ 이미지 첨부 시 <img src="...">
function setupImageInsert() {
    const fileInput = document.getElementById("fileInput");

    fileInput.addEventListener("change", function () {
        const files = fileInput.files;
        const formData = new FormData();

        for (let i = 0; i < files.length; i++) {
            formData.append("imageFiles", files[i]); // ✅ 백엔드에서 받을 필드명에 맞춰야 함
        }

        // ✅ 업로드용 임시 API 호출 (등록용이 아님!)
        fetch("/api/env/upload-temp", {
            method: "POST",
            body: formData
        })
            .then(res => res.json())
            .then(fileNames => {
                const contentBox = document.getElementById("postContent");
                fileNames.forEach(fileName => {
                    const imageUrl = `/images/env/${fileName}`;
                    contentBox.value += `\n<img src="${imageUrl}" alt="첨부 이미지">\n`;
                });
            })
            .catch(err => {
                console.error("임시 이미지 업로드 실패:", err);
                alert("이미지 업로드 중 오류 발생");
            });
    });
}

// ✅ 등록 처리
function submitPost() {
    if (!validateForm()) return;

    const title = document.getElementById("postTitle").value.trim();
    const content = document.getElementById("postContent").value.trim();
    const categoryId = document.getElementById("categorySelect").value;

    window.onbeforeunload = null;

    const formData = new FormData();
    formData.append("envFormDto", new Blob([
        JSON.stringify({
            envDto: { title, content, category: categoryId }
        })
    ], { type: "application/json" }));

    const fileInput = document.getElementById("fileInput");
    if (fileInput.files.length > 0) {
        for (let i = 0; i < fileInput.files.length; i++) {
            formData.append("envImgFiles", fileInput.files[i]);
        }
    } else {
        formData.append("envImgFiles", new Blob()); // 빈 Blob
    }

    fetch("/api/env/register", {
        method: "POST",
        body: formData
    })
        .then(response => {
            if (!response.ok) throw new Error("등록 실패");
            return response.json();
        })
        .then(data => {
            alert("게시글이 성공적으로 등록되었습니다.");
            window.location.href = "/env/list";
        })
        .catch(error => {
            console.error("등록 중 오류:", error);
            alert("게시글 등록에 실패했습니다.");
        });
}
