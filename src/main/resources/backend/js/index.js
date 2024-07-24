//方法
function goBack(){
    //发出请求
    postJsonData('/blog/emp/logout',null,function(){
        window.location.href = './login.html'
    });
    
}

//初始化
getRetData('/blog/emp/-1').then(data => {
    console.log('获得数据:', data);
    document.getElementById("empName").innerHTML = '欢迎您，' + data.username;
});
