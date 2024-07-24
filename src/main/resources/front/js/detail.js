var Id = 0;
var chooseSpot = null;
var chooseHotel = null;

//方法
function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}
function changeLike(id,likeId){
    var likeStatu = document.getElementById('like_' + id).innerText;
    var count = parseInt(document.getElementById('count_' + id).innerText);
    var status = 0;
    if(likeStatu == '👍'){
        document.getElementById('like_' + id).innerText = '👍🏿';
        document.getElementById('count_' + id).innerText = String(count-1);
    }else{
        document.getElementById('like_' + id).innerText = '👍';
        document.getElementById('count_' + id).innerText = String(count+1);
        status = 1;
    }
    postData(2,`/ts/like?likesId=${likeId}&status=${status}`,null);
}
function changeAmount(){
    var amount = 0.0;
    var tickets = document.getElementById('tickets').value;
    var rooms = document.getElementById('rooms').value;
    if(chooseHotel != null){
        amount += rooms * chooseHotel.price;
    }
    amount += tickets * chooseSpot.price;
    document.getElementById("amount").innerHTML =  amount;
}
function formSubmit_comment(i,url){
    const Form = document.getElementById('comment-form');
    let formData = new FormData(Form);
    formData.set('spotId', Id);
    let jsonData = formDataToJson(formData);
    postJsonData(i,url,jsonData,function(){ location.reload() });
}
function formSubmit_order(i,url){
    if(document.getElementById('tickets').value > 0){
        const Form = document.getElementById('order-msg');
        let formData = new FormData(Form);
        formData.set('spotId', Id);
        if(chooseHotel != null)
        formData.set('hotelId', chooseHotel.id);
        postData(i,url,formData).then((data)=>{
            alert(data);
            window.location.href = './spot.html'
        });
    }else{
        alert('票数至少为1');
    }
}

//渲染用方法
function InputData_Hotel(record){
    var complexContent = '';
    // 开始渲染
    for (var rec of record){
      complexContent+= `
      <div class="spot">
        <img onerror="this.onerror=null;  this.src='/img/OIP.jpg';" src='${getImgUrl(rec.photos)}' alt="找不到图片" />
        <a href="./hotelDetail.html?id=${rec.hotelId}">${rec.name}</a>
        <p>地址：${rec.address}</p>
        <p>票数：${rec.room}</p>
        <p>票价：${rec.price}元</p>
        <input type="radio" name="chooseHotel" onclick="ChooseHotel('${rec.hotelId}',${rec.price},'${rec.name}')">
      </div>
      `;
    }
    // 获取 div 元素
    const myDiv = document.getElementById('leftSection');
    // 使用 innerHTML 插入复杂的内容
    myDiv.innerHTML = complexContent;
}
function InputData_Comment(record){
    var complexContent = '';
    // 开始渲染
    for (var rec of record){
      complexContent+= `
      <div class="comment-unit">
        <div class="comment-box">
            <div class="user-info">
            <img onerror="this.onerror=null;  this.src='/img/OIP.jpg';" src='${getImgUrl(rec.img)}' alt="找不到图片" />
                <span class="username">${rec.username}</span>
            </div>
            <div class="actions">
                <span class="comment-score">${rec.score}分</span>
                <button id="like_${rec.commentId}" class="like-button" onclick="changeLike('${rec.commentId}','${rec.likesId}')">
                ${rec.like == 1? '👍' : '👍🏿'} </button>
                <span id="count_${rec.commentId}" class="like-count">${rec.likeCount}</span>
                <button class="like-button" onclick="del(2,'comment','${rec.commentId}')">🗑️</button>
            </div>
        </div>
        <div class="comment-table">
            <p style="font-size:10px">${rec.createTime}</p>
            <p>${rec.text}</p>
        </div>
    </div>
      `;
    }
    // 获取 div 元素
    const myDiv = document.getElementById('comment-table');
    // 使用 innerHTML 插入复杂的内容
    myDiv.innerHTML = complexContent;
}
function ChooseHotel(id,price,name){
    chooseHotel = {};
    chooseHotel.id = id;
    chooseHotel.price = price;
    chooseHotel.name = name;
    document.getElementById("hname").innerHTML =  chooseHotel.name;
}



//初始化
document.addEventListener("DOMContentLoaded", function() {
    //读取参数
    var id = getParameterByName('id');
    Id = id;
    //获取数据
    getRetData(1,'/ts/spot/'+id).then(data => {
        console.log('获得数据:', data);
        chooseSpot = data;
        document.getElementById('inp_img').setAttribute('src', getImgUrl(data.photos));
        document.getElementById("inp_name").innerHTML =  data.name;
        document.getElementById("inp_spotId").innerHTML = '景点号：' + data.spotId;
        document.getElementById("inp_address").innerHTML = '景点地址：' + data.address;
        document.getElementById("inp_describes").innerHTML = '景点介绍：' + data.describes;
        document.getElementById("inp_route").innerHTML = '景点路线：' + data.route;
        document.getElementById("inp_score").innerHTML = '景点评分：' + (data.score == null ? '目前没有评分' : data.score.toFixed(2));
        document.getElementById("inp_price").innerHTML = '景点票价：' + data.price;
        document.getElementById("inp_tickets").innerHTML = '景点票数：' + data.tickets;

        document.getElementById("sname").innerHTML =  chooseSpot.name;
      });
    getRetData(1,'/ts/hotel/page?page=-1&pageSize=-1&in_spot='+id).then(data => {
        console.log('获得数据:', data);
        InputData_Hotel(data.records)
      });
    getRetData(2,'/ts/comment/userpage?page=1&pageSize=10000&spotId='+id).then(data => {
        console.log('获得数据:', data);
        InputData_Comment(data.records)
      });

      

    // 返回按钮的点击事件
    const backButton = document.getElementById("backButton");
    backButton.addEventListener("click", function() {
        // 在这里添加返回逻辑，例如返回上一页或跳转到其他页面
        window.location.href = './spot.html'
    });
});

document.addEventListener('submit', function(event) {
    event.preventDefault(); // 阻止表单默认提交行为
    // 在这里添加其他的逻辑，或者不进行任何操作
  });
