/*
    에코마켓 목록 JavaScript
    @author : sehui
    @fileName : eco-market-list.js
    @since : 250729
    @history
     - 250729 | sehui | 에코마켓 목록 비동기 요청하여 조회 후 렌더링 기능 추가
     - 250729 | sehui | 에코마켓 목록 페이징 처리 기능 추가
 */

document.addEventListener("DOMContentLoaded", function (){
    const filterForm = document.getElementById("filterForm");

    filterForm.addEventListener("submit", function (e){
        e.preventDefault();      //기본 동작 막기

        const searchType = document.getElementById("searchType").value;
        const keyword = document.getElementById("searchInput").value.trim();

        //검색 기준에 따라 itemNm 또는 category에 값 전달
        let itemNm = "";
        let category = "";

        if(searchType === "itemNm") {
            itemNm = keyword;
        } else if(searchType === "category") {
            category = keyword;
        } else {
            itemNm = keyword;
            category = keyword;
        }

        loadItems(1, itemNm, category);      //1페이지부터 검색 조건 반영
    });

    loadItems();    //최초 페이지 로딩 시 전체 목록
});

//상품 목록 조회
function loadItems(pageNum = 1,  itemNm = "", category = "") {
    const url = new URL("/api/eco/list", window.location.origin);
    url.searchParams.set('pageNum', pageNum);
    url.searchParams.set("amount", 10);

    if (itemNm) {
        url.searchParams.set("itemNm", itemNm);
    }
    if (category) {
        url.searchParams.set("category", category);
    }

    //fetch 함수로 GET 요청 보냄
    fetch(url)
        .then(response => response.json())
        .then(data => {
            console.log("PageMaker: ", data.pageMaker);
            renderItemList(data.itemList);
            renderPagination(data.pageMaker, itemNm, category);
        })
        .catch(error => console.error('상품 목록 조회 실패: ', error));
}

//상품 목록 렌더링
function renderItemList(itemList) {
    const itemsGrid = document.getElementById('itemsGrid');

    //기존 목록 초기화
    itemsGrid.innerHTML = '';

    if(!itemList || itemList.length === 0) {
        //상품이 존재하지 않는 경우
        itemsGrid.innerHTML = `
                    <div class="no-products">
                        <div class="no-products-content">
                            <h3>😔 검색 조건에 맞는 상품이 없습니다</h3>
                            <p>다른 조건으로 검색해보시거나, 직접 상품을 등록해보세요!</p>
                            <a href="/eco/new" class="btn btn-primary">상품 등록하기</a>
                        </div>
                    </div>
                `;
        return;
    }

    //상품이 존재하는 경우
    itemList.forEach(product => {
        const productCard = document.createElement('div');
        productCard.classList.add('product-card');

        //클릭 시 상세 페이지 이동
        productCard.style.cursor = 'pointer';
        productCard.onclick = () => {
            location.href = `/eco/${product.itemId}`;
        };

        const imgUrl = product.imgUrl || "https://via.placeholder.com/200x150?text=No+Image";

        //상태 배지 클래스 및 텍스트 설정
        let statusClass = '';
        let statusText = '';

        switch (product.itemSellStatus) {
            case 'SELL':
                statusClass = 'available';
                statusText = '판매중';
                break;
            case 'SOLD_OUT':
                statusClass = 'completed';
                statusText = '판매완료';
                break;
            default:
                statusClass = '';
                statusText = product.itemSellStatus;
        }

        productCard.innerHTML = `
            <div class="item-card">
                <div class="item-image">
                    <img src="${imgUrl}" alt="${product.itemNm}" />  
                    <div class="item-status ${statusClass}">${statusText}</div>  
                </div>
                <div class="item-info">
                    <h3 class="item-title">${product.itemNm}</h3>
                    <div class="item-meta">
                        <span class="item-category">${product.category}</span>
                    </div>
                </div>
            </div>
        `;

        itemsGrid.appendChild(productCard);
    });
}

//페이징 렌더링
function renderPagination(pageMaker, itemNm, category) {
    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    //이전 페이지 버튼
    if (pageMaker.prev) {
        pagination.innerHTML += `<a class="pagination-btn" onclick="loadItems(${pageMaker.startPage - 1})">‹</a>`;
    }

    //페이지 번호들
    for (let i = pageMaker.startPage; i <= pageMaker.endPage; i++) {
        const active = i === pageMaker.cri.pageNum ? "active" : "";
        pagination.innerHTML += `<a class="pagination-btn ${active}" onclick="loadItems(${i})">${i}</a>`;
    }

    //다음 페이지 버튼
    if (pageMaker.next) {
        pagination.innerHTML += `<a class="pagination-btn" onclick="loadItems(${pageMaker.endPage + 1})">›</a>`;
    }
}