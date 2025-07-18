// Global variables
let cartItems = [];
let cartTotal = 0;
let appliedCoupons = [];

// DOM Elements
const header = document.getElementById('header');
const hamburger = document.getElementById('hamburger');
const navMenu = document.getElementById('navMenu');
const cartCountElement = document.getElementById('cartCount');
const selectedCountElement = document.getElementById('selectedCount');

// Cart data structure
const cartData = [
    {
        id: 1,
        name: '천연 세제 세트 (3개입)',
        description: '화학성분 0%, 100% 천연 원료로 만든 친환경 세제',
        price: 32000,
        originalPrice: 35000,
        quantity: 2,
        image: '🧴',
        features: ['🌱 비건 인증', '♻️ 재활용 용기', '🌍 탄소중립'],
        carbonSaved: 5.2,
        selected: true
    },
    {
        id: 2,
        name: '대나무 칫솔 세트 (가족용 4개입)',
        description: '플라스틱 대신 100% 대나무로 만든 친환경 칫솔',
        price: 15000,
        originalPrice: null,
        quantity: 1,
        image: '🌱',
        features: ['🎋 천연 대나무', '🌊 생분해 가능', '✋ 핸드메이드'],
        carbonSaved: 2.1,
        selected: true
    },
    {
        id: 3,
        name: '천연 수세미 5개 세트',
        description: '수세미 열매로 만든 100% 천연 설거지 수세미',
        price: 8500,
        originalPrice: 12000,
        quantity: 1,
        image: '🧽',
        features: ['🌾 천연 수세미', '🌊 생분해', '🏠 국내산'],
        carbonSaved: 1.5,
        selected: true
    }
];

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    initializeHeader();
    initializeCart();
    initializeEventListeners();
    updateCartSummary();
    
    console.log('🛒 GreenCycle 장바구니가 초기화되었습니다.');
});

// Header functionality
function initializeHeader() {
    // Header scroll effect
    window.addEventListener('scroll', () => {
        if (window.scrollY > 100) {
            header.classList.add('scrolled');
        } else {
            header.classList.remove('scrolled');
        }
    });
    
    // Mobile menu toggle
    if (hamburger) {
        hamburger.addEventListener('click', () => {
            hamburger.classList.toggle('active');
            navMenu.classList.toggle('active');
            
            // Animate hamburger
            const spans = hamburger.querySelectorAll('span');
            if (hamburger.classList.contains('active')) {
                spans[0].style.transform = 'rotate(45deg) translate(5px, 5px)';
                spans[1].style.opacity = '0';
                spans[2].style.transform = 'rotate(-45deg) translate(7px, -6px)';
            } else {
                spans[0].style.transform = 'none';
                spans[1].style.opacity = '1';
                spans[2].style.transform = 'none';
            }
        });
    }
    
    // Close mobile menu when clicking on a link
    document.querySelectorAll('.nav-menu a').forEach(link => {
        link.addEventListener('click', () => {
            closeMobileMenu();
        });
    });
}

function closeMobileMenu() {
    if (hamburger && navMenu) {
        hamburger.classList.remove('active');
        navMenu.classList.remove('active');
        
        const spans = hamburger.querySelectorAll('span');
        spans[0].style.transform = 'none';
        spans[1].style.opacity = '1';
        spans[2].style.transform = 'none';
    }
}

// Initialize cart
function initializeCart() {
    cartItems = [...cartData];
    updateCartCount();
    updateSelectedCount();
}

// Event listeners
function initializeEventListeners() {
    // Select all checkbox
    const selectAllCheckbox = document.getElementById('selectAll');
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', toggleSelectAll);
    }
    
    // Individual item checkboxes
    const itemCheckboxes = document.querySelectorAll('.item-checkbox');
    itemCheckboxes.forEach((checkbox, index) => {
        checkbox.addEventListener('change', () => toggleItemSelection(index));
    });
    
    // Quantity inputs
    const quantityInputs = document.querySelectorAll('.qty-input');
    quantityInputs.forEach((input, index) => {
        input.addEventListener('change', (e) => {
            const newQuantity = parseInt(e.target.value);
            if (newQuantity > 0 && newQuantity <= 10) {
                const itemId = parseInt(input.id.split('-')[1]);
                updateItemQuantity(itemId, newQuantity);
            }
        });
    });
}

// Cart functionality
function updateQuantity(itemId, change) {
    const item = cartItems.find(item => item.id === itemId);
    if (item) {
        const newQuantity = item.quantity + change;
        if (newQuantity > 0 && newQuantity <= 10) {
            item.quantity = newQuantity;
            document.getElementById(`qty-${itemId}`).value = newQuantity;
            updateCartSummary();
            showNotification(`수량이 ${newQuantity}개로 변경되었습니다.`, 'info');
        }
    }
}

function updateItemQuantity(itemId, quantity) {
    const item = cartItems.find(item => item.id === itemId);
    if (item) {
        item.quantity = quantity;
        updateCartSummary();
        showNotification(`수량이 ${quantity}개로 변경되었습니다.`, 'info');
    }
}

function toggleSelectAll() {
    const selectAll = document.getElementById('selectAll').checked;
    cartItems.forEach(item => {
        item.selected = selectAll;
    });
    
    const itemCheckboxes = document.querySelectorAll('.item-checkbox');
    itemCheckboxes.forEach(checkbox => {
        checkbox.checked = selectAll;
    });
    
    updateSelectedCount();
    updateCartSummary();
}

function toggleItemSelection(index) {
    const item = cartItems[index];
    if (item) {
        item.selected = !item.selected;
        updateSelectedCount();
        updateCartSummary();
        
        // Update select all checkbox
        const allSelected = cartItems.every(item => item.selected);
        const selectAllCheckbox = document.getElementById('selectAll');
        selectAllCheckbox.checked = allSelected;
    }
}

function updateCartCount() {
    const totalItems = cartItems.reduce((sum, item) => sum + item.quantity, 0);
    if (cartCountElement) {
        cartCountElement.textContent = totalItems;
    }
}

function updateSelectedCount() {
    const selectedItems = cartItems.filter(item => item.selected).length;
    if (selectedCountElement) {
        selectedCountElement.textContent = selectedItems;
    }
}

function updateCartSummary() {
    const selectedItems = cartItems.filter(item => item.selected);
    
    // Calculate subtotal
    const subtotal = selectedItems.reduce((sum, item) => {
        return sum + (item.price * item.quantity);
    }, 0);
    
    // Calculate discount
    const originalTotal = selectedItems.reduce((sum, item) => {
        const originalPrice = item.originalPrice || item.price;
        return sum + (originalPrice * item.quantity);
    }, 0);
    
    const discount = originalTotal - subtotal;
    
    // Apply coupon discounts
    let finalTotal = subtotal;
    appliedCoupons.forEach(coupon => {
        if (coupon.type === 'percentage') {
            finalTotal = finalTotal * (1 - coupon.value / 100);
        } else if (coupon.type === 'fixed') {
            finalTotal = Math.max(0, finalTotal - coupon.value);
        }
    });
    
    // Update DOM
    const subtotalElement = document.getElementById('subtotal');
    const discountElement = document.getElementById('discount');
    const totalElement = document.getElementById('total');
    
    if (subtotalElement) subtotalElement.textContent = formatPrice(originalTotal);
    if (discountElement) discountElement.textContent = `-${formatPrice(originalTotal - finalTotal)}`;
    if (totalElement) totalElement.textContent = formatPrice(finalTotal);
    
    // Update order button
    const orderButton = document.querySelector('.btn-order');
    if (orderButton) {
        orderButton.textContent = `🛒 주문하기 (${formatPrice(finalTotal)})`;
    }
    
    // Update environmental impact
    updateEnvironmentalImpact(selectedItems);
    
    cartTotal = finalTotal;
}

function updateEnvironmentalImpact(selectedItems) {
    const totalCarbonSaved = selectedItems.reduce((sum, item) => {
        return sum + (item.carbonSaved * item.quantity);
    }, 0);
    
    const plasticSaved = Math.round(totalCarbonSaved * 50); // Estimate plastic saved
    const pointsToEarn = Math.floor(cartTotal * 0.01); // 1% of total as points
    
    // Update impact display
    const carbonElement = document.querySelector('.impact-item:nth-child(1) .impact-value');
    const plasticElement = document.querySelector('.impact-item:nth-child(2) .impact-value');
    const pointsElement = document.querySelector('.impact-item:nth-child(3) .impact-value');
    
    if (carbonElement) carbonElement.textContent = `${totalCarbonSaved.toFixed(1)}kg CO₂`;
    if (plasticElement) plasticElement.textContent = `${plasticSaved}g`;
    if (pointsElement) pointsElement.textContent = `${pointsToEarn}P`;
}

function formatPrice(price) {
    return new Intl.NumberFormat('ko-KR').format(Math.round(price)) + '원';
}

// Cart actions
function removeItem(itemId) {
    const itemIndex = cartItems.findIndex(item => item.id === itemId);
    if (itemIndex !== -1) {
        const itemName = cartItems[itemIndex].name;
        cartItems.splice(itemIndex, 1);
        
        // Remove from DOM
        const cartItemElement = document.querySelector(`[data-item-id="${itemId}"]`);
        if (cartItemElement) {
            cartItemElement.style.opacity = '0';
            cartItemElement.style.transform = 'translateX(-100px)';
            setTimeout(() => {
                cartItemElement.remove();
                updateCartCount();
                updateSelectedCount();
                updateCartSummary();
            }, 300);
        }
        
        showNotification(`"${itemName}"이 장바구니에서 제거되었습니다.`, 'success');
    }
}

function deleteSelected() {
    const selectedItems = cartItems.filter(item => item.selected);
    if (selectedItems.length === 0) {
        showNotification('선택된 상품이 없습니다.', 'warning');
        return;
    }
    
    if (confirm(`선택한 ${selectedItems.length}개 상품을 삭제하시겠습니까?`)) {
        selectedItems.forEach(item => {
            removeItem(item.id);
        });
        showNotification(`${selectedItems.length}개 상품이 삭제되었습니다.`, 'success');
    }
}

function addToWishlist(itemId) {
    const item = cartItems.find(item => item.id === itemId);
    if (item) {
        showNotification(`"${item.name}"이 관심상품에 추가되었습니다. 💖`, 'success');
        
        // Animate heart
        const heartBtn = event.target;
        heartBtn.style.transform = 'scale(1.3)';
        heartBtn.style.color = '#dc3545';
        setTimeout(() => {
            heartBtn.style.transform = 'scale(1)';
        }, 200);
    }
}

function moveAllToWishlist() {
    const selectedItems = cartItems.filter(item => item.selected);
    if (selectedItems.length === 0) {
        showNotification('선택된 상품이 없습니다.', 'warning');
        return;
    }
    
    if (confirm(`선택한 ${selectedItems.length}개 상품을 관심상품으로 이동하시겠습니까?`)) {
        selectedItems.forEach(item => {
            removeItem(item.id);
        });
        showNotification(`${selectedItems.length}개 상품이 관심상품으로 이동되었습니다. 💖`, 'success');
    }
}

// Coupon functionality
function applyCoupon() {
    const couponCode = document.getElementById('couponCode').value.trim().toUpperCase();
    
    if (!couponCode) {
        showNotification('쿠폰 코드를 입력해주세요.', 'warning');
        return;
    }
    
    const coupons = {
        'FIRST10': { type: 'percentage', value: 10, name: '첫 구매 10% 할인' },
        'ECO20': { type: 'percentage', value: 20, name: '친환경 20% 할인' },
        'WELCOME5000': { type: 'fixed', value: 5000, name: '신규회원 5천원 할인' }
    };
    
    const coupon = coupons[couponCode];
    if (!coupon) {
        showNotification('유효하지 않은 쿠폰 코드입니다.', 'error');
        return;
    }
    
    // Check if already applied
    if (appliedCoupons.find(c => c.code === couponCode)) {
        showNotification('이미 적용된 쿠폰입니다.', 'warning');
        return;
    }
    
    appliedCoupons.push({ ...coupon, code: couponCode });
    updateCartSummary();
    showNotification(`"${coupon.name}" 쿠폰이 적용되었습니다! 🎫`, 'success');
    
    // Clear input
    document.getElementById('couponCode').value = '';
}

function applyCouponDirect(couponCode) {
    document.getElementById('couponCode').value = couponCode;
    applyCoupon();
}

// Navigation functions
function goToMarket() {
    showNotification('에코마켓으로 이동합니다. 🛍️', 'info');
    console.log('에코마켓으로 이동');
}

function proceedToOrder() {
    const selectedItems = cartItems.filter(item => item.selected);
    if (selectedItems.length === 0) {
        showNotification('주문할 상품을 선택해주세요.', 'warning');
        return;
    }
    
    const paymentMethod = document.querySelector('input[name="payment"]:checked').value;
    
    // Animate order button
    const orderBtn = event.target;
    orderBtn.textContent = '주문 처리 중...';
    orderBtn.disabled = true;
    orderBtn.style.opacity = '0.7';
    
    setTimeout(() => {
        showNotification(`${selectedItems.length}개 상품 주문이 완료되었습니다! 🎉`, 'success');
        console.log('주문 완료:', { items: selectedItems, payment: paymentMethod, total: cartTotal });
        
        // Reset button
        orderBtn.textContent = `🛒 주문하기 (${formatPrice(cartTotal)})`;
        orderBtn.disabled = false;
        orderBtn.style.opacity = '1';
    }, 2000);
}

// Recommended products
function addRecommendedToCart(productId) {
    const products = {
        1: { name: '천연 유기농 샴푸', price: 18000 },
        2: { name: '천연 올리브 비누 세트', price: 12500 },
        3: { name: '대나무 화장지 12롤', price: 24000 },
        4: { name: '밀짚 식기 세트', price: 35000 }
    };
    
    const product = products[productId];
    if (product) {
        showNotification(`"${product.name}"이 장바구니에 추가되었습니다! 🛒`, 'success');
        
        // Animate product card
        const productCard = event.target.closest('.product-card');
        productCard.style.transform = 'scale(0.95)';
        productCard.style.boxShadow = '0 0 20px rgba(45, 90, 61, 0.3)';
        
        setTimeout(() => {
            productCard.style.transform = 'scale(1)';
            productCard.style.boxShadow = '';
        }, 200);
        
        // Update cart count (simulate)
        updateCartCount();
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
    
    // Style the notification
    notification.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: ${getNotificationColor(type)};
        color: white;
        padding: 15px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        z-index: 10000;
        transform: translateX(400px);
        transition: transform 0.3s ease;
        max-width: 300px;
        font-weight: 500;
        font-size: 14px;
    `;
    
    document.body.appendChild(notification);
    
    // Show notification
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);
    
    // Hide notification
    setTimeout(() => {
        notification.style.transform = 'translateX(400px)';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

function getNotificationColor(type) {
    switch(type) {
        case 'success': return '#2d5a3d';
        case 'error': return '#dc3545';
        case 'warning': return '#ffc107';
        case 'info': return '#6fa776';
        default: return '#2d5a3d';
    }
}

// Animations and interactions
function animateCartUpdate() {
    const cartItems = document.querySelectorAll('.cart-item');
    cartItems.forEach((item, index) => {
        item.style.opacity = '0';
        item.style.transform = 'translateY(20px)';
        
        setTimeout(() => {
            item.style.opacity = '1';
            item.style.transform = 'translateY(0)';
            item.style.transition = 'all 0.3s ease';
        }, index * 100);
    });
}

// Local storage management
function saveCartToStorage() {
    localStorage.setItem('greenCycleCart', JSON.stringify(cartItems));
    localStorage.setItem('greenCycleCoupons', JSON.stringify(appliedCoupons));
}

function loadCartFromStorage() {
    const savedCart = localStorage.getItem('greenCycleCart');
    const savedCoupons = localStorage.getItem('greenCycleCoupons');
    
    if (savedCart) {
        cartItems = JSON.parse(savedCart);
    }
    
    if (savedCoupons) {
        appliedCoupons = JSON.parse(savedCoupons);
    }
}

// Page lifecycle events
window.addEventListener('beforeunload', () => {
    saveCartToStorage();
});

// Performance optimization
function optimizeImages() {
    const images = document.querySelectorAll('img[data-src]');
    const imageObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.dataset.src;
                img.removeAttribute('data-src');
                imageObserver.unobserve(img);
            }
        });
    });
    
    images.forEach(img => imageObserver.observe(img));
}

// Error handling
function handleError(error, context = '') {
    console.error(`Error in ${context}:`, error);
    showNotification('오류가 발생했습니다. 다시 시도해주세요.', 'error');
}

// Accessibility enhancements
function enhanceAccessibility() {
    // Add ARIA labels
    const quantityButtons = document.querySelectorAll('.qty-btn');
    quantityButtons.forEach(btn => {
        const isPlus = btn.textContent === '+';
        btn.setAttribute('aria-label', isPlus ? '수량 증가' : '수량 감소');
    });
    
    // Add keyboard support for product cards
    const productCards = document.querySelectorAll('.product-card');
    productCards.forEach(card => {
        card.setAttribute('tabindex', '0');
        card.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                card.click();
            }
        });
    });
}

// Initialize page when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    try {
        loadCartFromStorage();
        initializeHeader();
        initializeCart();
        initializeEventListeners();
        updateCartSummary();
        optimizeImages();
        enhanceAccessibility();
        
        // Animate cart items on load
        setTimeout(() => {
            animateCartUpdate();
        }, 500);
        
        console.log('🛒 GreenCycle 장바구니 초기화 완료');
        
    } catch (error) {
        handleError(error, 'Cart initialization');
    }
});

// Global error handler
window.addEventListener('error', (e) => {
    handleError(e.error, 'Global error');
});

// Expose functions globally for HTML onclick events
window.updateQuantity = updateQuantity;
window.removeItem = removeItem;
window.deleteSelected = deleteSelected;
window.addToWishlist = addToWishlist;
window.moveAllToWishlist = moveAllToWishlist;
window.applyCoupon = applyCoupon;
window.applyCouponDirect = applyCouponDirect;
window.goToMarket = goToMarket;
window.proceedToOrder = proceedToOrder;
window.addRecommendedToCart = addRecommendedToCart;