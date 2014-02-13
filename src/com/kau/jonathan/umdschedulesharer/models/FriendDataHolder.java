package com.kau.jonathan.umdschedulesharer.models;

import java.util.Iterator;
import java.util.Set;

import android.widget.ImageView;
import android.widget.TextView;

public class FriendDataHolder implements Comparable<FriendDataHolder> {
	String name;
	long facebookID;
	boolean allowShare;
	Set<String> sharedClasses;
	String classesText;


	public FriendDataHolder(String name, long facebookID, boolean allowShare) {
		this.name = name;
		this.facebookID = facebookID;
		this.allowShare = allowShare;
	}

	@Override
	public int compareTo(FriendDataHolder o) {
		int returnValue = 0;
		if(o != null){
			returnValue = this.name.compareTo(o.name);
		}
		return returnValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getFacebookID() {
		return facebookID;
	}

	public void setFacebookID(long facebookID) {
		this.facebookID = facebookID;
	}

	public boolean isAllowShare() {
		return allowShare;
	}

	public void setAllowShare(boolean allowShare) {
		this.allowShare = allowShare;
	}
	
	public Set<String> getClasses() {
		return sharedClasses;
	}
	
	public void setClasses(Set<String> classes) {
		this.sharedClasses = classes;
		
		Iterator<String> iterator = sharedClasses.iterator();
		classesText = "";

		while(iterator.hasNext()) {
			String s = iterator.next();

			classesText = classesText + s;
			if(iterator.hasNext()) classesText = classesText  + ", ";
		}
	}

	public String getClassesText() {
		return classesText;
	}

	public void setClassesText(String classesText) {
		this.classesText = classesText;
	}
}
