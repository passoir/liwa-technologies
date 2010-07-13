/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package CPESetup;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.apache.uima.ResourceSpecifierFactory;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CasConsumer;
import org.apache.uima.collection.CasConsumerDescription;
import org.apache.uima.collection.CollectionProcessingManager;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.collection.StatusCallbackListener;
import org.apache.uima.examples.cpe.InlineXmlCasConsumer;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.apache.uima.util.ProcessTrace;
import org.apache.uima.util.ProcessTraceEvent;
import org.apache.uima.util.XMLInputSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import collectionreader.TimesCollectionReader;
import de.l3s.database.LiwaDatabase;
import de.tudarmstadt.ukp.dkpro.core.util.DKProContext;
import de.tudarmstadt.ukp.dkpro.core.util.UimaUtils;

import sun.util.calendar.JulianCalendar;

/**
 * Main Class that runs the Collection Processing Manager (CPM). This class
 * reads descriptor files and initiailizes the following components:
 * <ol>
 * <li>CollectionReader</li>
 * <li>Analysis Engine</li>
 * <li>CAS Consumer</li>
 * </ol>
 * <br>
 * It also registers a callback listener with the CPM, which will print progress
 * and statistics to System.out. <br>
 * Command lines arguments for the run are :
 * <ol>
 * <li>args[0] : CollectionReader descriptor file</li>
 * <li>args[1] : CAS Consumer descriptor file.</li>
 * <li>args[2] : AnnotationPrinter descriptor file</li>
 * </ol>
 * <br>
 * Example : <br>
 * java -cp &lt; all jar files needed &gt;
 * org.apache.uima.example.cpe.SimpleRunCPE
 * descriptors/collection_reader/FileSystemCollectionReader.xml
 * descriptors/analysis_engine/PersonTitleAnnotator.xml
 * descriptors/cas_consumer/XmiWrtierCasConsumer.xml
 * 
 */
public class ARCCollectionParserCPMPharos extends Thread {
	/**
	 * The Collection Processing Manager instance that coordinates the
	 * processing.
	 */
	public CollectionProcessingManager mCPM;

	/**
	 * Start time of the processing - used to compute elapsed time.
	 */
	private long mStartTime;

	// public static int collectionEntityCount = 0;
	/**
	 * Constructor for the class.
	 * 
	 * @param args
	 *            command line arguments into the program - see class
	 *            description
	 */
	private static String ColectionReaderDesc = "desc/ARCReaderDescriptor.xml";
	// private static String ColectionReaderDesc =
	// System.getenv("UIMA_HOME")+"/examples/descriptors/collection_reader/FileSystemCollectionReader.xml";
	// private static String AnnotatorDesc = "desc/timesAggregate.xml";
	private static String AnnotatorDesc = "desc/archiveAggregate.xml";
	// private static String AnnotatorDesc = "desc/archiveAggregateNoDB.xml";

	// private static String AnnotatorDesc =
	// System.getenv("UIMA_HOME")+"/examples/descriptors/analysis_engine/SimpleTokenAndSentenceAnnotator.xml";

	// private static String AnnotatorDesc =
	// System.getenv("UIMA_HOME")+"/addons/dextract/org.tud.sir.uima.dextract/desc/analysis_engine/JTokenizerTreeTaggerAggregate.xml";

	// private static String AnnotatorDesc =
	// System.getenv("UIMA_HOME")+"/examples/descriptors/analysis_engine/XmlDetagger.xml";
	// private static String CasConsumerDesc =
	// System.getenv("UIMA_HOME")+"/examples/descriptors/cas_consumer/XmiWriterCasConsumer.xml";
	// private static String CasConsumerDesc =
	// "//home/tereza/uimatools/DKProUGD_101/workspace/desc/consumer/AnnotationWriter.xml";
	// System.getProperty("DKproWorkspace")+"/desc/consumer/AnnotationWriter.xml";

	//

	private static String CasConsumerDesc = "desc/XmiWriterCasConsumer.xml";

	// private static String CasConsumerDesc =
	// "workspace/desc/consumer/XmiWriterCasConsumer.xml";

	public ARCCollectionParserCPMPharos(String args[]) throws UIMAException,
			IOException {
		mStartTime = System.currentTimeMillis();

		//default folders
		String InputDir = "testarcs";
		String OutputDir = "testprocesseddata";
		int maxArchives = 100;
		
		// check command line args
		if (args.length < 2) {
			printUsageMessage();
			//args = new String[2];

		//	args[0] = "testarcs";
			//args[0] = "/liwa/tereza/liwa/ukgov/";
			
			//args[0] = "/data/iofciu/liwaData/ukgov";
			//args[1] = "testprocesseddata";
			
			
			// args[0] = "bla";

			// args[0] = "/media/fs/data/times/XML";
			// http://www.kbs.uni-hannover.de/~iofciu/liwatest/storage/main/get_archive_files/1

			// args[1] = "/home/iofciu/data/processeddata";
			// System.exit(1);

		}else{
			
			InputDir = args[0];
			maxArchives = Integer.parseInt(args[1]);
		}

		
		// create components from their descriptors

		// Collection Reader
		System.out.println("Initializing Collection Reader");

		ResourceSpecifier colReaderSpecifier = UIMAFramework.getXMLParser()
				.parseCollectionReaderDescription(
						new XMLInputSource(ColectionReaderDesc));

		// colReaderSpecifier.setAttributeValue("InputDirectory", InputDir);

		CollectionReader collectionReader = UIMAFramework
				.produceCollectionReader(colReaderSpecifier);
		// collectionReader = new TimesCollectionReader();
		System.out.println("setting input directory to: " + InputDir);

		collectionReader.setConfigParameterValue("InputDirectory", InputDir);
		collectionReader.setConfigParameterValue("MaxArchives", maxArchives);
		collectionReader.reconfigure();

		System.out.println((collectionReader.getProgress())[0].getTotal());
		// collectionEntityCount =
		// (int)(collectionReader.getProgress())[0].getTotal();

	//	UimaUtils.setDataPath("/home/tereza/uimatools/TreeTagger");
		UimaUtils.setDataPath("/data/iofciu/uimatools/TreeTagger");
		// AnalysisEngines

		// to be uncommented
		System.out.println("Initializing AnalysisEngine");
		ResourceSpecifier aeSpecifier = UIMAFramework.getXMLParser()
				.parseResourceSpecifier(new XMLInputSource(AnnotatorDesc));
		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aeSpecifier);

		// ae.setConfigParameterValue("XmlTagContainingText", "ARTICLETEXT");

		// CAS Consumer
		System.out.println("Initializing CAS Consumer");

		// ResourceSpecifier consumerSpecifier =
		// UIMAFramework.getXMLParser().parseCasConsumerDescription(new
		// XMLInputSource(CasConsumerDesc));
		CasConsumerDescription casConsumerDescription = UIMAFramework
				.getXMLParser().parseCasConsumerDescription(
						new XMLInputSource(CasConsumerDesc));

		ConfigurationParameterSettings cps = casConsumerDescription
				.getMetaData().getConfigurationParameterSettings();
		cps.setParameterValue(InlineXmlCasConsumer.PARAM_OUTPUTDIR, OutputDir);

		CasConsumer casConsumer = UIMAFramework
				.produceCasConsumer(casConsumerDescription);

		// create a new Collection Processing Manager
		mCPM = UIMAFramework.newCollectionProcessingManager();

		// Register AE and CAS Consumer with the CPM
		// mCPM.setSerialProcessingRequired(true);

		// to be uncommented
		mCPM.setAnalysisEngine(ae);

		mCPM.addCasConsumer(casConsumer);
		// Create and register a Status Callback Listener
		mCPM.addStatusCallbackListener(new StatusCallbackListenerImpl());

		// Finish setup
		mCPM.setPauseOnException(false);

		// Start Processing (in batches of 10, just for testing purposes)
		mCPM.process(collectionReader);

	}

	/**
   * 
   */
	private static void printUsageMessage() {
		System.out.println(" Arguments to the program are as follows : \n"
				+ "args[0] : InputDirectory \n " + "args[1] : Number of archives to be processed. Program will run with default: arcstest and 100");
	}

	/**
	 * main class.
	 * 
	 * @param args
	 *            Command line arguments - see class description
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws UIMAException, IOException,
			InterruptedException {
		ARCCollectionParserCPMPharos cpm = new ARCCollectionParserCPMPharos(args);
		// colCPM.processCollection(10);
		// Thread myThread = Thread.currentThread();
		// myThread.join();

	}

	/**
	 * Callback Listener. Receives event notifications from CPM.
	 * 
	 * 
	 */
	public class StatusCallbackListenerImpl implements StatusCallbackListener {
		int entityCount = 0;
		long size = 0;

		/**
		 * Called when the initialization is completed.
		 * 
		 * @see org.apache.uima.collection.processing.StatusCallbackListener#initializationComplete()
		 */
		public void initializationComplete() {
			System.out.println("CPM Initialization Complete");
		}

		/**
		 * Called when the batchProcessing is completed.
		 * 
		 * @see org.apache.uima.collection.processing.StatusCallbackListener#batchProcessComplete()
		 * 
		 */
		public void batchProcessComplete() {
			System.out.print("Completed " + entityCount + " documents");
			if (size > 0) {
				System.out.print("; " + size + " characters");
			}
			System.out.println();
			long elapsedTime = System.currentTimeMillis() - mStartTime;
			System.out.println("Time Elapsed : " + elapsedTime + " ms ");
		}

		/**
		 * Called when the collection processing is completed.
		 * 
		 * @see org.apache.uima.collection.processing.StatusCallbackListener#collectionProcessComplete()
		 */
		public void collectionProcessComplete() {
			System.out.print("Completed " + entityCount + " documents");
			// if (size > 0) {
			// System.out.print("; " + size + " characters");
			// }
			System.out.println();
			long elapsedTime = System.currentTimeMillis() - mStartTime;
			System.out.println("Time Elapsed : " + elapsedTime + " ms ");
			System.out
					.println("\n\n ------------------ PERFORMANCE REPORT ------------------\n");
			System.out.println(mCPM.getPerformanceReport().toString());
			System.out.println("active threads: " + Thread.activeCount());
			// Thread allThreads[] = new Thread[Thread.activeCount()];

			System.exit(0);
			// ExecutorService service = Executors.newCachedThreadPool();
			// service.shutdown();
		}

		/**
		 * Called when the CPM is paused.
		 * 
		 * @see org.apache.uima.collection.processing.StatusCallbackListener#paused()
		 */
		public void paused() {
			System.out.println("Paused");
		}

		/**
		 * Called when the CPM is resumed after a pause.
		 * 
		 * @see org.apache.uima.collection.processing.StatusCallbackListener#resumed()
		 */
		public void resumed() {
			System.out.println("Resumed");
		}

		/**
		 * Called when the CPM is stopped abruptly due to errors.
		 * 
		 * @see org.apache.uima.collection.processing.StatusCallbackListener#aborted()
		 */
		public void aborted() {
			System.out.println("Aborted");
		}

		/**
		 * Called when the processing of a Document is completed. <br>
		 * The process status can be looked at and corresponding actions taken.
		 * 
		 * @param aCas
		 *            CAS corresponding to the completed processing
		 * @param aStatus
		 *            EntityProcessStatus that holds the status of all the
		 *            events for aEntity
		 */
		public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
			if (aStatus.isException()) {
				List exceptions = aStatus.getExceptions();
				for (int i = 0; i < exceptions.size(); i++) {
					((Throwable) exceptions.get(i)).printStackTrace();
				}
				return;
			}
			entityCount++;
			// String docText = aCas.getDocumentText();
			// if(entityCount % 1000 == 0)
			// System.out.println("Processed doc: "+ entityCount);
			// if (docText != null) {
			// size += docText.length();

			// }
		}
	}

}