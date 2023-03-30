Pub/Sub to Kafka Template
---
A streaming pipeline which inserts data from a Pub/Sub Topic and transforms them using a JavaScript user-defined function (UDF)(Optional), and writes them to kafka topic.

:memo: This is a Google-provided template! Please
check [Provided templates documentation](https://cloud.google.com/dataflow/docs/guides/templates/provided-templates)
on how to use it without having to build from sources.

:bulb: This is a generated documentation based
on [Metadata Annotations](https://github.com/GoogleCloudPlatform/DataflowTemplates#metadata-annotations)
. Do not change this file directly.

## Parameters

### Mandatory Parameters

* **inputTopic** (Input Pub/Sub topic): The name of the topic from which data should published, in the format of 'projects/your-project-id/topics/your-topic-name' (Example: projects/your-project-id/topics/your-topic-name).
* **outputTopic** (Kafka topic to write the input from pubsub): Kafka topic to write the input from pubsub. (Example: topic).
* **outputDeadLetterTopic** (Output deadletter Pub/Sub topic): The Pub/Sub topic to publish deadletter records to. The name should be in the format of projects/your-project-id/topics/your-topic-name.

### Optional Parameters

* **bootstrapServer** (Output Kafka Bootstrap Server): Kafka Bootstrap Server  (Example: localhost:9092).
* **secretStoreUrl** (Secret Store URL): URL to credentials in Vault.
* **vaultToken** (Vault token): Token to use for Vault.
* **javascriptTextTransformGcsPath** (Cloud Storage path to Javascript UDF source): The Cloud Storage path pattern for the JavaScript code containing your user-defined functions. (Example: gs://your-bucket/your-function.js).
* **javascriptTextTransformFunctionName** (UDF Javascript Function Name): The name of the function to call from your JavaScript file. Use only letters, digits, and underscores. (Example: 'transform' or 'transform_udf1').

## Getting Started

### Requirements

* Java 11
* Maven
* Valid resources for mandatory parameters.
* [gcloud CLI](https://cloud.google.com/sdk/gcloud), and execution of the
  following commands:
    * `gcloud auth login`
    * `gcloud auth application-default login`

The following instructions use the
[Templates Plugin](https://github.com/GoogleCloudPlatform/DataflowTemplates#templates-plugin)
. Install the plugin with the following command to proceed:

```shell
mvn clean install -pl plugins/templates-maven-plugin -am
```

### Building Template

This template is a Flex Template, meaning that the pipeline code will be
containerized and the container will be executed on Dataflow. Please
check [Use Flex Templates](https://cloud.google.com/dataflow/docs/guides/templates/using-flex-templates)
and [Configure Flex Templates](https://cloud.google.com/dataflow/docs/guides/templates/configuring-flex-templates)
for more information.

#### Staging the Template

If the plan is to just stage the template (i.e., make it available to use) by
the `gcloud` command or Dataflow "Create job from template" UI,
the `-PtemplatesStage` profile should be used:

```shell
export PROJECT=<my-project>
export BUCKET_NAME=<bucket-name>

mvn clean package -PtemplatesStage  \
-DskipTests \
-DprojectId="$PROJECT" \
-DbucketName="$BUCKET_NAME" \
-DstagePrefix="templates" \
-DtemplateName="PubSub_to_Kafka" \
-pl v2/pubsub-to-kafka \
-am
```

The command should build and save the template to Google Cloud, and then print
the complete location on Cloud Storage:

```
Flex Template was staged! gs://<bucket-name>/templates/flex/PubSub_to_Kafka
```

The specific path should be copied as it will be used in the following steps.

#### Running the Template

**Using the staged template**:

You can use the path above run the template (or share with others for execution).

To start a job with that template at any time using `gcloud`, you can use:

```shell
export PROJECT=<my-project>
export BUCKET_NAME=<bucket-name>
export REGION=us-central1
export TEMPLATE_SPEC_GCSPATH="gs://$BUCKET_NAME/templates/flex/PubSub_to_Kafka"

### Mandatory
export INPUT_TOPIC=<inputTopic>
export OUTPUT_TOPIC=<outputTopic>
export OUTPUT_DEAD_LETTER_TOPIC=<outputDeadLetterTopic>

### Optional
export BOOTSTRAP_SERVER=<bootstrapServer>
export SECRET_STORE_URL=<secretStoreUrl>
export VAULT_TOKEN=<vaultToken>
export JAVASCRIPT_TEXT_TRANSFORM_GCS_PATH=<javascriptTextTransformGcsPath>
export JAVASCRIPT_TEXT_TRANSFORM_FUNCTION_NAME=<javascriptTextTransformFunctionName>

gcloud dataflow flex-template run "pubsub-to-kafka-job" \
  --project "$PROJECT" \
  --region "$REGION" \
  --template-file-gcs-location "$TEMPLATE_SPEC_GCSPATH" \
  --parameters "inputTopic=$INPUT_TOPIC" \
  --parameters "bootstrapServer=$BOOTSTRAP_SERVER" \
  --parameters "outputTopic=$OUTPUT_TOPIC" \
  --parameters "outputDeadLetterTopic=$OUTPUT_DEAD_LETTER_TOPIC" \
  --parameters "secretStoreUrl=$SECRET_STORE_URL" \
  --parameters "vaultToken=$VAULT_TOKEN" \
  --parameters "javascriptTextTransformGcsPath=$JAVASCRIPT_TEXT_TRANSFORM_GCS_PATH" \
  --parameters "javascriptTextTransformFunctionName=$JAVASCRIPT_TEXT_TRANSFORM_FUNCTION_NAME"
```

For more information about the command, please check:
https://cloud.google.com/sdk/gcloud/reference/dataflow/flex-template/run


**Using the plugin**:

Instead of just generating the template in the folder, it is possible to stage
and run the template in a single command. This may be useful for testing when
changing the templates.

```shell
export PROJECT=<my-project>
export BUCKET_NAME=<bucket-name>
export REGION=us-central1

### Mandatory
export INPUT_TOPIC=<inputTopic>
export OUTPUT_TOPIC=<outputTopic>
export OUTPUT_DEAD_LETTER_TOPIC=<outputDeadLetterTopic>

### Optional
export BOOTSTRAP_SERVER=<bootstrapServer>
export SECRET_STORE_URL=<secretStoreUrl>
export VAULT_TOKEN=<vaultToken>
export JAVASCRIPT_TEXT_TRANSFORM_GCS_PATH=<javascriptTextTransformGcsPath>
export JAVASCRIPT_TEXT_TRANSFORM_FUNCTION_NAME=<javascriptTextTransformFunctionName>

mvn clean package -PtemplatesRun \
-DskipTests \
-DprojectId="$PROJECT" \
-DbucketName="$BUCKET_NAME" \
-Dregion="$REGION" \
-DjobName="pubsub-to-kafka-job" \
-DtemplateName="PubSub_to_Kafka" \
-Dparameters="inputTopic=$INPUT_TOPIC,bootstrapServer=$BOOTSTRAP_SERVER,outputTopic=$OUTPUT_TOPIC,outputDeadLetterTopic=$OUTPUT_DEAD_LETTER_TOPIC,secretStoreUrl=$SECRET_STORE_URL,vaultToken=$VAULT_TOKEN,javascriptTextTransformGcsPath=$JAVASCRIPT_TEXT_TRANSFORM_GCS_PATH,javascriptTextTransformFunctionName=$JAVASCRIPT_TEXT_TRANSFORM_FUNCTION_NAME" \
-pl v2/pubsub-to-kafka \
-am
```