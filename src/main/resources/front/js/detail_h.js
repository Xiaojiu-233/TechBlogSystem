var Id = 0;

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

//渲染用方法
function InputData_Hotel(record){
    var complexContent = '';
    // 开始渲染
    for (var rec of record){
      complexContent+= `
      <div class="spot" onclick="alert('${rec.hotelId}')">
        <img onerror="this.onerror=null;  this.src='/img/OIP.jpg';" src='${getImgUrl(rec.photos)}' alt="找不到图片" />
        <p>${rec.name}</p>
        <p>地址：${rec.address}</p>
        <p>票数：${rec.room}</p>
        <p>票价：${rec.price}元</p>
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
function formSubmit_comment(i,url){
    const Form = document.getElementById('comment-form');
    let formData = new FormData(Form);
    formData.set('hotelId', Id);
    let jsonData = formDataToJson(formData);
    postJsonData(i,url,jsonData,function(){ location.reload() });

}

//初始化
document.addEventListener("DOMContentLoaded", function() {
    //读取参数
    var id = getParameterByName('id');
    Id = id;
    //获取数据
    getRetData(1,'/ts/hotel/'+id).then(data => {
        console.log('获得数据:', data);
        UserBody = data;
        document.getElementById('inp_img').setAttribute('src', getImgUrl(data.photos));
        document.getElementById("inp_name").innerHTML =  data.name;
        document.getElementById("inp_hotelId").innerHTML = '酒店号：' + data.hotelId;
        document.getElementById("inp_address").innerHTML = '酒店地址：' + data.address;
        document.getElementById("inp_describes").innerHTML = '酒店介绍：' + data.describes;
        document.getElementById("inp_score").innerHTML = '酒店评分：' + (data.score == null ? '目前没有评分' : data.score.toFixed(2));
        document.getElementById("inp_price").innerHTML = '酒店住房价：' + data.price;
        document.getElementById("inp_room").innerHTML = '酒店房间数：' + data.room;
      });
    getRetData(2,'/ts/comment/userpage?page=1&pageSize=10000&hotelId='+id).then(data => {
        console.log('获得数据:', data);
        InputData_Comment(data.records)
      });
});
