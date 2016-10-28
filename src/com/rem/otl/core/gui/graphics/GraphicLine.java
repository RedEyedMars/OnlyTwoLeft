package com.rem.otl.core.gui.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.List;

import com.rem.otl.core.main.Hub;

public class GraphicLine extends GraphicEntity{

	public static final int SEGMENT_COUNT = 10;
	public GraphicLine(int layer) {
		super("squares", layer);
		this.entity = Hub.creator.createGraphicLine("blank", this);
		this.entity.setLayer(layer);
	}
}
