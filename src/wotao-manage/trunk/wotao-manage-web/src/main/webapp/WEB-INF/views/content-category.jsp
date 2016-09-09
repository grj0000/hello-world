<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div>
	 <ul id="contentCategory" class="easyui-tree">
    </ul>
</div>
<div id="contentCategoryMenu" class="easyui-menu" style="width:120px;" data-options="onClick:menuHandler">
    <div data-options="iconCls:'icon-add',name:'add'">添加</div>
    <div data-options="iconCls:'icon-remove',name:'rename'">重命名</div>
    <div class="menu-sep"></div>
    <div data-options="iconCls:'icon-remove',name:'delete'">删除</div>
</div>
<script type="text/javascript">
$(function(){
	$("#contentCategory").tree({
		url : '/rest/content/category',
		animate: true,
		method : "GET",
		onContextMenu: function(e,node){
            e.preventDefault();//右键执行onContextMenu事件，此句时屏蔽浏览器默认菜单的意思，使得右键点击内容分类时显示添加删除和重命名，而不是浏览器的右键菜单，返回，重新加载，打印……
            $(this).tree('select',node.target);//选中当前节点
            $('#contentCategoryMenu').menu('show',{
                left: e.pageX,
                top: e.pageY
            });//e为点击事件，就是点右键时鼠标光标的位置
        },
        onAfterEdit : function(node){
        	var _tree = $(this);//在右键菜单处理完后执行此 onAfterEdit事件指定的方法
        	if(node.id == 0){
        		// 新增节点
        		$.post("/rest/content/category",{parentId:node.parentId,name:node.text},function(data){
        			_tree.tree("update",{
        				target : node.target,
        				id : data.id
        			});//数据库新增节点成功以后，就是$.post成功以后更新，update方法包括的参数'param'参数包含以下属性：
        			//target(DOM对象，将被更新的目标节点)，id，text，iconCls，checked等
        		});
        	}else{
        		$.ajax({
        			   type: "PUT",
        			   url: "/rest/content/category",
        			   data: {id:node.id,name:node.text},
        			   success: function(msg){
        				   //$.messager.alert('提示','新增商品成功!');
        			   },
        			   error: function(){
        				   $.messager.alert('提示','重命名失败!');
        			   }
        			});
        	}
        }
	});
});
function menuHandler(item){
	var tree = $("#contentCategory");
	var node = tree.tree("getSelected");
	if(item.name === "add"){
		tree.tree('append', {
            parent: (node?node.target:null),
            data: [{
                text: '新建分类',
                id : 0,
                parentId : node.id
            }]//parent是父节点属性名，值根据选中的节点定，data是节点的属性
        }); 
		var _node = tree.tree('find',0);//找到新增的节点
		tree.tree("select",_node.target).tree('beginEdit',_node.target);//选中新增的节点，并对其进行编辑
	}else if(item.name === "rename"){
		tree.tree('beginEdit',node.target);
	}else if(item.name === "delete"){
		$.messager.confirm('确认','确定删除名为 '+node.text+' 的分类吗？',function(r){
			if(r){
				$.ajax({
     			   type: "POST",
     			   url: "/rest/content/category",
     			   data : {parentId:node.parentId,id:node.id,"_method":"DELETE"},
     			   success: function(msg){
     				   //$.messager.alert('提示','新增商品成功!');
     				  tree.tree("remove",node.target);
     			   },
     			   error: function(){
     				   $.messager.alert('提示','删除失败!');
     			   }
     			});
			}
		});
	}
}
</script>