package com.mdj.moudle.userPackage;

public interface SelectPackageListItemListEventListener {
	public enum ButtonAction{
		ActionAdd,
		ActionSub,
	}
	public void OnEvent(int list1Position, int list2Position, ButtonAction buttonAction);
}

