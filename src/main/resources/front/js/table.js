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
async function SetPage2(Url,curPage,pageSize,args,func){
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
    $('#paginator-container2').bootstrapPaginator({
      // 这里是你的配置选项
      currentPage: curPage,
      totalPages: Math.ceil(data.total / data.size),
      size: 'normal', // 或 'large'、'small'
      onPageClicked: function(e, originalEvent, type, page) {
        console.log(page);
        SetPage2(Url,page,pageSize,args,func)
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
  var confirmation = confirm(`确定要删除这个元素吗？`);
  if (confirmation) {
    postJsonData(url,null,function(){
      location.reload();
    });
  } 
}
function delFunc(elem,id,func){
  var url = '/blog/' + elem + '/del/' + id;
  var confirmation = confirm(`确定要删除这个元素吗？`);
  if (confirmation) {
    postJsonData(url,null,func);
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
    var confirmation = confirm(`确定要删除这些元素吗？`);
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
function InputData_Spot(record){
  var complexContent = '';
  // 开始渲染
  for (var rec of record){
    complexContent+= `
    <div class="spot" onclick="window.location.href = './spotDetail.html?id=${rec.spotId}'">
      <img onerror="this.onerror=null;  this.src='/img/OIP.jpg';" src='${getImgUrl(rec.photos)}' alt="找不到图片" />
      <p>${rec.name}</p>
      <p>地址：${rec.address}</p>
      <p>票数：${rec.tickets}</p>
      <p>票价：${rec.price}元</p>
    </div>
    `;
  }
  // 获取 div 元素
  const myDiv = document.getElementById('spot-body');
  // 使用 innerHTML 插入复杂的内容
  myDiv.innerHTML = complexContent;
}
function InputData_Blog(record){
  var complexContent = '';
  // 开始渲染
  for (var rec of record){
    complexContent+= `
    <div class="spot" onclick="window.location.href = './blogDetail.html?id=${rec.id}&li=${rec.likeNum}&share=${rec.share}&ls=${rec.likeState}&a=${rec.userName}'">
      <img onerror="this.onerror=null;  this.src='/img/OIP.jpg';" src='${getImgUrl(rec.images)}' alt="找不到图片" />
      <p class="hiddenText">${rec.title}</p>
      作者：<a class="hiddenText" href="./userSpace.html?id=${rec.userId}">${rec.userName}</a>
      <p>点赞：${rec.likeNum}</p>
      <p>转发：${rec.share}</p>
    </div>
    `;
  }
  // 获取 div 元素
  const myDiv = document.getElementById('blog-body');
  // 使用 innerHTML 插入复杂的内容
  myDiv.innerHTML = complexContent;
}
function InputData_LogSp(record){
  var complexContent = '';
  var uid = getId();
  // 开始渲染
  for (var rec of record){
    if(rec.userId == uid){
      complexContent+= `
      <div class="spot">
        <img onerror="this.onerror=null;  this.src='/img/OIP.jpg';" src='${getImgUrl(rec.photos)}' alt="找不到图片" />
        <p class="hiddenText">${rec.title}</p>
        <p class="hiddenText">${rec.text}</p>
        <p>作者：${rec.author}</p>
        <p>点赞：${rec.likeCount}</p>
        <br>
        <button class="like-button" onclick="window.location.href = './logForm.html?id=${rec.logId}&uid=${getId()}'">✍️</button>
        <button class="like-button" onclick="delFunc(2,'touristLog','${rec.logId}',function(){
          location.reload()})">🗑️</button>
      </div>
      `;
    }
  }
  // 获取 div 元素
  const myDiv = document.getElementById('log-body');
  // 使用 innerHTML 插入复杂的内容
  myDiv.innerHTML = complexContent;
}
function InputData_Order(record){
  var complexContent = '';
  // 开始渲染
  for (var rec of record){
    complexContent+= `
    <tr>
      <td>${rec.orderId}</td>
      <td>${rec.spotId}</td>
      <td>${rec.hotelId}</td>
      <td>${rec.totalPrice }</td>
      <td>${rec.startDate }</td>
      <td>${rec.status == 0 ? '已下单' :rec.status == 2 ? '已使用' : '已失效'}</td>
      <td>${rec.tickets}</td>
      <td>${rec.rooms}</td>
    </tr>
    `;
  }
  // 获取 div 元素
  const myDiv = document.getElementById('order-body');
  // 使用 innerHTML 插入复杂的内容
  myDiv.innerHTML = complexContent;
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

//初始化