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

import com.intellij.openapi.diagnostic.Logger;
import geneon.intellij.plugin.jenkins.model.JenkinsJob;
import geneon.intellij.plugin.jenkins.model.JenkinsServer;
import geneon.intellij.plugin.jenkins.ui.EditServerDialog;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ComponentRegistrationProblems")
public class JenkinsService {
    private static final Logger log = Logger.getInstance(EditServerDialog.class);

    private AppConfiguration appConfiguration;

    public JenkinsService(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    public String testServerConnectivity(String url) {
        String error = null;
        try {
            Response response = ClientBuilder.newClient().target(url).path("api/xml").request(MediaType.APPLICATION_XML_TYPE).get();
            if (response.getStatus() != 200) {
                error = "[" + response.getStatus() + "] " + response.readEntity(String.class);
            }
            response.close();
        } catch (Exception ex) {
            error = ex.getMessage();
            log.info("test of jenkins server failed", ex);
        }
        return error;
    }

    public List<JenkinsJob> getJobs(JenkinsServer jenkinsServer) {
        if (jenkinsServer.getJenkinsJobs() == null) {
            JobList list = ClientBuilder.newClient()
                    .target(jenkinsServer.getUrl())
                    .path("api/xml")
                    .queryParam("tree", "jobs[name]")
                    .request(MediaType.APPLICATION_XML_TYPE)
                    .get(JobList.class);

            jenkinsServer.setJenkinsJobs(list.getJobs());
        }

        return jenkinsServer.getJenkinsJobs();
    }

    @XmlRootElement(name = "hudson")
    public static class JobList {
        private List<JenkinsJob> jobs = new ArrayList<JenkinsJob>();

        @XmlElement(name = "job")
        public List<JenkinsJob> getJobs() {
            return jobs;
        }

        public void setJobs(List<JenkinsJob> jobs) {
            this.jobs = jobs;
        }
    }
}
