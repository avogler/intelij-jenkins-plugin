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

package geneon.intellij.plugin.jenkins;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import geneon.intellij.plugin.jenkins.model.JenkinsServer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ComponentRegistrationProblems")
@State(name = "JenkinsConfiguration", storages = @Storage(id = "jenkins", file = StoragePathMacros.APP_CONFIG + "/jenkins.xml"))
public class AppConfiguration implements PersistentStateComponent<AppConfiguration> {

    private List<JenkinsServer> servers = new ArrayList<JenkinsServer>();

    public List<JenkinsServer> getServers() {
        return servers;
    }

    public void setServers(List<JenkinsServer> servers) {
        this.servers = servers;
    }

    @Nullable
    public AppConfiguration getState() {
        return this;
    }

    public void loadState(AppConfiguration state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
