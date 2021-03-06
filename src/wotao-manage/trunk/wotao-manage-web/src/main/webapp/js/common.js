Date.prototype.format = function(format){ 
    var o =  { 
    "M+" : this.getMonth()+1, //month 
    "d+" : this.getDate(), //day 
    "h+" : this.getHours(), //hour 
    "m+" : this.getMinutes(), //minute 
    "s+" : this.getSeconds(), //second 
    "q+" : Math.floor((this.getMonth()+3)/3), //quarter 
    "S" : this.getMilliseconds() //millisecond 
    };
    if(/(y+)/.test(format)){ 
    	format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
    }
    for(var k in o)  { 
	    if(new RegExp("("+ k +")").test(format)){ 
	    	format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
	    } 
    } 
    return format; 
};

var TT = TAOTAO = {
	// 编辑器参数
	kingEditorParams : {
		filePostName  : "uploadFile",//上传表单名称
		uploadJson : '/rest/pic/upload',//上传路径
		dir : "image"//上传类型
	},
	// 格式化时间
	formatDateTime : function(val,row){
		var now = new Date(val);
    	return now.format("yyyy-MM-dd hh:mm:ss");
	},
	// 格式化连接
	formatUrl : function(val,row){
		if(val){
			return "<a href='"+val+"' target='_blank'>查看</a>";			
		}
		return "";
	},
	// 格式化价格
	formatPrice : function(val,row){
		return (val/100).toFixed(2);//toFixed() 方法可把 Number 四舍五入为指定小数位数的数字。
	},
	// 格式化商品的状态
	formatItemStatus : function formatStatus(val,row){
        if (val == 1){
            return '正常';
        } else if(val == 2){
        	return '<span style="color:red;">下架</span>';
        } else {
        	return '未知';
        }
    },
    
    init : function(data){
    	alert("TAOTAO.init进入");
    	alert("data="+JSON.stringify(data));
    	//alert(JSON.stringify(data));  此处加入这句会显示{}，说明data是返回来的值。
    	this.initPicUpload(data);
    	//data就是{
		//	"pics" : data.image,
		//	"cid" : data.cid,
		//	"title":data.title,
		//	fun:function(node){
		//		alert("fun:function(node){");
		//		TAOTAO.changeItemParam(node, "itemeEditForm");
		//	}
		//}
    	this.initItemCat(data);
    },
    // 初始化图片上传组件
    initPicUpload : function(data){//data由
    	$(".picFileUpload").each(function(i,e){
    		var _ele = $(e);
    		_ele.siblings("div.pics").remove();
    		_ele.after('\
    			<div class="pics">\
        			<ul></ul>\
        		</div>');
    		// 回显图片
    		alert("回显data="+JSON.stringify(data));
        	if(data && data.pics){
        		var imgs = data.pics.split(",");
        		for(var i in imgs){
        			if($.trim(imgs[i]).length > 0){
        				_ele.siblings(".pics").find("ul").append("<li><a href='"+imgs[i]+"' target='_blank' class='aaa'>看大图</a><img src='"+imgs[i]+"' width='80' height='50' /></li>");
        			}
        		}
        	}
        	alert("回显完毕data="+JSON.stringify(data));
        	$(e).click(function(){
        		var form = $(this).parentsUntil("form").parent("form");//拿到这个class="picFileUpload"链接所在的form表单
        		KindEditor.editor(TT.kingEditorParams).loadPlugin('multiimage',function(){//TT.kingEditorParams为上传参数，multiimage指定多图片上传
        			var editor = this;
        			editor.plugin.multiImageDialog({
						clickFn : function(urlList) {//在点击全部插入按钮的时候执行
							var imgArray = [];
							KindEditor.each(urlList, function(i, data) {
								imgArray.push(data.url);
//								form.find(".pics ul").append("<li><a href='"+data.url+"' target='_blank'><img src='"+data.url+"' width='80' height='50' /></a></li>");
								form.find(".pics ul").append("<li><a href='"+data.url+"' target='_blank' class='aaa'>看大图</a><img src='"+data.url+"' width='80' height='50' /></li>");
							});
							form.find("[name=image]").val(imgArray.join(","));
							editor.hideDialog();
						}
					});
        		});
        	});
    	});
    },
    
    // 初始化选择类目组件
    initItemCat : function(data){
    	//遍历，i是索引，e是$(".selectItemCat")的每一个元素
    	alert("初始化类目组件，data="+JSON.stringify(data));
    	$(".selectItemCat").each(function(i,e){
    		var _ele = $(e);
    		if(data && data.cid){
//    			alert(JSON.stringify(data));  
    			_ele.after("<span style='margin-left:10px;'>"+data.title+"</span>");
    		}else{
    			_ele.after("<span style='margin-left:10px;'></span>");
    		}
    		_ele.unbind('click').click(function(){
    			$("<div>").css({padding:"5px"}).html("<ul>")
    			.window({//$("<div>")是新增一个div的意思，$("div")是选择一个div
    				width:'500',
    			    height:"450",
    			    modal:true,
    			    closed:true,
    			    iconCls:'icon-save',
    			    title:'选择类目',
    			    onOpen : function(){
    			    	var _win = this;
    			    	$("ul",_win).tree({
    			    		url:'/rest/item/cat',
    			    		animate:true,
    			    		method:"GET",
    			    		onClick : function(node){
    			    			if($(this).tree("isLeaf",node.target)){//如果这个node是叶子节点
    			    				// 填写到cid中
    			    				_ele.parent().find("[name=cid]").val(node.id);//实际上是传给了input元素
    			    				_ele.next().text(node.text).attr("cid",node.id);//是给_ele.next()这个对象赋值为node.text
    			    				$(_win).window('close');
    			    				if(data && data.fun){
    			    					//通过.call执行传入data的function方法
    			    					//第一个参数必须一般为this，指定this后面的参数为this对象的参数。下句的意思就是调用fun函数
    			    					//把this.node传入进去。
    			    					data.fun.call(this,node);
    			    				}
    			    			}
    			    		}
    			    	});
    			    },
    			    onClose : function(){
    			    	$(this).window("destroy");
    			    }
    			}).window('open');
    		});
    	});
    },
    
    createEditor : function(select){
    	return KindEditor.create(select, TT.kingEditorParams);
    },
    
    /**
     * 创建一个窗口，关闭窗口后销毁该窗口对象。<br/>
     * 
     * 默认：<br/>
     * width : 80% <br/>
     * height : 80% <br/>
     * title : (空字符串) <br/>
     * 
     * 参数：<br/>
     * width : <br/>
     * height : <br/>
     * title : <br/>
     * url : 必填参数 <br/>
     * onLoad : function 加载完窗口内容后执行<br/>
     * 
     * 
     */
    createWindow : function(params){
    	$("<div>").css({padding:"5px"}).window({
    		width : params.width?params.width:"80%",
    		height : params.height?params.height:"80%",
    		modal:true,
    		title : params.title?params.title:" ",
    		href : params.url,
		    onClose : function(){
		    	$(this).window("destroy");
		    },
		    onLoad : function(){
		    	if(params.onLoad){
		    		params.onLoad.call(this);
		    	}
		    }
    	}).window("open");
    },
    
    closeCurrentWindow : function(){
    	$(".panel-tool-close").click();
    },
    
    changeItemParam : function(node,formId){
    	$.ajax({
			type : "GET",
			url : "/rest/item/param/" + node.id,
			statusCode : {
				200 : function(data) {
					 $("#"+formId+" .params").show();
					 var paramData = JSON.parse(data.paramData);
					 var html = "<ul>";
					 for(var i in paramData){
						 var pd = paramData[i];
						 html+="<li><table>";
						 html+="<tr><td colspan=\"2\" class=\"group\">"+pd.group+"</td></tr>";
						 
						 for(var j in pd.params){
							 var ps = pd.params[j];
							 html+="<tr><td class=\"param\"><span>"+ps+"</span>: </td><td><input autocomplete=\"off\" type=\"text\"/></td></tr>";
						 }
						 
						 html+="</li></table>";
					 }
					 html+= "</ul>";
					 $("#"+formId+" .params td").eq(1).html(html);
				},
				404 : function() {
					 $("#"+formId+" .params").hide();
					 $("#"+formId+" .params td").eq(1).empty();
				},
				500 : function() {
					alert("error");
				}
			}
		});
    },
    getSelectionsIds : function (select){
    	var list = $(select);
    	var sels = list.datagrid("getSelections");
    	var ids = [];
    	for(var i in sels){
    		ids.push(sels[i].id);
    	}
    	ids = ids.join(",");
    	return ids;
    },
    
    /**
     * 初始化单图片上传组件 <br/>
     * 选择器为：.onePicUpload <br/>
     * 上传完成后会设置input内容以及在input后面追加<img> 
     */
    initOnePicUpload : function(){
    	$(".onePicUpload").click(function(){
			var _self = $(this);
			KindEditor.editor(TT.kingEditorParams).loadPlugin('image', function() {
				this.plugin.imageDialog({
					showRemote : false,
					clickFn : function(url, title, width, height, border, align) {
						var input = _self.siblings("input");
						input.parent().find("img").remove();
						input.val(url);
						input.after("<a href='"+url+"' target='_blank'><img src='"+url+"' width='80' height='50'/></a>");
						this.hideDialog();
					}
				});
			});
		});
    }
};
