package test;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.FieldModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.IndexedCollectionModel;
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.KeyedCollectionModel;

public class ContextParseLabelProvider extends LabelProvider implements IStyledLabelProvider{

	private Display display;
	private Image image;

	private Color colorDataType;
	
	public ContextParseLabelProvider(Display display) {
		this.display = display;
		colorDataType = new Color(display, new RGB(255, 0, 0));
	}

	@Override
	public Image getImage(Object element) {

		if (element instanceof KeyedCollectionModel) {
			image = new Image(display, "icons/kcoll.png");
		} else if (element instanceof IndexedCollectionModel) {
			image = new Image(display, "icons/icoll.png");
		} else if (element instanceof FieldModel) {
			image = new Image(display, "icons/field.png");
		} else {
			image = super.getImage(element);
		}
		return image;
	}

	@Override
	public void dispose() {
		image.dispose();
		image = null;
		colorDataType.dispose();
		colorDataType = null;
		super.dispose();
	}

	@Override
	public String getText(Object element) {
		return getStyledText(element).toString();
	}

	@Override
	public StyledString getStyledText(Object element) {
		String normalText = "ROOT";
		String dataType = null;
		if (element instanceof KeyedCollectionModel) {
			normalText =  ((KeyedCollectionModel) element).getId();
		} else if (element instanceof IndexedCollectionModel) {
			normalText =  ((IndexedCollectionModel) element).getId();
		} else if (element instanceof FieldModel) {
			normalText =  ((FieldModel) element).getId() + " ";
			dataType = ((FieldModel) element).getDataType();
			System.out.println("IS DataType null ? "+(dataType==null));
		}
		Styler styler = new Styler() {
			
			@Override
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = colorDataType;
			}
		};
		StyledString styledString = new StyledString(normalText);
		if(dataType !=  null){
			styledString.append("("+dataType+")", styler);
		}
		System.out.println(styledString);
		return styledString;
	}
}
