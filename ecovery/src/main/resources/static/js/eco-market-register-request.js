/*
    에코마켓 상품 등록 JavaScript
    @author : sehui
    @fileName : eco-market-register-request.js
    @since : 250730
    @history
     - 250730 | sehui | 에코마켓 상품 등록 페이지 요청 기능 추가
     - 250730 | sehui | 에코마켓 상품 등록 버튼 클릭 시 등록 요청 기능 추가
     - 250730 | sehui | 취소 버튼 클릭 시 목록 페이지 이동 요청 기능 추가
 */

document.addEventListener("DOMContentLoaded", () => {
    //페이지 로드 후 바로 API 호출하여 상품 등록 페이지 데이터 요청
    fetch("/api/eco/new")
        .then(response => {
            if(response.status === 403) {
                alert("권한이 없습니다. 관리자만 접근 가능합니다.");
                window.location.href = "/eco/list";
                throw new Error("권한 없음");
            }
            if(!response.ok) {
                throw new Error("서버 에러");
            }
            return response.json();
        })
        .then(data => {
            //응답객체 data.itemFormDto, data.categories 사용
            //카테고리 select 박스에 옵션 추가
            const categorySelect = document.getElementById("category");
            categorySelect.innerHTML = '<option value="">카테고리를 선택해주세요</option>';

            data.categories.forEach(category => {
                const option = document.createElement("option");
                option.value = category.categoryId;
                option.textContent = category.categoryName;

                categorySelect.appendChild(option);
            });
        })
        .catch(error => {
            console.error("상품 등록 폼 데이터 로드 실패 : ", error);
            alert("상품 등록 폼을 불러오지 못했습니다.");
        });

    let isSubmiting = false;

    //beforeunload 이벤트 핸들러
    function beforeUnloadHandler(event) {
        if(isSubmiting) return;        //등록 중일 때는 경고창 안 띄움

        if(checkFormHasContent()){
            event.preventDefault();
            event.returnValue = '';
            return '';
        }
    }

    //페이지 준비 완료 후 이벤트 등록
    window.addEventListener('beforeunload', beforeUnloadHandler);

    //상품 등록 form 제출 이벤트
    const form = document.getElementById("registrationForm");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();     //form의 기본 동작 막기

        //제출 직전에 플래그 설정
        isSubmiting = true;

        //입력값 수집
        const itemFormDto = {
            itemNm: document.getElementById("productName").value,
            price: document.getElementById("price").value,
            stockNumber: document.getElementById("stockNumber").value,
            categoryId: document.getElementById("category").value,
            itemDetail: document.getElementById("description").value,
            itemSellStatus: document.getElementById("condition").value,
        }

        //FormData 구성
        const formData = new FormData();
        formData.append("itemFormDto", new Blob(
           [JSON.stringify(itemFormDto)],
            {type: "application/json"}
        ));

        //이미지 파일 추가
        //eco-market-register.js의 전역 변수 uploadedImages (이미지 배열) 사용
        uploadedImages.forEach(function (images){
            formData.append("itemImgFile", images.file);
        });

        //서버에 POST 요청
        try{
            const response = await fetch("/api/eco/new", {
                method: "POST",
                body: formData
            });

            if(response.status === 201) {
                //경고창 제거
                window.removeEventListener("beforeunload", beforeUnloadHandler);
                alert("상품이 성공적으로 등록되었습니다.");
                setTimeout(() => {
                    window.location.href = "/eco/list";
                }, 200);
            }else {
                isSubmiting = false;       //실패하면 플래그 복구
                const errorData = await response.json();
                alert(errorData.errorMessage || "상품 등록 중 오류가 발생했습니다.");
            }
        }catch (error) {
            isSubmiting = false;       //실패하면 플래그 복구
            console.error("상품 등록 요청 실패 : ", error);
            alert("서버와 통신 중 문제가 발생했습니다.");
        }
    });

    //취소 버튼 클릭 시
    const cancelBtn = document.getElementById("cancelBtn");

    cancelBtn.addEventListener("click", function () {
        //checkFormHasContent() : 입력값/이미지 있는지 확인하는 함수
        if(checkFormHasContent()) {
            const confirmLeave = confirm("작성한 내용이 삭제됩니다. 정말 나가시겠습니까?");
            if(confirmLeave) {
                //경고창 무시하고 이동할 수 있도록 breforeunload 핸들러 제거
                window.removeEventListener("beforeunload", beforeUnloadHandler);
                setTimeout(() => {
                    window.location.href = "/eco/list";
                }, 200);
            }
        //입력값이 없는 경우
        }else{
            setTimeout(() => {
                window.location.href = "/eco/list";
            }, 200);
        }
    });
});

