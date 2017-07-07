/*
 * Copyright 2017 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.io.apicurio.hub.api;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.apicurio.hub.api.beans.ApiDesignResourceInfo;
import io.apicurio.hub.api.beans.Collaborator;
import io.apicurio.hub.api.beans.ResourceContent;
import io.apicurio.hub.api.exceptions.NotFoundException;
import io.apicurio.hub.api.github.IGitHubService;

/**
 * @author eric.wittmann@gmail.com
 */
public class MockGitHubService implements IGitHubService {
    
    public static final String STATIC_CONTENT = "{\r\n" + 
            "  \"swagger\" : \"2.0\",\r\n" + 
            "  \"info\" : {\r\n" + 
            "    \"title\": \"Swagger Sample App\",\r\n" + 
            "    \"description\": \"This is a sample server Petstore server.\",\r\n" + 
            "    \"termsOfService\": \"http://swagger.io/terms/\",\r\n" + 
            "    \"contact\": {\r\n" + 
            "      \"name\": \"API Support\",\r\n" + 
            "      \"url\": \"http://www.swagger.io/support\",\r\n" + 
            "      \"email\": \"support@swagger.io\"\r\n" + 
            "    },\r\n" + 
            "    \"license\": {\r\n" + 
            "      \"name\": \"Apache 2.0\",\r\n" + 
            "      \"url\": \"http://www.apache.org/licenses/LICENSE-2.0.html\"\r\n" + 
            "    },\r\n" + 
            "    \"version\": \"1.0.1\"\r\n" + 
            "  },\r\n" + 
            "  \"host\": \"example.org\",\r\n" + 
            "  \"basePath\" : \"/example-api\",\r\n" + 
            "  \"schemes\" : [\r\n" + 
            "    \"http\", \"https\"\r\n" + 
            "  ],\r\n" + 
            "  \"consumes\" : [\r\n" + 
            "    \"application/json\",\r\n" + 
            "    \"application/xml\"\r\n" + 
            "  ],\r\n" + 
            "  \"produces\" : [\r\n" + 
            "    \"application/json\"\r\n" + 
            "  ]\r\n" + 
            "}\r\n" + 
            "";
    
    private List<String> audit = new ArrayList<>();

    /**
     * @see io.apicurio.hub.api.github.IGitHubService#validateResourceExists(java.lang.String)
     */
    @Override
    public ApiDesignResourceInfo validateResourceExists(String repositoryUrl) throws NotFoundException {
        getAudit().add("validateResourceExists::" + repositoryUrl);
        if (repositoryUrl.endsWith("new-api.json")) {
            throw new NotFoundException();
        }
        try {
            URI uri = new URI(repositoryUrl);
            String name = new File(uri.getPath()).getName();
            ApiDesignResourceInfo info = new ApiDesignResourceInfo();
            info.setName(name);
            info.setDescription(repositoryUrl);
            info.setUrl(repositoryUrl);
            return info;
        } catch (URISyntaxException e) {
            throw new NotFoundException();
        }
    }
    
    /**
     * @see io.apicurio.hub.api.github.IGitHubService#getCollaborators(java.lang.String)
     */
    @Override
    public Collection<Collaborator> getCollaborators(String repositoryUrl) {
        getAudit().add("getCollaborators::" + repositoryUrl);
        Set<Collaborator> rval = new HashSet<>();

        Collaborator c1 = new Collaborator();
        c1.setCommits(7);
        c1.setName("user1");
        c1.setUrl("urn:user1");
        rval.add(c1);
        
        Collaborator c2 = new Collaborator();
        c2.setCommits(7);
        c2.setName("user1");
        c2.setUrl("urn:user1");
        rval.add(c2);
        
        return rval;
    }
    
    /**
     * @see io.apicurio.hub.api.github.IGitHubService#getResourceContent(java.lang.String)
     */
    @Override
    public ResourceContent getResourceContent(String repositoryUrl) throws NotFoundException {
        getAudit().add("getResourceContent::" + repositoryUrl);
        ResourceContent rval = new ResourceContent();
        rval.setContent(STATIC_CONTENT);
        rval.setSha(String.valueOf(STATIC_CONTENT.hashCode()));
        return rval;
    }
    
    /**
     * @see io.apicurio.hub.api.github.IGitHubService#updateResourceContent(java.lang.String, java.lang.String, io.apicurio.hub.api.beans.ResourceContent)
     */
    @Override
    public void updateResourceContent(String repositoryUrl, String commitMessage, ResourceContent content) {
        getAudit().add("updateResourceContent::" + repositoryUrl + "::" + commitMessage + "::" + content.getSha() + "::" + content.getContent().hashCode());
        // do nothing - mock only
    }
    
    /**
     * @see io.apicurio.hub.api.github.IGitHubService#createResourceContent(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void createResourceContent(String repositoryUrl, String commitMessage, String content) {
        getAudit().add("createResourceContent::" + repositoryUrl + "::" + commitMessage + "::" + content.hashCode());
        // do nothing - mock only
    }
    
    /**
     * @see io.apicurio.hub.api.github.IGitHubService#getOrganizations()
     */
    @Override
    public Collection<String> getOrganizations() {
        getAudit().add("getOrganizations");
        Set<String> orgs = new HashSet<>();
        orgs.add("Org1");
        orgs.add("Org2");
        orgs.add("Org3");
        return orgs;
    }
    
    /**
     * @see io.apicurio.hub.api.github.IGitHubService#getRepositories(java.lang.String)
     */
    @Override
    public Collection<String> getRepositories(String org) {
        getAudit().add("getRepositories::" + org);
        Set<String> repos = new HashSet<>();
        repos.add(org + "-Repo1");
        repos.add(org + "-Repo2");
        repos.add(org + "-Repo3");
        return repos;
    }

    /**
     * @return the audit
     */
    public List<String> getAudit() {
        return audit;
    }

    public String auditLog() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("---\n");
        for (String auditEntry : this.audit) {
            buffer.append(auditEntry.trim());
            buffer.append("\n");
        }
        buffer.append("---");
        return buffer.toString();
    }
}
