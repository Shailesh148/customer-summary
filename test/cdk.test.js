const cdk = require("aws-cdk-lib");
const { Template } = require("aws-cdk-lib/assertions");
const { CdkAppStack } = require("../lib/cdk-app-stack");

test("creates the Java Lambda and customer summary API", () => {
	const app = new cdk.App();
	const stack = new CdkAppStack(app, "TestStack");
	const template = Template.fromStack(stack);

	template.resourceCountIs("AWS::Lambda::Function", 1);
	template.hasResourceProperties("AWS::Lambda::Function", {
		Runtime: "java17",
		Handler: "com.customersummary.Handler::handleRequest",
	});
	template.resourceCountIs("AWS::ApiGateway::RestApi", 1);
});
