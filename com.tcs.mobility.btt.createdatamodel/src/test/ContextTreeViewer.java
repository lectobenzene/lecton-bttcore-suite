package test;


import java.io.File;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.tcs.mobility.btt.core.source.parsers.context.DataModelParser;

public class ContextTreeViewer {

	public ContextTreeViewer() {
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Tree Viewer Example");
		shell.setBounds(100, 100, 200, 200);
		shell.setLayout(new FillLayout());
		final TreeViewer treeViewer = new TreeViewer(shell, SWT.SINGLE);
		final DelegatingStyledCellLabelProvider styledCellLP1= new DelegatingStyledCellLabelProvider(new ContextParseLabelProvider(display));

		treeViewer.setLabelProvider(styledCellLP1);
		treeViewer.setContentProvider(new ContextContentProvider());
		
		DataModelParser parser = new DataModelParser();
		File file = new File("temp/response.txt");
		String xmlContent = parser.readFromFile(file);
		
		treeViewer.setInput(parser.getRootNodeFromFile(xmlContent).getChildren().toArray());
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
