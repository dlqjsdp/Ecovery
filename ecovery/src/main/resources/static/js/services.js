document.addEventListener("DOMContentLoaded", () => {
    const imageInput = document.getElementById('imageInput');
    const uploadZone = document.getElementById('uploadZone');
    const uploadPreview = document.getElementById('uploadPreview');
    const previewImage = document.getElementById('previewImage');
    const resetBtn = document.getElementById('resetBtn');
    const analyzeBtn = document.getElementById('analyzeBtn');

    // 이미지 미리보기
    imageInput.addEventListener('change', function (event) {
        const file = event.target.files[0];
        if (!file) return;

        // 미리보기 표시
        uploadZone.style.display = 'none';
        uploadPreview.style.display = 'block';
        resetBtn.style.display = 'inline-block';
        analyzeBtn.style.display = 'inline-block';

        const reader = new FileReader();
        reader.onload = function (e) {
            previewImage.src = e.target.result;
        };
        reader.readAsDataURL(file);
    });

    // 업로드 리셋
    window.resetUpload = function () {
        imageInput.value = '';
        uploadZone.style.display = 'block';
        uploadPreview.style.display = 'none';
        resetBtn.style.display = 'none';
        analyzeBtn.style.display = 'none';
    };

    // 드래그 앤 드롭
    uploadZone.addEventListener('dragover', function (e) {
        e.preventDefault();
        uploadZone.classList.add('dragover');
    });

    uploadZone.addEventListener('dragleave', function (e) {
        e.preventDefault();
        uploadZone.classList.remove('dragover');
    });

    uploadZone.addEventListener('drop', function (e) {
        e.preventDefault();
        uploadZone.classList.remove('dragover');
        const file = e.dataTransfer.files[0];
        if (file) {
            imageInput.files = e.dataTransfer.files;
            imageInput.dispatchEvent(new Event('change'));
        }
    });

    // 수수료 확인 버튼 클릭 (AI 서버 연동 예시)
    window.checkFeeInfo = async function () {
        const file = imageInput.files[0];
        const district = document.getElementById("districtSelect").value;

        if (!file || !district) {
            alert("이미지와 지역을 모두 선택해주세요.");
            return;
        }

        const formData = new FormData();
        formData.append("file", file);
        formData.append("regionGu", district);

        try {
            const response = await fetch("/api/disposal/initial-request", {
                method: "POST",
                body: formData
            });

            if (!response.ok) throw new Error("AI 서버 응답 실패");

            const result = await response.json();
            // 예시: 모달에 데이터 띄우기
            openClassificationModal(result.category, result.confidence);
        } catch (error) {
            console.error("AI 분석 오류:", error);
            alert("AI 분석 중 오류가 발생했습니다.");
            window.location.href = "/disposalMain"; // 메인으로 이동
        }
    };
// 1차 예측 category에 따른 2차 분류 선택지 목록
    const classificationOptions = {
        small_item: ["베개", "소형 매트", "액자", "다리미판"],
        chair: ["의자", "유아용 의자", "보행기"],
        desk_cabinet: ["책상", "서랍장", "옷장"],
        sofa_mat: ["1인용 소파", "2인용 소파", "매트리스"],
        etc_misc: ["정수기", "화분", "타이어"]
    };

// 모달을 띄우고 분류값 넣어주는 함수
    function openClassificationModal(category, confidence) {
        const modal = document.getElementById("classificationModal"); // 모달 ID 수정
        const select = document.getElementById("finalItemSelect");
        const confidenceBadge = document.getElementById("confidenceBadge");

        // 기존 옵션 초기화
        select.innerHTML = "";

        // 해당 category에 맞는 옵션 넣기
        const options = classificationOptions[category] || [];
        options.forEach(item => {
            const option = document.createElement("option");
            option.value = item;
            option.textContent = item;
            select.appendChild(option);
        });

        // 정확도 표시
        confidenceBadge.textContent = `정확도 ${confidence.toFixed(1)}%`;

        // 모달 보여주기
        modal.style.display = "block";
    }


    // 모달 닫기
    window.closeAlert = function () {
        document.getElementById("alertModal").style.display = "none";
    };

    // 사용자 확정 후 분류 저장
    window.confirmAlert = async function () {
        const selectedCategory = document.querySelector("input[name='finalItem']:checked")?.value;
        if (!selectedCategory) {
            alert("분류를 선택해주세요.");
            return;
        }

        try {
            const response = await fetch("/api/disposal/final-selection", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ finalItem: selectedCategory })
            });

            if (!response.ok) throw new Error("최종 분류 저장 실패");

            const { disposalHistoryId } = await response.json();
            window.location.href = `/disposal/${disposalHistoryId}`;
        } catch (error) {
            console.error("최종 분류 저장 오류:", error);
            alert("저장 중 오류가 발생했습니다.");
        }
    };
});
