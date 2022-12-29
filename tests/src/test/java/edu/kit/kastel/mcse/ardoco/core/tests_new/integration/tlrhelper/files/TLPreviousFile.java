/* Licensed under MIT 2022. */
package edu.kit.kastel.mcse.ardoco.core.tests_new.integration.tlrhelper.files;

import edu.kit.kastel.mcse.ardoco.core.api.output.ArDoCoResult;
import edu.kit.kastel.mcse.ardoco.core.tests_new.TestUtil;
import edu.kit.kastel.mcse.ardoco.core.tests_new.eval.results.EvaluationResults;
import edu.kit.kastel.mcse.ardoco.core.tests_new.eval.Project;
import edu.kit.kastel.mcse.ardoco.core.tests_new.integration.tlrhelper.TestLink;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * This is a helper class to load and write out the results of the previous evaluation run for TLR results.
 */
public class TLPreviousFile {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private TLPreviousFile() {
        throw new IllegalAccessError("This constructor should not be called!");
    }

    /**
     * Loads the previous results
     * 
     * @param sourceFile file to load from
     * @return the previous results
     * @throws IOException if file access fails
     */
    public static Collection<Pair<Project, EvaluationResults<TestLink>>> load(Path sourceFile,  final Map<Project, ArDoCoResult> DATA_MAP) throws IOException {
        List<String> lines = Files.readAllLines(sourceFile);
        Map<Project, List<TestLink>> foundLinkMap = new HashMap<>();
        List<Pair<Project, EvaluationResults<TestLink>>> results = new ArrayList<>();

        for (String line : lines) {
            var parts = line.split(",", -1);
            Project project = Project.valueOf(parts[0]);
            String modelId = parts[1];
            int sentenceNr = Integer.parseInt(parts[2]);

            var testLink = new TestLink(modelId, sentenceNr);

            if (!foundLinkMap.containsKey(project)) {
                foundLinkMap.put(project, new ArrayList<>());
            }

            foundLinkMap.get(project).add(testLink);
        }

        for (Project project : foundLinkMap.keySet()) {
            var correctLinks = TLGoldStandardFile.loadLinks(project);
            var foundLinks = foundLinkMap.get(project);

            results.add(Tuples.pair(project, TestUtil.compare(DATA_MAP.get(project), foundLinks, correctLinks, true)));
        }

        return results;
    }

    /**
     * Saves the given results to the given file.
     * 
     * @param targetFile file to save to
     * @param projectResults    results to save
     * @throws IOException if writing to file system fails
     */
    public static void save(Path targetFile, Collection<Pair<Project, EvaluationResults<TestLink>>> projectResults) throws IOException {
        if (Files.exists(targetFile)) {
            return; // do not overwrite
        }

        var sortedResults = new ArrayList<>(projectResults);
        sortedResults.sort(Comparator.comparing(x -> x.getOne().name()));

        var builder = new StringBuilder();

        for (Pair<Project, EvaluationResults<TestLink>> projectResult : sortedResults) {
            EvaluationResults<TestLink> result = projectResult.getTwo();
            for (TestLink foundLink : result.getFound()) {
                builder.append(projectResult.getOne().name());
                builder.append(',');
                builder.append(foundLink.modelId());
                builder.append(',');
                builder.append(foundLink.sentenceNr());
                builder.append(LINE_SEPARATOR);
            }
        }

        Files.writeString(targetFile, builder.toString(), StandardOpenOption.CREATE);
    }

}
