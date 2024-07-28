var updateSubmit = false;
var Id = 0;
var ImgValue = '';

//方法
function formSubmit_img(url,cate,img){
    const Form = document.getElementById('InpForm');
    var fileInput = document.getElementById(img);

    let formData1 = new FormData(Form);
    if(fileInput.files.length == 0 ){
        if(updateSubmit){
            formData1.set('id', Id);
            formData1.set(img, ImgValue);
            postData(url+'/upd',formData1).then(data => {
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
    postData('/blog/common/upload/'+cate,formData2).then(data => {
        formData1.set(img, '/blog/common/download/'+data);
        if(updateSubmit){
            formData1.set('id', Id);
            postData(url+'/upd',formData1).then(data => {
                alert(data);
            })
        }else{
            let jsonData = formDataToJson(formData1);
            postJsonData(url,jsonData,function(){
                location.reload();
            });
        }

    });

}
function formSubmit_img2(url,cate,img){
    const Form = document.getElementById('InpForm');
    var uid = getParameterByName('uid');
    var fileInput = document.getElementById(img);
    
    let formData1 = new FormData(Form);
    if(fileInput.files.length == 0 ){
        if(updateSubmit){
            formData1.set('userId', uid);
            formData1.set('id', Id);
            formData1.set(img, ImgValue);
            postData(url+'/upd',formData1).then(data => {
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
    postData('/blog/common/upload/'+cate,formData2).then(data => {
        formData1.set(img, '/blog/common/download/'+data);
        console.log('是否为更新上传' + updateSubmit);
        if(updateSubmit){
            formData1.set('userId', uid);
            formData1.set('id', Id);
            postData(url+'/upd',formData1).then(data => {
                alert(data);
            })
        }else{
            let jsonData = formDataToJson(formData1);
            var time = new Date(jsonData.targetTime).getTime();
            if(isNaN(time)){time = 0}
            postJsonData(url+`/${time}`,jsonData,function(){
                location.reload();
            });
        }

    });
}
function formSubmit_user(url){
    const Form = document.getElementById('register-form');
    let formData = new FormData(Form);
    if(updateSubmit){
        formData.set('userId', Id);
        postData(url+'/update',formData).then(data => {
            alert(data);
        })
    }else{
        let jsonData = formDataToJson(formData);
        postJsonData(url,jsonData,function(){
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
function setMessage(url){
    //读取参数
    var id = getParameterByName('id');
    Id = id;
    console.log('id:'+id);
    //进行处理
    if(id != null){
        updateSubmit = true;
        getRetData(url+id).then(data => {
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
