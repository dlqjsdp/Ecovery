let cartItems = [];
let cartTotal = 0;
let appliedCoupons = [];

const header = document.getElementById('header');
const hamburger = document.getElementById('hamburger');
const navMenu = document.getElementById('navMenu');
const cartCountElement = document.getElementById('cartCount');
const selectedCountElement = document.getElementById('selectedCount');

// ✅ 장바구니 데이터 서버에서 불러오기
function initializeCart() {
    fetch('/cart/list')
        .then(response => {
            if (!response.ok) throw new Error('서버 응답 실패');
            return response.json();
        })
        .then(data => {
            cartItems = data.map(item => ({
                ...item,
                selected: true,
                quantity: item.count
            }));
            updateCartCount();
            updateSelectedCount();
            renderCartItems();
            updateCartSummary();
        })
        .catch(error => {
            handleError(error, '장바구니 데이터 불러오기');
        });
}

// ✅ 장바구니 항목 렌더링
function renderCartItems() {
    const cartListElement = document.getElementById('cartList');
    if (!cartListElement) return;

    cartListElement.innerHTML = '';

    cartItems.forEach((item, index) => {
        const itemHtml = `
            <div class="cart-item" data-item-id="${item.cartItemId}">
                <input type="checkbox" class="item-checkbox" ${item.selected ? 'checked' : ''}>
                <div class="item-info">
                    <span class="item-name">${item.itemNm}</span>
                    <span class="item-price">${formatPrice(item.price)} x ${item.quantity}개</span>
                </div>
                <div class="item-actions">
                    <button onclick="removeItem(${item.cartItemId})">삭제</button>
                </div>
            </div>
        `;
        cartListElement.insertAdjacentHTML('beforeend', itemHtml);
    });
}

function updateQuantity(itemId, change) {
    const item = cartItems.find(item => item.cartItemId === itemId);
    if (item) {
        const newQuantity = item.quantity + change;
        if (newQuantity > 0 && newQuantity <= item.stockNumber) {
            item.quantity = newQuantity;
            document.getElementById(`qty-${itemId}`).value = newQuantity;
            updateCartSummary();
            showNotification(`수량이 ${newQuantity}개로 변경되었습니다.`, 'info');
        }
    }
}

function updateItemQuantity(itemId, quantity) {
    const item = cartItems.find(item => item.cartItemId === itemId);
    if (item) {
        item.quantity = quantity;
        updateCartSummary();
        showNotification(`수량이 ${quantity}개로 변경되었습니다.`, 'info');
    }
}

function toggleSelectAll() {
    const selectAll = document.getElementById('selectAll').checked;
    cartItems.forEach(item => item.selected = selectAll);
    document.querySelectorAll('.item-checkbox').forEach(cb => cb.checked = selectAll);
    updateSelectedCount();
    updateCartSummary();
}

function toggleItemSelection(index) {
    const item = cartItems[index];
    if (item) {
        item.selected = !item.selected;
        updateSelectedCount();
        updateCartSummary();
        document.getElementById('selectAll').checked = cartItems.every(i => i.selected);
    }
}

function updateCartCount() {
    const totalItems = cartItems.reduce((sum, item) => sum + item.quantity, 0);
    if (cartCountElement) cartCountElement.textContent = totalItems;
}

function updateSelectedCount() {
    const selectedItems = cartItems.filter(item => item.selected).length;
    if (selectedCountElement) selectedCountElement.textContent = selectedItems;
}

function updateCartSummary() {
    const selectedItems = cartItems.filter(item => item.selected);
    const subtotal = selectedItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
    let finalTotal = subtotal;

    appliedCoupons.forEach(coupon => {
        if (coupon.type === 'percentage') {
            finalTotal *= (1 - coupon.value / 100);
        } else if (coupon.type === 'fixed') {
            finalTotal = Math.max(0, finalTotal - coupon.value);
        }
    });

    document.getElementById('subtotal').textContent = formatPrice(subtotal);
    document.getElementById('discount').textContent = formatPrice(subtotal - finalTotal);
    document.getElementById('total').textContent = formatPrice(finalTotal);

    const orderBtn = document.querySelector('.btn-order');
    if (orderBtn) orderBtn.textContent = `🛒 주문하기 (${formatPrice(finalTotal)})`;

    cartTotal = finalTotal;
    updateEnvironmentalImpact(selectedItems);
}

function updateEnvironmentalImpact(selectedItems) {
    const totalCarbonSaved = selectedItems.reduce((sum, item) => sum + ((item.carbonSaved || 0) * item.quantity), 0);
    const plasticSaved = Math.round(totalCarbonSaved * 50);
    const pointsToEarn = Math.floor(cartTotal * 0.01);

    document.querySelector('.impact-item:nth-child(1) .impact-value').textContent = `${totalCarbonSaved.toFixed(1)}kg CO₂`;
    document.querySelector('.impact-item:nth-child(2) .impact-value').textContent = `${plasticSaved}g`;
    document.querySelector('.impact-item:nth-child(3) .impact-value').textContent = `${pointsToEarn}P`;
}

function removeItem(itemId) {
    const index = cartItems.findIndex(item => item.cartItemId === itemId);
    if (index !== -1) {
        cartItems.splice(index, 1);
        document.querySelector(`[data-item-id="${itemId}"]`)?.remove();
        updateCartCount();
        updateSelectedCount();
        updateCartSummary();
        showNotification('상품이 삭제되었습니다.', 'success');
    }
}

function deleteSelected() {
    const selectedItems = cartItems.filter(item => item.selected);
    if (selectedItems.length === 0) return showNotification('선택된 상품이 없습니다.', 'warning');
    if (confirm(`선택한 ${selectedItems.length}개 상품을 삭제할까요?`)) {
        selectedItems.forEach(item => removeItem(item.cartItemId));
    }
}

function applyCoupon() {
    const input = document.getElementById('couponCode');
    const code = input.value.trim().toUpperCase();
    const coupons = {
        'FIRST10': { type: 'percentage', value: 10 },
        'ECO20': { type: 'percentage', value: 20 },
        'WELCOME5000': { type: 'fixed', value: 5000 }
    };

    const coupon = coupons[code];
    if (!coupon) return showNotification('유효하지 않은 쿠폰입니다.', 'error');
    if (appliedCoupons.some(c => c.code === code)) return showNotification('이미 적용된 쿠폰입니다.', 'warning');

    appliedCoupons.push({ ...coupon, code });
    updateCartSummary();
    input.value = '';
    showNotification(`"${code}" 쿠폰이 적용되었습니다!`, 'success');
}

function applyCouponDirect(code) {
    document.getElementById('couponCode').value = code;
    applyCoupon();
}

function goToMarket() {
    showNotification('에코마켓으로 이동합니다.', 'info');
    window.location.href = '/eco-market';
}

function proceedToOrder() {
    const selectedItems = cartItems.filter(item => item.selected);
    if (selectedItems.length === 0) return showNotification('주문할 상품을 선택해주세요.', 'warning');
    showNotification(`${selectedItems.length}개 상품 주문 완료 🎉`, 'success');
}

function showNotification(message, type = 'success') {
    const existing = document.querySelector('.notification');
    if (existing) existing.remove();

    const n = document.createElement('div');
    n.className = `notification ${type}`;
    n.textContent = message;
    n.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: ${getNotificationColor(type)};
        color: white;
        padding: 12px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        z-index: 10000;
        max-width: 300px;
    `;
    document.body.appendChild(n);
    setTimeout(() => n.remove(), 3000);
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

function initializeHeader() {
    window.addEventListener('scroll', () => {
        header.classList.toggle('scrolled', window.scrollY > 100);
    });
    if (hamburger) {
        hamburger.addEventListener('click', () => {
            hamburger.classList.toggle('active');
            navMenu.classList.toggle('active');
        });
    }
}

function initializeEventListeners() {
    const selectAllCheckbox = document.getElementById('selectAll');
    if (selectAllCheckbox) selectAllCheckbox.addEventListener('change', toggleSelectAll);
}

function formatPrice(price) {
    return new Intl.NumberFormat('ko-KR').format(Math.round(price)) + '원';
}

document.addEventListener('DOMContentLoaded', function () {
    initializeHeader();
    initializeCart();
    initializeEventListeners();
});