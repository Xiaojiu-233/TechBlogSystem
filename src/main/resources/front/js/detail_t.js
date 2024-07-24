var Id = 0;
var Likes = 0;
var Author = 0;
var LikeStatu = 0;

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

//初始化
//读取参数
var id = getParameterByName('id');
Id = id;
var Likes = getParameterByName('li');
var LikeStatu = getParameterByName('ls');
var Author = getParameterByName('a');
//获取数据
getRetData(2,'/ts/touristLog/'+id).then(data => {
    console.log('获得数据:', data);
    document.getElementById('tlog-img').src = getImgUrl(data.photos);
    document.getElementById("tlog-title").innerHTML =  data.title;
    document.getElementById("tlog-text").innerHTML =  data.text;
    document.getElementById("tlog-date").innerHTML = '创建日期：' +  data.createTime;
    document.getElementById("tlog-author").innerHTML = '作者：' +  Author;
    var LikeText = `
    <button id="like_${data.logId}" class="like-button" onclick="changeLike('${data.logId}','${data.likesId}')">
    ${LikeStatu == 1? '👍' : '👍🏿'} </button>
    <span id="count_${data.logId}" class="like-count">${Likes}</span>
    <button class="like-button" onclick="delFunc(2,'touristLog','${data.logId}',function(){
        window.location.href = './log.html'
    })">🗑️</button>
    `
    // 获取 div 元素
    const myDiv = document.getElementById('tlog-like');
    // 使用 innerHTML 插入复杂的内容
    myDiv.innerHTML = LikeText;
});