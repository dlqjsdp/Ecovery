// =========================
// 전역 변수 (페이지 전체에서 사용)
// =========================

// 지역 데이터 (구를 선택하면 동이 나타남)
const regionData = {
    "강남구": [
        "개포동", "논현동", "대치동", "도곡동", "삼성동", "세곡동", "신사동", "압구정동",
        "역삼동", "율현동", "일원동", "자동차운전면허시험장", "청담동"
    ],
    "강동구": [
        "강일동", "고덕동", "길동", "둔촌동", "명일동", "상일동", "성내동", "암사동", "천호동"
    ],
    "강북구": [
        "미아동", "번동", "수유동", "우이동"
    ],
    "강서구": [
        "가양동", "강서동", "개화동", "공항동", "과해동", "내발산동", "등촌동", "마곡동",
        "방화동", "염창동", "오곡동", "오쇠동", "외발산동", "화곡동"
    ],
    "관악구": [
        "남현동", "봉천동", "신림동"
    ],
    "광진구": [
        "광장동", "구의동", "군자동", "능동", "자양동", "중곡동", "화양동"
    ],
    "구로구": [
        "가리봉동", "개봉동", "고척동", "구로동", "궁동", "신도림동", "오류동", "온수동", "천왕동"
    ],
    "금천구": [
        "가산동", "독산동", "시흥동"
    ],
    "노원구": [
        "공릉동", "상계동", "월계동", "중계동", "하계동"
    ],
    "도봉구": [
        "도봉동", "방학동", "쌍문동", "창동"
    ],
    "동대문구": [
        "답십리동", "동답동", "장안동", "전농동", "제기동", "청량리동", "회기동", "휘경동", "이문동", "용두동"
    ],
    "동작구": [
        "노량진동", "본동", "사당동", "상도동", "신대방동", "흑석동"
    ],
    "마포구": [
        "공덕동", "구수동", "노고산동", "당인동", "대흥동", "도화동", "동교동", "마포동", "망원동", "상수동",
        "상암동", "서교동", "성산동", "신공덕동", "신수동", "아현동", "연남동", "염리동", "오른쪽여의도", "용강동", "중동", "창전동", "토정동", "합정동"
    ],
    "서대문구": [
        "남가좌동", "북가좌동", "북아현동", "신촌동", "연희동", "천연동", "충정로3가", "홍은동", "홍제동"
    ],
    "서초구": [
        "내곡동", "반포동", "반포본동", "방배동", "서초동", "신원동", "양재동", "염곡동", "우면동", "원지동", "잠원동"
    ],
    "성동구": [
        "금호동1가", "금호동2가", "금호동3가", "금호동4가", "도선동", "마장동", "사근동", "상왕십리동", "성수동1가",
        "성수동2가", "송정동", "옥수동", "응봉동", "하왕십리동", "행당동"
    ],
    "성북구": [
        "길음동", "돈암동", "동선동1가", "동선동2가", "동선동3가", "동소문동1가", "동소문동2가", "동소문동3가", "동소문동4가",
        "동소문동5가", "동소문동6가", "동소문동7가", "보문동1가", "보문동2가", "보문동3가", "보문동4가", "보문동5가", "보문동6가",
        "삼선동1가", "삼선동2가", "삼선동3가", "삼선동4가", "삼선동5가", "상월곡동", "성북동", "성북동1가", "성북동2가", "안암동1가",
        "안암동2가", "안암동3가", "안암동4가", "안암동5가", "장위동", "정릉동", "종암동", "하월곡동"
    ],
    "송파구": [
        "가락동", "거여동", "마천동", "문정동", "방이동", "삼전동", "석촌동", "송파동", "신천동", "오금동", "잠실동", "장지동", "풍납동"
    ],
    "양천구": [
        "목동", "신월동", "신정동"
    ],
    "영등포구": [
        "당산동1가", "당산동2가", "당산동3가", "당산동4가", "당산동5가", "당산동6가", "대림동", "도림동", "문래동1가",
        "문래동2가", "문래동3가", "문래동4가", "문래동5가", "문래동6가", "신길동", "양평동1가", "양평동2가", "양평동3가",
        "양평동4가", "양평동5가", "양평동6가", "여의도동", "영등포동", "영등포동1가", "영등포동2가", "영등포동3가",
        "영등포동4가", "영등포동5가", "영등포동6가"
    ],
    "용산구": [
        "갈월동", "남영동", "동빙고동", "동자동", "문배동", "보광동", "산천동", "서계동", "서빙고동", "신계동", "용문동", "용산동2가",
        "용산동3가", "용산동5가", "원효로1가", "원효로2가", "원효로3가", "원효로4가", "이촌동", "이태원동", "주성동", "청암동", "한강로1가",
        "한강로2가", "한강로3가", "한남동", "후암동"
    ],
    "은평구": [
        "갈현동", "구산동", "대조동", "불광동", "수색동", "신사동", "역촌동", "응암동", "증산동", "진관동"
    ],
    "종로구": [
        "가회동", "견지동", "경운동", "계동", "공평동", "관훈동", "교남동", "교북동", "구기동", "궁정동", "권농동", "낙원동",
        "내수동", "내자동", "누상동", "누하동", "당주동", "도렴동", "돈의동", "동숭동", "명륜1가", "명륜2가", "명륜3가",
        "명륜4가", "묘동", "봉익동", "부암동", "사간동", "사직동", "삼청동", "서린동", "세종로", "소격동", "송현동", "수송동",
        "숭인동", "신교동", "신문로1가", "신문로2가", "신영동", "안국동", "연건동", "연지동", "예지동", "옥인동", "와룡동",
        "운니동", "원남동", "원서동", "익선동", "인사동", "인의동", "장사동", "재동", "적선동", "종로1가", "종로2가",
        "종로3가", "종로4가", "종로5가", "종로6가", "중학동", "창성동", "창신동", "청진동", "청운동", "체부동", "충신동",
        "평동", "필운동", "홍지동", "홍파동", "화동", "효자동", "효제동", "훈정동"
    ],
    "중구": [
        "광희동1가", "광희동2가", "남대문로1가", "남대문로2가", "남대문로3가", "남대문로4가", "남산동1가", "남산동2가", "남산동3가",
        "남창동", "남학동", "다동", "만리동1가", "만리동2가", "명동1가", "명동2가", "무교동", "묵정동", "방산동", "봉래동1가",
        "봉래동2가", "북창동", "산림동", "삼각동", "서소문동", "소공동", "수표동", "순화동", "신당동", "쌍림동", "예관동",
        "예장동", "오장동", "을지로1가", "을지로2가", "을지로3가", "을지로4가", "을지로5가", "을지로6가", "을지로7가",
        "의주로1가", "의주로2가", "인현동1가", "인현동2가", "장교동", "장충동1가", "장충동2가", "저동1가", "저동2가",
        "정동", "주교동", "주자동", "중림동", "진관동", "초동", "충무로1가", "충무로2가", "충무로3가", "충무로4가",
        "충무로5가", "태평로1가", "태평로2가", "필동1가", "필동2가", "필동3가", "황학동", "회현동1가", "회현동2가", "회현동3가"
    ],
    "중랑구": [
        "망우동", "면목동", "묵동", "상봉동", "신내동", "중화동"
    ]
};

// 새로 업로드될 이미지들을 저장할 배열 (기존 이미지는 DOM으로 관리)
let newUploadedFiles = [];
let deletedImageIds = []; // 기존 이미지 삭제용 배열

let existingCount = 0;

// =========================
// 페이지가 로드되면 실행되는 함수
// =========================
document.addEventListener('DOMContentLoaded', function() {
    console.log('무료나눔 수정 페이지가 로드되었습니다!');

    // 1. Thymeleaf에서 free 객체가 넘어왔는지 확인
    if (typeof window.free === 'undefined' || window.free === null) {
        console.error('오류: Thymeleaf에서 free 객체가 JavaScript로 전달되지 않았습니다. window.free를 확인해주세요.');
        // 이 경우, 기본값 또는 에러 처리를 할 수 있습니다.
    }

    // 2. 폼 초기화  (Thymeleaf에서 채워진 데이터를 기반으로 JS 상태 업데이트)
    initializeFormData();

    // 3. 이벤트 등록
    setupEventListeners();

    // 4. 파일 선택 이벤트 → 이미지 처리 연결
    const fileInput = document.getElementById('imageInput');
    if (fileInput) {
        fileInput.addEventListener('change', function () {
            handleNewImageFiles(Array.from(this.files));
            this.value = '';  // 파일 input 초기화 (동일한 파일 다시 선택할 수 있게)
        });
    }

    // 5. 페이드인 애니메이션 적용
    setTimeout(function() {
        const formContainer = document.querySelector('.form-container');
        if (formContainer) {
            formContainer.classList.add('fade-in');
        }
    }, 200);
});

// =========================
// 폼 데이터 초기화 함수 (수정 페이지용)
// =========================
function initializeFormData() {
    const region1Select = document.getElementById('region1');
    // const region2Select = document.getElementById('region2');
    const conditionSelect = document.getElementById('condition');
    const categorySelect = document.getElementById('category');

    // window.free를 통해 HTML에서 넘어온 free 객체에 접근
    const freeData = window.free;
    console.log('window.free 값 확인:', window.free);

    // region1 (구) 값 설정 및 동적 동 목록 채우기
    if (region1Select && freeData.regionGu) {
        region1Select.value = freeData.regionGu;
        populateRegion2(freeData.regionGu, freeData.regionDong);
    }

    // itemCondition (상품 상태) 값 설정 및 미리보기 업데이트
    if (conditionSelect && freeData.itemCondition) {
        conditionSelect.value = freeData.itemCondition;
        handleConditionChange(); // 미리보기 업데이트 함수 호출
    }

    // category (카테고리) 값 설정 및 도움말 업데이트
    if (categorySelect && freeData.category) {
        categorySelect.value = freeData.category;
        handleCategoryChange(); // 도움말 업데이트 함수 호출
    }

    // 이미지 미리보기 초기화는 HTML에서 th:each로 이미 처리되므로 여기서는 새로운 파일 업로드만 관리
    // newUploadedFiles 배열은 초기에는 비어있습니다.
}

// =========================
// 이벤트 리스너 설정 함수
// =========================
function setupEventListeners() {
    // 구 선택이 바뀔 때
    const region1Select = document.getElementById('region1');
    if (region1Select) {
        region1Select.addEventListener('change', (event) => populateRegion2(event.target.value));
    }

    // 이미지 업로드 관련
    const imageUploadArea = document.getElementById('imageUploadArea');
    const imageInput = document.getElementById('imageInput');
    const imagePreview = document.getElementById('imagePreview'); // 미리보기 컨테이너

    if (imageUploadArea && imageInput) {
        // 업로드 영역 클릭시
        imageUploadArea.addEventListener('click', function () {
            // 현재 이미지 개수 확인 (기존 + 새로 추가될 이미지)
            const currentTotalImages = imagePreview.querySelectorAll('.preview-image').length + newUploadedFiles.length;
            if (currentTotalImages >= 5) {
                showNotification('사진은 최대 5개까지 업로드 가능합니다.', 'error');
                return;
            }
            imageInput.click();
        });

        // 파일이 선택되었을 때
        imageInput.addEventListener('change', handleNewImageSelect);

        // 드래그 앤 드롭 관련
        setupDragAndDrop(imageUploadArea);
    }

    // 폼 제출 이벤트
    const form = document.getElementById('modifyForm');
    if (form) {
        form.addEventListener('submit', handleFormSubmit);
    }

    // 실시간 유효성 검사
    setupRealtimeValidation();

    // 카테고리 변경시 도움말 표시
    const categorySelect = document.getElementById('category');
    if (categorySelect) {
        categorySelect.addEventListener('change', handleCategoryChange);
    }

    // 상품 상태 변경시 미리보기 표시
    const conditionSelect = document.getElementById('condition');
    if (conditionSelect) {
        conditionSelect.addEventListener('change', handleConditionChange);
    }

    // 제목 글자수 제한
    const titleInput = document.getElementById('title');
    if (titleInput) {
        titleInput.addEventListener('input', function () {
            limitCharacters(this, 50, '제목');
        });
    }

    // 설명 글자수 제한 및 카운터
    const descriptionInput = document.getElementById('description');
    if (descriptionInput) {
        descriptionInput.addEventListener('input', function () {
            limitCharacters(this, 1000, '설명');
            updateCharacterCounter(this, 1000);
        });
    }


    // ✅ 기존 이미지 삭제 처리
    const existingRemoveButtons = document.querySelectorAll('.preview-image.existing .remove-image');
    existingRemoveButtons.forEach(button => {
        button.addEventListener('click', function () {
            const imageBox = this.closest('.preview-image');
            if (imageBox) {
                const statusInput = imageBox.querySelector('input[name$=".imgStatus"]');
                if (statusInput) {
                    statusInput.value = 'DELETED';
                }
                imageBox.style.display = 'none';
            }
        });
    });
}
// =========================
// 지역 선택 관련 함수
// =========================

// 구가 변경되었을 때 또는 초기화 시 실행
function populateRegion2(selectedGu, selectedDong = null) {
    const region2Select = document.getElementById('region2');

    if (!region2Select) return;

    // 동 선택박스 초기화
    region2Select.innerHTML = '<option value="">동</option>';

    // 선택된 구에 해당하는 동 추가
    if (selectedGu && regionData[selectedGu]) {
        regionData[selectedGu].forEach(function(dong) {
            const option = document.createElement('option');
            option.value = dong;
            option.textContent = dong;
            region2Select.appendChild(option);
        });

        // 기존 동 값이 있다면 선택
        if (selectedDong) {
            region2Select.value = selectedDong;
        }
    }
}


// =========================
// 이미지 업로드 관련 함수
// =========================

// 새 파일이 선택되었을 때
function handleNewImageSelect(event) {
    const files = event.target.files;
    handleNewImageFiles(Array.from(files));

    // 같은 파일 선택 시에도 change 이벤트가 발생하도록 value 초기화
    event.target.value = '';
}

// 드래그 앤 드롭 설정
function setupDragAndDrop(uploadArea) {
    // 드래그 오버
    uploadArea.addEventListener('dragover', function(event) {
        event.preventDefault();
        uploadArea.style.borderColor = 'var(--primary-green)';
        uploadArea.style.background = 'rgba(45, 90, 61, 0.1)';
    });

    // 드래그 나감
    uploadArea.addEventListener('dragleave', function() {
        uploadArea.style.borderColor = 'var(--accent-green)';
        uploadArea.style.background = 'rgba(111, 167, 118, 0.05)';
    });

    // 드롭
    uploadArea.addEventListener('drop', function(event) {
        event.preventDefault();

        // 스타일 원복
        uploadArea.style.borderColor = 'var(--accent-green)';
        uploadArea.style.background = 'rgba(111, 167, 118, 0.05)';

        const files = event.dataTransfer.files;
        handleNewImageFiles(Array.from(files));
    });
}

// 새 이미지 파일들 처리 (파일 선택 또는 드래그 맨 드롭 시 호출)
function handleNewImageFiles(files) {
    const imagePreview = document.getElementById('imagePreview');
    if (!imagePreview) return;

    const imageFiles = files.filter(file => file.type.startsWith('image/'));

    // ✅ 삭제되지 않은 기존 이미지만 카운트
    existingCount = [...imagePreview.querySelectorAll('.preview-image')].filter(img => {
        const statusInput = img.closest('.preview-item')?.querySelector('input[name$=".imgStatus"]');
        return !statusInput || statusInput.value !== 'DELETED';
    }).length;

    const totalAfterAdd = existingCount + newUploadedFiles.length + imageFiles.length;

    if (totalAfterAdd > 5) {
        showNotification('사진은 최대 5개까지 업로드 가능합니다.', 'error');
        return;
    }

    imageFiles.forEach(function(file) {
        if (file.size > 10 * 1024 * 1024) {
            showNotification(file.name + '은 10MB를 초과합니다.', 'error');
            return;
        }

        const isDuplicate = newUploadedFiles.some(img =>
            img.file.name === file.name && img.file.size === file.size
        );
        if (isDuplicate) {
            showNotification(file.name + '은 이미 추가된 파일입니다.', 'warning');
            return;
        }

        const reader = new FileReader();
        reader.onload = function (event) {
            const imageData = {
                file: file,
                src: event.target.result,
                id: 'new-image-' + Date.now() + Math.random().toString(36).substring(2, 9)
            };

            newUploadedFiles.push(imageData);
            displayNewImagePreview(imageData);
        };
        reader.readAsDataURL(file);
    });
}

// 새 이미지 미리보기 표시 (새 파일이 추가될 때 호출)
function displayNewImagePreview(imageData) {
    const previewContainer = document.getElementById('imagePreview');
    if (!previewContainer) return;

    // 미리보기 아이템 생성
    const previewItem = document.createElement('div');
    previewItem.className = 'preview-image new'; // 'new' 클래스 추가
    previewItem.id = imageData.id; // 제거를 위한 ID 부여
    previewItem.innerHTML = `<img src="${imageData.src}" alt="새 이미지 미리보기">
        <button type="button" class="remove-image" data-image-id="${imageData.id}">×</button>`; // 데이터 속성으로 ID 저장

    previewContainer.appendChild(previewItem);

    // 새 이미지의 제거 버튼에 이벤트 리스너 추가
    previewItem.querySelector('.remove-image').addEventListener('click', function() {
        removeNewImage(this.dataset.imageId);
    });
}

// 새 이미지 삭제 (uploadedImages 배열에서 제거)
function removeNewImage(freeImgId) {
    // const imagePreview = document.getElementById('imagePreview');
    const imageToRemove = document.getElementById(freeImgId);

    if (imageToRemove) {
        imageToRemove.remove(); // DOM에서 제거
        newUploadedFiles = newUploadedFiles.filter(item => item.id !== freeImgId); // 배열에서 제거
    }
}

function renderAllImages() {
    const previewContainer = document.getElementById('imagePreview');
    if (!previewContainer) return;

    // 기존 내용을 모두 지우고 새로 랜더링할 준비
    previewContainer.innerHTML = '';

    let allImageElements = []; // 렌더링된 이미지 div들 (대표 이미지 자동 지정용)
    let currentImageIndex = 0; // imgList[${index}]를 위한 인덱스

    // 기존 이미지 렌더링
    existingImages.forEach(imgDto => {
        // 삭제된 이미지 ID 리스트에 포함되어 있는지 확인
        const isDeleted = deletedImageIds.includes(String(imgDto.freeImgId));
        // 개별 이미지 박스 생성
        const div = document.createElement('div');
        div.className = 'preview-item existing-image-item';
        div.style.opacity = isDeleted ? '0.5' : '1';
        div.dataset.id = imgDto.freeImgId; // 기존 이미지 ID

        // 버튼 텍스트 및 비활성화 처리
        const buttonText = isDeleted ? '삭제됨' : '×';
        const buttonDisabled = isDeleted ? 'disabled' : '';

        // 이미지 미리보기 및 삭제 버튼
        div.innerHTML = `
            <img src="${imgDto.freeImgUrl}" alt="등록된 이미지" class="preview-image" style="width:100px; height:auto;">
            <button type="button" class="btn-delete-existing" data-id="${imgDto.freeImgId}" ${buttonDisabled}>${buttonText}</button>

            <input type="hidden" name="imgList[${index}].freeImgId" value="${imgDto.freeImgId}">
            <input type="hidden" name="imgList[${index}].imgUrl" value="${imgDto.freeImgUrl}">
            <input type="hidden" name="imgList[${index}].repImgYn" value="${imgDto.repImgYn || 'N'}">
            <input type="hidden" name="imgList[${index}].imgStatus" value="${isDeleted ? 'DELETED' : 'EXIST'}">
        `;

        previewContainer.appendChild(div);
        allImageElements.push(div); // 대표 이미지 후보로 추가
        currentImageIndex++; // 인덱스 증가
    });

    // 전체 newUploadedFiles 배열 출력
    console.log("현재 업로드된 새 이미지 리스트:", newUploadedFiles);

    // 새 이미지 렌더링
    newUploadedFiles.forEach((imageData, index) => {
        const totalIndex = existingImages.length + index;

        const div = document.createElement('div');
        div.className = 'preview-item new-image-item';
        div.dataset.id = imageData.id;

        div.innerHTML = `
            <img src="${imageData.src}" alt="미리보기" class="preview-image" />
            <button type="button" class="remove-image" data-image-id="${imageData.id}">×</button>

            <input type="hidden" name="imgList[${totalIndex}].imgUrl" value="${imageData.src}">
            <input type="hidden" name="imgList[${totalIndex}].repImgYn" value="N">
            <input type="hidden" name="imgList[${totalIndex}].imgStatus" value="NEW">
        `;

        previewContainer.appendChild(div);
        allImageElements.push(div); // 대표 이미지 후보로 추가
        currentImageIndex++; // 인덱스 증가

        // 🔍 로그: 새 이미지 추가됨
        console.log("새 이미지 추가됨:", imageData.id);

        // 제거 이벤트 연결
        div.querySelector('.remove-image').addEventListener('click', function () {
            removeNewImage(this.dataset.imageId);
            renderAllImages(); // 이미지 삭제 후 다시 렌더링하여 상태 업데이트 => 추가
        });
    });

    // 기존 이미지 삭제 버튼 이벤트 등록
    previewContainer.querySelectorAll('.btn-delete-existing').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');

            // 이미 삭제된 이미지가 아니라면 삭제 리스트에 추가
            if (!deletedImageIds.includes(String(id))) {
                deletedImageIds.push(String(id));
            }

            // 🔍 로그: 삭제된 이미지 ID 목록 출력
            console.log("삭제된 이미지 ID 목록:", deletedImageIds);

            // 이미지 다시 렌더링 (버튼 disabled 처리 반영)
            renderAllImages(); // 다시 렌더링
        });
    });

    // 대표 이미지 자동 지정 로직
    let repSet = false;

    allImageElements.forEach(div => {
        const imgStatusInput = div.querySelector('input[name$=".imgStatus"]');
        const repInput = div.querySelector('input[name$=".repImgYn"]');

        const isDeleted = imgStatusInput && imgStatusInput.value === 'DELETED';

        if (!repSet && !isDeleted) {
            // 첫 번째 삭제되지 않은 이미지를 대표 이미지로 지정
            if (repInput) repInput.value = 'Y';
            repSet = true; ; // 대표 이미지 설정 완료
        } else {
            if (repInput) repInput.value = 'N';
        }
    });
}



// =========================
// 폼 유효성 검사 함수
// =========================

// 실시간 유효성 검사 설정
function setupRealtimeValidation() {
    const inputs = document.querySelectorAll('.form-input, .form-select, .form-textarea'); // textarea 포함

    inputs.forEach(function(input) {
        // 포커스를 잃었을 때
        input.addEventListener('blur', function() {
            validateField(input);
        });

        // 내용이 변경될 때
        input.addEventListener('input', function() {
            // Thymeleaf 오류 메시지가 아닌 경우에만 처리
            if (!input.classList.contains('error-message')) {
                if (input.classList.contains('error') && input.value.trim()) {
                    clearFieldError(input);
                    input.classList.add('success');
                } else if (!input.value.trim() && input.hasAttribute('required')) {
                    // 비어있고 필수 항목이면 다시 에러 상태로
                    showFieldError(input, '필수 입력 항목입니다.');
                } else {
                    // 값이 있고 필수 항목이 아니거나 유효하면 success
                    clearFieldError(input);
                    if (input.value.trim()) {
                        input.classList.add('success');
                    } else {
                        input.classList.remove('success'); // 값이 없으면 success 제거
                    }
                }
            }
        });
    });
}

// 개별 필드 유효성 검사
function validateField(field) {
    const value = field.value.trim();
    const isRequired = field.hasAttribute('required');

    if (isRequired && !value) {
        showFieldError(field, '필수 입력 항목입니다.');
        return false;
    } else {
        clearFieldError(field);
        if (value) {
            field.classList.add('success');
        } else {
            field.classList.remove('success');
        }
        return true;
    }
}

// 필드 에러 표시
function showFieldError(field, message) {
    field.classList.add('error');
    field.classList.remove('success');

    // 기존 에러 메시지 제거 (부모 노드 기준)
    const parent = field.parentNode;
    let existingError = parent.querySelector('.error-message');
    // Thymeleaf에서 이미 렌더링된 에러 메시지는 덮어쓰지 않고 새로운 에러 메시지만 추가
    if (existingError && !existingError.hasAttribute('th:errors')) { // th:errors 속성이 없는 경우만 제거
        existingError.remove();
    }

    // 새 에러 메시지 생성 및 추가 (기존 Thymeleaf 에러 메시지 다음으로)
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;

    // 만약 기존 Thymeleaf 에러 메시지가 있다면 그 뒤에 추가, 아니면 필드 뒤에 추가
    const thymeleafError = parent.querySelector('[th\\:errors]'); // th:errors 속성을 가진 요소 찾기
    if (thymeleafError) {
        parent.insertBefore(errorDiv, thymeleafError.nextSibling);
    } else {
        parent.appendChild(errorDiv);
    }
}


// 필드 에러 제거
function clearFieldError(field) {
    field.classList.remove('error');

    // 해당 필드에 대한 모든 동적으로 추가된 .error-message 제거
    const parent = field.parentNode;
    const errorMessages = parent.querySelectorAll('.error-message');
    errorMessages.forEach(msg => {
        // Thymeleaf에서 생성된 에러 메시지(`th:errors`)는 제거하지 않도록 조건 추가
        if (!msg.hasAttribute('th:errors')) {
            msg.remove();
        }
    });
}


// 전체 폼 유효성 검사
function validateForm() {
    let isValid = true;
    // memberId는 hidden 필드이므로 검사 대상에서 제외
    const requiredFields = ['title', 'condition', 'region1', 'region2', 'category', 'description'];

    requiredFields.forEach(function (fieldId) {
        const field = document.getElementById(fieldId);
        if (field && !validateField(field)) {
            isValid = false;
        }
    });

    // 추가적으로 이미지 개수 검사
    const totalImagesCount = existingCount + newUploadedFiles.length;

    if (totalImagesCount === 0) {
        const imageUploadArea = document.getElementById('imageUploadArea');
        showFieldError(imageUploadArea, '사진은 최소 1개 이상 업로드해야 합니다.');
        isValid = false;
    } else {
        clearFieldError(document.getElementById('imageUploadArea'));
    }

}
// =========================
// 폼 제출 처리 함수 (게시글 수정)
// =========================
async function handleFormSubmit(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    console.log('폼 수정 제출 시도');


    // 유효성 검사
    if (!validateForm()) {
        showNotification('필수 입력 항목을 모두 작성하고, 사진을 1개 이상 첨부해주세요.', 'error');
        return;
    }

    // 제출 버튼 비활성화 (중복 제출 방지)
    const submitBtn = document.querySelector('.btn-submit');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = '수정 중...';
    submitBtn.disabled = true;

    // 게시글 ID 및 freeDto 생성
    const freeId = document.querySelector('input[name="freeId"]').value;

    const freeDto = {
        freeId: Number(freeId),
        title: document.getElementById('title').value,
        content: document.getElementById('description').value,
        category: document.getElementById('category').value,
        regionGu: document.getElementById('region1').value,
        regionDong: document.getElementById('region2').value,
        itemCondition: document.getElementById('condition').value
    };


    // FormData 생성 및 필드 추가
    const formData = new FormData();

    // freeDto를 JSON 문자열로 변환 후 Blob 형태로 추가
    formData.append("freeDto", new Blob([JSON.stringify(freeDto)], { type: "application/json" }));

    // 새로 업로드된 이미지 파일들 추가
    newUploadedFiles.forEach(imageData => {
        formData.append("imgFile", imageData.file);
    });

    // 삭제된 기존 이미지 ID 수집 및 추가
    const deletedIds = [];
    const existingImages = document.querySelectorAll('.preview-image.existing');

    existingImages.forEach(imgBox => {
        const statusInput = imgBox.querySelector('input[name$=".imgStatus"]');
        const idInput = imgBox.querySelector('input[name$=".freeImgId"]');

        if (statusInput?.value === 'DELETED' && idInput?.value) {
            deletedIds.push(idInput.value);
        }
    });
    // 삭제된 기존 이미지 ID 수집 및 추가
    deletedIds.forEach(id => {
        formData.append("deletedImageIds", id);
    });

    // imgList 관련 input 필드 추가
    document.querySelectorAll('input[name^="imgList"]').forEach(input => {
        formData.append(input.name, input.value);
    });

    // 대표 이미지 자동 지정 확인 (렌더링된 이미지 중에서 repImgYn = 'Y'인 값이 있는지)
    const allRepInputs = document.querySelectorAll('input[name$=".repImgYn"]');
    const hasRepImg = Array.from(allRepInputs).some(input => input.value === 'Y');

    if (!hasRepImg) {
        showNotification('대표 이미지를 자동 지정하지 못했습니다. 다시 시도해주세요.', 'error');
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
        return;
    }

    // 서버로 Post 요청 전송
    try {
        const response = await fetch(`/api/free/modify/${freeId}`, {
            method: 'POST',
            body: formData
        });


        // 응답 파싱: JSON 또는 텍스트
        const contentType = response.headers.get("content-type");
        let responseData;

        if (contentType?.includes("application/json")) {
            responseData = await response.json();
        } else {
            responseData = await response.text();
        }


        // 응답 성공 처리
        if (response.ok) {
            showNotification('나눔 정보가 성공적으로 수정되었습니다! 🎉', 'success');

            setTimeout(() => {
                if (confirm('수정된 나눔 게시글을 확인하시겠습니까?')) {
                    // 수정된 게시글 상세 페이지로 이동
                    window.location.href = `/free/get/${freeId}`;
                } else {
                    // 아니면 목록으로 이동
                    window.location.href = '/free/list';
                }
            }, 500);


            // 응답 실패 (else 블록)
        } else {
            // 서버에서 온 메시지를 우선 활용
            let errorMessage = '수정 중 오류가 발생했습니다.';
            if (typeof responseData === 'object' && responseData.message) {
                errorMessage = responseData.message;
            } else if (typeof responseData === 'string' && responseData.trim() !== '') {
                errorMessage = responseData;
            }

            showNotification(errorMessage, 'error');
            console.error('수정 실패 응답:', responseData);
        }


        // 네트워크 오류 또는 예외 (catch 블록)
    } catch (error) {
        console.error('수정 요청 오류:', error);
        showNotification('네트워크 오류 또는 서버 통신 중 문제가 발생했습니다.', 'error');


        // 최종적으로 버튼 상태 복원 (finally 블록)
    } finally {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
}

// =========================
// 상품 상태 관련 함수
// =========================

// 상품 상태 변경시 미리보기 표시
function handleConditionChange() {
    const conditionSelect = document.getElementById('condition');
    const conditionPreview = document.getElementById('conditionPreview');

    if (!conditionSelect || !conditionPreview) return;

    const selectedCondition = conditionSelect.value;

    // 미리보기 초기화
    conditionPreview.className = 'condition-preview';
    conditionPreview.style.display = 'none'; // 기본적으로 숨김

    if (selectedCondition) {
        let previewText = '';
        let previewClass = '';
        let stars = '';

        switch(selectedCondition) {
            case 'HIGH':
                previewText = '매우 좋음 - 거의 사용하지 않아 새 제품과 같은 상태';
                stars = '⭐⭐⭐⭐⭐';
                previewClass = 'good';
                break;
            case 'MEDIUM':
                previewText = '보통 - 일반적인 사용감이 있지만 기능상 문제없음';
                stars = '⭐⭐⭐';
                previewClass = 'fair';
                break;
            case 'LOW':
                previewText = '사용감 있음 - 오래 사용하여 흔적이 있지만 사용 가능';
                stars = '⭐⭐';
                previewClass = 'poor';
                break;
        }

        // 미리보기 표시
        conditionPreview.innerHTML = `
            <div style="padding: 10px; background: #f5f5f5; border-radius: 5px; margin-top: 10px;">
                <span style="font-size: 16px;">${stars}</span>
                <span style="margin-left: 10px; color: #666;">${previewText}</span>
            </div>
        `;
        conditionPreview.style.display = 'block'; // 보이도록 설정
        conditionPreview.classList.add(previewClass);

        // 애니메이션 효과
        setTimeout(function() {
            conditionPreview.style.opacity = '1';
            conditionPreview.style.transform = 'translateY(0)';
        }, 100);
    }
}

// 카테고리 변경시 도움말 표시
function handleCategoryChange() {
    const categorySelect = document.getElementById('category');
    const descriptionInput = document.getElementById('description');

    if (!categorySelect || !descriptionInput) return;

    const category = categorySelect.value;

    // 카테고리별 도움말
    const helpTexts = {
        '가구': '가구는 크기와 무게를 미리 안내해주세요.',
        '가전': '정상 작동 여부와 구매 시기를 명시해주세요.',
        '잡화': '재질, 브랜드, 사용 빈도 등을 명시해주세요.',
        '기타': '물품의 종류를 구체적으로 설명해주세요.'
    };

    // 현재 placeholder 값 저장
    const currentPlaceholder = descriptionInput.placeholder;
    const defaultPlaceholder = "물품에 대한 자세한 설명을 적어주세요";

    // 설명란이 비어있을 때만 도움말 추가/갱신
    // 기존 placeholder에 도움말이 있었는지 확인하고, 현재 입력값이 없을 경우에만 갱신
    if (descriptionInput.value.trim() === '') {
        if (helpTexts[category]) {
            descriptionInput.placeholder = `${defaultPlaceholder}\n\n💡 ${helpTexts[category]}\n\n예시:\n- 사용 기간\n- 구매 시기\n- 특이사항 등`;
        } else {
            descriptionInput.placeholder = defaultPlaceholder; // 카테고리 선택 없으면 기본 문구
        }
    }
    // 사용자가 이미 입력했다면 placeholder를 변경하지 않음
}


// =========================
// 글자수 제한 관련 함수
// =========================

// 글자수 제한
function limitCharacters(input, maxLength, fieldName) {
    if (input.value.length > maxLength) {
        input.value = input.value.substring(0, maxLength);
        showNotification(`${fieldName}은 ${maxLength}자까지 입력 가능합니다.`, 'error');
    }
}

// 글자수 카운터 업데이트
function updateCharacterCounter(input, maxLength) {
    const currentLength = input.value.length;

    // 기존 카운터 찾기 또는 생성
    let counter = input.parentNode.querySelector('.char-counter');
    if (!counter) {
        counter = document.createElement('div');
        counter.className = 'char-counter';
        counter.style.cssText = 'font-size: 12px; color: var(--medium-gray); text-align: right; margin-top: 5px;';
        input.parentNode.appendChild(counter);
    }

    // 카운터 텍스트 업데이트
    counter.textContent = `${Math.min(currentLength, maxLength)}/${maxLength}`;

    // 글자수가 많아지면 색상 변경
    if (currentLength > maxLength * 0.9) {
        counter.style.color = 'var(--error-red)';
    } else {
        counter.style.color = 'var(--medium-gray)';
    }
}

// =========================
// 알림 메시지 관련 함수
// =========================

// 알림 메시지 표시
function showNotification(message, type) {
    type = type || 'success';

    // 기존 알림 제거
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }

    // 새 알림 생성
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    // 페이지에 추가
    document.body.appendChild(notification);

    // 애니메이션으로 표시
    setTimeout(function() {
        notification.classList.add('show');
    }, 100);

    // 3초 후 자동 제거
    setTimeout(function() {
        notification.classList.remove('show');
        setTimeout(function() {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// =========================
// 기타 유틸리티 함수
// =========================

// 뒤로가기 함수 (수정 페이지용)
function goBack() {
    // 폼 변경사항이 있는지 확인
    const formChanged = checkFormChanged(); // 새로운 함수 필요

    if (formChanged) {
        if (confirm('수정 중인 내용이 저장되지 않을 수 있습니다. 정말로 나가시겠습니까?')) {
            window.history.back();
        }
    } else {
        window.history.back();
    }
}

// 폼 변경사항 확인 (간단 구현)
function checkFormChanged() {
    // TODO: 실제 폼 데이터를 로드 시의 초기 데이터와 비교하는 더 정교한 로직 필요
    // 현재는 단순히 입력 필드에 값이 채워져 있으면 변경된 것으로 간주 (정확하지 않을 수 있음)
    const inputs = document.querySelectorAll('.form-input, .form-select, .form-textarea');
    let hasChanged = false;

    inputs.forEach(function(input) {
        // 작성자와 등록일은 제외
        if (input.id !== 'author' && input.id !== 'regDate' && input.value.trim()) {
            hasChanged = true;
        }
    });

    // 새로운 이미지가 추가되었는지 확인
    if (newUploadedFiles.length > 0) {
        hasChanged = true;
    }

    // 기존 이미지가 삭제되었는지 확인 (hidden input 존재 여부)
    const deletedImageInputs = document.querySelectorAll('input[name="deletedImageIds"]');
    if (deletedImageInputs.length > 0) {
        hasChanged = true;
    }

    return hasChanged;
}

// =========================
// 자동 저장 기능 (수정 페이지에서는 필요성 낮음, 주석 처리)
// =========================
/*
function autoSave() {
    // ... 자동 저장 로직 ...
}

function restoreDraft() {
    // ... 복원 로직 ...
}

function clearDraft() {
    // ... 삭제 로직 ...
}

function setupAutoSave() {
    // ... 자동 저장 타이머 설정 로직 ...
}
*/

// =========================
// 키보드 단축키
// =========================

// 키보드 이벤트 처리
document.addEventListener('keydown', function(event) {
    // Ctrl + S: 폼 저장 (제출)
    if (event.ctrlKey && event.key === 's') {
        event.preventDefault();
        const form = document.getElementById('modifyForm');
        if (form) {
            form.dispatchEvent(new Event('submit'));
        }
    }

    // ESC: 취소
    if (event.key === 'Escape') {
        goBack();
    }
});

// =========================
// 페이지 이탈 경고 (수정 페이지에서는 폼 변경 여부 확인 후 경고)
// =========================

window.addEventListener('beforeunload', function(event) {
    if (checkFormChanged()) { // 폼 변경사항이 있을 때만 경고
        event.preventDefault();
        event.returnValue = ''; // 표준에 따라 이 속성 설정
        return ''; // 일부 브라우저에서는 이 값 반환 필요
    }
});


// =========================
// 전역 함수로 노출 (HTML에서 onclick 등으로 사용)
// =========================

// HTML의 onclick에서 사용할 수 있도록 전역으로 노출
window.goBack = goBack;
// window.removeImage는 새 이미지 삭제용 removeNewImage로 대체하거나,
// 기존 이미지 삭제는 HTML에서 직접 data-속성을 활용하므로 JS에서 직접 호출할 필요 없음.
// 만약 JS에서 removeImage 함수가 여전히 필요하다면 removeNewImage를 사용하도록 이름을 바꿔야 함.
// 여기서는 removeNewImage를 전역으로 노출합니다.
window.removeNewImage = removeNewImage;


// 기타 유용한 전역 함수들
window.showNotification = showNotification;
window.validateForm = validateForm;

// =========================
// 에러 핸들링
// =========================

// 전역 에러 처리
window.addEventListener('error', function(event) {
    console.error('페이지 오류:', event.error);
    showNotification('페이지를 처리하는 중 오류가 발생했습니다.', 'error');
});

// Promise 거부 처리
window.addEventListener('unhandledrejection', function(event) {
    console.error('처리되지 않은 Promise 거부:', event.reason);
    showNotification('작업 처리 중 오류가 발생했습니다.', 'error');
});

// =========================
// 최종 로그
// =========================

console.log('🤝 무료나눔 수정 페이지 JavaScript가 로드되었습니다.');
console.log('📝 사용 가능한 기능:');
console.log('   - 지역 연동 선택 (기존 값 자동 채우기 포함)');
console.log('   - 이미지 드래그 앤 드롭 (새 이미지 추가)');
console.log('   - 기존 이미지 삭제 및 새 이미지 추가 통합');
console.log('   - 실시간 유효성 검사');
console.log('   - 키보드 단축키 (Ctrl+S, ESC)');
console.log('   - 접근성 지원');
console.log('   - 페이지 이탈 시 변경사항 경고');
console.log('newUploadedFiles:', newUploadedFiles);