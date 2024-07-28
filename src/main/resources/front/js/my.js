var Id = 0;
var UserBody = null;

function showContent(contentId) {
    // 隐藏所有内容区域
    document.querySelectorAll(".content-section").forEach(function (section) {
      section.style.display = "none";
    });
  
    // 显示目标内容区域
    document.getElementById(contentId).style.display = "block";
}
function formSubmit_pwd(url){
  const Form = document.getElementById('PwdForm');
  let formData = new FormData(Form);
  formData.set('userId', Id);
  postData(url,formData).then(data => {
    alert(data);
  })
}
function getId(){
  return Id;
}

//初始化
showContent('profile-info');
getRetData('/blog/user/-1').then(data => {
  console.log('获得数据:', data);
  setUpdate(data.id);
  UserBody = data;
  Object.entries(data).forEach(([key, value]) => {
    //个人信息
    var element = document.getElementById('m_' +key);
    if (element != null ) {
      if(key == 'headImg'){
        element.src = getImgUrl(value);
      }else{
        element.innerHTML = value;
      }
    }
    //修改信息
    var element = document.getElementById(key);
    if (element != null && element.getAttribute("type") !== "file") {
      element.value=value;
    }
    if(key == 'headImg'){
        ImgValue = value;
    }
  });
});
