package com.kau.jonathan.umdschedulesharer.Models;

import java.util.HashMap;
import java.util.LinkedList;

public class ClassDataHolder implements Comparable<ClassDataHolder>{
	String name;
	String section;
	LinkedList<FriendSectionData> friends;
	
	public ClassDataHolder(String name, String section, LinkedList<FriendSectionData> friends) {
		this.name = name;
		this.friends = friends;
		this.section = section;
	}

	@Override
	public int compareTo(ClassDataHolder another) {
		// TODO Auto-generated method stub
		return name.compareTo(another.getName());
	}

	public String getName() {
		return name;
	}

	public void LinkedListName(String name) {
		this.name = name;
	}

	public LinkedList<FriendSectionData> getFriends() {
		return friends;
	}

	public void LinkedListFriends(LinkedList<FriendSectionData> friends) {
		this.friends = friends;
	}
	
	public static class FriendSectionData {
		String name;
		String facebookId;
		String section;
		public String getName() {
			return name;
		}
		public void LinkedListName(String name) {
			this.name = name;
		}
		public String getFacebookId() {
			return facebookId;
		}
		public void LinkedListFacebookId(String facebookId) {
			this.facebookId = facebookId;
		}
		public String getSection() {
			return section;
		}
		public void LinkedListSection(String section) {
			this.section = section;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setFacebookId(String facebookId) {
			this.facebookId = facebookId;
		}
		public void setSection(String section) {
			this.section = section;
		}
	}

	public String getSection() {
		return section;
	}

	public void LinkedListSection(String section) {
		this.section = section;
	}

}
