// 設置 Cookie
function setCookie(name, value) {
    document.cookie = name + '=' + value + ';path=/';
}

// 獲取 Cookie
function getCookie(name) {
    const cname = name + '=';
    const decodedCookie = decodeURIComponent(document.cookie);
    const ca = decodedCookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(cname) === 0) {
            return c.substring(cname.length, c.length);
        }
    }
    return '';
}

// 删除 Cookie 
function deleteCookie(name) {
    document.cookie = name + '=;expires=Thu, 01 Jan 1970 00:00:00 UTC;path=/;';
}

// 登出
function logout() {
    // 刪除儲存的登入狀態 Cookie
    deleteCookie('adminLoggedIn');
    deleteCookie('memberLoggedIn');
    deleteCookie('memberId');

    window.alert('登出成功！');

    // 跳轉首頁
    window.location.href = "/SA_Project/index.html";
}