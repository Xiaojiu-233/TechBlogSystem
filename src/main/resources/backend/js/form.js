var updateSubmit = false;
var Id = 0;
var ImgValue = '';

//方法
function formSubmit_sh(i,url,cate){
    const Form = document.getElementById('InpForm');
    var fileInput = document.getElementById('photos');
    let formData1 = new FormData(Form);
    if(fileInput.files.length == 0 ){
        if(updateSubmit){
            formData1.set(cate.toLowerCase()+'Id', Id);
            formData1.set('photos', ImgValue);
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
        formData1.set('photos', '/ts/common/download/'+data);
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
function formSubmit_add(url){
    const Form = document.getElementById('InpForm');
    let formData = new FormData(Form);
    let jsonData = formDataToJson(formData);
        postJsonData(url,jsonData,function(){
            location.reload();
        });

}
function formSubmit_upd(url){
    //读取数据
    idNum = getParameterByName('eid');
    //提交
    const Form = document.getElementById('InpForm');
    let formData = new FormData(Form);
    let param = formDataToStr(formData);
    param += '&empId='+idNum;
    postJsonData(url+param,null,function(){
        location.reload();
    });

}
function back(url){
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

//初始化
