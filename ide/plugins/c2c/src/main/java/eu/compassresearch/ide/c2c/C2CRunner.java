package eu.compassresearch.ide.c2c;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;

import eu.compassresearch.core.analysis.c2c.CircusList;
import eu.compassresearch.core.analysis.c2c.ICircusList;
import eu.compassresearch.core.analysis.c2c.utility.C2CPluginUtil;
import eu.compassresearch.ide.core.resources.ICmlModel;
import eu.compassresearch.ide.core.resources.ICmlProject;
import eu.compassresearch.ide.ui.utility.CmlProjectUtil;

public class C2CRunner {
	private IWorkbenchWindow window;
	private IWorkbenchSite site;
	private ICmlProject cmlProj;
	
	public void runC2C(){
		
		if (!CmlProjectUtil.typeCheck(this.window.getShell(), cmlProj)) {
			C2CPluginUtils.popErrorMessage(window, "Errors in model.");
		}
		
		final ICmlModel model = cmlProj.getModel();
		
		//List<INode> ast = model.getAst();
	
		//Class<?> c = ast.getClass();
		
		//C2CPluginUtils.popErrorMessage(window, "Error: " + System.getProperty("java.class.path"));
		
	
		ICircusList circList = genCircus(model);	
		
		if(!circList.isEmpty()){
			C2CPluginUtils.popInformationMessage(window, "About to create Circus file.");
			createCircusFile(circList);
		}
		
		C2CPluginUtils.popInformationMessage(window, "Plugin execution completed.");
	}
	
	public ICircusList genCircus(ICmlModel model){
		ICircusList circList = new CircusList();
		
		try{
			circList = C2CPluginUtil.generateCircus(model.getAst());
			return circList;
		} catch (Exception e){
			C2CPluginUtils.popErrorMessage(window, "Internal C2C error.");
			e.printStackTrace();
			return circList;
		}
	}
	
	public File createCircusFile(ICircusList cList){
		C2CPluginUtils.popInformationMessage(window, "Creating file...");
		//String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		String projectPath = C2CPluginUtils.getCurrentlySelectedProject().getLocation().toString();
		
		IProject project = C2CPluginUtils.getCurrentlySelectedProject();
		IFolder folder = project.getFolder("gen");
		if (!folder.exists()) {
			try {
				folder.create(false, true, null);
			} catch (CoreException e1) {
				C2CPluginUtils.popErrorMessage(window,"Error: Folder \"gen\" could not be created.");
			}
		}
		File circus = new File(projectPath.concat("/gen/"+project.getName()+".tex"));
		
		if (!circus.exists()) {
			try {
				circus.createNewFile();
			} catch (IOException e) {
				C2CPluginUtils.popErrorMessage(window,"Error: The Circus file could not be created.");
				e.printStackTrace();
			}
		}
		
		if(!circus.canWrite()){
			C2CPluginUtils.popErrorMessage(window,"Error: The Circus file cannot be written to.");
			circus.setWritable(true);	

		} else {
			try{
				FileWriter creator = new FileWriter(circus);
				creator.write("\\begin{circus} \n");
				int i = 0;
				while(i < cList.size()){
					creator.write(cList.get(i));
					creator.write("\n");
					i++;
				}
				creator.write("\\end{circus}\n");
				creator.close();
			} catch(IOException e){
				C2CPluginUtils.popErrorMessage(window, "Unable to beign Circus File.");
			}
			
			
		}
		C2CPluginUtils.popInformationMessage(window, "File completed.");
		
		return circus;
	}
	
	public C2CRunner(IWorkbenchWindow window, IWorkbenchSite site,
			ICmlProject cmlproj) {
		this.cmlProj = cmlproj;
		this.window = window;
		this.site = window.getActivePage().getActivePart().getSite();
	}

}
