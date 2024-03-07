/*
 * Copyright mongoDB
 */
package com.google.cloud.teleport.v2.mongodb.templates;

import com.google.cloud.teleport.metadata.Template;
import com.google.cloud.teleport.metadata.TemplateCategory;
import com.google.cloud.teleport.v2.mongodb.options.PubsubToMongoDBOptions.MongoDbOptions;
import com.google.cloud.teleport.v2.mongodb.options.PubsubToMongoDBOptions.PubsubReadOptions;
import com.google.cloud.teleport.v2.mongodb.templates.PubsubToMongoDB.Options;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.io.mongodb.MongoDbIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.bson.Document;

/**
 * The {@link PubsubToMongoDB} pipeline is a STREAMING pipeline which reads data from PubSub and
 * ingests to MongoDB.
 *
 * <p>Check out <a
 * href="https://github.com/GoogleCloudPlatform/DataflowTemplates/blob/main/v2/mongodb-to-googlecloud/PubsubToMongoDB.md">README</a>
 * for instructions on how to use or modify this template.
 */
@Template(
    name = "Pubsub_to_MongoDB",
    category = TemplateCategory.STREAMING,
    displayName = "PubSub to MongoDB",
    description = "",
    optionsClass = Options.class,
    flexContainerName = "pubsub-to-mongodb",
    documentation =
        "https://cloud.google.com/dataflow/docs/guides/templates/provided/pubsub-to-mongodb",
    contactInformation = "https://cloud.google.com/support",
    preview = true,
    requirements = {
      "The source Pubsub subscription must exist.",
      "The targe MongoDB instance must be accessible from the Dataflow worker machines."
    })
public class PubsubToMongoDB {
  /**
   * Options supported by {@link PubsubToMongoDB}
   *
   * <p>Inherits standard configuration options.
   */
  public interface Options extends PipelineOptions, MongoDbOptions, PubsubReadOptions {}

  private static class ParseAsDocumentsFn extends DoFn<String, Document> {
    @ProcessElement
    public void processElement(ProcessContext context) {
      context.output(Document.parse(context.element()));
    }
  }

  public static void main(String[] args) {
    Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);

    run(options);
  }

  public static boolean run(Options options) {
    Pipeline pipeline = Pipeline.create(options);

    PCollection<PubsubMessage> messages = null;
    messages =
        pipeline.apply(
            "Read PubSub Events",
            PubsubIO.readMessagesWithAttributes().fromSubscription(options.getInputSubscription()));

    messages
        .apply(
            "PubSub Message",
            ParDo.of(
                new DoFn<PubsubMessage, Document>() {
                  @ProcessElement
                  public void process(ProcessContext c) {

                    String payloadString =
                        new String(c.element().getPayload(), StandardCharsets.UTF_8);
                    Document doc = new Document();
                    Document parsedDoc = doc.parse(payloadString);
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(c.element().getAttributeMap());
                    jsonString = jsonString.replace("mde.", "mde_");
                    Document parsedAttributes = doc.parse(jsonString);
                    parsedDoc.append("mde_type", parsedAttributes.get("mde_type"));
                    parsedDoc.append("mde_tag", parsedAttributes.get("mde_tag"));

                    c.output(parsedDoc);
                  }
                }))
        .apply(
            "Put to MongoDB",
            MongoDbIO.write()
                .withUri(options.getMongoDBUri())
                .withDatabase(options.getDatabase())
                .withCollection(options.getCollection())
                .withIgnoreSSLCertificate(true));

    pipeline.run();
    return true;
  }
}
