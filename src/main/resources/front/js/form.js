var updateSubmit = false;
var Id = 0;
var ImgValue = '';

//方法
function formSubmit_img(i,url,cate,img){
    const Form = document.getElementById('InpForm');
    var fileInput = document.getElementById(img);
    let formData1 = new FormData(Form);
    if(fileInput.files.length == 0 ){
        if(updateSubmit){
            formData1.set(cate.toLowerCase()+'Id', Id);
            formData1.set(img, ImgValue);
            postData(i,url+'/update',formData1).then(data => {
                alert(data);
            })
            return;
        }else{
            alert('请先插入图片！');
            return;
        }
    }
    let formData2 = new FormData();
    formData2.append('file', fileInput.files[0]);
    postData(1,'/ts/common/upload/'+cate,formData2).then(data => {
        formData1.set(img, '/ts/common/download/'+data);
        if(updateSubmit){
            formData1.set(cate.toLowerCase()+'Id', Id);
            postData(i,url+'/update',formData1).then(data => {
                alert(data);
            })
        }else{
            let jsonData = formDataToJson(formData1);
            postJsonData(i,url,jsonData,function(){
                location.reload();
            });
        }

    });
}
function formSubmit_img2(i,url,cate,img){
    const Form = document.getElementById('InpForm');
    var uid = getParameterByName('uid');
    var fileInput = document.getElementById(img);
    let formData1 = new FormData(Form);
    if(fileInput.files.length == 0 ){
        if(updateSubmit){
            formData1.set('userId', uid);
            formData1.set('logId', Id);
            formData1.set(img, ImgValue);
            postData(i,url+'/update',formData1).then(data => {
                alert(data);
            })
            return;
        }else{
            alert('请先插入图片！');
            return;
        }
    }
    let formData2 = new FormData();
    formData2.append('file', fileInput.files[0]);
    postData(1,'/ts/common/upload/'+cate,formData2).then(data => {
        formData1.set(img, '/ts/common/download/'+data);
        if(updateSubmit){
            formData1.set('userId', uid);
            formData1.set('logId', Id);
            postData(i,url+'/update',formData1).then(data => {
                alert(data);
            })
        }else{
            let jsonData = formDataToJson(formData1);
            postJsonData(i,url,jsonData,function(){
                location.reload();
            });
        }

    });
}
function formSubmit_user(i,url){
    const Form = document.getElementById('register-form');
    let formData = new FormData(Form);
    if(updateSubmit){
        formData.set('userId', Id);
        postData(i,url+'/update',formData).then(data => {
            alert(data);
        })
    }else{
        let jsonData = formDataToJson(formData);
        postJsonData(i,url,jsonData,function(){
            window.location.href = './login.html'
        });
    }
}

function back(url){
    var uid = getParameterByName('uid');
    if(uid !== null)
        window.location.href = './my.html'
    else
        window.location.href = './'+url+'.html'
}
function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}
function setMessage(i,url){
    //读取参数
    var id = getParameterByName('id');
    Id = id;
    //进行处理
    if(id != -1){
        updateSubmit = true;
        getRetData(i,url+id).then(data => {
            Object.entries(data).forEach(([key, value]) => {
                var element = document.getElementById(key);
                if (element != null && element.getAttribute("type") !== "file") {
                    if(element.getAttribute("type") === "checkbox")
                        element.checked=value == 1 ;
                    else
                        element.value=value;
                }
                if(key == 'photos'){
                    ImgValue = value;
                }
              });
        });
    }
}
function setUpdate(id){
    updateSubmit = true;
    Id= id;
}

//初始化
