const path = require("path");

const cdk = require("aws-cdk-lib");

const iam = require("aws-cdk-lib/aws-iam");
const lambda = require("aws-cdk-lib/aws-lambda");
const apigateway = require("aws-cdk-lib/aws-apigateway");

class CdkAppStack extends cdk.Stack {
    /**
     *
     * @param {cdk.Construct} scope
     * @param {string} id
     * @param {cdk.StackProps=} props
     */
    constructor(scope, id, props) {
        super(scope, id, props);

        const role = new iam.Role(this, "CustomerSummaryLambdaRole", {
            assumedBy: new iam.ServicePrincipal("lambda.amazonaws.com"),
            managedPolicies: [
                iam.ManagedPolicy.fromAwsManagedPolicyName(
                    "service-role/AWSLambdaBasicExecutionRole"
                ),
            ],
        });

        const lambdaFn = new lambda.Function(
            this,
            "CustomerSummaryFunction",
            {
                runtime: lambda.Runtime.JAVA_17,
                architecture: lambda.Architecture.ARM_64,
                handler: "com.customersummary.Handler::handleRequest",
                code: lambda.Code.fromAsset(
                    path.join(__dirname, "..", "lambda", "target", "customer-summary.jar")
                ),
                memorySize: 512,
                timeout: cdk.Duration.seconds(30),
                role,
            }
        );

        const api = new apigateway.RestApi(
            this,
            "CustomerSummaryApi"
        );

        const customerSummary =
            api.root.addResource("customer-summary");

        customerSummary.addMethod(
            "GET",
            new apigateway.LambdaIntegration(lambdaFn)
        );

        new cdk.CfnOutput(this, "CustomerSummaryUrl", {
            value: api.urlForPath("/customer-summary"),
        });
    }
}

module.exports = { CdkAppStack };