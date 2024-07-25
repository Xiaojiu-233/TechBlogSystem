//数据
dict = {"博客":"blog","评论":"comment"};

//方法
async function SetPage(Url,curPage,pageSize,args,func){
  //请求数据处理
  var url = Url;
  url += `?page=${curPage}&pageSize=${pageSize}`
  for(let arg of args){
    var value = document.getElementById('search-'+ arg).value;
    url += `&${arg}=${value}`
  }
  //发出请求
  getRetData(url).then(data => {
    console.log('获得数据:', data);
    // 在这里处理返回的数据
    callFunction(func,data.records);
    //开始分页
    $('#paginator-container').bootstrapPaginator({
      // 这里是你的配置选项
      currentPage: curPage,
      totalPages: Math.ceil(data.total / data.size),
      size: 'normal', // 或 'large'、'small'
      onPageClicked: function(e, originalEvent, type, page) {
        console.log(page);
        SetPage(Url,page,pageSize,args,func)
        // 处理页面点击事件
      }
    });
  })  
  .catch(error => {
    console.error('获得报错:', error);
    // 处理错误情况
  });

}
function del(elem,id){
  var url = '/blog/' + elem + '/del/' + id;
  var confirmation = confirm(`确定要删除编号为${id}的元素吗？`);
  if (confirmation) {
    postJsonData(url,null,function(){
      location.reload();
    });
  } 
}
function unable(elem,id){
  var url = '/blog/' + elem + '/reject/' + id;
  var confirmation = confirm(`确定要驳回编号为${id}的元素吗？`);
  if (confirmation) {
    postJsonData(url,null,function(){
      location.reload();
    });
  } 
}
function used(elem,id,tar){
  var url = '/blog/' + elem + '/handle/' + id;
  var confirmation = confirm(`确定要处理编号为${id}的元素吗？`);
  if (confirmation) {
    postJsonData('/blog/' + dict[tar] + '/del/' + id,null,function(){
      postJsonData(url,null,function(){
        location.reload();
      });
    });
  } 
}
function lock(id){
  var num = prompt(`确定要封禁编号为${id}的用户吗？请确定封禁天数，数量为-1则代表永封`,0);
  if (num == -1 || isNonNegativeInteger(num)) {
    var url = `/blog/user/lock?userId=${id}&days=${num}`;
    postJsonData(url,null,function(){
      location.reload();
    });
  } else{
    alert("请输入一个非负整数或-1！");
  
  }
}
function unlock(id){
  var confirmation = confirm(`确定要解禁编号为${id}的用户吗？`);
  if (confirmation) {
    var url = `/blog/user/unlock?userId=${id}`;
    postJsonData(url,null,function(){
      location.reload();
    });
  } 
}
function multiDel(elem){
  const elements = document.querySelectorAll('[id^="op_"]');
  const idNumbers = [];
  elements.forEach(element => {
    if(element.checked){
      const id = element.id; // 获取元素的id
      const idNumber = id.split('_')[1]; // 获取编号部分并转换为数字
      idNumbers.push(idNumber); // 将编号信息存储到数组中
    }
  });
  console.log(idNumbers); // 输出存储的编号数组
  if(idNumbers.length == 0)
  alert('请在右侧复选框中选择元素！');
  else{
    var id = idNumbers.join(',');
    var url = '/blog/' + elem + '/del/' + id;
    var confirmation = confirm(`确定要删除编号为${id}的元素吗？`);
    if (confirmation) {
      postJsonData(url,null,function(){
        location.reload();
      })
    } 
  }

}
function changeFrame(url,id){
  window.location.href = './'+url+'Form.html?id=' + id;
}

//渲染用方法
function InputData_User(record){
  var complexContent = '';
  // 开始渲染
  for (var rec of record){
    const days = calculateDaysDifference(rec.isLock);
    complexContent+= `
    <tr>
      <td>${rec.id}</td>
      <td>${rec.name}</td>
      <td>${rec.username}</td>
      <td><img onerror="this.onerror=null;  this.src='../img/OIP.jpg';"
       src="${getImgUrl(rec.headImg)}" alt="找不到图片" /></td>
      <td>${rec.phone}</td>
      <td>${rec.age}</td>
      <td>${rec.loginTime}</td>
      <td>${rec.registerTime}</td>
      <td>${rec.sex}</td>
      <td class="flowTd" onclick="showDetailsPopup(this)">${rec.sign}
        <div class="details-popup">${rec.sign}</div>
      </td>
      <td>${days == 0 ? '未封禁' : days + '天'}</td>
      <td><button class="choiceBtn gray" onclick="lock(${rec.id})">封禁</button></td>
      <td><button class="choiceBtn green" onclick="unlock(${rec.id})">解封</button></td>
    </tr>
    `;
  }
  // 获取 div 元素
  const myDiv = document.getElementById('user-body');
  // 使用 innerHTML 插入复杂的内容
  myDiv.innerHTML = complexContent;
}
function InputData_Blog(record){
  var complexContent = '';
  // 开始渲染
  for (var rec of record){
    complexContent+= `
    <tr>
      <td><input type="checkbox" id="op_${rec.id}"></td>
      <td>${rec.id}</td>
      <td>${rec.title}</td>
      <td>${rec.userName}</td>
      <td>${rec.userId}</td>
      <td>${rec.share}</td>
      <td>${rec.createTime}</td>
      <td><button class="choiceBtn" onclick="del('hotel','${rec.id}')">删除</button></td>
    </tr>
    `;
  }
  // 获取 div 元素
  const myDiv = document.getElementById('blog-body');
  // 使用 innerHTML 插入复杂的内容
  myDiv.innerHTML = complexContent;
}
function InputData_Comment(record){
  var complexContent = '';
  // 开始渲染
  for (var rec of record){
    complexContent+= `
    <tr>
      <td><input type="checkbox" id="op_${rec.id}"></td>
      <td>${rec.id}</td>
      <td>${rec.blogId}</td>
      <td>${rec.userId}</td>
      <td>${rec.userName}</td>
      <td class="flowTd" onclick="showDetailsPopup(this)">${rec.text}
        <div class="details-popup">${rec.text}</div>
      </td>
      <td>${rec.share}</td>
      <td>${rec.createTime}</td>
      <td><button class="choiceBtn" onclick="del('comment','${rec.id}')">删除</button></td>
    </tr>
    `;
  }
  // 获取 div 元素
  const myDiv = document.getElementById('comment-body');
  // 使用 innerHTML 插入复杂的内容
  myDiv.innerHTML = complexContent;
}
function InputData_Emp(record){
  var complexContent = '';
  // 开始渲染
  for (var rec of record){
    complexContent+= `
    <tr>
      <td><input type="checkbox" id="op_${rec.id}"></td>
      <td>${rec.id}</td>
      <td>${rec.username}</td>
      <td>${rec.createTime}</td>
      <td>${rec.loginTime}</td>
      <td>${rec.id == 1 ? '是' : '否'}</td>
      <td><button class="choiceBtn green" onclick="window.location.href = './empUpd.html?eid=${rec.id}'">修改</button></td>
      <td><button class="choiceBtn gray" onclick="del('emp','${rec.id}')">删除</button></td>
    </tr>
    `;
  }
  // 获取 div 元素
  const myDiv = document.getElementById('emp-body');
  // 使用 innerHTML 插入复杂的内容
  myDiv.innerHTML = complexContent;
}
function InputData_Notice(record){
  var complexContent = '';
  // 开始渲染
  for (var rec of record){
    complexContent+= `
    <tr>
      <td><input type="checkbox" id="op_${rec.id}"></td>
      <td>${rec.id}</td>
      <td>${rec.title}</td>
      <td class="flowTd" onclick="showDetailsPopup(this)">${rec.text}
        <div class="details-popup">${rec.text}</div>
      </td>
      <td>${rec.createTime}</td>
      <td><button class="choiceBtn" onclick="del('mail/notice','${rec.id}')">删除</button></td>
    </tr>
    `;
  }
  // 获取 div 元素
  const myDiv = document.getElementById('notice-body');
  // 使用 innerHTML 插入复杂的内容
  myDiv.innerHTML = complexContent;
}
function InputData_Report(record){
  var complexContent = '';
  // 开始渲染
  for (var rec of record){
    complexContent+= `
    <tr>
      <td>${rec.id}</td>
      <td>${rec.target}</td>
      <td>${rec.targetId}</td>
      <td>${rec.userId}</td>
      <td class="flowTd" onclick="showDetailsPopup(this)">${rec.text}
        <div class="details-popup">${rec.text}</div>
      </td>
      <td>${rec.createTime}</td>
      <td><button class="choiceBtn green" onclick="used('report',${rec.id},'${rec.target}')">处理</button></td>
      <td><button class="choiceBtn red" onclick="unable('report',${rec.id},'${rec.target}')">驳回</button></td>
    </tr>
    `;
  }
  // 获取 div 元素
  const myDiv = document.getElementById('report-body');
  // 使用 innerHTML 插入复杂的内容
  myDiv.innerHTML = complexContent;
}
function showDetailsPopup(cell) {
  const popup = cell.querySelector('.details-popup');
  popup.style.display = (popup.style.display === 'block') ? 'none' : 'block';
}

//功能方法
function callFunction(funcName,arg) {
  // 检查函数是否存在
  if (window[funcName] && typeof window[funcName] === 'function') {
    // 调用传入的函数名
    window[funcName](arg);
  } else {
    console.log('函数不存在或不可用');
  }
}
function isNonNegativeInteger(value) {
  // 使用正则表达式检查输入是否为非负整数
  return /^\d+$/.test(value) && Number(value) >= 0;
}
function changeIframe(name){
  // 获取需要修改的元素
  const Element = document.getElementById('IFrame');
  // 使用 setAttribute() 修改元素的 src 属性
  Element.setAttribute('th:src', `@{/html/${name}.html}`);
  Element.setAttribute('src', `./${name}.html`);
}
function calculateDaysDifference(timestamp) {  
  if(timestamp == 0)return 0;
  // 获取当前时间的时间戳  
  const now = Date.now();  
  // 计算两个时间戳之间的毫秒差  
  const diff = timestamp - now;  
  // 将毫秒差转换为天数  
  // 注意：Math.abs确保得到的是正值，因为时间戳可能表示过去或未来的时间  
  const daysDifference = Math.abs(diff) / (1000 * 60 * 60 * 24);  
  return Math.ceil(daysDifference);  
}  

//初始化