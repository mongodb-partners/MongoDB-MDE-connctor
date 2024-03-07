/*
 * Copyright mongoDB
 */
package com.google.cloud.teleport.v2.mongodb.options;

import com.google.cloud.teleport.metadata.TemplateParameter;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.PipelineOptions;

/**
 * The {@link PubsubToMongoDBOptions} class provides the custom execution options passed by the
 * executor at the command-line.
 */
public class PubsubToMongoDBOptions {
  /** MongoDB write Options initialization. */
  public interface MongoDbOptions extends PipelineOptions, DataflowPipelineOptions {

    @TemplateParameter.Text(
        order = 1,
        description = "MongoDB Connection URI",
        helpText = "URI to connect to MongoDB Atlas.")
    String getMongoDBUri();

    void setMongoDBUri(String getMongoDBUri);

    @TemplateParameter.Text(
        order = 2,
        description = "MongoDB Database",
        helpText = "Database in MongoDB to store the collection.",
        example = "my-db")
    String getDatabase();

    void setDatabase(String database);

    @TemplateParameter.Text(
        order = 3,
        description = "MongoDB collection",
        helpText = "Name of the collection inside MongoDB database.",
        example = "my-collection")
    String getCollection();

    void setCollection(String collection);
  }

  /** PubSub read Options initialization. */
  public interface PubsubReadOptions extends PipelineOptions, DataflowPipelineOptions {

    @TemplateParameter.PubsubSubscription(
        order = 1,
        description = "Pubsub subscription",
        helpText = "pubsub subscription",
        example = "")
    String getInputSubscription();

    void setInputSubscription(String inputSubscription);
  }
}
