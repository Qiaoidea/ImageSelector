# ImageSelector
 本地选图控件,支持单选、多选和预览。注：实例中提供的图片质量（这里只提供选项，并没有实际压缩操作）

<p>
   <img src="https://raw.githubusercontent.com/Qiaoidea/ImageSelector/master/screenShot/device-1.png" width="480" alt="Screenshot"/>
   &nbsp;&nbsp;
   <img src="https://raw.githubusercontent.com/Qiaoidea/ImageSelector/master/screenShot/device-2.png" width="480" alt="Screenshot"/>
   &nbsp;&nbsp;
   <img src="https://raw.githubusercontent.com/Qiaoidea/ImageSelector/master/screenShot/device-3.png" width="480" alt="Screenshot"/>
</p>

![选图界面](http://upload-images.jianshu.io/upload_images/125949-de049fa9435b3723.png)

![相册列表](http://upload-images.jianshu.io/upload_images/125949-7aa736a06afadcac.png)

![选择图片质量](http://upload-images.jianshu.io/upload_images/125949-590c9e9c147995b5.png)

# 如何使用
- ##导入
1. 可以单独将此项目作为库（isLibrary）或者直接将java src和res文件拷贝至项目中,在 AndroidManifest.xml 中配置

``
        <activity android:name="com.qiao.activity.ImageSelectorActivty"></activity>
        <activity android:name="com.qiao.activity.ImageBrowserActivity"></activity>
``

- ##使用
2. 使用指向ImageSelectorActivity的Intent ,传入参数为 SelectorParamContext

``
	public class SelectorParamContext implements Serializable{
	
		protected int maxCount;//最大选图数量
		protected boolean hasQulityMenu;//是否有图片清晰度选项
		protected boolean isHighQulity;//是否高清 
		protected boolean isMult;//是否多选
		protected ArrayList<String> selectedFile; //选中图片path
``

3. 示例：

``
Intent intent = new Intent(YourActivity , ImageSelectorActivity.class);
SelectorParamContext params = new SelectorParamContext();
params.setMult(true);
params.setMaxCount(9);
params.setHasQulityMenu(true);
startActivityForResult(intent, requestCode);

//返回对象也为 SelectorParamContext
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			SelectorParamContext params = (SelectorParamContext)data.getSerializableExtra(SelectorParamContext.TAG_SELECTOR);
		//你的处理逻辑
		}
	}
``
