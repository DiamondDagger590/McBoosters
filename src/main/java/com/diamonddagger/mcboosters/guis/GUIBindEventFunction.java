package com.diamonddagger.mcboosters.guis;

import java.util.ArrayList;

@FunctionalInterface
public interface GUIBindEventFunction {

  ArrayList<GUIEventBinder> bindEvents(GUIBuilder guiBuilder);
}
