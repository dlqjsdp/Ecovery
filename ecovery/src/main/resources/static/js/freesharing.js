/*서버에서 받은 데이터를 사용자가 읽기 좋게 바꿔주는 유틸리티 함수*/
// 거래상태
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
        case 'HIGH': return "상 (매우 좋음)";
        case 'MEDIUM': return '중 (보통)';
        case 'LOW': return '하 (사용감 있음)';
        default: return '상 (매우 좋음)';
    }
}

// 등록된 시간이 현재 시간보다 얼마나 지났는지 계산
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
let currentItems = [];  // 전체 나눔 게시글 데이터를 담는 배열
let filteredItems = []; // 현재 필터링된 상태의 게시글들만 담는 배열
let currentPage = 1;  // 현재 보고 있는 페이지 번호 (페이징용)
const itemsPerPage = 8; // 한 페이지당 보여줄 게시글 수 (8개로 고정)
// let totalPages = 1;
let sharingItemsData = []; // 서버에서 받아온 전체 데이터를 원본 형태로 저장

// DOM elements - HTML 요소(id로 찾은 것들)를 미리 변수로 선언해 놓은 코드
const header = document.getElementById('header');
const hamburger = document.getElementById('hamburger');
const navMenu = document.getElementById('navMenu');
const addItemBtn = document.getElementById('addItemBtn');
const itemsGrid = document.getElementById('itemsGrid');
const loadMoreBtn = document.getElementById('loadMoreBtn');
const totalItems = document.getElementById('totalItems');
const pagination = document.getElementById('pagination');


/* 페이지가 처음 로딩되었을 때 실행되는 함수*/
// 1. 페이지 로드 후 실행
document.addEventListener('DOMContentLoaded', async function() {
    initializePage(); // 필터/모달 초기화

    // 첫 페이지 로딩
    loadPage(currentPage);
});

// 페이지 번호에 따라 서버에서 데이터 요청하고 렌더링하는 함수
async function loadPage(pageNum) {
    try {
        // 2. API로 데이터 요청
        const res = await fetch(`/api/free/list?pageNum=${currentPage}&amount=${itemsPerPage}`);
        // const res = await fetch('/api/free/list'); // 실제 API URL 로딩
        const result = await res.json();

        console.log('전체 result:', result);
        console.log('result.list:', result.list);
        console.log('result.content:', result.content);

        // 3. 응답 데이터 파싱 - 받은 데이터가 .list에 있으면 쓰고 아니면 .content 확인
        const list = Array.isArray(result.list)
            ? result.list
            : Array.isArray(result.content)
                ? result.content
                : []; // 아무것도 없으면 빈 배열

        sharingItemsData = list; // 원본 전체

        // 4. 렌더링 대상 배열 복사 - 현재 화면에 보여줄 대상 (필터링에 사용됨)
        currentItems = [...sharingItemsData];
        filteredItems = [...currentItems];

        // pageMaker에서 total 페이지 수 계산
        const totalCount = result.pageMaker?.total || list.length;
        totalPages = Math.ceil(totalCount / itemsPerPage);

        // 5. 렌더링 함수 실행
        renderItems();  // 아이템 카드 렌더링
        updateItemCount(); // 아이템 수 갱신
        renderPagination(totalPages); // 페이징 처리

        // 6. 버튼, 모달 등의 이벤트 바인딩 - DOM 렌더링 순서가 뒤엉키는 걸 방지
        window.requestAnimationFrame(() => {
            setupEventListeners();
        });

    } catch (err) {
        console.error('데이터 불러오기 실패 : ', err);
    }
}

// 현재 필터링된 게시글 수를 화면에 표시하는 함수
function updateItemCount() {
    const totalItems = document.getElementById('totalItems');
    if (totalItems) {
        totalItems.textContent = `총 ${filteredItems.length}건`;
    } else {
        console.warn('⚠️ totalItems 요소를 찾을 수 없습니다.');
    }
}


// 무료나눔 게시글을 아이템 카드 생성 함수
function createItemElement(item) {
    const card = document.createElement('div');
    card.className = 'sharing-card';

    card.innerHTML = `
        <div class="item-image">
            <img src="${item.imgUrl || '/img/logo.png'}" alt="${item.title}">
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

    //상세페이지로 이동
    card.addEventListener('click', () => {
        window.location.href = `/free/get.html?id=${item.freeId}`;
    });

    return card;
}

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

function setupEventListeners() {
//페이지 이동
document.getElementById('addItemBtn')?.addEventListener('click', () => {
    window.location.href = '/free/register';
});

// 필터 & 정렬
document.getElementById('categoryFilter')?.addEventListener('change', applyFilters);
document.getElementById('searchInput')?.addEventListener('input', debounce(applyFilters, 300));
document.getElementById('searchInput')?.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        e.preventDefault();
        applyFilters();
    }
});
document.getElementById('sortSelect')?.addEventListener('change', applySorting);
document.getElementById('searchBtn')?.addEventListener('click', applyFilters);


// 더보기 버튼을 누르면 다음 페이지의 아이템을 더 불러옴
document.getElementById('loadMoreBtn')?.addEventListener('click', loadMoreItems);

}


// Load items
function loadItems() {
    // currentItems = [...sampleItems];
    // 현재 불러온 전체 데이터를 안전하게 필터링해서 렌더링에 사용할 준비
    filteredItems = [...currentItems].filter(item => item && typeof item === 'object');

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

    // 1. 기존 내용 초기화
    itemsGrid.innerHTML = '';

    // 2. 결과 없을 때 처리
    if (!filteredItems || filteredItems.length === 0) {
        showEmptyState?.();
        loadMoreBtn.style.display = 'none';
        pagination.style.display = 'none';
        return;
    }

    // 3. 서버에서 이미 페이징된 데이터를 받아왔기 때문에 slice 생략
    const itemsToShow = filteredItems;


    // 4. 아이템 렌더링
    itemsToShow.forEach(item => {
        if (!item) {
            console.warn("item이 undefined입니다. 필터링된 데이터:", filteredItems);
            return;
        }

        const itemElement = createItemElement(item);
        itemsGrid.appendChild(itemElement);
    });

    // 5. 더보기 버튼 숨김, 페이지네이션 표시
    loadMoreBtn.style.display = 'none';
    pagination.style.display = 'flex';
    renderPagination?.(); // 페이지네이션 렌더링 함수 실행
}

// 새로운 페이징 렌더링 함수
function renderPagination() {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;

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
        button.addEventListener('click', async () => {
            // '...' 버튼 클릭 시에는 아무 동작도 하지 않음
            if (text === '...') {
                return;
            }
            currentPage = pageNum;

            await loadPage(currentPage); // 서버에서 해당 페이지 데이터
            renderPagination(totalPages); // 페이지 버튼 active 개신
            window.scrollTo({ top: 0, behavior: 'smooth' }); // 페이지 상단으로 스크롤
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
    itemDiv.addEventListener('click', () => {
        window.location.href = `/free/get/${item.freeId}`;
    });

    itemDiv.innerHTML = `
        <div class="item-image">
            <img src="${item.imgUrl || '/img/logo.png'}" alt="${item.title}">
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

const detailContent = document.getElementById('detailContent');
    document.getElementById('detailTitle').textContent = item.title;

// 이미지 배열 안전 처리 - imgList에서 imgUrl만 추출
    const images = Array.isArray(item.imgList)
        ? item.imgList.map(img => img.imgUrl)
        : [];

// 대표 이미지 설정 (첫번째 이미지 또는 기본이미지)
    const mainImage = images.length > 0
        ? `<img src="${images[0]}" alt="대표 이미지">`
        : `<img src="/img/logo.png" alt="기본 이미지">`;

// 썸네일 리스트 HTML 생성
    const thumbnails = images.length > 0
        ? images.map((img, index) => `
            <div class="thumbnail ${index === 0 ? 'active' : ''}">
                <img src="${img}" alt="썸네일 ${index + 1}">
            </div>
        `).join('')
        : `<div class="thumbnail active"><img src="/img/logo2.png" alt="기본 썸네일"></div>`;

// 상세 내용 렌더링
    detailContent.innerHTML = `
        <div class="detail-images">
            <div class="main-image">
                ${mainImage}
            </div>
            <div class="thumbnail-list">
                ${thumbnails}
            </div>
            
            <div class="thumbnail-list">
                ${images.map((img, index) => `
                    <div class="thumbnail ${index === 0 ? 'active' : ''}">${img}</div>
                `).join('')}
            </div>
        </div>
        <div class="detail-info">
            <div class="detail-header">
                <h2 class="detail-title">${item.title}</h2>
                <span class="item-category">${item.category || '기타'}</span>
                <div class="detail-status ${item.dealStatus}">${item.dealStatus === 'DONE' ? '나눔 완료' : '나눔중'}</div>
            </div>
            
            <div class="detail-description">
                <h4>상세 설명</h4>
                <p>${item.content || '설명이 없습니다.'}</p>
            </div>
            
            <div class="detail-meta">
                <div class="meta-item">
                    <span class="meta-label">나눔 장소</span>
                    <span class="meta-value">${item.regionGu} ${item.regionDong}</span>
                </div>
                <div class="meta-item">
                    <span class="meta-label">등록시간</span>
                    <span class="meta-value">${item.createdAt}</span>
                </div>
                <div class="meta-item">
                    <span class="meta-label">조회수</span>
                    <span class="meta-value">${item.viewCount}회</span>
                </div>
            </div>
        </div>
    `;




// 무료나눔 게시판에서 검색 필터링 기능을 수행하는 함수
function applyFilters() {
    const searchScope = document.getElementById('categoryFilter').value; // title, region, content 중 선택
    const search = document.getElementById('searchInput').value.toLowerCase();

    filteredItems = currentItems.filter(item => {
        const title = item.title?.toLowerCase() || '';
        const content = item.content?.toLowerCase() || '';
        const region = (item.regionGu + ' ' + item.regionDong)?.toLowerCase() || '';

        // 검색어가 있을 때, 선택된 필드만 검사
        if (search) {
            switch (searchScope) {
                case 'title':
                    if (!title.includes(search)) return false;
                    break;
                case 'content':
                    if (!content.includes(search)) return false;
                    break;
                case 'region':
                    if (!region.includes(search)) return false;
                    break;
                default: // 전체
                    if (!title.includes(search) && !content.includes(search) && !region.includes(search)) {
                        return false;
                    }
            }
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

function updateItemCount() {
    const totalItems = document.getElementById('totalItems');
    if (totalItems) {
        totalItems.textContent = `총 ${filteredItems.length}건`;
    } else {
        console.warn('⚠️ totalItems 요소를 찾을 수 없습니다.');
    }
}
// 결과없음을 보여주는 코드
function showEmptyState() {
    itemsGrid.innerHTML = `
        <div class="empty-state" style="grid-column: 1 / -1;">
            <div class="empty-icon">🔍</div>
            <h3>검색 결과가 없습니다</h3>
            <p>다른 검색어나 필터를 시도해보세요.<br>또는 새로운 나눔 물건을 등록해보세요!</p>
            <a href="/free/register" class="btn btn-primary">나눔 등록하기</a>
        </div>
    `;
}


// 해당하는 한글 카테고리명
function getCategoryName(category) {
    const categories = {
        'furniture': '가구',
        'appliances': '가전',
        'accessory': '잡화',
        'etc': '기타'
    };
    return categories[category] || '기타';
}

// 알림창 예쁘게 보여주는 코드
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

// 디바운스 유틸리티 함수 - 자주 실행되는 함수의 호출 횟수를 제어해서 성능을 향상시키는 함수
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

// // Geolocation functions (simplified for demo)
// function getCurrentLocation() {
//     if (navigator.geolocation) {
//         navigator.geolocation.getCurrentPosition(
//             (position) => {
//                 console.log('위치 정보 획득:', position.coords);
//                 // In real app, this would update distance calculations
//             },
//             (error) => {
//                 console.warn('위치 정보 접근 실패:', error);
//             }
//         );
//     }
// }

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