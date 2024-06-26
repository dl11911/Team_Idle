window.onload = () =>{
    GetOrderListView('/api/view/orderList');
}
let price = 0;
let items = [];

function GetOrderListView(url){
    const headers = {
        'Content-Type': 'application/json',
    };
    fetch(url, {
        method: 'POST',
        headers: headers
    }).then(response => response.json())
        .then(data => {
            // Thymeleaf로 직접 데이터 추가
            const dataContainer = document.getElementById('field');
            console.log(url);
            let cartHtml =
                `
                <h1>주문 내역</h1>
                <table id="cartTable">
                    <thead>
              <tr>
                <th>상품</th>
                <th>사이즈</th>
                <th>가격</th>
                <th>수량</th>
                <th>총 가격</th>
                <th>선택</th>
              </tr>
            </thead>
            <tbody>`
            // 데이터 배열을 순회하면서 각 항목을 HTML로 변환
            cartHtml += data.map(item => {
                return `
                <!-- Repeat for each row -->
                    <tr>
                        <td>${item.name}<img src="https://kr.object.ncloudstorage.com/miniidle/storage/${item.img}" alt="상품 이미지" width="100" height="100"></td>
                        <td>${item.size}</td>
                        <td>${item.price}</td>
                        <td>${item.count}</td>
                        <td>${item.totalPrice}</td>
                        <td><input type="checkbox" class="item-checkbox" data-price=${item.price} data-size=${item.size}  
                        data-count=${item.count} data-id=${item.id} onchange="handleChange(this)"></td>
                    </tr>
                <!-- ... more rows ... -->
            `;
            }).join(''); // 배열의 모든 항목을 하나의 문자열로 결합
            cartHtml+=`
            </tbody>
            </table>
            <p id="totalPrice">총 가격: ${price}</p>
            <button class="checkout-btn" onclick="goPayment('/api/POST/paymentList',items)">문의하기</button>`;
            dataContainer.innerHTML = cartHtml;
        })
        .catch(error => {
            console.error('Error fetching user data:', error);
        });
}

function handleChange(e){
    // 체크박스가 체크되었는지 확인
    if (e.checked) {
        console.log("체크됨. 가격:", e.dataset.price);
        price = price + parseInt(e.dataset.price);
        items.push(e.dataset.id+','+e.dataset.size+','+e.dataset.count);
    } else {
        // 체크 해제되었을 때 수행할 동작
        console.log("체크 해제됨. 가격:", e.dataset.price);
        price = price - parseInt(e.dataset.price);
        items = items.filter(item => item !== (e.dataset.id+','+e.dataset.size+','+e.dataset.count));
    }
    console.log(items);
    document.getElementById('totalPrice').innerHTML = `총 가격: ${price}`;
}