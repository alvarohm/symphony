package eu.compassresearch.ide.cml.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.overture.ide.ui.VdmUIPlugin;
//import org.eclipse.ui.console.IConsoleConstants;

public class CmlPerspectiveFactory implements IPerspectiveFactory {

		public void createInitialLayout(IPageLayout layout) {

			String editorArea = layout.getEditorArea();

			IFolderLayout outputfolder= layout.createFolder("bottom", IPageLayout.BOTTOM, (float)0.75, editorArea); //$NON-NLS-1$
			if(VdmUIPlugin.DEBUG)
				outputfolder.addView("org.eclipse.pde.runtime.LogView");
			
			outputfolder.addView(IPageLayout.ID_PROBLEM_VIEW);
			outputfolder.addView(IPageLayout.ID_TASK_LIST);
//			outputfolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
			
			layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, (float)0.75, editorArea);

		}

		
		
	}
