# ImageSelector
 本地选图控件,支持单选、多选和预览。注：实例中提供的图片质量（这里只提供选项，并没有实际压缩操作）
 
 ``v2.0`` **android studio**
- 1.修改AsyncTask查询图库为 CurSorLoader;
- 2.更改ImageView显示方式，支持缩放、旋转；
- 3.使用AppCompact、RecyclerView替换GridView；
- 4.变更大图打开方式，添加过渡动画；

 ![android studio](https://raw.githubusercontent.com/Qiaoidea/ImageSelector/master/screenShot/ImageDemo.gif)
 
 ``1.0``

<p>
   <img src="https://raw.githubusercontent.com/Qiaoidea/ImageSelector/master/screenShot/device-1.png" width="350" alt="Screenshot"/>
     
   <img src="https://raw.githubusercontent.com/Qiaoidea/ImageSelector/master/screenShot/device-2.png" width="350" alt="Screenshot"/>
     
   <img src="https://raw.githubusercontent.com/Qiaoidea/ImageSelector/master/screenShot/device-3.png" width="350" alt="Screenshot"/>
</p>

# 如何使用
## 一、使用Activity方式
### 导入
1. 可以单独将此项目作为库（isLibrary）或者直接将java src和res文件拷贝至项目中,在 AndroidManifest.xml 中配置

```
        <activity android:name="com.qiao.activity.ImageSelectorActivty"></activity>
        <activity android:name="com.qiao.activity.ImageBrowserActivity"></activity>
```

### 使用
2. 使用指向ImageSelectorActivity的Intent ,传入参数为 SelectorParamContext

```
	public class SelectorParamContext implements Serializable{
	
		protected int maxCount;//最大选图数量
		protected boolean hasQulityMenu;//是否有图片清晰度选项
		protected boolean isHighQulity;//是否高清 
		protected boolean isMult;//是否多选
		protected ArrayList<String> selectedFile; //选中图片path
```

## 二、使用Fragment方式
1. 在你的页面添加Fragment,详情参见DemoActivity。用法同上。只是不用再AndroidManifest.xml 中配置


## 示例：

```
/**
 *选图时调用
 **/
 public void startImageSelector(){
	Intent intent = new Intent(YourActivity , ImageSelectorActivity.class);
	SelectorParamContext params = new SelectorParamContext();
	params.setMult(true);
	params.setMaxCount(9);
	params.setHasQulityMenu(true);
	startActivityForResult(intent, requestCode);
}

/**
 *返回对象也为 SelectorParamContext
 **/
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	if (resultCode == Activity.RESULT_OK) {
		SelectorParamContext params = (SelectorParamContext)data.getSerializableExtra(SelectorParamContext.TAG_SELECTOR);
		//你的处理逻辑
	}
}
```
