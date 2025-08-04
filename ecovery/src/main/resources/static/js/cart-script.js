let cartItems = [];
let cartTotal = 0;
let appliedCoupons = [];

document.addEventListener('DOMContentLoaded', function () {
    initializeHeader();
    initializeCartItemsFromDOM();
    initializeEventListeners();
    bindDynamicEventListeners();
});

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
        const unitPrice = parseInt(itemEl.dataset.unitPrice);
        const qtyInput = itemEl.querySelector('.qty-input');
        const count = parseInt(qtyInput.value);
        const stock = parseInt(qtyInput.getAttribute('max'));
        const selected = itemEl.querySelector('.item-checkbox').checked;

        cartItems.push({
            cartItemId,
            realItemId,
            itemNm: name,
            price: unitPrice,
            quantity: count,
            stockNumber: stock,
            selected
        });
    });
    updateSelectedCount();
    updateCartSummary();
}

function initializeEventListeners() {
    document.getElementById('selectAll')?.addEventListener('change', toggleSelectAll);
    document.querySelector('.btn-order')?.addEventListener('click', () => {
        const selectedItems = cartItems.filter(item => item.selected);
        if (selectedItems.length === 0) return showNotification("주문할 상품을 선택해주세요.", "warning");

        const orderData = selectedItems.map(item => ({
            itemId: item.realItemId,
            count: item.quantity
        }));

        fetch("/api/order/prepare", {   // ✅ 여기가 핵심: 실제 컨트롤러 경로는 '/api/order/prepare'야!
            method: "POST",
            headers: {
                "Content-Type": "application/json"  // ✅ JSON으로 명시
            },
            body: JSON.stringify(selectedItems)     // ✅ 배열 형태 그대로 전송
        })
            .then(response => {
                if (response.ok) {
                    return response.json();             // ✅ 정상 응답이면 JSON 파싱
                } else {
                    return response.text().then(text => { throw new Error(text); });
                }
            })
            .then(data => {
                console.log("✅ 서버 응답:", data);
                // 예: 주문 확인 페이지로 이동
                window.location.href = `/order/confirm?orderId=${data.orderId}`; // ← 이건 상황에 따라
            })
            .catch(error => {
                console.error("❌ 주문 요청 실패:", error);
            });
    });
}

function bindDynamicEventListeners() {
    document.querySelectorAll('.item-checkbox').forEach((cb, index) => {
        cb.addEventListener('change', () => toggleItemSelection(index));
    });

    document.querySelectorAll('.qty-btn.plus').forEach(btn => {
        btn.addEventListener('click', () => {
            const itemEl = btn.closest('.cart-item');
            updateQuantity(parseInt(itemEl.dataset.itemId), 1, parseInt(itemEl.dataset.realItemId));
        });
    });

    document.querySelectorAll('.qty-btn.minus').forEach(btn => {
        btn.addEventListener('click', () => {
            const itemEl = btn.closest('.cart-item');
            updateQuantity(parseInt(itemEl.dataset.itemId), -1, parseInt(itemEl.dataset.realItemId));
        });
    });
}

function updateQuantity(cartItemIdToUpdate, change, realItemId) {
    const item = cartItems.find(item => item.cartItemId === cartItemIdToUpdate);
    if (item) {
        const newQuantity = item.quantity + change;
        if (newQuantity > 0 && newQuantity <= item.stockNumber) {
            item.quantity = newQuantity;
            document.getElementById(`qty-${cartItemIdToUpdate}`).value = newQuantity;
            const itemElement = document.querySelector(`[data-item-id="${cartItemIdToUpdate}"]`);
            itemElement.querySelector('.sale-price').textContent = formatPrice(item.price * newQuantity);
            updateCartSummary();

            fetch(`/cart/update?cartItemId=${cartItemIdToUpdate}&count=${newQuantity}&itemId=${realItemId}`, {
                method: 'PUT'
            })
                .then(response => {
                    if (response.ok) showNotification(`수량이 ${newQuantity}개로 변경되었습니다.`, 'success');
                    else {
                        showNotification('수량 변경 실패! 서버 오류.', 'error');
                        response.text().then(msg => console.error("서버 오류 메시지:", msg));
                    }
                })
                .catch(err => {
                    console.error("수량 변경 중 네트워크 오류:", err);
                    showNotification('수량 변경 중 문제가 발생했습니다.', 'error');
                });
        } else {
            showNotification("더 이상 수량을 변경할 수 없습니다. (재고 부족 또는 1개 미만)", 'warning');
        }
    } else {
        showNotification("장바구니 아이템 정보를 찾을 수 없습니다.", 'error');
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
    cartItems[index].selected = !cartItems[index].selected;
    updateSelectedCount();
    updateCartSummary();
    document.getElementById('selectAll').checked = cartItems.every(i => i.selected);
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

function goToMarket() {
    window.location.href = "/eco/list";
}
