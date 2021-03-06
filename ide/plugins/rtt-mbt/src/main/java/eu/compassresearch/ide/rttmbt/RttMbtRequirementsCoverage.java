package eu.compassresearch.ide.rttmbt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.part.MultiPageEditorPart;

import eu.compassresearch.rttMbtTmsClientApi.RttMbtClient;

public class RttMbtRequirementsCoverage extends MultiPageEditorPart  {

	private enum Verdict {
		UNCOVERED,
		COVERED,
		NOTTESTED,
	    INCONCLUSIVE,
	    PASS,
	    FAIL,
	    TESTERROR,
	    UNDEFINED
	}

	private String Verdict2String(Verdict verdict) {
		String value = "";
		switch (verdict) {
		case UNCOVERED:
			value = "NOT COVERED";
			break;
		case COVERED:
			value = "COVERED";
			break;
		case NOTTESTED:
			value = "NOT TESTED";
			break;
		case INCONCLUSIVE:
			value = "INCONCLUSIVE";
			break;
		case PASS:
			value = "PASS";
			break;
		case FAIL:
			value = "FAIL";
			break;
		case TESTERROR:
			value = "TESTERROR";
			break;
		case UNDEFINED:				
			value = "UNDEFINED";
			break;
		}
		return value;
	}

	private RGB Verdict2RGB(Verdict verdict) {
		RGB value = null;
		switch (verdict) {
		case UNCOVERED:
			value = new RGB(255,255,0);
			break;
		case COVERED:
			value = new RGB(0,255,0);
			break;
		case NOTTESTED:
			value = new RGB(255,255,0);
			break;
		case INCONCLUSIVE:
			value = new RGB(255,255,0);
			break;
		case PASS:
			value = new RGB(0,255,0);
			break;
		case FAIL:
			value = new RGB(255,0,0);
			break;
		case TESTERROR:
			value = new RGB(255,0,0);
			break;
		case UNDEFINED:				
			value = new RGB(255,0,0);
			break;
		}
		return value;
	}

	// widget to display the data
	private Tree reqTreeView;
	private Tree tcTreeView;

	// maps for file content
	private SortedMap<String,List<String>> req2tc = new TreeMap<String,List<String>>();
	private SortedMap<String,List<String>> tc2req = new TreeMap<String,List<String>>();
	private SortedMap<String,String> testcases = new TreeMap<String,String>();
	private SortedMap<String,Verdict> tccov = new TreeMap<String,Verdict>();
	private SortedMap<String,Verdict> reqcov = new TreeMap<String,Verdict>();
	
	// a reference to the RttMbtClient for log messages
	protected RttMbtClient client = null;
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// get RTT-MBT TMS client
    	client = Activator.getClient();
    	client.setConsoleName("Coverage");
    	// check input
		if (! (input instanceof IFileEditorInput) ) {
			throw new PartInitException("Invalid input! Expected file input");
		} else {
			IFileEditorInput iFileInput = (IFileEditorInput) input;
			IFile ifile = iFileInput.getFile();

			// init client according to selected file
			String selectedObjectFilesystemPath = RttMbtClient.getAbsolutePathFromFileURI(ifile.getLocationURI());
			String selectedObjectWorkspacePath = ifile.getFullPath().toString();
			String selectedObjectWorkspaceProjectName = ifile.getProject().getName();
			String selectedObjectWorkspaceProjectPath = ifile.getProject().getFullPath().toString();
			client.initPathVariablesFromSelection(selectedObjectFilesystemPath,
					                              selectedObjectWorkspacePath,
					                              selectedObjectWorkspaceProjectName,
					                              selectedObjectWorkspaceProjectPath);
	    	IWorkspace workspace = ResourcesPlugin.getWorkspace();
			String workspacePath = workspace.getRoot().getLocation().toFile().getAbsolutePath();
			client.setWorkspacePath(workspacePath);
			client.setConsoleName(selectedObjectWorkspaceProjectName);

			// get the input stream
			InputStream istream;
			try {
				// create fileScanners for req2tc.csv and tc2req.csv
				istream = ifile.getContents();
				Scanner covScanner = new Scanner(istream);
				String modelpath = client.getRttProjectPath() + File.separator + "model" + File.separator;
				String req2tcpath = modelpath + "req2tc.csv";
				String tc2reqpath = modelpath + "tc2req.csv";
				String tcpath = modelpath + "testcases.csv";
				FileInputStream req2tcstream = new FileInputStream(req2tcpath);
				FileInputStream tc2reqstream = new FileInputStream(tc2reqpath);
				FileInputStream tcstream = new FileInputStream(tcpath);
				// file scanner initialised with input file
				Scanner req2tcScanner = new Scanner(req2tcstream);
				Scanner tc2reqScanner = new Scanner(tc2reqstream);
				Scanner tcScanner = new Scanner(tcstream);
				// read file content and create maps
				Scanner line;
				String key, value;
				List<String> list;
				// get mapping from requirements to test cases from req2tc.csv
				while (req2tcScanner.hasNextLine()) {
					line = new Scanner(req2tcScanner.nextLine());
					line.useDelimiter(";");
					if (line.hasNext()) {
						key = line.next();
						if (line.hasNext()) {
							value = line.next();
							if (!req2tc.containsKey(key)) {
								// create new entry
								list = new ArrayList<String>();
								list.add(value);
								req2tc.put(key, list);
							} else {
								// update entry
								list = req2tc.get(key);
								list.add(value);
								req2tc.put(key, list);
							}
						} else {
							client.addErrorMessage("[FAIL]: illegal file format for req2tc.csv");
						}
					} else {
						client.addErrorMessage("[FAIL]: illegal file format for req2tc.csv");
					}
				}
				// get mapping from test cases to requirements from tc2req.csv
				while (tc2reqScanner.hasNextLine()) {
					line = new Scanner(tc2reqScanner.nextLine());
					line.useDelimiter(";");
					if (line.hasNext()) {
						key = line.next();
						if (line.hasNext()) {
							value = line.next();
							if (!tc2req.containsKey(key)) {
								// create new entry
								list = new ArrayList<String>();
								list.add(value);
								tc2req.put(key, list);
							} else {
								// update entry
								list = tc2req.get(key);
								list.add(value);
								tc2req.put(key, list);
							}
						} else {
							client.addErrorMessage("[FAIL]: illegal file format for req2tc.csv");
						}
					} else {
						client.addErrorMessage("[FAIL]: illegal file format for req2tc.csv");
					}
				}
				// get list of all test cases from testcases.csv and initialize coverage with false;
				while (tcScanner.hasNextLine()) {
					line = new Scanner(tcScanner.nextLine());
					line.useDelimiter(";");
					if (line.hasNext()) {
						key = line.next();
						if (line.hasNext()) {
							value = line.next();
							testcases.put(key, value);
							tccov.put(key, Verdict.UNCOVERED);
						} else {
							client.addErrorMessage("[FAIL]: illegal file format for req2tc.csv");
						}
					} else {
						client.addErrorMessage("[FAIL]: illegal file format for req2tc.csv");
					}
				}
				// get test case coverage from covered_testcases.csv and update test case coverage
				while (covScanner.hasNextLine()) {
					Verdict verdict = Verdict.UNCOVERED;
					if (ifile.getName().compareTo("overall_coverage.csv") == 0) {
						key = covScanner.nextLine();
						verdict = Verdict.COVERED;
					} else if (ifile.getName().compareTo("covered_testcases.csv") == 0) {
						String raw = covScanner.nextLine();
						int start = raw.indexOf(';');
						int end = raw.lastIndexOf(';');
						if (start == -1 || end == -1) {
							key = raw;
							verdict = Verdict.COVERED;
						} else if (start == end) {
							key = raw.substring(start + 1);
							verdict = Verdict.COVERED;
						} else {
							key = raw.substring(start + 1, end);
							String covVerdict = raw.substring(end + 1);
							verdict = Verdict.INCONCLUSIVE;
							if (covVerdict.compareTo("PASS") == 0) {
								verdict = Verdict.PASS;
							}
							if (covVerdict.compareTo("FAIL") == 0) {
								verdict = Verdict.FAIL;
							}
						}
					} else {
						key = covScanner.nextLine();
						verdict = Verdict.COVERED;
					}
					if (!tccov.containsKey(key)) {
						client.addErrorMessage("[WARNING]: added coverage for unknown test case '" + key + "'");						
					}
					tccov.put(key, verdict);
				}
				// calculate requirements coverage
				Set<String> requirements = req2tc.keySet();
				Iterator<String> i = requirements.iterator();
				while (i.hasNext()) {
					String requirement = i.next();
					List<String> tcs = req2tc.get(requirement);
					Verdict verdict = Verdict.UNDEFINED;
					for (int idx = 0; idx < tcs.size(); idx++) {
						String testcase = tcs.get(idx);
						Verdict tcVerdict = tccov.get(testcase);
						switch(tcVerdict) {
						case NOTTESTED:
								verdict = Verdict.NOTTESTED;
							break;
						case PASS:
							if (verdict == Verdict.UNDEFINED) {
								verdict = Verdict.PASS;
							}
							break;
						case FAIL:
							if (verdict != Verdict.TESTERROR) {
								verdict = Verdict.FAIL;
							}
							break;
						case INCONCLUSIVE:
							if ((verdict != Verdict.FAIL) &&
								(verdict != Verdict.TESTERROR)) {
								verdict = Verdict.INCONCLUSIVE;
							}
							break;
						case TESTERROR:
							verdict = Verdict.TESTERROR;
							break;
						case COVERED:
							if (verdict == Verdict.UNDEFINED) {
								verdict = Verdict.COVERED;
							}
							break;
						case UNCOVERED:
							if ((verdict == Verdict.UNDEFINED) ||
								(verdict == Verdict.PASS) ||
								(verdict == Verdict.COVERED)) {
								verdict = Verdict.UNCOVERED;
							}
							break;
						default:
							verdict = Verdict.UNDEFINED;
							break;
						}
					}
					reqcov.put(requirement, verdict);
				}
				covScanner.close();
				req2tcScanner.close();
				tc2reqScanner.close();
				tcScanner.close();
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// @todo: add error here!
				client.addErrorMessage("[FAIL]: unable to find expected coverage file: " + e.getMessage() + "");
			}
		}
		setSite(site);
		setInput(input);
	}

	@Override
	protected void createPages() {
		createTc2ReqPage();
		createReq2TcPage();
	}

	private void addTestcase(SelectionEvent e) {
		Button button = (Button) e.getSource();
		String ltlformula = button.getData().toString();
		ElementListSelectionDialog selection =
				new ElementListSelectionDialog(getContainer().getShell(), new LabelProvider());
		File tprocRoot = new File(client.getRttProjectPath() + File.separator + client.getRttMbtTProcGenCtxFolderName());
		File[] files = tprocRoot.listFiles();
		List<File> subdirs = new ArrayList<File>();
		List<File> testprocs = new ArrayList<File>();
		int idx;
		if (files != null) {
			for (idx = 0; idx < files.length; idx++) {
				if (files[idx].isDirectory()) {
					subdirs.add(files[idx]);
				}
			}
		} else {
			client.addErrorMessage("unable to find test generation context directories!");
		}
		for (idx = 0; idx < subdirs.size(); idx++) {
			File conf = new File(subdirs.get(idx), "conf");
			if (conf.isDirectory()) {
				File configuration = new File(conf, "configuration.csv");
				if (configuration.isFile()) {
					testprocs.add(subdirs.get(idx));
				}
			}
		}
		String[] names = new String[testprocs.size()];
		for (idx = 0; idx < testprocs.size(); idx++) {
			names[idx] = testprocs.get(idx).getName();
		}
		selection.setElements(names);
		if (selection.open() == Window.OK) {
			String testproc = (String) selection.getFirstResult();
			// get additional goal file of selected test procedure
			File addgoals = null;
			for (idx = 0; idx < testprocs.size(); idx++) {
				if (testproc.compareTo(testprocs.get(idx).getName()) == 0) {
					addgoals = new File(testprocs.get(idx), File.separator + "conf" + File.separator + "addgoals.conf");
					// add test case to additional goals
					try {
						FileWriter addgoalStream = new FileWriter(addgoals.getAbsolutePath(), true);
						BufferedWriter append = new BufferedWriter(addgoalStream);
						append.write(ltlformula + "\n");
						append.close();
						addgoalStream.close();
						client.addLogMessage("added '" + ltlformula + "'");
						client.addLogMessage("to '" + addgoals.getAbsolutePath() + "'");
					} catch (IOException ex) {
						client.addErrorMessage("Unable to open '" + addgoals.getAbsolutePath() + "' for writing!");
					}
					idx = testprocs.size();
				}
			}
		} else {
			client.addErrorMessage("unable to open test generation context seleciton dialog");
		}
	}

	// testcase coverage
	private void createTc2ReqPage() {
		// create tree view
		tcTreeView = new Tree(getContainer(), SWT.H_SCROLL | SWT.V_SCROLL);
		tcTreeView.setHeaderVisible(true);
		int index = addPage(tcTreeView);
		setPageText(index,"Testcase Coverage");

		// header:
		String[] tc2reqHeader = {"Name", "Status", "Additional Goals"};
		for (int idx = 0; idx < tc2reqHeader.length; idx++) {
			TreeColumn column = new TreeColumn(tcTreeView, SWT.LEFT);
			column.setText(tc2reqHeader[idx]);
			column.setResizable(true);
			tcTreeView.getColumn(idx).pack();
		}

		String tcTag, reqTag;
		Set<String> tcSet = testcases.keySet();
		Iterator<String> tcIt = tcSet.iterator();
		while (tcIt.hasNext()) {
			tcTag = tcIt.next();
			TreeItem reqParent = null;
			// add test case
			reqParent = new TreeItem(tcTreeView, SWT.NONE);
			reqParent.setText(0, tcTag);
			reqParent.setText(1, Verdict2String(tccov.get(tcTag)));
			reqParent.setBackground(1, new Color(Display.getDefault(), Verdict2RGB(tccov.get(tcTag))));
			TreeEditor editor = new TreeEditor(tcTreeView);
			editor.horizontalAlignment = SWT.LEFT;
			editor.minimumWidth = 40;
			editor.minimumHeight = 12;
		    Button cellEditor = new Button(tcTreeView, SWT.PUSH);
		    cellEditor.setText("add");
		    cellEditor.setBackground(reqParent.getBackground());
		    cellEditor.setData(tcTag + ";" + testcases.get(tcTag) + ";");
		    cellEditor.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) { addTestcase(e); }

				@Override
				public void widgetDefaultSelected(SelectionEvent e) { addTestcase(e); }
				});
		    editor.setEditor(cellEditor, reqParent, 2);
			// add requirements
			List<String> requirements = tc2req.get(tcTag);
			for (int idx = 0; ((requirements != null) && (idx < requirements.size())); idx++) {
				reqTag = requirements.get(idx);
				TreeItem req = new TreeItem(reqParent, SWT.NONE);
				req.setText(0, reqTag);
				req.setText(1, Verdict2String(reqcov.get(reqTag)));
				req.setBackground(1, new Color(Display.getDefault(), Verdict2RGB(reqcov.get(reqTag))));
				req.setText(2, "");
			}
		}

    	// pack columns
		for (int idx = 0; idx < tc2reqHeader.length; idx++) {
			tcTreeView.getColumn(idx).pack();
		}
	}

	// requirements coverage
	private void createReq2TcPage() {
    	// init tree view
    	reqTreeView = new Tree(getContainer(), SWT.H_SCROLL | SWT.V_SCROLL);
		reqTreeView.setHeaderVisible(true);
		int index = addPage(reqTreeView);
		setPageText(index,"Requirements Coverage");

		// header:
		String[] req2tcHeader = {"Name", "Status", "Additional Goals"};
		for (int idx = 0; idx < req2tcHeader.length; idx++) {
			TreeColumn column = new TreeColumn(reqTreeView, SWT.LEFT);
			column.setText(req2tcHeader[idx]);
			column.setResizable(true);
			reqTreeView.getColumn(idx).pack();
		}

		String tcTag, reqTag;
		Set<String> reqSet = req2tc.keySet();
		Iterator<String> reqIt = reqSet.iterator();
		while (reqIt.hasNext()) {
			reqTag = reqIt.next();
			List<String> tcs = req2tc.get(reqTag);
			TreeItem tcParent = null;
			tcParent = new TreeItem(reqTreeView, SWT.NONE);
			tcParent.setText(0, reqTag);
			tcParent.setText(1, Verdict2String(reqcov.get(reqTag)));
			tcParent.setBackground(1, new Color(Display.getDefault(), Verdict2RGB(reqcov.get(reqTag))));
			tcParent.setText(2, "");
			for (int idx = 0; idx < tcs.size(); idx++) {
				tcTag = tcs.get(idx);
				TreeItem tc = new TreeItem(tcParent, SWT.NONE);
				tc.setText(0, tcTag);
				tc.setText(1, Verdict2String(tccov.get(tcTag)));
				tc.setBackground(1, new Color(Display.getDefault(), Verdict2RGB(tccov.get(tcTag))));
				TreeEditor editor = new TreeEditor(reqTreeView);
				editor.horizontalAlignment = SWT.LEFT;
				editor.minimumWidth = 40;
				editor.minimumHeight = 12;
			    Button cellEditor = new Button(reqTreeView, SWT.PUSH);
			    //Image image = this.getDefaultImage();
			    //Device device = image.getDevice();
			    //Image plus = new Image(device, RttMbtRequirementsCoverage.class.getResourceAsStream("plus.png"));
			    //cellEditor.setImage(plus);
			    cellEditor.setText("add");
			    cellEditor.setBackground(tcParent.getBackground());
			    cellEditor.setData(tcTag + ";" + testcases.get(tcTag) + ";");
			    cellEditor.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) { addTestcase(e); }

					@Override
					public void widgetDefaultSelected(SelectionEvent e) { addTestcase(e); }
					});
			    editor.setEditor(cellEditor, tc, 2);
			}
			tcParent.setExpanded(false);
		}
		// pack columns
		for (int idx = 0; idx < req2tcHeader.length; idx++) {
			reqTreeView.getColumn(idx).pack();
		}
	}
	
	@Override
	public void setFocus() {
		tcTreeView.setFocus();
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void doSave(IProgressMonitor arg0) {
		return;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSaveAs() {
		return;
	}
}
