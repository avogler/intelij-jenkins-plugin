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

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.treeStructure.Tree;
import geneon.intellij.plugin.jenkins.AppConfiguration;
import geneon.intellij.plugin.jenkins.model.JenkinsServer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.Enumeration;
import java.util.List;

public class JenkinsToolWindow extends JPanel implements ProjectComponent {
    public static final String JENKINS_WINDOW_ID = "Jenkins";
    private static final Icon jenkinsIcon = IconLoader.getIcon("/geneon/intellij/plugin/jenkins/ui/jenkins.png");

    private Project myProject;

    public JenkinsToolWindow(Project project) {
        myProject = project;
    }

    public void projectOpened() {
        ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).registerToolWindow(JENKINS_WINDOW_ID, false, ToolWindowAnchor.RIGHT);
        toolWindow.setIcon(jenkinsIcon);
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory().createContent(this, null, true);
        contentManager.addContent(content);
        List<JenkinsServer> servers = ServiceManager.getService(AppConfiguration.class).getServers();

    }

    public void projectClosed() {
        ToolWindowManager.getInstance(myProject).unregisterToolWindow(JENKINS_WINDOW_ID);
    }

    public void initComponent() {
        setLayout(new BorderLayout());
        add(new JToolBar(), BorderLayout.NORTH);
        Tree tree = new Tree(new JenkinsRootNode());
        tree.setRootVisible(false);
        tree.getEmptyText().setText("Please configure at least one Jenkins server!");
        add(tree, BorderLayout.CENTER);
    }

    public void disposeComponent() {
        removeAll();
    }

    @NotNull
    public String getComponentName() {
        return "geneon.intellij.plugin.jenkins.ui.JenkinsToolWindow";
    }

    private class JenkinsRootNode implements TreeNode {
        public Enumeration children() {
            return null;
        }

        public TreeNode getChildAt(int childIndex) {
            return null;
        }

        public int getChildCount() {
            return 0;
        }

        public TreeNode getParent() {
            return null;
        }

        public int getIndex(TreeNode node) {
            return 0;
        }

        public boolean getAllowsChildren() {
            return true;
        }

        public boolean isLeaf() {
            return false;
        }
    }
}
