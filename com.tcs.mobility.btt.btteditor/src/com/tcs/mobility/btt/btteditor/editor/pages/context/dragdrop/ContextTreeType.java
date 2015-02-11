package com.tcs.mobility.btt.btteditor.editor.pages.context.dragdrop;

public class ContextTreeType {

	public static final String TYPE_FIELD_MODEL = "FIELDMODEL";
	public static final String TYPE_KEYED_COLLECTION = "KEYEDCOLLECTION";
	public static final String TYPE_REFDATA = "REFDATA";
	public static final String TYPE_INDEXED_COLLECTION = "INDEXEDCOLLECTION";
	public static final String DEFAULT = "DEFAULT_VALUE_ENCOUNTERED";
	
	private String id;
	private String type;
	private String value;

	public ContextTreeType(String id, String type) {
		this.id = id;
		this.type = type;
		this.value = DEFAULT;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
