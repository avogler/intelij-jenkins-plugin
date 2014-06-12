/*
 * IntelliJ Jenkins Integration Plugin
 * Copyright (C) 2014 Andreas Vogler
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package geneon.intellij.plugin.jenkins.ui;

import com.intellij.openapi.components.ServiceManager;
import geneon.intellij.plugin.jenkins.AppConfiguration;
import geneon.intellij.plugin.jenkins.JenkinsService;
import geneon.intellij.plugin.jenkins.model.JenkinsJob;
import geneon.intellij.plugin.jenkins.model.JenkinsServer;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Collections;
import java.util.List;

public class JenkinsTreeModel implements TreeModel {
    public static final String JENKINS_TREE_ROOT = "JENKINS_TREE_ROOT";

    protected EventListenerList listenerList = new EventListenerList();

    public Object getRoot() {
        return JENKINS_TREE_ROOT;
    }

    public Object getChild(Object parent, int index) {
        return getChildren(parent).get(index);
    }

    @NotNull
    private List<?> getChildren(Object parent) {
        if (JENKINS_TREE_ROOT.equals(parent)) {
            return ServiceManager.getService(AppConfiguration.class).getServers();
        }
        if (parent instanceof JenkinsServer) {
            ServiceManager.getService(JenkinsService.class).getJobs((JenkinsServer) parent);
        }
        return Collections.emptyList();
    }

    public int getChildCount(Object parent) {
        return getChildren(parent).size();
    }

    public boolean isLeaf(Object node) {
        return node instanceof JenkinsJob;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        // not implemented, model is not mutable
    }

    public int getIndexOfChild(Object parent, Object child) {
        return getChildren(parent).indexOf(child);
    }

    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    protected void fireTreeNodesChanged(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        final TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
        fireEvent(new EventSender<TreeModelListener>() {
            public void sendEvent(TreeModelListener listener) {
                listener.treeNodesChanged(event);
            }
        });
    }

    protected void fireTreeNodesInserted(Object source, Object[] path,
                                         int[] childIndices,
                                         Object[] children) {
        final TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
        fireEvent(new EventSender<TreeModelListener>() {
            public void sendEvent(TreeModelListener listener) {
                listener.treeNodesInserted(event);
            }
        });
    }

    protected void fireTreeNodesRemoved(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        final TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
        fireEvent(new EventSender<TreeModelListener>() {
            public void sendEvent(TreeModelListener listener) {
                listener.treeNodesRemoved(event);
            }
        });
    }

    protected void fireTreeStructureChanged(Object source, Object[] path,
                                            int[] childIndices,
                                            Object[] children) {
        final TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
        fireEvent(new EventSender<TreeModelListener>() {
            public void sendEvent(TreeModelListener listener) {
                listener.treeStructureChanged(event);
            }
        });
    }

    private void fireTreeStructureChanged(Object source, TreePath path) {

        final TreeModelEvent event = new TreeModelEvent(source, path);
        fireEvent(new EventSender<TreeModelListener>() {
            public void sendEvent(TreeModelListener listener) {
                listener.treeNodesInserted(event);
            }
        });
    }

    private void fireEvent(EventSender eventSender) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                eventSender.sendEvent((TreeModelListener) listeners[i + 1]);
            }
        }
    }

}
