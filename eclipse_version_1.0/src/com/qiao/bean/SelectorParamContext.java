package com.qiao.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 图片基础数据选项
 * @author qiao
 * 2015-3-20
 */
public class SelectorParamContext implements Serializable{
	
	protected int maxCount;//最大选图数量
	protected boolean hasQulityMenu;//是否有图片清晰度选项
	protected boolean isHighQulity;//是否高清 
	protected boolean isMult;//是否多选
	protected ArrayList<String> selectedFile; //选中图片path
	
	/**
	 * 通用常量
	 */
	public static final String TAG_SELECTOR = "SelectorParamContext";
	private static final long serialVersionUID = 1L;
	public static final int MAXCOUNT = 9;
	public static final int mcolor = 0xff0072c6;
	public static final int gcolor = 0xffcccccc;
	public static final String []menuItems = new String[] {"标清","原图"};

	public SelectorParamContext() {
		maxCount = MAXCOUNT;
		isMult = true;
		hasQulityMenu = true;
		isHighQulity = false;
		selectedFile = new ArrayList<String>();
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public boolean isMult() {
		return isMult;
	}

	public void setMult(boolean isMult) {
		this.isMult = isMult;
	}

	public boolean hasQulityMenu() {
		return hasQulityMenu;
	}

	public void setHasQulityMenu(boolean hasQulityMenu) {
		this.hasQulityMenu = hasQulityMenu;
	}

	public boolean isHighQulity() {
		return isHighQulity;
	}

	public void setHighQulity(boolean isHighQulity) {
		this.isHighQulity = isHighQulity;
	}
	
	public ArrayList<String> getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(ArrayList<String> selectedFile) {
		this.selectedFile = selectedFile;
	}
	
	public String getPercent(){
		return selectedFile.size()+"/"+getMaxCount();
	}
	
	public String getQuality(){
		return menuItems[isHighQulity?1:0];
	}
	
	public boolean isAvaliable(){
		return selectedFile.size()<maxCount;
	}
	
	public boolean isChecked(String path){
		return selectedFile.contains(path);
	}
	
	public void addItem(String path){
		selectedFile.add(path);
	}
	
	public void removeItem(String path){
		selectedFile.remove(path);
	}
}
