package uk.ac.standrews.cs.usp.parser.classifiers;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.mahout.classifier.ClassifierResult;
import org.apache.mahout.classifier.naivebayes.AbstractNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.BayesUtils;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.training.TrainNaiveBayesJob;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.vectorizer.encoders.Dictionary;

import uk.ac.standrews.cs.usp.parser.datastructures.Bucket;
import uk.ac.standrews.cs.usp.parser.datastructures.Record;
import uk.ac.standrews.cs.usp.parser.datastructures.TokenSet;
import uk.ac.standrews.cs.usp.parser.datastructures.code.Code;
import uk.ac.standrews.cs.usp.parser.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.usp.parser.datastructures.vectors.CustomVectorWriter;
import uk.ac.standrews.cs.usp.parser.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.usp.parser.resolver.CodeTriple;
import uk.ac.standrews.cs.usp.parser.resolver.Pair;
import uk.ac.standrews.cs.usp.tools.Utils;
import uk.ac.standrews.cs.usp.tools.configuration.MachineLearningConfiguration;

/**
 * Class has methods for training and classifying using the Mahout Naive Bayes
 * Classifier. The most two important methods are train() and classify().
 * 
 * @author jkc25
 * 
 */
public class NaiveBayesClassifier extends AbstractClassifier {

	private Configuration conf;
	private FileSystem fs;
	private Properties properties = MachineLearningConfiguration
			.getDefaultProperties();
	private NaiveBayesModel model = null;

	/**
	 * Create Naive Bayes classifier with default properties.
	 * 
	 * @param vectorFactory
	 *            This is the vector factory used when creating vectors for each
	 *            record.
	 */
	public NaiveBayesClassifier(final VectorFactory vectorFactory) {

		super(vectorFactory);
		try {
			conf = new Configuration();
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create Naive Bayes classifier with custom properties.
	 * 
	 * @param customProperties
	 *            Customised properties file.
	 * @param vectorFactory
	 *            This is the vector factory used when creating vectors for each
	 *            record.
	 */
	public NaiveBayesClassifier(final String customProperties,
			final VectorFactory vectorFactory) {

		this(vectorFactory);
		MachineLearningConfiguration mlc = new MachineLearningConfiguration();
		properties = mlc.extendDefaultProperties(customProperties);
	}

	@Override
	public void train(final Bucket trainingBucket) throws Exception {

		File trainingVectorsDirectory = new File(
				properties.getProperty("trainingVectorsDirectory"));
		String trainingVectorFile = trainingVectorsDirectory.getAbsolutePath()
				+ "/part-m-00000";
		String naiveBayesModelPath = properties
				.getProperty("naiveBayesModelPath");

		CustomVectorWriter vectorWriter = createVectorWriter(trainingVectorFile);
		writeTrainingVectorsToDisk(trainingBucket, vectorWriter);
		vectorWriter.close();
		writeLabelIndex(trainingBucket);
		model = trainNaiveBayes(trainingVectorFile, naiveBayesModelPath);
	}

	private void writeLabelIndex(final Bucket trainingBucket) {

		Collection<String> seen = new HashSet<String>();

		for (Record record : trainingBucket) {
			Set<CodeTriple> codeTriple = record.getOriginalData()
					.getGoldStandardCodeTriples();
			for (CodeTriple codeTriple2 : codeTriple) {
				String theLabel = codeTriple2.getCode().getCodeAsString();
				if (!seen.contains(theLabel)) {
					Utils.writeToFile(theLabel + ", ", "labelindex.csv", true);
					seen.add(theLabel);
				}
			}

		}

	}

	/**
	 * Writes each vector in the bucket using {@link CustomVectorWriter}
	 * vectorWriter.
	 * 
	 * @param bucket
	 *            {@link Bucket} containing the vectors you want to write.
	 * @param vectorWriter
	 *            {@link CustomVectorWriter} instantiated with output path and
	 *            types.
	 * @throws IOException
	 *             IO Error
	 */
	private void writeTrainingVectorsToDisk(final Bucket bucket,
			final CustomVectorWriter vectorWriter) throws IOException {

		for (Record record : bucket) {

			List<NamedVector> vectors = vectorFactory
					.generateVectorsFromRecord(record);
			for (Vector vector : vectors) {
				vectorWriter.write(vector);
			}

		}
	}

	/**
	 * Creates a {@link CustomVectorWriter} to write vectors to disk.
	 * {@link CustomVectorWriter} writes out {@link Text},
	 * {@link VectorWritable} pairs into the file specified.
	 * 
	 * @param trainingVectorLocation
	 *            location to write vectors to.
	 * @return {@link Writer} with trainingVectorLocation as it's output path
	 *         and {@link Text}, {@link VectorWritable} as its types.
	 * @throws IOException
	 *             I/O Error
	 */
	private CustomVectorWriter createVectorWriter(
			final String trainingVectorLocation) throws IOException {

		SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf,
				new Path(trainingVectorLocation), Text.class,
				VectorWritable.class);
		CustomVectorWriter c = new CustomVectorWriter(writer);
		return c;
	}

	@Override
	public Record classify(final Record record) throws IOException {

		Record classifiedRecord = record;
		Set<CodeTriple> resultSet = new HashSet<>();
		List<NamedVector> vectors = vectorFactory
				.generateVectorsFromRecord(record);

		for (NamedVector namedVector : vectors) {
			resultSet.add(getClassification(record, namedVector));
		}

		classifiedRecord.addAllCodeTriples(resultSet);
		return classifiedRecord;
	}

	private CodeTriple getClassification(final Record record,
			final NamedVector namedVector) throws IOException {

		Dictionary dictionary = buildDictionaryFromLabelMap(properties
				.getProperty("labelindex"));

		AbstractNaiveBayesClassifier classifier = getClassifier();

		Vector resultVector = classifier.classifyFull(namedVector);

		ClassifierResult cr = getClassifierResult(resultVector, dictionary);

		Code code = getCode(resultVector.maxValueIndex());

		double confidence = getConfidence(cr.getLogLikelihood());

		return new CodeTriple(code,
				new TokenSet(record.getCleanedDescription()), confidence);
	}

	/**
	 * Builds a {@link ClassifierResult} from a result vector and the dictionary
	 * of interger/classifications.
	 * 
	 * @param result
	 *            result vector from the Naive Bayes Classifier.
	 * @param dictionary
	 *            Dictionary containing the mapping of Integers to
	 *            classifications (labels)
	 * @return {@link ClassifierResult} containing the details of the
	 *         classification
	 */
	private ClassifierResult getClassifierResult(final Vector result,
			final Dictionary dictionary) {

		int categoryOfClassification = result.maxValueIndex();
		ClassifierResult cr = new ClassifierResult(dictionary.values().get(
				categoryOfClassification));
		return cr;
	}

	/**
	 * Builds a {@link StandardNaiveBayesClassifier}.
	 * 
	 * @return StandardNaiveBayesClassifier built from the model stored in
	 *         "target/naiveBayesModelPath"
	 * @throws IOException
	 *             if the model cannot be read
	 */
	private AbstractNaiveBayesClassifier getClassifier() throws IOException {

		if (model == null) {
			model = getModel();
		}

		AbstractNaiveBayesClassifier classifier = new StandardNaiveBayesClassifier(
				model);

		return classifier;
	}

	/**
	 * Reads and returns the Naive Bayes model from the disk.
	 * 
	 * @return {@link NaiveBayesModel} from disk
	 * @throws IOException
	 *             if the model cannot be read
	 */
	private NaiveBayesModel getModel() throws IOException {

		Configuration conf = new Configuration();
		String modelLocation = "target/naiveBayesModelPath";
		NaiveBayesModel model = NaiveBayesModel.materialize(new Path(
				modelLocation), conf);
		return model;
	}

	/**
	 * Builds a {@link Dictionary} from a label map. Usually used to read the
	 * labelindex written by Mahout {@link BayesUtils}.
	 * 
	 * @param labelMapPath
	 *            location of the label index written by the mahout
	 *            trainNaiveByaes job return dictionary {@link Dictionary}
	 *            containing all label/class mappings
	 */
	private Dictionary buildDictionaryFromLabelMap(final String labelMapPath) {

		Configuration conf = new Configuration();
		Dictionary dictionary = new Dictionary();
		Map<Integer, String> labelMap = BayesUtils.readLabelIndex(conf,
				new Path(labelMapPath));

		for (int i = 0; i < labelMap.size(); i++) {
			dictionary.intern(labelMap.get(i).trim());
		}

		return dictionary;
	}

	/**
	 * TODO - finish writing this method Generates a confidence value from a log
	 * likelihood.
	 * 
	 * @param logLikelihood
	 * @return confidence score
	 */
	private double getConfidence(final double logLikelihood) {

		// TODO FIXME return real confidence... might be hard as this is Bayes
		// double confidence = Math.pow(logLikelihood, 2);
		// confidence = Math.sqrt(confidence);

		return 0.5;
	}

	/**
	 * Gets an Occ Code from the {@link CodeFactory}.
	 * 
	 * @param resultOfClassification
	 *            the string representation of the classification code
	 * @return Code code representation of the resultOfClassification
	 * @throws IOException
	 *             if resultOfClassification is not a valid code or CodeFactory
	 *             cannot read the code list.
	 */
	private Code getCode(final int resultOfClassification) throws IOException {

		return CodeFactory.getInstance().getCode(resultOfClassification);

	}

	/**
	 * Trains a Naive Bayes model with the vectors supplied.
	 * 
	 * @param trainingVectorLocation
	 * @param naiveBayesModelPath
	 * @return model {@link NaiveBayesModel} the trained model
	 * @throws Exception
	 */
	private NaiveBayesModel trainNaiveBayes(
			final String trainingVectorLocation,
			final String naiveBayesModelPath) throws Exception {

		// -i = training vector location, -el = extract label index, -li- place
		// to store labelindex
		// -o = model output path, -ow = overwrite existing vectors
		String[] args = new String[] { "-i", trainingVectorLocation, "-o",
				naiveBayesModelPath, "-el", "-li",
				properties.getProperty("labelindex"), "-ow" };

		TrainNaiveBayesJob.main(args);

		return getModel();
	}

	@Override
	public void getModelFromDefaultLocation() {

		try {
			model = getModel();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Pair<Code, Double> classify(final TokenSet tokenSet)
			throws IOException {

		Pair<Code, Double> pair;
		NamedVector vector = vectorFactory.createNamedVectorFromString(
				tokenSet.toString(), "unknown");
		int classificationID = getClassifier().classifyFull(vector)
				.maxValueIndex();
		Code code = CodeFactory.getInstance().getCode(classificationID);
		// double confidence =
		// Math.exp(getClassifier().logLikelihood(classificationID, vector));
		// THIS WONT WORK - Need to fudge it
		double confidence = 0.1;

		pair = new Pair<>(code, confidence);
		return pair;
	}
}
