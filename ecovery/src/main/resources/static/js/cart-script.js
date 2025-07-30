// cart-script.js - 실무 기준 전체 구조 정리본

let cartItems = [];
let cartTotal = 0;
let appliedCoupons = [];

// DOMContentLoaded 이후 실행
document.addEventListener('DOMContentLoaded', function () {
    initializeHeader();
    initializeCartItemsFromDOM();
    initializeEventListeners();
    bindDynamicEventListeners();
});

// ============================================
// 초기화 함수들
// ============================================

function initializeHeader() {
    const header = document.getElementById('header');
    const hamburger = document.getElementById('hamburger');
    const navMenu = document.getElementById('navMenu');

    window.addEventListener('scroll', () => {
        header?.classList.toggle('scrolled', window.scrollY > 100);
    });

    hamburger?.addEventListener('click', () => {
        hamburger.classList.toggle('active');
        navMenu.classList.toggle('active');
    });
}

function initializeCartItemsFromDOM() {
    cartItems = [];
    document.querySelectorAll('.cart-item').forEach(itemEl => {
        const cartItemId = parseInt(itemEl.dataset.itemId);
        const realItemId = parseInt(itemEl.dataset.realItemId);
        const name = itemEl.querySelector('.item-name').textContent.trim();

        // 여기! 이제 data-unit-price 속성에서 진짜 단가를 가져옵니다.
        const unitPrice = parseInt(itemEl.dataset.unitPrice);

        const qtyInput = itemEl.querySelector('.qty-input');
        const count = parseInt(qtyInput.value);
        const stock = parseInt(qtyInput.getAttribute('max'));
        const selected = itemEl.querySelector('.item-checkbox').checked;

        cartItems.push({
            cartItemId: cartItemId,
            realItemId: realItemId,
            itemNm: name,
            price: unitPrice,
            quantity: count,
            stockNumber: stock,
            selected: selected
        });
    });

    updateSelectedCount();
    updateCartSummary();
}

function initializeEventListeners() {
    const selectAllCheckbox = document.getElementById('selectAll');
    selectAllCheckbox?.addEventListener('change', toggleSelectAll);
}

function bindDynamicEventListeners() {
    document.querySelectorAll('.item-checkbox').forEach((cb, index) => {
        cb.addEventListener('change', () => toggleItemSelection(index));
    });

    document.querySelectorAll('.qty-btn.plus').forEach(btn => {
        btn.addEventListener('click', () => {
            const cartItemId = parseInt(btn.closest('.cart-item').dataset.itemId);
            const realItemId = parseInt(btn.closest('.cart-item').dataset.realItemId);
            updateQuantity(cartItemId, 1, realItemId);
        });
    });

    document.querySelectorAll('.qty-btn.minus').forEach(btn => {
        btn.addEventListener('click', () => {
            const cartItemId = parseInt(btn.closest('.cart-item').dataset.itemId);
            const realItemId = parseInt(btn.closest('.cart-item').dataset.realItemId);
            updateQuantity(cartItemId, -1, realItemId);
        });
    });
}

// ============================================
// 장바구니 관련 유틸 함수들
// ============================================

function updateQuantity(cartItemIdToUpdate, change, realItemId) {
    const item = cartItems.find(item => item.cartItemId === cartItemIdToUpdate);
    if (item) {
        const newQuantity = item.quantity + change;

        // 새로운 수량이 유효한지 먼저 확인
        if (newQuantity > 0 && newQuantity <= item.stockNumber) {
            // UI 업데이트 (즉시 반응을 보여주기 위함)
            item.quantity = newQuantity;
            document.getElementById(`qty-${cartItemIdToUpdate}`).value = newQuantity;

            // 상품별 개별 금액 업데이트
            const itemElement = document.querySelector(`[data-item-id="${cartItemIdToUpdate}"]`);
            if (itemElement) {
                const salePriceElement = itemElement.querySelector('.sale-price');
                if (salePriceElement) {
                    const updatedItemPrice = item.price * newQuantity; // 단가 * 새로운 수량
                    salePriceElement.textContent = formatPrice(updatedItemPrice); // 포맷하여 표시
                }
            }

            updateCartSummary(); // 총 금액 업데이트

            // 서버에 수량 변경 요청을 보내는 코드 추가
            fetch(`/cart/update?cartItemId=${cartItemIdToUpdate}&count=${newQuantity}&itemId=${realItemId}`, {
                method: 'PUT' // 수량 변경은 PUT 방식으로 요청합니다.
            })
                .then(response => {
                    if (response.ok) { // 서버 응답이 성공적이면
                        showNotification(`수량이 ${newQuantity}개로 변경되었습니다.`, 'success');
                    } else { // 서버에서 오류 응답이 오면
                        showNotification('수량 변경 실패! 서버 오류.', 'error');
                        response.text().then(errorMessage => console.error("서버 오류 메시지:", errorMessage)); // 서버 오류 메시지 확인용
                    }
                })
                .catch(error => { // 네트워크 오류 등 예외 발생 시
                    console.error("수량 변경 중 네트워크 오류:", error);
                    showNotification('수량 변경 중 문제가 발생했습니다.', 'error');
                });

        } else {
            console.log("❌ 수량 변경 실패: 재고 초과 또는 0 이하 (newQuantity:", newQuantity, "stockNumber:", item.stockNumber, ")");
            showNotification("더 이상 수량을 변경할 수 없습니다. (재고 부족 또는 1개 미만)", 'warning'); // 사용자에게 알림
        }
    } else {
        console.log("❓ 수량을 업데이트할 아이템을 찾지 못했습니다. itemId:", itemId);
        showNotification("장바구니 아이템 정보를 찾을 수 없습니다.", 'error'); // 사용자에게 알림
    }
}

function toggleSelectAll() {
    const isChecked = document.getElementById('selectAll').checked;
    cartItems.forEach(item => item.selected = isChecked);
    document.querySelectorAll('.item-checkbox').forEach(cb => cb.checked = isChecked);
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

function updateCartSummary() {
    const selectedItems = cartItems.filter(item => item.selected);
    const subtotal = selectedItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
    let finalTotal = subtotal;

    appliedCoupons.forEach(coupon => {
        if (coupon.type === 'percentage') finalTotal *= (1 - coupon.value / 100);
        else if (coupon.type === 'fixed') finalTotal = Math.max(0, finalTotal - coupon.value);
    });

    document.getElementById('subtotal').textContent = formatPrice(subtotal);
    document.getElementById('discount').textContent = formatPrice(subtotal - finalTotal);
    document.getElementById('total').textContent = formatPrice(finalTotal);

    document.querySelector('.btn-order').textContent = `🛒 주문하기 (${formatPrice(finalTotal)})`;
    cartTotal = finalTotal;
}

function updateSelectedCount() {
    const selectedCount = cartItems.filter(item => item.selected).length;
    document.getElementById('selectedCount').textContent = selectedCount;
}

function removeItem(itemId) {
    fetch(`/cart/delete/${itemId}`, { method: 'DELETE' })
        .then(res => {
            if (!res.ok) throw new Error('삭제 실패');
            const index = cartItems.findIndex(item => item.cartItemId === itemId);
            if (index !== -1) {
                cartItems.splice(index, 1);
                document.querySelector(`[data-item-id="${itemId}"]`)?.remove();
                updateSuielectedCount();
                updateCartSummary();
                showNotification('상품이 삭제되었습니다.', 'success');
            }
        })
        .catch(() => {
            showNotification('삭제 중 오류가 발생했습니다.', 'error');
        });
}

function deleteSelected() {
    const selectedItems = cartItems.filter(item => item.selected);
    if (selectedItems.length === 0) return showNotification('선택된 상품이 없습니다.', 'warning');
    if (confirm(`선택한 ${selectedItems.length}개 상품을 삭제할까요?`)) {
        selectedItems.forEach(item => removeItem(item.cartItemId));
    }
}

// ============================================
// 기타 유틸리티 함수들
// ============================================

function formatPrice(price) {
    return new Intl.NumberFormat('ko-KR').format(Math.round(price)) + '원';
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
    switch (type) {
        case 'success': return '#2d5a3d';
        case 'error': return '#dc3545';
        case 'warning': return '#ffc107';
        case 'info': return '#6fa776';
        default: return '#2d5a3d';
    }
}
