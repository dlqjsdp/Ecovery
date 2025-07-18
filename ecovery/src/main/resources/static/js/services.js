// 전역 변수
let uploadedImage = null;
let currentStep = 1;
let analysisResult = null;
let selectedRegion = {
    city: '서울특별시',
    district: '강동구'
};

// 지역별 수수료 정보
const feeInfo = {
    '강남구': {
        contact: '02-3423-5678',
        fees: [
            { item: '냉장고', size: '대형', fee: '15,000원' },
            { item: '세탁기', size: '대형', fee: '12,000원' },
            { item: '에어컨', size: '대형', fee: '18,000원' },
            { item: '소파', size: '대형', fee: '10,000원' },
            { item: '매트리스', size: '대형', fee: '8,000원' }
        ]
    },
    '강동구': {
        contact: '02-3425-6789',
        fees: [
            { item: '냉장고', size: '대형', fee: '14,000원' },
            { item: '세탁기', size: '대형', fee: '11,000원' },
            { item: '에어컨', size: '대형', fee: '17,000원' },
            { item: '소파', size: '대형', fee: '9,000원' },
            { item: '매트리스', size: '대형', fee: '7,500원' }
        ]
    },
    '서초구': {
        contact: '02-2155-7890',
        fees: [
            { item: '냉장고', size: '대형', fee: '15,500원' },
            { item: '세탁기', size: '대형', fee: '12,500원' },
            { item: '에어컨', size: '대형', fee: '18,500원' },
            { item: '소파', size: '대형', fee: '10,500원' },
            { item: '매트리스', size: '대형', fee: '8,500원' }
        ]
    }
};

// 폐기물 분류 데이터베이스 (시뮬레이션용)
const wasteDatabase = {
    plastic_bottle: {
        name: "플라스틱 병",
        category: "재활용품",
        size: "중형 (500ml)",
        fee: "무료",
        confidence: 95.2,
        steps: [
            "라벨과 뚜껑을 제거해주세요",
            "내용물을 완전히 비워주세요", 
            "물로 헹구어 이물질을 제거해주세요",
            "플라스틱 전용 수거함에 배출해주세요"
        ]
    },
    glass_bottle: {
        name: "유리병",
        category: "재활용품", 
        size: "중형 (350ml)",
        fee: "무료",
        confidence: 92.7,
        steps: [
            "뚜껑을 제거해주세요",
            "내용물을 완전히 비워주세요",
            "물로 헹구어 이물질을 제거해주세요", 
            "유리병 전용 수거함에 배출해주세요"
        ]
    },
    paper_box: {
        name: "종이박스",
        category: "재활용품",
        size: "대형 (30x20x15cm)",
        fee: "무료", 
        confidence: 88.9,
        steps: [
            "테이프나 스테이플러를 제거해주세요",
            "이물질을 완전히 제거해주세요",
            "납작하게 펼쳐주세요",
            "종이류 전용 수거함에 배출해주세요"
        ]
    },
    electronic_device: {
        name: "소형 전자제품",
        category: "전자폐기물",
        size: "소형 (스마트폰 크기)",
        fee: "무료",
        confidence: 91.3,
        steps: [
            "개인정보를 완전히 삭제해주세요",
            "배터리를 분리해주세요", 
            "전자제품 전용 수거함에 배출해주세요",
            "또는 제조사 수거 서비스를 이용해주세요"
        ]
    },
    furniture: {
        name: "가구",
        category: "대형폐기물",
        size: "대형 (의자/테이블)",
        fee: "10,000원",
        confidence: 94.1,
        steps: [
            "대형폐기물 신고센터에 신고해주세요",
            "수수료를 결제해주세요",
            "지정된 배출일에 배출해주세요",
            "스티커를 부착해주세요"
        ]
    }
};

// DOM 요소들
const uploadZone = document.getElementById('uploadZone');
const imageInput = document.getElementById('imageInput');
const uploadPreview = document.getElementById('uploadPreview');
const previewImage = document.getElementById('previewImage');
const alertModal = document.getElementById('alertModal');
const alertMessage = document.getElementById('alertMessage');

// 새로 추가된 요소들
const resetBtn = document.getElementById('resetBtn');
const analyzeBtn = document.getElementById('analyzeBtn');
const imageName = document.getElementById('imageName');
const imageSize = document.getElementById('imageSize');

// 지역 선택 요소들
const cityInput = document.getElementById('cityInput');
const districtSelect = document.getElementById('districtSelect');
const widthInput = document.getElementById('widthInput');
const heightInput = document.getElementById('heightInput');

// 단계별 요소들
const uploadStep = document.getElementById('uploadStep');
const loadingStep = document.getElementById('loadingStep');
const resultStep = document.getElementById('resultStep');
const completionStep = document.getElementById('completionStep');

// 이벤트 리스너 설정
document.addEventListener('DOMContentLoaded', function() {
    initializeEventListeners();
    showStep(1);
});

function initializeEventListeners() {
    // 파일 입력 이벤트
    imageInput.addEventListener('change', handleFileSelect);
    
    // 드래그앤드롭 이벤트
    uploadZone.addEventListener('dragover', handleDragOver);
    uploadZone.addEventListener('dragenter', handleDragEnter);
    uploadZone.addEventListener('dragleave', handleDragLeave);
    uploadZone.addEventListener('drop', handleDrop);
    
    // 클릭 이벤트
    uploadZone.addEventListener('click', () => {
        imageInput.click();
    });
    
    // 지역 선택 이벤트
    if (districtSelect) {
        districtSelect.addEventListener('change', handleDistrictChange);
    }
    
    // 크기 입력 이벤트
    if (widthInput && heightInput) {
        widthInput.addEventListener('input', handleSizeInput);
        heightInput.addEventListener('input', handleSizeInput);
    }
}

// 지역 변경 핸들러
function handleDistrictChange() {
    selectedRegion.district = districtSelect.value;
    console.log('선택된 지역:', selectedRegion);
}

// 크기 입력 핸들러
function handleSizeInput() {
    const width = widthInput.value;
    const height = heightInput.value;
    const sizePreview = document.getElementById('sizePreview');
    
    if (width && height) {
        const area = width * height;
        let sizeCategory;
        let categoryIcon;
        let categoryColor;
        
        if (area < 100) {
            sizeCategory = '소형';
            categoryIcon = '📦';
            categoryColor = '#28a745';
        } else if (area < 1000) {
            sizeCategory = '중형';
            categoryIcon = '📋';
            categoryColor = '#ffc107';
        } else {
            sizeCategory = '대형';
            categoryIcon = '🏠';
            categoryColor = '#dc3545';
        }
        
        sizePreview.innerHTML = `
            ${categoryIcon} <strong>${sizeCategory}</strong> 폐기물 
            (${width} × ${height}cm = ${area.toLocaleString()}㎠)
        `;
        sizePreview.style.color = categoryColor;
        sizePreview.style.borderLeft = `4px solid ${categoryColor}`;
        sizePreview.classList.add('show');
        
        console.log(`입력된 크기: ${width} x ${height} cm (${sizeCategory})`);
    } else if (width || height) {
        sizePreview.innerHTML = `⚠️ 가로와 세로를 모두 입력해주세요`;
        sizePreview.style.color = '#6c757d';
        sizePreview.style.borderLeft = '4px solid #6c757d';
        sizePreview.classList.add('show');
    } else {
        sizePreview.classList.remove('show');
    }
}

// 수수료 확인하기
function checkFeeInfo() {
    const district = districtSelect.value;
    
    if (!district) {
        showNotification('먼저 구를 선택해주세요.', 'warning');
        return;
    }
    
    showFeeInfoModal(district);
}

// 수수료 정보 모달 표시
function showFeeInfoModal(district) {
    const info = feeInfo[district] || feeInfo['강동구']; // 기본값
    
    const modal = document.createElement('div');
    modal.className = 'fee-info-modal';
    modal.innerHTML = `
        <div class="fee-info-content">
            <div class="fee-info-header">
                <h3>💰 ${selectedRegion.city} ${district} 대형폐기물 수수료</h3>
            </div>
            <div class="fee-info-body">
                <table class="fee-table">
                    <thead>
                        <tr>
                            <th>📦 품목</th>
                            <th>📏 크기</th>
                            <th>💳 수수료</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${info.fees.map(fee => `
                            <tr>
                                <td>${fee.item}</td>
                                <td>${fee.size}</td>
                                <td><strong>${fee.fee}</strong></td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
                
                <div class="fee-contact">
                    <h4>📞 문의 및 신고센터</h4>
                    <p><strong>전화번호:</strong> ${info.contact}</p>
                    <p><strong>운영시간:</strong> 평일 09:00 ~ 18:00 (점심시간 12:00~13:00 제외)</p>
                    <p><strong>신고방법:</strong> 전화 신고 → 수수료 결제 → 스티커 발급</p>
                    <p><strong>배출방법:</strong> 지정된 날짜에 스티커 부착 후 지정장소 배출</p>
                </div>
                
                <div style="text-align: center; margin-top: 30px;">
                    <button class="modal-close-btn" onclick="closeFeeInfoModal()">✅ 확인했습니다</button>
                </div>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // 모달 외부 클릭으로 닫기
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            closeFeeInfoModal();
        }
    });
    
    // ESC 키로 닫기
    const handleKeyDown = (e) => {
        if (e.key === 'Escape') {
            closeFeeInfoModal();
            document.removeEventListener('keydown', handleKeyDown);
        }
    };
    document.addEventListener('keydown', handleKeyDown);
}

// 수수료 정보 모달 닫기
function closeFeeInfoModal() {
    const modal = document.querySelector('.fee-info-modal');
    if (modal) {
        modal.remove();
    }
}

// 드래그앤드롭 이벤트 핸들러
function handleDragOver(e) {
    e.preventDefault();
    e.stopPropagation();
}

function handleDragEnter(e) {
    e.preventDefault();
    e.stopPropagation();
    uploadZone.classList.add('dragover');
}

function handleDragLeave(e) {
    e.preventDefault();
    e.stopPropagation();
    uploadZone.classList.remove('dragover');
}

function handleDrop(e) {
    e.preventDefault();
    e.stopPropagation();
    uploadZone.classList.remove('dragover');
    
    const files = e.dataTransfer.files;
    if (files.length > 0) {
        handleFileUpload(files[0]);
    }
}

// 파일 선택 핸들러
function handleFileSelect(e) {
    const file = e.target.files[0];
    if (file) {
        handleFileUpload(file);
    }
}

// 파일 업로드 처리
function handleFileUpload(file) {
    // 파일 유효성 검사
    if (!file.type.startsWith('image/')) {
        showNotification('이미지 파일만 업로드 가능합니다.', 'error');
        return;
    }
    
    if (file.size > 10 * 1024 * 1024) { // 10MB
        showNotification('파일 크기는 10MB 이하여야 합니다.', 'error');
        return;
    }
    
    uploadedImage = file;
    
    // 파일 정보 표시
    const fileName = file.name;
    const fileSize = formatFileSize(file.size);
    
    if (imageName) imageName.textContent = fileName;
    if (imageSize) imageSize.textContent = fileSize;
    
    // 이미지 미리보기
    const reader = new FileReader();
    reader.onload = function(e) {
        previewImage.src = e.target.result;
        document.getElementById('resultImage').src = e.target.result;
        showUploadPreview();
    };
    reader.readAsDataURL(file);
}

// 파일 크기 포맷 함수
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// 업로드 미리보기 표시
function showUploadPreview() {
    // 업로드 존 상태 변경
    uploadZone.classList.add('has-image');
    
    // 미리보기 표시
    uploadPreview.style.display = 'block';
    
    // 버튼들 애니메이션으로 표시
    setTimeout(() => {
        if (resetBtn) {
            resetBtn.style.display = 'block';
            resetBtn.classList.add('show');
        }
        if (analyzeBtn) {
            analyzeBtn.style.display = 'block';
            analyzeBtn.classList.add('show');
        }
    }, 300);
    
    // 성공 알림
    showNotification('이미지가 성공적으로 업로드되었습니다! 📸', 'success');
}

// 업로드 초기화
function resetUpload() {
    uploadedImage = null;
    
    // 업로드 존 상태 초기화
    uploadZone.classList.remove('has-image');
    
    // 미리보기 숨기기
    uploadPreview.style.display = 'none';
    
    // 버튼들 숨기기
    if (resetBtn) {
        resetBtn.classList.remove('show');
        setTimeout(() => {
            resetBtn.style.display = 'none';
        }, 300);
    }
    if (analyzeBtn) {
        analyzeBtn.classList.remove('show');
        setTimeout(() => {
            analyzeBtn.style.display = 'none';
        }, 300);
    }
    
    // 파일 입력 초기화
    imageInput.value = '';
    
    // 알림
    showNotification('업로드가 초기화되었습니다.', 'info');
}

// 이미지 분석 시작
function analyzeImage() {
    if (!uploadedImage) {
        showNotification('먼저 이미지를 업로드해주세요.', 'warning');
        return;
    }
    
    if (!districtSelect.value) {
        showNotification('지역을 선택해주세요.', 'warning');
        // 지역 선택 부분으로 스크롤
        const regionSection = document.querySelector('.region-selection');
        if (regionSection) {
            regionSection.scrollIntoView({ behavior: 'smooth', block: 'center' });
            regionSection.style.animation = 'pulse 0.5s ease-in-out';
        }
        return;
    }
    
    // 분석 시작 알림
    showNotification('AI 분석을 시작합니다... 🤖', 'info');
    
    // 버튼 비활성화
    if (analyzeBtn) {
        analyzeBtn.disabled = true;
        analyzeBtn.innerHTML = '🔄 분석 중...';
    }
    
    // 다음 단계로 이동
    setTimeout(() => {
        showStep(2);
        startAnalysisAnimation();
    }, 1000);
}

// 추가 CSS 애니메이션 (펄스 효과)
const pulseAnimation = `
@keyframes pulse {
    0% { transform: scale(1); box-shadow: 0 0 0 0 rgba(45, 90, 61, 0.4); }
    50% { transform: scale(1.02); box-shadow: 0 0 0 10px rgba(45, 90, 61, 0.1); }
    100% { transform: scale(1); box-shadow: 0 0 0 0 rgba(45, 90, 61, 0); }
}
`;

// 스타일 추가
if (!document.querySelector('#pulse-animation')) {
    const style = document.createElement('style');
    style.id = 'pulse-animation';
    style.textContent = pulseAnimation;
    document.head.appendChild(style);
}

// 분석 애니메이션 시작
function startAnalysisAnimation() {
    const progressFill = document.getElementById('progressFill');
    const progressText = document.getElementById('progressText');
    let progress = 0;
    
    const interval = setInterval(() => {
        progress += Math.random() * 15 + 5;
        if (progress > 100) {
            progress = 100;
        }
        
        progressFill.style.width = progress + '%';
        progressText.textContent = Math.round(progress) + '%';
        
        if (progress >= 100) {
            clearInterval(interval);
            setTimeout(() => {
                completeAnalysis();
            }, 1000);
        }
    }, 300);
}

// 분석 완료
function completeAnalysis() {
    // 크기 정보 가져오기
    const width = widthInput.value;
    const height = heightInput.value;
    let sizeCategory = '중형';
    let estimatedSize = '중형 (추정)';
    
    if (width && height) {
        const area = width * height;
        if (area < 100) {
            sizeCategory = '소형';
            estimatedSize = `소형 (${width}×${height}cm)`;
        } else if (area < 1000) {
            sizeCategory = '중형';
            estimatedSize = `중형 (${width}×${height}cm)`;
        } else {
            sizeCategory = '대형';
            estimatedSize = `대형 (${width}×${height}cm)`;
        }
    }
    
    // 크기에 따라 폐기물 종류 조정
    const wasteTypes = Object.keys(wasteDatabase);
    let randomType;
    
    if (sizeCategory === '대형') {
        randomType = 'furniture'; // 대형은 가구로 분류
    } else {
        // 소형/중형은 다른 타입들 중 랜덤 선택
        const smallMediumTypes = wasteTypes.filter(type => type !== 'furniture');
        randomType = smallMediumTypes[Math.floor(Math.random() * smallMediumTypes.length)];
    }
    
    analysisResult = { ...wasteDatabase[randomType] };
    analysisResult.size = estimatedSize;
    analysisResult.sizeCategory = sizeCategory;
    analysisResult.region = selectedRegion;
    
    // 지역별 수수료 정보 반영
    if (sizeCategory === '대형' && feeInfo[selectedRegion.district]) {
        const regionFeeInfo = feeInfo[selectedRegion.district];
        const matchingFee = regionFeeInfo.fees.find(fee => 
            analysisResult.name.includes('가구') || 
            analysisResult.name.includes('의자') || 
            analysisResult.name.includes('테이블')
        );
        
        if (matchingFee) {
            analysisResult.fee = matchingFee.fee;
        }
        
        analysisResult.contact = regionFeeInfo.contact;
    }
    
    // 결과 표시
    displayAnalysisResult();
    showStep(3);
}

// 분석 결과 표시
function displayAnalysisResult() {
    if (!analysisResult) return;
    
    document.getElementById('wasteType').textContent = analysisResult.name;
    document.getElementById('wasteCategory').textContent = analysisResult.category;
    document.getElementById('itemName').textContent = analysisResult.name;
    document.getElementById('itemSize').textContent = analysisResult.size;
    document.getElementById('processingFee').textContent = analysisResult.fee;
    document.getElementById('reliability').textContent = analysisResult.confidence + '%';
    document.getElementById('confidenceBadge').textContent = `정확도 ${analysisResult.confidence}%`;
    
    // 처리 단계 표시
    const disposalSteps = document.getElementById('disposalSteps');
    disposalSteps.innerHTML = '';
    analysisResult.steps.forEach(step => {
        const li = document.createElement('li');
        li.textContent = step;
        disposalSteps.appendChild(li);
    });
    
    // 대형폐기물인 경우 연락처 정보 추가
    if (analysisResult.sizeCategory === '대형' && analysisResult.contact) {
        const contactInfo = document.createElement('div');
        contactInfo.className = 'contact-info';
        contactInfo.innerHTML = `
            <h4>📞 ${analysisResult.region.city} ${analysisResult.region.district} 신고센터</h4>
            <p><strong>전화번호:</strong> ${analysisResult.contact}</p>
            <p><strong>운영시간:</strong> 평일 09:00 ~ 18:00</p>
        `;
        contactInfo.style.cssText = `
            background: rgba(45, 90, 61, 0.1);
            padding: 20px;
            border-radius: 8px;
            margin-top: 20px;
            border-left: 4px solid var(--primary-green);
        `;
        
        const resultInfo = document.querySelector('.result-info');
        if (resultInfo) {
            resultInfo.appendChild(contactInfo);
        }
    }
}

// 단계 표시
function showStep(stepNumber) {
    // 모든 단계 숨기기
    const steps = [uploadStep, loadingStep, resultStep, completionStep];
    steps.forEach(step => {
        step.style.display = 'none';
        step.classList.remove('active');
    });
    
    // 해당 단계 표시
    let targetStep;
    switch(stepNumber) {
        case 1:
            targetStep = uploadStep;
            break;
        case 2:
            targetStep = loadingStep;
            break;
        case 3:
            targetStep = resultStep;
            break;
        case 4:
            targetStep = completionStep;
            break;
    }
    
    if (targetStep) {
        targetStep.style.display = 'block';
        setTimeout(() => {
            targetStep.classList.add('active');
        }, 50);
    }
    
    currentStep = stepNumber;
}

// 정확한 분류법 신고
function confirmDisposal() {
    showAlert('예상 물품의 분류가 정확하길 원하시나요?');
}

// 뒤로 가기
function goBack() {
    if (currentStep > 1) {
        showStep(currentStep - 1);
    }
}

// 다시 시작
function startOver() {
    resetUpload();
    analysisResult = null;
    showStep(1);
}

// 지자체 홈페이지 방문
function visitLocalWebsite() {
    // 김포시 분리배출 홈페이지로 이동 (시뮬레이션)
    showNotification('김포시 분리배출 홈페이지로 이동합니다.', 'info');
    // window.open('https://www.gimpo.go.kr', '_blank');
}

// 알림 모달 표시
function showAlert(message) {
    alertMessage.textContent = message;
    alertModal.style.display = 'flex';
}

// 알림 모달 닫기
function closeAlert() {
    alertModal.style.display = 'none';
}

// 알림 확인
function confirmAlert() {
    closeAlert();
    showStep(4); // 완료 단계로 이동
}

// 알림 메시지 표시
function showNotification(message, type = 'info') {
    // 기존 알림 제거
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }
    
    // 새 알림 생성
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    // 스타일 설정
    notification.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: ${type === 'error' ? '#dc3545' : type === 'warning' ? '#ffc107' : type === 'success' ? '#28a745' : '#17a2b8'};
        color: white;
        padding: 15px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        z-index: 10000;
        transform: translateX(400px);
        transition: transform 0.3s ease;
        max-width: 300px;
        font-weight: 500;
    `;
    
    document.body.appendChild(notification);
    
    // 애니메이션
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);
    
    // 자동 제거
    setTimeout(() => {
        notification.style.transform = 'translateX(400px)';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// 키보드 이벤트 처리
document.addEventListener('keydown', function(e) {
    // ESC 키로 모달 닫기
    if (e.key === 'Escape') {
        closeAlert();
    }
    
    // Enter 키로 다음 단계 진행
    if (e.key === 'Enter' && currentStep === 1 && uploadedImage) {
        analyzeImage();
    }
});

// 모달 외부 클릭으로 닫기
alertModal.addEventListener('click', function(e) {
    if (e.target === alertModal) {
        closeAlert();
    }
});

// 페이지 언로드 시 정리
window.addEventListener('beforeunload', function() {
    if (uploadedImage) {
        // 업로드된 파일 정리
        uploadedImage = null;
    }
});

// 반응형 처리
function handleResize() {
    // 모바일에서 미리보기 이미지 크기 조정
    if (window.innerWidth <= 768) {
        const previewImg = document.getElementById('previewImage');
        const resultImg = document.getElementById('resultImage');
        
        if (previewImg) {
            previewImg.style.maxHeight = '250px';
        }
        if (resultImg) {
            resultImg.style.maxHeight = '250px';
        }
    }
}

window.addEventListener('resize', handleResize);

// 초기 리사이즈 처리
handleResize();

// 디버깅용 함수들 (개발 환경에서만 사용)
if (process?.env?.NODE_ENV === 'development') {
    window.debugWasteClassification = {
        showStep: showStep,
        setAnalysisResult: (type) => {
            if (wasteDatabase[type]) {
                analysisResult = wasteDatabase[type];
                displayAnalysisResult();
            }
        },
        getCurrentStep: () => currentStep,
        getUploadedImage: () => uploadedImage,
        getAnalysisResult: () => analysisResult
    };
}

// 접근성 개선
document.addEventListener('DOMContentLoaded', function() {
    // 이미지 업로드 영역에 키보드 접근성 추가
    uploadZone.setAttribute('tabindex', '0');
    uploadZone.setAttribute('role', 'button');
    uploadZone.setAttribute('aria-label', '이미지 업로드 영역');
    
    uploadZone.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' || e.key === ' ') {
            e.preventDefault();
            imageInput.click();
        }
    });
    
    // 모달에 포커스 트랩 추가
    const modalContent = document.querySelector('.modal-content');
    if (modalContent) {
        modalContent.addEventListener('keydown', function(e) {
            if (e.key === 'Tab') {
                const focusableElements = modalContent.querySelectorAll(
                    'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
                );
                const firstElement = focusableElements[0];
                const lastElement = focusableElements[focusableElements.length - 1];
                
                if (e.shiftKey && document.activeElement === firstElement) {
                    e.preventDefault();
                    lastElement.focus();
                } else if (!e.shiftKey && document.activeElement === lastElement) {
                    e.preventDefault();
                    firstElement.focus();
                }
            }
        });
    }
});

// 성능 최적화: 이미지 리사이즈
function resizeImage(file, maxWidth = 800, quality = 0.8) {
    return new Promise((resolve) => {
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        const img = new Image();
        
        img.onload = function() {
            const ratio = Math.min(maxWidth / img.width, maxWidth / img.height);
            canvas.width = img.width * ratio;
            canvas.height = img.height * ratio;
            
            ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
            
            canvas.toBlob(resolve, 'image/jpeg', quality);
        };
        
        img.src = URL.createObjectURL(file);
    });
}

// 애니메이션 효과
function addTransitionEffects() {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    });
    
    // 관찰할 요소들 설정
    document.querySelectorAll('.step-card').forEach(card => {
        card.style.opacity = '0';a
        card.style.transform = 'translateY(30px)';
        card.style.transition = 'all 0.6s ease';
        observer.observe(card);
    });
}

// 페이지 로드 완료 후 애니메이션 초기화
window.addEventListener('load', function() {
    addTransitionEffects();
});

console.log('🤖 GreenCycle 폐기물 분류 시스템이 초기화되었습니다.');
console.log('📝 지원되는 폐기물 유형:', Object.keys(wasteDatabase));
console.log('🏢 지원되는 지역:', Object.keys(feeInfo));

// 전역 함수로 노출 (HTML에서 호출)
window.checkFeeInfo = checkFeeInfo;
window.closeFeeInfoModal = closeFeeInfoModal;
window.resetUpload = resetUpload;
window.analyzeImage = analyzeImage;
window.goBack = goBack;
window.startOver = startOver;
window.visitLocalWebsite = visitLocalWebsite;
window.confirmDisposal = confirmDisposal;
window.showAlert = showAlert;
window.closeAlert = closeAlert;
window.confirmAlert = confirmAlert;