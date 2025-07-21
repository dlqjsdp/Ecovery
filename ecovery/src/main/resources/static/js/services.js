document.addEventListener("DOMContentLoaded", () => {
    const imageInput = document.getElementById('imageInput');
    const uploadZone = document.getElementById('uploadZone');
    const uploadPreview = document.getElementById('uploadPreview');
    const previewImage = document.getElementById('previewImage');
    const resetBtn = document.getElementById('resetBtn');
    const analyzeBtn = document.getElementById('analyzeBtn');

    // 🔐 form 기본 제출 방지
    document.getElementById("uploadForm").addEventListener("submit", function (e) {
        e.preventDefault();
    });

    // 이미지 미리보기
    imageInput.addEventListener('change', function (event) {
        const file = event.target.files[0];
        if (!file) return;

        // 미리보기 표시
        uploadZone.style.display = 'none';
        uploadPreview.style.display = 'block';
        resetBtn.style.display = 'inline-block';


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
            console.log("AI 예측 결과:", result); // ✅ 추가
            console.log("AI 예측 결과:", result.aiPrediction); // ✅ 추가
            lastDisposalHistoryId = result.disposalHistoryId; // ✅ 저장

            // 아래 값이 null 또는 undefined일 경우를 대비한 방어 로직
            if (!result.aiPrediction) {
                alert("AI 예측값이 없습니다.");
                return;
            }
            // 예시: 모달에 데이터 띄우기
            openClassificationModal(result.aiPrediction);
        } catch (error) {
            console.error("AI 분석 오류:", error);
            alert("AI 분석 중 오류가 발생했습니다.");
            //window.location.href = "/disposalMain"; // 메인으로 이동
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

    function openClassificationModal(category) {
        const modal = document.getElementById("classificationModal");
        const optionsContainer = document.getElementById("classificationOptions");

        // 기존 옵션 초기화
        optionsContainer.innerHTML = "";

        // 분류별 항목
        const classificationOptions = {
            small_item: ["베개", "소형 매트", "액자", "다리미판"],
            chair: ["의자", "유아용 의자", "보행기"],
            desk_cabinet: ["책상", "서랍장", "옷장"],
            sofa_mat: ["1인용 소파", "2인용 소파", "매트리스"],
            etc_misc: ["정수기", "화분", "타이어"]
        };

        const options = classificationOptions[category] || [];

        options.forEach(item => {
            const label = document.createElement("label");
            label.style.display = "block";

            const input = document.createElement("input");
            input.type = "radio";
            input.name = "finalItem";
            input.value = item;

            label.appendChild(input);
            label.append(` ${item}`);

            optionsContainer.appendChild(label);
        });

        modal.style.display = "block";
    }

    window.confirmAlert = async function () {
        const selectedCategory = document.querySelector("input[name='finalItem']:checked")?.value;

        if (!selectedCategory) {
            alert("분류를 선택해주세요.");
            return;
        }

        if (!lastDisposalHistoryId) {
            alert("처리 이력이 없습니다. 다시 시도해주세요.");
            return;
        }

        try {
            const response = await fetch("/api/disposal/finalize-request", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    disposalHistoryId: lastDisposalHistoryId,
                    selectedFinalItem: selectedCategory // ✅ DTO의 필드명과 정확히 일치
                })
            });

            if (!response.ok) throw new Error("최종 분류 저장 실패");

            const { disposalHistoryId } = await response.json();
            window.location.href = `/disposal/${disposalHistoryId}`;
        } catch (error) {
            console.error("최종 분류 저장 오류:", error);
            alert("저장 중 오류가 발생했습니다.");
        }
    };



    window.closeAlert = function () {
        document.getElementById("classificationModal").style.display = "none";
    };




});