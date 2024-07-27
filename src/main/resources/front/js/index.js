var UserBody = null;

//方法
function showContent(contentId) {
    var iframe = document.getElementById('contentFrame');
    iframe.src = '/html/'+ contentId + '.html';
}

function goBack(){
  //发出请求
  postJsonData('/blog/user/logout',null,function(){
      window.location.href = './login.html'
  });
  
}

//初始化
getRetData('/blog/user/-1').then(data => {
  console.log('获得数据:', data);
  UserBody = data;
  document.getElementById("empName").innerHTML = data.name;
  document.getElementById('userImg').setAttribute('src', getImgUrl(data.headImg));
  //读取消息数
  var num = 0;
  getRetData('/blog/like/count').then(data => {
    num += data;
    getRetData('/blog/mail/noticecount').then(data => {
      num += data;
      getRetData('/blog/mail/count').then(data => {
        num += data;
        console.log('消息数:', num);
        document.getElementById("mail_num").innerHTML = `看消息(${num})`;
      });
    });
  });
});
