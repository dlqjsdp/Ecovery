// =========================
// 상품 상세 페이지 JavaScript
// 기존 product-edit.js와 별도로 사용
// =========================

document.addEventListener('DOMContentLoaded', function() {
    // 수량 조절 기능 초기화
    initQuantityControls();

    // 썸네일 이미지 기능 초기화 (있는 경우)
    initThumbnailGallery();

    // 장바구니 및 구매 버튼 기능 초기화
    initPurchaseButtons();

    // 공유하기 기능 초기화
    initShareButtons();
});

// =========================
// 수량 조절 기능
// =========================
function initQuantityControls() {
    const minusBtn = document.querySelector('.quantity-btn.minus');
    const plusBtn = document.querySelector('.quantity-btn.plus');
    const quantityInput = document.getElementById('quantity');

    if (minusBtn && plusBtn && quantityInput) {
        minusBtn.addEventListener('click', function() {
            const currentValue = parseInt(quantityInput.value);
            if (currentValue > 1) {
                quantityInput.value = currentValue - 1;
            }
        });

        plusBtn.addEventListener('click', function() {
            const currentValue = parseInt(quantityInput.value);
            const maxValue = parseInt(quantityInput.max);
            if (currentValue < maxValue) {
                quantityInput.value = currentValue + 1;
            }
        });

        // 직접 입력 시 유효성 검사
        quantityInput.addEventListener('change', function() {
            const value = parseInt(this.value);
            const min = parseInt(this.min);
            const max = parseInt(this.max);

            if (value < min) {
                this.value = min;
            } else if (value > max) {
                this.value = max;
                alert(`최대 ${max}개까지 주문 가능합니다.`);
            }
        });
    }
}

// =========================
// 썸네일 이미지 갤러리 기능
// =========================
function initThumbnailGallery() {
    const thumbnails = document.querySelectorAll('.thumbnail');
    const mainImage = document.getElementById('mainImage');

    if (thumbnails && mainImage) {
        thumbnails.forEach(thumbnail => {
            thumbnail.addEventListener('click', function() {
                // 기존 active 클래스 제거
                thumbnails.forEach(thumb => thumb.classList.remove('active'));

                // 클릭한 썸네일에 active 클래스 추가
                this.classList.add('active');

                // 메인 이미지 변경
                mainImage.src = this.src;
                mainImage.alt = this.alt;
            });
        });
    }
}

// =========================
// 장바구니 및 구매 버튼 기능
// =========================
function initPurchaseButtons() {
    const cartBtn = document.querySelector('.btn-cart');
    const buyBtn = document.querySelector('.btn-buy');

    if (cartBtn) {
        cartBtn.addEventListener('click', function() {
            const quantity = document.getElementById('quantity')?.value || 1;
            addToCart(quantity);
        });
    }

    if (buyBtn) {
        buyBtn.addEventListener('click', function() {
            const quantity = document.getElementById('quantity')?.value || 1;
            buyNow(quantity);
        });
    }
}

// =========================
// 장바구니 추가 기능
// =========================
function addToCart(quantity) {
    // 로그인 체크
    if (!checkLoginStatus()) {
        if (confirm('로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?')) {
            window.location.href = '/members/login';
        }
        return;
    }

    // 여기에 실제 장바구니 추가 로직 구현
    const productId = getProductIdFromUrl();

    fetch('/api/cart/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            itemId: productId,
            count: parseInt(quantity)
        })
    })
        .then(response => {
            if (response.ok) {
                if (confirm('장바구니에 상품이 담겼습니다. 장바구니로 이동하시겠습니까?')) {
                    window.location.href = '/cart';
                }
            } else {
                alert('장바구니 추가에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('오류가 발생했습니다.');
        });
}

// =========================
// 즉시 구매 기능
// =========================
function buyNow(quantity) {
    // 로그인 체크
    if (!checkLoginStatus()) {
        if (confirm('로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?')) {
            window.location.href = '/members/login';
        }
        return;
    }

    // 즉시 구매 로직 (주문 페이지로 이동)
    const productId = getProductIdFromUrl();
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/order';

    const itemIdInput = document.createElement('input');
    itemIdInput.type = 'hidden';
    itemIdInput.name = 'itemId';
    itemIdInput.value = productId;

    const countInput = document.createElement('input');
    countInput.type = 'hidden';
    countInput.name = 'count';
    countInput.value = quantity;

    form.appendChild(itemIdInput);
    form.appendChild(countInput);
    document.body.appendChild(form);
    form.submit();
}

// =========================
// 공유하기 기능
// =========================
function initShareButtons() {
    const shareButtons = document.querySelectorAll('.share-btn');

    shareButtons.forEach(button => {
        button.addEventListener('click', function() {
            const type = this.dataset.type;
            const url = window.location.href;
            const title = document.querySelector('.product-title')?.textContent || '에코마켓 상품';

            switch(type) {
                case 'link':
                    copyToClipboard(url);
                    break;
                case 'kakao':
                    shareToKakao(url, title);
                    break;
                case 'facebook':
                    shareToFacebook(url);
                    break;
                case 'twitter':
                    shareToTwitter(url, title);
                    break;
            }
        });
    });
}

// =========================
// 공유 기능 구현
// =========================
function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(function() {
        alert('링크가 클립보드에 복사되었습니다.');
    }, function() {
        alert('링크 복사에 실패했습니다.');
    });
}

function shareToKakao(url, title) {
    // 카카오톡 공유 API 사용 (카카오 SDK 필요)
    if (typeof Kakao !== 'undefined') {
        Kakao.Share.sendDefault({
            objectType: 'feed',
            content: {
                title: title,
                description: '에코마켓에서 친환경 상품을 만나보세요!',
                imageUrl: document.querySelector('.main-image img')?.src || '',
                link: {
                    mobileWebUrl: url,
                    webUrl: url,
                },
            },
        });
    } else {
        alert('카카오톡 공유 기능을 사용할 수 없습니다.');
    }
}

function shareToFacebook(url) {
    const facebookUrl = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url)}`;
    window.open(facebookUrl, '_blank', 'width=600,height=400');
}

function shareToTwitter(url, title) {
    const twitterUrl = `https://twitter.com/intent/tweet?url=${encodeURIComponent(url)}&text=${encodeURIComponent(title)}`;
    window.open(twitterUrl, '_blank', 'width=600,height=400');
}

// =========================
// 유틸리티 함수들
// =========================
function checkLoginStatus() {
    // 실제 구현에서는 서버에서 로그인 상태를 확인
    // 여기서는 간단히 세션 스토리지나 쿠키로 확인
    return localStorage.getItem('isLoggedIn') === 'true' ||
        document.cookie.includes('JSESSIONID');
}

function getProductIdFromUrl() {
    // URL에서 상품 ID 추출
    const pathParts = window.location.pathname.split('/');
    return pathParts[pathParts.length - 1];
}

// =========================
// 기존 삭제 함수 (관리자용)
// =========================
function deleteItem(itemId) {
    if (confirm('정말로 삭제하시겠습니까?')) {
        fetch(`/eco/delete/${itemId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => {
                if (response.ok) {
                    alert('상품이 삭제되었습니다.');
                    location.href = '/eco/list';
                } else {
                    alert('삭제에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('삭제 중 오류가 발생했습니다.');
            });
    }
}