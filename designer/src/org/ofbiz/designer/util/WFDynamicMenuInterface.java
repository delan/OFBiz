package org.ofbiz.designer.util;


public interface WFDynamicMenuInterface  {
  public void updateDynamicMenu(String menuName, String[] menuItems, String[] menuItemCommands);
  public void setDynamicMenuName(String menuName);
}

