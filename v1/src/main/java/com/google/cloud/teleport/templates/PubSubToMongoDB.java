package com.google.cloud.teleport.templates;

import com.google.cloud.teleport.metadata.Template;
import com.google.cloud.teleport.metadata.TemplateCategory;
import com.google.cloud.teleport.metadata.TemplateParameter;
import com.google.cloud.teleport.templates.PubSubToMongoDB.Options;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.io.mongodb.MongoDbIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.StreamingOptions;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.DoFn;//import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;//import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.PCollection;
import org.bson.Document;

@Template(
        name = "Cloud_PubSub_to_MongoDB_Updated",
        category = TemplateCategory.STREAMING,
        displayName = "Pub/Sub to Avro Files on Cloud Storage",
        description = "The Pub/Sub to Avro files on Cloud Storage template is a streaming pipeline that reads data from a Pub/Sub "
                        + "topic and writes Avro files into the specified Cloud Storage bucket.",
        optionsClass = Options.class,
        skipOptions = "inputSubscription",
        documentation = "https://cloud.google.com/dataflow/docs/guides/templates/provided/pubsub-to-avro",
        contactInformation = "https://cloud.google.com/support",
        requirements = {"The input Pub/Sub topic must exist prior to pipeline execution."},
        streaming = true)
public class PubSubToMongoDB {

    public interface Options
            extends PipelineOptions, StreamingOptions {
        @TemplateParameter.PubsubSubscription(
                order = 1,
                description = "Pub/Sub input subscription",
                helpText =
                        "Pub/Sub subscription to read the input from, in the format of"
                                + " 'projects/your-project-id/subscriptions/your-subscription-name'",
                example = "projects/your-project-id/subscriptions/your-subscription-name")
        ValueProvider<String> getInputSubscription();

        void setInputSubscription(ValueProvider<String> value);

    }

    public static void main(String[] args) {

        Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);
        options.setStreaming(true);

        run(options);
    }

    public static PipelineResult run(Options options) {
        // Create the pipeline
        Pipeline pipeline = Pipeline.create(options);

        PCollection<PubsubMessage> messages = null;
        messages =
                pipeline.apply(
                        "Read PubSub Events",
                        PubsubIO.readMessagesWithAttributes()
                                .fromSubscription("projects/mde-official-ce-demo/subscriptions/mongodb-writer-subscription-json"));


        messages.apply(
                "PubSub Message",
                ParDo.of(
                        new DoFn<PubsubMessage, Document>() {
                            @ProcessElement
                            public void process(ProcessContext c) {

                                //String payloadString = new String(pubSubMessage.getPayload(), StandardCharsets.UTF_8);
                                String payloadString = new String(c.element().getPayload(), StandardCharsets.UTF_8);
                                Document doc = new Document();
                                Document parsedDoc = doc.parse(payloadString);
                                Gson gson = new Gson();
                                String jsonString = gson.toJson(c.element().getAttributeMap());
                                jsonString = jsonString.replace("mde.", "mde_");
                                Document parsedAttributes = doc.parse(jsonString);
                                parsedDoc.append("mde_type", parsedAttributes.get("mde_type"));
                                System.out.println(parsedDoc.get("embeddedMetadata"));
                                c.output(parsedDoc);

                            }
                        })).apply(
                "Put to MongoDB",
                MongoDbIO.write()
                        .withUri("mongodb+srv://venkatesh:ashwin123@freetier.kxcgwh2.mongodb.net")
                        .withDatabase("pbdb03")
                        .withCollection("pbcol03")
                        .withIgnoreSSLCertificate(true));
        return pipeline.run();


    }
}
