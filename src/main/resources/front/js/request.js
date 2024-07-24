// JavaScript文件（script.js）

//后端请求区 业务: 1后台 2旅游 3用户
var baseUrl = ["http://localhost:8101","http://localhost:8201","http://localhost:8301"];
const proxyUrl = '';
var loadCat = false;
function getData(i,url){
  fetch( proxyUrl + baseUrl[i] + url,{
    method: 'GET',
    credentials: 'include',
  }) // 替换成后端提供数据的端点
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  })
  .then(data => {
    console.log(data);
    if(data != undefined && data != null){
      if(data.code == 1){
        alert(data.data);
      }else{
        alert(data.msg);
      }
    }
    
  })
  .catch(error => {
    console.error('There has been a problem with your fetch operation:', error);
  });
}
function getRetData(i,url){
  return new Promise((resolve, reject) => {
    fetch( proxyUrl + baseUrl[i] + url,{
      method: 'GET',
      credentials: 'include',
    }) // 替换成后端提供数据的端点
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      if(data != undefined && data != null){
        if(data.code == 1){
          resolve(data.data);
        }else{
          alert(data.msg);
          reject(new Error(data.msg));
        }
      }
      
    })
    .catch(error => {
      console.error('There has been a problem with your fetch operation:', error);
    });
  })
  
}
function postData(i,url,dt){
  return new Promise((resolve, reject) => {
    fetch( proxyUrl + baseUrl[i] + url,{
      method: 'POST',
      credentials: 'include',
      body: dt
    }) // 替换成后端提供数据的端点
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      if(data != undefined && data != null){
        if(data.code == 1){
          resolve(data.data);
        }else{
          alert(data.msg);
          reject(new Error(data.msg));
        }
      }
      
    })
    .catch(error => {
      console.error('There has been a problem with your fetch operation:', error);
    });
  })
}
function postFormData(i,url,dt){
  return new Promise((resolve, reject) => {
    fetch( proxyUrl + baseUrl[i] + url,{
      method: 'POST',
      credentials: 'include',
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      body: dt
    }) // 替换成后端提供数据的端点
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      if(data != undefined && data != null){
        if(data.code == 1){
          resolve(data.data);
        }else{
          alert(data.msg);
          reject(new Error(data.msg));
        }
      }
      
    })
    .catch(error => {
      console.error('There has been a problem with your fetch operation:', error);
    });
  })
}
function postJsonData(i,url,data,func){
  fetch( proxyUrl + baseUrl[i] + url,{
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  }) // 替换成后端提供数据的端点
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  })
  .then(data => {
    if(data != undefined && data != null){
      if(data.code == 1){
        alert(data.data);
        func();
      }else{
        alert(data.msg);
      }
    }
    
  })
  .catch(error => {
    console.error('There has been a problem with your fetch operation:', error);
  });
}
function postRetJsonData(i,url,data){
  fetch( proxyUrl + baseUrl[i] + url,{
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data)
  }) // 替换成后端提供数据的端点
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  })
  .then(data => {
    if(data != undefined && data != null){
      if(data.code == 1){
        return data.data;
      }else{
        alert(data.msg);
      }
    }
    
  })
  .catch(error => {
    console.error('There has been a problem with your fetch operation:', error);
  });
}
function formSubmit(i,form,url,func){
  const Form = document.getElementById(form);
  let formData = new FormData(Form);
  let jsonData = formDataToJson(formData);
  postJsonData(i,url,jsonData,func);
}
function getImgUrl(url){
  if(!(url.includes('html')))
    return baseUrl[1] + url;
  return url;
}

//辅助方法区
function formDataToJson(formData) {  
  let json = {};  
  for (let [key, value] of formData.entries()) {  
      json[key] = value;
  }  
  console.log(json);
  return json;  
}  

//初始化执行区
document.addEventListener('submit', function(event) {
  event.preventDefault(); // 阻止表单默认提交行为
  // 在这里添加其他的逻辑，或者不进行任何操作
});
