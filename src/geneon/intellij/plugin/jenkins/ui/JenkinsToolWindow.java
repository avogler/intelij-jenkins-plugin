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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class JenkinsToolWindow extends JPanel implements ProjectComponent {
    public static final String JENKINS_WINDOW_ID = "Jenkins Integration";
    private static final Icon JENKINS_ICON = IconLoader.getIcon("/geneon/intellij/plugin/jenkins/ui/jenkins.png");

    private Project myProject;
    private Tree tree;

    public JenkinsToolWindow(Project project) {
        myProject = project;
    }

    public void projectOpened() {
        ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).registerToolWindow(JENKINS_WINDOW_ID, false, ToolWindowAnchor.RIGHT);
        toolWindow.setIcon(JENKINS_ICON);
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory().createContent(this, null, true);
        contentManager.addContent(content);
        tree.setModel(new JenkinsTreeModel());
    }

    public void projectClosed() {
        ToolWindowManager.getInstance(myProject).unregisterToolWindow(JENKINS_WINDOW_ID);
    }

    public void initComponent() {
        setLayout(new BorderLayout());
        add(new JToolBar(), BorderLayout.NORTH);
        tree = new Tree();
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
}
