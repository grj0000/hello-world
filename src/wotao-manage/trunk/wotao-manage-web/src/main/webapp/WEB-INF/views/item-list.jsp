<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<table class="easyui-datagrid" id="itemList" title="商品列表" 
       data-options="singleSelect:false,collapsible:true,pagination:true,url:'/rest/item',method:'get',pageSize:30,toolbar:toolbar">
    <thead>
        <tr>
        	<th data-options="field:'ck',checkbox:true"></th>
        	<th data-options="field:'id',width:60">商品ID</th>
            <th data-options="field:'title',width:200">商品标题</th>
            <th data-options="field:'cid',width:100">叶子类目</th>
            <th data-options="field:'sellPoint',width:100">卖点</th>
            <th data-options="field:'price',width:70,align:'right',formatter:TAOTAO.formatPrice">价格</th>
            <th data-options="field:'num',width:70,align:'right'">库存数量</th>
            <th data-options="field:'barcode',width:100">条形码</th>
            <th data-options="field:'status',width:60,align:'center',formatter:TAOTAO.formatItemStatus">状态</th>
            <th data-options="field:'created',width:130,align:'center',formatter:TAOTAO.formatDateTime">创建日期</th>
            <th data-options="field:'updated',width:130,align:'center',formatter:TAOTAO.formatDateTime">更新日期</th>
        </tr>
    </thead>
</table>
<div id="itemEditWindow" class="easyui-window" title="编辑商品" data-options="modal:true,closed:true,iconCls:'icon-save',href:'/rest/page/item-edit'" style="width:80%;height:80%;padding:10px;">
</div>
<script>

    function getSelectionsIds(){//用于将所有选中行的id用逗号拼接成一个字符串
    	var itemList = $("#itemList");//id="itemList"的表单是显示所有商品的表单
    	var sels = itemList.datagrid("getSelections");//所有商品列表中勾选中的行
    	var ids = [];
    	for(var i in sels){//i是下标值，遍历每一个选中的行
    		ids.push(sels[i].id);//把选中行的id压入数组，<th data-options="field:'id',width:60">商品ID</th>
    	}
    	ids = ids.join(",");//把数组中各元素用逗号拼接成一个字符串
    	//alert(JSON.stringify(ids));  //如果只有一个id为50的元素时，拼接的结果是“50”，没有逗号，如果有两个元素，则形式为“50,49”
    	//alert(ids.indexOf(','));  //索引以0开始，“50,49”中逗号所在索引为2
    	return ids;//返回该字符串
    }
    
    var toolbar = [{
        text:'新增',
        iconCls:'icon-add',
        handler:function(){
        	$(".tree-title:contains('新增商品')").parent().click();
        }
    },{
        text:'编辑',
        iconCls:'icon-edit',
        handler:function(){//点击编辑按钮后判断商品列表中选中的行数是不是一行
        	var ids = getSelectionsIds();
        	if(ids.length == 0){
        		$.messager.alert('提示','必须选择一个商品才能编辑!');
        		return ;
        	}
        	if(ids.indexOf(',') > 0){
        		$.messager.alert('提示','只能选择一个商品!');
        		return ;
        	}
        	alert("打开编辑窗口");
        	$("#itemEditWindow").window({
        		onLoad :function(){//后面.window("open");以后会加载item-edit.jsp页面，页面加载onLoad完以后执行该方法
        			//回显数据
        			alert("回显数据");
        			var data = $("#itemList").datagrid("getSelections")[0];
        			data.priceView = TAOTAO.formatPrice(data.price);//给data添加一个priceView属性，并把data.price的值格式化以后赋给priceView
        			$("#itemeEditForm").form("load",data);//把选中的行的数据data加载（"load"）到item-edit.jsp页面的表单中
        			
        			// 加载商品描述
        			$.getJSON('/rest/item/desc/'+data.id,function(_data){//加载完'/rest/item/desc/'+data.id页面的数据以后，执行function
        				itemEditEditor.html(_data.itemDesc);//把'/rest/item/desc/'+data.id页面返回的数据写入到富文本编辑器中
        			});
        			
        			//加载商品规格
        	    	$.ajax({
        				type : "GET",
        				url : "/rest/item/param/item/" +data.id,
        				statusCode : {
        					200 : function(_data) {
            					$("#itemeEditForm .params").show();
            					$("#itemeEditForm [name=itemParams]").val(_data.paramData);
            					$("#itemeEditForm [name=itemParamId]").val(_data.id);
            					
            					//回显商品规格
            					 var paramData = JSON.parse(_data.paramData);
            					
            					 var html = "<ul>";
            					 for(var i in paramData){
            						 var pd = paramData[i];
            						 html+="<li><table>";
            						 html+="<tr><td colspan=\"2\" class=\"group\">"+pd.group+"</td></tr>";
            						 
            						 for(var j in pd.params){
            							 var ps = pd.params[j];
            							 html+="<tr><td class=\"param\"><span>"+ps.k+"</span>: </td><td><input autocomplete=\"off\" type=\"text\" value='"+ps.v+"'/></td></tr>";
            						 }
            						 
            						 html+="</li></table>";
            					 }
            					 html+= "</ul>";
            					 $("#itemeEditForm .params td").eq(1).html(html);
        					},
        					404 : function() {
        					},
        					500 : function() {
        						alert("内部服务器错误");
        					}
        				}
        			});
        			
        			alert("TAOTAO.init开始");
        			TAOTAO.init({
        				"pics" : data.image,
        				"cid" : data.cid,
        				"title":data.title,
        				fun:function(node){
        					alert("fun:function(node){");
        					TAOTAO.changeItemParam(node, "itemeEditForm");
        				}
        			});
        		}
        	}).window("open");
        }
    },{
        text:'删除',
        iconCls:'icon-cancel',
        handler:function(){
        	var ids = getSelectionsIds();
        	if(ids.length == 0){
        		$.messager.alert('提示','未选中商品!');
        		return ;
        	}
        	$.messager.confirm('确认','确定删除ID为 '+ids+' 的商品吗？',function(r){
        	    if (r){
        	    	var params = {"ids":ids};
                	$.post("/rest/item/delete",params, function(data){
            			if(data.status == 200){
            				$.messager.alert('提示','删除商品成功!',undefined,function(){
            					$("#itemList").datagrid("reload");
            				});
            			}
            		});
        	    }
        	});
        }
    },'-',{
        text:'下架',
        iconCls:'icon-remove',
        handler:function(){
        	var ids = getSelectionsIds();
        	if(ids.length == 0){
        		$.messager.alert('提示','未选中商品!');
        		return ;
        	}
        	$.messager.confirm('确认','确定下架ID为 '+ids+' 的商品吗？',function(r){
        	    if (r){
        	    	var params = {"ids":ids};
                	$.post("/rest/item/instock",params, function(data){
            			if(data.status == 200){
            				$.messager.alert('提示','下架商品成功!',undefined,function(){
            					$("#itemList").datagrid("reload");
            				});
            			}
            		});
        	    }
        	});
        }
    },{
        text:'上架',
        iconCls:'icon-remove',
        handler:function(){
        	var ids = getSelectionsIds();
        	if(ids.length == 0){
        		$.messager.alert('提示','未选中商品!');
        		return ;
        	}
        	$.messager.confirm('确认','确定上架ID为 '+ids+' 的商品吗？',function(r){
        	    if (r){
        	    	var params = {"ids":ids};
                	$.post("/rest/item/reshelf",params, function(data){
            			if(data.status == 200){
            				$.messager.alert('提示','上架商品成功!',undefined,function(){
            					$("#itemList").datagrid("reload");
            				});
            			}
            		});
        	    }
        	});
        }
    }];
</script>