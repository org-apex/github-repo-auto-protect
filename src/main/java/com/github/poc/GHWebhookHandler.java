/**
 * MIT License
 */

package com.github.poc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHBranchProtection;
import org.kohsuke.github.GHBranchProtection.RequiredReviews;
import org.kohsuke.github.GHBranchProtection.RequiredStatusChecks;
import org.kohsuke.github.GHContentBuilder;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;
import java.io.IOException;
import java.util.Optional;

/**
 * 
 * This is the Azure Spring Functions Handler class to handle Github requests and enable protection to Github repositories
 * 
 * @author rtallam
 *
 */
public class GHWebhookHandler extends FunctionInvoker<String, String> {

	// TODO: Externalize the PAT Token.
	static final String OATUH_KEY = "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";

	/**
	 * Azure function to handle Github webhook requests
	 * @param request
	 * @param context
	 * @return
	 */
	@FunctionName("handler")
	public HttpResponseMessage execute(@HttpTrigger(name = "request", methods = { HttpMethod.GET,
			HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
			ExecutionContext context) {
		context.getLogger().info("Enter Handler Method...");

		try {
			String webhookBody = request.getBody().get();
			@SuppressWarnings("deprecation")
			JsonObject jsonObject = new JsonParser().parse(webhookBody).getAsJsonObject();

			String action = jsonObject.get("action").getAsString();
			context.getLogger().info("Action:" + action);

			// Handle only created action, ignore remaining actions
			if (action.equalsIgnoreCase("created")) {
				String repoName = jsonObject.getAsJsonObject("repository").get("full_name").getAsString();
				context.getLogger().info("Repository Name:" + repoName);
				protectGitHubRepositoryBranch(repoName, context);
			} else {
				context.getLogger().info("Do NOT Do Anything!!");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return request.createResponseBuilder(HttpStatus.OK).body(handleRequest("SUCCESS", context))
				.header("Content-Type", "application/json").build();
	}
	
	
	/**
	 * Protect Github repository branch
	 * @param repoName
	 * @param context
	 */
	private void protectGitHubRepositoryBranch(String repoName, ExecutionContext context) {
		context.getLogger().info("Enter protectGitHubRepositoryBranch Method...");

		GitHub github = null;
		GHRepository repo = null;
		String defaultBranchName = null;
		GHBranch branch = null;
		ContentHandler contentHandler =  null;
		
		try { 
			
			// Connect to GitHub organization using OAuth Private Access Key
			github = new GitHubBuilder().withOAuthToken(OATUH_KEY).build();
			
			// Create repository under the organization
			repo = github.getRepository(repoName);

			if (repo == null) {
				context.getLogger().info("Repository not found..." + repoName);
				return;
			}
			
			// Get default branch name
			defaultBranchName = repo.getDefaultBranch();
			context.getLogger().info("Default Branch Name:" + defaultBranchName);
			
			// Get the branch is it's already created
			branch = repo.getBranch(defaultBranchName);

		} catch (Exception e) {
			context.getLogger().info("Exception Message" + e.getLocalizedMessage());
			
			GHContentBuilder contentBuilder = repo.createContent();
			contentBuilder.branch(defaultBranchName);
			
			contentHandler =  new ContentHandler(contentBuilder);
			
			// Create README.MD file
			contentHandler.createReadmeFileContent(repoName, context);
			
			// Create CONTRIBUTNG.MD file
			contentHandler.createContributingMdFileContent(context);
			
			// Create LICENSE.MD file
			contentHandler.createLicesnseFileContent(context);
			
			try {
				branch = repo.getBranch(defaultBranchName);
			} catch (IOException e1) {
				context.getLogger().info("Exception Message while getting branch" + e.getLocalizedMessage());

			}
		}

		if (branch != null) {
			context.getLogger().info("Branch Name:" + branch.getName());
			if (!branch.isProtected()) {
				context.getLogger().info("Branch is not protected: " + branch.getName());
				try {
					context.getLogger().info("Protect the Branch: " + branch.getName());
					// Enable protection and edit protection rules
					GHBranchProtection protection = branch.enableProtection().addRequiredChecks("test-status-check")
							.requireBranchIsUpToDate().requireCodeOwnReviews().dismissStaleReviews()
							.requiredReviewers(2).includeAdmins().enable();
					RequiredStatusChecks statusChecks = protection.getRequiredStatusChecks();
					context.getLogger().info("Requires Branch UpToDate: " + statusChecks.isRequiresBranchUpToDate());
					RequiredReviews requiredReviews = protection.getRequiredReviews();
					context.getLogger()
							.info("isRequireCodeOwnerReviews: " + requiredReviews.isRequireCodeOwnerReviews());
					context.getLogger().info("RequiredReviewers: " + requiredReviews.getRequiredReviewers());

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				String title = "Protection is Enabled for the branch '" + defaultBranchName + "'";
				GHIssueBuilder issueBuilder = repo.createIssue(title);
				if(contentHandler == null) {
					contentHandler =  new ContentHandler();
				}
				String issueBodyContent = contentHandler.createIssueBody(defaultBranchName, context);
				issueBuilder.body(issueBodyContent);
				issueBuilder.label("protection enabled");
				issueBuilder.assignee("ravikumarth");
				try {
					GHIssue issue = issueBuilder.create();
					issue.comment("Protection is enabled");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}	
}

	