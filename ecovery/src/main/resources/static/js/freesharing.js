/*서버에서 받은 데이터를 사용자가 읽기 좋게 바꿔주는 유틸리티 함수*/
// 거래생태
function getStatusText(status){
    switch (status){
        case 'ONGOING': return '나눔중';
        case 'DONE': return '나눔 완료';
        default: return '나눔중';
    }
}

// 상품상태
function getConditionText(condition){
    switch (condition){
        case 'HIGH': return "최상";
        case 'MEDIUM': return '중간';
        case 'LOW': return '사용감 있음';
        default: return '최상';
    }
}

function formatTimeAgo(dateTime){
    const now = new Date();
    const created = new Date(dateTime);
    const diff = Math.floor((now - created) / 1000); // 초단위

    if (diff < 60) return '방금 전';
    if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;
    if (diff < 604800) return `${Math.floor(diff / 86400)}일 전`;
    return created.toLocaleDateString(); // ex: 2025.07.28
}


// 전역 변수 선언
let currentItems = [];
let filteredItems = [];
let currentPage = 1;
const itemsPerPage = 12;
let sharingItemsData = [];

// DOM elements
const header = document.getElementById('header');
const hamburger = document.getElementById('hamburger');
const navMenu = document.getElementById('navMenu');
const addItemBtn = document.getElementById('addItemBtn');
const addItemModal = document.getElementById('addItemModal');
const itemDetailModal = document.getElementById('itemDetailModal');
const closeModal = document.getElementById('closeModal');
const closeDetailModal = document.getElementById('closeDetailModal');
const addItemForm = document.getElementById('addItemForm');
const itemsGrid = document.getElementById('itemsGrid');
const loadMoreBtn = document.getElementById('loadMoreBtn');
const totalItems = document.getElementById('totalItems');
const pagination = document.getElementById('pagination');

/* 페이지가 처음 로딩되었을 때 실행되는 함수*/
// 1. 페이지 로드 후 실행
document.addEventListener('DOMContentLoaded', async function(){
    initializePage(); // 필터/모달 초기화

    try{
        // 2. API로 데이터 요청
        const res = await fetch('/api/free/list'); // 실제 API URL 로딩
        const result = await res.json();

        console.log('전체 result:', result);
        console.log('result.list:', result.list);
        console.log('result.content:', result.content);

        // 3. 응답 데이터 파싱
        const list = Array.isArray(result.list)
                ? result.list
                : Array.isArray(result.content)
                ? result.content
                : [];

        sharingItemsData = list;

        // 4. 렌더링 대상 배열 복사
        currentItems = [...sharingItemsData];
        filteredItems = [...currentItems];

        // 5. 렌더링 함수 실행
        renderItems();  // 아이템 카드 렌더링
        updateItemCount(); // 아이템 수 갱신
        renderPagination(); // 페이징 처리

        // 6. 버튼, 모달 등의 이벤트 바인딩
        setupEventListeners();

    } catch (err){
        console.error('데이터 불러오기 실패 : ', err);
    }
});

function updateItemCount() {
    const totalItems = document.getElementById('totalItems');
    if (totalItems) {
        totalItems.textContent = `총 ${filteredItems.length}건`;
    } else {
        console.warn('⚠️ totalItems 요소를 찾을 수 없습니다.');
    }
}

function renderPagination() {
    const pagination = document.getElementById('pagination');
    if (!pagination) {
        console.warn('⚠️ pagination 요소가 없습니다.');
        return;
    }

    pagination.innerHTML = ''; // 기존 내용 초기화

    const totalPages = Math.ceil(filteredItems.length / itemsPerPage);

    for (let i = 1; i <= totalPages; i++) {
        const pageBtn = document.createElement('button');
        pageBtn.textContent = i;
        pageBtn.className = 'page-btn';
        if (i === currentPage) {
            pageBtn.classList.add('active');
        }

        // 페이지 클릭 이벤트
        pageBtn.addEventListener('click', () => {
            currentPage = i;
            renderItems();
        });

        pagination.appendChild(pageBtn);
    }
}


//  아이템 카드 생성 함수
function createItemElement(item) {
    const card = document.createElement('div');
    card.className = 'sharing-card';

    card.innerHTML = `
        <div class="item-image">
            <img src="${item.imgUrl || '/img/default-sharing.svg'}" alt="${item.title}">
            <div class="item-status">${getStatusText(item.dealStatus)}</div>
        </div>
        <div class="item-info">
            <h3 class="item-title">${item.title}</h3>
            <div class="item-meta">
                <span class="item-category">${item.category}</span>
                <span class="item-condition">${getConditionText(item.itemCondition)}</span>
            </div>
            <div class="item-location">
                <span>📍${item.regionGu} ${item.regionDong}</span>
                <span class="item-time">${formatTimeAgo(item.createdAt)}</span>
            </div>
            <div class="item-stats">
                <span class="stat-item">👁️ ${item.viewCount}</span>
            </div>
        </div>
    `;

    card.addEventListener('click', () => {
        window.location.href = `/free/${item.freeId}`;
    });

    return card;
}

// // 결과 없음 표시 함수
// function showEmptyState() {
//     const itemsGrid = document.getElementById('itemsGrid');
//     if (!itemsGrid) return;
//
//     const emptyDiv = document.createElement('div');
//     emptyDiv.className = 'no-items-content';
//     emptyDiv.innerHTML = `
//         <h3>😔 검색 조건에 맞는 나눔이 없습니다</h3>
//         <p>다른 조건으로 검색해보시거나, 직접 나눔을 등록해보세요!</p>
//         <a href="/free/register" class="btn btn-primary">나눔 등록하기</a>
//     `;
//     itemsGrid.appendChild(emptyDiv);
// }


// Page initialization
function initializePage() {
    // 헤더 스크롤 효과
    window.addEventListener('scroll', () => {
        if (window.scrollY > 100) {
            header.classList.add('scrolled');
        } else {
            header.classList.remove('scrolled');
        }
    });
    // 햄버거 메뉴 클릭 시 모바일 메뉴 토글
    const hamburger = document.getElementById('hamburger');
    if (hamburger) {
        hamburger.addEventListener('click', () => {
            hamburger.classList.toggle('active');

            const navMenu = document.getElementById('navMenu');
            if (navMenu) {
                navMenu.classList.toggle('active');
            }
        });
    }

}

// 페이지가 동작하게 만드는 핵심 JS 함수
function setupEventListeners() {
    // 모달 열기 버튼 클릭 시
    addItemBtn.addEventListener('click', () => openModal(addItemModal));
    // 나눔 등록 버튼 클릭하면 addItemModal 모달이 열림
    closeModal.addEventListener('click', () => closeModalHandler(addItemModal));
    closeDetailModal.addEventListener('click', () => closeModalHandler(itemDetailModal));
    
    // 모달 우측 상단의 X 버튼 클릭 시 모달 닫힘
    window.addEventListener('click', (e) => {
        if (e.target === addItemModal) closeModalHandler(addItemModal);
        if (e.target === itemDetailModal) closeModalHandler(itemDetailModal);
    });

    // 모달 영역 바깥을 클릭하면 해당 모달 닫힘
    addItemForm.addEventListener('submit', handleFormSubmit);
    
    // 나눔 등록 폼 제출 시 handleFormSubmit 함수 실행 (폼 데이터 처리)
    document.getElementById('cancelBtn').addEventListener('click', () => {
        closeModalHandler(addItemModal); // 취소 버튼 누르면 등록 모달 닫힘
    });

    // 필터 & 정렬
    document.getElementById('categoryFilter').addEventListener('change', applyFilters);
    document.getElementById('distanceFilter').addEventListener('change', applyFilters);
    document.getElementById('statusFilter').addEventListener('change', applyFilters);
    document.getElementById('searchInput').addEventListener('input', debounce(applyFilters, 300));
    document.getElementById('sortSelect').addEventListener('change', applySorting);
    
    // 더보기 버튼을 누르면 다음 페이지의 아이템을 더 불러옴
    loadMoreBtn.addEventListener('click', loadMoreItems);

    // 파일 업로드 영역(사진 업로드)을 초기화하고 관련 이벤트를 설정하는 함수
    setupFileUpload();
}

// Load items
function loadItems() {
     // currentItems = [...sampleItems];
    filteredItems = [...currentItems];
    currentPage = 1;     // 페이징 처리를 위해 현재 페이지를 1페이지로 초기화
    renderItems();      // 실제 화면에 카드들을 그려주는 함수
    updateItemCount(); // 상단에 있는 "총 N건의 나눔 물건" 업데이트하는 함수
    renderPagination(); // 페이징 렌더링 추가
}

// 아이템 전체를 화면에 렌더링
function renderItems(append = false) {
    // 1. DOM 요소 존재 여부 확인
    const itemsGrid = document.getElementById('itemsGrid');
    const loadMoreBtn = document.getElementById('loadMoreBtn');
    const pagination = document.getElementById('pagination');

    if (!itemsGrid || !loadMoreBtn || !pagination) {
        console.warn(' 필수 DOM 요소가 존재하지 않아 렌더링을 중단합니다.');
        return;
    }

    // 2. 초기 렌더링 시 이전 내용 제거
    if (!append) {
        itemsGrid.innerHTML = '';
        currentPage = currentPage || 1;
    }

    // 3. 현재 보여줄 항목 계산
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const itemsToShow = filteredItems.slice(startIndex, endIndex);

    // 4. 결과가 없을 때
    if (itemsToShow.length === 0 && currentPage === 1) {
        showEmptyState?.(); // 함수가 있으면 실행
        loadMoreBtn.style.display = 'none';
        pagination.style.display = 'none';
        return;
    }

    // 5. 아이템 렌더링
    itemsToShow.forEach(item => {
        if(!item) {
            console.warn("item이 undefined입니다. 필터링된 데이터:", filteredItems);
            return;
        }

        const itemElement = createItemElement(item);
        itemsGrid.appendChild(itemElement);
    });

    // 6. 더보기 버튼 표시 여부
    if (endIndex >= filteredItems.length) {
        loadMoreBtn.style.display = 'none';
    } else {
        loadMoreBtn.style.display = 'block';
    }

    // 7. 페이지네이션 표시
    pagination.style.display = 'flex';
    renderPagination?.(); // 함수가 있으면 실행
}

// 새로운 페이징 렌더링 함수
function renderPagination() {
    const totalPages = Math.ceil(filteredItems.length / itemsPerPage);
    
    if (totalPages <= 1) {
        pagination.style.display = 'none';
        return;
    }

    pagination.style.display = 'flex';
    pagination.innerHTML = '';

    // 이전 버튼
    const prevBtn = createPaginationButton('‹', currentPage - 1, currentPage === 1);
    pagination.appendChild(prevBtn);

    // 페이지 번호 버튼들
    const pageNumbers = generatePageNumbers(currentPage, totalPages);
    
    pageNumbers.forEach(pageNum => {
        if (pageNum === '...') {
            const ellipsis = document.createElement('span');
            ellipsis.className = 'pagination-ellipsis';
            ellipsis.textContent = '...';
            pagination.appendChild(ellipsis);
        } else {
            const pageBtn = createPaginationButton(pageNum, pageNum, false, pageNum === currentPage);
            pagination.appendChild(pageBtn);
        }
    });

    // 다음 버튼
    const nextBtn = createPaginationButton('›', currentPage + 1, currentPage === totalPages);
    pagination.appendChild(nextBtn);
}

// 페이징 버튼 생성 함수
function createPaginationButton(text, pageNum, disabled = false, active = false) {
    const button = document.createElement('button');
    button.className = `pagination-btn ${active ? 'active' : ''} ${disabled ? 'disabled' : ''}`;
    button.textContent = text;
    
    if (!disabled) {
        button.addEventListener('click', () => {
            currentPage = pageNum;
            renderItems();
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
    }
    
    return button;
}

// 페이지 번호 생성 함수 (10페이지가 넘으면 11페이지로 확장)
function generatePageNumbers(current, total) {
    const pages = [];
    const maxVisible = 7; // 최대 표시할 페이지 수
    
    if (total <= maxVisible) {
        // 총 페이지가 7개 이하면 모두 표시
        for (let i = 1; i <= total; i++) {
            pages.push(i);
        }
    } else {
        // 총 페이지가 7개 초과인 경우
        if (current <= 4) {
            // 현재 페이지가 앞쪽에 있을 때
            for (let i = 1; i <= 5; i++) {
                pages.push(i);
            }
            pages.push('...');
            pages.push(total);
        } else if (current >= total - 3) {
            // 현재 페이지가 뒤쪽에 있을 때
            pages.push(1);
            pages.push('...');
            for (let i = total - 4; i <= total; i++) {
                pages.push(i);
            }
        } else {
            // 현재 페이지가 중간에 있을 때
            pages.push(1);
            pages.push('...');
            for (let i = current - 1; i <= current + 1; i++) {
                pages.push(i);
            }
            pages.push('...');
            pages.push(total);
        }
    }
    
    return pages;
}

// Create item element
function createItemElement(item) {
    const itemDiv = document.createElement('div');
    itemDiv.className = 'item-card';
    itemDiv.addEventListener('click', () => openItemDetail(item));

    itemDiv.innerHTML = `
        <div class="item-image">
            <img src="${item.imgUrl || '/img/default-sharing.svg'}" alt="${item.title}">
            <div class="item-status">${getStatusText(item.dealStatus)}</div>
        </div>
        <div class="item-info">
            <h3 class="item-title">${item.title}</h3>
            <div class="item-meta">
                <span class="item-category">${item.category}</span>
                <span class="item-condition">${getConditionText(item.itemCondition)}</span>
            </div>
             <div class="item-location">
                <span>📍${item.regionGu} ${item.regionDong}</span>
                <span class="item-time">${formatTimeAgo(item.createdAt)}</span>
            </div>
             <div class="item-stats">
                <span class="stat-item">👁️ ${item.viewCount}</span>
            </div>
        </div>
    `;

    return itemDiv;
}

// Open item detail modal
function openItemDetail(item) {
    const detailContent = document.getElementById('detailContent');
    document.getElementById('detailTitle').textContent = item.title;
    
    detailContent.innerHTML = `
        <div class="detail-images">
            <div class="main-image">
                ${item.images[0]}
            </div>
            <div class="thumbnail-list">
                ${item.images.map((img, index) => `
                    <div class="thumbnail ${index === 0 ? 'active' : ''}">${img}</div>
                `).join('')}
            </div>
        </div>
        <div class="detail-info">
            <div class="detail-header">
                <h2 class="detail-title">${item.title}</h2>
                <span class="item-category">${item.categoryName}</span>
                <div class="detail-status ${item.status}">${item.statusText}</div>
            </div>
            
            <div class="detail-description">
                <h4>상세 설명</h4>
                <p>${item.description}</p>
            </div>
            
            <div class="detail-meta">
                <div class="meta-item">
                    <span class="meta-label">나눔 장소</span>
                    <span class="meta-value">${item.location}</span>
                </div>
                <div class="meta-item">
                    <span class="meta-label">거리</span>
                    <span class="meta-value">${item.distance}</span>
                </div>
                <div class="meta-item">
                    <span class="meta-label">등록시간</span>
                    <span class="meta-value">${item.time}</span>
                </div>
                <div class="meta-item">
                    <span class="meta-label">조회수</span>
                    <span class="meta-value">${item.views}회</span>
                </div>
                <div class="meta-item">
                    <span class="meta-label">관심</span>
                    <span class="meta-value">❤️ ${item.likes}</span>
                </div>
            </div>
            
            <div class="detail-actions">
                ${item.status === 'available' ? `
                    <button class="btn btn-primary" onclick="requestItem(${item.id})">나눔 요청하기</button>
                    <button class="btn btn-secondary" onclick="likeItem(${item.id})">관심 표시 ❤️</button>
                ` : item.status === 'reserved' ? `
                    <button class="btn btn-secondary" disabled>예약중입니다</button>
                ` : `
                    <button class="btn btn-secondary" disabled>완료된 나눔입니다</button>
                `}
            </div>
            
            <div class="contact-info">
                <h4>나눔자 정보</h4>
                <p><strong>닉네임:</strong> ${item.author}</p>
                <p><strong>연락처:</strong> ${item.contact}</p>
                <p><strong>나눔 기여도:</strong> ⭐⭐⭐⭐⭐ (4.8/5.0)</p>
            </div>
        </div>
    `;

    openModal(itemDetailModal);
}

// Apply filters
function applyFilters() {
    const category = document.getElementById('categoryFilter').value;
    const distance = document.getElementById('distanceFilter').value;
    const status = document.getElementById('statusFilter').value;
    const search = document.getElementById('searchInput').value.toLowerCase();

    filteredItems = currentItems.filter(item => {
        if (category && item.category !== category) return false;
        if (status && item.status !== status) return false;
        if (search && !item.title.toLowerCase().includes(search) && 
            !item.description.toLowerCase().includes(search)) return false;
        
        // Distance filter (simplified)
        if (distance) {
            const itemDistance = parseFloat(item.distance);
            const maxDistance = parseFloat(distance) / 1000; // Convert m to km
            if (itemDistance > maxDistance) return false;
        }
        
        return true;
    });

    currentPage = 1;
    renderItems();
    updateItemCount();
}

// Apply sorting
function applySorting() {
    const sortBy = document.getElementById('sortSelect').value;
    
    filteredItems.sort((a, b) => {
        switch (sortBy) {
            case 'recent':
                return new Date(b.time) - new Date(a.time);
            case 'distance':
                return parseFloat(a.distance) - parseFloat(b.distance);
            case 'popular':
                return b.views - a.views;
            default:
                return 0;
        }
    });

    currentPage = 1;
    renderItems();
}

// Load more items (기존 기능 유지)
function loadMoreItems() {
    currentPage++;
    renderItems(true);
}

// Update item count
function updateItemCount() {
    totalItems.textContent = `총 ${filteredItems.length}개`;
}

// Show empty state
function showEmptyState() {
    itemsGrid.innerHTML = `
        <div class="empty-state" style="grid-column: 1 / -1;">
            <div class="empty-icon">🔍</div>
            <h3>검색 결과가 없습니다</h3>
            <p>다른 검색어나 필터를 시도해보세요.<br>또는 새로운 나눔 물건을 등록해보세요!</p>
        </div>
    `;
}

// Modal functions
function openModal(modal) {
    modal.classList.add('show');
    document.body.style.overflow = 'hidden';
}

function closeModalHandler(modal) {
    modal.classList.remove('show');
    document.body.style.overflow = 'auto';
    
    if (modal === addItemModal) {
        resetForm();
    }
}

// Form handling
function handleFormSubmit(e) {
    e.preventDefault();
    
    const formData = new FormData(addItemForm);
    const newItem = {
        id: Date.now(),
        title: document.getElementById('itemTitle').value,
        category: document.getElementById('itemCategory').value,
        categoryName: getCategoryName(document.getElementById('itemCategory').value),
        description: document.getElementById('itemDescription').value || '상세 설명이 없습니다.',
        location: `${document.getElementById('regionGu').value || ''} ${document.getElementById('regionDong').value || ''}`.trim() || '장소 미정',
        distance: '0m',
        status: 'available',
        statusText: '나눔중',
        time: '방금 전',
        author: '나',
        contact: '010-0000-0000',
        images: ['📦'],
        views: 0,
        likes: 0
    };

    // Add to items
    currentItems.unshift(newItem);
    
    // Show success message
    showNotification('나눔 물건이 성공적으로 등록되었습니다!', 'success');
    
    // Close modal and refresh
    closeModalHandler(addItemModal);
    applyFilters();
}

// Reset form
function resetForm() {
    addItemForm.reset();
    document.getElementById('imagePreview').innerHTML = '';
}

// Get category name
function getCategoryName(category) {
    const categories = {
        'furniture': '가구',
        'appliances': '가전',
        'accessory': '잡화',
        'etc': '기타'
    };
    return categories[category] || '기타';
}

// File upload handling
function setupFileUpload() {
    const uploadArea = document.getElementById('uploadArea');
    const fileInput = document.getElementById('itemImages');
    const imagePreview = document.getElementById('imagePreview');
    
    uploadArea.addEventListener('click', () => fileInput.click());
    
    // Drag and drop
    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.classList.add('dragover');
    });
    
    uploadArea.addEventListener('dragleave', () => {
        uploadArea.classList.remove('dragover');
    });
    
    uploadArea.addEventListener('drop', (e) => {
        e.preventDefault();
        uploadArea.classList.remove('dragover');
        
        const files = e.dataTransfer.files;
        handleFiles(files);
    });
    
    fileInput.addEventListener('change', (e) => {
        handleFiles(e.target.files);
    });
    
    function handleFiles(files) {
        const maxFiles = 5;
        const currentImages = imagePreview.children.length;
        
        if (currentImages + files.length > maxFiles) {
            showNotification(`최대 ${maxFiles}장까지만 업로드 가능합니다.`, 'error');
            return;
        }
        
        Array.from(files).forEach(file => {
            if (file.type.startsWith('image/')) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    const previewItem = document.createElement('div');
                    previewItem.className = 'preview-item';
                    previewItem.innerHTML = `
                        <img src="${e.target.result}" alt="Preview">
                        <button type="button" class="preview-remove" onclick="removePreview(this)">&times;</button>
                    `;
                    imagePreview.appendChild(previewItem);
                };
                reader.readAsDataURL(file);
            }
        });
    }
}

// Remove preview image
function removePreview(button) {
    button.parentElement.remove();
}

// Item interaction functions
function requestItem(itemId) {
    showNotification('나눔 요청이 전송되었습니다! 나눔자가 연락드릴 예정입니다.', 'success');
    closeModalHandler(itemDetailModal);
}

function likeItem(itemId) {
    const item = currentItems.find(item => item.id === itemId);
    if (item) {
        item.likes++;
        showNotification('관심 표시가 등록되었습니다! ❤️', 'success');
        
        // Update UI if detail modal is open
        const likeElement = document.querySelector('.meta-value');
        if (likeElement && likeElement.textContent.includes('❤️')) {
            likeElement.textContent = `❤️ ${item.likes}`;
        }
    }
}

// Notification system
function showNotification(message, type = 'success') {
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }
    
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    // Notification styles
    notification.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: ${type === 'success' ? 'var(--primary-green)' : type === 'error' ? '#dc3545' : 'var(--accent-green)'};
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
    
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);
    
    setTimeout(() => {
        notification.style.transform = 'translateX(400px)';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// Utility functions
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Real-time updates simulation
function startRealTimeUpdates() {
    setInterval(() => {
        // Simulate new items being added
        if (Math.random() < 0.1) { // 10% chance every interval
            simulateNewItem();
        }
        
        // Update view counts randomly
        currentItems.forEach(item => {
            if (Math.random() < 0.05) { // 5% chance per item
                item.views += Math.floor(Math.random() * 3) + 1;
            }
        });
        
        // Update times
        updateItemTimes();
    }, 30000); // Every 30 seconds
}

// function simulateNewItem() {
//     const categories = ['clothes', 'electronics', 'furniture', 'books', 'kids', 'etc'];
//     const locations = ['강남역', '홍대입구역', '신촌역', '이태원역', '잠실역', '여의도역'];
//     const items = ['책상', '의자', '가방', '신발', '책', '화분', '램프', '쿠션'];
//
//     const randomCategory = categories[Math.floor(Math.random() * categories.length)];
//     const randomLocation = locations[Math.floor(Math.random() * locations.length)];
//     const randomItem = items[Math.floor(Math.random() * items.length)];
//
//     const newItem = {
//         id: Date.now() + Math.random(),
//         title: `${randomItem} 나눔합니다`,
//         category: randomCategory,
//         categoryName: getCategoryName(randomCategory),
//         description: `상태 좋은 ${randomItem}입니다. 필요하신 분께 나눔해요.`,
//         location: randomLocation,
//         distance: `${(Math.random() * 5 + 0.5).toFixed(1)}km`,
//         status: 'available',
//         statusText: '나눔중',
//         time: '방금 전',
//         author: `${String.fromCharCode(65 + Math.floor(Math.random() * 26))}○○님`,
//         contact: '010-****-****',
//         images: ['📦'],
//         views: 1,
//         likes: 0
//     };
//
//     currentItems.unshift(newItem);
//
//     // If no filters applied, show the new item
//     if (document.getElementById('categoryFilter').value === '' &&
//         document.getElementById('statusFilter').value === '' &&
//         document.getElementById('searchInput').value === '') {
//         filteredItems.unshift(newItem);
//         updateItemCount();
//         renderPagination();
//
//         // Show notification about new item
//         showNotification(`새로운 나눔이 등록되었습니다: ${newItem.title}`, 'info');
//     }
// }

function updateItemTimes() {
    // This would update the time display in real applications
    // For demo purposes, we'll just log it
    console.log('시간 업데이트됨');
}

// Geolocation functions (simplified for demo)
function getCurrentLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            (position) => {
                console.log('위치 정보 획득:', position.coords);
                // In real app, this would update distance calculations
            },
            (error) => {
                console.warn('위치 정보 접근 실패:', error);
            }
        );
    }
}

// Advanced search functionality
function setupAdvancedSearch() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        // Add search suggestions
        searchInput.addEventListener('focus', showSearchSuggestions);
        searchInput.addEventListener('blur', hideSearchSuggestions);
    }
}

function showSearchSuggestions() {
    // Extract common terms from item titles
    const terms = new Set();
    currentItems.forEach(item => {
        item.title.split(' ').forEach(word => {
            if (word.length > 2) {
                terms.add(word);
            }
        });
    });
    
    // This would show a dropdown with suggestions in a real app
    console.log('검색 제안:', Array.from(terms).slice(0, 5));
}

function hideSearchSuggestions() {
    // Hide suggestions dropdown
    setTimeout(() => {
        console.log('검색 제안 숨김');
    }, 200);
}

// Analytics and tracking (simplified)
function trackUserInteraction(action, itemId = null) {
    const event = {
        action: action,
        itemId: itemId,
        timestamp: new Date().toISOString(),
        userAgent: navigator.userAgent
    };
    
    // In real app, this would send to analytics service
    console.log('사용자 상호작용 추적:', event);
}

// Add event tracking to existing functions
const originalOpenItemDetail = openItemDetail;
openItemDetail = function(item) {
    trackUserInteraction('view_item', item.id);
    return originalOpenItemDetail.call(this, item);
};

const originalRequestItem = requestItem;
requestItem = function(itemId) {
    trackUserInteraction('request_item', itemId);
    return originalRequestItem.call(this, itemId);
};

// Performance optimization
function optimizeImages() {
    // In real app, this would lazy load images and optimize them
    const images = document.querySelectorAll('img[data-src]');
    
    if ('IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.classList.remove('lazy');
                    imageObserver.unobserve(img);
                }
            });
        });
        
        images.forEach(img => imageObserver.observe(img));
    }
}

// Initialize with more data
// addMoreSampleData();

// Start real-time updates and get location
setTimeout(() => {
    startRealTimeUpdates();
    getCurrentLocation();
    setupAdvancedSearch();
    optimizeImages();
}, 2000);

// Export functions for testing
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        loadItems,
        applyFilters,
        createItemElement,
        showNotification,
        renderPagination,
        generatePageNumbers
    };
}