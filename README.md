# Customer Summary BFF

This AWS CDK application deploys a Java 17 Lambda behind API Gateway. The Lambda
fetches users, products, and carts from Fake Store API and returns
enriched customer order summaries.

## Key Design Decisions
- I chose plain Java rather than Java with Spring Boot because the Lambda has a relatively simple responsibility: call three external APIs, transform their responses, and return a combined result.

- Using plain Java keeps the deployment lightweight and minimizes framework and dependency overhead. Spring Boot would introduce additional startup work, including initializing the application context and creating/configuring beans, which can contribute to Lambda cold-start latency.

- For a larger application with more complex dependency injection, configuration, security, persistence, or business logic, Spring Boot could be justified. For this small, focused Lambda, I felt that plain Java was the simpler and more appropriate choice.

- I have added dto layer for handling request and responses classes, mapper to do the necessary conversions as required by the openapi yml, service layer to handle all logical portions and a common httpClient class so the three api calls could call this directly.

- I used AWS CDK to define and provision the deployment stack as infrastructure as code. The deployment stack consists of an API Gateway endpoint that receives request and then invokes the Lambda function. The Lambda function then calls the three downstream APIs, aggregates their responses, and returns the combined result through API Gateway. I have not kept Lambda inside a private VPC subnet here. 

## Edge Cases Handling
- Customers without any carts are excluded from the response.
- Customers with multiple carts receive one order entry per cart.
- A generic Error Message Payload is created containing `message` and `code`
- Non-success responses or invalid downstream payloads return HTTP `502` with
  `code: "DOWNSTREAM_ERROR"`.
- Downstream timeouts return HTTP `504` with
  `code: "DOWNSTREAM_TIMEOUT"`.
- Error responses always contain a JSON `message` and `code`.
- If any required downstream API fails, the request returns an error instead of
  returning incomplete customer summaries.

## Deploy

## Prerequisites

- Node.js and npm
- Java 17+
- Maven 3.9+
- AWS CLI credentials configured
- AWS CDK bootstrap completed for the target account and region

## Install

```bash
npm install
```

## Test and run locally

To build and run the lambda handler locally:

```bash
npm run build
cd lambda
java -cp target/customer-summary.jar com.customersummary.LocalRunner
cd ..
```

The local runner calls the live Fake Store API services and prints the response.

## Deploy to AWS

The first deployment:

```bash
npx cdk bootstrap
```

For a preview without deploying:

```bash
npm run synth
npx cdk diff
```

The `deploy` script builds the Java jar before CDK deployment:

```bash
npm run deploy
```



## Project layout

- `lib/`: CDK stack
- `lambda/src/main/java/`: Java Lambda source
- `lambda/src/test/java/`: Java unit tests
- `lambda/pom.xml`: Maven build configuration
