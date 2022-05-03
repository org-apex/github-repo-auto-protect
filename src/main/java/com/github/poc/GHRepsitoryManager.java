/**
 * MIT License
 */
package com.github.poc;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

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
	 * createGitHubRepositoryOnaScale
	 */
	private static void createGitHubRepositoryOnaScale(String repoName) {
		Timer timer = new Timer();
		int begin = 0;
		int timeInterval = 2000;
		timer.schedule(new TimerTask() {
		  int counter = 0;
		   @Override
		   public void run() {
		       //call the method
		       counter++;
		       print(repoName, counter);
		       if (counter >= 5){
		         timer.cancel();
		       }
		   }
		}, begin, timeInterval);
    }
		

	private static void print(String repoName, int counter) {
		String updatedRepoName = repoName + counter;
		createGitHubRepository(updatedRepoName);
        System.out.println(repoName + counter + " created at : " + new java.util.Date());
    }
 

	
    /**
     * Main function
     * @param args
     */
    public static void main(String args[]) {
	    // Generate random repository name
	    String repoName = "apex-repo-repository";
	    System.out.println("REPO Name: " + repoName);
    	//createGitHubRepository(repoName);
    	//repoName = "apex-repo-HiEQrqeJJj";
    	//archiveGitHubRepository(repoName);
	    createGitHubRepositoryOnaScale(repoName);
    }

}
