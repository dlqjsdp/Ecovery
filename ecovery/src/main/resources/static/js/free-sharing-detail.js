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
        case 'HIGH': return '상 (매우 좋음)';
        case 'MEDIUM': return '중 (보통)';
        case 'LOW': return '하 (사용감 있음)';
        default: return '상 (매우 좋음)';
    }
}

// 날짜
function formatTimeAgo(dateTimeStr) {
    const now = new Date();
    const created = new Date(dateTimeStr);
    const diffMs = now - created;
    const diffMin = Math.floor(diffMs / 60000);

    if (diffMin < 1) return '방금 전';
    if (diffMin < 60) return `${diffMin}분 전`;
    const diffHr = Math.floor(diffMin / 60);
    if (diffHr < 24) return `${diffHr}시간 전`;
    const diffDay = Math.floor(diffHr / 24);
    return `${diffDay}일 전`;
}
// 전역 변수 선언 (item을 여기에 선언)
let item = null; // 게시글 데이터를 저장할 전역 변수



<!-- 상품 데이터를 JavaScript로 전달 -->

    // 서버에서 전달받은 상품 데이터를 JavaScript 변수로 설정
    var sharingItemData = /*[[${sharingItem}]]*/ {};
    var currentUserId = /*[[${session.memberId != null ? session.memberId.id : null}]]*/ null;
    var isOwner = /*[[${session.memberId != null and session.memberId.id == sharingItem.authorId}]]*/ false;

    // 전역 변수로 설정
    window.sharingItemData = sharingItemData;
    window.currentUserId = currentUserId;
    window.isOwner = isOwner;

    // 메인 이미지 변경 함수
    function changeMainImage(thumbnail) {
    const mainImage = document.getElementById('mainImage');
    const thumbnails = document.querySelectorAll('.thumbnail');

    // 메인 이미지 변경
    mainImage.src = thumbnail.src;

    // 활성 썸네일 변경
    thumbnails.forEach(thumb => thumb.classList.remove('active'));
    thumbnail.classList.add('active');
}

// =========================
// 전역 변수 선언 (item을 여기에 선언)
// =========================
let item = null; // 게시글 데이터를 저장할 전역 변수

// 메인 이미지 변경 함수 (이 함수는 `DOMContentLoaded`와 직접적인 관련 없으니 그대로 둡니다.)
function changeMainImage(thumbnail) {
    const mainImage = document.getElementById('mainImage');
    const thumbnails = document.querySelectorAll('.thumbnail');

    // 메인 이미지 변경
    mainImage.src = thumbnail.src;

    // 활성 썸네일 변경
    thumbnails.forEach(thumb => thumb.classList.remove('active'));
    thumbnail.classList.add('active');
}

// =========================
// 페이지가 로드되면 실행되는 함수
// =========================
document.addEventListener('DOMContentLoaded', async function() {
    console.log('무료나눔 상세페이지가 로드되었습니다!');

    // 경로(path)에서 freeId 추출
    const pathParts = window.location.pathname.split('/');
    const freeId = pathParts[pathParts.length - 1]; // 마지막 segment가 freeId

    if (!freeId) {
        alert('잘못된 접근입니다.');
        return;
    }

    try {
        // 게시글 데이터 비동기 조회
        const response = await fetch(`/api/free/get/${freeId}`);
        if (!response.ok) throw new Error('게시글 정보를 불러올 수 없습니다.');
        const data = await response.json();
        const item = data.free; // 전역 변수 item에 데이터 할당

        console.log('📦 item 객체:', item);

        // 이미 Thymeleaf에서 전달된 currentUser 객체를 그대로 사용
        const loginUser = currentUser;

        // 로그인 유저 정보 확인용 로그
        console.log('로그인 유저 정보: ', loginUser);

        // 상세페이지 렌더링
        renderDetailPage(item, loginUser);

        // 댓글 목록 렌더링
        renderComments(item.freeId);

        // 댓글 등록 이벤트 연결
        const submitCommentBtn = document.getElementById('submitCommentBtn');
        if (submitCommentBtn) {
            submitCommentBtn.addEventListener('click', function (e){
                submitComment(e, item.freeId); // e와 item.freeId 전달
            });
        }

        // 썸네일 클릭 이벤트 연결
        setupThumbnailEvents();

        // fade-in 애니메이션
        setTimeout(function () {
            const detailContainer = document.querySelector('.detail-container');
            if (detailContainer) {
                detailContainer.classList.add('fade-in');
            }
        }, 200);

        // 관리 버튼 표시 조건(작성자 본인일 경우)
        if (
            loginUser &&
            (item.memberId === loginUser.id || (loginUser.role && loginUser.role === 'ADMIN')) // loginUser.id와 loginUser.role 확인
        ) {
            const actions = document.getElementById('productActions');
            if (actions) actions.style.display = 'block';

            const editBtn = document.getElementById('editBtn');
            if (editBtn) editBtn.href = `/free/modify/${item.freeId}`;

            const deleteBtn = document.getElementById('deleteBtn');
            if (deleteBtn) {
                deleteBtn.addEventListener('click', () => deletePost(item.freeId));
            }
        }

        // 조회수 증가 (1초 후)
        setTimeout(() => increaseViewCount(item.freeId), 1000);

        // 모달 배경 클릭시 닫기
        const modals = document.querySelectorAll('.modal');
        modals.forEach(function (modal) {
            modal.addEventListener('click', function (event) {
                if (event.target === modal) {
                    closeModal(modal.id);
                }
            });
        });

    }catch (err) {
        console.error(err);
        alert('상세 정보를 불러오는 중 오류가 발생했습니다.')
    }
    setupEventListeners(); // 모든 이벤트 리스너를 설정하는 함수

});

// 댓글 등록 함수
function submitComment(e, freeId) {
    if(e) e.preventDefault(); // 폼 제출 방지

    const input = document.getElementById('commentInput');
    const content = input.value.trim();

    if (!content) {
        alert('댓글 내용을 입력해주세요.');
        return;
    }

    fetch(`/api/replies/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            freeId: freeId,
            content: content
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('서버 오류');
            }
            return response.text();
        })
        .then(data => {
            input.value = '';
            loadComments(freeId); // 부모 댓글 다시 불러오기
        })
        .catch(err => {
            console.error('댓글 등록 실패:', err);
            alert('댓글 등록 중 오류가 발생했습니다.');
        });
}

// 대댓글 등록 함수
function submitChildComment(parentId) {
    const input = document.getElementById(`childCommentInput-${parentId}`);
    const content = input.value.trim();
    const freeId = item?.freeId;

    if (!content) {
        alert('대댓글 내용을 입력해주세요.');
        return;
    }

    fetch(`/api/replies/register/child`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            freeId: freeId,
            parentId: parentId,
            content: content
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('서버 오류');
            }
            return response.text(); // ← 대댓글도 문자열 반환
        })
        .then(() => {
            input.value = '';
            loadComments(freeId); // 부모 + 대댓글 포함 새로고침
        })
        .catch(err => {
            console.error('대댓글 등록 실패:', err);
            alert('대댓글 등록 중 오류가 발생했습니다.');
        });
}

// 댓글 목록 불러오는 함수
function loadComments(freeId) {
    fetch(`/api/replies/parent/${freeId}`)
        .then(response => response.json())
        .then(data => {
            const list = document.getElementById('commentList');
            if (!list) { // commentList 요소가 없을 경우 에러 방지
                console.error("commentList 요소를 찾을 수 없습니다.");
                return;
            }
            list.innerHTML = ''; // 초기화

            data.list.forEach(parent => {
                const parentDiv = document.createElement('div');
                parentDiv.className = 'comment-item';
                parentDiv.innerHTML = `
                    <p class="comment-author">${parent.nickname}</p>
                    <p class="comment-content">${parent.content}</p>
                    <p class="comment-date">${formatTimeAgo(parent.createdAt)}</p>
                    <div class="child-comments" id="child-${parent.replyId}"></div>
                    <div class="reply-form">
                        <textarea id="childCommentInput-${parent.replyId}" placeholder="대댓글을 입력하세요..."></textarea>
                        <button onclick="submitChildComment(${parent.replyId})">답글등록</button>
                    </div>
                `;
                list.appendChild(parentDiv);

                // 대댓글 불러오기
                fetch(`/api/replies/child/${parent.replyId}`)
                    .then(res => res.json())
                    .then(childReplies => {
                        const childContainer = document.getElementById(`child-${parent.replyId}`);
                        if (childContainer) {
                            childReplies.forEach(child => {
                                const childDiv = document.createElement('div');
                                childDiv.className = 'child-comment-item';
                                childDiv.innerHTML = `
                                <p class="child-author">↳ ${child.nickname}</p>
                                <p class="child-content">${child.content}</p>
                                <p class="child-date">${formatTimeAgo(child.createdAt)}</p>
                            `;
                                childContainer.appendChild(childDiv);
                            });
                        }
                    })
            .catch(error => console.error('Error fetching child replies:', error));
            });
        })
    .catch(error => console.error('Error fetching comments:', error));

}

// =========================
// 상세 페이지 렌더링 함수
// =========================
function renderDetailPage(item, loginUser) {
    // 제목
    document.getElementById('itemTitle').textContent = item.title;
    document.getElementById('detailTitle').textContent = item.title;

    // 작성자 닉네임
    document.getElementById('authorNickname').textContent = item.nickname;
    document.getElementById('detailAuthor').textContent = item.nickname;

    // 등록일 (예: 2025-07-29 형식으로 변환)
    const createdDate = new Date(item.createdAt);
    document.getElementById('detailDate').textContent = createdDate.toLocaleDateString('ko-KR');
    document.getElementById('createdAt').textContent = formatTimeAgo(item.createdAt);

    // 조회수
    document.getElementById('viewCount').textContent = '👀 ' + item.viewCount;
    document.getElementById('detailViews').textContent = item.viewCount;

    // 상품 상태
    document.getElementById('detailCondition').textContent = getConditionText(item.itemCondition);

    // 나눔 상태
    document.getElementById('itemStatus').textContent = getStatusText(item.dealStatus); // 배지

    // 카테고리
    document.getElementById('detailCategory').textContent = item.category;

    // 나눔 지역
    document.getElementById('regionGu').textContent = item.regionGu;
    document.getElementById('regionDong').textContent = item.regionDong;

    // 상세 설명
    document.getElementById('detailContent').textContent = item.content;

    // 작성자 info 영역 (위쪽 카드)
    document.getElementById('authorNickname').textContent = item.nickname;
    document.getElementById('viewCount').textContent = '👀 ' + item.viewCount;
    document.getElementById('createdAt').textContent = formatTimeAgo(item.createdAt);
}

//
// // =========================
// // 로그인 상태 관리 함수
// // =========================
//
// // 서버에서 로그인 상태 확인
// function checkAuthStatus() {
//     fetch('/api/auth/status')
//         .then(response => response.json())
//         .then(data => {
//             if (data.success && data.user) {
//                 currentUser.id = data.user.id;
//                 currentUser.nickname = data.user.nickname;
//                 currentUser.isLoggedIn = true;
//             }
//             updateAuthUI();
//         })
//         .catch(error => {
//             console.error('로그인 상태 확인 오류:', error);
//             updateAuthUI();
//         });
// }

// 로그인 상태에 따른 UI 업데이트
function updateAuthUI() {
    const loginButtons = document.getElementById('loginButtons');
    const userInfo = document.getElementById('userInfo');

    if (currentUser.isLoggedIn) {
        // 로그인된 상태
        if (loginButtons) loginButtons.style.display = 'none';
        if (userInfo) userInfo.style.display = 'flex';

        // 사용자 이름 업데이트
        const userName = userInfo?.querySelector('.user-name');
        if (userName) {
            userName.textContent = currentUser.nickname;
        }
    } else {
        // 로그인되지 않은 상태
        if (loginButtons) loginButtons.style.display = 'flex';
        if (userInfo) userInfo.style.display = 'none';
    }
}

// 작성자 권한에 따른 관리 버튼 표시
function updateAuthorActions() {
    const productActions = document.getElementById('productActions');

    // 로그인했고, 현재 사용자가 게시글 작성자인 경우에만 관리 버튼 표시
    if (currentUser.isLoggedIn && item && currentUser.id === item.memberId) {
        if (productActions) productActions.style.display = 'block';
        console.log('✅ 작성자 본인입니다. 수정/삭제 버튼을 표시합니다.');
    } else {
        if (productActions) productActions.style.display = 'none';
        if (!currentUser.isLoggedIn) {
            console.log('❌ 비로그인 상태입니다. 수정/삭제 버튼을 숨깁니다.');
        } else {
            console.log('❌ 작성자가 아닙니다. 수정/삭제 버튼을 숨깁니다.');
        }
    }
}

// =========================
// 이벤트 리스너 설정 함수
// =========================
function setupEventListeners() {
    // 드롭다운 토글 버튼
    const dropdownToggle = document.getElementById('dropdownToggle');
    const dropdownMenu = document.getElementById('dropdownMenu');

    if (dropdownToggle && dropdownMenu) {
        dropdownToggle.addEventListener('click', function (event) {
            event.stopPropagation();
            toggleDropdown();
        });
    }

    // 문서 전체 클릭시 드롭다운 닫기
    document.addEventListener('click', function (event) {
        if (dropdownMenu && !dropdownMenu.contains(event.target)) {
            closeDropdown();
        }
    });

    // ESC 키로 모달과 드롭다운 닫기
    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            closeAllModals();
            closeDropdown();
        }
    });


// =========================
// 드롭다운 메뉴 관련 함수
// =========================

// 드롭다운 메뉴 토글
    function toggleDropdown() {
        const dropdownMenu = document.getElementById('dropdownMenu');
        if (!dropdownMenu) return; // 요소 없으면 종료

        if (dropdownMenu?.classList.contains('show')) {
            closeDropdown();
        } else {
            openDropdown();
        }
    }

// 드롭다운 메뉴 열기
    function openDropdown() {
        const dropdownMenu = document.getElementById('dropdownMenu');
        const dropdownToggle = document.getElementById('dropdownToggle');
        if (!dropdownMenu) return; //요소 없으면 종료

        dropdownMenu.classList.add('show');

            // 버튼 활성화 상태 표시
            if (dropdownToggle) {
                dropdownToggle.style.background = 'var(--primary-green)';
                dropdownToggle.style.color = 'var(--white)';
            }

            console.log('드롭다운 메뉴가 열렸습니다.');
        }
    }

// 드롭다운 메뉴 닫기
    function closeDropdown() {
        const dropdownMenu = document.getElementById('dropdownMenu');
        const dropdownToggle = document.getElementById('dropdownToggle');
        if (!dropdownMenu) return; // 요서 없으면 종료

        dropdownMenu.classList.remove('show');

            // 버튼 원래 상태로 복원
            if (dropdownToggle) {
                dropdownToggle.style.background = '';
                dropdownToggle.style.color = '';
            }

    }

// =========================
// 이미지 관련 함수
// =========================

// 메인 이미지 변경 (썸네일 클릭시)
    function changeMainImage(thumbnail) {
        const mainImage = document.getElementById('mainImage');
        const allThumbnails = document.querySelectorAll('.thumbnail');

        if (mainImage && thumbnail) {
            // 메인 이미지 변경
            mainImage.src = thumbnail.src.replace('80x80', '500x400');

            // 기존 active 클래스 제거
            allThumbnails.forEach(function (thumb) {
                thumb.classList.remove('active');
            });

            // 클릭된 썸네일에 active 클래스 추가
            thumbnail.classList.add('active');

            // 애니메이션 효과
            mainImage.style.transform = 'scale(0.95)';
            setTimeout(function () {
                mainImage.style.transform = 'scale(1)';
            }, 150);

            console.log('메인 이미지가 변경되었습니다.');
        }
    }

// // =========================
// // 모달 관련 함수
// // =========================
//
// // 연락처 모달 열기
//     function showContactInfo() {
//         // 로그인 확인
//         if (!currentUser.isLoggedIn) {
//             showNotification('로그인 후 연락처를 확인할 수 있습니다.', 'error');
//             return;
//         }
//
//         const modal = document.getElementById('contactModal');
//         if (modal) {
//             modal.classList.add('show');
//             document.body.style.overflow = 'hidden';
//             console.log('연락처 모달이 열렸습니다.');
//         }
//     }


// 특정 모달 닫기
    function closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('show');
            document.body.style.overflow = '';
            console.log(modalId + ' 모달이 닫혔습니다.');
        }
    }

// 모든 모달 닫기
    function closeAllModals() {
        const modals = document.querySelectorAll('.modal');
        modals.forEach(function (modal) {
            modal.classList.remove('show');
        });
        document.body.style.overflow = '';
    }

// 시간 포맷팅 함수
    function formatTime(date) {
        const hours = date.getHours();
        const minutes = date.getMinutes();
        const ampm = hours >= 12 ? '오후' : '오전';
        const displayHours = hours % 12 || 12;
        const displayMinutes = minutes.toString().padStart(2, '0');

        return `${ampm} ${displayHours}:${displayMinutes}`;
    }

// =========================
// 게시글 관리 함수 (수정/삭제)
// =========================

// 게시글 수정
    function editPost() {
        closeDropdown();

        if (confirm('게시글을 수정하시겠습니까?')) {
            showNotification('수정 페이지로 이동합니다.', 'success');

            setTimeout(function () {
                if (item && item.freeId) { // item이 로드되었는지 확인
                    window.location.href = '/free/modify/' + item.freeId;
                } else {
                    console.error('게시글 ID를 찾을 수 없습니다.');
                    showNotification('게시글 정보를 불러오는 중 오류가 발생했습니다.', 'error');
                }
            }, 1000);
        }
    }

// 게시글 삭제 함수 (API 컨트롤러 사용)
    function deletePost(freeId) {
        closeDropdown();

        if (confirm('정말로 이 게시글을 삭제하시겠습니까?\n삭제된 게시글은 복구할 수 없습니다.')) {
            // 삭제 중 상태 표시
            showNotification('게시글을 삭제하는 중입니다...', 'info');

            // 서버에 삭제 요청
            fetch(`/api/free/remove/${freeId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                }
            })
                .then(response => {
                    if (!response.ok) {
                        // 서버에서 오류 응답 보낸 경우
                        return response.text().then(text => {
                            throw new Error(text || '삭제 실패');
                        });
                    }
                    return response.text(); // 성공 메시지 텍스트
                })
                .then(message => {
                    showNotification(message || '게시글이 삭제되었습니다.', 'success');
                    setTimeout(() => {
                        window.location.href = '/free/list'; // 목록으로 이동
                    }, 1500);
                })
                .catch(error => {
                    console.error('게시글 삭제 오류:', error);
                    showNotification(error.message || '삭제 중 오류가 발생했습니다.', 'error');
                });
        }
    }

// =========================
// 알림 메시지 관련 함수
// =========================

// 알림 메시지 표시
    function showNotification(message, type) {
        type = type || 'success';

        // 기존 알림 제거
        const existingNotification = document.querySelector('.notification');
        if (existingNotification) {
            existingNotification.remove();
        }

        // 새 알림 생성
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.textContent = message;

        // 페이지에 추가
        document.body.appendChild(notification);

        // 애니메이션으로 표시
        setTimeout(function () {
            notification.classList.add('show');
        }, 100);

        // 3초 후 자동 제거
        setTimeout(function () {
            notification.classList.remove('show');
            setTimeout(function () {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 3000);
    }

// =========================
// 기타 유틸리티 함수
// =========================

// 조회수 증가
    function increaseViewCount(freeId) {
        fetch(`/api/free/get/${freeId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const viewCountElement = document.querySelector('.view-count');
                    if (viewCountElement) {
                        viewCountElement.textContent = `👀 ${data.viewCount}`;
                    }
                }
            })
            .catch(error => {
                console.error('조회수 증가 오류:', error);
            });
    }

// 상품 상태 업데이트
    function updateProductStatus(newStatus) {
        const formData = new FormData();

        // 기존 게시글 데이터 포함 (freeDto)
        const freeDto = {
            ...item,
            dealStatus: newStatus // 상태만 변경
        };

        // JSON -> Blob 변환 후 추가
        formData.append("freeDto", new Blob([JSON.stringify(freeDto)], {type: "application/json"}));

        // 이미지 파일은 없는 경우 빈 배열 전달 또는 기존 이미지 유지
        const dummyFileList = []; // 이미지가 필요하면 추가 가능
        dummyFileList.forEach(file => {
            formData.append("imgFile", file); // 이미지 파일은 여럿일 수 있으니 반복
        });

        fetch(`/api/free/modify/${item.freeId}`, {
            method: 'PUT',
            body: formData
        })
            .then(response => response.text())
            .then(message => {
                if (message.includes("수정되었습니다.")) {
                    const statusBadge = document.querySelector('.status-badge');
                    if (statusBadge) {
                        statusBadge.textContent = newStatus;

                        // 상태에 따른 색상 변경
                        statusBadge.className = 'status-badge';
                        let displayText = '';

                        switch (newStatus) {
                            case 'ONGOING':
                                displayText = '나눔중';
                                statusBadge.style.background = 'var(--success-green)';
                                break;
                            case 'DONE':
                                displayText = '나눔완료';
                                statusBadge.style.background = 'var(--medium-gray)';
                                break;
                        }
                        // 상태 배지에 텍스트 적용
                        statusBadge.textContent = displayText;

                        // 클래스 초기화 (필요시 스타일 적용을 위해)
                        statusBadge.className = 'status-badge';
                    }
                    showNotification('상품 상태가 업데이트되었습니다.', 'success');
                } else {
                    showNotification(message || '상태 업데이트 중 오류가 발생했습니다.', 'error');
                }
            })
            .catch(error => {
                console.error('상태 업데이트 오류:', error);
                showNotification('상태 업데이트 중 오류가 발생했습니다.', 'error');
            });
    }


// =========================
// 페이지 초기화 완료 후 실행
// =========================

// // DOM이 완전히 로드된 후 추가 설정
//     document.addEventListener('DOMContentLoaded', function () {
//         // 조회수 증가 (1초 후)
//         setTimeout(() => increaseViewCount(item.freeId), 1000);
//
//         // 모달 배경 클릭시 닫기
//         const modals = document.querySelectorAll('.modal');
//         modals.forEach(function (modal) {
//             modal.addEventListener('click', function (event) {
//                 if (event.target === modal) {
//                     closeModal(modal.id);
//                 }
//             });
//         });
//     });

// =========================
// 키보드 단축키
// =========================


// =========================
// 전역 함수로 노출 (HTML에서 onclick 등으로 사용)
// =========================

// HTML의 onclick에서 사용할 수 있도록 전역으로 노출
    window.changeMainImage = changeMainImage;
    window.showContactInfo = showContactInfo;
    window.closeModal = closeModal;
    // window.sendMessage = sendMessage;
    window.editPost = editPost;
    window.deletePost = deletePost;

// 기타 유용한 전역 함수들
    window.showNotification = showNotification;
    window.toggleDropdown = toggleDropdown;
    window.closeDropdown = closeDropdown;
    window.updateProductStatus = updateProductStatus;
    // window.toggleBookmark = toggleBookmark;
    // window.reportPost = reportPost;

// =========================
// 에러 핸들링
// =========================

// 전역 에러 처리
    window.addEventListener('error', function (event) {
        console.error('페이지 오류:', event.error);

        // 서버에 에러 리포트 전송
        fetch('/api/errors', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                error: event.error?.message || 'Unknown error',
                stack: event.error?.stack,
                url: window.location.href,
                timestamp: new Date().toISOString()
            })
        }).catch(console.error);
    });

// Promise 거부 처리
    window.addEventListener('unhandledrejection', function (event) {
        console.error('처리되지 않은 Promise 거부:', event.reason);
        showNotification('작업 처리 중 오류가 발생했습니다.', 'error');
    });

// =========================
// 최종 로그
// =========================

    console.log('🤝 무료나눔 상세페이지 JavaScript가 로드되었습니다.');
    console.log('📝 사용 가능한 기능:');
    console.log('   - 작성자 권한에 따른 수정/삭제 버튼');
    console.log('   - 드롭다운 메뉴 토글');
    console.log('   - 이미지 썸네일 변경');
    console.log('   - 연락처/채팅 모달');
    console.log('   - 키보드 단축키 지원');
    // console.log('   - 반응형 UI');
