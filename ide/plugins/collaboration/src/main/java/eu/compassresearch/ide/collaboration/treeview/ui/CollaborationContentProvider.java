package eu.compassresearch.ide.collaboration.treeview.ui;

import java.util.Iterator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import eu.compassresearch.ide.collaboration.treeview.model.CollaborationGroup;
import eu.compassresearch.ide.collaboration.treeview.model.Contract;
import eu.compassresearch.ide.collaboration.treeview.model.Contracts;
import eu.compassresearch.ide.collaboration.treeview.model.DeltaEvent;
import eu.compassresearch.ide.collaboration.treeview.model.IDeltaListener;
import eu.compassresearch.ide.collaboration.treeview.model.Model;
import eu.compassresearch.ide.collaboration.treeview.model.Shares;
import eu.compassresearch.ide.collaboration.treeview.model.TreeRoot;
import eu.compassresearch.ide.collaboration.treeview.model.Versions;

public class CollaborationContentProvider implements ITreeContentProvider, IDeltaListener {
	private static Object[] EMPTY_ARRAY = new Object[0];
	protected TreeViewer viewer;
	
	public void dispose() {}

	/**
	* Notifies this content provider that the given viewer's input
	* has been switched to a different element.
	* <p>
	* A typical use for this method is registering the content provider as a listener
	* to changes on the new input (using model-specific means), and deregistering the viewer 
	* from the old input. In response to these change notifications, the content provider
	* propagates the changes to the viewer.
	* </p>
	*
	* @param viewer the viewer
	* @param oldInput the old input element, or <code>null</code> if the viewer
	*   did not previously have an input
	* @param newInput the new input element, or <code>null</code> if the viewer
	*   does not have an input
	*/
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
		if(oldInput != null) {
			removeListenerFrom((TreeRoot)oldInput);
		}
		if(newInput != null) {
			addListenerTo((TreeRoot)newInput);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively remove this listener
	 * from each child box of the given box. */
	protected void removeListenerFrom(TreeRoot root) {
		root.removeListener(this);
		for (Iterator iterator = root.getContracts().iterator(); iterator.hasNext();) {
			Contracts aBox = (Contracts) iterator.next();
			//removeListenerFrom(aBox);
		}
	}
	
	protected void addListenerTo(Model root) {
		root.addListener(this);
	}

	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof TreeRoot) {
			TreeRoot root = (TreeRoot)parentElement;
				return concat(root.getContracts().toArray(), root.getCollaboratorGroups().toArray());
		} else if(parentElement instanceof Contracts) {
			Contracts contracts = (Contracts)parentElement;
			return contracts.getContracts().toArray();
		} else if(parentElement instanceof Contract) {
			Contract contract = (Contract)parentElement;
			return new Object[]{contract.getShares(), contract.getVersions()};
		} else if(parentElement instanceof Shares) {
			Shares shares = (Shares)parentElement;
			return shares.getShares().toArray();
		} else if(parentElement instanceof Versions) {
			Versions versions = (Versions)parentElement;
			return versions.getVersions().toArray();
		} else if(parentElement instanceof CollaborationGroup) {
			CollaborationGroup collabGroup = (CollaborationGroup)parentElement;
			return collabGroup.getCollaborators().toArray();
		}
		
		return EMPTY_ARRAY;
	}
	
	protected Object[] concat(Object[] objects, Object[] objects2) {
		Object[] both = new Object[objects.length + objects2.length];
		System.arraycopy(objects, 0, both, 0, objects.length);
		System.arraycopy(objects2, 0, both, objects.length, objects2.length);		
		return both;
	}

	public Object getParent(Object element) {
		if(element instanceof Model) {
			return ((Model)element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void add(DeltaEvent event) {
		Object root = ((Model)event.receiver()).getParent();
		viewer.refresh(root, true);
	}

	public void remove(DeltaEvent event) {
		add(event);
	}

}
