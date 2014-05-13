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
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.*;
import com.intellij.ui.table.JBTable;
import geneon.intellij.plugin.jenkins.AppConfiguration;
import geneon.intellij.plugin.jenkins.model.JenkinsServer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppConfigurable implements Configurable {
    // UI
    private JPanel configPanel;
    private JBTable serversTable;
    private JenkinsServerTableModel serversTableModel;

    public AppConfigurable() {

    }

    @Nls
    public String getDisplayName() {
        return "Jenkins Integration";
    }

    @Nullable
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    public JComponent createComponent() {
        configPanel = new JPanel(new GridBagLayout());

        configPanel.setBorder(IdeBorderFactory.createTitledBorder("Jenkins servers", false));

        serversTableModel = new JenkinsServerTableModel();

        serversTable = new JBTable(serversTableModel);
        JPanel serversPanel = ToolbarDecorator.createDecorator(serversTable)
                .setAddAction(new AnActionButtonRunnable() {
                    public void run(AnActionButton anActionButton) {
                        stopEditing();
                        JenkinsServer server = new JenkinsServer();
                        if (editServer(server)) {
                            serversTableModel.addServer(server);
                        }
                    }
                })
                .setEditAction(new AnActionButtonRunnable() {
                    public void run(AnActionButton button) {
                        editSelectedServer();
                    }
                })
                .setRemoveAction(new AnActionButtonRunnable() {
                    public void run(AnActionButton button) {
                        stopEditing();
                        serversTableModel.removeServer(getSelectedServer());
                    }
                })
                .createPanel();

        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(MouseEvent e) {
                editSelectedServer();
                return true;
            }
        }.installOn(serversTable);

        configPanel.add(serversPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        return configPanel;
    }

    private void editSelectedServer() {
        if (editServer(getSelectedServer())) {
            serversTableModel.fireServerUpdated(getSelectedServer());
        }
    }

    private boolean editServer(JenkinsServer server) {
        EditServerDialog dialog = new EditServerDialog(configPanel, server);
        dialog.show();
        return dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE;
    }

    private JenkinsServer getSelectedServer() {
        int row = serversTable.getSelectedRow();
        if (row != -1) {
            return serversTableModel.getServer(row);
        } else {
            return null;
        }
    }

    private void stopEditing() {
        if (serversTable.isEditing()) {
            TableCellEditor editor = serversTable.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }
    }

    public boolean isModified() {
        stopEditing();

        List<JenkinsServer> initialServers = getConfiguredServers();
        List<JenkinsServer> myServers = serversTableModel.getServers();
        if (myServers.size() != initialServers.size()) {
            return true;
        }
        for (JenkinsServer initialServer : initialServers) {
            if (!myServers.contains(initialServer)) {
                return true;
            }
        }

        return false;
    }

    public void apply() throws ConfigurationException {
        stopEditing();
        if (isModified()) {
            setConfiguredServers(serversTableModel.getServers());
        }
    }

    public void reset() {
        serversTableModel.setServers(getConfiguredServers());
    }

    private List<JenkinsServer> getConfiguredServers() {
        return ServiceManager.getService(AppConfiguration.class).getServers();
    }

    private void setConfiguredServers(List<JenkinsServer> servers) {
        ServiceManager.getService(AppConfiguration.class).setServers(servers);
    }

    public void disposeUIResources() {
        configPanel = null;
        serversTableModel = null;
        serversTable = null;
    }

    private static class JenkinsServerTableModel extends AbstractTableModel {
        private String[] colNames = {"Server", "URL"};
        private List<JenkinsServer> servers = Collections.emptyList();

        public int getRowCount() {
            return getServers().size();
        }

        public int getColumnCount() {
            return 2;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            JenkinsServer server = getServers().get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return server.getName();
                case 1:
                    return server.getUrl();
                default:
                    throw new IllegalArgumentException("invalid column index");
            }
        }

        public void addServer(JenkinsServer server) {
            servers.add(server);
            int row = servers.size() - 1;
            fireTableRowsInserted(row, row);
        }

        public void removeServer(JenkinsServer server) {
            int row = servers.indexOf(server);
            if (row != -1) {
                servers.remove(row);
                fireTableRowsDeleted(row, row);
            }
        }

        @Override
        public String getColumnName(int column) {
            return colNames[column];
        }

        public void setServers(List<JenkinsServer> servers) {
            this.servers = new ArrayList<JenkinsServer>(servers);
            fireTableDataChanged();
        }

        public List<JenkinsServer> getServers() {
            return Collections.unmodifiableList(servers);
        }

        public JenkinsServer getServer(int row) {
            return servers.get(row);
        }

        public void fireServerUpdated(JenkinsServer server) {
            int row = servers.indexOf(server);
            fireTableRowsUpdated(row, row);
        }
    }
}
