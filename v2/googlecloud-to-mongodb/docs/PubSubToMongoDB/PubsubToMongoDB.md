
Pubsub to MongoDB template
---
The Pubsub to MongoDB template is a Streaming pipeline that reads rows from a
Pubsub and writes data to MongoDB as documents.

:bulb: This is a generated documentation based
on [Metadata Annotations](https://github.com/GoogleCloudPlatform/DataflowTemplates#metadata-annotations)
. Do not change this file directly.

## Parameters

### Required Parameters

* **mongoDbUri** (MongoDB Connection URI): URI to connect to MongoDB Atlas.
* **database** (MongoDB Database): Database in MongoDB to store the collection. (Example: my-db).
* **collection** (MongoDB collection): Name of the collection inside MongoDB database. (Example: my-collection).
* **subscription** (Pubsub subscription): Subsub source table spec. (Example: "projects/project-name/subscriptions/subscription-name).

### Optional Parameters




## Getting Started

### Requirements

* Java 11
* Maven
* [gcloud CLI](https://cloud.google.com/sdk/gcloud), and execution of the
  following commands:
  * `gcloud auth login`
  * `gcloud auth application-default login`

:star2: Those dependencies are pre-installed if you use Google Cloud Shell!

[![Open in Cloud Shell](http://gstatic.com/cloudssh/images/open-btn.svg)](https://console.cloud.google.com/cloudshell/editor?cloudshell_git_repo=https%3A%2F%2Fgithub.com%2FGoogleCloudPlatform%2FDataflowTemplates.git&cloudshell_open_in_editor=v2/googlecloud-to-mongodb/src/main/java/com/google/cloud/teleport/v2/mongodb/templates/PubsubToMongoDB.java)

### Templates Plugin

This README provides instructions using
the [Templates Plugin](https://github.com/GoogleCloudPlatform/DataflowTemplates#templates-plugin).

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
-DtemplateName="Pubsub_to_MongoDB" \
-f v2/googlecloud-to-mongodb
```


The command should build and save the template to Google Cloud, and then print
the complete location on Cloud Storage:

```
Flex Template was staged! gs://<bucket-name>/templates/flex/Pubsub_to_MongoDB
```

The specific path should be copied as it will be used in the following steps.

#### Running the Template

**Using the staged template**:

You can use the path above run the template (or share with others for execution).

To start a job with the template at any time using `gcloud`, you are going to
need valid resources for the required parameters.

Provided that, the following command line can be used:

```shell
export PROJECT=<my-project>
export BUCKET_NAME=<bucket-name>
export REGION=us-central1
export TEMPLATE_SPEC_GCSPATH="gs://$BUCKET_NAME/templates/flex/Pubsub_to_MongoDB"

### Required
export MONGO_DB_URI=<mongoDbUri>
export DATABASE=<database>
export COLLECTION=<collection>
export SUBSCRIPTION=<subscription>

### Optional

gcloud dataflow flex-template run "pubsub-to-mongodb-job" \
  --project "$PROJECT" \
  --region "$REGION" \
  --template-file-gcs-location "$TEMPLATE_SPEC_GCSPATH" \
  --parameters "mongoDbUri=$MONGO_DB_URI" \
  --parameters "database=$DATABASE" \
  --parameters "collection=$COLLECTION" \
  --parameters "subscription=$SUBSCRIPTION"
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

### Required
export MONGO_DB_URI=<mongoDbUri>
export DATABASE=<database>
export COLLECTION=<collection>
export INPUT_TABLE_SPEC=<inputTableSpec>

### Optional

mvn clean install -PtemplatesRun \
-DskipTests \
-DprojectId="$PROJECT" \
-DbucketName="$BUCKET_NAME" \
-Dregion="$REGION" \
-DjobName="pubsub-to-mongodb-job" \
-DtemplateName="Pubsub_to_MongoDB" \
-Dparameters="mongoDbUri=$MONGO_DB_URI,database=$DATABASE,collection=$COLLECTION,subscription=$SUBSCRIPTION" \
-f v2/googlecloud-to-mongodb
```
