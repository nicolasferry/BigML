/**
 * This file is part of BigML [ http://cloudml.org ]
 *
 * Copyright (C) ${year} - SINTEF Digital
 * Contact: Nicolas Ferry <nicolas.ferry@sintef.no>
 *
 * Module: bigml
 *
 * BigML is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * BigML is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with BigML. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package model;

import bigml.*;
import bigml.Port;
import com.sun.org.apache.xml.internal.serializer.OutputPropertyUtils;
import org.cloudml.core.*;
import org.cloudml.core.actions.StandardLibrary;
import org.cloudml.facade.CloudML;
import org.cloudml.facade.Factory;
import org.cloudml.facade.commands.CloudMlCommand;
import org.cloudml.facade.commands.CommandFactory;
import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.defer.KDefer;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;

import java.util.Objects;


/**
 * Created by ferrynico on 09/01/2017.
 */
public class temp {

    public static final long BASE_UNIVERSE = 0;
    public static final long BASE_TIME = 0;

    private static BigmlModel model = new BigmlModel(DataManagerBuilder.buildDefault());
    private static Deployment deploy;

    public static void main(String[] args){

        model.connect(o -> {
            BigmlView baseView= model.universe(BASE_UNIVERSE).time(BASE_TIME);

            DataModel m=baseView.createDataModel();
            m.setName("MC-Suite");

            //SensorSimulator
            ComponentType c=baseView.createComponentType();
            c.setName("REST_Stream");
            c.setDeploymentModel("/Users/ferrynico/Documents/Code/demo/Bigml/simulator.json");
            PortType pSimu=baseView.createPortType();
            pSimu.setName("simuPort");
            OutputPort outSimu=baseView.createOutputPort();
            outSimu.setDeploymentModelMapping("simulator1/simulator93541260");
            outSimu.setName("simu_output1");
            outSimu.addType(pSimu);
            InputPort inSimu=baseView.createInputPort();
            inSimu.setName("simu_input1");
            inSimu.addType(pSimu);
            DataSource s=baseView.createDataSource();
            s.setName("REST_Stream_instance");
            s.addType(c);
            s.addInputPorts(inSimu);
            s.addOutputPorts(outSimu);
            m.addComponents(s);

            //Storm
            ComponentType cStorm=baseView.createComponentType();
            cStorm.setDeploymentModel("/Users/ferrynico/Documents/Code/demo/Bigml/storm-maksym.json");
            cStorm.setName("StormTopology");
            PortType pStorm=baseView.createPortType();
            pSimu.setName("stormPort");
            OutputPort outStorm=baseView.createOutputPort();
            outStorm.setDeploymentModelMapping("nimbus-i/nimbusPortProvided-i");
            outStorm.setName("storm_output1");
            outStorm.addType(pStorm);
            InputPort inStorm=baseView.createInputPort();
            inStorm.setDeploymentModelMapping("nimbus-i/zookeeperPortRequired-i");
            inStorm.setName("storm_input1");
            inStorm.addType(pStorm);
            ManagedComponent cStormInstance=baseView.createDataProcessor();
            cStormInstance.setName("StormTopology1");
            cStormInstance.addType(cStorm);
            cStormInstance.addInputPorts(inStorm);
            cStormInstance.addOutputPorts(outStorm);
            m.addComponents(cStormInstance);

            //Storm composite assembly
            ComponentType spout=baseView.createComponentType();
            spout.setName("sensorSimulator");



            //Kafka
            ComponentType kafka=baseView.createComponentType();
            kafka.setDeploymentModel("/Users/ferrynico/Documents/Code/demo/Bigml/kafka.json");
            kafka.setName("kafka");
            PortType pKafka=baseView.createPortType();
            pKafka.setName("kafkaPort");
            OutputPort outKafka = baseView.createOutputPort();
            outKafka.setName("Kafka_output1");
            outKafka.addType(pKafka);
            InputPort inKafka=baseView.createInputPort();
            inKafka.setDeploymentModelMapping("kafka1/kafka93541260");
            inKafka.setName("Kafka_input1");
            inKafka.addType(pKafka);
            MessageQueue mq=baseView.createMessageQueue();
            mq.setName("kafka1");
            mq.addType(kafka);
            mq.addInputPorts(inKafka);
            mq.addOutputPorts(outKafka);
            m.addComponents(mq);

            //CouchDB
            ComponentType CouchDB = baseView.createComponentType();
            CouchDB.setDeploymentModel("/Users/ferrynico/Documents/Code/demo/Bigml/couchdb.json");
            CouchDB.setName("CouchDB");
            PortType pcouch=baseView.createPortType();
            pcouch.setName("couchPort");
            pcouch.setProtocol("HTTP");
            OutputPort outCouch=baseView.createOutputPort();
            outCouch.setName("Couch_output1");
            outCouch.addType(pcouch);
            InputPort inCouch=baseView.createInputPort();
            inCouch.setDeploymentModelMapping("couchdb1/couchdb93541260");
            inCouch.setName("Couch_input1");
            inCouch.addType(pcouch);
            StorageSystem couch=baseView.createStorageSystem();
            couch.setName("couch1");
            couch.addType(CouchDB);
            couch.addInputPorts(inCouch);
            couch.addOutputPorts(outCouch);
            m.addComponents(couch);

            //pipelines
            PipelineType simu_storm=baseView.createPipelineType();
            simu_storm.setName("simu->storm");
            Pipeline simu_storm1=baseView.createPipeline();
            simu_storm1.setName("simu->storm1");
            simu_storm1.addType(simu_storm);
            simu_storm1.addInputPort(outSimu);
            simu_storm1.addOutputPort(inStorm);
            m.addPipelines(simu_storm1);

            PipelineType storm_kafka=baseView.createPipelineType();
            storm_kafka.setName("storm->kafka");
            Pipeline storm_kafka1=baseView.createPipeline();
            storm_kafka1.setName("storm->kafka1");
            storm_kafka1.addType(storm_kafka);
            storm_kafka1.addInputPort(outStorm);
            storm_kafka1.addOutputPort(inKafka);
            m.addPipelines(storm_kafka1);

            PipelineType storm_couch=baseView.createPipelineType();
            storm_couch.setName("storm->couch");
            Pipeline storm_couch1=baseView.createPipeline();
            storm_couch1.setName("storm->couch1");
            storm_couch1.addType(storm_couch);
            storm_couch1.addInputPort(outStorm);
            storm_couch1.addOutputPort(inCouch);
            m.addPipelines(storm_couch1);

            m.traversal()
                    .traverseQuery("components")
                    .traverseQuery("type")
                    .then(resultSet -> deploymentModel(resultSet));

            m.traversal()
                    .traverseQuery("pipelines")
                    .then(resultSet -> addMissingRelationships(resultSet));

            baseView.json().save(m, new KCallback<String>() {
                @Override
                public void on(String savedFullView) {
                    System.out.println("FullModel:" + savedFullView);
                }
            });
        });

    }

    //For classical deployment
    //Takes all component and merge their deployment models
    private static void deploymentModel(Object[] extractedObjects){
        CloudML cml = Factory.getInstance().getCloudML();
        for(Object o: extractedObjects) {
            System.out.println(((ComponentType) o).getDeploymentModel());
            deploy=cml.merge(((ComponentType) o).getDeploymentModel());
        }
        /*CommandFactory fcommand = new CommandFactory();
        CloudMlCommand d=fcommand.deploy();
        System.out.println("Fire deployment");
        cml.fireAndWait(d);*/
    }

    //Add the missing relationships
    private static void addMissingRelationships(Object[] extractedObjects){
        KDefer d=model.defer();
        for(Object o: extractedObjects) {
            ((Pipeline)o).getInputPort(d.waitResult());
            ((Pipeline)o).getOutputPort(d.waitResult());
            d.then(results -> {
                for(int i=0; i<results.length-1;i+=2){//crappy code here but I am lazy
                    Object[] t=(Object[]) results[0];//input
                    Object[] t1=(Object[]) results[1];//output

                    String[] path=((Port) t[0]).getDeploymentModelMapping().split("/");//format input component/port
                    String[] path2=((Port) t1[0]).getDeploymentModelMapping().split("/");//format output component/port

                    String sourceComp=path[0];
                    String sourcePort=path[1];
                    String destComp=path2[0];
                    String destPort=path2[1];

                    InternalComponentInstance compSource=deploy.getComponentInstances().onlyInternals().firstNamed(sourceComp);
                    InternalComponentInstance compDest=deploy.getComponentInstances().onlyInternals().firstNamed(destComp);
                    ProvidedPortInstance prov=compSource.getProvidedPorts().firstNamed(sourcePort);
                    RequiredPortInstance req=compDest.getRequiredPorts().firstNamed(destPort);
                    Relationship r=new Relationship(((Pipeline)o).getName(),req.getType(),prov.getType());
                    RelationshipInstance ri=new RelationshipInstance(((Pipeline)o).getName(),req,prov,r);
                    deploy.getRelationships().add(r);
                    deploy.getRelationshipInstances().add(ri);
                }
            });
        }
    }

}
