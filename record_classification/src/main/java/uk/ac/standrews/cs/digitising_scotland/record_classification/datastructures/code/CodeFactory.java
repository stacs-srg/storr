package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * Factory for the creation of {@link Code} objects.
 * Codes can only be created if they are within a pre-determined list of codes, this prevents
 * malformed codes being used for classification.
 *
 * @author jkc25, frjd2
 */
public final class CodeFactory {

    /** The Constant INSTANCE. */
    private static final CodeFactory INSTANCE = new CodeFactory();

    /** The code map. */
    private Map<String, Code> codeMap = new HashMap<String, Code>();

    /** The id to code map. */
    private Map<Integer, Code> idToCodeMap = new HashMap<Integer, Code>();

    /** The do once. */
    private boolean doOnce = true;

    /** The input file. */
    private File inputFile;

    /** The current max id. */
    private int currentMaxID;

    /** The Constant ENCODING. */
    private static final String ENCODING = "UTF-8";

    /** The code map null counter. */
    private int codeMapNullCounter = 0;

    /**
     * Instantiates a new code factory.
     */
    private CodeFactory() {

        Properties properties = MachineLearningConfiguration.getDefaultProperties();
        String property = properties.getProperty("codeDictionaryFile");
        String path = System.getProperty("user.dir") + "/" + property;
        inputFile = new File(path);

        if (doOnce) {
            tryInitCodeMap();
        }

        MachineLearningConfiguration.getDefaultProperties().setProperty("numCategories", String.valueOf(codeMap.size()));
    }

    /**
     * Returns the single instance of this class that is being used.
     *
     * @return The instance of this class
     */
    public static CodeFactory getInstance() {

        return INSTANCE;
    }

    /**
     * Gets a code object from the code mapping in memory.
     *
     * @param code String representation of the {@link Code}.
     * @return {@link Code} with code as String and description as String.
     */
    public Code getCode(final String code) {

        if (doOnce) {
            tryInitCodeMap();
        }
        return tryGetCodeFromMap(code);
    }

    /**
     * Returns the code that this id is mapped to.
     * @param id associated with mapped code
     * @return Code assosciated with this id
     */
    public Code getCode(final Integer id) {

        if (doOnce) {
            tryInitCodeMap();
        }
        return idToCodeMap.get(id);
    }

    /**
     * Try init code map.
     */
    private void tryInitCodeMap() {

        try {
            initCodeMap();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (CodeNotValidException e) {
            e.printStackTrace();
        }
    }

    /**
     * Try get code from map.
     *
     * @param code the code
     * @return the code
     */
    private Code tryGetCodeFromMap(final String code) {

        Code codeFromMap = null;
        try {
            codeFromMap = getCodeFromMap(code);
        }
        catch (CodeNotValidException e) {
            e.printStackTrace();
        }
        return codeFromMap;
    }

    /**
     * Gets the code from map.
     *
     * @param code the code
     * @return the code from map
     * @throws CodeNotValidException the code not valid exception
     */
    protected Code getCodeFromMap(final String code) throws CodeNotValidException {

        //FIXME Remember, we've hacked this to perform scale testing. Need to check with Lee about 0's on end of codes.
        Code codeFromMap = codeMap.get(code);
        if (codeFromMap == null) {
            codeFromMap = codeMap.get(code.substring(0, code.length() - 1));
            if (codeFromMap != null) {
                setCodeMapNullCounter(getCodeMapNullCounter() + 1);
            }
        }
        if (codeFromMap == null) { throw new CodeNotValidException(code + " is not a valid code, or is not in the code dictionary(" + inputFile.getAbsolutePath() + ")"); }

        return codeFromMap;
    }

    /**
     * Inits the code map.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException the code not valid exception
     */
    private void initCodeMap() throws IOException, CodeNotValidException {

        doOnce = false;

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), ENCODING));
        String line;

        boolean isCoDCode = isCauseOfDeath();

        if (isCoDCode) {
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split("\t");
                String codeFromFile = splitLine[0].trim();
                String descriptionFromFile = splitLine[1].trim();

                putCoDCodeInMap(codeFromFile, descriptionFromFile);

            }
        }
        else {

            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split("\t");
                String codeFromFile = splitLine[0].trim();
                String descriptionFromFile = splitLine[1].trim();

                putOccCodeInMap(codeFromFile, descriptionFromFile);

            }

        }

        br.close();

    }

    /**
     * Returns the total number of output classes based on the size of the code map.
     * @return the number of output classes in the codeMap.
     */
    public int getNumberOfOutputClasses() {

        return codeMap.size();
    }

    /**
     * Checks if is cause of death.
     *
     * @return true, if is cause of death
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private boolean isCauseOfDeath() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), ENCODING));
        String firstLine = br.readLine();
        br.close();
        if (firstLine == null) { return false; }
        if (firstLine.substring(0, 1).matches("[a-zA-Z]")) { return true; }

        return false;
    }

    /**
     * Loads a <b>new</b> set of codes in to the code factory.
     * @param codeFile File with set of tab separated codes and descriptions. 1 per line.
     */
    public void loadDictionary(final File codeFile) {

        resetParams();
        inputFile = codeFile;
        try {
            initCodeMap();
            MachineLearningConfiguration.getDefaultProperties().setProperty("numCategories", String.valueOf(codeMap.size()));
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (CodeNotValidException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Resets the internal code map to an empty map as well as resetting the current max ID count.
     * Should be used for testing purposes only.
     */
    public void resetParams() {

        codeMap = new HashMap<String, Code>();
        currentMaxID = 0;
        idToCodeMap = new HashMap<Integer, Code>();
    }

    /**
     * Puts a cause of death code in the map.
     *
     * @param codeFromFile the code from file
     * @param descriptionFromFile the description from file
     * @throws CodeNotValidException the code not valid exception
     */
    private void putCoDCodeInMap(final String codeFromFile, final String descriptionFromFile) throws CodeNotValidException {

        if (codeMap.get(codeFromFile) == null) {
            CoDCode code = new CoDCode(codeFromFile, descriptionFromFile, currentMaxID);
            codeMap.put(codeFromFile, code);
            idToCodeMap.put(currentMaxID++, code);
        }
    }

    /**
     * Puts an occupation code in the map.
     *
     * @param codeFromFile the code from file
     * @param descriptionFromFile the description from file
     * @throws CodeNotValidException the code not valid exception
     */
    private void putOccCodeInMap(final String codeFromFile, final String descriptionFromFile) throws CodeNotValidException {

        if (codeMap.get(codeFromFile) == null) {
            OccCode code = new OccCode(codeFromFile, descriptionFromFile, currentMaxID);
            codeMap.put(codeFromFile, code);
            idToCodeMap.put(currentMaxID++, code);

        }
    }

    /**
     * Gets the code map null counter.
     *
     * @return the code map null counter
     */
    public int getCodeMapNullCounter() {

        return codeMapNullCounter;
    }

    /**
     * Sets the code map null counter.
     *
     * @param codeMapNullCounter the new code map null counter
     */
    public void setCodeMapNullCounter(final int codeMapNullCounter) {

        this.codeMapNullCounter = codeMapNullCounter;
    }

}
