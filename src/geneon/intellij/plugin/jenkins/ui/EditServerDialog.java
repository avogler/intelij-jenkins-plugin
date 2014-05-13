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

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import geneon.intellij.plugin.jenkins.model.JenkinsServer;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class EditServerDialog extends DialogWrapper {
    JenkinsServer jenkinsServer;
    JTextField nameTextField = new JTextField();
    JTextField urlTextField = new JTextField();

    public EditServerDialog(Component parent, JenkinsServer jenkinsServer) {
        super(parent, true);
        setTitle("Jenkins Server");
        this.jenkinsServer = jenkinsServer;
        this.nameTextField.setText(jenkinsServer.getName());
        this.urlTextField.setText(jenkinsServer.getUrl());
        init();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (StringUtils.isEmpty(nameTextField.getText())) {
            return new ValidationInfo("Name must be specified", nameTextField);
        }
        if (StringUtils.isEmpty(urlTextField.getText())) {
            return new ValidationInfo("URL must be specified", urlTextField);
        }
        return super.doValidate();
    }

    @Override
    protected void doOKAction() {
        jenkinsServer.setName(nameTextField.getText());
        jenkinsServer.setUrl(urlTextField.getText());
        super.doOKAction();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        nameTextField.setMinimumSize(new Dimension(300, 10));
        urlTextField.setMinimumSize(new Dimension(300, 10));

        JPanel panel = new JPanel(new GridBagLayout());
        JLabel nameLabel = new JLabel("Server name:");
        panel.add(nameLabel,
                new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 10), 0, 0));
        panel.add(nameTextField,
                new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
        JLabel urlLabel = new JLabel("URL:");
        panel.add(urlLabel,
                new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 10), 0, 0));
        panel.add(urlTextField,
                new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        return panel;
    }
}
