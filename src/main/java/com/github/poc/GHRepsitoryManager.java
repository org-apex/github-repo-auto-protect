/**
 * MIT License
 */
package com.github.poc;

import java.io.IOException;

import org.apache.commons.lang3.RandomStringUtils;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

/**
 * @author ravi2013
 *
 */
public class GHRepsitoryManager {
	
	static final String OATUH_KEY = "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";
	static final String ORGANIZATION = "org-apex";
	
	/**
	 * Function to Create Repository under the organization
	 * @param name
	 */
	private static void createGitHubRepository(String name) {
        try {
        	// Connect to GitHub organization using OAuth Private Access Key
			GitHub github = new GitHubBuilder().withOAuthToken(OATUH_KEY,ORGANIZATION).build();
            GHOrganization org = github.getOrganization(ORGANIZATION);
            // Create repository under the organization
            GHRepository repo = org.createRepository(name).create();
            System.out.println(name + " Repo created Default Branch:: " + repo.getDefaultBranch());
            System.out.println(name + " Repo created Full Name:: " + repo.getFullName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	
	/**
	 * Function to Create Repository under the organization
	 * @param name
	 */
	private static void archiveGitHubRepository(String name) {
        try {
        	// Connect to GitHub organization using OAuth Private Access Key
			GitHub github = new GitHubBuilder().withOAuthToken(OATUH_KEY,ORGANIZATION).build();
            GHOrganization org = github.getOrganization(ORGANIZATION);
            // Create repository under the organization
            GHRepository repo = org.getRepository(name);
            repo.archive();
            System.out.println(name + " Repo Archived:: " + repo.getDefaultBranch());
            System.out.println(name + " Repo Archived:: " + repo.getFullName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Main function
     * @param args
     */
    public static void main(String args[]) {
    	int length = 10;
	    boolean useLetters = true;
	    boolean useNumbers = false;
	    // Generate random repository name
	    String repoName = "apex-repo-" + RandomStringUtils.random(length, useLetters, useNumbers);
	    System.out.println("REPO Name: " + repoName);
    	createGitHubRepository(repoName);
    	//repoName = "apex-repo-HiEQrqeJJj";
    	//archiveGitHubRepository(repoName);
    }

}
