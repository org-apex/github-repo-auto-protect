/**
 * MIT License
 */

package com.github.poc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.github.GHContentBuilder;
import org.kohsuke.github.GHContentUpdateResponse;
import org.thymeleaf.context.Context;

import com.microsoft.azure.functions.ExecutionContext;

/**
 * 
 * @author rthallam
 *
 */
public class ContentHandler {

	private GHContentBuilder contentBuilder = null;
	
	/**
	 * ContentHandler constructor
	 * @param contentBuilder
	 */
	public ContentHandler(GHContentBuilder contentBuilder) {
		this.setContentBuilder(contentBuilder);
	}
	
	/**
	 * ContentHandler constructor
	 * 
	 */
	public ContentHandler() {
	}
	
	/**
	 * Creates issue body content
	 * @param repoName
	 * @param context
	 * @return
	 */
	public String createIssueBody(String branchName,  ExecutionContext context) {
        
		 // Creating hash map
        Map<String, String> variables
            = new HashMap<String, String>();
		
        variables.put("default_branch_name", branchName);
		// Creating hash map
        Map<String, Object> args
            = new HashMap<String, Object>();
        
        List<String> protections =  new ArrayList<String>();
        protections.add("Require a pull request before merging");
        protections.add("Required Code reviewers 2");
        protections.add("Require status checks to pass before merging");
        protections.add("Enforce Admins");
        args.put("branch_protections", protections);
        return renderTempalte(variables, args, "issue", context);
	}
	
	/**
	 * Creates Readme.md file content
	 * @param repoName
	 * @param context
	 * @return
	 */
	public void createReadmeFileContent(String repoName, ExecutionContext context) {
		
		// Commit README.md file 
		Map<String, String> variables
			= new HashMap<String, String>();

		variables.put("repo_name", repoName);
		String readMdtext = renderTempalte(variables, null, "README", context);
		
		context.getLogger().info("Readme mark down Content:: " + readMdtext);
		contentBuilder.content(readMdtext);
		contentBuilder.path("README.md");
		contentBuilder.message("Creating README.MD File");
		
		try {
			GHContentUpdateResponse response = contentBuilder.commit();
			response.getCommit().createComment("Creating README.MD File thru commit");

		} catch (IOException e) {
			context.getLogger().info("Exception Message while creating commit" + e.getLocalizedMessage());

		}
	}
	
	/**
	 * Creates CONTRIBUTING.MD file content
	 * @param context
	 * @return String
	 */
	public void createContributingMdFileContent(ExecutionContext context) {
     // Commit CONTRIBUTING.md file 
		String contributingMdtext = renderTempalte(null, null, "CONTRIBUTING", context);;
		context.getLogger().info("CONTRIBUTING.MD file Content:: " + contributingMdtext);
		contentBuilder.content(contributingMdtext);
		contentBuilder.path("CONTRIBUTING.md");
		contentBuilder.message("Creating CONTRIBUTING.MD File");
		
		try {
			GHContentUpdateResponse response = contentBuilder.commit();
			response.getCommit().createComment("Creating CONTRIBUTING.MD File thru commit");

		} catch (IOException e) {
			context.getLogger().info("Exception Message while creating commit" + e.getLocalizedMessage());

		}
	}
	
	
	/**
	 * Creates License.md file content
	 * @param context
	 * @return
	 */
	public void createLicesnseFileContent(ExecutionContext context) {
        
		// Set variables for LICENSE.md template file.
		Map<String, String> variables
        	= new HashMap<String, String>();
        variables.put("year", "2022");
        variables.put("organization", "APEX");
        
        // Commit LICENSE.md file 
		String licenseMdtext = renderTempalte(variables, null, "LICENSE", context);
		context.getLogger().info("LICENSE.MD file Content:: " + licenseMdtext);
		contentBuilder.content(licenseMdtext);
		contentBuilder.path("LICENSE.md");
		contentBuilder.message("Creating LICENSE.MD File");
		
		try {
			GHContentUpdateResponse response = contentBuilder.commit();
			response.getCommit().createComment("Creating LICENSE.MD File thru commit");

		} catch (IOException e) {
			context.getLogger().info("Exception Message while creating commit" + e.getLocalizedMessage());

		}
	}
	
	/**
	 * Render Template file
	 * @param args
	 * @param templateName
	 * @param context
	 * @return
	 */
	public String renderTempalte( Map<String, String> variables, Map<String, Object> args, String templateName, ExecutionContext context) {
        Context templateContext = new Context();
        
        if (variables != null) {
        	 //Apply variable values to Thymeleaf variables in text template. 
        	variables.forEach((key,value) -> templateContext.setVariable(key, value));
        }
        
        if (args != null) {
        	// Set Arguments list
        	templateContext.setVariables(args);
        }
        
        //Render Templates
		String text = ThymeleafConfig.textTemplateEngine().process(templateName, templateContext);
		context.getLogger().info(templateName + " content:: " + text);
		return text;
	}

	/**
	 * getContentBuilder
	 * @return
	 */
	public GHContentBuilder getContentBuilder() {
		return contentBuilder;
	}

	/**
	 * setContentBuilder
	 * @param contentBuilder
	 */
	public void setContentBuilder(GHContentBuilder contentBuilder) {
		this.contentBuilder = contentBuilder;
	}
}
