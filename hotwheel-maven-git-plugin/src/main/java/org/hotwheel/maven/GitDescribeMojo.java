package org.hotwheel.maven;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * The GitDescribe Maven plugin entry point.
 *
 * <table> <caption>Makes use of the following properties.</caption> <tr>
 * <th>Property</th> <th>Description</th> <th>Default Value</th> </tr>
 *
 * <tr> <td>git-describe-prop-name</td> <td></td> <td>git.describe</td> </tr>
 * <tr> <td>makeUseOfJavaFile</td> <td></td> <td>false</td> </tr> <tr>
 * <td>classPathToJavaFile</td> <td></td> <td>No</td> </tr> <tr>
 * <td>constantToChange</td> <td></td> <td>No</td> </tr> </table>
 *
 * @author jasonbruwer
 * @since v1.0
 */
@Mojo(name = "git_describe", defaultPhase = LifecyclePhase.INITIALIZE, requiresProject = true, threadSafe = true)
public class GitDescribeMojo extends AbstractMojo {

    private static final String DEFAULT_GIT_DESCRIBE = "git.describe";
    private static final String PREFIX = "src/main/java/";

    @Parameter(property = "gitDescribe", defaultValue = DEFAULT_GIT_DESCRIBE)
    private String gitDescribe;

    @Parameter(property = "makeUseOfJavaFile", defaultValue = "false")
    private boolean makeUseOfJavaFile;

    @Parameter(property = "classPathToJavaFile", defaultValue = "false")
    private String classPathToJavaFile;

    @Parameter(property = "constantToChange", defaultValue = "false")
    private String constantToChange;

    /**
     * @parameter default-value="${project}"
     */
    @Component
    private MavenProject mavenProject;

    /**
     * Maven Settings
     *
     * @since 1.4
     */
    @Parameter( defaultValue = "${settings}", readonly = true )
    protected Settings settings;

    @Parameter( defaultValue = "${project}", required = true, readonly = true )
    protected MavenProject project;

    /**
     * Contains the full list of projects in the reactor.
     *
     * @since 1.0-beta-3
     */
    @Parameter( defaultValue = "${reactorProjects}", readonly = true, required = true )
    private List<MavenProject> reactorProjects;

    private static final String SOURCE = "" + "package {{PACKAGE}};\n\n" + "" + "public class {{CLASS_NAME}} {\n\n" + ""
			+ "public static final String {{GIT_DESCRIBE_CONSTANT_NAME}} = \"\";\n\n" + "" + "}";

	/**
	 * Contains the Tokens used for the values to be replaced in the Java source
	 * files.
	 */
	private static final class Token {
		private static final String CLASS_NAME = "{{CLASS_NAME}}";
		private static final String PACKAGE = "{{PACKAGE}}";
		private static final String GIT_DESCRIBE_CONSTANT_NAME = "{{GIT_DESCRIBE_CONSTANT_NAME}}";
	}

	/**
	 * Default constructor.
	 */
	public GitDescribeMojo() {
		super();
	}

    /**
     * Performs the actual plugin execution.
     *
     * The typical command will be {@code git describe}.
     *
     * @throws MojoExecutionException An exception occurring during the
     *             execution of a plugin.
     * @throws MojoFailureException An exception occurring during the execution
     *             of a plugin (such as a compilation failure).
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Properties properties = this.mavenProject.getProperties();

        this.getLog().info("Executing '" + this.mavenProject.getName()
				+ "' Version: " + this.mavenProject.getVersion() +
				" with 'git describe' support.");

        File pomFileExecuted = this.mavenProject.getFile();
        File directoryOfPomFile = pomFileExecuted.getParentFile();

        // Make use of the Java File...
        if (this.makeUseOfJavaFile) {
            // pathOfTheJavaFile
            if (this.classPathToJavaFile == null || this.classPathToJavaFile.isEmpty()) {
                this.getLog().error("When 'makeUseOfJavaFile' the property 'classPathToJavaFile' also needs to be supplied.");
                return;
            }

            // constantToChange
            if (this.constantToChange == null || this.constantToChange.isEmpty()) {
                this.getLog().error("When 'makeUseOfJavaFile' the property 'constantToChange' needs to be supplied also.");
                return;
            }

            String actualPath = this.getActualPathToJavaFile();
            File actualPathFile = new File(actualPath);

            if (!actualPathFile.exists()) {
                this.getLog().info("No GitDescribe file at '" + new File(actualPath).getAbsolutePath() + "'. Creating one.");

                try {
                    this.createDefaultGitDescribe(new File(actualPath));
                } catch (IOException ioExcept) {
                    this.getLog().error("Unable to create new Git Describe java file from scratch: " + ioExcept.getMessage() + ".", ioExcept);
                }
            } else {
                this.getLog().info("File '" + actualPathFile.getAbsolutePath() + "' exists. No need to create.");
            }

            this.getLog().info("Making use of Java File at '" + actualPath + "' for constant '" + this.constantToChange + "'.");

            //
            if (actualPathFile.exists()) {
                try {
                    String propertyVal = this.getGitDescribeValue();
                    this.getLog().info("Starting to edit Java File...");
                    this.editJavaFile(actualPathFile, propertyVal);
                }
                //
                catch (IOException ioExcept) {
                    this.getLog().error("Unable to add Git Describe: " + ioExcept.getMessage() + ".", ioExcept);
                }
            }
            //
            else {
                this.getLog().info("No Java File at location '" + actualPathFile.getAbsolutePath() + "' exists to edit.");
            }
        }
		//Making use of setting Maven property.
        else {
            this.getLog().info("Making use of setting Maven property.");
            String revision = this.getGitDescribeValue();

            if (this.gitDescribe == null || this.gitDescribe.trim().isEmpty()) {
                this.gitDescribe = DEFAULT_GIT_DESCRIBE;
            }

            properties.setProperty(this.gitDescribe, revision);
            properties.put(this.gitDescribe, revision);
            this.getLog().info("[" + this.gitDescribe + "]: " + revision);

            if ( mavenProject != null ) {

                if ( mavenProject != null ) {
                    mavenProject.getProperties().put( gitDescribe, revision );
                }
                // Add the revision and timestamp properties to each project in the reactor
                if (reactorProjects != null )
                {
                    Iterator<MavenProject> projIter = reactorProjects.iterator();
                    while ( projIter.hasNext() )
                    {
                        MavenProject nextProj = (MavenProject) projIter.next();
                        getLog().debug( "Storing timestamp property in project " + project.getId() );
                        if ( revision != null )
                        {
                            nextProj.getProperties().put( this.gitDescribe, revision );
                        }
                    }
                }
            }
        }
    }

	/**
	 * Modifies the {@code toReadParam} file to change {@code constantToChange} to
	 * {@code gitDescribeValueParam}.
	 *
	 * @param toReadParam The file to read and change.
	 * @param gitDescribeValueParam The outcome of the {@code git describe} command.
	 * @throws IOException If any File IO Problems occur.
	 */
	private void editJavaFile(File toReadParam, String gitDescribeValueParam) throws IOException {
		String finalSource = null;
		String fullPath = null;
		try {
			this.getLog().info("Existing Value: \n\n" + gitDescribeValueParam);

			finalSource = FileUtils.readFileToString(toReadParam);

			fullPath = toReadParam.getAbsolutePath();

			// public static final String VERSION = "";
			int indexOfConstantVarDecl = finalSource.lastIndexOf(this.constantToChange);

			String prefix = finalSource.substring(0, indexOfConstantVarDecl);

			String poster = finalSource.substring(indexOfConstantVarDecl + this.constantToChange.length());
			int indexOfDblQuote = poster.indexOf('\"');
			String postfixPartOne = poster.substring(0, indexOfDblQuote + 1);

			int indexOfSecondDblQuote = poster.indexOf('\"', indexOfDblQuote + 1);

			String postfixPartTwo = poster.substring(indexOfSecondDblQuote);

			StringBuilder stringBuilder = new StringBuilder();

			stringBuilder.append(prefix);
			stringBuilder.append(this.constantToChange);
			stringBuilder.append(postfixPartOne);
			stringBuilder.append(gitDescribeValueParam);
			stringBuilder.append(postfixPartTwo);

			this.getLog().info("\n\n\n\n----------\n" + stringBuilder.toString() + "\n---------\n\n\n\n\n\n");

			toReadParam.delete();

			finalSource = stringBuilder.toString();
		} finally {
			if (!new File(fullPath).exists() && (finalSource != null && !finalSource.isEmpty())) {
				FileUtils.writeStringToFile(new File(fullPath), finalSource);
			}
		}
	}

	/**
	 * Constructs the path to the Java source file to change based
	 * on the Maven project location.
	 *
	 * @return Path to the Java file where the variable will be replaced.
     */
    private String getActualPathToJavaFile() {
        if (this.classPathToJavaFile == null) {
            return null;
        }

        String replaceDotWithForward = this.classPathToJavaFile.replace('.', '/');

        File pomFileExecuted = this.mavenProject.getFile();
        File directoryOfPomFile = pomFileExecuted.getParentFile();

        StringBuilder returnVal = new StringBuilder();
        returnVal.append(directoryOfPomFile.getAbsolutePath());
        returnVal.append("/");
        returnVal.append(PREFIX);
        returnVal.append(replaceDotWithForward);
        returnVal.append(".java");

        return returnVal.toString();

    }

    /**
	 * Creates a default GitDescribe Java source file.
     *
     */
    private void createDefaultGitDescribe(File toCreateParam) throws IOException {

        int lastIndexOfDot = this.classPathToJavaFile.lastIndexOf('.');

        String className = this.classPathToJavaFile.substring(lastIndexOfDot + 1);
        String packageOnly = this.classPathToJavaFile.substring(0, lastIndexOfDot);

        String modifiedSource = SOURCE.replace(Token.CLASS_NAME, className);
        modifiedSource = modifiedSource.replace(Token.PACKAGE, packageOnly);
        modifiedSource = modifiedSource.replace(Token.GIT_DESCRIBE_CONSTANT_NAME, this.constantToChange);

        this.getLog().info(modifiedSource);

        FileUtils.writeStringToFile(toCreateParam, modifiedSource);
    }

    /**
	 * Executes the {@code git describe} command and returns the result.
     * 
     * @return Outcome of {@code git describe}.
     */
    private String getGitDescribeValue() throws MojoExecutionException {
        CommandUtil.CommandResult commandResult = CommandUtil.executeCommand(this.getLog(), "git", "describe");

        String[] resultLines = commandResult.getResultLines();
        if (resultLines == null || resultLines.length == 0) {
            return "Unable to get tag version.";
        }

        if (commandResult.getExitCode() != 0) {
            String specificError = "";

            for (String line : resultLines) {
                specificError += line;
            }

            return ("Not expected response code [" + commandResult.getExitCode() + "]. 'git describe' failed. Tag your repository!!! " + specificError);
        }

        this.getLog().info("Execute Result: " + commandResult.getExitCode() + commandResult.getResultLines()[0]);

        String returnVal = "";
        for (String line : resultLines) {
            returnVal += line;
        }

        return returnVal;
    }
}
