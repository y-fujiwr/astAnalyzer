package astStringGenerator;

public class Config {
	public static String target = null;
	public static final String DATASET_FILE = "dataset.txt";
	public static final String SOURCE_DIR = "./googlejam/"; // ./data/bcb/external/function_snippets/";
	public static final String DATA_FILE_DIR = "../data/bcb/";
	public static final String TRAIN_FILE_NAME = "train.csv";
	public static final String TEST_FILE_NAME = "test.csv";
	public static final String DEV_FILE_NAME = "dev.csv";
	public static final int JAVA = 0;
	public static final int CPP = 1;
	public static int lang = JAVA;
	public static String charset = "UTF-8";
	public static String virtualClassName = null;
}