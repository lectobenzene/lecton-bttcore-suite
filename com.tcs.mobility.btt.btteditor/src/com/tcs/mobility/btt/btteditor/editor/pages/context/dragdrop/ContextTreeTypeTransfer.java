package com.tcs.mobility.btt.btteditor.editor.pages.context.dragdrop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

public class ContextTreeTypeTransfer extends ByteArrayTransfer {

	private static ContextTreeTypeTransfer _instance = new ContextTreeTypeTransfer();
	
	private ContextTreeTypeTransfer() {}
	
	public static ContextTreeTypeTransfer getInstance () {
		return _instance;
	}
	
	/* The data flavor must be MIME type-like */
	static final String MIME_TYPE = "custom/myType";

	final int MIME_TYPE_ID = registerType(MIME_TYPE);

	@Override
	protected int[] getTypeIds() {
		return new int[] { MIME_TYPE_ID };
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { MIME_TYPE };
	}

	@Override
	public void javaToNative(Object object, TransferData transferData) {
		if (!checkMyType(object) || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		ContextTreeType myType = (ContextTreeType) object;
		byte[] bytes = convertToByteArray(myType);
		if (bytes != null) {
			super.javaToNative(bytes, transferData);
		}
	}

	@Override
	public Object nativeToJava(TransferData transferData) {
		if (!isSupportedType(transferData))
			return null;
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		return bytes == null ? null : restoreFromByteArray(bytes);
	}

	boolean checkMyType(Object object) {
		if (object == null)
			return false;
		ContextTreeType myType = (ContextTreeType) object;
		System.out.println("CHECK : value is "+myType.getValue());
		return myType != null && myType.getId() != null && myType.getId().length() > 0 && myType.getType() != null
				&& myType.getType().length() > 0 && myType.getValue() != null;
	}

	@Override
	protected boolean validate(Object object) {
		return checkMyType(object);
	}

	/* shared methods for converting instances of MyType <-> byte[] */

	static byte[] convertToByteArray(ContextTreeType type) {
		DataOutputStream dataOutStream = null;
		try {
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			dataOutStream = new DataOutputStream(byteOutStream);
			
			byte[] bytesId = type.getId().getBytes();
			dataOutStream.writeInt(bytesId.length);
			dataOutStream.write(bytesId);

			byte[] bytesType = type.getType().getBytes();
			dataOutStream.writeInt(bytesType.length);
			dataOutStream.write(bytesType);
			
			byte[] bytesValue = type.getValue().getBytes();
			dataOutStream.writeInt(bytesValue.length);
			dataOutStream.write(bytesValue);

			return byteOutStream.toByteArray();
		} catch (IOException e) {
			return null;
		} finally {
			if (dataOutStream != null) {
				try {
					dataOutStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	static ContextTreeType restoreFromByteArray(byte[] bytes) {
		DataInputStream dataInStream = null;
		try {
			ByteArrayInputStream byteInStream = new ByteArrayInputStream(bytes);
			dataInStream = new DataInputStream(byteInStream);
			
			int size = dataInStream.readInt();
			byte[] id = new byte[size];
			dataInStream.read(id);

			size = dataInStream.readInt();
			byte[] type = new byte[size];
			dataInStream.read(type);

			size = dataInStream.readInt();
			byte[] value = new byte[size];
			dataInStream.read(value);
			
			ContextTreeType result = new ContextTreeType(new String(id), new String(type));
			result.setValue(new String(value));
			
			return result;
		} catch (IOException ex) {
			return null;
		} finally {
			if (dataInStream != null) {
				try {
					dataInStream.close();
				} catch (IOException e) {
				}
			}
		}
	}
}