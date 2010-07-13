/**
 * 
 */
package CPESetup;

import java.io.IOException;

import org.apache.uima.collection.impl.metadata.cpe.CpeDescriptionImpl;
import org.apache.uima.collection.impl.metadata.cpe.CpeDescriptorFactory;
import org.apache.uima.collection.metadata.CpeDescription;
import org.apache.uima.collection.metadata.CpeDescriptorException;
import org.apache.uima.collection.metadata.CpeIntegratedCasProcessor;
import org.xml.sax.SAXException;

/**
 * @author tereza
 * 
 */
public class CPEConfigure {

	public static void main(String[] args) {
		try {
			Configure();
		} catch (CpeDescriptorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String Configure() throws CpeDescriptorException,
			SAXException, IOException {

		// Creates descriptor with default settings
		CpeDescription cpe = CpeDescriptorFactory.produceDescriptor();

		// Add CollectionReader
		cpe.addCollectionReader("[descriptor]");

		// Add CasInitializer (deprecated)
		cpe.addCasInitializer("<cas initializer descriptor>");

		// Provide the number of CASes the CPE will use

		// cpe.setCasPoolSize(2);

		// Define and add Analysis Engine
		// CpeIntegratedCasProcessor personTitleProcessor =
		// CpeDescriptorFactory.produceCasProcessor (null“Person”);
		CpeIntegratedCasProcessor personTitleProcessor = CpeDescriptorFactory
				.produceCasProcessor("Person");
		// Provide descriptor for the Analysis Engine

		personTitleProcessor.setDescriptor("[descriptor]");

		// Continue, despite errors and skip bad Cas
		personTitleProcessor.setActionOnMaxError("terminate");

		// Increase amount of time in ms the CPE waits for response
		// from this Analysis Engine
		personTitleProcessor.setTimeout(100000);

		// Add Analysis Engine to the descriptor
		cpe.addCasProcessor(personTitleProcessor);

		// Define and add CAS Consumer
		CpeIntegratedCasProcessor consumerProcessor = CpeDescriptorFactory
				.produceCasProcessor("Printer");
		consumerProcessor.setDescriptor("[descriptor]");

		// Define batch size
		consumerProcessor.setBatchSize(100);

		// Terminate CPE on max errors
		personTitleProcessor.setActionOnMaxError("terminate");

		// Add CAS Consumer to the descriptor
		cpe.addCasProcessor(consumerProcessor);

		// Add Checkpoint file and define checkpoint frequency (ms)
		cpe.setCheckpoint("tmp/checkpoint.dat", 3000);

		// Plug in custom timer class used for timing events
		cpe.setTimer("org.apache.uima.internal.util.JavaTimer");

		// Define number of documents to process
		cpe.setNumToProcess(1000);

		// Dump the descriptor to the System.out
		((CpeDescriptionImpl) cpe).toXML(System.out);

		return "";

	}
}
