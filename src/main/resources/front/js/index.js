var UserBody = null;

//方法
function showContent(contentId) {
    var iframe = document.getElementById('contentFrame');
    iframe.src = '/html/'+ contentId + '.html';
}

function goBack(){
  //发出请求
  postJsonData(2,'/ts/user/logout',null,function(){
      window.location.href = './login.html'
  });
  
}

//初始化
getRetData(2,'/ts/user/-1').then(data => {
  console.log('获得数据:', data);
  UserBody = data;
  document.getElementById("empName").innerHTML = data.name;
  document.getElementById('userImg').setAttribute('src', getImgUrl(data.headImg));
});
